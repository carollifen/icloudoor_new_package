package com.icloudoor.cloudoor.utli;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.MyDataBaseHelper;
import com.icloudoor.cloudoor.MyDebugLog;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.ReportToRepairActivity;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Version;
import com.icloudoor.cloudoor.widget.UserStatusDialog;

public class KeyHelper {

	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private RequestQueue mQueue;
	private Version version;
	Context context;
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private int newNum;
	private SharedPreferences carNumAndPhoneNumShare;
	public static KeyHelper helper;
	
	 public static KeyHelper getInstance(Context context){
	        if(helper==null){
	            synchronized(KeyHelper.class){
	                if(helper==null){
	                	helper=new KeyHelper(context);
	                }
	            }
	        }
	        return helper;
	    }
	
	public KeyHelper(Context context) {
		this.context = context;
		mQueue = Volley.newRequestQueue(context);
		version = new Version(context);
		mKeyDBHelper = new MyDataBaseHelper(context, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		carNumAndPhoneNumShare = context.getSharedPreferences("carNumAndPhoneNum", 0);
	}

	public String loadSid() {
		SharedPreferences loadSid = context.getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	public String loadUUID() {
		SharedPreferences loadUUID = context.getSharedPreferences("SAVEDUUID",
				0);
		return loadUUID.getString("UUID", null);
	}

	public void downLoadKey2() {
		String downLoadKeyURL = UrlUtils.HOST + "/user/door/download2.do"
				+ "?sid=" + loadSid() + "&ver=" + version.getVersionName()
				+ "&imei=" + version.getDeviceId();

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								new Thread(new DownloadeKeyTask(response))
										.start();
							} else if (response.getInt("code") == -81) {
								Toast.makeText(context, R.string.have_no_key_authorised, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("appId", loadUUID());
				return map;
			}
		};
		mQueue.add(mJsonRequest);
	}
	
	
	public void parseKeyData(JSONObject response) throws JSONException {
		MyDebugLog.e("test for new interface", "parseKeyData func");
		// for new key download interface
		JSONObject data = response.getJSONObject("data");
		JSONArray doorAuths = data.getJSONArray("doorAuths");
		JSONArray zones = data.getJSONArray("zones");
		JSONArray cars = data.getJSONArray("cars");
		
		// for doorauths table -- START
		for (int index = 0; index < doorAuths.length(); index++) {
			JSONObject doorData = (JSONObject) doorAuths.get(index);
			
			ContentValues value = new ContentValues();
			
			if(doorData.getString("deviceId").length() > 0){
				if(!hasData(mKeyDB, doorData.getString("deviceId").toUpperCase())){    //insert the new key
					
					newNum++;
					
					value.put("zoneId", doorData.getString("zoneId"));
					value.put("doorName", doorData.getString("doorName"));
					value.put("doorId", doorData.getString("doorId"));
					value.put("deviceId", doorData.getString("deviceId").toUpperCase());
					value.put("doorType", doorData.getString("doorType"));
					value.put("authFrom", doorData.getString("authFrom"));
					value.put("authTo", doorData.getString("authTo"));
					
					if (!doorData.getString("doorType").equals("2")) {
						value.put("direction", "none");
						value.put("plateNum", "none");
						mKeyDB.insert(TABLE_NAME, null, value);
					} else if (doorData.getString("doorType").equals("2")){
								value.put("plateNum", doorData.getString("plateNum"));
								value.put("direction", doorData.getString("direction"));
								mKeyDB.insert(TABLE_NAME, null, value);
					}
				} else {            // update the old key status
						ContentValues valueTemp = new ContentValues();
						valueTemp.put("doorName", doorData.getString("doorName"));
						valueTemp.put("authFrom", doorData.getString("authFrom"));
						valueTemp.put("authTo", doorData.getString("authTo"));
						
						mKeyDB.update("KeyInfoTable", valueTemp, "deviceId = ?", new String[] {doorData.getString("deviceId")});
				}
			}	
		}
		
		//need to delete the old key
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey;
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					
					do{
						keepKey = false;
						String deviceId = mCursor.getString(deviceIdIndex).toUpperCase();
						for (int index = 0; index < doorAuths.length(); index++) {
							JSONObject doorData = (JSONObject) doorAuths.get(index);
							
							if(doorData.getString("deviceId").length() > 0){
								if((doorData.getString("deviceId")).toUpperCase().equals(deviceId)){
									keepKey = true;
									break;
								}
							}
						}	
						
						if(!keepKey){
							//delete in the table
							mKeyDB.delete("KeyInfoTable", "deviceId = ?", new String[] {deviceId});
						}
						
					}while(mCursor.moveToNext());				
				}	
				mCursor.close();
			}
		}
		// for doorauths table -- END
		
		// for zones table -- START
		for (int index = 0; index < zones.length(); index++) {
			JSONObject zoneData = (JSONObject) zones.get(index);
			ContentValues value = new ContentValues();
			
			if(zoneData.getString("zoneId").length() > 0){
				if(!hasZoneData(mKeyDB, zoneData.getString("zoneId"))){   // insert new
					value.put("zoneid", zoneData.getString("zoneId"));		
					value.put("zonename", zoneData.getString("zoneName"));
					value.put("parentzoneid", zoneData.getString("parentZoneId"));
					mKeyDB.insert(ZONE_TABLE_NAME, null, value);
				}
			}
		}
		
		// delete old
		if (mKeyDBHelper.tabIsExist(ZONE_TABLE_NAME)) {
			if (DBCountZone() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey;
					int zoneidIndex = mCursor.getColumnIndex("zoneid");
					
					do{
						keepKey = false;
						String zoneid = mCursor.getString(zoneidIndex);
						for (int index = 0; index < zones.length(); index++) {
							JSONObject zoneData = (JSONObject) zones.get(index);
							
							if(zoneData.getString("zoneId").length() > 0){
								if(zoneData.getString("zoneId").equals(zoneid)){
									keepKey = true;
									break;
								}
							}
						}		
						
						if(!keepKey){
							mKeyDB.delete("ZoneTable", "zoneId = ?", new String[] {zoneid});
						}
					}while(mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
		// for zones table -- END
		
		// for cars table -- START
		for (int index = 0; index < cars.length(); index++) {
			JSONObject carData = (JSONObject) cars.get(index);
			
			if(carData.getString("l1ZoneId").length() > 0){
				if(!hasCarData(mKeyDB, carData.getString("l1ZoneId"), carData.getString("plateNum"))){   // insert new
					ContentValues value = new ContentValues();
					value.put("l1ZoneId", carData.getString("l1ZoneId"));
					value.put("plateNum", carData.getString("plateNum"));
					value.put("carStatus", carData.getString("carStatus"));
					value.put("carPosStatus", carData.getString("carPosStatus"));
					mKeyDB.insert(CAR_TABLE_NAME, null, value);
					
					// refresh the carPosStatus to "0" if carPosStatus not "0" and you hava a car key (own or borrowed) when you get the new car key
//					if(!carData.getString("carPosStatus").equals("0") && !carData.getString("carStatus").equals("3")){
//						updatePosStatus(carData.getString("l1ZoneId"), carData.getString("plateNum"));
//					}
				}else{   // update old
					ContentValues value = new ContentValues();
					
					if (carData.getString("plateNum").equals(
							carNumAndPhoneNumShare.getString("CARNUM", null))){
						value.put("carStatus", carData.getString("carStatus"));
						value.put("carPosStatus", carData.getString("carPosStatus"));
						
						mKeyDB.update("CarKeyTable", value, "l1ZoneId = ? and plateNum = ?", new String[] {carData.getString("l1ZoneId"), carData.getString("plateNum")});
					}
				}
			}
		}
		
		// delete old
		
			if (mKeyDBHelper.tabIsExist("CarKeyTable")) {
				if(DBCountCar() > 0){
					Cursor mCursor = mKeyDB.rawQuery("select * from " + "CarKeyTable", null);
					if(mCursor.moveToFirst()){
						boolean keepKey;
						int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
						int plateNumIndex = mCursor.getColumnIndex("plateNum");

						do{
							keepKey = false;
							String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
							String plateNum = mCursor.getString(plateNumIndex);
							for (int index = 0; index < cars.length(); index++) {
								JSONObject carData = (JSONObject) cars.get(index);
								
								if(carData.getString("l1ZoneId").length() > 0){
									if(carData.getString("l1ZoneId").equals(l1ZoneId) && carData.getString("plateNum").equals(plateNum)){
										keepKey = true;
										break;
									}
								}
							}
							if(!keepKey){
								mKeyDB.delete("CarKeyTable", "l1ZoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
								mKeyDB.delete("KeyInfoTable", "zoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
							}
						}while(mCursor.moveToNext());
					}
					mCursor.close();
				}
			}
		// for cars table -- END
			
		if (newNum > 0) {
				SharedPreferences saveNewKeyState = context.getSharedPreferences("SAVESIGN", Context.MODE_PRIVATE);
				Editor editor = saveNewKeyState.edit();
				editor.putString("newKeyState", "true");
				editor.commit();
				
		}
	}
	
	
	private boolean hasCarData(SQLiteDatabase mDB, String str, String str1){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
			int plateNumIndex = mCursor.getColumnIndex("plateNum");
			do{
				 String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
				 String plateNum = mCursor.getString(plateNumIndex);
				 
				 if(l1ZoneId.equals(str) && plateNum.equals(str1)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,null);
		
		if(mCursor.moveToFirst()){
			int deviceIdIndex = mCursor.getColumnIndex("deviceId");
			do{
				 String deviceId = mCursor.getString(deviceIdIndex);
				 
				 if(deviceId.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasZoneData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int zoneidIndex = mCursor.getColumnIndex("zoneid");
			do{
				 String zoneid = mCursor.getString(zoneidIndex);
				 
				 if(zoneid.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountZone() {  
	    String sql = "SELECT COUNT(*) FROM " + ZONE_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountCar() {  
	    String sql = "SELECT COUNT(*) FROM " + CAR_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	public class DownloadeKeyTask implements Runnable{

		private JSONObject response = null;

		public DownloadeKeyTask (JSONObject data) {
			this.response = data;
		}
		public void run(){

			try {
				parseKeyData(response);
			}catch (JSONException e){
				Log.e("error", "There is a error");
			}

//			
		}

	}
	
	public void saveSid(String sid) {
        SharedPreferences savedSid = context.getSharedPreferences("SAVEDSID", 0);
        Editor editor = savedSid.edit();
        editor.putString("SID", sid);
        editor.commit();
	}
	
	public void checkForUserStatus() {
		URL getUserStatusURL = null;
		try {
			getUserStatusURL = new URL(UrlUtils.HOST + "/user/manage/getProfile.do" + "?sid=" + loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, getUserStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if(response.getInt("code") == 1){
								if(response.getString("sid") != null)
									saveSid(response.getString("sid"));
									SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS", 0);
									Editor editor = loginStatus.edit();
									editor.putInt("STATUS", response.getJSONObject("data").getInt("userStatus"));
									editor.putBoolean( "isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									editor.putString( "l1Zones", response.getJSONObject("data").getString("l1Zones"));
									editor.commit();

									SharedPreferences saveProfile = context.getSharedPreferences("PROFILE", 0);
									Editor edit = saveProfile.edit();
									edit.putInt("userStatus", response.getJSONObject("data").getInt("userStatus"));
									edit.putBoolean("isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									edit.commit();
									int status = response.getJSONObject("data").getInt("userStatus");
//									if(status!=2){
//										UserStatusDialog statusDialog = new UserStatusDialog(getActivity());
//										statusDialog.show();
//										statusDialog.setOKOnClickListener(new OnClickListener() {
//											
//											@Override
//											public void onClick(View v) {
//												// TODO Auto-generated method stub
//												Intent intent = new Intent(getActivity(),ReportToRepairActivity.class);
//												intent.putExtra("webUrl", "/user/auth/request.do");
//												startActivity(intent);
//											}
//										});
//									}
										
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		mQueue.add(mJsonRequest);
	}

}
