package org.thinkit.data.hbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HbaseAudioUtil {

	private static String table = "AudioBase";

	/**
	 * 用于测试的写法
	 * 
	 * @param localsrcpath
	 * @param table
	 * @param UUID
	 * @return
	 * @throws IOException
	 */
	public static boolean writelocalAudioValByte(String localsrcpath, String table,String UUID) throws IOException {
		File f = new File(localsrcpath);
		FileInputStream fis;

		long length = f.length();
		byte[] bytes = new byte[(int) length];
		fis = new FileInputStream(f);
		int c = fis.read(bytes);
		while (c != -1) {
			try {
				// HBaseSound.addBytesRecord(HBaseIFamilyHelper.table,UUID,"m",Bytes.toBytes("lock"),"true".getBytes());
				HBaseSound.addHbaseBytes(HbaseAudioUtil.table, UUID, "AudioOriginalByteFile","AudioByte", bytes);
				// HBaseSound.addBytesRecord(HBaseIFamilyHelper.table,UUID,"m",Bytes.toBytes("lock"),"false".getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
			c = fis.read(bytes);
		}
		fis.close();
		return true;
	}

}
