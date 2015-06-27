package com.icloudoor.cloudoor.chat.activity;

import android.content.Intent;
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

import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.entity.SearchUserList;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.FindDBUtile;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UsersDetailActivity extends BaseActivity implements OnClickListener {
	
	private ImageView right_img;
	private ImageView btn_back;
	private ImageView sex_img;
	private ImageView user_head;
	private TextView user_name;
	private TextView address_tx;
	private Button add_contact_bnt;
	
	String Nickname;
	String PortraitUrl;
	String UserId;
	int CityId;
	int DistrictId;
	int ProvinceId;
	int Sex;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_detail);
		right_img = (ImageView) findViewById(R.id.right_img);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		user_head = (ImageView) findViewById(R.id.user_head);
		sex_img = (ImageView) findViewById(R.id.sex_img);
		user_name = (TextView) findViewById(R.id.user_name);
		address_tx = (TextView) findViewById(R.id.address_tx);
		add_contact_bnt = (Button) findViewById(R.id.add_contact_bnt);
		right_img.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		add_contact_bnt.setOnClickListener(this);
		
		
		CityId = getIntent().getIntExtra("CityId",0);
		DistrictId = getIntent().getIntExtra("DistrictId",0);
		ProvinceId = getIntent().getIntExtra("ProvinceId",0);
		Sex = getIntent().getIntExtra("Sex",0);
		Nickname = getIntent().getStringExtra("Nickname");
		PortraitUrl = getIntent().getStringExtra("PortraitUrl");
		UserId = getIntent().getStringExtra("UserId");
		setdata();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.add_contact_bnt:
			if(UserId==null){
				showToast(R.string.getuseriderror);
			}
			Intent intent = new Intent(this,RequestFriendActivity.class);
			intent.putExtra("trgUserId", UserId);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	
	public void setdata(){
		ImageLoader.getInstance().displayImage(PortraitUrl, user_head, DisplayImageOptionsUtli.options);
		user_name.setText(Nickname);
		address_tx.setText(FindDBUtile.getProvinceName(this, ProvinceId)+""+FindDBUtile.getCityName(this, CityId)+
				"   "+FindDBUtile.getDistrictName(this, DistrictId));
		if(Sex==1){
			sex_img.setBackgroundResource(R.drawable.boy_ioc);
		}else{
			sex_img.setBackgroundResource(R.drawable.girl_ioc);
		}
	}
	
	
	


	
	
}
