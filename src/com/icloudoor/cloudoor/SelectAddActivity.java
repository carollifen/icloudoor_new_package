package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class SelectAddActivity extends BaseActivity implements OnWheelChangedListener {
	
	private String HOST = UrlUtils.HOST;
	private RequestQueue mQueue;
	private String sid;
	private URL setInfoURL;
	
	private TextView btn_cancel, btn_ok;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
	
	private String[] provinceSet;
	private String[][] citySet;
	private String[][][] districtSet;

	private int maxPlength;
	private int maxClength;
	private int maxDlength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_add);
		
		
		mAreaDBHelper = new MyAreaDBHelper(SelectAddActivity.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();
		
		initSpinnerData();
		
		setUpViews();
		setUpListener();
		setUpData();
		
		final int height = findViewById(R.id.content).getTop();
		View mainView = findViewById(R.id.main);
		mainView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y > height) {
						finish();
					}
				}
				return true;
			}
			
		});
	}
	
	public void initSpinnerData() {
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIdIndex = mCursorP.getColumnIndex("province_id");
		int cityIdIndex = mCursorP.getColumnIndex("city_id");
		int districtIdIndex = mCursorP.getColumnIndex("district_id");
		maxPlength = 1;
		maxClength = 1;
		maxDlength = 1;

		if (mCursorP.moveToFirst()) {
			int tempPId = mCursorP.getInt(provinceIdIndex);
			while(mCursorP.moveToNext()){
				if (mCursorP.getInt(provinceIdIndex) != tempPId) {
					tempPId = mCursorP.getInt(provinceIdIndex);
					maxPlength++;
				}
			}
			mCursorP.close();
		}

		if(mCursorC.moveToFirst()){
			int tempCcount = 1;
			int tempPId = mCursorC.getInt(provinceIdIndex);
			int tempCId = mCursorC.getInt(cityIdIndex);
			while (mCursorC.moveToNext()) {
				if(mCursorC.getInt(provinceIdIndex) == tempPId && mCursorC.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorC.getInt(cityIdIndex);
					tempCcount++;
				}else if(mCursorC.getInt(provinceIdIndex) != tempPId && mCursorC.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorC.getInt(provinceIdIndex);
					tempCId = mCursorC.getInt(cityIdIndex);
					if(tempCcount > maxClength) {
						maxClength = tempCcount;
					}
					tempCcount = 1;
				}
			}
			mCursorC.close();
		}

		if(mCursorD.moveToFirst()){
			int tempDcount = 1;
			int tempPId = mCursorD.getInt(provinceIdIndex);
			int tempCId = mCursorD.getInt(cityIdIndex);
			while (mCursorD.moveToNext()) {
				if(mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) == tempCId){
					tempDcount++;
				}else if(mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}else if(mCursorD.getInt(provinceIdIndex) != tempPId && mCursorD.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorD.getInt(provinceIdIndex);
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}
			}
			mCursorD.close();
		}

		provinceSet = new String[maxPlength];
		citySet = new String[maxPlength][maxClength];
		districtSet = new String[maxPlength][maxClength][maxDlength];
		int a = 0, b = 0, c = 0;
		Cursor mCursor = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIndex = mCursor.getColumnIndex("province_short_name");
		int cityIndex = mCursor.getColumnIndex("city_short_name");
		int disdrictIndex = mCursor.getColumnIndex("district_short_name");
		if(mCursor.moveToFirst()){
			provinceSet[a] = mCursor.getString(provinceIndex);
			citySet[a][b] = mCursor.getString(cityIndex);
			districtSet[a][b][c] = mCursor.getString(disdrictIndex);

			while(mCursor.moveToNext()){
				if(mCursor.getString(provinceIndex).equals(provinceSet[a])){
					if(mCursor.getString(cityIndex).equals(citySet[a][b])){
						c++;
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}else{
						c = 0;
						b++;
						citySet[a][b] = mCursor.getString(cityIndex);
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}
				}else{
					b = 0;
					c = 0;
					a++;
					provinceSet[a] = mCursor.getString(provinceIndex);
					citySet[a][b] = mCursor.getString(cityIndex);
					districtSet[a][b][c] = mCursor.getString(disdrictIndex);
				}
			}
		}
		mCursor.close();
	}
	
	private void setUpViews() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
		
		btn_cancel = (TextView) findViewById(R.id.button_cancel);
		btn_ok = (TextView) findViewById(R.id.button_ok);
		
		btn_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
			
		});
		
		btn_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("provinceId", String.valueOf(getProvinceID(provinceSet[mViewProvince.getCurrentItem()])));
				map.put("cityId", String.valueOf(getCityID(citySet[mViewProvince.getCurrentItem()][mViewCity.getCurrentItem()])));
				map.put("districtId", String.valueOf(getDistrictID(districtSet[mViewProvince.getCurrentItem()][mViewCity.getCurrentItem()][mViewDistrict.getCurrentItem()])));
				updateProfile(map);
				
				finish();
			}
			
		});
	}
	
	private void setUpListener() {
    	mViewProvince.addChangingListener(this);
    	mViewCity.addChangingListener(this);
    	mViewDistrict.addChangingListener(this);
    }
	
	private void setUpData() {
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(SelectAddActivity.this, provinceSet));
		mViewProvince.setCurrentItem(provinceSet.length/2);

		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCity(mViewCity, citySet, mViewProvince.getCurrentItem());
		updatecDistrict(mViewDistrict, districtSet, mViewProvince.getCurrentItem(), mViewCity.getCurrentItem());
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCity(mViewCity, citySet, newValue);
		} else if (wheel == mViewCity) {
			updatecDistrict(mViewDistrict, districtSet, mViewProvince.getCurrentItem(), newValue); 
		} else if (wheel == mViewDistrict) {

		}
	}

	private void updatecDistrict(WheelView city, String ccities[][][], int index,int index2) {
    	
    	int len = 0;
    	for(int i = 0; i < ccities[index][index2].length; i++){
    		if(ccities[index][index2][i] != null) len++;
    	}
    	
    	String[] newDistrict = new String[len];
    	for(int i = 0; i < len; i++){
    		if(ccities[index][index2][i].length() > 0) newDistrict[i] = ccities[index][index2][i];
    	}
    	
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, newDistrict);
        city.setViewAdapter(adapter);
        city.setCurrentItem(newDistrict.length / 2);     
    }

	private void updateCity(WheelView city, String cities[][], int index) {
    	
    	int len = 0;
    	for(int i = 0; i < cities[index].length; i++){
    		if(cities[index][i] != null ) len++;
    	}
    	
    	String[] newCity = new String[len];
    	for(int i = 0; i < len; i++){
    		if(cities[index][i].length() > 0) newCity[i] = cities[index][i];
    	}
    	
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, newCity);
        city.setViewAdapter(adapter);
        city.setCurrentItem(newCity.length / 2);    
    }
	
	private void updateProfile (final Map<String, String> profileMap) {
		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			setInfoURL = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
				Method.POST, setInfoURL.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						
						try {
							if(response.getInt("code") == 1) {

								if(!response.getString("sid").equals(null)) 
									saveSid(response.getString("sid"));
	
								SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								Editor editor = loginStatus.edit();
								if(profileMap.containsKey("provinceId")) {
									editor.putInt("PROVINCE", Integer.parseInt(profileMap.get("provinceId")));
									editor.putInt("CITY", Integer.parseInt(profileMap.get("cityId")));
									editor.putInt("DIS", Integer.parseInt(profileMap.get("districtId")));
									editor.commit();
								} 

							} else if (response.getInt("code") == -1) {
								Toast.makeText(getApplicationContext(), R.string.not_enough_params, Toast.LENGTH_SHORT).show();
							} else if (response.getInt("code") == -2) {
								Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
							} else if (response.getInt("code") == -99) {
								Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
							} else if (response.getInt("code") == -42) {
								Toast.makeText(getApplicationContext(), R.string.nick_name_already, Toast.LENGTH_SHORT).show();
							}
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {

			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map = profileMap;
				return map;
			}
		};

		mQueue.add(mJsonRequest);

	}
}
