package com.icloudoor.cloudoor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;


public class CommonProblem extends BaseActivity {
	
	private RelativeLayout back;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_problem);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
        });
        
        String url = UrlUtils.HOST + "/user/help/faq.do" + "?sid=" + loadSid() + "&ver="
		+ version.getVersionName() + "&imei=" + version.getDeviceId();
        webView = (WebView) findViewById(R.id.common_problem_webview);
        webView.loadUrl(url);
        
        WebChromeClient wcc = new WebChromeClient(){
			@Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                
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
		
		webView.setWebChromeClient(wcc);
	}

}
