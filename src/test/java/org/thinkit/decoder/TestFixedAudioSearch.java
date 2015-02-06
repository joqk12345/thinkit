package org.thinkit.decoder;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.thinkit.data.gather.LocalDataGather;

public class TestFixedAudioSearch {
	
	//测试固定音频
	private Sound sd;
	
	private LocalDataGather lg;
	@Test
	public void testDoRecognize() {
		//固定音频的测试
//		String soundFm = "C://Users//lenovo//Desktop//固定音频检索//30s//000047.01.wav";
//		String soundFm = "C://Users//lenovo//Desktop//固定音频检索//30s//000001.01.wav";
//		String soundFm = "C://Users//lenovo//Desktop//固定音频检索//song//000001.wav";

		String soundFm = "C://Users//lenovo//Desktop//固定音频检索//002.wav";
//		File file = new File(soundFm);
//		System.out.println(file.getName().substring(0,file.getName().lastIndexOf(".")));
		
		sd=new Sound();
		try {
			sd.setSoundData(sd.readRecogntionData(soundFm));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//设置IP 和 端口号
//		sd.setServer("192.168.11.39", 5020);
//		sd.setServer("192.168.14.204", 5050);
//		sd.setServer("192.168.14.204", 5020);
//		sd.setServer("192.168.11.39", 5020);
		sd.setServer("192.168.11.40", 5020);
		//进行识别测试
		sd.doRecognize();
	}
	
	@Test
	public void testBatchRecognize(){
		
//		List s  = lg.getFileList("C://Users//lenovo//Desktop//固定音频检索//30s","wav");
		List s  = lg.getFileList("C://Users//lenovo//Desktop//固定音频检索//song","wav");
		System.out.println("扫描数据 进行输入……");
		
		sd=new Sound();
//		sd.setServer("192.168.14.204", 5050);
		sd.setServer("192.168.11.40", 5020);
//		sd.setServer("192.168.11.39", 5020);
		for(int i=0;i<s.size();i++){
			System.out.println(s.get(i));
			
			try {
				sd.setSoundData(sd.readRecogntionData(s.get(i).toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			sd.doRecognize();
		}
		
//		System.out.println("开始进行数据导入……");
		
	}

}
