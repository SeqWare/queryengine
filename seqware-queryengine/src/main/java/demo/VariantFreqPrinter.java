package demo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class VariantFreqPrinter {

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      System.out.println("Usage: VariantFreqPrinter <freq-table> <freq-family> <freq-column>");
    }
    
    String table = args[0];
    String family = args[1];
    String column = args[2];

    Configuration conf = HBaseConfiguration.create();
    HTable htable = null;
    ResultScanner scanner = null;
    try {
      htable = new HTable(conf, table);
      Scan scan = new Scan();
      scanner = htable.getScanner(scan);
      System.out.println("CHROM\tPOS\tREF\tALT\tCOUNT");
      for (Result r : scanner){
        String[] parts = VariantFreq.deserializeVariantKey(r.getRow());
        int count = Bytes.toInt(r.getValue(Bytes.toBytes(family), Bytes.toBytes(column)));
        for (String s : parts){
          System.out.print(s);
          System.out.println('\t');
        }
        System.out.println(count);
      }
    } finally {
      if (scanner != null)
        scanner.close();
      if (htable != null)
        htable.close();
    }
  }
}
