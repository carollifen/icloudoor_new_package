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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BillActivity extends BaseActivity {

	private RelativeLayout back;
	private WebView billWebView;
	private String sid;
	private String url = UrlUtils.HOST + "/user/prop/zone/payment/bill.do";
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
		setContentView(R.layout.activity_bill);
		
		mFinishActivityBroadcast = new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		registerReceiver(mFinishActivityBroadcast, intentFilter);

		version = new Version(getApplicationContext());
		
		mKeyDBHelper = new MyDataBaseHelper(this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		sid = loadSid();
		
		billWebView = (WebView) findViewById(R.id.id_costlist);
		webSetting = billWebView.getSettings();

		webSetting.setUseWideViewPort(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportZoom(false);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setBuiltInZoomControls(true);

		billWebView.addJavascriptInterface(new autoLogout(), "cloudoorNative");
		
		billWebView.loadUrl(url + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		
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
		
		billWebView.setWebChromeClient(wcc);
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getApplicationContext().getSharedPreferences("SAVEDSID",
				0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}
	

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
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
				intent.setClass(BillActivity.this, VerifyGestureActivity.class);
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
			BillActivity.this.finish();
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
            intent.setClass(BillActivity.this, Login.class);
            startActivity(intent);
            
            BillActivity.this.finish();
		}
	}
}
