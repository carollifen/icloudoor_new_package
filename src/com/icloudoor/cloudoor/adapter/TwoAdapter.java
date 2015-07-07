package com.icloudoor.cloudoor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.Uitls;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TwoAdapter extends BaseAdapter{

	
	Context context;
	int[] w_h;
	int poorW;
	int width;
	List<String> data;
	public TwoAdapter(Context context ,List<String> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.data = data ;
		w_h = Uitls.getWH(context);
		width = w_h [0];
		poorW = Uitls.dip2px(context, 16);
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
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.item_show_dynamic, null);
		final ImageView sendimage = (ImageView) convertView.findViewById(R.id.sendimage);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width/2, width/2*3/4);
		sendimage.setLayoutParams(params);
		String url = data.get(position);
		ImageLoader.getInstance().displayImage(url, sendimage, DisplayImageOptionsUtli.options);
		return convertView;
	}
}
