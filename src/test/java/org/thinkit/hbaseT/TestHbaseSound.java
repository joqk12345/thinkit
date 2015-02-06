package org.thinkit.hbaseT;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.junit.Before;
import org.junit.Test;
import org.thinkit.data.hbase.HBaseSound;

public class TestHbaseSound {

	private HBaseSound hbaseSound;
	
	private String tablename = "AudioBase";
	
	 private static Configuration conf = null;

	 static {
		 		Configuration HBASE_CONFIG = new Configuration();
		        //与hbase/conf/hbase-site.xml中hbase.zookeeper.quorum配置的值相同 
		        HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.11.39");
		        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
		        HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		        conf = HBaseConfiguration.create(HBASE_CONFIG);
	}
	
	@Test
	public void testCreatTable() {
//        String[] familys = {"grade", "course"};
        String[] familys = {"AudioOriginalByteFile","Audiofeature","AudioAttr","Ecf1"};
        try {
			hbaseSound.creatTable(tablename, familys);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteTable() {
		
			try {
				hbaseSound.deleteTable(tablename);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Test
	public void testAddRecord() throws Exception {
		System.out.println("===========插入一个语音数据及其他记录========");
		hbaseSound.addHbaseChars(tablename, "row1", "AudioOriginalByteFile", "AudioByte", "语音二进制数据");
		hbaseSound.addHbaseChars(tablename, "row1", "Audiofeature", "Keywordfeature", "语音关键词二进制特征数据");
		hbaseSound.addHbaseChars(tablename, "row1", "Audiofeature", "Speakerfeature", "说话人二进制特征数据");
		hbaseSound.addHbaseChars(tablename, "row1", "AudioAttr", "Format", "语音格式数据 8k 8bit alaw");
		System.out.println("===========结束插入一个语音数据========");
	}
	

	@Test
	public void testAddAudio() {
	}

	@Test
	public void testDelRecord() throws IOException {
		System.out.println("===========删除一条语音记录========");
		hbaseSound.delRecord(tablename, "row1");
	}

	@Test
	public void testGetOneRecord() {
		  System.out.println("===========拿到一条语音记录========");
		  try {
			hbaseSound.getOneRecord(tablename, "000100.05");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetAllRecord() {
	}
	
}
