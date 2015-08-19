package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

public class SettingDetailActivity extends BaseActivity {
	private LinearLayout TVBtnResetPwd;
	
	private ImageView IVSetDetailShake;
	private ImageView IVSetDetailSound;
	private ImageView IVSetDetailDisturb;
	
	private LinearLayout setGesture;
	
	private RelativeLayout IVBack;
	private Broadcast mFinishActivityBroadcast;
	private LinearLayout logout;
	private LinearLayout ChooseCarMan;
	
	private int canShake, haveSound, canDisturb, switchToCar;
	private MyBtnOnClickListener mMyBtnOnClickListener;
	
	boolean isDebug = DEBUG.isDebug;
	private String sid = null;
	private String HOST = UrlUtils.HOST;
	private URL logOutURL;
	private int isLogin = 1;
	private LogoutPop menuWindow;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_detail);
		mFinishActivityBroadcast=	new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		registerReceiver(mFinishActivityBroadcast, intentFilter);
		
		TVBtnResetPwd = (LinearLayout) findViewById(R.id.btn_reset_pwd);
		
		IVSetDetailShake = (ImageView) findViewById(R.id.btn_set_detail_shake);
		IVSetDetailSound = (ImageView) findViewById(R.id.btn_set_detail_sound);
		IVSetDetailDisturb = (ImageView) findViewById(R.id.btn_set_detail_disturb);
		IVBack = (RelativeLayout) findViewById(R.id.btn_back_set_detail);
		ChooseCarMan = (LinearLayout) findViewById(R.id.btn_swich_car_man);
		logout = (LinearLayout) findViewById(R.id.btn_logout);

		setGesture = (LinearLayout) findViewById(R.id.btn_set_gesture);
		setGesture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingDetailActivity.this, SignActivity.class);
				startActivity(intent);
			}
		});
		
		mMyBtnOnClickListener = new MyBtnOnClickListener();
		
		TVBtnResetPwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ResetPwdActivity.class);
				startActivity(intent);
			}
			
		});

		IVBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//to save the setting detail
				SharedPreferences setting = getSharedPreferences("SETTING",
						MODE_PRIVATE);
				Editor editor = setting.edit();
