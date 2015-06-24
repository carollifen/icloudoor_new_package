package com.icloudoor.cloudoor.chat;

import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.VerificationFrientsAdapter;

public class VerificationFrientsActivity extends BaseActivity implements NetworkInterface{
	
	
	private ListView vf_listView;
	VerificationFrientsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_verificationfrients);
		vf_listView = (ListView) findViewById(R.id.vf_listView);
		adapter = new VerificationFrientsAdapter(this);
		vf_listView.setAdapter(adapter);
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

}
