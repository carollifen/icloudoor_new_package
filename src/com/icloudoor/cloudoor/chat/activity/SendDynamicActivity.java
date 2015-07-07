package com.icloudoor.cloudoor.chat.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.fireking.app.imagelib.entity.ImageBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Entities.FilePart;
import com.icloudoor.cloudoor.Entities.MultipartEntity;
import com.icloudoor.cloudoor.Entities.Part;
import com.icloudoor.cloudoor.Entities.StringPart;
import com.icloudoor.cloudoor.Entities.ZonesInfo;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.SendDynamicAdapter;
import com.icloudoor.cloudoor.widget.ZonesDialog;

public class SendDynamicActivity extends BaseActivity implements
		OnClickListener ,NetworkInterface{

	ImageView btn_back;
	EditText content_edit;
	TextView right_send;
	TextView zones_name;
	GridView dynamic;
	SendDynamicAdapter adapter;
	LinearLayout wholook_layout;
	private String resultForup = UrlUtils.HOST
			+ "/user/file/getSignatureAndPolicy.do";
	
	MyAsyncTask asyncTask;
	int count;
	List<String> urlData;
	String subject;
	String l1ZoneName;
	String l1ZoneId;
	JSONArray overall ;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_send_dynamic);
//		subject = getIntent().getExtras().getString("subject");
		subject = "小区趣事";
		dynamic = (GridView) findViewById(R.id.dynamic);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		content_edit = (EditText) findViewById(R.id.content_edit);
		right_send = (TextView) findViewById(R.id.right_send);
		zones_name = (TextView) findViewById(R.id.zones_name);
		wholook_layout = (LinearLayout) findViewById(R.id.wholook_layout);
		wholook_layout.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		right_send.setOnClickListener(this);
		adapter = new SendDynamicAdapter(this);
		dynamic.setAdapter(adapter);
		dialog = new ZonesDialog(this, R.style.QRCode_dialog);
		
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
		String l1Zones = loginStatus.getString("l1Zones", "");
		try {
			JSONArray array = new JSONArray(l1Zones);
			if(array!=null && array.length()>0){
				JSONObject object = (JSONObject) array.get(0);
				l1ZoneName = object.getString("l1ZoneName");
				l1ZoneId = object.getString("l1ZoneId");
				zones_name.setText(l1ZoneName);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	ZonesDialog dialog;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.wholook_layout:
			dialog.show();
			dialog.setonItem(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					ZonesInfo info = dialog.Changed(position);
					zones_name.setText(info.getL1ZoneName());
					l1ZoneId = info.getL1ZoneId();
				}
			});
			break;
		case R.id.right_send:
			count=0;
			overall = new JSONArray();
			urlData = adapter.getList();
			if(urlData==null||urlData.size()==0){
				sendDynamic(null);
			}else{
				for (int i = 0; i < urlData.size(); i++) {
					System.out.println("urlData.get(i) = "+urlData.get(i));
					getNetWork(urlData.get(i));
				}
			}
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x123 && resultCode == RESULT_OK) {
			Intent intent = data;
			List<ImageBean> images = (List<ImageBean>) intent
					.getSerializableExtra("images");
			for (ImageBean b : images) {
				System.out.println("<><<><> ???" + b.toString());
				adapter.addPath(b.path);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	public void sendDynamic(JSONArray array){
		JSONObject parm = new JSONObject();
		String content = content_edit.getText().toString();
		if(array==null&&TextUtils.isEmpty(content)){
			showToast(R.string.not_content);
			return;
		}
		try {
			parm.put("subject", subject);
			parm.put("l1ZoneId", l1ZoneId);
			if(!TextUtils.isEmpty(content)){
				parm.put("content", content);
			}
			if(array!=null){
				parm.put("photoUrls", array);
			}else{
				loading();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getNetworkData(this , "/user/im/act/add.do", parm.toString(), false);
	}

	public void getNetWork(final String imgPath) {
		loading();
		JsonObjectRequest upRequest = new JsonObjectRequest(resultForup
				+ "?sid=" + loadSid() + "&type=" + "41" + "&ext=" + "jpeg"
				+ "&ver=" + version.getVersionName(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject obj) {

						try {
							if (obj.getString("sid") != null) {
								System.out.println("提交："+obj);
								JSONObject UPjsa = obj.getJSONObject("data");
								String upPolicy = UPjsa.getString("policy");
								String upSignature = UPjsa.getString("signature");
								String upsubmitUrl = UPjsa.getString("submitUrl");
								String upphotoUrl = UPjsa.getString("photoUrl");
								overall.put(upphotoUrl);
								count++;
								if(count==urlData.size()){
									sendDynamic(overall);
								}
								asyncTask = new MyAsyncTask();
								asyncTask.execute(upPolicy,upSignature,upsubmitUrl,upphotoUrl,imgPath);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.getMessage();
					}
				});

		mQueue.add(upRequest);

	}

	
	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			System.out.println("onPreExecute");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("onPostExecute");

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			System.out.println("onProgressUpdate");
		}

		@Override
		protected String doInBackground(String... params) {

			System.out.println("doInBackground");
			String upPolicy = params[0];
			String upSignature = params[1];
			String upsubmitUrl = params[2];
			String upphotoUrl = params[3];
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(upsubmitUrl);
			File file = new  File(params[4]);
			JSONObject fromUPjson;
//			if (URLUtil.isFileUrl(mList.get(0).getAbsolutePath())) {
//				file = new File(URI.create(mList.get(0).getAbsolutePath()));
//			} else {
//				file = new File(mList.get(0).getAbsolutePath());
//			}

			// MultipartEntity myMul=new MultipartEntity();

			Part[] parts = null;
			FilePart photoPart;
			try {
				photoPart = new FilePart("file", file);
				StringPart policyPart = new StringPart("policy", upPolicy);
				StringPart signaturePart = new StringPart("signature",
						upSignature);
				parts = new Part[] { policyPart, signaturePart, photoPart };
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			postRequest.setEntity(new MultipartEntity(parts));
			HttpResponse response;

			// try {
			try {
				response = httpClient.execute(postRequest);

				String jsonString = EntityUtils.toString(response.getEntity());
				fromUPjson = new JSONObject(jsonString);
				
				String postToServer = fromUPjson.getString("url");
				String upcode = fromUPjson.getString("code");
				System.out.println("postToServer = "+postToServer+"  upcode = "+upcode);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}


	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			int code = response.getInt("code");
			if(code==1){
				finish();
				showToast(R.string.send_dunamic_success);
			}else{
				showToast(R.string.send_dunamic_Fail);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}

}
