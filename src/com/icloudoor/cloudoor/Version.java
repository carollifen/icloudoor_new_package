package com.icloudoor.cloudoor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Version {
	
	private String TAG = this.getClass().getSimpleName();
	private Context context;
	
	public Version(Context context) {
		this.context = context;
	}
	
	public String getVersionName() {
		String versionName = null;
		try {
			if(context != null){
				PackageManager pm = context.getApplicationContext().getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
				if (pi != null) {
					versionName = pi.versionName == null ? "null" : pi.versionName;
				}
			}
			
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		return "android_" + versionName;
	}
	
	public String getDeviceId() {
		String Imei = null;
		if(context != null){
			TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Imei = telephonyManager.getDeviceId();
		}
		return Imei;
	}
}