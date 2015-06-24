package com.icloudoor.cloudoor.chat.entity;

import java.util.List;
import java.util.jar.JarOutputStream;

import com.google.gson.Gson;

public class MyFriendInfo {

	
	private List<MyFriendsEn> data;
	private String message;
	private String sid;
	private int code;
	
	public List<MyFriendsEn> getData() {
		return data;
	}
	public void setData(List<MyFriendsEn> data) {
		this.data = data;
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
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	

}
