package org.thinkit.data.vo;

public class MCHEntity {
	private String UUID;
	private String speechid;
	
	
	
	public MCHEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MCHEntity(String uUID, String speechid) {
		super();
		UUID = uUID;
		this.speechid = speechid;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getSpeechid() {
		return speechid;
	}
	public void setSpeechid(String speechid) {
		this.speechid = speechid;
	}
}
