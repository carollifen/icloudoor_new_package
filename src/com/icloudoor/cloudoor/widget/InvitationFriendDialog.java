package com.icloudoor.cloudoor.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.activity.RedActivity;
import com.icloudoor.cloudoor.chat.activity.VerificationFrientsActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class InvitationFriendDialog extends Dialog implements android.view.View.OnClickListener{
	
	private Window window = null;
	Context context;
	VerificationFrientsActivity activity;
	UMSocialService mController;
	private SnsPostListener mSnsPostListener;
	UMWXHandler wxHandler;
	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";
	public InvitationFriendDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.activity  = (VerificationFrientsActivity) context;
		this.context  = context;
	}
	public InvitationFriendDialog(Context context , int theme) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		this.activity  = (VerificationFrientsActivity) context;
		this.context  = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_invitation_friend);
		findViewById(R.id.weixin_share).setOnClickListener(this);
		findViewById(R.id.qq_share).setOnClickListener(this);
		mController = UMServiceFactory.getUMSocialService("myshare");
		wxHandler = new UMWXHandler(context, appID, appSecret);
		wxHandler.addToSocialSDK();
		
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1104648343","PEtfnVQR6M4L5Er2");
		qqSsoHandler.addToSocialSDK();  
		
		mSnsPostListener = new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1,
					SocializeEntity arg2) {
				if (arg1 == 200) {
					dismiss();
					System.out.println("share Success");
				}
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

		};
		windowDeploy();
		
	}
	
	
	 //ÉèÖÃ´°¿ÚÏÔÊ¾  
    public void windowDeploy(){  
        window = getWindow(); //µÃµ½¶Ô»°¿ò  
        window.setWindowAnimations(R.style.dialogWindowAnim); //ÉèÖÃ´°¿Úµ¯³ö¶¯»­  
        window.setBackgroundDrawableResource(android.R.color.transparent); //ÉèÖÃ¶Ô»°¿ò±³¾°ÎªÍ¸Ã÷  
        WindowManager.LayoutParams wl = window.getAttributes();  
        wl.gravity = Gravity.BOTTOM;
        wl.width = LayoutParams.MATCH_PARENT;
//        ¸ù¾Ýx£¬y×ø±êÉèÖÃ´°¿ÚÐèÒªÏÔÊ¾µÄÎ»ÖÃ  
//        wl.alpha = 0.6f; //ÉèÖÃÍ¸Ã÷¶È  
//        wl.gravity = Gravity.BOTTOM; //ÉèÖÃÖØÁ¦  
        window.setAttributes(wl);  
    }  
    
    
    public void init(String title){
    	
    	SharedPreferences preferences = context.getSharedPreferences("LOGINSTATUS", Context.MODE_PRIVATE);
    	
    	String content = context.getString(R.string.invitatiion1);
    	
    	WeiXinShareContent weiXinleMedia = new WeiXinShareContent();
		weiXinleMedia.setShareContent(content);
		weiXinleMedia.setTitle(title);
		weiXinleMedia.setShareImage(new UMImage(activity,R.drawable.logo_deep144));
		weiXinleMedia.setTargetUrl("http://www.icloudoor.com/d");
		mController.setShareMedia(weiXinleMedia);
		
		
		
		QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(content);
        qqShareContent.setTitle(title);
        qqShareContent.setShareMedia(new UMImage(activity,R.drawable.logo_deep144));
        qqShareContent.setTargetUrl("http://www.icloudoor.com/d");
        mController.setShareMedia(qqShareContent);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.weixin_share:
			init(context.getString(R.string.invitatiion2));
			mController.postShare(activity, SHARE_MEDIA.WEIXIN, mSnsPostListener);
			break;
		case R.id.qq_share:
			init(context.getString(R.string.invitatiion2));
			mController.postShare(activity, SHARE_MEDIA.QQ, mSnsPostListener);
			break;

		default:
			break;
		}
	}

}
