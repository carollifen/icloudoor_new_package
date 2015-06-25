package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class UsersDetailInfo {
	
	private String code;
	private List<UsersDetailList> data;
	private String message;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<UsersDetailList> getData() {
		return data;
	}
	public void setData(List<UsersDetailList> data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
