package org.thinkit;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;

public class MyReadjob2 {
	public static class MyMapper extends TableMapper<ImmutableBytesWritable, Put>{
//		ByteDecoderJulyNinth byteDecoderJulyNinth=ByteDecoderJulyNinth.getInstance();
		
		public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
			// this example is just copying the data from the source table...
			
			String ip = InetAddress.getLocalHost().getHostAddress();
 			System.out.println(" 打印本机ip地址："+ip);
			context.write(row, resultToPut(row,value));
		}
		
		private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
	  		Put put = new Put(key.get());
	 		for (KeyValue kv : result.raw()) {
	 			//执行识别操作
				put.add(kv);
			}
			return put;
	   	}
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		
		Configuration config = HBaseConfiguration.create();
		Job job = new Job(config, "ExampleReadWrite");
		job.setJarByClass(MyReadjob2.class);     // class that contains mapper
		
		
		config.set("hbase.zookeeper.quorum", "compute1");
        
        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
		config.set("hbase.zookeeper.property.clientPort", "2181");
		
		
		
		Scan scan = new Scan();
//		scan.addFamily(Bytes.toBytes(new String ("m")));
		scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
		scan.setCacheBlocks(false);  // don't set to true for MR jobs
		// set other scan attrs
		//...

		TableMapReduceUtil.initTableMapperJob(
		  "t",        // input HBase table name
		  scan,             // Scan instance to control CF and attribute selection
		  MyMapper.class,   // mapper
		  null,             // mapper output key
		  null,             // mapper output value
		  job,
		  false);
		
		TableMapReduceUtil.initTableReducerJob(
				"t2",      // output table
				null,             // reducer class
				job,null, null, null, null, false);
//		job.setOutputFormatClass(NullOutputFormat.class);   // because we aren't emitting anything from mapper
		job.setNumReduceTasks(0);
		boolean b = job.waitForCompletion(true);
		if (!b) {
		  throw new IOException("error with job!"); 
		}
	}
}
