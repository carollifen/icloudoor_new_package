package com.icloudoor.cloudoor.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.activity.DynamicActivity;
import com.icloudoor.cloudoor.chat.activity.FriendDetailActivity;
import com.icloudoor.cloudoor.chat.activity.UsersDetailActivity;
import com.icloudoor.cloudoor.chat.entity.DynamicInfo;
import com.icloudoor.cloudoor.chat.entity.SearchUserInfo;
import com.icloudoor.cloudoor.chat.entity.SearchUserList;
import com.icloudoor.cloudoor.chat.entity.ThumberInfo;
import com.icloudoor.cloudoor.utli.DateUtli;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.Uitls;
import com.icloudoor.cloudoor.widget.CircleImageView;
import com.icloudoor.cloudoor.widget.GridViewForScrollview;
import com.icloudoor.cloudoor.widget.MultipleTextView;
import com.icloudoor.cloudoor.widget.MultipleTextView.OnMultipleTVItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DynamicAdapter extends BaseAdapter implements OnMultipleTVItemClickListener{
	
	private Context context;
	List<DynamicInfo> data;
	DynamicActivity activity;
	int[] w_h;
	int width;
	int poorW;
	String userid ;
	String nickName;
	public DynamicAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = (DynamicActivity) context;
		SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS", Context.MODE_PRIVATE);
		userid = loginStatus.getString("USERID", "");
		nickName = loginStatus.getString("NICKNAME","");
		w_h = Uitls.getWH(context);
		width = w_h [0];
		poorW = Uitls.dip2px(context, 10);
		width = width - poorW;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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
			hodler.mulipletextview = (MultipleTextView) convertView.findViewById(R.id.mulipletextview);
			convertView.setTag(hodler);
		}else{
			hodler = (ViewHodler) convertView.getTag();
		}
		final DynamicInfo info = data.get(position);
		ImageLoader.getInstance().displayImage(info.getPortaitUrl(), hodler.head_img, DisplayImageOptionsUtli.options);
		hodler.user_name.setText(info.getNickname());
		List<String> photoUrls = info.getPhotoUrls();
		if(photoUrls!=null && photoUrls.size()>0){
			hodler.content_ioc_layout.setVisibility(View.VISIBLE);
			if(photoUrls.size()==1){
				hodler.content_ioc.setVisibility(View.VISIBLE);
				hodler.gridview1.setVisibility(View.GONE);
				hodler.gridview2.setVisibility(View.GONE);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width*3/4);
				hodler.content_ioc.setLayoutParams(params);
				ImageLoader.getInstance().displayImage(photoUrls.get(0), hodler.content_ioc, DisplayImageOptionsUtli.options);
			}else if (photoUrls.size()==2){
				hodler.content_ioc.setVisibility(View.GONE);
				hodler.gridview1.setVisibility(View.VISIBLE);
				hodler.gridview2.setVisibility(View.GONE);
				hodler.gridview1.setAdapter(new TwoAdapter(context, photoUrls));
			}else{
				hodler.content_ioc.setVisibility(View.GONE);
				hodler.gridview1.setVisibility(View.GONE);
				hodler.gridview2.setVisibility(View.VISIBLE);
				hodler.gridview2.setAdapter(new ThreeAdapter(context, photoUrls));
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
		
		if(userid.equals(info.getUserId())){
			hodler.delete.setVisibility(View.VISIBLE);
			hodler.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					JSONObject delete = new JSONObject();
					try {
						delete.put("actId", info.getActId());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					activity.getNetworkData(new NetworkInterface() {
						
						@Override
						public void onSuccess(JSONObject response) {
							// TODO Auto-generated method stub
							System.out.println("response = "+response);
							try {
								int code = response.getInt("code");
								if(code==1){
									data.remove(position);
									notifyDataSetChanged();
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
					}, "/user/im/act/remove.do", delete.toString(), true);
				}
			});
		}else{
			hodler.delete.setVisibility(View.INVISIBLE);
		}
		if(info.getHasThumb()){
			hodler.zan_tx.setText(R.string.unThumb);
		}else{
			hodler.zan_tx.setText(R.string.Thumb);
		}
		hodler.zan_tx.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(info.getHasThumb()){
					
					unthumb(info.getActId(),(TextView) v,position);
				}else{
					thumb(info.getActId(),(TextView)v,position);
				}
			}
		});
		
		List<ThumberInfo> thumbers = info.getThumbers();
		if(thumbers==null||thumbers.size()==0){
			hodler.mulipletextview.setVisibility(View.GONE);
		}else{
			hodler.mulipletextview.setVisibility(View.VISIBLE);
			hodler.mulipletextview.setTextViews(thumbers);
			hodler.mulipletextview.setOnMultipleTVItemClickListener(new OnMultipleTVItemClickListener() {
				
				@Override
				public void onMultipleTVItemClick(View view, int position, ThumberInfo info) {
					// TODO Auto-generated method stub
					if(!info.getUserId().equals(userid)){
						
						JSONObject object = new JSONObject();
						try {
							object.put("userIds", info.getUserId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						activity.getNetworkData(new NetworkInterface() {
							
							@Override
							public void onSuccess(JSONObject response) {
								// TODO Auto-generated method stub
								SearchUserInfo searchUserInfo = GsonUtli.jsonToObject(response.toString(), SearchUserInfo.class);
								if(searchUserInfo!=null){
									List<SearchUserList> data = searchUserInfo.getData();
									if(data!=null && data.size()>0){
										SearchUserList searchUserList = data.get(0);
										
										Boolean isFirend = searchUserList.getIsFriend();
										if(isFirend){
											Intent intent = new Intent(context,FriendDetailActivity.class);
											intent.putExtra("CityId", searchUserList.getCityId());
											intent.putExtra("DistrictId", searchUserList.getDistrictId());
											intent.putExtra("ProvinceId", searchUserList.getProvinceId());
											intent.putExtra("Nickname", searchUserList.getNickname());
											intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
											intent.putExtra("Sex", searchUserList.getSex());
											intent.putExtra("UserId", searchUserList.getUserId());
											context.startActivity(intent);
										}else{
											Intent intent = new Intent(context,UsersDetailActivity.class);
											intent.putExtra("CityId", searchUserList.getCityId());
											intent.putExtra("DistrictId", searchUserList.getDistrictId());
											intent.putExtra("ProvinceId", searchUserList.getProvinceId());
											intent.putExtra("Nickname", searchUserList.getNickname());
											intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
											intent.putExtra("Sex", searchUserList.getSex());
											intent.putExtra("UserId", searchUserList.getUserId());
											context.startActivity(intent);
										}
										
									}else{
										activity.showToast(R.string.search_result);
									}
								}
							}
							
							@Override
							public void onFailure(VolleyError error) {
								// TODO Auto-generated method stub
								
							}
						}, "/user/im/getUsersDetailWsIsFriend.do", object.toString(), true);
					}
				}
			});
		}
		
		return convertView;
	}
	
	public void thumb(String actId,final TextView view,final int position){
		
		JSONObject josn = new JSONObject();
		try {
			josn.put("actId", actId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		activity.getNetworkData(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
//				view.setText(R.string.unThumb);
				
				try {
					int code = response.getInt("code");
					if(code==1){
						DynamicInfo info = data.get(position);
						List<ThumberInfo> list = info.getThumbers();
						if(list==null){
							list = new ArrayList<ThumberInfo>();
						}
						ThumberInfo thumberInfo = new ThumberInfo();
						thumberInfo.setUserId(userid);
						thumberInfo.setNickname(nickName);
						thumberInfo.setThumbTime(System.currentTimeMillis()+"");
						list.add(thumberInfo);
						info.setHasThumb(true);
						info.setThumbers(list);
						notifyDataSetChanged();
					}else if(code==-155){
						activity.showToast(R.string.thisDynamicDelete);
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
		}, "/user/im/act/thumb.do", josn.toString(), true);
	}
	
	
	public void unthumb(String actId,final TextView view,final int position){
		
		JSONObject josn = new JSONObject();
		try {
			josn.put("actId", actId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		activity.getNetworkData(new NetworkInterface() {
			
			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
				
				int code;
				try {
					code = response.getInt("code");
					if(code==1){
						DynamicInfo info = data.get(position);
						info.getThumbers();
						info.setHasThumb(false);
						List<ThumberInfo> list = info.getThumbers();
						for (int i = 0; i < list.size(); i++) {
							ThumberInfo thumberInfo = list.get(i);
							if(thumberInfo.getUserId().equals(userid)){
								list.remove(i);
							}
						}
						info.setThumbers(list);
						notifyDataSetChanged();
					}else if(code==-155){
						activity.showToast(R.string.thisDynamicDelete);
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
		}, "/user/im/act/unthumb.do", josn.toString(), true);
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
		TextView zan_tx;
		MultipleTextView mulipletextview;
		RelativeLayout content_ioc_layout;
	}

	@Override
	public void onMultipleTVItemClick(View view, int position, ThumberInfo info) {
		// TODO Auto-generated method stub
		
	}

}
