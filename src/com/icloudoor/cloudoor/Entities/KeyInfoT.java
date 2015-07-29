package com.icloudoor.cloudoor.Entities;

import java.io.Serializable;
import java.util.List;

import com.icloudoor.cloudoor.chat.entity.Key;

public class KeyInfoT implements Serializable{

	private String l1ZoneId;
	private String zoneUserId;
	private List<Key> keys;

	public String getL1ZoneId() {
		return l1ZoneId;
	}

	public void setL1ZoneId(String l1ZoneId) {
		this.l1ZoneId = l1ZoneId;
	}

	public String getZoneUserId() {
		return zoneUserId;
	}

	public void setZoneUserId(String zoneUserId) {
		this.zoneUserId = zoneUserId;
	}

	public List<Key> getKeys() {
		return keys;
	}

	public void setKeys(List<Key> keys) {
		this.keys = keys;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String address;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
