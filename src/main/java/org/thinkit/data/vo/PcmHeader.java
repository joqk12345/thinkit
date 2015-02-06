package org.thinkit.data.vo;

import java.nio.ByteBuffer;

import org.thinkit.util.BCDDecode;

public class PcmHeader {
	//0-8保留  若有字段，设置为0
	
	//9# v3cp号
	private int v3cp1;
	//10#-11# 录音通道号
	private short recodeRoute2;
	//主叫号码
	private String callnum;
	//被叫号码
	private String calledNum;
	
	private String startCallTime;


	//局号
	private int departNum;
	//话音输出模式
	private int toneMode;
	//编码模式
	private int codingMode;
	//dpc编号
	private int DPCNum;
	//opc编号
	private int OPCNum;
	
	//CIC 编号 60#－61#
	private short CICNum;
	
	//模块号64#
	private int moduleNum;
	//局向号65#
	private int departDrectNum;
	//实时模板号68#-69# 
	private short realTimeModuelNum;
	
	//type 74# 类型0疑似语音,1检出语音,2疑似传真3检出传真
	private int typeNum;
	// 76# 前导模板号 或 人判属性
	private int headModuleNum;
	
	
	
	//话音输出模式的枚举结构
	public enum ToneMode {  
		Type1("单路输出",0),
		Type2("合路输出",1);
		
		private String name;  
	    private int index;  
	    // 构造方法  
	    private ToneMode(String name, int index) {  
	        this.name = name;  
	        this.index = index;  
	    }
	    
	    public  String getName(int index) {  
	        for (ToneMode c : ToneMode.values()) {  
	            if (c.getIndex() == index) {  
	                return c.name;  
	            }  
	        }  
	        return null;  
	    }
	    
