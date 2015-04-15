package org.thinkit.mr.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;


/**
 * 统计500w数据的频次（比如说主被叫号码）
 * @author lenovo
 *
 */
public class CDRStatisticsMRforCSV {
	//拿到数据
	public static class  CDRStatisticsMapper extends Mapper<LongWritable, Text,Text, IntWritable>{
		
		private static Text key1;
		private static IntWritable value1 =new IntWritable(1);
		
		public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException {
			String s = value.toString();

            //添加主叫号的字段{第五个字段}

			s = s.substring(0,s.indexOf(","));
            String [] csvArray=s.split(",");
            //主叫号码是第五个 csvArray[4]

			//生成的新的key
			key1=new Text(csvArray[4]);
			//写到reduce 程序中
			context.write(key1, value1);
		}
	}
	
	
	//归类合并
	public static class CDRStatisticsReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private static IntWritable value2;

		public void reduce(Text key, Iterable<IntWritable> value,Context context)throws IOException, InterruptedException {
			Iterator<IntWritable> iter =value.iterator();
			int v=0;
			while(iter.hasNext()){
				v+=iter.next().get();
			}
			value2 =new IntWritable(v);
			context.write(key, value2);
		}
	}
	
	
	public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {

		  
		  Configuration conf = new Configuration();
		  Job job = new Job(conf, "频次统计0");
		  job.setJarByClass(CDRStatisticsMRforCSV.class);

		  final String INPUT_PATH = "hdfs://master:9000/testData/";
	      final String OUTPUT_PATH = INPUT_PATH.substring(0, INPUT_PATH.lastIndexOf("/")) + "/_output1";

	      FileInputFormat.addInputPath(job,new Path(INPUT_PATH));
	      FileOutputFormat.setOutputPath(job,new Path(OUTPUT_PATH));
	      
	      job.setMapperClass(CDRStatisticsMapper.class);
	      job.setReducerClass(CDRStatisticsReducer.class);

	      //TextInputFormat输入是默认的InputFormat
//	    job.setInputFormatClass(TextInputFormat.class);
	/*    job.setMapOutputKeyClass(Text.class);
	      job.setMapOutputValueClass(IntWritable.class);*/

	      job.setOutputKeyClass(Text.class);
	      job.setOutputValueClass(IntWritable.class);
	      job.waitForCompletion(true); 
	}

}
