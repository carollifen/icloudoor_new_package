package com.icloudoor.cloudoor.chat.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.cache.UserCacheWrapper;
import com.icloudoor.cloudoor.chat.entity.AuthKeyEn;
import com.icloudoor.cloudoor.chat.entity.FamilyAddr;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class AuthKeyActivity extends BaseActivity implements OnClickListener , NetworkInterface{
	
	TextView start_time;
	TextView end_time;
	LinearLayout auth_key_layout;
	ExpandableListView listView;
	String userid;
	List<KeyInfo> data;
	Myadapter myadapter;
	ImageView btn_back;
	boolean isCarDoor;
	boolean isFlage = true;;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_authkey);
		userid = getIntent().getStringExtra("userid");
		auth_key_layout = (LinearLayout) findViewById(R.id.auth_key_layout);
		auth_key_layout.setOnClickListener(this);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		listView = (ExpandableListView) findViewById(R.id.listView);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String startTime = format.format(new Date(System.currentTimeMillis()));
		start_time.setText(startTime);
		JSONObject parm = new JSONObject();
		try {
			parm.put("trgUserId", userid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getNetworkData(this, "/user/api/getFamilyAddr.do", parm.toString(), true);
		getMyKey();
	}
	
	
	public void getMyKey(){
		AuthKeyEn keyEn = UserCacheWrapper.getMedicalRecord(this);
		if(keyEn!=null){
			if(keyEn.getCode().equals("1")){
				data = keyEn.getData();
				if(data==null|| data.size()==0){
					
				}else{
					myadapter = new Myadapter(data);
					listView.setAdapter(myadapter);
					for (int i = 0; i < myadapter.getGroupCount(); i++) {
						listView.expandGroup(i);
					}
				}
			}else{
				getNetworkData(new NetworkInterface() {
					
					@Override
					public void onSuccess(JSONObject response) {
						// TODO Auto-generated method stub
						AuthKeyEn keyEn = GsonUtli.jsonToObject(response.toString(), AuthKeyEn.class);
						if(keyEn!=null){
							data = keyEn.getData();
							if(data==null|| data.size()==0){
								
							}else{
								myadapter = new Myadapter(data);
								listView.setAdapter(myadapter);
								for (int i = 0; i < myadapter.getGroupCount(); i++) {
									listView.expandGroup(i);
								}
							}
						}
					}
					
					@Override
					public void onFailure(VolleyError error) {
						// TODO Auto-generated method stub
						
					}
				}, "/user/api/keys/my.do", "{}", true);
			}
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.auth_key_layout:
			List<List<Boolean>> chekbleData = myadapter.getChekbleData();
			for (int i = 0; i < chekbleData.size(); i++) {
				for (int j = 0; j < chekbleData.get(i).size(); j++) {
					if(chekbleData.get(i).get(j)){
						KeyInfo keyInfo = data.get(i);
						List<Key> keys = keyInfo.getKeys();
						Key key = keys.get(j);
						isFlage = false;
						if(isCarDoor){
							Map<String, String> map = new HashMap<String, String>();
							map.put("zoneUserId", keyInfo.getZoneUserId());
							map.put("plateNum", key.getName());
							map.put("toUserId", userid);
							map.put("carPosStatus", key.getAuthStatus());
							map.put("authFrom", start_time.getText().toString());
							authTempCar(map);
						}else{
							Map<String, String> map = new HashMap<String, String>();
							map.put("zoneUserId", keyInfo.getZoneUserId());
							map.put("toUserId", userid);
							map.put("authDate", start_time.getText().toString().split(" ")[0].replaceAll("/", "-"));
							map.put("authFrom", start_time.getText().toString());
							map.put("authTo", end_time.getText().toString());
							authTempNormal(map);
						}
						break;
					}
				}
			}
			if(isFlage){
				showToast(R.string.plass_door);
			}
			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}
	
//	/user/api/authTempCar.do
	public void authTempCar(Map<String, String> map){
		getMyJsonObjectRequest(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					int code = response.getInt("code");
					switch (code) {
					case 1:
						List<List<Boolean>> chekbleData = myadapter.getChekbleData();
						for (int i = 0; i < chekbleData.size(); i++) {
							for (int j = 0; j < chekbleData.get(i).size(); j++) {
								if(chekbleData.get(i).get(j)){
									KeyInfo keyInfo = data.get(i);
									List<Key> keys = keyInfo.getKeys();
									Key key = keys.get(j);
									Intent chatIntent = new Intent();
									chatIntent.putExtra("zoneName", keyInfo.getAddress());
									chatIntent.putExtra("zoneType", getString(R.string.doorType2));
									setResult(Activity.RESULT_OK, chatIntent);  
					                finish();  
									break;
								}
							}
						}
						
						showToast(R.string.auth_key_success);
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
						List<List<Boolean>> chekbleData = myadapter.getChekbleData();
						for (int i = 0; i < chekbleData.size(); i++) {
							for (int j = 0; j < chekbleData.get(i).size(); j++) {
								if(chekbleData.get(i).get(j)){
									KeyInfo keyInfo = data.get(i);
									List<Key> keys = keyInfo.getKeys();
									Key key = keys.get(j);
									
									Intent chatIntent = new Intent();
									chatIntent.putExtra("zoneName", keyInfo.getAddress());
									chatIntent.putExtra("zoneType", getString(R.string.doorType1));
									setResult(Activity.RESULT_OK, chatIntent);  
					                finish();  
									
									break;
								}
							}
						}
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
	
	List<String> addrAata;
	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		FamilyAddr familyAddr = GsonUtli.jsonToObject(response.toString(), FamilyAddr.class);
		if(familyAddr!=null){
			if(familyAddr.getCode()==1){
				addrAata = familyAddr.getData();
			}else{
				showToast(R.string.network_error);
			}
		}else{
			showToast(R.string.network_error);
		}
		
	}
	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	class Myadapter extends BaseExpandableListAdapter{

		
		List<KeyInfo> data;
		List<List<Boolean>> isChekble;
		public Myadapter(List<KeyInfo> data) {
			// TODO Auto-generated constructor stub
			this.data = data;
			isChekble = new ArrayList<List<Boolean>>();
			for (int i = 0; i < data.size(); i++) {
				List<Boolean> isChildren = new LinkedList<Boolean>();
				for (int j = 0; j < data.get(i).getKeys().size(); j++) {
//					if(i==0&&j==0){
//						isChildren.add(true);
//					}else{
						isChildren.add(false);
//					}
				}
				isChekble.add(isChildren);
			}
		}
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return data.get(groupPosition).getKeys().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return data.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return data.get(groupPosition).getKeys().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public List<List<Boolean>> getChekbleData(){
			
			
			return isChekble;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			KeyInfo info = data.get(groupPosition);
			convertView = LayoutInflater.from(AuthKeyActivity.this).inflate(R.layout.key_groupview, null);
			TextView zone_name = (TextView) convertView.findViewById(R.id.zone_name);
			zone_name.setText(info.getAddress());
			convertView.setEnabled(false);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(AuthKeyActivity.this).inflate(R.layout.key_childview, null);
			TextView key_name = (TextView) convertView.findViewById(R.id.key_name);
			TextView state_tx = (TextView) convertView.findViewById(R.id.state_tx);
			ImageView auth_key_img = (ImageView) convertView.findViewById(R.id.auth_key_img);
			View divider_view = convertView.findViewById(R.id.divider_view);
			if((childPosition+1)==data.get(groupPosition).getKeys().size()){
				divider_view.setVisibility(View.GONE);
			}else{
				divider_view.setVisibility(View.VISIBLE);
			}
			if(isChekble.get(groupPosition).get(childPosition)){
				auth_key_img.setImageResource(R.drawable.key_box_p);
			}else{
				auth_key_img.setImageResource(R.drawable.key_box_n);
			}
			Key keys = data.get(groupPosition).getKeys().get(childPosition);
			final String l1ZoneId = data.get(groupPosition).getZoneUserId();
			String authStatus = keys.getAuthStatus();
			final String doorType = keys.getDoorType();
			
			if(doorType.trim().equals("2")){
				if(authStatus.trim().equals("2")){
					state_tx.setVisibility(View.VISIBLE);
					auth_key_img.setVisibility(View.GONE);
				}else{
					state_tx.setVisibility(View.GONE);
					auth_key_img.setVisibility(View.VISIBLE);
				}
			}
			key_name.setText(keys.getName());
			
			auth_key_img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(doorType.trim().equals("2")){
						if(addrAata==null||addrAata.size()==0){
							
						}else{
							for (int k = 0; k < addrAata.size(); k++) {
								if(l1ZoneId.equals(addrAata.get(k))){
									for (int i = 0; i < isChekble.size(); i++) {
										for (int j = 0; j < isChekble.get(i).size(); j++) {
											isChekble.get(i).set(j, false);
										}
									}
									isChekble.get(groupPosition).set(childPosition, true);
									end_time.setText(R.string.notTime);
									isCarDoor = true;
									notifyDataSetChanged();
								}
							}
							
							
						}
					}else{
						for (int i = 0; i < isChekble.size(); i++) {
							for (int j = 0; j < isChekble.get(i).size(); j++) {
								isChekble.get(i).set(j, false);
							}
						}
						isCarDoor = false;
						isChekble.get(groupPosition).set(childPosition, true);
						String startTime = start_time.getText().toString();
						String endTime = startTime.split(" ")[0]+" 23:59";
						end_time.setText(endTime);
						notifyDataSetChanged();
					}
					
					
				}
			});
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	

}
