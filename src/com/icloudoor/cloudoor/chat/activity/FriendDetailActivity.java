package com.icloudoor.cloudoor.chat.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.FindDBUtile;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.UserDBHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FriendDetailActivity extends BaseActivity implements OnClickListener , NetworkInterface{
	
	
	private ImageView right_img;
	private ImageView btn_back;
	private ImageView sex_img;
	private ImageView user_head;
	private TextView user_name;
	private TextView address_tx;
	private Button add_contact_bnt;

	
	String Nickname;
	String PortraitUrl;
	String UserId;
	int CityId;
	int DistrictId;
	int ProvinceId;
	int Sex;
	int returnChat;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		
		setContentView(R.layout.activity_frienddetail);
		right_img = (ImageView) findViewById(R.id.right_img);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		user_head = (ImageView) findViewById(R.id.user_head);
		sex_img = (ImageView) findViewById(R.id.sex_img);
		user_name = (TextView) findViewById(R.id.user_name);
		address_tx = (TextView) findViewById(R.id.address_tx);
		add_contact_bnt = (Button) findViewById(R.id.add_contact_bnt);
		right_img.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		add_contact_bnt.setOnClickListener(this);
		
		returnChat = getIntent().getIntExtra("returnChat",0);
		
		CityId = getIntent().getIntExtra("CityId",0);
		DistrictId = getIntent().getIntExtra("DistrictId",0);
		ProvinceId = getIntent().getIntExtra("ProvinceId",0);
		Sex = getIntent().getIntExtra("Sex",0);
		Nickname = getIntent().getStringExtra("Nickname");
		PortraitUrl = getIntent().getStringExtra("PortraitUrl");
		UserId = getIntent().getStringExtra("UserId");
		registerBoradcastReceiver();
		if(returnChat==1){
			add_contact_bnt.setText(R.string.returnChat);
		}
		setdata();
	}

	PopupWindow pw;

	public void initPopupWindow() {
		if (pw == null) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.pw_usersdetail, null);
			view.findViewById(R.id.delete_layout).setOnClickListener(this);
			view.findViewById(R.id.report_layout).setOnClickListener(this);
			pw = new PopupWindow(view);
			pw.setHeight(LayoutParams.WRAP_CONTENT);
			pw.setWidth(LayoutParams.WRAP_CONTENT);
			pw.setFocusable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.showAsDropDown(right_img);
		} else {
			if (!pw.isShowing()) {
				pw.showAsDropDown(right_img);
			}

		}

	}
	
	public void setdata(){
		ImageLoader.getInstance().displayImage(PortraitUrl, user_head, DisplayImageOptionsUtli.options);
		user_name.setText(Nickname);
		address_tx.setText(FindDBUtile.getProvinceName(this, ProvinceId)+""+FindDBUtile.getCityName(this, CityId)+
				"   "+FindDBUtile.getDistrictName(this, DistrictId));
		if(Sex==1){
			sex_img.setBackgroundResource(R.drawable.boy_ioc);
		}else{
			sex_img.setBackgroundResource(R.drawable.girl_ioc);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.right_img:
			initPopupWindow();
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.delete_layout:
			pw.dismiss();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("friendUserId", UserId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loading();
			getNetworkData(this, "/user/im/removeFriend.do", jsonObject.toString(),false);
			break;
		case R.id.report_layout:
			pw.dismiss();
			Intent reportIntent = new Intent(this,ReportActivity.class);
			reportIntent.putExtra("trgUserId", UserId);
			startActivity(reportIntent);
			break;
		case R.id.add_contact_bnt:
			if(returnChat==1){
				finish();
			}else{
				Intent intent = new Intent(this,ChatActivity.class);
				intent.putExtra("userId", UserId);
				startActivity(intent);
				break;
			}

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			int code = response.getInt("code");
			if(code==1){
				getFriends();
			}else{
				showToast(R.string.removeFriendFail);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void getFriends() {
//    	RequestQueue mRequestQueue = Volley.newRequestQueue(this);
		String url = UrlUtils.HOST + "/user/im/getFriends.do" + "?sid=" + loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId();
		MyRequestBody requestBody = new MyRequestBody( url, "{}",new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				MyFriendInfo friendInfo = GsonUtli.jsonToObject(
						response.toString(), MyFriendInfo.class);
				showToast(R.string.removeFriendSuccess);
				if(returnChat==1){
					setResult(RESULT_OK);
					finish();
				}else{
					finish();
				}
				destroyDialog();
				if (friendInfo != null) {
					List<MyFriendsEn> data = friendInfo.getData();
					UserDBHelper.getInstance(FriendDetailActivity.this).initTable(friendInfo.getData());
					finish();
//					FriendDaoImpl daoImpl = new FriendDaoImpl(
//							FriendDetailActivity.this);
//					SQLiteDatabase db = daoImpl.getDbHelper()
//							.getWritableDatabase();
//					if(data==null||data.size() == 0){
//						db.execSQL("delete from friends");
//						finish();
//					}else{
//						db.beginTransaction();
//						try {
//							db.execSQL("delete from friends");
//							for (int i = 0; i < data.size(); i++) {
//								MyFriendsEn friendsEn = data.get(i);
//								db.execSQL("insert into friends(userId, nickname ,portraitUrl,provinceId,districtId,cityId,sex) values(?,?,?,?,?,?,?)",
//										new Object[] { friendsEn.getUserId(),friendsEn.getNickname(),friendsEn.getPortraitUrl(), 
//										friendsEn.getProvinceId(), friendsEn.getDistrictId(), friendsEn.getCityId(), friendsEn.getSex()});
//							}
//							db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
//						} finally {
//							db.endTransaction();
//							finish();
//						}
//					}
				} 
			}
			
		},new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				destroyDialog();
				showToast(R.string.network_error);
			}
		});
		
		mQueue.add(requestBody);
	}
	
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("removeFriend");
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
		
	};
}
