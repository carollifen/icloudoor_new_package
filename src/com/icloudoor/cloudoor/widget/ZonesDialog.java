package com.icloudoor.cloudoor.widget;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Entities.ZonesInfo;

public class ZonesDialog extends Dialog{
	ListView listView;
	TextView btn_back;
	Context context;
	List<ZonesInfo> lookdata ;
	ZonesAdapter adapter;
	public ZonesDialog(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	public ZonesDialog(Context context,int theme){
		super(context, theme);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_zones);
		listView = (ListView) findViewById(R.id.listView);
		btn_back = (TextView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		lookdata = new ArrayList<ZonesInfo>();
		SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS", 0);
		String l1Zones = loginStatus.getString("l1Zones", "");
		try {
			JSONArray array = new JSONArray(l1Zones);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				String l1ZoneName = object.getString("l1ZoneName");
				String l1ZoneId = object.getString("l1ZoneId");
				ZonesInfo info = new ZonesInfo();
				if(i==0){
					info.setIschekble(true);
				}else{
					info.setIschekble(false);
				}
				info.setL1ZoneId(l1ZoneId);
				info.setL1ZoneName(l1ZoneName);
				lookdata.add(info);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new ZonesAdapter();
		listView.setAdapter(adapter);
	}
	
	public void setonItem(OnItemClickListener itemClickListener){
		listView.setOnItemClickListener(itemClickListener);
	}
	
	public ZonesInfo Changed(int position){
		ZonesInfo info = null;
		for (int i = 0; i < lookdata.size(); i++) {
			info = lookdata.get(i);
			if(i==position){
				info.setIschekble(true);
			}else{
				info.setIschekble(false);
			}
		}
		adapter.notifyDataSetChanged();
		dismiss();
		return lookdata.get(position);
	}
	
	class ZonesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lookdata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lookdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(context).inflate(R.layout.item_zones, null);
			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			TextView zones_name = (TextView) convertView.findViewById(R.id.zones_name);
			ZonesInfo info = lookdata.get(position);
			checkBox.setChecked(info.isIschekble());
			zones_name.setText(info.getL1ZoneName());
			return convertView;
		}
		
	}
}
