package com.icloudoor.cloudoor.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.widget.ShareRedDialog;
import com.icloudoor.cloudoor.widget.ShareRedDialog.OnDismissListener;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class UserStatusActivity extends BaseActivity implements OnClickListener{

	
	private WebView webView;
	private WebSettings webSettings;

	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";
	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	private SnsPostListener mSnsPostListener;
	ShareRedDialog dialog;
	ClipboardManager clip;
	String callback;
	ImageView close_red;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_red);
		close_red = (ImageView) findViewById(R.id.close_red);
		close_red.setOnClickListener(this);
		clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		dialog = new ShareRedDialog(this, R.style.mydialog);
		webView = (WebView) findViewById(R.id.webView);
		webView.setFocusable(true);
		webView.requestFocus();
		webView.cancelLongPress();
		webView.setBackgroundResource(R.color.transparent);
		webView.setBackgroundColor(0);
		webView.getBackground().setAlpha(150);
		dialog.setOnDismiss(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						close_red.setVisibility(View.VISIBLE);
//						webView.loadUrl("javascript:" + callback + "(" + 1
//								+ ")");
					}
				});

			}
		});
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				loading();
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				destroyDialog();
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				destroyDialog();
			}
		});

		webSettings = webView.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		String url = UrlUtils.HOST + "/user/auth/request.do" + "?sid="
				+ loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId();
		System.out.println("url = " + url);
		webView.addJavascriptInterface(new ObjectForJS(), "cloudoorNative");
		webView.loadUrl(url);

	}


	String imgUrl;
	String linkUrl;
	String title;
	String description;

	

	@Override
	protected void onPause() {
		super.onPause();
		UMSsoHandler ssoHandler = null;

	}

	CircleShareContent circleMedia;
	WeiXinShareContent weiXinleMedia;

	private class ObjectForJS {

		

		@JavascriptInterface
		public void takePhoto(String parameterJSONStr) {
			
		}

	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.close_red:
			finish();
			break;

		default:
			break;
		}
	}
	
}
