package org.thinkit.distributedcompute;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.thinkit.decoder.SpeechAnalysisTool;

import java.io.IOException;
import java.net.InetAddress;

public class DNAAudioSearchMapReduceJob2 {
	
	public static final String TEMP_INDEX_PATH="/tmp/DNAPATH";
	public static final String inputTable="minifiletable";
	
	public static class MyMapper extends TableMapper<Writable, Writable>{
//		ByteDecoderJulyNinth byteDecoderJulyNinth=ByteDecoderJulyNinth.getInstance();
		private Text k=new Text();
		private Text v=new Text();
		
		public void map(ImmutableBytesWritable row, Result columns, Context context) throws IOException, InterruptedException {
			// this example is just copying the data from the source table...
			String ip = InetAddress.getLocalHost().getHostAddress();
 			System.out.println(" 打印本机ip地址："+ip);

 			String rowkey = new String(row.get());
 			
 			String columnFamily =null;
 			String columnQualifier =null;
 			Long ts =0L;
 			
 			for (Cell cell : columns.listCells()) {
// 				Sound recognition = new Sound ();
//	 			String ip = InetAddress.getLocalHost().getHostAddress();
//	 			System.out.println(" 打印本机ip地址："+ip);
//	 			a.setServer("192.168.11.39", 8888);
// 				recognition.setServer("127.0.0.1", 40010);
                SpeechAnalysisTool recognition=new SpeechAnalysisTool();
 				
 				columnFamily=new String(CellUtil.cloneFamily(cell));
 				columnQualifier=new String(CellUtil.cloneQualifier(cell));
 				ts = cell.getTimestamp();
 				//如果是原始语音的  且 qulifer 等于  AUdioByte的，我进行固定音频检测
 				if(columnFamily.equals("AudioOriginalByteFile")&&columnQualifier.equals("AudioByte")){
 					//送数据
                    recognition.setSounddata(CellUtil.cloneValue(cell));
 		 			//执行固定音频检索	
 	 				recognition.doFixAudioSearch();
 	 				//设置输出值
 	 				k.set(rowkey);
 	 				v.set(recognition.getRecognitionresult());
 	 	 			context.write(k, v);	
 				}
 			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		
		Configuration config = HBaseConfiguration.create();
		
		/**
		 * job信息配置
		 */
		Job job = new Job(config, "批量DNA音频解码、数据的输出源为HDFS");
		job.setJarByClass(DNAAudioSearchMapReduceJob2.class);     // class that contains mapper
		
		/**
		 * 设置hmast的配置信息
		 */
		config.set("hbase.zookeeper.quorum", "compute1,master,thinkit-4");
        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置的值相同
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 1200000); 
		/**
		 * 设置Hadoop的预测机制为false
		 */
		config.setBoolean("mapreduce.map.speculative", false);
		config.setBoolean("mapreduce.reduce.speculative", false);
		
		/**
		 * 路径信息配置
		 */
		Path tempIndexPath = new Path(TEMP_INDEX_PATH);
		FileSystem fs =FileSystem.get(config);
		if(fs.exists(tempIndexPath)){
			fs.delete(tempIndexPath, true);
		}
		
		
		/**
		 * 批处理加缓存消息
		 */
		Scan scan = new Scan();
//		scan.addFamily(Bytes.toBytes(new String ("m")));
		scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
		scan.setCacheBlocks(false);  // don't set to true for MR jobs
		// set other scan attrs
		//...


		TableMapReduceUtil.initTableMapperJob(
		  inputTable,        // input HBase table name
		  scan,             // Scan instance to control CF and attribute selection
		  MyMapper.class,   // mapper
		  Text.class,             // mapper output key
		  Text.class,             // mapper output value
		  job,
		  false);
		
		job.setNumReduceTasks(0);
		/*TableMapReduceUtil.initTableReducerJob(
				"minifiletable2",      // output table
				null,             // reducer class
				job,null, null, null, null, false);*/
		job.setOutputFormatClass(TextOutputFormat.class);   // because we aren't emitting anything from mapper
		FileOutputFormat.setOutputPath(job, tempIndexPath);
		
		
		boolean b = job.waitForCompletion(true);
		if (!b) {
		  throw new IOException("error with job!"); 
		}
	}
}
