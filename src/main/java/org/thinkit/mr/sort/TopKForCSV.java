package org.thinkit.mr.sort;

import java.io.IOException;

/**
 * 该类用作频次统计的TopK 的类
 * 使用方法如下
 * hadoop jar thinkit.jar [主类] [源输入路径] [汇总输入路径] [排序输出路径] [取前多少条路径]
 * hadoop jar thinkit.jar [org.thinkit.mr.sort.TopKForCSV] [in] [out] [sort] [topK]
 */
public class TopKForCSV {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		
		 //要统计字数，排序的cnr记录                                 
//		String in = "hdfs://master:9000/mapreduce-example-data";
        String in = args[0];

        //统计频次后的结果
//		String wordCout = "hdfs://master:9000/mapreduce-example-data/_output2";   
//		String wordCout = "hdfs://master:9000/mapreduce-example-data/output";
        String wordCout = args[1];
		//对统计完后的结果再排序后的内容                          
//		String sort = "hdfs://master:9000/mapreduce-example-data/sort";
        String sort = args[2];
		//前K条                                                   
//		String topK = "hdfs://master:9000/mapreduce-example-data/topK";
        String topK = args[3];
		//如果统计字数的job完成后就开始排序                       
		if(CopyTest.run(in, wordCout)){                          
		    Sort.run(wordCout, sort,topK);
		    
		}

    }

}
