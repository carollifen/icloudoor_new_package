package com.icloudoor.cloudoor.chat.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Version;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.MyFriendsAdapter;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.UserInfoTable;
import com.icloudoor.cloudoor.utli.CharacterParser;
import com.icloudoor.cloudoor.utli.FriendDaoImpl;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.UserinfoDaoImpl;
import com.icloudoor.cloudoor.widget.SideBar;
import com.icloudoor.cloudoor.widget.SideBar.OnTouchingLetterChangedListener;

public class ContactActivity extends BaseActivity implements OnClickListener,
		NetworkInterface {


	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private TextView sweep_tx;
	private MyFriendsAdapter adapter;
	private CharacterParser characterParser;
	LayoutInflater inflater;
	private ImageView btn_back;
	UserinfoDaoImpl daoImpl;
	FriendDaoImpl friendDaoImpl;
	int type;
	
	private Version version;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_contact);
		
		version = new Version(getApplicationContext());
		
		type = getIntent().getExtras().getInt("type",0);
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sweep_tx = (TextView) findViewById(R.id.sweep_tx);
		sideBar.setTextView(dialog);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.friend_header_view, null);
		v.findViewById(R.id.add_friend).setOnClickListener(this);
		btn_back.setOnClickListener(this);
		sweep_tx.setOnClickListener(this);

		sortListView.addHeaderView(v);
		adapter = new MyFriendsAdapter(this, null);
		sortListView.setAdapter(adapter);
		characterParser = new CharacterParser();
		setListener();
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserInfoTable friendsEn = (UserInfoTable) adapter.getItem(position-1);
				if(type==1){
					Intent it  = new Intent();
					it.putExtra("CityId", friendsEn.getCityId());
					it.putExtra("DistrictId", friendsEn.getDistrictId());
					it.putExtra("ProvinceId", friendsEn.getProvinceId());
					it.putExtra("Sex", friendsEn.getSex());
					it.putExtra("Nickname", friendsEn.getNickname());
					it.putExtra("PortraitUrl", friendsEn.getPortraitUrl());
					it.putExtra("UserId", friendsEn.getUserId());
					setResult(Activity.RESULT_OK, it);  
		                finish();  
				}else{
					Intent intent = new Intent(ContactActivity.this, FriendDetailActivity.class);
					intent.putExtra("CityId", friendsEn.getCityId());
					intent.putExtra("DistrictId", friendsEn.getDistrictId());
					intent.putExtra("ProvinceId", friendsEn.getProvinceId());
					intent.putExtra("Sex", friendsEn.getSex());
					intent.putExtra("Nickname", friendsEn.getNickname());
					intent.putExtra("PortraitUrl", friendsEn.getPortraitUrl());
					intent.putExtra("UserId", friendsEn.getUserId());
					startActivity(intent);
				}
				
				
			}
		});
		
		
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		daoImpl = new UserinfoDaoImpl(this);
		friendDaoImpl = new FriendDaoImpl(this);
		List<UserInfoTable> data  = daoImpl.find();
		List<MyFriendsEn> friendData = friendDaoImpl.find();
		List<UserInfoTable> userInfoData = new LinkedList<UserInfoTable>();
		for (int i = 0; i < friendData.size(); i++) {
			for (int j = 0; j < data.size(); j++) {
				if(friendData.get(i).getUserId().equals(data.get(j).getUserId())){
					userInfoData.add(data.get(j));
				}
			}
		}
		adapter.updateListView(filledData(userInfoData));
	}

	public void setListener() {

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});
	}
	
	


	private List<UserInfoTable> filledData(List<UserInfoTable> data) {
		List<UserInfoTable> mSortList = new ArrayList<UserInfoTable>();

		for (int i = 0; i < data.size(); i++) {
			UserInfoTable sortModel = data.get(i);
			if(TextUtils.isEmpty(data.get(i).getNickname())){
				continue;
			}
			String pinyin = characterParser.getSelling(data.get(i).getNickname());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_friend:
			Intent intent = new Intent(this, VerificationFrientsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.sweep_tx:
			Intent sweepIntent = new Intent();
			sweepIntent.setClass(this, MipcaActivityCapture.class);
			sweepIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(sweepIntent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		MyFriendInfo friendInfo = GsonUtli.jsonToObject(response.toString(),
				MyFriendInfo.class);
		if (friendInfo != null) {
			
		} else {
			showToast(R.string.jsonerror);
		}

	}
	

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

}
