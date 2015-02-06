package org.thinkit.parsedata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ReadWriteCompare {
	public static void main(String[] args) throws IOException {
		
		FileInputStream fileInputStream = new FileInputStream("D:" + File.separator + "111.v3");
		FileOutputStream fileOutputStream = new FileOutputStream("d:"+ File.separator + "test.v3");
		
		
		FileChannel inChannel = fileInputStream.getChannel();
		FileChannel outChannel = fileOutputStream.getChannel();

		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		// Direct Buffer的效率会更高。
		// ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
		long start = System.currentTimeMillis();
		while (true) {
			int eof = inChannel.read(byteBuffer);
			if (eof == -1)
				break;
			byteBuffer.flip();
			outChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		System.out.println("spending : " + (System.currentTimeMillis() - start));
		inChannel.close();
		outChannel.close();
	}
}
