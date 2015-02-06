package org.thinkit.parsedata;

import java.nio.ByteBuffer;

public class ByteBufferTest {

	public static void main(String[] args) {
	     String str = "helloWorld";                                                                       
	     ByteBuffer buff = ByteBuffer.wrap(str.getBytes());                                             
	     System.out.println("1position:"+buff.position()+"\t limit:"+buff.limit());                            
	     //读取两个字节                                                                                       
	     int i = (int) buff.get(0);
	     System.out.println(i);       
	     int j = (int) buff.get(5);
	     System.out.println(buff.get(5)+"强制转换过后的"+j);       
	     
	     
	     System.out.println("2position:"+ buff.position()+"\t limit:"+buff.limit());                 
	     buff.mark();                                                                                         
	     System.out.println("3position:"+buff.position()+"\t limit:"+buff.limit());                            
	     buff.flip();                                                                                         
	     System.out.println("4position:"+buff.position()+"\t limit:"+buff.limit());                            

	}

}
