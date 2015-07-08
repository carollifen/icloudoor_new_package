package com.icloudoor.cloudoor;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.easemob.EMCallBack;
import com.icloudoor.cloudoor.chat.DemoHXSDKHelper;
import com.icloudoor.cloudoor.chat.User;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class cloudApplication extends Application {
	private PushAgent mPushAgent;
	private SharedPreferences noticeUrlShare;
	private Editor noticeUrlEditor;

	private SharedPreferences queryShare;
	private Editor queryEditor;

	public static final String CALLBACK_RECEIVER_ACTION = "callback_receiver_action";
	public static IUmengRegisterCallback mRegisterCallback;
	public static IUmengUnregisterCallback mUnregisterCallback;
	private static cloudApplication instance;
	public static Context applicationContext;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	public static String currentUserNick = "";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		applicationContext = this;
		hxSDKHelper.onInit(applicationContext);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
		    @Override
		    public void dealWithCustomAction(Context context, UMessage msg) {
		    	
		    	noticeUrlShare=context.getSharedPreferences("noticeUrlShare", 0);
		    	noticeUrlEditor=noticeUrlShare.edit();
		    	queryShare=context.getSharedPreferences("queryShare", 0);
		    	queryEditor=queryShare.edit();
		    	MyDebugLog.e("push", msg.custom.toString());
		    	try {
					JSONObject customJson=new JSONObject(msg.custom.toString());

					Intent intent=new Intent();
					if(customJson.getString("url").indexOf("?")!=-1)
					{
					intent.setClass(context, NoticeActivity.class);
					noticeUrlEditor.putString("NOTICEURL", customJson.getString("url")).commit();
					}
					else
					{
						intent.setClass(context, QueryActivity.class);
						queryEditor.putString("QUERYURL",  customJson.getString("url")).commit();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);

		mRegisterCallback = new IUmengRegisterCallback() {

			@Override
			public void onRegistered(String registrationId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
				sendBroadcast(intent);
			}

		};
		mPushAgent.setRegisterCallback(mRegisterCallback);

		mUnregisterCallback = new IUmengUnregisterCallback() {

			@Override
			public void onUnregistered(String registrationId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
				sendBroadcast(intent);
			}
		};
		mPushAgent.setUnregisterCallback(mUnregisterCallback);

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		initImageLoaderConfiguration();
	}

	public void initImageLoaderConfiguration() {

		

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// ½«±£´æµÄÊ±ºòµÄURIÃû³ÆÓÃMD5 ¼ÓÃÜ
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // Not
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	public static cloudApplication getInstance() {
		return instance;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	public void logout(final EMCallBack emCallBack) {

		hxSDKHelper.logout(emCallBack);
	}

}
