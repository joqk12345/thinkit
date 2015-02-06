package org.thinkit.hbaseT;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.thinkit.data.filegateway.LocalFileGateWay;
import org.thinkit.data.gather.LocalDataGather;
import org.thinkit.data.hbase.HBaseSound;
import org.thinkit.data.hbase.HbaseAudioUtil;
import org.thinkit.data.hbase.HbaseExtend;
import org.thinkit.data.util.MysqlHelper;
import org.thinkit.data.vo.FileAbstract;

/**
 * 测试扩展类
 * 
 * @author lenovo
 * 
 */
public class TestHbaseExtend {

//	private HbaseExtend he = new HbaseExtend();
//	private HBaseSound he = new HBaseSound();
	private HbaseAudioUtil he = new HbaseAudioUtil();

	@Test
	public void testWritelocalValByte() {
		// 生命本地文件网关
		LocalFileGateWay lfg = new LocalFileGateWay();
		MysqlHelper mshelper = new MysqlHelper();
		lfg.setMshelper(mshelper);

		System.out.println("1.文件网关中设置  数据库信息");

		// 得到 素有的list数据
		String aduioPath = "C://Users//lenovo//Desktop//固定音频检索//30s";
		String srcPath = "D://thinkit//project//projectInfo//固定音频检索//30s";

		List flist = LocalDataGather.getFileList(srcPath, "wav");
		System.out.println("=====================================2.筛选拿到文件列表================================");

		for (int i = 0; i < flist.size(); i++) {
			// 预处理 数据
			FileAbstract fat = lfg.getFileAbstract(flist.get(i).toString());
			// 这里是打印文件列表
			System.out.println(fat.toString());
			
			  try { 
//				  he.writelocalValByte(flist.get(i).toString(), "minifiletable", fat.getUUID()); 
//				  he.addHbaseBytes(tableName, rowKey, family, qualifier, bytes);
				  he.writelocalAudioValByte(flist.get(i).toString(),"AudioBase" ,fat.getUUID());
			  } catch (IOException e) {
					  e.printStackTrace(); 
				
			  }
	
		}
			 
		System.out.println("=====================================3.逐条写入数据================================");

	}

}
