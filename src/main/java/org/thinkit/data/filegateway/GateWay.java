package org.thinkit.data.filegateway;

import java.net.URI;

import org.thinkit.data.vo.FileAbstract;

public interface GateWay {
		//根据用户路径或者uri来解析并且实现获取文件摘要
		public abstract FileAbstract getFileAbstract(String p);
		
		public abstract FileAbstract getFileAbstract(String p,String sid);
		
		//获取hbase 中的数据
		public abstract FileAbstract getDBFileAbstract(String sid);
		
		//根据用户路径或者uri来解析并且实现获取文件摘要
		public abstract FileAbstract getFileAbstract(URI u);
}
