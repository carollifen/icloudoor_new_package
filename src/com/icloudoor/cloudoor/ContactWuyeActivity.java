package com.icloudoor.cloudoor;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactWuyeActivity extends BaseActivity {

	private WebView webview;
	private RelativeLayout back;
	
	private String sid;
	private URL newurl;
	private String url = UrlUtils.HOST + "/user/prop/zone/contact/page.do";
	private RequestQueue requestQueue;
	
	private String phonenum;
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
//		getActionBar().hide();
		setContentView(R.layout.activity_contact_wuye);

		mFinishActivityBroadcast = new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");
		registerReceiver(mFinishActivityBroadcast, intentFilter);

		version = new Version(getApplicationContext());
		
		mKeyDBHelper = new MyDataBaseHelper(this, DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		    
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		final TextView Title = (TextView) findViewById(R.id.page_title);
		
		webview = (WebView) findViewById(R.id.webview);

		sid = loadSid();

		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new Contact(), "cloudoorNative");
		webview.loadUrl(url + "?sid=" + sid + "&ver=" + version.getVersionName());

		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {

			}

			@Override
			public void onProgressChanged(WebView view, int progress) {

			}

		});
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
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
		};
		webview.setWebChromeClient(wcc);
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

	public class Contact {

		@JavascriptInterface
		public void callout(final String phone) {
			runOnUiThread(new Runnable() {
				public void run() {
					JSONObject jsObj;
					try {
						jsObj = new JSONObject(phone);
						String phonenum = jsObj.getString("phoneNum");
						startActivity(new Intent(Intent.ACTION_DIAL,
								Uri.parse("tel:" + phonenum)));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

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
            intent.setClass(ContactWuyeActivity.this, Login.class);
            startActivity(intent);
            
            ContactWuyeActivity.this.finish();
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
				intent.setClass(ContactWuyeActivity.this, VerifyGestureActivity.class);
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
			ContactWuyeActivity.this.finish();
		}
		
	}
	
	
}
