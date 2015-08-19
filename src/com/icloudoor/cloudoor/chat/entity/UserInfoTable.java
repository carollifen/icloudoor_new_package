package com.icloudoor.cloudoor.chat.entity;

import com.icloudoor.cloudoor.db.Column;
import com.icloudoor.cloudoor.db.Table;

@Table(name = "userinfo")
public class UserInfoTable {
	@Column(name = "id")
	private int id;
	@Column(name = "userId")
	private String userId;
	@Column(name = "nickname")
	private String nickname;
	@Column(name = "portraitUrl")
	private String portraitUrl;
	@Column(name = "sortLetters")
	private String sortLetters;
	@Column(name = "provinceId", type = "INTEGER")
	private int provinceId;
	@Column(name = "districtId", type = "INTEGER")
	private int districtId;
	@Column(name = "cityId", type = "INTEGER")
	private int cityId;
	@Column(name = "sex", type = "INTEGER")
	private int sex;
	@Column(name = "type", type = "INTEGER")
	private int type;
	@Column(name = "myUserId")
	private String myUserId;
	
	

	public String getMyUserId() {
		return myUserId;
	}

	public void setMyUserId(String myUserId) {
		this.myUserId = myUserId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
