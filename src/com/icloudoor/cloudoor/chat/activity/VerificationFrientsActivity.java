package com.icloudoor.cloudoor.chat.activity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.MsgFragment;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.VerificationFrientsAdapter;
import com.icloudoor.cloudoor.chat.entity.SearchUserInfo;
import com.icloudoor.cloudoor.chat.entity.SearchUserList;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.VFDaoImpl;
import com.icloudoor.cloudoor.widget.InvitationFriendDialog;
import com.umeng.analytics.MobclickAgent;

public class VerificationFrientsActivity extends BaseActivity implements
		OnClickListener, NetworkInterface {

	private ListView vf_listView;
	private ImageView btn_back;
	private LinearLayout search_layout;
	private RelativeLayout addphoneContact_layout;
	private VerificationFrientsAdapter adapter;
	private String usernumber;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_verificationfrients);
		vf_listView = (ListView) findViewById(R.id.vf_listView);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		search_layout = (LinearLayout) findViewById(R.id.search_layout);
		addphoneContact_layout = (RelativeLayout) findViewById(R.id.addphoneContact_layout);
		adapter = new VerificationFrientsAdapter(this);
		vf_listView.setAdapter(adapter);
		search_layout.setOnClickListener(this);
		addphoneContact_layout.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		VFDaoImpl daoImpl = new VFDaoImpl(this);
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS",Context.MODE_PRIVATE);
		List<VerificationFrientsList> data = daoImpl.find(null, "myUserId = ?", new String[]{loginStatus.getString("USERID", "")}, null, null, null, null);
		adapter.setData(data);
		// getNetworkData(this, "/user/im/getInvitations.do", null);
		registerBoradcastReceiver();
		vf_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				VerificationFrientsList verificationFrientsList = (VerificationFrientsList) adapter.getItem(position);
				String userid = verificationFrientsList.getUserId();
				getUsersDetailWsIsFriend(userid);
			}
		});
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
				Cursor cursor = reContentResolverol.query(contactData, null,
						null, null, null);
				cursor.moveToFirst();
				String username = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				cursor.close();
				Cursor phone = reContentResolverol.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);

				if (phone.moveToFirst()) {
					for (; !phone.isAfterLast(); phone.moveToNext()) {
						int index = phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
						int typeindex = phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
						int phone_type = phone.getInt(typeindex);
						String phoneNumber = phone.getString(index);
						switch (phone_type) {
						case 2:
							usernumber = phoneNumber.replace(" ", "")
									.replace("-", "").replace("+86", "");
							isUserReg(usernumber);
							break;
						}
					}
					if (!phone.isClosed()) {
						phone.close();
					}
				}
			}
		}
	}

	public void isUserReg(String phoneNumber) {
		JSONObject josn = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			array.put(phoneNumber);
			josn.put("mobiles", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loading();
		getNetworkData(this, "/user/api/isUserReg.do", josn.toString(), false);
	}

	
	public void getUsersDetailWsIsFriend(String userIds){
		loading();
		JSONObject jsonObject = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			array.put(userIds);
			jsonObject.put("userIds", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = UrlUtils.HOST + "/user/im/getUsersDetailWsIsFriend.do" + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		MyRequestBody requestBody = new MyRequestBody(url,
				jsonObject.toString(), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						destroyDialog();
						SearchUserInfo searchUserInfo = GsonUtli.jsonToObject(response.toString(), SearchUserInfo.class);
						if(searchUserInfo!=null){
							List<SearchUserList> data = searchUserInfo.getData();
							if(data!=null && data.size()>0){
								SearchUserList searchUserList = data.get(0);
								Boolean isFirend = searchUserList.getIsFriend();
								if(isFirend){
									Intent intent = new Intent(VerificationFrientsActivity.this,FriendDetailActivity.class);
									intent.putExtra("CityId", searchUserList.getCityId());
									intent.putExtra("DistrictId", searchUserList.getDistrictId());
									intent.putExtra("ProvinceId", searchUserList.getProvinceId());
									intent.putExtra("Nickname", searchUserList.getNickname());
									intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
									intent.putExtra("Sex", searchUserList.getSex());
									intent.putExtra("UserId", searchUserList.getUserId());
									startActivity(intent);
								}else{
									Intent intent = new Intent(VerificationFrientsActivity.this,UsersDetailActivity.class);
									intent.putExtra("CityId", searchUserList.getCityId());
									intent.putExtra("DistrictId", searchUserList.getDistrictId());
									intent.putExtra("ProvinceId", searchUserList.getProvinceId());
									intent.putExtra("Nickname", searchUserList.getNickname());
									intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
									intent.putExtra("Sex", searchUserList.getSex());
									intent.putExtra("UserId", searchUserList.getUserId());
									startActivity(intent);
								}
							}else{
								showToast(R.string.search_result);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						destroyDialog();
						showToast(R.string.network_error);
					}
				});
		mQueue.add(requestBody);
	}
	
	public void invite(String phoneNumber) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("comment", "");
			jsonObject.put("trgMobile", phoneNumber);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = UrlUtils.HOST + "/user/im/invite.do" + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		MyRequestBody requestBody = new MyRequestBody(url,
				jsonObject.toString(), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						destroyDialog();
						try {
							if (response.getInt("code") == 1) {
								showToast(R.string.friendquestsuccess);
								finish();
							}else if(response.getInt("cede") == -150){
								showToast(R.string.isFriendtrue);
								finish();
							}else{
								showToast(R.string.network_error);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						destroyDialog();
						showToast(R.string.network_error);
					}
				});
		mQueue.add(requestBody);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_layout:
			Intent intent = new Intent(this, SearchFriendActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.addphoneContact_layout:

			Intent contactIntent = new Intent();

			contactIntent.setAction(Intent.ACTION_PICK);

			contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);

			startActivityForResult(contactIntent, 1);
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONArray data = response.getJSONArray("data");
			if (data.toString().contains(usernumber)) {
				invite(usernumber);
			} else {
				destroyDialog();
				notHavaUser();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void notHavaUser() {
		InvitationFriendDialog dialog = new InvitationFriendDialog(this, R.style.QRCode_dialog);
		dialog.show();
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(MsgFragment.class.getName());
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  

	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			VFDaoImpl daoImpl = new VFDaoImpl(VerificationFrientsActivity.this);
			List<VerificationFrientsList> data = daoImpl.find();
			adapter.setData(data);
		}
		
	};

}
