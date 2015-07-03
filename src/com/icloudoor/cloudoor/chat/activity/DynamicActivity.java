package com.icloudoor.cloudoor.chat.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.widget.RefreshListView;

public class DynamicActivity extends BaseActivity implements NetworkInterface{

	RequestQueue mQueue;
	RefreshListView listview;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_dynamic);
		listview = (RefreshListView) findViewById(R.id.listview);
		View head = LayoutInflater.from(this).inflate(R.layout.dynamic_head, null);
		listview.addHeaderView(head);
		JSONObject parm = new JSONObject();
		try {
			parm.put("num", 20);
			getNetworkDataJSONType(this, "/user/im/act/get.do", parm.toString(), true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