//				editor.putInt("chooseCar", switchToCar);
				editor.putInt("disturb", canDisturb);
				editor.putInt("sound", haveSound);
				editor.putInt("shake", canShake);
				editor.commit();
				finish();		
			}
			
		});
		
		ChooseCarMan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingDetailActivity.this, ChooseCarMan.class);
				startActivity(intent);
			}
		});
		
		logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				menuWindow = new LogoutPop(SettingDetailActivity.this, itemsOnClick); 
				menuWindow.showAtLocation(SettingDetailActivity.this.findViewById(R.id.set_detail), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		        }
			}
			
		});
		
		InitBtns();
		IVSetDetailShake.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailSound.setOnClickListener(mMyBtnOnClickListener);
		IVSetDetailDisturb.setOnClickListener(mMyBtnOnClickListener);

	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
	}
	
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.exit:
				
				if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
					loading();
                    sid = loadSid("SID");

                    try {
                        logOutURL = new URL(HOST + "/user/manage/logout.do" + "?sid=" + sid + "&ver=" + version.getVersionName());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
                            Method.POST, logOutURL.toString(), null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                    	
                                    	if(response.getInt("code") == 1) {
                                    		saveSid("SID", null);
                                    		
                                    		isLogin = 0;
                                            final SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
                                            Editor editor1 = loginStatus.edit();
                                            editor1.putInt("LOGIN", isLogin);
                                            editor1.commit();
                                            	
                                            SharedPreferences previousNum = getSharedPreferences("PREVIOUSNUM", 0);
                                            previousNum.edit().putString("NUM", loginStatus.getString("PHONENUM", null)).commit();
                                            
                                            SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
                            				Editor editor = savedUrl.edit();
                            				editor.putString("Url", " ").commit();
                            				
                            				 EMChatManager.getInstance().logout(new EMCallBack() {
												
												@Override
												public void onSuccess() {
													// TODO Auto-generated method stub
													Intent intent3 = new Intent();
		                                            Bundle bundle = new Bundle();
		                                            bundle.putString("phone", loginStatus.getString("PHONENUM", ""));
		                                            intent3.putExtras(bundle);
		                                            intent3.setClass(SettingDetailActivity.this, Login.class);
		                                            startActivity(intent3);
												}
												
												@Override
												public void onProgress(int arg0, String arg1) {
													// TODO Auto-generated method stub
													
												}
												
												@Override
												public void onError(int arg0, String arg1) {
													// TODO Auto-generated method stub
													
												}
											});
                                            
                                            
                                            CloudDoorMainActivity.instance.finish();

                                            destroyDialog();
                                            
                                            finish();
                                    	}
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
    							Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    mQueue.add(mJsonRequest);
                }else {
                    	SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
                        Editor editor1 = loginStatus.edit();
                        editor1.putInt("LOGIN", 0);
                        editor1.commit();
                        
                        saveSid("SID", null);
                        
                        SharedPreferences previousNum = getSharedPreferences("PREVIOUSNUM", 0);
                    	previousNum.edit().putString("NUM", loginStatus.getString("PHONENUM", null)).commit();
                    	
                    	SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
        				Editor editor = savedUrl.edit();
        				editor.putString("Url", " ").commit();
        				
                        Intent intent4 = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("phone", loginStatus.getString("PHONENUM", ""));
                        intent4.putExtras(bundle);
                        intent4.setClass(SettingDetailActivity.this, Login.class);
                        startActivity(intent4);
                        
                        CloudDoorMainActivity.instance.finish();
              
                        finish();
                    
                }
				
				break;
			case R.id.cancle_logout:
				menuWindow.dismiss();
				break;
				
			}
		}

	};	
	
	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);
		if (homePressed == 1 && useSign == 1) {
			if(System.currentTimeMillis() - homeKeyEvent.getLong("TIME", 0) > 60 * 1000){
				Intent intent = new Intent();
				intent.setClass(SettingDetailActivity.this, VerifyGestureActivity.class);
				startActivity(intent);
			}
		}
	}
	
	public void InitBtns(){
		SharedPreferences setting = getSharedPreferences("SETTING", 0);		
		canShake = setting.getInt("shake", 1);
		haveSound = setting.getInt("sound", 1);
		canDisturb = setting.getInt("disturb", 0);
		switchToCar = setting.getInt("chooseCar", 1);
		
		if(canShake == 1)
			IVSetDetailShake.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailShake.setImageResource(R.drawable.btn_no);
		
		if(haveSound == 1)
			IVSetDetailSound.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailSound.setImageResource(R.drawable.btn_no);

		if(canDisturb == 1)
			IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
		else
			IVSetDetailDisturb.setImageResource(R.drawable.btn_no);
	
	}
	
	public class MyBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			SharedPreferences setting = getSharedPreferences("SETTING", 0);
			Editor editor = setting.edit();
			switch(v.getId()){
			case R.id.btn_set_detail_shake:
				if(canShake == 1){
					IVSetDetailShake.setImageResource(R.drawable.btn_no);
					canShake = 0;
					editor.putInt("shake", canShake);
				}else{
					IVSetDetailShake.setImageResource(R.drawable.btn_yes);
					canShake = 1;
					editor.putInt("shake", canShake);
				}
				break;
			case R.id.btn_set_detail_sound:
				if(haveSound == 1){
					IVSetDetailSound.setImageResource(R.drawable.btn_no);
					haveSound = 0;
					editor.putInt("sound", haveSound);
				}else{
					IVSetDetailSound.setImageResource(R.drawable.btn_yes);
					haveSound = 1;
					editor.putInt("sound", haveSound);
				}
				break;
			case R.id.btn_set_detail_disturb:
				if(canDisturb == 1){
					IVSetDetailDisturb.setImageResource(R.drawable.btn_no);
					canDisturb = 0;
					editor.putInt("disturb", canDisturb);
				}else{
					IVSetDetailDisturb.setImageResource(R.drawable.btn_yes);
					canDisturb = 1;
					editor.putInt("disturb", canDisturb);
				}
				break;
			}
			editor.commit();
		}
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFinishActivityBroadcast);
		
	}
	
	class Broadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			SettingDetailActivity.this.finish();
		}
		
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
            //do something here
            SharedPreferences setting = getSharedPreferences("SETTING", MODE_PRIVATE);
            Editor editor = setting.edit();
            editor.putInt("chooseCar", switchToCar);
            editor.putInt("disturb", canDisturb);
            editor.putInt("sound", haveSound);
            editor.putInt("shake", canShake);
            editor.commit();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	public void saveSid(String key, String value) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID", MODE_PRIVATE);
			Editor editor = savedSid.edit();
			editor.putString(key, value);
			editor.commit();
	}

	public String loadSid(String key) {
		SharedPreferences loadSid = getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString(key, null);
	}
	
}