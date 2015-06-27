package com.icloudoor.cloudoor.chat.entity;

import java.io.Serializable;

public class SearchUserList implements Serializable{

	private String userId;
	private String nickname;
	private String portraitUrl;
//	private String mobile;
	private int cityId;
	private int districtId;
	private int provinceId;
	private int sex;
	private Boolean isFriend;
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
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public int getDistrictId() {
		return districtId;
	}
	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public Boolean getIsFriend() {
		return isFriend;
	}
	public void setIsFriend(Boolean isFriend) {
		this.isFriend = isFriend;
	}
	
	
	
	

}
