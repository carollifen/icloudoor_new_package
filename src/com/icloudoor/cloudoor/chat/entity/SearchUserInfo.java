package com.icloudoor.cloudoor.chat.entity;

import java.util.List;


public class SearchUserInfo {

	private String message;
	private String code;
	private List<SearchUserList> data;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<SearchUserList> getData() {
		return data;
	}
	public void setData(List<SearchUserList> data) {
		this.data = data;
	}
	
	
}
