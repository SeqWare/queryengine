package demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;

public class VariantFreq {

  private static final String FEATURE_TABLE = "feature-table";
  private static final String FEATURE_FAMILY = "feature-family";
  private static final String FREQ_TABLE = "freq-table";
  private static final String FREQ_FAMILY = "freq-family";
  private static final String FREQ_COLUMN = "freq-column";

  private static final byte DELIM = 0;
  private static final String _ = new String(new byte[] { DELIM });

  public static void ensureDelims(byte[] ba, int expected) {
    int cnt = 0;
    for (byte b : ba)
      if (b == DELIM)
        cnt++;

    if (cnt != expected)
      throw new RuntimeException("Expected " + expected + " delimiter bytes, found " + cnt);
  }

  public static byte[] ascii(String s) {
    try {
      return s.getBytes("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String ascii(byte[] ba) {
    try {
      return new String(ba, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] serialzeVariantKey(Feature f) {
    String chromosome = f.getSeqid();
    String position = "" + f.getStart();
    /*
     * The following was inferred from VCFVariantImporter lines 246,250,441, and
     * AtomImpl line 272.
     */
    String referenceBase = (String) f.getTagByKey((String) null, ImportConstants.VCF_REFERENCE_BASE).getValue();
    String calledBase = (String) f.getTagByKey((String) null, ImportConstants.VCF_CALLED_BASE).getValue();

    /*
     * Due to variable length components, use ascii with a delimiter the
     * components are not expected to contain.
     */
    byte[] key = ascii(chromosome + _ + position + _ + referenceBase + _ + calledBase);

    ensureDelims(key, 3);

    return key;
  }

  public static String[] deserializeVariantKey(byte[] key) {
    return ascii(key).split(_);
  }

  public static class VariantFreqMapper extends TableMapper<ImmutableBytesWritable, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    @Override
    protected void map(ImmutableBytesWritable key, Result row, Context context) throws IOException, InterruptedException {
      String family = context.getConfiguration().get(FEATURE_FAMILY);
      Map<byte[], byte[]> cols = row.getFamilyMap(Bytes.toBytes(family));
      for (byte[] data : cols.values()) {
        FeatureList fl = SWQEFactory.getSerialization().deserialize(data, FeatureList.class);
        for (Feature f : fl.getFeatures()){
          byte[] var = serialzeVariantKey(f);
          context.write(new ImmutableBytesWritable(var), one);
        }
      }
    }
  }

  public static class VariantFreqReducer extends
      TableReducer<ImmutableBytesWritable, IntWritable, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }

      Configuration conf = context.getConfiguration();
      String family = conf.get(FREQ_FAMILY);
      String column = conf.get(FREQ_COLUMN);

      Put put = new Put(key.get());
      put.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(sum));
      context.write(key, put);
    }

  }

  public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
    if (args.length != 5) {
      System.out.println("Usage: VariantFreq <feature-table> <feature-family> <freq-table> <freq-family> <freq-column>");
      System.exit(1);
      // create 'variant_aggregates', {NAME=>'all_features', VERSIONS=>1}
    }

    Configuration conf = HBaseConfiguration.create();
    HBaseStorage.configureHBaseConfig(conf);

    conf.set(FEATURE_TABLE, args[0]);
    conf.set(FEATURE_FAMILY, args[1]);
    conf.set(FREQ_TABLE, args[2]);
    conf.set(FREQ_FAMILY, args[3]);
    conf.set(FREQ_COLUMN, args[4]);

    String featureTable = args[0];
    String featureFamily = args[1];
    String freqTable = args[2];

    Job job = new Job(conf, "VariantFreq");
    job.setJarByClass(VariantFreq.class);
    Scan scan = new Scan();
    scan.addFamily(Bytes.toBytes(featureFamily));

    TableMapReduceUtil.initTableMapperJob(featureTable, scan, VariantFreqMapper.class, ImmutableBytesWritable.class,
                                          IntWritable.class, job);

    TableMapReduceUtil.initTableReducerJob(freqTable, VariantFreqReducer.class, job);

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}
