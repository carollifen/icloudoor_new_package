package com.icloudoor.cloudoor.chat.entity;

import com.icloudoor.cloudoor.db.Column;
import com.icloudoor.cloudoor.db.Table;

@Table(name = "verificationFrients")  
public class VerificationFrientsList {
	
	
	@Column(name = "id")  
    private int id; 
  
    @Column(name = "nickname")  
	private String nickname;
    @Column(name = "portraitUrl")  
	private String portraitUrl;
    @Column(name = "status")  
	private String status;
    @Column(name = "userId")  
	private String userId;
    @Column(name = "invitationId")  
    private String invitationId;
    @Column(name = "comment")  
    private String comment;
    
    @Column(name = "myUserId")  
    private String myuserId;
    
	
	
	public String getMyuserId() {
		return myuserId;
	}
	public void setMyuserId(String myuserId) {
		this.myuserId = myuserId;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getInvitationId() {
		return invitationId;
	}
	public void setInvitationId(String invitationId) {
		this.invitationId = invitationId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

	
}
