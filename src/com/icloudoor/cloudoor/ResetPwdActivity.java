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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPwdActivity extends BaseActivity implements TextWatcher {
	private EditText ETInputOldPwd;
	private EditText ETInputNewPwd;
	private EditText ETConfirmNewPwd;
	private LinearLayout TVResetDone;
	private LinearLayout IVBack;
	private URL resetPwdURL;
	private RequestQueue mQueue;
	private String oldPwd, newPwd, confirmPwd;

	private int statusCode;
	private String HOST = UrlUtils.HOST;
	private String sid = null;
	
	boolean isDebug = DEBUG.isDebug;
	
	private Version version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_detail_reset_pwd);

		version = new Version(getApplicationContext());
		
		setupUI(findViewById(R.id.main));
		
		mQueue = Volley.newRequestQueue(this);

		IVBack = (LinearLayout) findViewById(R.id.btn_back_reset_pwd);
		ETInputOldPwd = (EditText) findViewById(R.id.input_old_pwd);
		ETInputNewPwd = (EditText) findViewById(R.id.input_new_pwd);
		ETConfirmNewPwd = (EditText) findViewById(R.id.confirm_new_pwd);
		TVResetDone = (LinearLayout) findViewById(R.id.reset_pwd_done);
		
		TVResetDone.setEnabled(false);
		
		ETInputOldPwd.addTextChangedListener(this);
		ETInputNewPwd.addTextChangedListener(this);
		ETConfirmNewPwd.addTextChangedListener(this);
		
		sid = loadSid();

		IVBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		TVResetDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					resetPwdURL = new URL(HOST + "/user/manage/changePassword.do" + "?sid=" + sid + "&ver=" + version.getVersionName());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				oldPwd = ETInputOldPwd.getText().toString();
				newPwd = ETInputNewPwd.getText().toString();
				confirmPwd = ETConfirmNewPwd.getText().toString();
				if (newPwd.equals(confirmPwd)) {
					MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
							Method.POST, resetPwdURL.toString(), null,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try {
										statusCode = response.getInt("code");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
									if (statusCode == 1) {
										try {
											if (response.getString("sid") != null) {
												sid = response.getString("sid");
												saveSid(sid);
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
										Toast.makeText(getApplicationContext(), R.string.forget_success, Toast.LENGTH_SHORT).show();
										
										finish();
									}else if (statusCode == -41) {
										Toast.makeText(getApplicationContext(), R.string.weak_pwd, Toast.LENGTH_SHORT).show();
									}else if (statusCode == -51) {
										Toast.makeText(getApplicationContext(), R.string.wrong_old_pwd, Toast.LENGTH_SHORT).show();
									}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
								}
							}) {
						@Override
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();
							map.put("oldPassword", oldPwd);
							map.put("newPassword", confirmPwd);
							return map;
						}
					};
					mQueue.add(mJsonRequest);
				} else {
					Toast.makeText(v.getContext(), R.string.diff_pwd,
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);

		if (homePressed == 1 && useSign == 1) {
			Intent intent = new Intent();
			intent.setClass(ResetPwdActivity.this, VerifyGestureActivity.class);
			startActivity(intent);
		}
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
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
		
		String temp = s.toString();
		
		if(temp.length() > 1){
			String tem = temp.substring(temp.length()-1, temp.length());
			char[] temC = tem.toCharArray();
			int mid = temC[0];
			
			if(mid >= 33 && mid <= 126){
				
			}else{
				s.delete(temp.length()-1, temp.length());
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT).show();
			}
		}else if(temp.length() == 1){
			char[] temC = temp.toCharArray();
			int mid = temC[0];
			
			if(mid >= 33 && mid <= 126){
				
			}else{
				s.clear();
				Toast.makeText(this, R.string.input_wrong, Toast.LENGTH_SHORT).show();
			}
		}
		
		if(ETInputNewPwd.getText().toString().length() > 5 && ETConfirmNewPwd.getText().toString().length() > 5 && ETInputOldPwd.getText().toString().length() > 5){
			TVResetDone.setEnabled(true);
		} else {
			TVResetDone.setEnabled(false);
		}
	}	
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(ResetPwdActivity.this); 
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
}