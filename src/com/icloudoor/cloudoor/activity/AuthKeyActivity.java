package com.icloudoor.cloudoor.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;

public class AuthKeyActivity extends BaseActivity implements OnClickListener{
	
	TextView start_time;
	TextView end_time;
	TextView zone_name;
	TextView key_name;
	EditText number_edit;
	ImageView call_contacts;
	ImageView btn_back;
	LinearLayout auth_key_layout;
	Key key;
	String zonename;
	String zoneid;
	boolean isCarDoor;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_auth_key);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		zone_name = (TextView) findViewById(R.id.zone_name);
		key_name = (TextView) findViewById(R.id.key_name);
		auth_key_layout = (LinearLayout) findViewById(R.id.auth_key_layout);
		call_contacts = (ImageView) findViewById(R.id.call_contacts);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		call_contacts.setOnClickListener(this);
		auth_key_layout.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		number_edit = (EditText) findViewById(R.id.number_edit);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String startTime = format.format(new Date(System.currentTimeMillis()));
		start_time.setText(startTime);
		key = (Key) getIntent().getSerializableExtra("key");
		zonename = getIntent().getStringExtra("zonename");
		zoneid = getIntent().getStringExtra("zoneid");
		isCarDoor = getIntent().getBooleanExtra("isCarDoor", true);
		if(isCarDoor){
			end_time.setText(R.string.notTime);
		}else{
			startTime = start_time.getText().toString();
			String endTime = startTime.split(" ")[0]+" 23:59";
			end_time.setText(endTime);
		}
		zone_name.setText(zonename);
		key_name.setText(key.getName());
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {
				ContentResolver reContentResolverol = getContentResolver();
				Uri contactData = data.getData();
				
				@SuppressWarnings("deprecation")
				Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
				cursor.moveToFirst();
				String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                cursor.close();
				Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				
				if(phone.moveToFirst()){
					for (;!phone.isAfterLast();phone.moveToNext()) { 
						int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); 
                        int typeindex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE); 
                        int phone_type = phone.getInt(typeindex);
                        String phoneNumber = phone.getString(index);
                        switch(phone_type) 
                        { 
                            case 2: 
                            	String usernumber = phoneNumber.replace(" ", "").replace("-", "").replace("+86", ""); 
                            	number_edit.setText(usernumber);
                            break; 
                        } 
					}
					if (!phone.isClosed()) 
                    { 
                           phone.close(); 
                    } 
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.call_contacts:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, 1);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.auth_key_layout:
			String phone = number_edit.getText().toString().trim();
			if(TextUtils.isEmpty(phone)){
				showToast(R.string.Please_choose_contact);
			}else{
				if (isCarDoor) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("zoneUserId", zoneid);
					map.put("plateNum", key.getName());
					map.put("toMobile", phone);
					map.put("carPosStatus", key.getAuthStatus());
//					map.put("authFrom", start_time.getText().toString());
					authTempCar(map);
				}else{
					Map<String, String> map = new HashMap<String, String>();
					map.put("zoneUserId", zoneid);
					map.put("toMobile", phone);
					map.put("authDate", start_time.getText().toString().split(" ")[0].replaceAll("/", "-"));
//					map.put("authFrom", start_time.getText().toString());
//					map.put("authTo", end_time.getText().toString());
					authTempNormal(map);
				}
			}
			
			break;

		default:
			break;
		}
	}
	
	public void authTempCar(Map<String, String> map){
		getMyJsonObjectRequest(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					int code = response.getInt("code");
					switch (code) {
					case 1:
						
						showToast(R.string.auth_key_success);
						break;
					case -100:
						showToast(R.string.auth_key_car_Fail0);
						break;
					case -101:
						showToast(R.string.auth_key_car_Fail1);
						break;
					case -102:
						showToast(R.string.auth_key_car_Fail2);
						break;
					case -103:
						showToast(R.string.auth_key_car_Fail3);
						break;
					case -104:
						showToast(R.string.auth_key_car_Fail4);
						break;
					case -105:
						showToast(R.string.auth_key_car_Fail5);
						break;
					case -106:
						showToast(R.string.auth_key_car_Fail6);
						break;
					case -108:
						showToast(R.string.auth_key_car_Fail7);
						break;

					default:
						showToast(R.string.auth_key_Fail);
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				showToast(R.string.network_error);
			}
		}, "/user/api/authTempCar.do", map, true);
	}
	
	
	
	
	
	
	
//	/user/api/authTempNormal.do
	public void authTempNormal(Map<String, String> map){
		getMyJsonObjectRequest(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
//				{"message":"successful","sid":"0609415359714fe7a512c33df60db485","code":1}
				try {
					int code = response.getInt("code");
					if(code==1){
						showToast(R.string.auth_key_success);
						
					}else if(response.getInt("code") == -101){
						showToast(R.string.user_not_regis);
					}else if(response.getInt("code") == -102){
						showToast(R.string.lend_count_too_more);
					}else if(response.getInt("code") == -103){
						showToast(R.string.already_have_the_temp_key);
					}else if(response.getInt("code") == -104){
						showToast(R.string.cannot_auth_to_self);
					}else if(response.getInt("code") == -106){
						showToast( R.string.borrow_count_too_more);
					}else{
						showToast(R.string.auth_key_Fail);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			
			@Override
			public void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				showToast(R.string.network_error);
			}
		}, "/user/api/authTempNormal.do", map, true);
	}
	
	

}
