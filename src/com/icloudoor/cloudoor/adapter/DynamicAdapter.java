package com.icloudoor.cloudoor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.entity.DynamicInfo;
import com.icloudoor.cloudoor.utli.DateUtli;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.widget.CircleImageView;
import com.icloudoor.cloudoor.widget.GridViewForScrollview;
import com.icloudoor.cloudoor.widget.MultipleTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DynamicAdapter extends BaseAdapter{
	
	private Context context;
	List<DynamicInfo> data;
	
	public DynamicAdapter(Context context) {
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
		return position;
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
			hodler.content_ioc_layout = (RelativeLayout) convertView.findViewById(R.id.content_ioc_layout);
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
			convertView.setTag(hodler);
		}else{
			hodler = (ViewHodler) convertView.getTag();
		}
		DynamicInfo info = data.get(position);
		ImageLoader.getInstance().displayImage(info.getPortaitUrl(), hodler.head_img, DisplayImageOptionsUtli.options);
		hodler.user_name.setText(info.getNickname());
		List<String> photoUrls = info.getPhotoUrls();
		if(photoUrls!=null && photoUrls.size()>0){
			hodler.content_ioc_layout.setVisibility(View.VISIBLE);
			if(photoUrls.size()==1){
				hodler.content_ioc.setVisibility(View.VISIBLE);
				hodler.gridview1.setVisibility(View.GONE);
				hodler.gridview2.setVisibility(View.GONE);
				ImageLoader.getInstance().displayImage(info.getPortaitUrl(), hodler.content_ioc, DisplayImageOptionsUtli.options);
			}else if (photoUrls.size()==2){
				hodler.content_ioc.setVisibility(View.GONE);
				hodler.gridview1.setVisibility(View.VISIBLE);
				hodler.gridview2.setVisibility(View.GONE);
			}else{
				hodler.content_ioc.setVisibility(View.GONE);
				hodler.gridview1.setVisibility(View.GONE);
				hodler.gridview2.setVisibility(View.VISIBLE);
			}
		}else{
			hodler.content_ioc_layout.setVisibility(View.GONE);
		}
		hodler.content_tx.setText(info.getContent());
		hodler.fulltext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		hodler.time.setText(DateUtli.getTime(info.getCreateTime(), context));
		SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS", Context.MODE_PRIVATE);
		String userid = loginStatus.getString("ID", "");
		if(userid.equals(info.getUserId())){
			hodler.delete.setVisibility(View.VISIBLE);
			hodler.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		}else{
			hodler.delete.setVisibility(View.INVISIBLE);
		}
		
		hodler.zan_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return convertView;
	}
	
	public void setData(List<DynamicInfo> list){
		this.data = list;
		notifyDataSetChanged();
	}
	public void addData(List<DynamicInfo> list){
		if(data==null){
			data = new ArrayList<DynamicInfo>();
		}
		data.addAll(list);
		notifyDataSetChanged();
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
		RelativeLayout content_ioc_layout;
	}

}
