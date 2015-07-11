package com.icloudoor.cloudoor.cache;

import android.content.Context;

import com.icloudoor.cloudoor.chat.entity.AuthKeyEn;

public class UserCacheWrapper {

	public static final String MEDICAL_RECORD_LIST = "persion"; 			

	
	public static AuthKeyEn getMedicalRecord(Context context) {
		AuthKeyEn authKeyEn = (AuthKeyEn) ACache.get(context).getAsObject(
				generateUserKey(MEDICAL_RECORD_LIST));
		return authKeyEn == null ? null : authKeyEn;
	}

	public static void setMedicalRecord(Context context, AuthKeyEn authKeyEn) {
		ACache.get(context).put(generateUserKey(MEDICAL_RECORD_LIST), authKeyEn);
	}

	
	public static String generateUserKey(String pref) {
		return pref + SharePrefUtil.getString("USERID");
	}

}
