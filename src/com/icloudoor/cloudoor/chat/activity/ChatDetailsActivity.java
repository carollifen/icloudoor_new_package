package com.icloudoor.cloudoor.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.widget.ClearChatDialog;

public class ChatDetailsActivity extends BaseActivity implements OnClickListener{

	RelativeLayout addphoneContact_layout;
	RelativeLayout report_layout;
	ImageView btn_back;
	String trgUserId;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_chatdetails);
		trgUserId = getIntent().getExtras().getString("trgUserId");
		addphoneContact_layout = (RelativeLayout) findViewById(R.id.addphoneContact_layout);
		report_layout = (RelativeLayout) findViewById(R.id.report_layout); 
		btn_back = (ImageView) findViewById(R.id.btn_back); 
		btn_back.setOnClickListener(this);
		addphoneContact_layout.setOnClickListener(this);
		report_layout.setOnClickListener(this);
	}
	ClearChatDialog dialog;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.addphoneContact_layout:
			dialog = new ClearChatDialog(this);
			dialog.show();
			dialog.setOnlick(this);
			break;
		case R.id.report_layout:
			Intent intent = new Intent(this,ReportActivity.class);
			intent.putExtra("trgUserId", trgUserId);
			startActivity(intent);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.report_tx:
			dialog.dismiss();
			EMChatManager.getInstance().clearConversation(trgUserId);
			finish();
			break;

		default:
			break;
		}
	}
	
	
}
