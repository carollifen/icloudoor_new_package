package com.icloudoor.cloudoor;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.SettingFragment.MyOnClickListener;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HelpFeedback extends Activity {

	private RelativeLayout common_problem;
	private RelativeLayout back_from_user;
	private RelativeLayout back;

	private FeedbackAgent agent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_feedback);
		back_from_user = (RelativeLayout) findViewById(R.id.back_from_user);
		back_from_user.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
				
				agent = new FeedbackAgent(HelpFeedback.this);
				UserInfo info = agent.getUserInfo();
				if (info == null)
					info = new UserInfo();
				Map<String, String> contact = info.getContact();
				if (contact == null)
					contact = new HashMap<String, String>();
				
				
				if(loginStatus.getString("NAME", null).length() > 0)
					contact.put("name", loginStatus.getString("NAME", null));
				if(loginStatus.getString("PHONENUM", null).length() > 0)
					contact.put("phone", loginStatus.getString("PHONENUM", null));
				info.setContact(contact);
				agent.setUserInfo(info);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean result = agent.updateUserInfo();
					}
					
				}).start();
				
				agent.setWelcomeInfo(getString(R.string.umeng_fb_reply_content_default));
				agent.startFeedbackActivity();
			}
		});
		
		common_problem = (RelativeLayout) findViewById(R.id.common_problem);
		common_problem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(HelpFeedback.this, CommonProblem.class);
				startActivity(intent);
				
			}
		});
		 
        back = (RelativeLayout) findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
        	
        });
		
	}
	
	
}


