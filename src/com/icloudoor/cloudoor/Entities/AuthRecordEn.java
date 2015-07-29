package com.icloudoor.cloudoor.Entities;

import java.util.List;

public class AuthRecordEn {

	private String message;
	private String sid;
	private int code;
	private List<RecordsEn> data;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public List<RecordsEn> getData() {
		return data;
	}
	public void setData(List<RecordsEn> data) {
		this.data = data;
	}
	
	
}
