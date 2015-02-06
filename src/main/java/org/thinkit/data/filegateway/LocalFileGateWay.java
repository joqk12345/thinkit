package org.thinkit.data.filegateway;

import java.io.File;
import java.net.URI;

import org.thinkit.data.util.HBaseKeyGenerator;
import org.thinkit.data.util.MysqlHelper;
import org.thinkit.data.vo.FileAbstract;
import org.thinkit.data.vo.MCHEntity;

public class LocalFileGateWay {
	private MysqlHelper mshelper;
	
	public MysqlHelper getMshelper() {
		return mshelper;
	}

	public void setMshelper(MysqlHelper mshelper) {
		this.mshelper = mshelper;
	}





	public FileAbstract getFileAbstract(String p) {
		
		
		FileAbstract fileabs = new FileAbstract();
		File file = new File(p);
		//去掉后缀
		String filename=file.getName().substring(0,file.getName().lastIndexOf("."));
		
		fileabs.setFilename(filename);
		fileabs.setFilesize(file.length());
		
		
		
		//在此处为数据打上标签  这里的UUID是可以
//		fileabs.setUUID(HBaseKeyGenerator.generateHEXUUID());
		//可以以文件名先作为唯一性的考虑
		fileabs.setUUID(filename);
		//关系入库  关闭入库
		//mshelper.insertIdentifyInfo(new MCHEntity(fileabs.getUUID(),fileabs.getFilename()));
		
		return fileabs;
	}


	
	
}
