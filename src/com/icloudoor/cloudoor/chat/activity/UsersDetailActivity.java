package com.icloudoor.cloudoor.chat.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.UsersDetailInfo;
import com.icloudoor.cloudoor.chat.entity.UsersDetailList;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.FindDBUtile;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UsersDetailActivity extends BaseActivity implements OnClickListener ,NetworkInterface{
	
	private ImageView right_img;
	private ImageView btn_back;
	private ImageView user_head;
	private TextView user_name;
	private TextView address_tx;
	private String userid;
	private Button add_contact_bnt;
	
	private String trgUserId;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_detail);
		right_img = (ImageView) findViewById(R.id.right_img);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		user_head = (ImageView) findViewById(R.id.user_head);
		user_name = (TextView) findViewById(R.id.user_name);
		address_tx = (TextView) findViewById(R.id.address_tx);
		add_contact_bnt = (Button) findViewById(R.id.add_contact_bnt);
		right_img.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		add_contact_bnt.setOnClickListener(this);
		userid = getIntent().getStringExtra("userId");
		Map<String, String> map = new HashMap<String, String>();
		map.put("userIds", userid);
		getNetworkData(this, "/user/api/getUsersDetail.do", map);
	}
	
	PopupWindow pw;
	
	public void initPopupWindow(){
		if(pw==null){
			View view = LayoutInflater.from(this).inflate(R.layout.pw_addfriend, null);
			view.findViewById(R.id.sweep_layout).setOnClickListener(this);
			view.findViewById(R.id.add_friend_layout).setOnClickListener(this);
			view.findViewById(R.id.constat_layout).setOnClickListener(this);
			pw = new PopupWindow(view);
			pw.setHeight(LayoutParams.WRAP_CONTENT);
			pw.setWidth(LayoutParams.WRAP_CONTENT);
			pw.setFocusable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.showAsDropDown(right_img);
		}else{
			if(!pw.isShowing()){
				pw.showAsDropDown(right_img);
			}
			
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.right_img:
			initPopupWindow();
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.add_contact_bnt:
			if(trgUserId==null){
				showToast(R.string.getuseriderror);
			}
			Intent intent = new Intent(this,RequestFriendActivity.class);
			intent.putExtra("trgUserId", trgUserId);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		UsersDetailInfo detailInfo = GsonUtli.jsonToObject(response.toString(), UsersDetailInfo.class);
		if(detailInfo!=null){
			List<UsersDetailList> data = detailInfo.getData();
			if(data!=null && data.size()>0){
				UsersDetailList detailList = data.get(0);
				ImageLoader.getInstance().displayImage(detailList.getPortraitUrl(), user_head, DisplayImageOptionsUtli.options);
				user_name.setText(detailList.getNickname());
				address_tx.setText(FindDBUtile.getProvinceName(this, detailList.getProvinceId())+""+FindDBUtile.getCityName(this, detailList.getCityId())+
						"   "+FindDBUtile.getDistrictName(this, detailList.getDistrictId()));
				trgUserId = detailList.getUserId();
				
			}
		}else{
			showToast(R.string.network_error);
		}
		
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}
	
	
}
