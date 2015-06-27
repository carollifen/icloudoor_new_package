package com.icloudoor.cloudoor.chat.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.icloudoor.cloudoor.chat.entity.SearchUserInfo;
import com.icloudoor.cloudoor.chat.entity.SearchUserList;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class SearchFriendActivity extends BaseActivity implements OnClickListener,NetworkInterface{
	
	private EditText edit_search;
	private LinearLayout search_layout;
	private TextView search_tx;
	private ImageView btn_back;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_friend);
		edit_search = (EditText) findViewById(R.id.edit_search);
		search_layout = (LinearLayout) findViewById(R.id.search_layout);
		search_tx = (TextView) findViewById(R.id.search_tx);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		edit_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>0){
					search_layout.setVisibility(View.VISIBLE);
					search_tx.setText(s);
				}else{
					search_layout.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		search_layout.setOnClickListener(this);
		btn_back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_layout:
			
			String search = search_tx.getText().toString().trim();
			Map<String, String> map = new HashMap<String, String>();
			map.put("searchValue", search);
			getNetworkData(this, "/user/im/searchUser.do", map);
			break;
		case R.id.btn_back:
			
			finish();
			break;

		default:
			break;
		}
	}
	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		SearchUserInfo searchUserInfo = GsonUtli.jsonToObject(response.toString(), SearchUserInfo.class);
		if(searchUserInfo!=null){
			List<SearchUserList> data = searchUserInfo.getData();
			if(data!=null && data.size()>0){
				SearchUserList searchUserList = data.get(0);
				
				Boolean isFirend = searchUserList.getIsFriend();
				if(isFirend){
					Intent intent = new Intent(this,FriendDetailActivity.class);
					intent.putExtra("CityId", searchUserList.getCityId());
					intent.putExtra("DistrictId", searchUserList.getDistrictId());
					intent.putExtra("ProvinceId", searchUserList.getProvinceId());
					intent.putExtra("Nickname", searchUserList.getNickname());
					intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
					intent.putExtra("Sex", searchUserList.getSex());
					intent.putExtra("UserId", searchUserList.getUserId());
					startActivity(intent);
				}else{
					Intent intent = new Intent(this,UsersDetailActivity.class);
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
	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
