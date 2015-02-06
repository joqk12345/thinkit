package org.thinkit.decoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * 
 * @author lenovo 请求头信息
 */
public class ASRHeader {

	private static final int KMaxProvider = 16;

	private short cmd;
	private short idx;
	private short pid;
	private short time_out;
	private short version;
	private short reserved;
	private int time_stamp;
	private int log_id;
	private char[] provider;
	private int res_num;
	private int body_len;

	//
	public ASRHeader() {

		char[] arrayOfChar = new char[KMaxProvider];
		this.provider = arrayOfChar;
		int i = 0;
		while (true) {
			if (i >= 16) {
				this.cmd = 0;
				this.idx = 0;
				this.time_out = 0;
				this.pid = 0;
				this.version = 0;
				this.version = 0;
				this.log_id = 0;
				this.res_num = 0;
				this.body_len = 0;
				return;
			}
			this.provider[i] = '0';
			i += 1;
		}
	}
	
	public void clear() {
		this.provider = null;
	}
	
	
	/**
	 * 读int类型 转换过的
	 * @param abyte0
	 * @param i
	 * @return
	 */
	public static int readInt(byte abyte0[], int i)
	{
		int j = i + 0;
		int k = abyte0[j] & 0xff;
		int l = i + 1;
		int i1 = (abyte0[l] & 0xff) << 8;
		int j1 = k | i1;
		int k1 = i + 2;
		int l1 = (abyte0[k1] & 0xff) << 16;
		int i2 = j1 | l1;
		int j2 = i + 3;
		int k2 = (abyte0[j2] & 0xff) << 24;
		return i2 | k2;
	}
	
	/**
	 * 读short类型的转换过的
	 * @param abyte0
	 * @param i
	 * @return
	 */
	private static short readShort(byte abyte0[], int i)
	{
		int j = i + 0;
		int k = abyte0[j] & 0xff;
		int l = i + 1;
		int i1 = (abyte0[l] & 0xff) << 8;
		return (short)(k | i1);
	}
	/**
	 * 读字符类型转换过为低字节的
	 * @param abyte0
	 * @param i
	 * @param j
	 * @return
	 * @throws Exception
	 */
	private static String readString(byte abyte0[], int i, int j)
		throws Exception
	{
		String s = null;
		if(j > 0){
			byte abyte1[] = new byte[j];
			int k = 0;
			int i1 = 0;
			for (k = 0; k < j; k++)
			{
				i1 = k + i;
				abyte1[k]= abyte0[i1];
			}
			s = new String(abyte1, "UTF-8");
		}
		return s;
	}
	
	/**
	 * 解析返回报文
	 * @param abyte0
	 * @return
	 */
	public ASRHeader parserArray(byte abyte0[]){
		ASRHeader res =new ASRHeader();
		res.clear();
		
		int a = 0;
		res.setCmd(readShort(abyte0, a));
		int b = a +  2;
		res.setIdx(readShort(abyte0, b));
		int c = b+2;
		res.setPid(readShort(abyte0, c));
		int d = c+2;
		res.setTime_out(readShort(abyte0, d));
		int f = d+2;
		res.setVersion(readShort(abyte0, f));
		int g = f+2;  //10
		res.setReserved(readShort(abyte0, g));
		int h = g + 2;//12  加前一个字段的步幅
		res.setTime_stamp(readInt(abyte0, h));
		int i = h +4 ; //16
		res.setLog_id(readInt(abyte0, i));
		int j = i+ 4;  //20
		char ac2[] = null;
		try {
			ac2 = readString(abyte0, j, 16).toCharArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.setProvider(ac2);

		int k =j +16; //36
		res.setRes_num(readInt(abyte0, k));
		int l =k +4;  //40
		res.setBody_len(readInt(abyte0, l));
		
		
		return res;
	}
	
	
	/**
	 * 将报头转为小字节序
	 * @return
	 */
	public byte[] toByteArray(){
		
		int i;
		ByteBuffer bytebuffer;
		i = 0;
		bytebuffer = ByteBuffer.allocate(44);
		ByteOrder byteorder = ByteOrder.LITTLE_ENDIAN;
		ByteBuffer bytebuffer1 = bytebuffer.order(byteorder);
		
		ByteBuffer bytebuffer2 = bytebuffer.putShort(this.getCmd());
		ByteBuffer bytebuffer3 = bytebuffer.putShort(this.getIdx());
		ByteBuffer bytebuffer4 = bytebuffer.putShort(this.getPid());
		ByteBuffer bytebuffer5 = bytebuffer.putShort(this.getTime_out());
		ByteBuffer bytebuffer6 = bytebuffer.putShort(this.getVersion());
		ByteBuffer bytebuffer7 = bytebuffer.putShort(this.getReserved());
		
		ByteBuffer bytebuffer8 = bytebuffer.putInt(this.getTime_stamp());
		ByteBuffer bytebuffer9 = bytebuffer.putInt(this.getLog_id());
		
		i = 0;
		for(i = 0 ; i < 16; i++)
		{
			byte byte1 = (byte)this.provider[i];
			ByteBuffer bytebuffer10 = bytebuffer.put(byte1);
		}
		
		ByteBuffer bytebuffer11 = bytebuffer.putInt(this.getRes_num());
		ByteBuffer bytebuffer12 = bytebuffer.putInt(this.getBody_len());
		
		return bytebuffer.array();
	}
	
	
	public static void main(String args[]){
		
		ASRHeader ash = new ASRHeader();
		ash.setCmd((short)1);
		
		System.out.println(ash.toByteArray());
	}
	

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}

	public short getIdx() {
		return idx;
	}

	public void setIdx(short idx) {
		this.idx = idx;
	}

	public short getPid() {
		return pid;
	}

	public void setPid(short pid) {
		this.pid = pid;
	}

	public short getTime_out() {
		return time_out;
	}

	public void setTime_out(short time_out) {
		this.time_out = time_out;
	}

	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	public short getReserved() {
		return reserved;
	}

	public void setReserved(short reserved) {
		this.reserved = reserved;
	}

	public int getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(int time_stamp) {
		this.time_stamp = time_stamp;
	}

	public int getLog_id() {
		return log_id;
	}

	public void setLog_id(int log_id) {
		this.log_id = log_id;
	}

	public char[] getProvider() {
		return provider;
	}

	public void setProvider(char[] provider) {
		this.provider = provider;
	}

	public int getRes_num() {
		return res_num;
	}

	public void setRes_num(int res_num) {
		this.res_num = res_num;
	}

	public int getBody_len() {
		return body_len;
	}

	public void setBody_len(int body_len) {
		this.body_len = body_len;
	}

	@Override
	public String toString() {
		return "ASRHeader [cmd=" + cmd + ", idx=" + idx + ", pid=" + pid
				+ ", time_out=" + time_out + ", version=" + version
				+ ", reserved=" + reserved + ", time_stamp=" + time_stamp
				+ ", log_id=" + log_id + ", provider="
				+ Arrays.toString(provider) + ", res_num=" + res_num
				+ ", body_len=" + body_len + "]";
	}
	
	

}
