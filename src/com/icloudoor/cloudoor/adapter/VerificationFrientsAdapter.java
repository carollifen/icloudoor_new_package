package com.icloudoor.cloudoor.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.activity.VerificationFrientsActivity;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
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
		if(TextUtils.isEmpty(frientsList.getComment())){
			holder.user_content.setText("");
		}else{
			holder.user_content.setText(frientsList.getComment());
		}
		String state = frientsList.getStatus();
		if(state.equals("1")){
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
//							06-25 14:33:06.802: I/System.out(6157): ºÃÓÑresponse = {"message":"successful","sid":"9dc7458797ac4259b6855e355ba71d78","code":1}
							int code;
							try {
								code = response.getInt("code");
								if(code==1){
									activity.showToast(R.string.addfridendSuccess);
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
	
	
	class ViewHolder{
		
		ImageView user_head;
		TextView user_name;
		TextView user_content;
		TextView state_tv;
		Button state_bnt;
		
	}

}
