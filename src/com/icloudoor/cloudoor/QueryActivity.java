package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.message.PushAgent;

public class QueryActivity extends BaseActivity {

	private RelativeLayout back;

	private SharedPreferences queryShare;
	private Editor queryEditor;

	private WebView surveyWebView;
	private String sid;
	private String url = UrlUtils.HOST + "/user/prop/zone/survey/page.do";

	private String HOST = UrlUtils.HOST;
	private String phonenum;

	private WebSettings webSetting;
	private Broadcast mFinishActivityBroadcast;
	
	boolean isDebug = DEBUG.isDebug;

	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";
	
	private Version version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		version = new Version(getApplicationContext());
		
		mFinishActivityBroadcast = new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		registerReceiver(mFinishActivityBroadcast, intentFilter);

		mKeyDBHelper = new MyDataBaseHelper(this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		    
		queryShare = getApplicationContext().getSharedPreferences("queryShare",
				0);
		queryEditor = queryShare.edit();

		sid = loadSid();

		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);

		PushAgent.getInstance(this).onAppStart();
		surveyWebView = (WebView) findViewById(R.id.id_survey);
		surveyWebView.addJavascriptInterface(new close(), "cloudoorNative");
		webSetting = surveyWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		if (queryShare.getString("QUERYURL", null) != null) {
			surveyWebView.loadUrl(HOST + queryShare.getString("QUERYURL", null)
					+ "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
			queryEditor.putString("QUERYURL", null).commit();
		} else {
			surveyWebView.loadUrl(url + "?sid=" + sid);
		}

		surveyWebView.setWebViewClient(new webViewClient());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				String metho = "backPagePop();";
				sb.append("javascript:").append(metho);
				surveyWebView.loadUrl(sb.toString());
			}

		});
		
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
			
			public void onProgressChanged(WebView view, int progress){
				loading();
				
				if(progress == 100)
					destroyDialog();
			}
			
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				destroyDialog();
			}
		};
		
		surveyWebView.setWebChromeClient(wcc);
	}

	class webViewClient extends WebViewClient { // override
												// shouldOverrideUrlLoading
												// method to use the webview to
												// response when click the link
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
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
	
	public class close {

		@JavascriptInterface
		public void closeWebBrowser() {
			QueryActivity.this.finish();
		}
		
		@JavascriptInterface
		public void logout() {
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", 0);
            Editor editor1 = loginStatus.edit();
            editor1.putInt("LOGIN", 0);
            editor1.commit();
            
            SharedPreferences previousNum = getSharedPreferences("PREVIOUSNUM", 0);
        	previousNum.edit().putString("NUM", loginStatus.getString("PHONENUM", null)).commit();
            
//            String sql = "DELETE FROM " + TABLE_NAME +";";
//            mKeyDB.execSQL(sql);
//            
//            String sq2 = "DELETE FROM " + CAR_TABLE_NAME +";";
//            mKeyDB.execSQL(sq2);
//            
//            String sq3 = "DELETE FROM " + ZONE_TABLE_NAME +";";
//            mKeyDB.execSQL(sq3);
            
            Intent intentKill = new Intent();
			intentKill.setAction("com.icloudoor.cloudoor.ACTION_FINISH");
			sendBroadcast(intentKill);
            
            Intent intent = new Intent();
            intent.setClass(QueryActivity.this, Login.class);
            startActivity(intent);
            
            QueryActivity.this.finish();
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
				intent.setClass(QueryActivity.this, VerifyGestureActivity.class);
				startActivity(intent);
			}
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
			QueryActivity.this.finish();
		}
		
	}
	
	
}
