package com.icloudoor.cloudoor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.widget.CircleImageView;
import com.icloudoor.cloudoor.widget.GridViewForScrollview;
import com.icloudoor.cloudoor.widget.MultipleTextView;

public class DynamicAdapter extends BaseAdapter{
	
	private Context context;
	
	public DynamicAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 20;
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
		
		ViewHodler hodler= null;
		if(convertView==null){
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_dynamic, null);
			hodler.head_img = (CircleImageView) convertView.findViewById(R.id.head_img);
			hodler.user_name = (TextView) convertView.findViewById(R.id.user_name);
			hodler.content_ioc = (ImageView) convertView.findViewById(R.id.content_ioc);
			hodler.gridview1 = (GridViewForScrollview) convertView.findViewById(R.id.gridview1);
			hodler.gridview2 = (GridViewForScrollview) convertView.findViewById(R.id.gridview2);
			hodler.content_tx = (TextView) convertView.findViewById(R.id.content_tx);
			hodler.fulltext = (TextView) convertView.findViewById(R.id.fulltext);
			hodler.time = (TextView) convertView.findViewById(R.id.time);
			hodler.delete = (TextView) convertView.findViewById(R.id.delete);
			hodler.zan_tx = (TextView) convertView.findViewById(R.id.zan_tx);
			hodler.zan_layout = (LinearLayout) convertView.findViewById(R.id.zan_layout);
			hodler.mulipletextview = (MultipleTextView) convertView.findViewById(R.id.mulipletextview);
			convertView.setTag(convertView);
		}else{
			hodler = (ViewHodler) convertView.getTag();
		}
		return convertView;
	}
	
	class ViewHodler{
		CircleImageView head_img;
		TextView user_name;
		ImageView content_ioc;
		GridViewForScrollview gridview1;
		GridViewForScrollview gridview2;
		TextView content_tx;
		TextView fulltext;
		TextView time;
		TextView delete;
		LinearLayout zan_layout;
		TextView zan_tx;
		MultipleTextView mulipletextview;
	}

}
