package com.icloudoor.cloudoor.chat.entity;

import java.util.List;

public class CMDData {
	private String invitationId;
	private String userId;
	private String nickname;
	private String portraitUrl;
	private String comment;
	private List<String> set;

	public String getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(String invitationId) {
		this.invitationId = invitationId;
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

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getSet() {
		return set;
	}

	public void setSet(List<String> set) {
		this.set = set;
	}

}
