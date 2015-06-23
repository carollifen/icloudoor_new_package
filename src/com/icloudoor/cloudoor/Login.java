package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.icloudoor.cloudoor.chat.CommonUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements TextWatcher {
	
	private String TAG = this.getClass().getSimpleName();
	
	private EditText ETInputPhoneNum;
	private EditText ETInputPwd;
	private TextView TVLogin;
	private TextView TVFogetPwd;
	private TextView TVGoToRegi;
	private RelativeLayout ShowPwd;
	private ImageView IVPwdIcon;
    private ProgressBar pbLoginBar;
	private boolean isHiddenPwd = true;

	private URL loginURL;
	private RequestQueue mQueue;

	private String phoneNum, password;

	private String HOST = UrlUtils.HOST;

	private int loginStatusCode;

	private String sid;
	private int isLogin = 0;
	
	private int setPersonal;
	
	private String name = null;
	private String nickname = null;
	private String id = null;
	private String birth = null;
	private int sex = 0, provinceId = 0, cityId = 0, districtId = 0;
	private String portraitUrl, userId;
	private String province = null;
	private String city = null;
	private String district = null;
	private int userStatus;
	private boolean isHasPropServ;
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
	
	// for new ui
	private RelativeLayout phoneLayout;
	private RelativeLayout pwdLayout;
	private RelativeLayout loginLayout;

	boolean isDebug = DEBUG.isDebug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().hide();
		setContentView(R.layout.login);
        pbLoginBar = (ProgressBar) findViewById(R.id.loginBar);
        pbLoginBar.setVisibility(View.INVISIBLE);
		registerReceiver(mConnectionStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		setupUI(findViewById(R.id.main));
		
		mAreaDBHelper = new MyAreaDBHelper(Login.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();	
		
		mQueue = Volley.newRequestQueue(this);

		ETInputPhoneNum = (EditText) findViewById(R.id.login_input_phone_num);
		ETInputPwd = (EditText) findViewById(R.id.login_input_pwd);
		TVLogin = (TextView) findViewById(R.id.btn_login);
		TVFogetPwd = (TextView) findViewById(R.id.login_foget_pwd);
		TVGoToRegi = (TextView) findViewById(R.id.login_go_to_regi);
		ShowPwd = (RelativeLayout) findViewById(R.id.show_pwd);
		IVPwdIcon = (ImageView) findViewById(R.id.btn_show_pwd);
		IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
		
		// for new ui
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		
		phoneLayout = (RelativeLayout) findViewById(R.id.phone_input_layout);
		pwdLayout = (RelativeLayout) findViewById(R.id.pwd_input_layout);
		loginLayout = (RelativeLayout) findViewById(R.id.login_btn_layout);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) phoneLayout.getLayoutParams();
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) pwdLayout.getLayoutParams();
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) loginLayout.getLayoutParams();
		params.width = screenWidth - 48*2;
		params1.width = screenWidth - 48*2;
		params2.width = screenWidth - 48*2;
		phoneLayout.setLayoutParams(params);
		pwdLayout.setLayoutParams(params1);
		loginLayout.setLayoutParams(params2);
		
		phoneLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
		pwdLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
		loginLayout.setBackgroundResource(R.drawable.shape_login_btn_disable);
		
		ETInputPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					phoneLayout.setBackgroundResource(R.drawable.shape_login_input);
				}else{
					phoneLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
                    if (ETInputPhoneNum.getText().toString().length() != 11){
                        Toast.makeText(getApplicationContext(), R.string.error_phonenumb_over, Toast.LENGTH_SHORT).show();
                    }
				}
			}
			
		});
		ETInputPwd.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					pwdLayout.setBackgroundResource(R.drawable.shape_login_input);
				}else{
					pwdLayout.setBackgroundResource(R.drawable.shape_login_input_normal);
				}
			}
			
		});
		
		TVLogin.setTextColor(0xFFf3f3f3);
		loginLayout.setEnabled(false);
		
		ETInputPhoneNum.addTextChangedListener(this); 
		ETInputPwd.addTextChangedListener(this);
		
		sid = loadSid("SID");

		isHiddenPwd = true;
		IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
		ShowPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isHiddenPwd) {
					isHiddenPwd = false;
					ETInputPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.show_pwd_new);
				} else {
					isHiddenPwd = true;
					ETInputPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					IVPwdIcon.setImageResource(R.drawable.hide_pwd_new);
				}

			}

		});

		TVGoToRegi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), RegisterActivity.class);
				startActivity(intent);
			}

		});
		TVFogetPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ForgetPwdActivity.class);
				startActivityForResult(intent, 0);
			}

		});

		loginLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
                    pbLoginBar.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), R.string.login_ing,
							Toast.LENGTH_SHORT).show();

					try {
						loginURL = new URL(HOST + "/user/manage/login.do"
								+ "?sid=" + sid);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					phoneNum = ETInputPhoneNum.getText().toString();
					password = ETInputPwd.getText().toString();
					MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
							Method.POST, loginURL.toString(), null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try {
										if (response.getString("sid") != null) {
											sid = response.getString("sid");
											saveSid("SID", sid);
										}
										loginStatusCode = response
												.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									Log.e("TEST", response.toString());

                                    pbLoginBar.setVisibility(View.INVISIBLE);
									if (loginStatusCode == 1) {
										isLogin = 1;
										SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
										Editor editor = loginStatus.edit();
										editor.putInt("LOGIN", isLogin);
										editor.putString("PHONENUM", phoneNum);
										editor.putString("PASSWARD", password);
//										editor.commit();

										SharedPreferences firstLoginShare=getSharedPreferences("FIRSTLOGINSHARE", 0);
										Editor mEditor=firstLoginShare.edit();
										mEditor.putBoolean("FIRSTLOGIN", true).commit();
										try {
											JSONObject data = response.getJSONObject("data");
											JSONObject info = data.getJSONObject("info");

											name = info.getString("userName");
											nickname = info.getString("nickname");
											id = info.getString("idCardNo");
											birth = info.getString("birthday");
											sex = info.getInt("sex");
											provinceId = info.getInt("provinceId");
											cityId = info.getInt("cityId");
											districtId = info.getInt("districtId");
											isHasPropServ = info.getBoolean("isHasPropServ");

											portraitUrl = info.getString("portraitUrl");
											userId = info.getString("userId");
											userStatus = info.getInt("userStatus");     //1 for not approved user; 2 for approved user

											editor.putString("NAME", name);
											editor.putString("NICKNAME",nickname);
											editor.putString("ID", id);
											editor.putString("BIRTH", birth);
											editor.putInt("SEX", sex);
											editor.putInt("PROVINCE",provinceId);
											editor.putInt("CITY", cityId);
											editor.putInt("DIS", districtId);
											editor.putString("URL", portraitUrl);
											editor.putString("USERID", userId);
											editor.putInt("STATUS", userStatus);
											editor.putBoolean("isHasPropServ", isHasPropServ);
											editor.commit();
											
											if(provinceId != 0)
												province = getProvinceName(provinceId);
											
											if (cityId != 0) 
												city = getCityName(cityId);

											if (districtId != 0) 
												district = getDistrictName(districtId);
											
											SharedPreferences saveProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);
											Editor editor1 = saveProfile.edit();
											editor1.putString("NAME", name);
											editor1.putString("NICKNAME", nickname);
											editor1.putString("ID", id);
											editor1.putString("PROVINCE", province);
											editor1.putString("CITY", city);
											editor1.putString("DISTRICT", district);
											editor1.putInt("PROVINCEID", provinceId);
											editor1.putInt("CITYID", cityId);
											editor1.putInt("DISTRICTID", districtId);
											editor1.putInt("SEX", sex);
											if(birth.length() > 0){
												editor1.putString("YEAR", birth.substring(0, 4));
												editor1.putString("MONTH", birth.substring(5, 7));
												editor1.putString("DAY", birth.substring(8));
											}
											editor1.putBoolean("isHasPropServ", isHasPropServ);
											editor1.commit();
											//
											Intent intent = new Intent();

											SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
											setPersonal = personalInfo.getInt("SETINFO", 1);

											if (setPersonal == 0 || name.length() == 0 || sex == 0 || provinceId == 0 || cityId == 0 || districtId == 0 || birth.length() == 0 || id.length() == 0) {
												Log.e("jump to set", "in login activity");
												
												if(userStatus == 2) {
													intent.setClass(Login.this, SetPersonalInfo.class);
													startActivity(intent);
													finish();
												} else if(userStatus == 1) {
													intent.setClass(Login.this, SetPersonalInfoNotCerti.class);
													startActivity(intent);
													finish();
												}
													
											}

											if (setPersonal == 1) {
												login("fff", "111");//test username password
//												intent.setClass(Login.this, CloudDoorMainActivity.class);a
//												startActivity(intent);
//												finish();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}

										new Handler().postDelayed(
												new Runnable() {
													@Override
													public void run() {
														
													}
												}, 1000);

										SharedPreferences downPic = getSharedPreferences("DOWNPIC", 0);
										Editor editor1 = downPic.edit();
										editor1.putInt("PIC", 0);
										editor1.commit();
										
									} else if (loginStatusCode == -71) {
										Toast.makeText(getApplicationContext(),
												R.string.login_fail,
												Toast.LENGTH_SHORT).show();
									} else if(loginStatusCode == -99) {
    									Toast.makeText(getApplicationContext(),
    											R.string.unknown_err, Toast.LENGTH_SHORT)
    											.show();
    								}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
										Toast.makeText(Login.this, R.string.network_error, Toast.LENGTH_SHORT).show();
								}
							}) {
						@Override
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();
							map.put("mobile", phoneNum);
							map.put("password", password);
							return map;
						}
					};
					mQueue.add(mJsonRequest);
				} else {
					if (getApplicationContext() != null) {
						Toast.makeText(getApplicationContext(),
								R.string.no_network, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
     	Editor editor = RegiPhone.edit();
     	editor.putString("PHONE", "");
     	editor.commit();
	}
	
	
	
	boolean progressShow;
	public void login(final String currentUsername,final String currentPassword) {
		if (!CommonUtils.isNetWorkConnected(Login.this)) {
			Toast.makeText(Login.this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(Login.this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(Login.this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(Login.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		final long start = System.currentTimeMillis();
		// µ÷ÓÃsdkµÇÂ½·½·¨µÇÂ½ÁÄÌì·þÎñÆ÷
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				// µÇÂ½³É¹¦£¬±£´æÓÃ»§ÃûÃÜÂë
				cloudApplication.getInstance().setUserName(currentUsername);
				cloudApplication.getInstance().setPassword(currentPassword);

				System.out.println("µÇÂ¼³É¹¦....");
				Login.this.runOnUiThread(new Runnable() {
					public void run() {
						pd.setMessage(getString(R.string.list_is_for));
					}
				});
				try {
					// ** µÚÒ»´ÎµÇÂ¼»òÕßÖ®Ç°logoutºóÔÙµÇÂ¼£¬¼ÓÔØËùÓÐ±¾µØÈººÍ»Ø»°
					// ** manually load all local groups and
				    EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// ´¦ÀíºÃÓÑºÍÈº×é
//					processContactsAndGroups();
				} catch (Exception e) {
					e.printStackTrace();
					// È¡ºÃÓÑ»òÕßÈºÁÄÊ§°Ü£¬²»ÈÃ½øÈëÖ÷Ò³Ãæ
					Login.this.runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							cloudApplication.getInstance().logout(null);
							Toast.makeText(Login.this.getApplicationContext(), R.string.login_failure_failed, 1).show();
						}
					});
					return;
				}
				// ¸üÐÂµ±Ç°ÓÃ»§µÄnickname ´Ë·½·¨µÄ×÷ÓÃÊÇÔÚiosÀëÏßÍÆËÍÊ±ÄÜ¹»ÏÔÊ¾ÓÃ»§nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
						cloudApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				if (!Login.this.isFinishing())
					pd.dismiss();
				// ½øÈëÖ÷Ò³Ãæ
				finish();
				startActivity(new Intent(Login.this, CloudDoorMainActivity.class));
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				System.out.println("µÇÂ¼Ê§°Ü....");
				if (!progressShow) {
					return;
				}
				Login.this.runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(Login.this, getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
         if(requestCode == 0 && resultCode == RESULT_OK) {

            finish();
        }
    }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    ETInputPhoneNum.setText("");
	    ETInputPwd.setText("");
	    
		//
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			String phone = bundle.getString("phone");
			ETInputPhoneNum.setText(phone.toString());
			ETInputPwd.requestFocus();
		}
	    
	    SharedPreferences RegiPhone = getSharedPreferences("REGIPHONE", 0);
	    String phone = RegiPhone.getString("PHONE", "");
	    if(RegiPhone.getString("PHONE", "").length() > 0){
	    	ETInputPhoneNum.setText(RegiPhone.getString("PHONE", null));
	    	ETInputPwd.requestFocus();
	    	
	    	Editor editor = RegiPhone.edit();
	    	editor.putString("PHONE", "");
	    	editor.commit();
	    }
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectionStatusReceiver);
	}
	
	public void saveSid(String key, String value) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String loadSid(String key) {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString(key, null);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(ETInputPhoneNum.getText().toString().length() > 10 && ETInputPwd.getText().toString().length() > 5){
			TVLogin.setTextColor(0xFFffffff);
			loginLayout.setEnabled(true);
			loginLayout.setBackgroundResource(R.drawable.selector_login_in);
		} else {
			TVLogin.setTextColor(0xFFf3f3f3);
			loginLayout.setEnabled(false);
			loginLayout.setBackgroundResource(R.drawable.shape_login_btn_disable);
		}
	}
	
	public BroadcastReceiver mConnectionStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: This method is called when the BroadcastReceiver is
			// receiving
			// an Intent broadcast.

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable()) {
					saveSid("NETSTATE", "NET_WORKS");
					Log.i("NOTICE", "The Net is available!");
				}
				NetworkInfo.State state = connectivityManager.getNetworkInfo(
						connectivityManager.TYPE_MOBILE).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "GPRS is OK!");
					NetworkInfo mobNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				}
				state = connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "WIFI is OK!");
					NetworkInfo wifiNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				}
			} else {
				saveSid("NETSTATE", "NET_NOT_WORK");
			}
		}
	};
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(Login.this); 
					return false;
				}
			});
		}
		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}
	
	public String getProvinceName(int provinceId) {
		String provinceName = null;
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorP.moveToFirst()) {
			int provinceIndex = mCursorP.getColumnIndex("province_short_name");
			int provinceIdIndex = mCursorP.getColumnIndex("province_id");
			do{
				int tempPID = mCursorP.getInt(provinceIdIndex);
			    String tempPName = mCursorP.getString(provinceIndex);
				if(tempPID == provinceId){
					provinceName = tempPName;
					break;
				}		
			}while(mCursorP.moveToNext());		
		}
		mCursorP.close();
		return provinceName;
	}
	
	public String getCityName(int cityId) {
		String cityName = null;
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorC.moveToFirst()) {
			int cityIndex = mCursorC.getColumnIndex("city_short_name");
			int cityIdIndex = mCursorC.getColumnIndex("city_id");
			do{
				int tempCID = mCursorC.getInt(cityIdIndex);
			    String tempCName = mCursorC.getString(cityIndex);
				if(tempCID == cityId){
					cityName = tempCName;
					break;
				}		
			}while(mCursorC.moveToNext());		
		}
		mCursorC.close();
		return cityName;
	}
	
	public String getDistrictName(int districtId) {
		String districtName = null;
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorD.moveToFirst()) {
			int districtIndex = mCursorD.getColumnIndex("district_short_name");
			int districtIdIndex = mCursorD.getColumnIndex("district_id");
			do{
				int tempDID = mCursorD.getInt(districtIdIndex);
			    String tempDName = mCursorD.getString(districtIndex);
				if(tempDID == districtId){
					districtName = tempDName;
					break;
				}		
			}while(mCursorD.moveToNext());		
		}
		mCursorD.close();
		return districtName;
	}
}