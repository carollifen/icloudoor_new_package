package com.icloudoor.cloudoor.activity;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.widget.ShareRedDialog;
import com.icloudoor.cloudoor.widget.ShareRedDialog.OnDismissListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class RedActivity extends BaseActivity implements OnClickListener{

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
	String callback ;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_red);
		clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
		dialog = new ShareRedDialog(this, R.style.mydialog);
		webView = (WebView) findViewById(R.id.webView);
		webView.setFocusable(true);
		webView.requestFocus();
		// webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

		webView.setBackgroundResource(R.color.transparent);// transparent是定义在color里的颜色值，可以为黑色
		webView.setBackgroundColor(0);// 以下这两行代码就是设置透明了
		webView.getBackground().setAlpha(150);// 这个是设置透明度
		dialog.setOnDismiss(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				System.out.println("*****javascript:"+callback+"(2)");
				runOnUiThread(new Runnable() {
					public void run() {
						webView.loadUrl("javascript:"+callback+"("+2+")");
					}
				});
			}
		});
		// 设置webview：跳转、开始加载、加载完成、加载失败的逻辑
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
				// Log.d(MYTAG, " onReceivedError ");
				destroyDialog();
			}
		});

		webSettings = webView.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		String url = UrlUtils.HOST + "/user/activity/rp/my.do" + "?sid="
				+ loadSid() + "&ver=" + version.getVersionName();
		System.out.println("url = " + url);
		webView.addJavascriptInterface(new ObjectForJS(), "cloudoorNative");
		webView.loadUrl(url);
		share();

	}

	public void share() {

		wxHandler = new UMWXHandler(this, appID, appSecret);
		wxHandler.addToSocialSDK();
		wxCircleHandler = new UMWXHandler(this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		mSnsPostListener = new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1,
					SocializeEntity arg2) {
				if (arg1 == 200) {
					System.out.println("share Success");
				}
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

		};

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//		mController.registerListener(mSnsPostListener);
//		mController.getConfig().removePlatform(SHARE_MEDIA.SINA,
//				SHARE_MEDIA.TENCENT);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		mController.unregisterListener(mSnsPostListener);
//		ssoHandler.isClientInstalled
		UMSsoHandler ssoHandler = null;
		
	}

	private class ObjectForJS {

		@JavascriptInterface
		public void closeWebBrowser() {
			finish();
		}

		@JavascriptInterface
		public void snsShare(String parameterJSONStr) {
			
			dialog.show();
			dialog.setClickListener(RedActivity.this);
			dialog.windowDeploy();
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(parameterJSONStr);
				String imgUrl = jsonObject.getString("imgUrl"); // 图片链接
				String linkUrl = jsonObject.getString("linkUrl"); // 用户点击后的跳转链接
				String title = jsonObject.getString("title"); // 标题
				String description = jsonObject.getString("description"); // 描述
				callback = jsonObject.getString("callback"); // 回调方法
				
				
				
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent(description);
				circleMedia.setTitle(title);
				circleMedia
						.setShareImage(new UMImage(RedActivity.this, imgUrl));
				circleMedia.setTargetUrl(linkUrl);
				
				mController.setShareMedia(circleMedia);
				
				
				WeiXinShareContent weiXinleMedia = new WeiXinShareContent();
				weiXinleMedia.setShareContent(description);
				weiXinleMedia.setTitle(title);
				weiXinleMedia.setShareImage(new UMImage(RedActivity.this,
						imgUrl));
				weiXinleMedia.setTargetUrl(linkUrl);
				mController.setShareMedia(weiXinleMedia);
				
//				SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
//				mController.openShare(RedActivity.this, false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		@JavascriptInterface
		public void copyToClipboard(String parameterJSONStr){
			try {
				JSONObject object = new JSONObject(parameterJSONStr);
				String text = object.getString("text");
				clip.setText(text); // 复制
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.weixin_layout:
			mController.postShare(this, SHARE_MEDIA.WEIXIN, mSnsPostListener);
			break;
		case R.id.weixin_circle_layout:
			mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, mSnsPostListener);
			break;

		default:
			break;
		}
	}
}
