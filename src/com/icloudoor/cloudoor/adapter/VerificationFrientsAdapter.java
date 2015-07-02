package com.icloudoor.cloudoor.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.activity.RequestFriendActivity;
import com.icloudoor.cloudoor.chat.activity.VerificationFrientsActivity;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.FriendDaoImpl;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.VFDaoImpl;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VerificationFrientsAdapter extends BaseAdapter{

	private Context context;
	List<VerificationFrientsList> data;
	private VerificationFrientsActivity activity;
	
	public VerificationFrientsAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = (VerificationFrientsActivity) context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(data==null)
			return 0;
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_verificattion, null);
			holder.user_head = (ImageView) convertView.findViewById(R.id.user_head);
			holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
			holder.user_content = (TextView) convertView.findViewById(R.id.user_content);
			holder.state_tv = (TextView) convertView.findViewById(R.id.state_tv);
			holder.state_bnt = (Button) convertView.findViewById(R.id.state_bnt);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		final VerificationFrientsList frientsList = data.get(position);
		ImageLoader.getInstance().displayImage(frientsList.getPortraitUrl(), holder.user_head, DisplayImageOptionsUtli.options);
		holder.user_name.setText(frientsList.getNickname());
		
		String state = frientsList.getStatus();
		if(state.equals("0")){
			holder.state_tv.setVisibility(View.GONE);
			holder.state_bnt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Map<String, String> map = new HashMap<String, String>();
					map.put("invitationId", frientsList.getInvitationId());
					activity.getNetworkData(new NetworkInterface() {
						
						@Override
						public void onSuccess(JSONObject response) {
							// TODO Auto-generated method stub
							int code;
							try {
								code = response.getInt("code");
								if(code==1){
									activity.showToast(R.string.addfridendSuccess);
									VFDaoImpl daoImpl = new VFDaoImpl(context);
									daoImpl.execSql("update verificationFrients set status=? where invitationId=?", new String[]{"1",frientsList.getInvitationId()});
									getFriends();
									activity.finish();
									
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(VolleyError error) {
							// TODO Auto-generated method stub
							
						}
					}, "/user/im/acceptInvitation.do", map);
				}
			});
		}else{
			holder.state_bnt.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public void setData(List<VerificationFrientsList> data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	public String loadSid() {
		SharedPreferences loadSid = context
				.getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	public void getFriends() {
    	RequestQueue mRequestQueue = Volley.newRequestQueue(context);
		String url = UrlUtils.HOST + "/user/im/getFriends.do" + "?sid=" + loadSid();
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				url, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						MyFriendInfo friendInfo = GsonUtli.jsonToObject(
								response.toString(), MyFriendInfo.class);
						if (friendInfo != null) {
							List<MyFriendsEn> data = friendInfo.getData();
							if (data != null && data.size() > 0) {
								FriendDaoImpl daoImpl = new FriendDaoImpl(
										context);
								SQLiteDatabase db = daoImpl.getDbHelper()
										.getWritableDatabase();
								db.beginTransaction();
								try {
									db.execSQL("delete from friends");
									for (int i = 0; i < data.size(); i++) {
										MyFriendsEn friendsEn = data.get(i);
										db.execSQL("insert into friends(userId, nickname ,portraitUrl,provinceId,districtId,cityId,sex) values(?,?,?,?,?,?,?)",
												new Object[] { friendsEn.getUserId(),friendsEn.getNickname(),friendsEn.getPortraitUrl(), 
												friendsEn.getProvinceId(), friendsEn.getDistrictId(), friendsEn.getCityId(), friendsEn.getSex()});
									}
									db.setTransactionSuccessful();// µ÷ÓÃ´Ë·½·¨»áÔÚÖ´ÐÐµ½endTransaction()
								} finally {
									db.endTransaction();

								}
							}
						} else {
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return null;
			}
		};
		mRequestQueue.add(mJsonRequest);
	}
	
	
	class ViewHolder{
		
		ImageView user_head;
		TextView user_name;
		TextView user_content;
		TextView state_tv;
		Button state_bnt;
		
	}

}
