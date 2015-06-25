package com.icloudoor.cloudoor.chat.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.VerificationFrientsAdapter;

public class VerificationFrientsActivity extends BaseActivity implements NetworkInterface, OnClickListener{
	
	
	private ListView vf_listView;
	private RelativeLayout search_layout;
	VerificationFrientsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_verificationfrients);
		vf_listView = (ListView) findViewById(R.id.vf_listView);
		search_layout = (RelativeLayout) findViewById(R.id.search_layout);
		adapter = new VerificationFrientsAdapter(this);
		vf_listView.setAdapter(adapter);
		search_layout.setOnClickListener(this);
		getNetworkData(this, "/user/im/getInvitations.do", null);
		
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		
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

		default:
			break;
		}
	}

}
