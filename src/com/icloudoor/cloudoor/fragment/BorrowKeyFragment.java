package com.icloudoor.cloudoor.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.BaseFragment;
import com.icloudoor.cloudoor.MyDebugLog;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Version;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.AuthKeyEn;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class BorrowKeyFragment extends BaseFragment implements OnClickListener {

	View rootView;
	LinearLayout give_key_layout;
	ExpandableListView listView;
	List<KeyInfo> data;
	Myadapter myadapter;
	boolean isCarDoor;
	boolean isFlage = true;
	LinearLayout content_layout;
	LinearLayout not_content_layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_borrowkey, null);
		listView = (ExpandableListView) rootView.findViewById(R.id.listView);
		content_layout = (LinearLayout) rootView.findViewById(R.id.content_layout);
		not_content_layout = (LinearLayout) rootView.findViewById(R.id.not_content_layout);
		give_key_layout = (LinearLayout) rootView.findViewById(R.id.give_key_layout);
		give_key_layout.setOnClickListener(this);
		getMyBorrow();
		return rootView;
	}

	public void getMyBorrow() {
		mQueue = Volley.newRequestQueue(getActivity());
		version = new Version(getActivity().getApplicationContext());
		String url = UrlUtils.HOST + "/user/api/keys/myBorrow.do" + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		MyJsonObjectRequest requestBody = new MyJsonObjectRequest(Method.POST,url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						AuthKeyEn keyEn = GsonUtli.jsonToObject(
								response.toString(), AuthKeyEn.class);
						if (keyEn != null) {
							data = keyEn.getData();
							if (data == null || data.size() == 0) {
								content_layout.setVisibility(View.GONE);
								not_content_layout.setVisibility(View.VISIBLE);
							} else {
								content_layout.setVisibility(View.VISIBLE);
								not_content_layout.setVisibility(View.GONE);
								myadapter = new Myadapter(data);
								listView.setAdapter(myadapter);
								for (int i = 0; i < myadapter.getGroupCount(); i++) {
									listView.expandGroup(i);
								}
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

	class Myadapter extends BaseExpandableListAdapter {

		List<KeyInfo> data;
		List<List<Boolean>> isChekble;

		public Myadapter(List<KeyInfo> data) {
			// TODO Auto-generated constructor stub
			this.data = data;
			isChekble = new ArrayList<List<Boolean>>();
			for (int i = 0; i < data.size(); i++) {
				List<Boolean> isChildren = new LinkedList<Boolean>();
				for (int j = 0; j < data.get(i).getKeys().size(); j++) {
					// if(i==0&&j==0){
					// isChildren.add(true);
					// }else{
					isChildren.add(false);
					// }
				}
				isChekble.add(isChildren);
			}
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

		public List<List<Boolean>> getChekbleData() {

			return isChekble;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			KeyInfo info = data.get(groupPosition);
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.key_groupview, null);
			TextView zone_name = (TextView) convertView
					.findViewById(R.id.zone_name);
			zone_name.setText(info.getAddress());
			convertView.setEnabled(false);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.key_childview, null);
			TextView key_name = (TextView) convertView
					.findViewById(R.id.key_name);
			 TextView state_tx = (TextView) convertView
					.findViewById(R.id.state_tx);
			 ImageView auth_key_img = (ImageView) convertView
					.findViewById(R.id.auth_key_img);
			View divider_view = convertView.findViewById(R.id.divider_view);
			if ((childPosition + 1) == data.get(groupPosition).getKeys().size()) {
				divider_view.setVisibility(View.GONE);
			} else {
				divider_view.setVisibility(View.VISIBLE);
			}
			if (isChekble.get(groupPosition).get(childPosition)) {
				auth_key_img.setImageResource(R.drawable.key_box_p);
			} else {
				auth_key_img.setImageResource(R.drawable.key_box_n);
			}
			Key keys = data.get(groupPosition).getKeys().get(childPosition);
			final String l1ZoneId = data.get(groupPosition).getZoneUserId();
			String authStatus = keys.getAuthStatus();
			final String doorType = keys.getDoorType();

			if (doorType.trim().equals("2")) {
				if (authStatus.trim().equals("2")) {
					state_tx.setVisibility(View.VISIBLE);
					auth_key_img.setVisibility(View.GONE);
				} else {
					state_tx.setVisibility(View.GONE);
					auth_key_img.setVisibility(View.VISIBLE);
				}
			}else{
				state_tx.setVisibility(View.VISIBLE);
				auth_key_img.setVisibility(View.GONE);
				state_tx.setText(R.string.key_type);
			}
			key_name.setText(keys.getName());

			auth_key_img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (doorType.trim().equals("2")) {
						for (int i = 0; i < isChekble.size(); i++) {
							for (int j = 0; j < isChekble.get(i).size(); j++) {
								isChekble.get(i).set(j, false);
							}
						}
						isChekble.get(groupPosition).set(childPosition, true);
						isCarDoor = true;
						notifyDataSetChanged();
					} 

				}
			});

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.give_key_layout:
			List<List<Boolean>> chekbleData = myadapter.getChekbleData();
			for (int i = 0; i < chekbleData.size(); i++) {
				for (int j = 0; j < chekbleData.get(i).size(); j++) {
					if (chekbleData.get(i).get(j)) {
						isFlage = false;
						KeyInfo keyInfo = data.get(i);
						List<Key> keys = keyInfo.getKeys();
						Key key = keys.get(j);
						
						Map<String, String> map = new HashMap<String, String>();
						map.put("l1ZoneId", keyInfo.getL1ZoneId());
						map.put("carPosStatus", key.getAuthStatus());
						map.put("plateNum", key.getName());
						
					}
				}
			}
			if (isFlage) {
				showToast(R.string.plass_door);
				return;
			}
			
			break;

		default:
			break;
		}
	}
	
	public void giveKey(Map<String, String> map){
		//»¹Ô¿³×
		getMyJsonObjectRequest(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if(response.getInt("code") == 1){
						
//						if(response.getString("sid") != null){
//							saveSid(response.getString("sid"));
//						}
						
					}else if(response.getInt("code") == -1){
						Toast.makeText(getActivity(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
					}else if(response.getInt("code") == -2){
						Toast.makeText(getActivity(), R.string.not_login, Toast.LENGTH_SHORT).show();
					}else if(response.getInt("code") == -99){
						Toast.makeText(getActivity(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
					}else if(response.getInt("code") == -107){
						Toast.makeText(getActivity(), R.string.car_not_lend, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		}
		
		, "/user/api/returnTempAuthCar.do", map, true);
	}

}
