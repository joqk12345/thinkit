package org.thinkit.data.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseSound {
	  private static Configuration conf = null;
	     
	    /**
	     * 初始化配置
	     */
	    static {
	        Configuration HBASE_CONFIG = new Configuration();
	        //与hbase/conf/hbase-site.xml中hbase.zookeeper.quorum配置的值相同 
	        HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.11.39");
	        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
	        HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
	        conf = HBaseConfiguration.create(HBASE_CONFIG);
	    }
	    
	    /**
	     * 创建一张表
	     */
	    public static void creatTable(String tableName, String[] familys) throws Exception {
	        HBaseAdmin admin = new HBaseAdmin(conf);
	        if (admin.tableExists(tableName)) {
	            System.out.println("table already exists!");
	        } else {
	            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
	            for(int i=0; i<familys.length; i++){
	                tableDesc.addFamily(new HColumnDescriptor(familys[i]));
	            }
	            admin.createTable(tableDesc);
	            System.out.println("create table " + tableName + " ok.");
	        } 
	        admin.close();
	    }
	    
	    /**
	     * 删除表
	     */
	    public static void deleteTable(String tableName) throws Exception {
	    	HBaseAdmin admin=null;
	       try {
	            admin = new HBaseAdmin(conf);
	           admin.disableTable(tableName);
	           admin.deleteTable(tableName);
	           System.out.println("delete table " + tableName + " ok.");
	       } catch (MasterNotRunningException e) {
	           e.printStackTrace();
	       } catch (ZooKeeperConnectionException e) {
	           e.printStackTrace();
	       }finally{
	    	   if(admin!=null){
	    		   admin.close();
	    	   }
	       }
	    }
	     
	    /**
	     * 插入一行记录
	     */
	    public static void addRecord (String tableName, String rowKey, String family, String qualifier, String value)throws Exception{
	        try {
	            HTable table = new HTable(conf, tableName);
	            Put put = new Put(Bytes.toBytes(rowKey));
	            put.add(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(value));
	            table.put(put);
	            System.out.println("insert recored " + rowKey + " to table " + tableName +" ok.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * 插入二进制流的工具函数
	     */
	    public static void addHbaseBytes (String tableName, String rowKey, String family, String qualifier, byte[] bytes)throws Exception{
	        try {
	            HTable table = new HTable(conf, tableName);
	            Put put = new Put(Bytes.toBytes(rowKey));
	            put.add(Bytes.toBytes(family),Bytes.toBytes(qualifier),bytes);
	            table.put(put);
	            System.out.println("insert recored " + rowKey + " to table " + tableName +" ok.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * 向hbase中插入chars  字符串 的记录
	     */
	    public static void addHbaseChars (String tableName, String rowKey, String family, String qualifier, String value)
	            throws Exception{
	        try {
	            HTable table = new HTable(conf, tableName);
	            Put put = new Put(Bytes.toBytes(rowKey));
	            put.add(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(value));
	            table.put(put);
	            System.out.println("insert recored " + rowKey + " to table " + tableName +" ok.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    
	    
	 
	    /**
	     * 删除一行记录
	     */
	    public static void delRecord (String tableName, String rowKey) throws IOException{
	        HTable table = new HTable(conf, tableName);
	        List list = new ArrayList();
	        Delete del = new Delete(rowKey.getBytes());
	        list.add(del);
	        table.delete(list);
	        System.out.println("del recored " + rowKey + " ok.");
	    }
	     
	    /**
	     * 查找一行记录
	     */
	    public static void getOneRecord (String tableName, String rowKey) throws IOException{
	        HTable table = new HTable(conf, tableName);
	        Get get = new Get(rowKey.getBytes());
//	        get.addFamily("i".getBytes());
	        Result rs = table.get(get);
	        int i=0;
	        for(Cell cell : rs.listCells()){
	            i++;
	        	System.out.print("raw:"+ new String (CellUtil.cloneRow(cell))+ " ");
	            System.out.print("family:"+new String(CellUtil.cloneFamily(cell)) + ":" );
	            System.out.print("qualifier:"+new String(CellUtil.cloneQualifier(cell)) + " " );
	            System.out.print("ts:"+cell.getTimestamp() + " " );
	            System.out.println("value:"+new String(CellUtil.cloneValue(cell)));
	            System.out.println("标号"+i);
	        }
	    }
	     
	    /**
	     * 显示所有数据
	     */
	    public static void getAllRecord (String tableName) {
	        try{
	             HTable table = new HTable(conf, tableName);
	             Scan s = new Scan();
	             ResultScanner ss = table.getScanner(s);
	             for(Result r:ss){
	                 for(Cell cell : r.listCells()){
	                    System.out.print(new String(CellUtil.cloneRow(cell)) + " ");
	                    System.out.print(new String(CellUtil.cloneFamily(cell)) + ":");
	                    System.out.print(new String(CellUtil.cloneQualifier(cell)) + " ");
	                    System.out.print(cell.getTimestamp() + " ");
	                    System.out.println(new String(CellUtil.cloneValue(cell)));
	                 }
	             }
	        } catch (IOException e){
	            e.printStackTrace();
	        }
	    }
	    
	    public static void  main (String [] agrs) {
	        try {
	            String tablename = "minifiletable3";
//	            String[] familys = {"grade", "course"};
	            String[] familys = {"i", "m","q"};
//	            HBaseSound.creatTable(tablename, familys);
	             
	            //add record zkb
//	            HBaseSound.addRecord(tablename,"zkb","grade","","5");
//	            HBaseSound.addRecord(tablename,"zkb","course","","90");
//	            HBaseSound.addRecord(tablename,"zkb","course","math","97");
//	            HBaseSound.addRecord(tablename,"zkb","course","art","87");
	            //add record  baoniu
//	            HBaseSound.addRecord(tablename,"baoniu","i","keydezhi","keydevalue");
//	            HBaseSound.addRecord(tablename,"baoniu","m","","true");
//	            HBaseSound.addRecord(tablename,"baoniu","q","","false");
	             
	            System.out.println("===========get one record========");
	            HBaseSound.getOneRecord(tablename, "baoniu");
	             
//	            System.out.println("===========show all record========");
//	            HBaseSound.getAllRecord(tablename);
//	            System.out.println("===========del one record========");
//	            HBaseSound.delRecord(tablename, "baoniu");
//	            HBaseSound.getAllRecord(tablename);
//	            System.out.println("===========show all record========");
	            HBaseSound.getAllRecord(tablename);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
