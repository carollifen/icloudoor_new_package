package com.icloudoor.cloudoor;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LateActivity extends BaseActivity {
	
	private RelativeLayout shareLayout;
	private RelativeLayout dismiss;
	
	private SnsPostListener mSnsPostListener;
	
	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";

	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	
	boolean isDebug = DEBUG.isDebug;
	
	private SharePopupWindow shareWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_late);
		
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


		wxHandler = new UMWXHandler(LateActivity.this, appID, appSecret);
		wxHandler.addToSocialSDK();

		wxCircleHandler = new UMWXHandler(LateActivity.this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		
		mController.registerListener(mSnsPostListener);
		
		mController.setShareMedia(new UMImage(LateActivity.this, BitmapFactory.decodeStream(getResources().openRawResource(R.raw.late_share_pic))));
//		mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);			
		
		shareLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				shareWindow = new SharePopupWindow(LateActivity.this, itemsOnClick);
				shareWindow.showAtLocation(LateActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//				mController.openShare(LateActivity.this, false);
			}
			
		});
	}
	
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			shareWindow.dismiss();
			switch (v.getId()) {
			case R.id.weixin_layout:
				shareWindow.dismiss();
				mController.postShare(LateActivity.this, SHARE_MEDIA.WEIXIN, mSnsPostListener);
				break;
			case R.id.weixin_circle_layout:
				shareWindow.dismiss();
				mController.postShare(LateActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, mSnsPostListener);
				break;
			}
		}
		
	};

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mController.unregisterListener(mSnsPostListener);
	}
}
