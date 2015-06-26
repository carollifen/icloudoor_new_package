package com.icloudoor.cloudoor.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.entity.ChatRoomList;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RoomListAdapter extends BaseAdapter{
	
	private Context context;
	private List<ChatRoomList> data;
	
	public RoomListAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.item_room_list, null);
		ImageView room_head = (ImageView) convertView.findViewById(R.id.room_head);
		TextView room_name = (TextView) convertView.findViewById(R.id.room_name);
		TextView content_tx = (TextView)convertView.findViewById(R.id.content_tx);
		TextView time_tx = (TextView)convertView.findViewById(R.id.time_tx);
		ChatRoomList roomList = data.get(position);
		ImageLoader.getInstance().displayImage(roomList.getPortraitUrl(), room_head, DisplayImageOptionsUtli.options);
		room_name.setText(roomList.getName());
		content_tx.setText(roomList.getDescription());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date(roomList.getCreateTime()));
		time_tx.setText(date);
		
		
		return convertView;
	}
	
	public void setData(List<ChatRoomList> data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	

}
