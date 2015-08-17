package com.icloudoor.cloudoor;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdWebViewActivity extends Activity {

	private RelativeLayout back;
	
	private WebView mAdWebView;
	private String mAdLink;
	
	TextView Title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_web_view);
		
		mAdLink = getIntent().getExtras().getString("webUrl");
		mAdWebView = (WebView) findViewById(R.id.ad_webview);

		mAdWebView.loadUrl(mAdLink);
		
		mAdWebView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            view.loadUrl(url);
	            return true;
	        }
	       });
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAdWebView.canGoBack())
					mAdWebView.goBack();
				else
					finish();
			}

		});
		
		Title = (TextView) findViewById(R.id.page_title);		
		WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Title.setText(title);
			}

		};
		
		mAdWebView.setWebChromeClient(wcc);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && mAdWebView.canGoBack()) {
			 mAdWebView.goBack();
			 return true;
			}
				
		return super.onKeyDown(keyCode, event);
	}

}
