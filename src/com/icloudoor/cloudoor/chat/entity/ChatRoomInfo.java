package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class ChatRoomInfo {
	
	private int code;
	private String message;
	private List<ChatRoomList> data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ChatRoomList> getData() {
		return data;
	}
	public void setData(List<ChatRoomList> data) {
		this.data = data;
	}
	

	
}
