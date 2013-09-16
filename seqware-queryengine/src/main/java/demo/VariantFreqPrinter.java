package demo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.github.seqware.queryengine.impl.HBaseStorage;

public class VariantFreqPrinter {

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      System.out.println("Usage: VariantFreqPrinter <freq-table> <freq-family> <freq-column>");
      System.exit(1);
    }
    
    byte[] table = Bytes.toBytes(args[0]);
    byte[] family = Bytes.toBytes(args[1]);
    byte[] column = Bytes.toBytes(args[2]);

    Configuration conf = HBaseConfiguration.create();
    HBaseStorage.configureHBaseConfig(conf);
    HTable htable = null;
    ResultScanner scanner = null;
    try {
      htable = new HTable(conf, table);
      Scan scan = new Scan();
      scan.addFamily(family);
      scan.setMaxVersions(1);

      scanner = htable.getScanner(scan);
      System.out.println("CHROM\tPOS\tREF\tALT\tCOUNT");
      for (Result r : scanner){
        KeyValue kv = r.getColumnLatest(family, column);
        if (kv != null){
          String[] parts = VariantFreq.deserializeVariantKey(r.getRow());
          for (String s : parts){
            System.out.print(s);
            System.out.print('\t');
          }
          int count = Bytes.toInt(kv.getValue());
          System.out.println(count);
        }
      }
    } finally {
      if (scanner != null)
        scanner.close();
      if (htable != null)
        htable.close();
    }
  }
}
