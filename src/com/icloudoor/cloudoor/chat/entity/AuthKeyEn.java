package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class AuthKeyEn {

	private String message;
	private String sid;
	private String code;
	private List<KeyInfo> data;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<KeyInfo> getData() {
		return data;
	}
	public void setData(List<KeyInfo> data) {
		this.data = data;
	}
	
	
}
