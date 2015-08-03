package com.icloudoor.cloudoor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import android.R.color;
import android.R.integer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SetSex extends BaseActivity {

	private String TAG = this.getClass().getSimpleName();

	private LinearLayout back;
	private LinearLayout SaveSex;

	private RelativeLayout ChooseMale;
	private RelativeLayout ChooseFemale;

	private ImageView ChooseMaleImage;
	private ImageView ChooseFemaleImage;
	
	private String sid;
	private String HOST = UrlUtils.HOST;
	private URL updateUrl;
	
	private int sex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_sex);

		back = (LinearLayout) findViewById(R.id.btn_back_from_set_sex);
		SaveSex = (LinearLayout) findViewById(R.id.save_sex);

		ChooseMale = (RelativeLayout) findViewById(R.id.choose_male);
		ChooseFemale = (RelativeLayout) findViewById(R.id.choose_female);

		ChooseMaleImage = (ImageView) findViewById(R.id.choose_male_image);
		ChooseFemaleImage = (ImageView) findViewById(R.id.choose_female_image);

		if (getIntent().getStringExtra("textsex").equals("ÄÐ")) {
			ChooseMaleImage.setImageResource(R.drawable.confirm);
			sex = 1;
			Log.e(TAG, String.valueOf(sex));
		}else {
			sex = 2;
			Log.e(TAG, String.valueOf(sex));
			ChooseFemaleImage.setImageResource(R.drawable.confirm);
		}
		
		
		ChooseMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChooseMaleImage.setImageResource(R.drawable.confirm);
				ChooseFemaleImage.setImageDrawable(null);
				sex = 1;
			}
		});

		ChooseFemale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChooseFemaleImage.setImageResource(R.drawable.confirm);
				ChooseMaleImage.setImageDrawable(null);
				sex  = 2;
			}
		});

		SaveSex.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateSex();
			}
		});
		
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}
//  updatesex
	public void UpdateSex() {
		sid = loadSid();
		mQueue = Volley.newRequestQueue(this);
		try {
			updateUrl = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MyJsonObjectRequest mJsonObjectRequest = new MyJsonObjectRequest(
				Method.POST, updateUrl.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override 
					public void onResponse(JSONObject response) {
						
						
						Log.e("####################3", response.toString());
						
						try {
							if (response.getString("sid") != null) {
								sid = response.getString("sid");
								saveSid(sid);
							}
							
							if(response.getInt("code") == 1) {
								Intent intent = new Intent(SetSex.this, ShowPersonalInfo.class);
								intent.putExtra("sex", sex);
								setResult(RESULT_OK, intent);
								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						

					};
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}){

			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("sex",String.valueOf(sex));
				Log.e(TAG, String.valueOf(sex));
				return map;
			}
		};

		mQueue.add(mJsonObjectRequest);
	}
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}


}
