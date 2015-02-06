package org.thinkit.parsedata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.thinkit.data.gather.LocalDataGather;
import org.thinkit.data.vo.PcmHeader;

public class ParseData {

	
	public String parseData(String pcmPath){
		String result="";
		//256字节的
		byte[] b =new byte[256];

		File f =new File(pcmPath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			fis.read(b);
			
			PcmHeader p =new PcmHeader();
//			System.out.println(p.parserArray(b).toString());
			result=p.parserArray(b).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	public String parseData2(String pcmPath) throws IOException{
//		File f =new File(pcmPath);
		
		FileInputStream fileInputStream = new FileInputStream(pcmPath);
		ByteBuffer buff = ByteBuffer.allocate(256);
		FileChannel fc = fileInputStream.getChannel();
	    int eof= fc.read(buff);
	    System.out.println(buff.array().length);
	    //读完头之后结束流
	    fc.close();
	    buff.clear();
		return "bbb";
	}
	
	
	/**
	 * 
	 * @param pcmdir  pcm文件目录
	 * @param suffix  pcm文件后缀
	 * @param destionFile  写入的目标文件
	 */
	public void batchWritePcmHeader(String pcmdir ,String suffix,String destionFile){
		File f =new File(destionFile);
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//测试多机版问题
		LocalDataGather lg=new LocalDataGather();
		List s  = lg.getFileList(pcmdir,suffix);
		
		for(int i=0;i<s.size();i++){
//			System.out.println(s.get(i));
			try {
				String x =s.get(i).toString();
				String name =x.substring(x.lastIndexOf("\\"),x.length());
//				System.out.println("file:"+s.get(i)+"消息头为:" +this.parseData(s.get(i).toString()));
				fw.write("file:"+name+"消息头为:" +this.parseData(s.get(i).toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String args[]){
//		String path ="D:"+File.separator+"thinkit"+File.separator+"project"+File.separator+"projectInfo"+File.separator+"固定音频检索"+File.separator+"11-25"+File.separator+"data"+File.separator+"201411121000090000240502474001-1.pcm";
//		String path ="D://111.v3";
		String path = "D:\\201411121000161000130102175F01-1.pcm";
		ParseData pd =new ParseData();
		System.out.println("file:"+path+"消息头为:" +pd.parseData(path));
		
		//测试多机版问题
		String pcmDir ="D://thinkit//project//projectInfo//固定音频检索//11-25//data//";
		String suffix ="pcm";
		String destionFile="D://parse2.txt";
		
		pd.batchWritePcmHeader(pcmDir, suffix, destionFile);
		
	}
	
	
}
