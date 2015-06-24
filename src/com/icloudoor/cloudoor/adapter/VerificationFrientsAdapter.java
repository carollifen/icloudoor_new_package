package com.icloudoor.cloudoor.adapter;

import com.icloudoor.cloudoor.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VerificationFrientsAdapter extends BaseAdapter{

	private Context context;
	
	
	public VerificationFrientsAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_verificattion, null);
			holder.user_head = (ImageView) convertView.findViewById(R.id.user_head);
			holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
			holder.user_content = (TextView) convertView.findViewById(R.id.user_content);
			holder.state_bnt = (Button) convertView.findViewById(R.id.state_bnt);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}
	
	
	class ViewHolder{
		
		ImageView user_head;
		TextView user_name;
		TextView user_content;
		Button state_bnt;
		
	}

}
