package org.thinkit.mr.sort;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Sort {

	public static class Map extends Mapper<LongWritable, Text, IntWritable, Text> {
		// 输出key 词频
		IntWritable outKey = new IntWritable();
		Text outValue = new Text();
		String arr[] =new String[2];
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			
			String s =value.toString();
			//得到用户编号
//			s = s.substring(0,s.indexOf("	"));
			arr =s.split("	");
			if(arr.length==2){
				//生成的新的key
				outKey = new IntWritable(Integer.parseInt(arr[1].toString()));
				//得到的新的输出value
				outValue =new Text(arr[0]);
				System.out.println("outkey:"+outKey +"outValue:"+outValue);
			}
			
			//写到reduce 程序中
			context.write(outKey, outValue);
		}

	}

	public static class Reduce extends Reducer<IntWritable, Text, Text, IntWritable> {

		private static MultipleOutputs<Text, IntWritable> mos = null;

		// 要获得前K个频率最高的词
		private static final int k = 10;

		// 用TreeMap存储可以利用它的排序功能
		// 这里用 MyInt 因为TreeMap是对key排序，且不能唯一，而词频可能相同，要以词频为Key就必需对它封装
		private static TreeMap<MyInt, String> tm = new TreeMap<MyInt, String>(new Comparator<MyInt>() {
					/**
					 * 默认是从小到大的顺序排的，现在修改为从大到小
					 * @param o1
					 * @param o2
					 * @return
					 */
					public int compare(MyInt o1, MyInt o2) {
						return o2.compareTo(o1);
					}

				});

		/*
		 * 以词频为Key是要用到reduce的排序功能
		 */
		@Override
		protected void reduce(IntWritable key, Iterable<Text> values,Context context) throws IOException, InterruptedException {
			for (Text text : values) {
				context.write(text, key);
				tm.put(new MyInt(key.get()), text.toString());
				// TreeMap以对内部数据进行了排序，最后一个必定是最小的
				if (tm.size() > k) {
					tm.remove(tm.lastKey());
				}
			}

		}

		/**
		 * 在结束task之后执行该方法
		 */
		@Override
		protected void cleanup(Context context) throws IOException,	InterruptedException {
			
			String path = context.getConfiguration().get("topKout");
			mos = new MultipleOutputs<Text, IntWritable>(context);
			Set<Entry<MyInt, String>> set = tm.entrySet();
			for (Entry<MyInt, String> entry : set) {
				mos.write("topKMOS", new Text(entry.getValue()),new IntWritable(entry.getKey().getValue()), path);
			}
			mos.close();
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static void run(String in, String out, String topKout) throws IOException, ClassNotFoundException,InterruptedException {

		Path inPath = new Path(in);
		Path outPath = new Path(out);
		Configuration conf = new Configuration();

		// 前K个词要输出到哪个目录
		conf.set("topKout", topKout);

		Job job = new Job(conf, "频次排序2");
		job.setJarByClass(Sort.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		// 设置Map输出类型
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);

		// 设置Reduce输出类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// 设置MultipleOutputs的输出格式
		// 这里利用MultipleOutputs进行对文件输出
		MultipleOutputs.addNamedOutput(job, "topKMOS",TextOutputFormat.class, Text.class, Text.class);

		// 设置输入和输出目录
		FileInputFormat.addInputPath(job, inPath);
		FileOutputFormat.setOutputPath(job, outPath);
		job.waitForCompletion(true);

	}

}
