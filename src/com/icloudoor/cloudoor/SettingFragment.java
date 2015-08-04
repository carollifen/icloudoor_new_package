package com.icloudoor.cloudoor;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SettingFragment extends Fragment {
	private String TAG = this.getClass().getSimpleName();
	public Context context;
	private final String mPageName = "SettingFragment";
    private ProgressDialog updateProgressDialog;
    private UpdateManager updateMan;

	private RelativeLayout RLSet;
	private RelativeLayout RLSig;
	private RelativeLayout RLShare;
	private RelativeLayout RLUpdate;
	private RelativeLayout showInfo;
	
	private RelativeLayout RLAbout;
	private RelativeLayout Help_Feedback;

	private TextView logOut;
	
	private TextView showName;
	private String name;
	
	private String versionName;
	private String versionCode;

	private MyOnClickListener myClickListener;

	private RequestQueue mQueue;
	private String HOST = UrlUtils.HOST;
	private URL logOutURL;
	private String sid = null;
	private int statusCode;

	private int isLogin = 1;
	
	private CircularImage image;
	private String portraitUrl;
	
	private Bitmap bitmap;
	private Thread mThread;
	
	private static final int MSG_SUCCESS = 0;//get the image success
	private static final int MSG_FAILURE = 1;// fail

	//
	private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Cloudoor/CacheImage/";
	private String imageName = "myImage.jpg";
	
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";

	String appID = "wxcddf37d2f770581b";
	String appSecret = "01d7ab875773e1282059d5b47b792e2b";
	UMWXHandler wxHandler;
	UMWXHandler wxCircleHandler;
	UMSocialService mController;
	private SnsPostListener mSnsPostListener;

	private Version version;
	
	public SettingFragment() {
		// Required empty public constructor
	}

	private RelativeLayout back_from_user;

	private FeedbackAgent agent;

	boolean isDebug = DEBUG.isDebug;

	private String share_link = null;
	private String defaultLink = "http://www.icloudoor.com/d";
	int role;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_page, container, false);
		
		try {
			PackageManager pm = getActivity().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionName = pi.versionName == null ? "null" : pi.versionName;
				versionCode = pi.versionCode + "";
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		
		if(getActivity() != null)
			version = new Version(getActivity());
		
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		share();
		
		mQueue = Volley.newRequestQueue(getActivity());
		myClickListener = new MyOnClickListener();

		showInfo = (RelativeLayout) view.findViewById(R.id.btn_show_personal_info);
		RLSet = (RelativeLayout) view.findViewById(R.id.btn_set);
		RLSig = (RelativeLayout) view.findViewById(R.id.btn_sig);
		SharedPreferences preferences = getActivity().getSharedPreferences("PROFILE", 0);
		role = preferences.getInt("role", 1);
		
		RLShare = (RelativeLayout) view.findViewById(R.id.btn_share);
		showName = (TextView) view.findViewById(R.id.show_name);
		
		RLAbout = (RelativeLayout) view.findViewById(R.id.btn_about_us);
		Help_Feedback = (RelativeLayout) view.findViewById(R.id.btn_help_feedback);
		
		
		
		image = (CircularImage) view.findViewById(R.id.person_image);
		image.setImageResource(R.drawable.default_image);

		RLSet.setOnClickListener(myClickListener);
		RLSig.setOnClickListener(myClickListener);
		RLShare.setOnClickListener(myClickListener);
		
		RLAbout.setOnClickListener(myClickListener);
		Help_Feedback.setOnClickListener(myClickListener);

		showInfo.setOnClickListener(myClickListener);

		return view;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		mController.unregisterListener(mSnsPostListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
		portraitUrl = loginStatus.getString("URL", null);	
		name = loginStatus.getString("NICKNAME", null);
		
		if(name != null)
			showName.setText(name);
		
		File f = new File(PATH + imageName);
		MyDebugLog.e(TAG, PATH + imageName);
		
		String imagePath = PATH + imageName;
		String imageUrl = Scheme.FILE.wrap(imagePath);
		
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getActivity());
		ImageLoader.getInstance().init(configuration);
     
        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.icon_boy_110) // resource or drawable
        .showImageForEmptyUri(R.drawable.icon_boy_110) // resource or drawable
        .showImageOnFail(R.drawable.icon_boy_110) // resource or drawable
        .resetViewBeforeLoading(false)  // default
        .delayBeforeLoading(10)
        .cacheInMemory(false) // default
        .cacheOnDisk(false) // default
        .considerExifParams(false) // default
        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
        .displayer(new SimpleBitmapDisplayer()) // default
        .handler(new Handler()) // default
        .build();
		
		if(f.exists()){

	        ImageLoader.getInstance().displayImage(imageUrl, image, options);
			
	        MyDebugLog.e(TAG, "use local");
//			BitmapFactory.Options opts=new BitmapFactory.Options();
//			opts.inTempStorage = new byte[100 * 1024];
//			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			opts.inPurgeable = true;
////			opts.inSampleSize = 4;
//			Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
//			image.setImageBitmap(bm);
		}else{
			// request bitmap in the new thread
			if(portraitUrl != null){
				MyDebugLog.e(TAG, "use net");
				ImageLoader.getInstance().displayImage(portraitUrl, image, options);
//				if (mThread == null) {
//					mThread = new Thread(runnable);
//					mThread.start();
//				}
			}
		}
		
		checkForUserStatus();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				image.setImageBitmap((Bitmap) msg.obj);
				break;
			case MSG_FAILURE:
				break;
			}
		}
	};
	
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(portraitUrl);
			final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);

				BitmapFactory.Options opts=new BitmapFactory.Options();
				opts.inTempStorage = new byte[100 * 1024];
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
				opts.inPurgeable = true;
