package com.icloudoor.cloudoor;

import java.util.List;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Config;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class NoOneActivity extends Activity {

	private String TAG = this.getClass().getSimpleName();
	
	private RelativeLayout shareLayout;
	private RelativeLayout dismiss;
	
	private SnsPostListener mSnsPostListener;
	
	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";

	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	
	boolean isDebug = DEBUG.isDebug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_no_one);
		
		
		
		dismiss = (RelativeLayout) findViewById(R.id.dismiss);
		shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
		
		dismiss.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
			
		});
		
		mSnsPostListener = new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				if(arg1 == 200){
					setResult(0);
					finish();
				}
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		// ���΢��ƽ̨
		wxHandler = new UMWXHandler(NoOneActivity.this, appID, appSecret);
		wxHandler.addToSocialSDK();
		// ���΢������Ȧ
		wxCircleHandler = new UMWXHandler(NoOneActivity.this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		
		mController.registerListener(mSnsPostListener);
		
		
//		CircleShareContent circleMedia = new CircleShareContent();
//		circleMedia.setShareContent("���Ǹ�����");
//		circleMedia.setTitle("���Ǹ�����");
//		circleMedia.setTargetUrl("http://www.icloudoor.com");
//		circleMedia.setShareImage(new UMImage(NoOneActivity.this, BitmapFactory.decodeStream(getResources().openRawResource(
//						R.raw.no1_share_pic))));

//		WeiXinShareContent weixinContent = new WeiXinShareContent();
//		weixinContent.setShareContent("���Ǹ�����");
//		weixinContent.setTitle("���Ǹ�����");
//		weixinContent.setTargetUrl("http://www.icloudoor.com");
//		weixinContent.setShareImage(new UMImage(NoOneActivity.this, BitmapFactory.decodeStream(getResources().openRawResource(
//				R.raw.no1_share_pic))));
		
		
//		mController.setShareMedia(circleMedia);
//		mController.setShareMedia(weixinContent);
//		
		mController.setShareMedia(new UMImage(NoOneActivity.this, BitmapFactory.decodeStream(getResources().openRawResource(
						R.raw.no1_share_pic))));
		mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
		
		shareLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO share

				mController.openShare(NoOneActivity.this, false);
			}
			
		});
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**ʹ��SSO��Ȩ����������´��� */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			setResult(0);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
