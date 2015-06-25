package com.icloudoor.cloudoor.utli;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icloudoor.cloudoor.MyAreaDBHelper;

public class FindDBUtile {
	
	public final static String DATABASE_NAME = "area.db";
	public final static String TABLE_NAME = "tb_core_area";

	private static MyAreaDBHelper mAreaDBHelper;
	private static SQLiteDatabase mAreaDB;
	
	
	public static void initDataBase(Context context){
		if(mAreaDBHelper==null || mAreaDB==null){
			mAreaDBHelper = new MyAreaDBHelper(context, DATABASE_NAME, null, 1);
			mAreaDB = mAreaDBHelper.getWritableDatabase();
		}
	}
	
	
	public static String getProvinceName(Context context,int provinceId) {
		initDataBase(context);
		String provinceName = null;
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorP.moveToFirst()) {
			int provinceIndex = mCursorP.getColumnIndex("province_short_name");
			int provinceIdIndex = mCursorP.getColumnIndex("province_id");
			do{
				int tempPID = mCursorP.getInt(provinceIdIndex);
			    String tempPName = mCursorP.getString(provinceIndex);
				if(tempPID == provinceId){
					provinceName = tempPName;
					break;
				}		
			}while(mCursorP.moveToNext());		
		}
		mCursorP.close();
		return provinceName;
	}
	
	
	
	public static String getCityName(Context context,int cityId) {
		initDataBase(context);
		String cityName = null;
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorC.moveToFirst()) {
			int cityIndex = mCursorC.getColumnIndex("city_short_name");
			int cityIdIndex = mCursorC.getColumnIndex("city_id");
			do{
				int tempCID = mCursorC.getInt(cityIdIndex);
			    String tempCName = mCursorC.getString(cityIndex);
				if(tempCID == cityId){
					cityName = tempCName;
					break;
				}		
			}while(mCursorC.moveToNext());		
		}
		mCursorC.close();
		return cityName;
	}
	
	public static String getDistrictName(Context context,int districtId) {
		initDataBase(context);
		String districtName = null;
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorD.moveToFirst()) {
			int districtIndex = mCursorD.getColumnIndex("district_short_name");
			int districtIdIndex = mCursorD.getColumnIndex("district_id");
			do{
				int tempDID = mCursorD.getInt(districtIdIndex);
			    String tempDName = mCursorD.getString(districtIndex);
				if(tempDID == districtId){
					districtName = tempDName;
					break;
				}		
			}while(mCursorD.moveToNext());		
		}
		mCursorD.close();
		return districtName;
	}

}
