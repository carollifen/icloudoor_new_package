package com.icloudoor.cloudoor.Entities;

import java.util.List;

import com.icloudoor.cloudoor.chat.entity.KeyInfo;

public class RecordsEn {
	
	private List<KeyInfo> records;
	private String date;
	public List<KeyInfo> getRecords() {
		return records;
	}
	public void setRecords(List<KeyInfo> records) {
		this.records = records;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
