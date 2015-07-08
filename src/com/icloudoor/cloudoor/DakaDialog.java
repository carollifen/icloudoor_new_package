package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DakaDialog extends BaseActivity {
	
	private String TAG = this.getClass().getSimpleName();
	
	private RelativeLayout dismiss;
	private TextView dakaTime;
	private TextView gowork;
	private TextView offwork;
	
	private String url = UrlUtils.HOST + "/user/api/sign.do";
	private URL requestURL;
	private RequestQueue mQueue;
	private String sid;
	private String TYPE_GOWORK = "1";
	private String TYPE_OFFWORK = "2";
	private String doorId;
	
	// only for gowork
	private int rank;
	private boolean isLate;
	private String beatRatio;
	
	boolean isDebug = DEBUG.isDebug;
	
	SimpleDateFormat formatter;
	Date curTime;
	String time = null;
	
	private Version version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.dakadialog_layout);
		
		version = new Version(getApplicationContext());
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		doorId = bundle.getString("doorIdToOpen");
		MyDebugLog.e(TAG, "doorId for daka: " + doorId);
		
		dismiss = (RelativeLayout) findViewById(R.id.dismiss);
		dakaTime = (TextView) findViewById(R.id.daka_time);
		gowork = (TextView) findViewById(R.id.gowork);
		offwork = (TextView) findViewById(R.id.offwork);
		
		
		
		dismiss.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setResult(2);
				finish();
			}
			
		});
		
		mQueue = Volley.newRequestQueue(this);
		
		getNowTime();
		
		
		
		gowork.setBackgroundResource(R.drawable.selector_gowork_bg);
		gowork.setTextColor(getResources().getColorStateList(R.color.selector_daka_text));
		gowork.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sid = loadSid();
				try {
					requestURL = new URL(url + "?sid=" + sid + "&ver=" + version.getVersionName());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, requestURL.toString(), null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									
									MyDebugLog.e(TAG, response.toString());
									
									if(response.getInt("code") == 1){
										if(response.getString("sid") != null)
											saveSid(response.getString("sid"));
										
										JSONObject data = response.getJSONObject("data");
										rank = data.getInt("rank");
										isLate = data.getBoolean("isLate");
										beatRatio = data.getString("beatRatio");
										
										MyDebugLog.e(TAG, "rank: " + String.valueOf(rank));
										MyDebugLog.e(TAG, "isLate: " + isLate);
										MyDebugLog.e(TAG, "beatRatio: " + beatRatio);
										
										Toast.makeText(DakaDialog.this, R.string.daka_success, Toast.LENGTH_SHORT).show();
										
										Intent intent = new Intent();
										
										if (!isLate){
											if (rank == 1) {
												intent.setClass(DakaDialog.this, NoOneActivity.class);
											} else if (rank == 2) {
												intent.setClass(DakaDialog.this, NoTwoActivity.class);
											} else if (rank == 3) {
												intent.setClass(DakaDialog.this, NoThreeActivity.class);
											} else {
												Bundle bundle = new Bundle();
												bundle.putString("beatRatio", beatRatio);
												intent.putExtras(bundle);
												intent.setClass(DakaDialog.this, NoOtherActivity.class);
											}
										} else {
											intent.setClass(DakaDialog.this, LateActivity.class);
										}
										
										startActivityForResult(intent, 0);
										
									}else if(response.getInt("code") == -91) {
										Toast.makeText(DakaDialog.this, R.string.have_already_daka, Toast.LENGTH_SHORT).show();
										
										setResult(2);
										DakaDialog.this.finish();
									}else if(response.getInt("code") == -99) {
										Toast.makeText(DakaDialog.this, R.string.unknown_err, Toast.LENGTH_SHORT).show();
									}else if(response.getInt("code") == -1) {
										Toast.makeText(DakaDialog.this, R.string.wrong_params, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
									
							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("doorId", doorId);
						map.put("type", TYPE_GOWORK);
						return map;
					}
				};
				mQueue.add(mJsonRequest);
			}
			
		});
		
		offwork.setBackgroundResource(R.drawable.selector_offwork_bg);
		offwork.setTextColor(getResources().getColorStateList(R.color.selector_daka_text));
		offwork.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sid = loadSid();
				try {
					requestURL = new URL(url + "?sid=" + sid + "&ver=" + version.getVersionName());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
						Method.POST, requestURL.toString(), null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									
									MyDebugLog.e(TAG, response.toString());
									
									if(response.getInt("code") == 1){
										if(response.getString("sid") != null)
											saveSid(response.getString("sid"));
										
										Toast.makeText(DakaDialog.this, R.string.daka_success, Toast.LENGTH_SHORT).show();
										
										Intent intent = new Intent();
										intent.setClass(DakaDialog.this, OffworkActivity.class);
										startActivityForResult(intent, 0);
									}else if(response.getInt("code") == -91) {
										Toast.makeText(DakaDialog.this, R.string.have_already_daka, Toast.LENGTH_SHORT).show();
										
										setResult(2);
										DakaDialog.this.finish();
									}else if(response.getInt("code") == -99) {
										Toast.makeText(DakaDialog.this, R.string.unknown_err, Toast.LENGTH_SHORT).show();
									}else if(response.getInt("code") == -1) {
										Toast.makeText(DakaDialog.this, R.string.wrong_params, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
									
							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("doorId", doorId);
						map.put("type", TYPE_OFFWORK);
						return map;
					}
				};
				mQueue.add(mJsonRequest);
			}
			
		});
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 0){
			setResult(2);
			DakaDialog.this.finish();
		}
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			setResult(2);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void getNowTime(){
		sid = loadSid();
		URL getTime = null;
		
		try {
			getTime = new URL(UrlUtils.HOST + "/user/utils/time.do" + "?sid=" + sid + "&ver=" + version.getVersionName());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, getTime.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if(response.getInt("code") == 1){
								
								if(response.getString("sid") != null)
									saveSid(response.getString("sid"));
								
								formatter = new SimpleDateFormat("HH:mm");
								curTime = new Date(response.getLong("data"));
								time = formatter.format(curTime);
								dakaTime.setText(getString(R.string.daka_info1) + time+ getString(R.string.daka_info2));
								Log.e(TAG, time);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
							
					}
				}
		);
		mQueue.add(mJsonRequest);
	}
}