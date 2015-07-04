package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class DynamicInfo {

	private String actId;
	private String userId;
	private String nickname;
	private String subject;
	private String content;
	private long createTime;
	private String portaitUrl;
	private List<String> photoUrls;
	private List<ThumberInfo> thumbers;
	
	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public List<String> getPhotoUrls() {
		return photoUrls;
	}
	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}
	public List<ThumberInfo> getThumbers() {
		return thumbers;
	}
	public void setThumbers(List<ThumberInfo> thumbers) {
		this.thumbers = thumbers;
	}
	public String getPortaitUrl() {
		return portaitUrl;
	}
	public void setPortaitUrl(String portaitUrl) {
		this.portaitUrl = portaitUrl;
	}
	
	

}
