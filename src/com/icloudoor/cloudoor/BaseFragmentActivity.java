package com.icloudoor.cloudoor;

import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class BaseFragmentActivity extends FragmentActivity{
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
