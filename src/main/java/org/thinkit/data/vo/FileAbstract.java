package org.thinkit.data.vo;

public class FileAbstract {
	//统一的UUID
	private String UUID;
	//文件名称
	private String filename ;
	//文件大小
	private long filesize ;
	//文件分片
	private int splitenum ;
	
	//文件字节
	private byte[] data;

	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	
	public int getSplitenum() {
		return splitenum;
	}
	public void setSplitenum(int splitenum) {
		this.splitenum = splitenum;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "FileAbstract [UUID=" + UUID + ", filename=" + filename + ", filesize=" + filesize + ", splitenum=" + splitenum + "]";
	}

}
