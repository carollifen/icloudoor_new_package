package com.icloudoor.cloudoor.chat.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.DynamicAdapter;
import com.icloudoor.cloudoor.chat.entity.DynamicEn;
import com.icloudoor.cloudoor.chat.entity.DynamicInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.widget.PicSelectActivity;
import com.icloudoor.cloudoor.widget.RefreshListView;
import com.icloudoor.cloudoor.widget.RefreshListView.IOnLoadMoreListener;
import com.icloudoor.cloudoor.widget.RefreshListView.IOnRefreshListener;

public class DynamicActivity extends BaseActivity implements NetworkInterface,OnClickListener{

	RequestQueue mQueue;
	RefreshListView listview;
	DynamicAdapter adapter;
	boolean isRefresh = false;
	TextView dynami_editing;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_dynamic);
		listview = (RefreshListView) findViewById(R.id.listview);
		dynami_editing = (TextView) findViewById(R.id.dynami_editing);
		dynami_editing.setOnClickListener(this);
		View head = LayoutInflater.from(this).inflate(R.layout.dynamic_head, null);
		listview.addHeaderView(head);
		adapter = new DynamicAdapter(this);
		listview.setAdapter(adapter);
		listview.onLoadMoreComplete(true);
		listview.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				isRefresh = true;
				refresh();
			}
		});
		
		listview.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				
			}
		});
		refresh();
	}
	
	public void loadMore(String beforeActId){
		isRefresh = false;
		JSONObject parm = new JSONObject();
		try {
			parm.put("num", 20);
			parm.put("beforeActId", beforeActId);
			getNetworkData(this, "/user/im/act/get.do", parm.toString(), true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refresh(){
		JSONObject parm = new JSONObject();
		try {
			parm.put("num", 20);
			getNetworkData(this, "/user/im/act/get.do", parm.toString(), true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		listview.onRefreshComplete();
		
		DynamicEn dynamicEn = GsonUtli.jsonToObject(response.toString(), DynamicEn.class);
		if(dynamicEn!=null){
			List<DynamicInfo> data = dynamicEn.getData();
			if(data!=null && data.size()>0){
				if(isRefresh){
					adapter.setData(data);
				}else{
					adapter.addData(data);
				}
			}else{
				showToast(R.string.notData);
			}
		}else{
			showToast(R.string.network_error);
		}
		
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		listview.onRefreshComplete();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dynami_editing:
			Intent intent = new Intent(this, PicSelectActivity.class);
			startActivityForResult(intent, 2);
			break;

		default:
			break;
		}
	}

}
