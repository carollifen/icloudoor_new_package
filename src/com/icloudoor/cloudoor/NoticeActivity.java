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
import android.view.KeyEvent;
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

public class NoticeActivity extends BaseActivity {

	private String TAG = this.getClass().getSimpleName();
	
	private SharedPreferences noticeUrlShare;
	private Editor noticeUrlEditor;

	private WebView anouncePageWebView;
	private RelativeLayout back;

	private String sid;
	private String str;
	private WebSettings anouncewebSetting;

	private WebView anounceDetailWebView;
	private String pageurl = UrlUtils.HOST + "/user/prop/zone/notice/page.do";
	private String HOST = UrlUtils.HOST;
	private String detailurl = UrlUtils.HOST + "/user/prop/zone/notice/detail.do";
	
	private Broadcast mFinishActivityBroadcast;

	boolean isDebug = DEBUG.isDebug;
	
	private Version version;
	
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		noticeUrlShare = getApplicationContext().getSharedPreferences("noticeUrlShare", 0);
		noticeUrlEditor = noticeUrlShare.edit();
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		version = new Version(getApplicationContext());
		
		mFinishActivityBroadcast=	new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		registerReceiver(mFinishActivityBroadcast, intentFilter);

		mKeyDBHelper = new MyDataBaseHelper(this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();


		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);

		PushAgent.getInstance(this).onAppStart();
		anouncePageWebView = (WebView) findViewById(R.id.id_public_anounce_page);

		anouncewebSetting = anouncePageWebView.getSettings();

		anouncewebSetting.setUseWideViewPort(true);
		anouncewebSetting.setJavaScriptEnabled(true);
		anouncewebSetting.setLoadWithOverviewMode(true);
		anouncewebSetting.setSupportZoom(false);
		anouncewebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		anouncewebSetting.setLoadsImagesAutomatically(true);
		anouncewebSetting.setBuiltInZoomControls(true);

		anouncewebSetting.setCacheMode(anouncewebSetting.LOAD_NO_CACHE);
		
		anouncePageWebView.setWebViewClient(new webViewClient());

		sid = loadSid();
		
		anouncePageWebView.addJavascriptInterface(new autoLogout(), "cloudoorNative");

		if (noticeUrlShare.getString("NOTICEURL", null) != null) {

			anouncePageWebView.loadUrl(HOST + noticeUrlShare.getString("NOTICEURL", null) + "&sid=" + sid);
			noticeUrlEditor.putString("NOTICEURL", null).commit();

		} else {
			anouncePageWebView.loadUrl(pageurl + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		}

		anouncePageWebView.setWebViewClient(new webViewClient());
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (anouncePageWebView.canGoBack())
					anouncePageWebView.goBack();
				else
					finish();
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
		anouncePageWebView.setWebChromeClient(wcc);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && anouncePageWebView.canGoBack()) {
			 anouncePageWebView.goBack();
			 return true;
			}
				
		return super.onKeyDown(keyCode, event);
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
				intent.setClass(NoticeActivity.this, VerifyGestureActivity.class);
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
			NoticeActivity.this.finish();
		}
		
	}
	
	public class autoLogout {
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
            intent.setClass(NoticeActivity.this, Login.class);
            startActivity(intent);
            
            NoticeActivity.this.finish();
		}
	}
}
