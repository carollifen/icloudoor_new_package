package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SignActivity extends BaseActivity{
	private ImageView IvSignSwitch;
	private RelativeLayout back;
	private RelativeLayout changeSign;
	private RelativeLayout forgetSign;
	private int useSign;
	private int haveSet;
	
	LayoutInflater inflater;
	private int COLOROLD=0xFF000000;
	private int COLORNEW=0xFFF3F3F3;
	
	private RelativeLayout mlayout;
	
	private Broadcast mFinishActivityBroadcast;
	
	boolean isDebug = DEBUG.isDebug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_detail_set_sign);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);



		
		mlayout=(RelativeLayout) findViewById(R.id.forget_layout);
		inflater = LayoutInflater.from(this);
		
		SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
		haveSet = setSign.getInt("HAVESETSIGN", 0);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);

		IvSignSwitch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(useSign == 1){
					IvSignSwitch.setImageResource(R.drawable.btn_no);
					useSign = 0;
					
					SharedPreferences setting = getSharedPreferences("SETTING",
							MODE_PRIVATE);
					Editor editor = setting.edit();
					editor.putInt("useSign", useSign);
					editor.commit();
				}else{
					if(haveSet == 0) {
						Intent intent = new Intent();
						intent.setClass(SignActivity.this, SetGestureActivity.class);
						startActivityForResult(intent, 0);
					}else {
                        IvSignSwitch.setImageResource(R.drawable.btn_yes);
                        useSign = 1;
                        SharedPreferences setting = getSharedPreferences("SETTING", MODE_PRIVATE);
                        Editor editor = setting.edit();
                        editor.putInt("useSign", useSign);
                        editor.commit();
                    }
				}
			}
			
		});
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == RESULT_OK) {
			MyDebugLog.e("Test Sign", "onActivityResult");
			setContentView(R.layout.set_detail_set_sign_now);

			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);

			if(useSign == 1)
				IvSignSwitch.setImageResource(R.drawable.btn_yes);
			else
				IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (useSign == 1) {
						IvSignSwitch.setImageResource(R.drawable.btn_no);
						useSign = 0;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();
					} else {
                        if(haveSet == 1) {
                            IvSignSwitch.setImageResource(R.drawable.btn_yes);
                            useSign = 1;
                            SharedPreferences setting = getSharedPreferences("SETTING", MODE_PRIVATE);
                            Editor editor = setting.edit();
                            editor.putInt("useSign", useSign);
                            editor.commit();
                        }
					}
				}

			});
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}

			});
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences Sign = getSharedPreferences("SETTING", 0);
		int usesign = Sign.getInt("useSign", 0);

		if (homePressed == 1 && usesign == 1) {
			if(System.currentTimeMillis() - homeKeyEvent.getLong("TIME", 0) > 60 * 1000){
				Intent intent = new Intent();
				intent.setClass(SignActivity.this, VerifyGestureActivity.class);
				startActivity(intent);
			}
		}	
		
		Log.e("Test Sign", "onResume");

		
		SharedPreferences setSign = getSharedPreferences("SETSIGN", 0);
		haveSet = setSign.getInt("HAVESETSIGN", 0);
		
		if(haveSet == 1){
			setContentView(R.layout.set_detail_set_sign_now);

			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
			changeSign = (RelativeLayout) findViewById(R.id.btn_change_sign);
			forgetSign = (RelativeLayout) findViewById(R.id.btn_forget_sign);

            SharedPreferences setting = getSharedPreferences("SETTING", 0);
            useSign = setting.getInt("useSign", 0);
            if(useSign == 1)
                IvSignSwitch.setImageResource(R.drawable.btn_yes);
            else
                IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (useSign == 1) {
						IvSignSwitch.setImageResource(R.drawable.btn_no);
						useSign = 0;

						SharedPreferences setting = getSharedPreferences(
								"SETTING", MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();
					} else {
                        if(haveSet == 1) {
                            IvSignSwitch.setImageResource(R.drawable.btn_yes);
                            useSign = 1;
                            SharedPreferences setting = getSharedPreferences("SETTING", MODE_PRIVATE);
                            Editor editor = setting.edit();
                            editor.putInt("useSign", useSign);
                            editor.commit();
                        }
					}
				}

			});
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}

			});
			
			changeSign.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					MyDebugLog.e("TEsT", "changeSign clicked!");
					Intent confirmIntent=new Intent(SignActivity.this,ConfirmGestureActivity.class);
					startActivity(confirmIntent);
				}
				
			});
			
			
			
			forgetSign.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					}
					 
					return false;
				}
			});
			
			
			
			forgetSign.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyDebugLog.e("TEsT", "forgetSign clicked!");
					
					
					MyDialog myDialog = new MyDialog(SignActivity.this,R.style.add_dialog, getString(R.string.login_pwd),
							new MyDialog.OnCustomDialogListener() {

								@Override
								public void back(int haveset) {
									SignActivity.this.haveSet = haveset;
									SharedPreferences setSign = getSharedPreferences(
											"SETSIGN", 0);
									Editor editor = setSign.edit();
									editor.putInt("HAVESETSIGN",
											SignActivity.this.haveSet);
									editor.commit();
									MyDebugLog.e("forgetOncreate",
											String.valueOf(haveSet));
									useSign = 0;
									SharedPreferences setting = getSharedPreferences(
											"SETTING", MODE_PRIVATE);
									Editor useSigneditor = setting.edit();
									useSigneditor.putInt("useSign", useSign);
									useSigneditor.commit();

									setContentView(R.layout.set_detail_set_sign);
									back = (RelativeLayout) findViewById(R.id.btn_back);
									IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);

									useSign = setting.getInt("useSign", 0);
									if (useSign == 1)
										IvSignSwitch
												.setImageResource(R.drawable.btn_yes);
									else
										IvSignSwitch
												.setImageResource(R.drawable.btn_no);
									IvSignSwitch
											.setOnClickListener(new android.view.View.OnClickListener() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													if (useSign == 1) {
														IvSignSwitch
																.setImageResource(R.drawable.btn_no);
														useSign = 0;

														SharedPreferences setting = getSharedPreferences(
																"SETTING",
																MODE_PRIVATE);
														Editor editor = setting
																.edit();
														editor.putInt(
																"useSign",
																useSign);
														editor.commit();
													} else {
														if (haveSet == 0) {
															Intent intent = new Intent();
															intent.setClass(
																	SignActivity.this,
																	SetGestureActivity.class);
															startActivity(intent);
														}else {
                                                            IvSignSwitch.setImageResource(R.drawable.btn_yes);
                                                            useSign = 1;
                                                            SharedPreferences setting = getSharedPreferences(
                                                                    "SETTING",
                                                                    MODE_PRIVATE);
                                                            Editor editor = setting.edit();
                                                            editor.putInt("useSign", useSign);
                                                            editor.commit();
                                                        }
													}
												}

											});

									back.setOnClickListener(new android.view.View.OnClickListener() {

										@Override
										public void onClick(View v) {
											finish();
										}

									});

								}
							});
					myDialog.show();
				}

			});
			
		
		}else if(haveSet==0) {
			setContentView(R.layout.set_detail_set_sign);
			back = (RelativeLayout) findViewById(R.id.btn_back);
			IvSignSwitch = (ImageView) findViewById(R.id.btn_sign_switch);
			
			SharedPreferences setting = getSharedPreferences(
					"SETTING", MODE_PRIVATE);
			useSign = setting.getInt("useSign", 0);
			if(useSign == 1)
				IvSignSwitch.setImageResource(R.drawable.btn_yes);
			else
				IvSignSwitch.setImageResource(R.drawable.btn_no);
			IvSignSwitch.setOnClickListener(new android.view.View.OnClickListener(){
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(useSign == 1){
						IvSignSwitch.setImageResource(R.drawable.btn_no);
						useSign = 0;
						
						SharedPreferences setting = getSharedPreferences("SETTING",
								MODE_PRIVATE);
						Editor editor = setting.edit();
						editor.putInt("useSign", useSign);
						editor.commit();
					}else{

						if(haveSet == 0	) 
						{
							Intent intent = new Intent();
							intent.setClass(SignActivity.this, SetGestureActivity.class);
							startActivity(intent);
						}else {
                            IvSignSwitch.setImageResource(R.drawable.btn_yes);
                            useSign = 1;

                            SharedPreferences setting = getSharedPreferences("SETTING",
                                    MODE_PRIVATE);
                            Editor editor = setting.edit();
                            editor.putInt("useSign", useSign);
                            editor.commit();

                        }
					}
				}
				
			});
			
			back.setOnClickListener(new android.view.View.OnClickListener(){

				@Override
				public void onClick(View v) {
					finish();
				}
				
			});
			
			
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
			SignActivity.this.finish();
		}
		
	}
	
	
}