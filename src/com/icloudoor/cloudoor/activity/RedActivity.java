package com.icloudoor.cloudoor.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class RedActivity extends BaseActivity {

	private WebView webView;
	private WebSettings webSettings;

	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";
	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	private SnsPostListener mSnsPostListener;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_red);

		webView = (WebView) findViewById(R.id.webView);
		webView.setFocusable(true);
		webView.requestFocus();
		// webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

		webView.setBackgroundResource(R.color.transparent);// transparent是定义在color里的颜色值，可以为黑色
		webView.setBackgroundColor(0);// 以下这两行代码就是设置透明了
		webView.getBackground().setAlpha(150);// 这个是设置透明度

		webSettings = webView.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		String url = UrlUtils.HOST + "/user/activity/rp/my.do" + "?sid="
				+ loadSid() + "&ver=" + version.getVersionName();
		System.out.println("url = "+url);
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
		mController.registerListener(mSnsPostListener);

		mController.getConfig().removePlatform(SHARE_MEDIA.SINA,
				SHARE_MEDIA.TENCENT);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mController.unregisterListener(mSnsPostListener);
	}

	private class ObjectForJS {

		@JavascriptInterface
		public void closeWebBrowser() {
			finish();
		}

		@JavascriptInterface
		public void snsShare(String parameterJSONStr) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(parameterJSONStr);
				String imgUrl = jsonObject.getString("imgUrl"); // 图片链接
				String linkUrl = jsonObject.getString("linkUrl"); // 用户点击后的跳转链接
				String title = jsonObject.getString("title"); // 标题
				String description = jsonObject.getString("description"); // 描述
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent(description);
				circleMedia.setTitle(title);
				circleMedia.setShareImage(new UMImage(RedActivity.this, imgUrl));
				circleMedia.setTargetUrl(linkUrl);
				mController.setShareMedia(circleMedia);
				WeiXinShareContent weiXinleMedia = new WeiXinShareContent();
				weiXinleMedia.setShareContent(description);
				weiXinleMedia.setTitle(title);
				weiXinleMedia.setShareImage(new UMImage(RedActivity.this, imgUrl));
				weiXinleMedia.setTargetUrl(linkUrl);
				mController.setShareMedia(weiXinleMedia);
				mController.openShare(RedActivity.this, false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
