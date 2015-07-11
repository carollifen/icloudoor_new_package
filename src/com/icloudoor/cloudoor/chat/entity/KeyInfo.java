package com.icloudoor.cloudoor.chat.entity;

import java.io.Serializable;
import java.util.List;

public class KeyInfo implements Serializable{

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

}
