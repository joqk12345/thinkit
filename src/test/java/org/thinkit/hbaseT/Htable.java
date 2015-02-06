package org.thinkit.hbaseT;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

public class Htable {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
//        Configuration hbaseConf = new Configuration();
//        hbaseConf.set("hbase.zookeeper.quorum", "192.168.14.20");
//        
//        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
//        hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");
//        hbaseConf =HBaseConfiguration.create(hbaseConf);
//        
    	
    	  Configuration hbaseConf = HBaseConfiguration.create();
//          hbaseConf.set("hbase.zookeeper.quorum", "master");
    	  hbaseConf.set("hbase.zookeeper.quorum", "compute1");
          //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
          hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");
          
        
        HBaseAdmin admin = new HBaseAdmin(hbaseConf);
        HTableDescriptor htableDescriptor = new HTableDescriptor("table".getBytes());  //set the name of table
        htableDescriptor.addFamily(new HColumnDescriptor("fam1")); //set the name of column clusters
        admin.createTable(htableDescriptor); //create a table 
        HTable table = new HTable(hbaseConf, "table"); //get instance of table.
        for (int i = 0; i < 3; i++) {   //for is number of rows
            Put putRow = new Put(("row" + i).getBytes()); //the ith row
            putRow.add("fam1".getBytes(), "col1".getBytes(), "vaule1"
                    .getBytes());  //set the name of column and value.
            putRow.add("fam1".getBytes(), "col2".getBytes(), "vaule2"
                    .getBytes());
            putRow.add("fam1".getBytes(), "col3".getBytes(), "vaule3"
                    .getBytes());
            table.put(putRow);
        }
        for(Result result: table.getScanner("fam1".getBytes())){//get data of column clusters 
            for(Map.Entry<byte[], byte[]> entry : result.getFamilyMap("fam1".getBytes()).entrySet()){//get collection of result
                String column = new String(entry.getKey());
                String value = new String(entry.getValue());
                System.out.println(column+","+value);
            }
        }
        admin.disableTable("table".getBytes()); //disable the table
        admin.deleteTable("table".getBytes());  //drop the tbale
    }
}