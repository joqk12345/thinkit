package org.thinkit.data.vo;

public class Splite {
	//目前的单位为kb   ，先默认为512
		public final static int Splite4Size = 4*1024;
		public final static int Splite128Size = 128*1024;
		public final static int Splite512Size = 512*1024;
		public final static int Splite1024Size = 1024*1024;
		
		
		
		Byte[] bytekey = new Byte[4];
		Byte[] byteval = new Byte[512];
}
