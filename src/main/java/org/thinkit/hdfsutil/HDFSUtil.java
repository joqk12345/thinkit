package org.thinkit.hdfsutil;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.thinkit.decoder.StreamTool;

public class HDFSUtil {
	
	
	
	private static final String YARN_RESOURCE="master:8032";
//	private static final String DEFAULT_FS="hdfs://master:8020";
	private static final String DEFAULT_FS="hdfs://master:9000";
	
	
	public synchronized static FileSystem getFileSystem(String ip, int port) {
		FileSystem fs = null;
//	伪分布模式
		String url = "hdfs://" + ip + ":" + String.valueOf(port);
		Configuration config = new YarnConfiguration();
		config.set("fs.defaultFS", url);
		config.set("mapreduce.framework.name", "yarn");
		config.set("yarn.resourcemanager.address", YARN_RESOURCE);
		
		try {
			fs = FileSystem.get(config);
		} catch (Exception e) {
		}
		return fs;
	}
	
	public synchronized static void listNode(FileSystem fs) {
		DistributedFileSystem dfs = (DistributedFileSystem) fs;
		try {
			DatanodeInfo[] infos = dfs.getDataNodeStats();
			for (DatanodeInfo node : infos) {
				System.out.println("HostName: " + node.getHostName() + "/n"	+ node.getDatanodeReport());
				System.out.println("--------------------------------");
			}
		} catch (Exception e) {
		}
	}
	/**
	 * 创建目录和父目录
	 * 
	 * @param fs
	 * @param dirName
	 */
	public synchronized static void mkdirs(FileSystem fs, String dirName) {
		// Path home = fs.getHomeDirectory();
		Path workDir = fs.getWorkingDirectory();
		// String dir = workDir + "/" + dirName;//以前的写法 create directory
		// hdfs://192.168.11.37:9000/user/guoyanwei//tmp/testdir
		String dir = "hdfs://master:9000" + dirName;
		Path src = new Path(dir);
		// FsPermission p = FsPermission.getDefault();
		boolean succ;
		try {
			succ = fs.mkdirs(src);
			if (succ) {
				System.out.println("create directory " + dir + " successed. ");
			} else {
				System.out.println("create directory " + dir + " failed. ");
			}
		} catch (Exception e) {
			System.out.println("create directory " + dir + " failed :" + e);
		}
	}
	
