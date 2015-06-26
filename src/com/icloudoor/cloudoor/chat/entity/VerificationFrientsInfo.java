package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class VerificationFrientsInfo {

	private String code;
	private String message;
	private String sid;
	private List<VerificationFrientsList> data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
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
	public List<VerificationFrientsList> getData() {
		return data;
	}
	public void setData(List<VerificationFrientsList> data) {
		this.data = data;
	}
	
	
	
}