//				opts.inSampleSize = 4;
				
				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent(), null, opts);
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}
	
			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};

	public class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_show_personal_info:
				Intent intent = new Intent();
				intent.setClass(getActivity(), ShowPersonalInfo.class);
				startActivity(intent);
				break;
			case R.id.btn_set:
				Intent intent1 = new Intent();
				intent1.setClass(getActivity(), SettingDetailActivity.class);
				startActivity(intent1);
				break;
			case R.id.btn_sig:
				if(role==1){
					Toast.makeText(getActivity(), R.string.not_Staff_member, Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent2 = new Intent();
				intent2.setClass(getActivity(), CheckRecord.class);
				startActivity(intent2);
				break;
				
			case R.id.btn_about_us:
				Intent intent3 = new Intent();
				intent3.setClass(getActivity(), AboutUs.class);
				startActivity(intent3);
				break;
			case R.id.btn_help_feedback:
				Intent intent5 = new Intent();
				intent5.setClass(getActivity(), HelpFeedback.class);
				startActivity(intent5);
			break;
			case R.id.btn_share:
				mController.openShare(getActivity(), false);
				break;
			}
		}

	}
	
	public void saveSid(String key, String value) {
		if(getActivity() != null){
			SharedPreferences savedSid = getActivity().getSharedPreferences(
					"SAVEDSID", 0);
			Editor editor = savedSid.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public String loadSid(String key) {
		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString(key, null);
	}

	@Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();
    }

	public void share() {

		share_link = OnlineConfigAgent.getInstance().getConfigParams(getActivity(), "share_link");

		SharedPreferences link = getActivity().getSharedPreferences("LINK", 0);
		Editor editor = link.edit();
		editor.putString("SHARELINK", share_link);
		editor.commit();
		
		if(share_link == null){
			share_link = defaultLink;
			MyDebugLog.e(TAG, "get default:" + share_link);
		}else {
			MyDebugLog.e(TAG, "share_link:" + share_link);
		}
		
		

		wxHandler = new UMWXHandler(getActivity(), appID, appSecret);
		wxHandler.addToSocialSDK();
		wxCircleHandler = new UMWXHandler(getActivity(), appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		
		mSnsPostListener = new SnsPostListener() {

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				if(arg1 == 200){
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
		
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(getActivity().getResources().getString(
				R.string.shareContent));
		circleMedia.setTitle(getActivity().getResources().getString(R.string.shareTitle));
		circleMedia.setShareImage(new UMImage(getActivity(),
				R.drawable.logo_deep144));
		circleMedia.setTargetUrl(share_link);
		
		mController.setShareMedia(circleMedia);
		
		
		WeiXinShareContent weiXinleMedia = new WeiXinShareContent();
		
		weiXinleMedia.setShareContent(getActivity().getResources().getString(
				R.string.shareContent));
		weiXinleMedia.setTitle(getActivity().getResources().getString(R.string.shareTitle));
		weiXinleMedia.setShareImage(new UMImage(getActivity(),
				R.drawable.logo_deep144));
		weiXinleMedia.setTargetUrl(share_link);
		mController.setShareMedia(weiXinleMedia);
		
		
		mController.getConfig().removePlatform(SHARE_MEDIA.SINA,
				SHARE_MEDIA.TENCENT);

	}


    public void checkForUserStatus() {
		Log.e(TAG, "checkForUserStatus()");
		URL getUserStatusURL = null;
		sid = loadSid("SID");
		try {
			getUserStatusURL = new URL(UrlUtils.HOST + "/user/manage/getProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, getUserStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, "checkForUserStatus: " + response.toString());
						try {
							if(response.getInt("code") == 1){
								if(response.getString("sid") != null)
									saveSid("SID", response.getString("sid"));
								
								if (getActivity() != null) {
									SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
									Editor editor = loginStatus.edit();
									editor.putInt("STATUS", response.getJSONObject("data").getInt("userStatus"));
									editor.putBoolean( "isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									editor.commit();

									SharedPreferences saveProfile = getActivity().getSharedPreferences("PROFILE", 0);
									Editor edit = saveProfile.edit();
									edit.putInt("userStatus", response.getJSONObject("data").getInt("userStatus"));
									edit.putBoolean("isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									edit.commit();
									MyDebugLog.e(TAG, String.valueOf(response.getJSONObject("data").getInt("userStatus")) + "in SettingFragment***********");
									MyDebugLog.e(TAG, String.valueOf(response.getJSONObject("data").getBoolean("isHasPropServ")) + "in SettingFragment***********");
								}
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		mQueue.add(mJsonRequest);
	}
}
