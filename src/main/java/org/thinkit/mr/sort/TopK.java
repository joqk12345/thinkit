package org.thinkit.mr.sort;

import java.io.IOException;

public class TopK {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		
		 //要统计字数，排序的cnr记录                                 
		String in = "hdfs://master:9000/mapreduce-example-data";    
		
		//统计频次后的结果                                        
//		String wordCout = "hdfs://master:9000/mapreduce-example-data/_output2";   
		String wordCout = "hdfs://master:9000/mapreduce-example-data/output";

		//对统计完后的结果再排序后的内容                          
		String sort = "hdfs://master:9000/mapreduce-example-data/sort";
		
		//前K条                                                   
		String topK = "hdfs://master:9000/mapreduce-example-data/topK";           
		
		//如果统计字数的job完成后就开始排序                       
		if(CopyTest.run(in, wordCout)){                          
		    Sort.run(wordCout, sort,topK);
		    
		}                                                         

	}

}
