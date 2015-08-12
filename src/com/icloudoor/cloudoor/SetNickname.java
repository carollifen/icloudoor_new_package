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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetNickname extends BaseActivity {

	private String TAG = this.getClass().getSimpleName();
	private EditText EditNickname;
	private LinearLayout SaveNickname;
	private ImageView emptyname;
	private RelativeLayout back;

	private String HOST = UrlUtils.HOST;
	private URL updateUrl;

	private String sid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_nickname);

		back = (RelativeLayout) findViewById(R.id.btn_back_show_personal_info);
		SaveNickname = (LinearLayout) findViewById(R.id.savenickname);
		EditNickname = (EditText) findViewById(R.id.editNickname);
		emptyname = (ImageView) findViewById(R.id.delete_nickname);
	
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
		if(loginStatus.getString("NICKNAME", null).length() > 0) {
			EditNickname.setText(loginStatus.getString("NICKNAME", null));
			EditNickname.setSelection(EditNickname.getText().length());
		}

		SaveNickname.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (EditNickname.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), R.string.input_cannot_empty, Toast.LENGTH_SHORT).show();
				} else {
					UpdateNickname();
					loading();
				}
				
			}
		});

		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		emptyname.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditNickname.setText("");
			}
		});


	}

	public void UpdateNickname() {
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
						destroyDialog();
						try {	
							if(response.getInt("code") == 1) {
								if (response.getString("sid") != null) {
									sid = response.getString("sid");
									saveSid(sid);
								}
								
								SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								Editor editor = loginStatus.edit();
								editor.putString("NICKNAME", EditNickname.getText().toString()).commit();

								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						

					};
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						destroyDialog();
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}){

			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("nickname", EditNickname.getText().toString());
				return map;
			}
		};

		mQueue.add(mJsonObjectRequest);
	}
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID", MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}
}
