package org.thinkit.data.util;

import java.util.UUID;

public class HBaseKeyGenerator {
	/**
	 * 对存储key进行uuid 变为16进制
	 * 随机可能导致 主键重复
	 * @return
	 */
	public static String generateHEXUUID() {
		
		UUID uuid = UUID.randomUUID();
		
		return toHexString(uuid.toString());
	}
	/**
	 * 将字符串变为16进制的字符
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	
	
	public static void main(String[] args) {
		UUID uuid = UUID.randomUUID();
		System.out.println(HBaseKeyGenerator.toHexString(uuid.toString()));

	}
}