	/**
	 * 删除目录和子目录
	 * 
	 * @param fs
	 * @param dirName
	 */
	public synchronized static void rmdirs(FileSystem fs, String dirName) {
		// Path home = fs.getHomeDirectory();
		// Path workDir = fs.getWorkingDirectory();
		String dir = "hdfs://192.168.11.37:9000" + "/" + dirName;
		Path src = new Path(dir);
		boolean succ;
		try {
			succ = fs.delete(src, true);
			if (succ) {
				System.out.println("remove directory " + dir + " successed. ");
			} else {
				System.out.println("remove directory " + dir + " failed. ");
			}
		} catch (Exception e) {
			System.out.println("remove directory " + dir + " failed :");
		}
	}

	
	/**
	 * 遍历HDFS上的文件和目录
	 * 
	 * @param fs
	 * @param path
	 */
	public synchronized static void listFile(FileSystem fs, String path) {
		Path dst = new Path( path);
		try {
			String relativePath = "";
			FileStatus[] fList = fs.listStatus(dst);
			for (FileStatus f : fList) {
				if (null != f) {
					relativePath = new StringBuffer()
							.append(f.getPath().getParent()).append("/")
							.append(f.getPath().getName()).toString();
					if (f.isDir()) {
						listFile(fs, relativePath);
					} else {
						System.out.println(convertSize(f.getLen()) + "/t/t"	+ relativePath);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	/**
	 * 上传目录或文件
	 * 
	 * @param fs
	 * @param local
	 * @param remote
	 */
	public synchronized static void upload(FileSystem fs, String local,String remote) {
		// Path home = fs.getHomeDirectory();
		// Path workDir = fs.getWorkingDirectory();
		String dir = "hdfs://master:8020" + remote;
		Path dst = new Path(dir);
		Path src = new Path(local);
		try {
			fs.copyFromLocalFile(false, true, src, dst);
			System.out.println("upload " + local + " to  " + remote + " successed. ");
		} catch (Exception e) {
			System.out.println("upload " + local + " to  " + remote + " failed :");
		}
	}
	
	/**
	 * 下载目录或文件
	 * 
	 * @param fs
	 * @param local
	 * @param remote
	 */
	public synchronized static void download(FileSystem fs, String local,
			String remote) {
		Path dst = new Path(DEFAULT_FS + remote);
		Path src = new Path(local);
		try {
			fs.copyToLocalFile(false, dst, src);
			System.out.println("download from " + remote + " to  " + local
					+ " successed. ");
		} catch (Exception e) {
			System.out.println("download from " + remote + " to  " + local
					+ " failed :");
		}
	}
	
	/**
	 * 字节数转换
	 * 
	 * @param size
	 * @return
	 */
	public synchronized static String convertSize(long size) {
		String result = String.valueOf(size);
		if (size < 1024 * 1024) {
			result = String.valueOf(size / 1024) + " KB";
		} else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
			result = String.valueOf(size / 1024 / 1024) + " MB";
		} else if (size >= 1024 * 1024 * 1024) {
			result = String.valueOf(size / 1024 / 1024 / 1024) + " GB";
		} else {
			result = result + " B";
		}
		return result;
	}
	
	public synchronized static void write(FileSystem fs, String path,String data) {
		Path dst = new Path(DEFAULT_FS + path);
		try {
			FSDataOutputStream dos = fs.create(dst);
			dos.writeUTF(data);
			dos.close();
			System.out.println("write content to " + path + " successed. ");
		} catch (Exception e) {
		}
	}
	
	public synchronized static String read(FileSystem fs, String path) {
		String content = null;
		Path dst = new Path( path);
		try {
			// reading
			FSDataInputStream dis = fs.open(dst);
			content = dis.readUTF();
			dis.close();
			System.out.println("read content from " + path + " successed. ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public synchronized static void readFile(FileSystem fs, String path) {
		String content = null;
		Path dst = new Path( path);
		try {
			// reading
			FSDataInputStream dis = fs.open(dst);
			
			 String line;
	    	  while ((line = dis.readLine()) != null) {
	    	    System.out.println("line : "+line);
	    	  }
			
			dis.close();
			System.out.println("read content from " + path + " successed. ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	   public static byte[] readStream(InputStream inStream) throws Exception {  
//	        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
//	        byte[] buffer = new byte[1024];  
//	        int len = -1;  
//	        while ((len = inStream.read(buffer)) != -1) {  
//	            outSteam.write(buffer, 0, len);  
//	        }  
//	        outSteam.close();  
//	        inStream.close();  
//	        return outSteam.toByteArray();  
	    	int count = 0;
			while (count == 0) {
				count = inStream.available();
			}
			byte[] b = new byte[count];
			inStream.read(b);
			return b;
	    }  
	   
	public synchronized static byte[] readByteFile(FileSystem fs, String path) {
		byte[] filebt=null;
		Path dst = new Path( path);
		try {
			// reading
			FSDataInputStream dis = fs.open(dst);
			filebt = readStream(dis);
			dis.close();
			System.out.println("read content from " + path + " successed. ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filebt;
	}
		
	/**
	 * 打印系统配置
	 * 
	 * @param fs
	 */
	public synchronized static void listConfig(FileSystem fs) {
		Iterator<Entry<String, String>> entrys = fs.getConf().iterator();
		while (entrys.hasNext()) {
			Entry<String, String> item = entrys.next();
			System.out.println((item.getKey() + ": " + item.getValue()));
		}
	}
	


	public static void main(String[] args) throws IOException {

		  FileSystem fs = null;

	        try {
	            fs = HDFSUtil.getFileSystem("master", 9000);
	            
	            String dirName = "/tmp/test/aa";
	            String localSrc = "/home/joqk/testdir/bbb.txt";

	            String dst = "/tmp/log4j.properties";
	            
	            //上传本地文件
//	            HDFSUtil.upload(fs, localSrc ,dst);
	            
//	            HDFSUtil.listNode(fs);
	            
	            HDFSUtil.listFile(fs, "hdfs://master:9000/mapreduce-example-data/_output2");
	            //创建目录
//	            HDFSUtil.mkdirs(fs, "//tmp/testDir");
	            //删除目录            
	            //HDFSUtil.rmdirs(fs, dirName);
	            //下载文件到本地
//	            HDFSUtil.download(fs, localSrc, dst);
	            //创建文件
//	            HDFSUtil.write(fs, "/tmp/9.log", "test-测试");
	            //读取文件
//	            String content = HDFSUtil.read(fs, "/home/hadoop/keywords.txt");
//	            HDFSUtil.readFile(fs, "/home/hadoop/keywords.txt");
//	            System.out.println(content);
	            //遍历文件夹(test successful)
//	            HDFSUtil.listFile(fs, "/home/");
	            //遍历节点
	            //HDFSUtil.listNode(fs);
	            //遍历配置信息
//	            HDFSUtil.listConfig(fs);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        } finally {
	            if (fs != null) {
	                fs.close();
	            }
	        }

	        System.out.println("over");
		
		
	}

}

