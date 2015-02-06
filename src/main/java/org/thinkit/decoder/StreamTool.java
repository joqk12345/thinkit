package org.thinkit.decoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class StreamTool {
    public static void main(String[] args) {  
        try {  
            File file = new File("d:\\keywords.txt");  
            FileInputStream fin = new FileInputStream(file);  
            byte[] filebt = readStream(fin);  
            System.out.println(filebt.length);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }     
    }     
    /** 
     * @功能 读取流 
     * @param inStream 
     * @return 字节数组 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception {  
//        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
//        byte[] buffer = new byte[1024];  
//        int len = -1;  
//        while ((len = inStream.read(buffer)) != -1) {  
//            outSteam.write(buffer, 0, len);  
//        }  
//        outSteam.close();  
//        inStream.close();  
//        return outSteam.toByteArray();  
    	int count = 0;
		while (count == 0) {
			count = inStream.available();
		}
		byte[] b = new byte[count];
		inStream.read(b);
		return b;
    }  
}
