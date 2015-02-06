package org.thinkit.decoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;

import org.thinkit.data.gather.LocalDataGather;
import org.thinkit.decoder.protocal.protobuf.FixAudioSearchProtocal;


public class ProtobufTestUtil {
	
	private  LocalDataGather lg;

	public  byte[] readRecogntionData(String filepath) throws Exception {
		File file = new File(filepath);
		FileInputStream fin = new FileInputStream(file);
		byte[] filebt = StreamTool.readStream(fin);
		return filebt;
	}
	
	public  void doFixedTempleSearch(String AudioTestPath,String decoderServerIp,int decoderServerPort) throws Exception {
		
		
		byte[] redata = readRecogntionData(AudioTestPath);
		//发送语音头
		FixAudioSearchProtocal.AudioHead.Builder builder1= FixAudioSearchProtocal.AudioHead.newBuilder();
		
		builder1.setIdx(0001);
		Timestamp d = new Timestamp(System.currentTimeMillis()); 
		builder1.setTimeStamp(d.getTime());
		builder1.setVersion(1);
		builder1.setDatacode(1);
		//随便设置了一个
		builder1.setDataLen(redata.length);
		
		FixAudioSearchProtocal.AudioHead ATSHeader =builder1.build();
		byte[] EncodingATSHeader = ATSHeader.toByteArray();
		
		
		try {
			Socket socket = new Socket(decoderServerIp, decoderServerPort);
			
			// 开启保持活动状态的套接字
			socket.setKeepAlive(true);
			DataOutputStream ops = new DataOutputStream(socket.getOutputStream());
			
			//1.0开始发送识别头信息
			ops.write(EncodingATSHeader); // 开始识别
			ops.flush();
			
			System.out.println("开始发送语音数据" );		
			ops.write(redata);
			System.out.println("结束发送语音数据" );
			
			
			// 得到返回结果
			 DataInputStream ips = new DataInputStream(socket.getInputStream());
			 
			 FixAudioSearchProtocal.ResultHead testResultHead = null;
			 FixAudioSearchProtocal.ATS_Result testResult;
			 
			 if (null != ops && null != ips) {
					int n = 15;
					byte buffer[] = new byte[n];
					// 读取输入流
					if ((ips.read(buffer, 0, n) != -1)) {
						// System.out.print(new String(buffer));
//						System.out.println("总次数为:" + (++i)	+ asr.parserArray(buffer).toString());
						System.out.println(testResultHead = FixAudioSearchProtocal.ResultHead.parseFrom(buffer));
					}
					//若果识别成功
					if(1 == testResultHead.getResultCode()){
						int resultLen = testResultHead.getResultLen();
						byte buffer1[] = new byte[resultLen];
						//读取结果信息
						if ((ips.read(buffer1, 0, resultLen) != -1)) {
							System.out.println(testResult = FixAudioSearchProtocal.ATS_Result.parseFrom(buffer1));
						}
					}
			 }
			 
			 socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param AudioTestDir
	 * @param decoderServerIp
	 * @param decoderServerPort
	 * @throws Exception
	 */
	public  void batcheFixedTempleSearch(String AudioTestDir,String suffix,String decoderServerIp,int decoderServerPort) throws Exception {
		
		List s  = lg.getFileList(AudioTestDir,suffix);
		
		for(int i=0;i<s.size();i++){
			System.out.println(s.get(i));
			
			try {
				doFixedTempleSearch(s.get(i).toString(), decoderServerIp, decoderServerPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	


	public static void main(String[] args) throws Exception {
		
		System.out.println("hhh");
		
		String decoderServerIp ="192.168.11.39";
		
		int decoderServerPort = 40010;
		
		String suffix="pcm";
		
		String AudioTestPath ="C://Users//lenovo//Desktop//alaw-pcm//S00147_Android_150CM.wav";
		
		String AudioTestDir ="C://Users//lenovo//Desktop//alaw-pcm";
		ProtobufTestUtil pt =new ProtobufTestUtil();
		
		pt.doFixedTempleSearch(AudioTestPath, decoderServerIp, decoderServerPort);
		
		pt.batcheFixedTempleSearch(AudioTestDir, suffix, decoderServerIp, decoderServerPort);
		
//
		
	}
	
}