	    public String getName() {  
	        return name;  
	    }  
	    public void setName(String name) {  
	        this.name = name;  
	    }  
	    public int getIndex() {  
	        return index;  
	    }  
	    public void setIndex(int index) {  
	        this.index = index;  
	    }  
	    //覆盖方法  
	    @Override  
	    public String toString() {  
	        return this.index+"_"+this.name;  
	    }  
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PcmHeader [v3cp1=" + v3cp1 + ", recodeRoute2=" + recodeRoute2
				+ ", callnum=" + callnum + ", calledNum=" + calledNum
				+ ", startCallTime=" + startCallTime + ", departNum="
				+ departNum + ", toneMode=" + toneMode + ", codingMode="
				+ codingMode + ", DPCNum=" + DPCNum + ", OPCNum=" + OPCNum
				+ ", CICNum=" + CICNum + ", moduleNum=" + moduleNum
				+ ", departDrectNum=" + departDrectNum + ", realTimeModuelNum="
				+ realTimeModuelNum + ", typeNum=" + typeNum
				+ ", headModuleNum=" + headModuleNum + "]";
	}
	/**
	 *  解析返回报文 
	 * */
	public PcmHeader parserArray(byte abyte0[]){
		//初始化
		PcmHeader res= new PcmHeader();
		//封装一个byte数组 为 Bytebuffer
		ByteBuffer buff = ByteBuffer.wrap(abyte0);
		
		int a = 9;
		//#9得到并这是 v3cp号
		res.setV3cp1((int)buff.get(a));
		
		//#得到#10 #11的值
		int b =10;
		int c =11;
//		int a1 =(int)buff.getInt(b);
//		int a2 =(int)buff.getInt(b);
		
		byte[] byte0 =new  byte[2];
		byte0[0]=buff.get(b);
		byte0[1]=buff.get(c);
		
		res.setRecodeRoute2(this.getShort(byte0, false));
		
		//#12 #19得到主叫号码
		byte[] byte1 =new byte[8];
		for(int d=12,i=0;d<=19;d++,i++){
			byte1[i]=buff.get(d);
		}
		res.setCallnum(BCDDecode.bcd2Str(byte1));
		
		
		//#20 #27 设置被叫号码
		byte[] byte2 =new byte[8];
		for(int d=20,i=0;d<=27;d++,i++){
			byte2[i]=buff.get(d);
		}
		res.setCalledNum(BCDDecode.bcd2Str(byte2));
		
		//#28  #34   yyyymmddHHMMSS,其中SS为十进制其它为bcd码 
		
		byte[] byte3 =new byte[6];
		for(int d=28,i=0;d<=33;d++,i++){
			byte3[i]=buff.get(d);
		}
		//第34位为十进制
		res.setStartCallTime(BCDDecode.bcd2Str(byte3)+(int)buff.get(34));
		
		//#35 局号
		int d =35;
		res.setDepartNum((int)buff.get(d));
		
		//#36话音输出模式
		int e=36;
		res.setToneMode((int)buff.get(e));
		
		//#37编码模式
		int f=37;
		res.setCodingMode((int)buff.get(f));
		
		
		//#DPC  空留
		int g=54;
		int h=55;
		int i=56;
		
		byte[] byte6 =new  byte[4];
		
		byte6[0]=0;
		byte6[1]=buff.get(i);
		byte6[2]=buff.get(g);
		byte6[3]=buff.get(h);
		
		res.setDPCNum(this.getInt(byte6, false));
		
		//OPC 空留
		int j=57;
		int k=58;
		int l=59;
		
		byte[] byte7 =new  byte[4];
		
		byte7[0]=0;
		byte7[1]=buff.get(l);
		byte7[2]=buff.get(j);
		byte7[3]=buff.get(k);
		res.setOPCNum(this.getInt(byte7, false));
		
		//CIC
		
		int m =60;
		int n=61;
		byte[] byte4 =new  byte[2];
		byte0[0]=buff.get(m);
		byte0[1]=buff.get(n);
		res.setRecodeRoute2(this.getShort(byte4, false));
		
//		64# 模块号
		int o=64;
		res.setModuleNum((int)buff.get(o));
//		65# 局向号
		int p=65;
		res.setDepartDrectNum(p);
//		68#-69#  实时模板号
		int q=68;
		int r=69;
		byte[] byte5 =new  byte[2];
		byte0[0]=buff.get(q);
		byte0[1]=buff.get(r);
		res.setRealTimeModuelNum(this.getShort(byte5, false));
		
//		74#  类型0疑似语音,1检出语音,2疑似传真3检出传真
		int s=74;
		res.setTypeNum((int)buff.get(s));
//		76#  前导模板号 或 人判属性
		int t=76;
		res.setHeadModuleNum((int)buff.get(t));
		
		return res;
	}
	/**
	 * 
	 * @param buf
	 * @param asc  是否为asc（小头字节序）
	 * @return
	 */
	public final  short getShort(byte[] buf, boolean asc){
		if (buf == null){
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 2){
			throw new IllegalArgumentException("byte array size > 2 !");
		}
		short r = 0;
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--){
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		else
			for (int i = 0; i < buf.length; i++){
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		return r;
	}
	
	/**
	 * 
	 * @param buf
	 * @param asc  是否为asc编码
	 * @return
	 */
	public final  int getInt(byte[] buf, boolean asc){
	    if (buf == null){
	      throw new IllegalArgumentException("byte array is null!");
	    }
	    if (buf.length > 4){
	      throw new IllegalArgumentException("byte array size > 4 !");
	    }
	    int r = 0;
	    if (asc)for (int i = buf.length - 1; i >= 0; i--) 
	        {
	        r <<= 8;
	        r |= (buf[i] & 0x000000ff);
	      }
	    else
	      for (int i = 0; i < buf.length; i++){
	        r <<= 8;
	        r |= (buf[i] & 0x000000ff);
	      }
	    return r;
	}

	public int getV3cp1() {
		return v3cp1;
	}

	public void setV3cp1(int v3cp1) {
		this.v3cp1 = v3cp1;
	}

	public short getRecodeRoute2() {
		return recodeRoute2;
	}

	public void setRecodeRoute2(short recodeRoute2) {
		this.recodeRoute2 = recodeRoute2;
	}


	public String getCallnum() {
		return callnum;
	}


	public void setCallnum(String callnum) {
		this.callnum = callnum;
	}


	public String getCalledNum() {
		return calledNum;
	}


	public void setCalledNum(String calledNum) {
		this.calledNum = calledNum;
	}


	public int getDepartNum() {
		return departNum;
	}


	public void setDepartNum(int departNum) {
		this.departNum = departNum;
	}


	public int getToneMode() {
		return toneMode;
	}


	public void setToneMode(int toneMode) {
		this.toneMode = toneMode;
	}


	public int getCodingMode() {
		return codingMode;
	}


	public void setCodingMode(int codingMode) {
		this.codingMode = codingMode;
	}


	public String getStartCallTime() {
		return startCallTime;
	}


	public void setStartCallTime(String startCallTime) {
		this.startCallTime = startCallTime;
	}
	public short getCICNum() {
		return CICNum;
	}
	public void setCICNum(short cICNum) {
		CICNum = cICNum;
	}
	public int getModuleNum() {
		return moduleNum;
	}
	public void setModuleNum(int moduleNum) {
		this.moduleNum = moduleNum;
	}
	public int getDepartDrectNum() {
		return departDrectNum;
	}
	public void setDepartDrectNum(int departDrectNum) {
		this.departDrectNum = departDrectNum;
	}
	public short getRealTimeModuelNum() {
		return realTimeModuelNum;
	}
	public void setRealTimeModuelNum(short realTimeModuelNum) {
		this.realTimeModuelNum = realTimeModuelNum;
	}
	public int getTypeNum() {
		return typeNum;
	}
	public void setTypeNum(int typeNum) {
		this.typeNum = typeNum;
	}
	public int getHeadModuleNum() {
		return headModuleNum;
	}
	public void setHeadModuleNum(int headModuleNum) {
		this.headModuleNum = headModuleNum;
	}
	public int getDPCNum() {
		return DPCNum;
	}
	public void setDPCNum(int dPCNum) {
		DPCNum = dPCNum;
	}
	public int getOPCNum() {
		return OPCNum;
	}
	public void setOPCNum(int oPCNum) {
		OPCNum = oPCNum;
	}
	
	

}
