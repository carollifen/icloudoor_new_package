package com.icloudoor.cloudoor.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.BaseFragment;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Version;
import com.icloudoor.cloudoor.Entities.AuthRecordEn;
import com.icloudoor.cloudoor.Entities.KeyInfoT;
import com.icloudoor.cloudoor.Entities.RecordsEn;
import com.icloudoor.cloudoor.adapter.RecordAdapter;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.widget.CustomExpandableListView;

public class AuthRecordFragment extends BaseFragment{
	
	View rootView;
	LinearLayout not_content_layout;
	ListView listView;
	MyAdapter myadapter;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_authrecord, null);
		not_content_layout = (LinearLayout) rootView.findViewById(R.id.not_content_layout);
		listView = (ListView) rootView.findViewById(R.id.listview);
		getAuthRecord();
		return rootView;
	}
	
	
	
	public void getAuthRecord() {
		mQueue = Volley.newRequestQueue(getActivity());
		version = new Version(getActivity().getApplicationContext());
		String url = UrlUtils.HOST + "/user/api/keys/myAuth.do" + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		MyJsonObjectRequest requestBody = new MyJsonObjectRequest(Method.POST,url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						AuthRecordEn recordEn = GsonUtli.jsonToObject(
								response.toString(), AuthRecordEn.class);
						if(recordEn.getCode()==1){
							List<RecordsEn> data = recordEn.getData();
							if(data==null|| data.size()==0){
								not_content_layout.setVisibility(View.VISIBLE);
								listView.setVisibility(View.GONE);
							}else{
								not_content_layout.setVisibility(View.GONE);
								listView.setVisibility(View.VISIBLE);
								initData(data);
							}
						}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						showToast(R.string.network_error);
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return null;
			}
		};
		mQueue.add(requestBody);
	}
	
	
	public void initData(List<RecordsEn> data){
		listView.setAdapter(new MyAdapter(data, getActivity()));
	}
	
	
 	class MyAdapter extends BaseAdapter{
 		List<RecordsEn> data;
 		Context context;
 		public MyAdapter(List<RecordsEn> data,Context context) {
			// TODO Auto-generated constructor stub
 			this.data = data;
 			this.context = context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_record, null);
				holder.time_tx = (TextView) convertView.findViewById(R.id.time_tx);
				holder.expandableListView =  (CustomExpandableListView) convertView.findViewById(R.id.item_listView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.time_tx.setText(data.get(position).getDate());
			RecordAdapter adapter = new RecordAdapter(data.get(position).getRecords(), context);
			holder.expandableListView.setAdapter(adapter);
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				holder.expandableListView.expandGroup(i);
			}
			return convertView;
		}
 		
 	}
 	
 	class ViewHolder{
 		CustomExpandableListView expandableListView;
 		TextView time_tx;
 	}

}
