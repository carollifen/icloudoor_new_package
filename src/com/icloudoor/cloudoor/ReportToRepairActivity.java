package com.icloudoor.cloudoor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.Entities.FilePart;
import com.icloudoor.cloudoor.Entities.MultipartEntity;
import com.icloudoor.cloudoor.Entities.Part;
import com.icloudoor.cloudoor.Entities.StringPart;
import com.umeng.message.PushAgent;

public class ReportToRepairActivity extends Activity {

	private String TAG = this.getClass().getSimpleName();

	private RelativeLayout back;

	private String callback;
	private WebView fixwebview;
	private String sid;
	private URL newurl;
	private String url = UrlUtils.HOST + "/user/prop/zone/rr/add.do";
	private String resultForup = UrlUtils.HOST + "/user/file/getSignatureAndPolicy.do";

	JsonObjectRequest upRequest;

	private String UrltoServer;
	private String phonenum;
	private RequestQueue requestQueue;

	private List<File> mList;

	private JSONObject UPjsa;

	private JSONObject policyjson;
	private JSONObject signaturejson;
	private JSONObject bucketjson;

	private String upPolicy;
	private String upSignature;
	private String upsubmitUrl;
	private String upphotoUrl;
	private String upcode;

	private MyHandler mhandler;

	private String imageUrl;

	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int PICTURE_REQUEST_CODE = 2;

	private JSONObject fromUPjson;

	private String postToServer;

	private WebSettings webSetting;
	private Broadcast mFinishActivityBroadcast;

	boolean isDebug = DEBUG.isDebug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_to_repair);
		
		mFinishActivityBroadcast=	new Broadcast();
		 IntentFilter intentFilter = new IntentFilter();
		    intentFilter.addAction("com.icloudoor.clouddoor.ACTION_FINISH");
		    registerReceiver(mFinishActivityBroadcast, intentFilter);



		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				String metho = "backPagePop();";
				sb.append("javascript:").append(metho);
				fixwebview.loadUrl(sb.toString());
			}

		});

		PushAgent.getInstance(this).onAppStart();
		fixwebview = (WebView) findViewById(R.id.repair_webview);
		webSetting = fixwebview.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		sid = loadSid();

		fixwebview.addJavascriptInterface(new Camera(), "cloudoorNative");
		fixwebview.loadUrl(url + "?sid=" + sid);
		sid = loadSid();
		
		WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Title.setText(title);
			}
			
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				// TODO Auto-generated method stub
				if (consoleMessage.message().contains("backPagePop is not defined")) {
					finish();
				}
				return super.onConsoleMessage(consoleMessage);
			}
		};
		
		fixwebview.setWebChromeClient(wcc);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAMERA_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Bitmap bitmap = data.getParcelableExtra("data");
			FixPictrueFileUtil.getInstance().saveBitmap(bitmap);

			Log.e(TAG, "����");

			mList = new ArrayList<File>();
			String url = Environment.getExternalStorageDirectory().toString()
					+ "/Cloudoor/FixImage";
			File albumdir = new File(url);
			File[] imgfile = albumdir.listFiles(filefiter);
			int len = imgfile.length;
			for (int i = 0; i < len; i++) {
				mList.add(imgfile[i]);
			}
			Log.e(TAG, "��ȡ" + "dsijkl");

			// Collections.sort(mList, new FileComparator());

			Log.e(TAG, "��ȡ" + "����");

			MyAsyncTask myAsyncTask = new MyAsyncTask();
			myAsyncTask.execute(upsubmitUrl);

		}

	}

	private FileFilter filefiter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			String tmp = pathname.getName().toLowerCase();

			if (tmp.endsWith(".png") || tmp.endsWith(".jpg")
					|| tmp.endsWith(".jpeg")) {
				return true;
			}
			return false;
		}

	};

	private class FileComparator implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.lastModified() < rhs.lastModified()) {
				return 1;
			} else
				return -1;
		}

	};

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			requestQueue = Volley.newRequestQueue(ReportToRepairActivity.this);
			upRequest = new JsonObjectRequest(resultForup + "?sid=" + loadSid()
					+ "&type=" + "1" + "&ext=" + "jpeg", null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject obj) {

							Log.e("TEst StringBuilder", obj.toString());
							try {
								if (obj.getString("sid") != null) {
									sid = obj.getString("sid");
									saveSid(sid);
									UPjsa = obj.getJSONObject("data");
									upPolicy = UPjsa.getString("policy");
									upSignature = UPjsa.getString("signature");
									upsubmitUrl = UPjsa.getString("submitUrl");
									upphotoUrl = UPjsa.getString("photoUrl");
									Log.e(TAG, upPolicy);
									Log.e(TAG, upSignature);
									Log.e(TAG, upsubmitUrl);
									Log.e(TAG, upphotoUrl);
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

			requestQueue.add(upRequest);

		}

	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Log.e(TAG, "���ǲ��Բ���");
			StringBuilder sb = new StringBuilder();
			sb.append("javascript:").append(callback).append('(').append(0)
					.append(",").append("'").append(upphotoUrl).append("');");
			fixwebview.loadUrl(sb.toString());
			Log.e("string dsklfjkl", sb.toString());
			upcode = "0";

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(params[0]);

			File file = null;
			if (URLUtil.isFileUrl(mList.get(0).getAbsolutePath())) {
				file = new File(URI.create(mList.get(0).getAbsolutePath()));
			} else {
				file = new File(mList.get(0).getAbsolutePath());
			}

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
				postToServer = fromUPjson.getString("url");
				upcode = fromUPjson.getString("code");

				Log.e(TAG, upcode);
				Log.e("TEst StringBuilder", postToServer);
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

	public void saveSid(String sid) {
		SharedPreferences savedSid = getApplicationContext()
				.getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);

	}

	
	public class Camera {
		
		
		@JavascriptInterface
		public void closeWebBrowser()
		{	
			ReportToRepairActivity.this.finish();
		}
		
		@JavascriptInterface
		public void takePhoto(final String str) {

			runOnUiThread(new Runnable() {
				public void run() {
					try {
						mhandler = new MyHandler();
						mhandler.sendEmptyMessage(0);
						Log.e("webview", str + "sdyiufoi");
						JSONObject jsObj = new JSONObject(str);
						Log.e("jsjsjjsjs", jsObj.getString("callback"));
						callback = jsObj.getString("callback");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivityForResult(new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE), 1);

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
			Intent intent = new Intent();
			intent.setClass(ReportToRepairActivity.this, VerifyGestureActivity.class);
			startActivity(intent);
		}	}
	
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
			ReportToRepairActivity.this.finish();
		}
		
	}
	
}
