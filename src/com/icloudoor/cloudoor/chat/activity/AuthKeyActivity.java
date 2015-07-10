package com.icloudoor.cloudoor.chat.activity;

import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.AuthKeyEn;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class AuthKeyActivity extends BaseActivity implements OnClickListener , NetworkInterface{
	
	TextView start_time;
	TextView end_time;
	LinearLayout auth_key_layout;
	ExpandableListView listView;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_authkey);
		
		auth_key_layout = (LinearLayout) findViewById(R.id.auth_key_layout);
		auth_key_layout.setOnClickListener(this);
		end_time = (TextView) findViewById(R.id.end_time);
		end_time = (TextView) findViewById(R.id.end_time);
		listView = (ExpandableListView) findViewById(R.id.listView);
		getNetworkData(this, "/user/api/keys/my.do", "{}", true);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.auth_key_layout:
			
			break;

		default:
			break;
		}
		
	}
	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		System.out.println("response = "+response);
		AuthKeyEn keyEn = GsonUtli.jsonToObject(response.toString(), AuthKeyEn.class);
		if(keyEn!=null){
			List<KeyInfo> data = keyEn.getData();
			if(data==null|| data.size()==0){
				
			}else{
				Myadapter myadapter = new Myadapter(data);
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
	
	
	
	class Myadapter extends BaseExpandableListAdapter{

		
		List<KeyInfo> data;
		public Myadapter(List<KeyInfo> data) {
			// TODO Auto-generated constructor stub
			this.data = data;
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

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			KeyInfo info = data.get(groupPosition);
			convertView = LayoutInflater.from(AuthKeyActivity.this).inflate(R.layout.key_groupview, null);
			TextView zone_name = (TextView) convertView.findViewById(R.id.zone_name);
			zone_name.setText(info.getAddress());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			convertView = LayoutInflater.from(AuthKeyActivity.this).inflate(R.layout.key_childview, null);
			TextView key_name = (TextView) convertView.findViewById(R.id.key_name);
			Key keys = data.get(groupPosition).getKeys().get(childPosition);
			key_name.setText(keys.getName());
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			
			
			return false;
		}

		
		
	}
	

}
