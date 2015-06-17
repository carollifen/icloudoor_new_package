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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class NoOtherActivity extends BaseActivity {
	
	private String TAG = this.getClass().getSimpleName();
	
	private RelativeLayout shareLayout;
	private RelativeLayout dismiss;
	
	private ImageView bgImage;
	private Bitmap bgBitmap1, bgBitmap2;
	
	private String beatRatio;
	
	private Bitmap shareBitmap1, shareBitmap2;
	
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
		setContentView(R.layout.activity_no_other);
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		beatRatio = bundle.getString("beatRatio");
		Log.e(TAG, "beatRatio: " + beatRatio);
		
		dismiss = (RelativeLayout) findViewById(R.id.dismiss);
		shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
		
		bgImage = (ImageView) findViewById(R.id.bg);
		
		bgBitmap1 = drawTextToBitmap(this, R.drawable.no_other_empty_pic, getString(R.string.daka_first_string) + beatRatio + getString(R.string.daka_first_second));
		bgBitmap2 = drawTextToBitmap2(this, bgBitmap1, getString(R.string.what_i_rank));
		
		bgImage.setImageBitmap(bgBitmap2);
		
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

		// 添加微信平台
		wxHandler = new UMWXHandler(NoOtherActivity.this, appID, appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		wxCircleHandler = new UMWXHandler(NoOtherActivity.this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		shareBitmap1 = drawTextToBitmap(this, R.drawable.no_other_share_empty_pic, getString(R.string.daka_first_string) + beatRatio + getString(R.string.daka_first_second));
		shareBitmap2 = drawTextToBitmap2(this, shareBitmap1, getString(R.string.what_i_rank));
		
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		
		mController.registerListener(mSnsPostListener);
		
		mController.setShareMedia(new UMImage(NoOtherActivity.this, shareBitmap2));
		mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);		
		
		shareLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mController.openShare(NoOtherActivity.this, false);
			}
			
		});
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	public Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		int screenWidth = dm.widthPixels;
		
		float ratioWidth = (float)screenWidth / 480;
		float ratioHeight = (float)screenHeight / 800;

		Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.parseColor("#fff7d9"));
		// text size in pixels
		float RATIO = Math.min(ratioWidth, ratioHeight);
		paint.setTextSize(36*RATIO);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		if(screenHeight <= 870){
			canvas.drawText(gText, x, screenHeight * 8 / 10, paint);
		} else {
			canvas.drawText(gText, x, screenHeight * 7 / 10, paint);
		}

		return bitmap;
	}
	
	public Bitmap drawTextToBitmap2(Context gContext, Bitmap bitmap, String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		int screenWidth = dm.widthPixels;
		
		float ratioWidth = (float)screenWidth / 480;
		float ratioHeight = (float)screenHeight / 800;
		

		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.parseColor("#fff7d9"));
		// text size in pixels
		
		float RATIO = Math.min(ratioWidth, ratioHeight); 
		
		paint.setTextSize(36*RATIO);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 5;

		if(screenHeight <= 870){
			canvas.drawText(gText, x, screenHeight * 8 / 10 + 40, paint);
		} else {
			canvas.drawText(gText, x, screenHeight * 8 / 10 - 40, paint);
		}

		return bitmap;
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
