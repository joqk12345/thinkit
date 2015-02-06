package org.thinkit.mr.sort;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.thinkit.mr.sort.Dedup.Map;
import org.thinkit.mr.sort.Dedup.Reduce;

public class CopyTest {
	
	//map将输入中的value复制到输出数据的key上，并直接输出
    public static class Map extends Mapper<LongWritable, Text,Text, IntWritable>{
    	
    	private static Text key1;
		private static IntWritable value1 =new IntWritable(1);
       
        //实现map函数
        public void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException{
        	
        	String s = value.toString();
			s = s.substring(0,s.indexOf("	"));

			//生成的新的key
			key1=new Text(s);
			//写到reduce 程序中
			context.write(key1, value1);
        }
       
    }
    //reduce将输入中的key复制到输出数据的key上，并直接输出
    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>{
    	private static IntWritable value2;
    	//实现reduce函数
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
	
    public static boolean run(String in,String out) throws IOException, ClassNotFoundException, InterruptedException{          
        
        Configuration conf = new Configuration();                                                                             
                                                                                                                              
        Job job = new Job(conf,"词频统计1");                                                                                  
        job.setJarByClass(CopyTest.class);                                                                                   
        job.setMapperClass(Map.class);                   
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);                                                                                    
                                                                                                                              
        // 设置Map输出类型                                                                                                    
        job.setMapOutputKeyClass(Text.class);                                                                                 
        job.setMapOutputValueClass(IntWritable.class);                                                                        
                                                                                                                              
        // 设置Reduce输出类型                                                                                                 
        job.setOutputKeyClass(Text.class);                                                                                    
        job.setOutputValueClass(IntWritable.class);                                                                           
                                                                                                                              
        // 设置输入和输出目录                                                                                                 
        FileInputFormat.addInputPath(job, new Path(in));                                                                      
        FileOutputFormat.setOutputPath(job, new Path(out));                                                                   
                                                                                                                              
        return job.waitForCompletion(true);                                                                                   
    }                                                                                                                         

	  public static void main(String[] args) throws Exception{
		  
	     Configuration conf = new Configuration();
	     Job job = new Job(conf, "频次统计");
	     job.setJarByClass(CopyTest.class);

	     //词频统计的输入输出目录
	     final String INPUT_PATH = "hdfs://master:9000/mapreduce-example-data/";
	     final String OUTPUT_PATH = INPUT_PATH.substring(0, INPUT_PATH.lastIndexOf("/")) + "/output";
	     
	     
	     //设置Map、Combine和Reduce处理类
	     job.setMapperClass(Map.class);
	     job.setCombinerClass(Reduce.class);
	     job.setReducerClass(Reduce.class);
	     
	     //设置输出类型
	     job.setOutputKeyClass(Text.class);
	     job.setOutputValueClass(IntWritable.class);
	     
	     //设置输入和输出目录
	     FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
	     FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
	     System.exit(job.waitForCompletion(true) ? 0 : 1);
	     }
}
