package com.icloudoor.cloudoor.chat.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.VerificationFrientsAdapter;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class VerificationFrientsActivity extends BaseActivity implements NetworkInterface, OnClickListener{
	
	
	private ListView vf_listView;
	private ImageView btn_back;
	private LinearLayout search_layout;
	private RelativeLayout addphoneContact_layout;
	private VerificationFrientsAdapter adapter;
	
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
		getNetworkData(this, "/user/im/getInvitations.do", null);
		
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		VerificationFrientsInfo frientsInfo = GsonUtli.jsonToObject(response.toString(), VerificationFrientsInfo.class);
		if(frientsInfo!=null){
			adapter.setData(frientsInfo.getData());
		}
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
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
			break;

		default:
			break;
		}
	}

}
