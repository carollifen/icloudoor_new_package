package com.icloudoor.cloudoor.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.activity.SendDynamicActivity;
import com.icloudoor.cloudoor.utli.BitmapUtil;
import com.icloudoor.cloudoor.utli.NativeImageLoader;
import com.icloudoor.cloudoor.utli.NativeImageLoader.NativeImageCallBack;
import com.icloudoor.cloudoor.utli.Uitls;
import com.icloudoor.cloudoor.widget.PicSelectActivity;

public class SendDynamicAdapter extends BaseAdapter{

	Context context;
	int[] w_h;
	int poorW;
	int width;
	LinkedList<String> data;
	SendDynamicActivity activity;
	public SendDynamicAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = (SendDynamicActivity) context;
		data = new LinkedList<String>();
		data.add("add");
		w_h = Uitls.getWH(context);
		width = w_h [0];
		poorW = Uitls.dip2px(context, 10);
		width = width - poorW;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
	public void addFirstPath(String path){
		data.addFirst(path);
		if(data.size()==4){
			data.removeLast();
		}
		notifyDataSetChanged();
	}
	public void addPath(String path){
		data.addFirst(path);
		if(data.size()==4){
			data.removeLast();
		}
	}
	
	public List<String> getList(){
		data.remove("add");
		return this.data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.item_send_dynamic, null);
		final ImageView sendimage = (ImageView) convertView.findViewById(R.id.sendimage);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, width/4);
		sendimage.setLayoutParams(params);
		ImageView delete_ioc = (ImageView) convertView.findViewById(R.id.delete_ioc);
		String url = data.get(position);
		if(url.equals("add")){
			sendimage.setBackgroundResource(R.drawable.add_send_ioc);
			sendimage.setScaleType(ScaleType.CENTER_CROP);
			delete_ioc.setVisibility(View.GONE);
			sendimage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, PicSelectActivity.class);
					activity.startActivityForResult(intent, 0x123);
				}
			});
		}else{
			delete_ioc.setVisibility(View.VISIBLE);
			sendimage.setBackgroundResource(R.drawable.add_send_ioc);
			sendimage.setScaleType(ScaleType.CENTER_CROP);
			delete_ioc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					data.remove(position);
					if(data.size()==2){
						if(!data.getLast().equals("add")){
							data.add("add");
						}
					}
					notifyDataSetChanged();
				}
			});
			Bitmap bm = BitmapUtil.getimage(data.get(position));
			sendimage.setImageBitmap(bm);
			sendimage.setScaleType(ScaleType.CENTER_CROP);
			
		}
		return convertView;
	}

}
