package org.thinkit.data.util;

public class Random {

	public static void main(String[] args) {
		long x =System.currentTimeMillis();
		
		System.out.println(x);
		System.out.println((int)x);
		
		  java.util.Random r=new java.util.Random(); 
		  for(int i=0;i<10;i++){ 
		      System.out.println(r.nextInt()); 
		  } 
	}
	
	public static int getRandomInt(){
		java.util.Random r=new java.util.Random();
		return r.nextInt();
	}

}
