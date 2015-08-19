package com.icloudoor.cloudoor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.cache.UserCacheWrapper;
import com.icloudoor.cloudoor.chat.CommonUtils;
import com.icloudoor.cloudoor.chat.entity.AuthKeyEn;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.KeyHelper;
import com.icloudoor.cloudoor.utli.UserDBHelper;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.tag.TagManager;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;

public class CloudDoorMainActivity extends BaseFragmentActivity implements
		EMEventListener {
	private final String TAG = this.getClass().getSimpleName();
	// private ViewPager mViewPager;
	// private ArrayList<Fragment> mFragmentsList;
	// private MyFragmentPagerAadpter mFragmentAdapter;
	public static CloudDoorMainActivity instance = null;
	private MsgFragment mMsgFragment;
	private KeyFragment mKeyFragment;
	private KeyFragmentNoBLE mKeyFragmentNoBLE;
	private SettingFragment mSettingFragment;
	private WuyeFragment mWuyeFragment;

	private Logout logoutToDo;

	private Broadcast mFinishActivityBroadcast;

	private RelativeLayout bottomWuye;
	private RelativeLayout bottomMsg;
	private RelativeLayout bottomKey;
	private RelativeLayout bottomSetting;

	private TextView bottomTvMsg;
	private TextView bottomTvKey;
	private TextView bottomTvSetting;
	private TextView bottomTvWuye;

	private ImageView bottomIvMsg;
	private ImageView bottomIvKey;
	private ImageView bottomIvSetting;
	private ImageView bottomIvWuye;

	public FragmentManager mFragmentManager;
	public MyOnClickListener myClickListener;
	public FragmentTransaction mFragmenetTransaction;
	// public MyPageChangeListener myPageChangeListener;

	private int COLOR_GRAY = 0xFF999999;
	private int COLOR_BLACK = 0xFF0065a1;

	private float alpha_half_transparent = 0.2f;
	private float alpha_opaque = 1.0f;

	private int homePressed = 0;
	private int lockScreenBefore = 0;

	private int currentVersion;
	private boolean mInKeyFragment;

	public MyThread myThread = null;

	// for Umeng Push Service
	private RequestQueue mRequestQueue;
	private String url = UrlUtils.HOST + "/user/api/getTags.do";
	private String tag;
	private String sid;
	private PushAgent mPushAgent;

	//
	private Bitmap bitmap;
	private Thread mThread;
	private String imageURL;
	private String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Cloudoor/CacheImage";
	private String jpegName = "myImage.jpg";

	private FeedbackAgent agent;

	boolean isDebug = DEBUG.isDebug;

	private Version version;
	
	private String previousUrl = " ";

	Handler mHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 10:
				if (mKeyFragment.mOpenDoorState == 0) {
					Log.i(TAG, "Thread handler");
					if (!mKeyFragment.mBTScanning) {
						mKeyFragment
								.populateDeviceList(mKeyFragment.mBtStateOpen);
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public class MyThread extends Thread {

		private volatile boolean mStopThread = false;
		public volatile boolean mKeyFindState = false;
		private final long mScanningProidShort = 2000;
		private final long mScanningProidLong = 6000;

		public void stopThread() {
			this.mStopThread = true;
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted() && !mStopThread) {
				if (mInKeyFragment) {
					Message msg = new Message();
					msg.what = 10;
					mHandler1.sendMessage(msg);
				}
				MyDebugLog.i("ThreadTest", Thread.currentThread().getId()
						+ "myThread");
				try {
					Thread.sleep(mScanningProidShort);
					if (mKeyFindState == true) {
						MyDebugLog.i("ThreadTest", "mKeyFindState true : "
								+ String.valueOf(mStopThread));
						Thread.sleep(mScanningProidLong);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_main);

		logoutToDo = new Logout(getApplicationContext());
		version = new Version(getApplicationContext());
		
		SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
		Editor editor = savedUrl.edit();
		editor.putString("Url", previousUrl).commit();

		UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {

			@Override
			public void OnDownloadStart() {
				Log.e("testDownload", "OnDownloadStart");
			}

			@Override
			public void OnDownloadUpdate(int progress) {
			}

			@Override
			public void OnDownloadEnd(int result, String file) {
				Log.e("testDownload", "OnDownloadEnd");
//				SharedPreferences setting = getSharedPreferences("com.icloudoor.clouddoor", 0);
//				setting.edit().putBoolean("FIRST", true).commit();

				File f = new File(file);
				UmengUpdateAgent.startInstall(getApplicationContext(), f);
			}
		});
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		// for Umeng Feedback
		agent = new FeedbackAgent(this);
		agent.sync();

		PushAgent.getInstance(this).onAppStart();

		// for Umeng Push Service
		mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();

		// remove tags before add
//		new Thread() {
//			public void run() {
//				String device_token;
//				try {
//					mPushAgent.getTagManager().reset();
//					for (int i = 0; i < 5; i++) {
//						device_token = UmengRegistrar
//								.getRegistrationId(getApplicationContext());
//						if (device_token != null) {
//							MyDebugLog.e("devicetoken", device_token);
//							break;
//						}
//						sleep(3000);
//					}
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
//
//		}.start();

		mFinishActivityBroadcast = new Broadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.icloudoor.cloudoor.ACTION_FINISH");

		registerReceiver(mFinishActivityBroadcast, intentFilter);

		// String device_token =
		// UmengRegistrar.getRegistrationId(getApplicationContext());
		// Log.e("devicetoken", device_token);
		// mPushAgent.setDebugMode(true);

		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		getFriends();
		sid = loadSid();
		JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url
				+ "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei="
				+ version.getDeviceId(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e("response", response.toString());
						try {

							if (response.getString("sid") != null)
								saveSid("SID", sid);

							if (response.getInt("code") == 1) {
								JSONArray tagJson = response
										.getJSONArray("data");
								for (int i = 0; i < tagJson.length(); i++) {
									tag = (String) tagJson.get(i);
									MyDebugLog.e("response", tag);
									new AddTagTask(tag).execute();
								}
							} else if (response.getInt("code") == -2) {
								Toast.makeText(getApplicationContext(),
										R.string.not_login, Toast.LENGTH_SHORT)
										.show();

								// SharedPreferences loginStatus =
								// getSharedPreferences("LOGINSTATUS",
								// MODE_PRIVATE);
								// Editor editor = loginStatus.edit();
								// editor.putInt("LOGIN", 0);
								// editor.commit();
								logoutToDo.logoutDoing();
								
								EMChatManager.getInstance().logout(new EMCallBack() {
									
									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub
										Intent intent = new Intent();
										intent.setClass(getApplicationContext(),
												Login.class);
										startActivity(intent);
										finish();
									}
									
									@Override
									public void onProgress(int arg0, String arg1) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onError(int arg0, String arg1) {
										// TODO Auto-generated method stub
										
									}
								});
								

								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(CloudDoorMainActivity.this,
									R.string.network_error, Toast.LENGTH_SHORT)
									.show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				});
//		mRequestQueue.add(mJsonObjectRequest);

		registerReceiver(mConnectionStatusReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		instance = this;

		// SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO",
		// MODE_PRIVATE);
		// int setPersonal = personalInfo.getInt("SETINFO", 1);
		// if(setPersonal == 0) {
		// Log.e("jump to set", "in main activity");
		// Intent intentSetInfo = new Intent();
		// intentSetInfo.setClass(getApplicationContext(),
		// SetPersonalInfo.class);
		// startActivity(intentSetInfo);
		// }

		mMsgFragment = new MsgFragment();
		currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion >= 18) {
			if (!getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				mKeyFragmentNoBLE = new KeyFragmentNoBLE();
			} else {
				mKeyFragment = new KeyFragment();
			}
		} else {
			mKeyFragmentNoBLE = new KeyFragmentNoBLE();
		}
		mSettingFragment = new SettingFragment();
		mWuyeFragment = new WuyeFragment();

		// mFragmentManager = getSupportFragmentManager();

		// InitViewPager();
		InitViews();
		InitState();
		EMChat.getInstance().setAppInited();
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		IntentFilter filter = new IntentFilter("refreshData");
		filter.addAction("refreshData");
		registerReceiver(refreshData, filter);
		if (currentVersion >= 18
				&& getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_BLUETOOTH_LE)) {
			myThread = new MyThread();
			myThread.start();
		}
		initLoginIM();
		getMyKey();
	}

	public void getMyKey() {

		getNetworkData(new NetworkInterface() {

			@Override
			public void onSuccess(JSONObject response) {
				// TODO Auto-generated method stub
				AuthKeyEn keyEn = GsonUtli.jsonToObject(response.toString(),
						AuthKeyEn.class);
				if (keyEn != null) {
					UserCacheWrapper.setMedicalRecord(
							CloudDoorMainActivity.this, keyEn);
				} else {
					showToast(R.string.network_error);
				}
			}

			@Override
			public void onFailure(VolleyError error) {
				// TODO Auto-generated method stub

			}
		}, "/user/api/keys/my.do", "{}", false);
	}

	public void InitViews() {
		myClickListener = new MyOnClickListener();
		// myPageChangeListener = new MyPageChangeListener();

		// mViewPager = (ViewPager) findViewById(R.id.vPager);

		bottomMsg = (RelativeLayout) findViewById(R.id.bottom_msg_layout);
		bottomKey = (RelativeLayout) findViewById(R.id.bottom_key_layout);
		bottomSetting = (RelativeLayout) findViewById(R.id.bottom_setting_layout);
		bottomWuye = (RelativeLayout) findViewById(R.id.bottom_wuye_layout);

		bottomTvMsg = (TextView) findViewById(R.id.msg_text);
		bottomTvKey = (TextView) findViewById(R.id.key_text);
		bottomTvSetting = (TextView) findViewById(R.id.set_text);
		bottomTvWuye = (TextView) findViewById(R.id.wuye_text);

		bottomIvMsg = (ImageView) findViewById(R.id.msg_image);
		bottomIvKey = (ImageView) findViewById(R.id.key_image);
		bottomIvSetting = (ImageView) findViewById(R.id.set_image);
		bottomIvWuye = (ImageView) findViewById(R.id.wuye_image);

		// mViewPager.setAdapter(mFragmentAdapter);
		// mViewPager.setOnPageChangeListener(myPageChangeListener);

		bottomMsg.setOnClickListener(myClickListener);
		bottomKey.setOnClickListener(myClickListener);
		bottomSetting.setOnClickListener(myClickListener);
		bottomWuye.setOnClickListener(myClickListener);
	}

	// public void InitViewPager() {
	// mFragmentsList = new ArrayList<Fragment>();
	//
	// mMsgFragment = new MsgFragment();
	// mKeyFragment = new KeyFragment();
	// mSettingFragment = new SettingFragment();
	//
	// mFragmentsList.add(mMsgFragment);
	// mFragmentsList.add(mKeyFragment);
	// mFragmentsList.add(mSettingFragment);
	//
	// mFragmentAdapter = new MyFragmentPagerAadpter(mFragmentManager,
	// mFragmentsList);
	// }

	public void InitState() {
		// mViewPager.setCurrentItem(1);
		mFragmentManager = getSupportFragmentManager();
		mFragmenetTransaction = mFragmentManager.beginTransaction();
		if (currentVersion >= 18) {
			// BLE
			if (!getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				mFragmenetTransaction.replace(R.id.id_content,
						mKeyFragmentNoBLE).commit();
			} else {
				mFragmenetTransaction.replace(R.id.id_content, mKeyFragment)
						.commit();
				mInKeyFragment = true;
			}
		} else {
			mFragmenetTransaction.replace(R.id.id_content, mKeyFragmentNoBLE)
					.commit();
		}

		bottomTvKey.setTextColor(COLOR_BLACK);
		bottomTvMsg.setTextColor(COLOR_GRAY);
		bottomTvSetting.setTextColor(COLOR_GRAY);
		bottomTvWuye.setTextColor(COLOR_GRAY);

		bottomIvMsg.setImageResource(R.drawable.msg_normal);
		bottomIvKey.setImageResource(R.drawable.key_pressed);
		bottomIvSetting.setImageResource(R.drawable.my_normal);
		bottomIvWuye.setImageResource(R.drawable.wuye_normal);
	}

	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");

		OnlineConfigAgent.getInstance().updateOnlineConfig(this);

		getBannerData();
		
		new Thread() {
			public void run() {
				String device_token;
				try {
					mPushAgent.getTagManager().reset();
					for (int i = 0; i < 5; i++) {
						device_token = UmengRegistrar
								.getRegistrationId(getApplicationContext());
						if (device_token != null) {
							MyDebugLog.e("devicetoken", device_token);
							break;
						}
						sleep(3000);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}.start();
		
		JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url + "?sid=" + loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e("response", response.toString());
						try {

							if (response.getString("sid") != null)
								saveSid("SID", sid);

							if (response.getInt("code") == 1) {
								JSONArray tagJson = response
										.getJSONArray("data");
								for (int i = 0; i < tagJson.length(); i++) {
									tag = (String) tagJson.get(i);
									MyDebugLog.e("response", tag);
									new AddTagTask(tag).execute();
								}
							} else if (response.getInt("code") == -2) {
								Toast.makeText(getApplicationContext(),
										R.string.not_login, Toast.LENGTH_SHORT)
										.show();
								logoutToDo.logoutDoing();

								final Intent intent = new Intent();
								intent.setClass(getApplicationContext(),
										Login.class);
								startActivity(intent);
								finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(CloudDoorMainActivity.this,
									R.string.network_error, Toast.LENGTH_SHORT)
									.show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				});
		mRequestQueue.add(mJsonObjectRequest);

		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck });
		if (mMsgFragment != null) {
			mMsgFragment.refresh();
		}

		SharedPreferences setting = getSharedPreferences("SETTING", 0);
		if (setting.getInt("disturb", 1) == 0)
			mPushAgent.enable(cloudApplication.mRegisterCallback);
		else
			mPushAgent.disable(cloudApplication.mUnregisterCallback);

		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		homePressed = homeKeyEvent.getInt("homePressed", 0);

		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);

		SharedPreferences firstLoginShare = getSharedPreferences(
				"FIRSTLOGINSHARE", 0);
		Editor mEditor = firstLoginShare.edit();

		if (!(firstLoginShare.getBoolean("FIRSTLOGIN", true))) {
			if (homePressed == 1 && useSign == 1) {
				if (System.currentTimeMillis()
						- homeKeyEvent.getLong("TIME", 0) > 60 * 1000) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(),
							VerifyGestureActivity.class);
					startActivity(intent);
				}
			}
		}
		mEditor.putBoolean("FIRSTLOGIN", false).commit();
		// save image to file

		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
		imageURL = loginStatus.getString("URL", null);
//
		SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
//
//		if(!savedUrl.getString("Url", " ").equals(imageURL) && imageURL != null) {
//			File f = new File(PATH + "/" + jpegName);
//			if (f.exists())
//				f.delete();
//			
//			MyDebugLog.e(TAG, imageURL + "  downloading");
//			downLoadImage();		
//		}
		File f = new File(PATH + "/" + jpegName);
		if (!f.exists() && imageURL.length() > 0) {
			Log.e(TAG, "use local");
			downLoadImage();
		} else if(f.exists()) {
			if(imageURL.equals(""))
				f.delete();
			else if(!savedUrl.getString("Url", " ").equals(imageURL) && imageURL.length() > 0){
				f.delete();
				Log.e(TAG, "use net");
				downLoadImage();
			}
		}
	}

	public void downLoadImage() {
		String imagePath = PATH + "/" + jpegName;
		File f = new File(imagePath);
		if (f.exists())
			f.delete();

		FinalHttp fh = new FinalHttp();
		fh.download(imageURL, imagePath, new AjaxCallBack<File>() {
			public void onStart() {
				super.onStart();
				MyDebugLog.e(TAG, "download image start");
			}

			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				MyDebugLog.e(TAG, "downloading image");
			}

			public void onSuccess(File t) {
				super.onSuccess(t);
				SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
				Editor editor = savedUrl.edit();
				editor.putString("Url", imageURL).commit();
				MyDebugLog.e(TAG, "download image success");
			}

			@SuppressWarnings("unused")
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, strMsg);
				MyDebugLog.e(TAG, "download image fail");
			}
		});
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				File f = null;
				f = new File(PATH);
				if (!f.exists())
					f.mkdirs();

				try {
					FileOutputStream fout = new FileOutputStream(PATH + "/"
							+ jpegName);
					BufferedOutputStream bos = new BufferedOutputStream(fout);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				SharedPreferences downPic = getSharedPreferences("DOWNPIC", 0);
				Editor editor = downPic.edit();
				editor.putInt("PIC", 1);
				editor.commit();

				break;
			case 2:
				break;
			}
		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(imageURL);
			// final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);

				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inTempStorage = new byte[100 * 1024];
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
				opts.inPurgeable = true;
				// opts.inSampleSize = 4;

				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent(), null, opts);
			} catch (Exception e) {
				mHandler.obtainMessage(2).sendToTarget();
				return;
			}

			mHandler.obtainMessage(1, bitmap).sendToTarget();
		}
	};

	public class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			BottomColorChange(view.getId());
		}
	}

	// public class MyPageChangeListener implements OnPageChangeListener {
	//
	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	// if (arg0 == 2) {
	// int index = mViewPager.getCurrentItem();
	// BottomColorChange(index);
	// }
	// }
	//
	// @Override
	// public void onPageScrolled(int arg0, float arg1, int arg2) {
	// }
	//
	// @Override
	// public void onPageSelected(int index) {
	// }
	//
	// }

	public void BottomColorChange(int index) {
		mFragmentManager = getSupportFragmentManager();
		mFragmenetTransaction = mFragmentManager.beginTransaction();
		mInKeyFragment = false;
		switch (index) {
		case R.id.bottom_wuye_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_BLACK);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_selected);

			mFragmenetTransaction.replace(R.id.id_content, mWuyeFragment);

			break;
		case R.id.bottom_msg_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_BLACK);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_selected);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			mFragmenetTransaction.replace(R.id.id_content, mMsgFragment);

			// mViewPager.setCurrentItem(0);
			break;

		case R.id.bottom_key_layout:
			bottomTvKey.setTextColor(COLOR_BLACK);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_GRAY);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_pressed);
			bottomIvSetting.setImageResource(R.drawable.my_normal);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			if (currentVersion >= 18) {
				// BLE
				if (!getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_BLUETOOTH_LE)) {
					mFragmenetTransaction.replace(R.id.id_content,
							mKeyFragmentNoBLE);
				} else {
					mFragmenetTransaction
							.replace(R.id.id_content, mKeyFragment);
					mInKeyFragment = true;
				}
			} else {
				mFragmenetTransaction.replace(R.id.id_content,
						mKeyFragmentNoBLE);
			}

			// mViewPager.setCurrentItem(1);
			break;

		case R.id.bottom_setting_layout:
			bottomTvKey.setTextColor(COLOR_GRAY);
			bottomTvMsg.setTextColor(COLOR_GRAY);
			bottomTvSetting.setTextColor(COLOR_BLACK);
			bottomTvWuye.setTextColor(COLOR_GRAY);

			bottomIvMsg.setImageResource(R.drawable.msg_normal);
			bottomIvKey.setImageResource(R.drawable.key_normal);
			bottomIvSetting.setImageResource(R.drawable.my_selected);
			bottomIvWuye.setImageResource(R.drawable.wuye_normal);

			mFragmenetTransaction.replace(R.id.id_content, mSettingFragment);

			// mViewPager.setCurrentItem(2);
			break;
		}
		mFragmenetTransaction.commitAllowingStateLoss();
	}

	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {

		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_DIALOG_REASON_LOCK = "lock";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					homePressed = 1;

					SharedPreferences homeKeyEvent = getSharedPreferences(
							"HOMEKEY", 0);
					Editor editor = homeKeyEvent.edit();
					editor.putInt("homePressed", homePressed);
					editor.putLong("TIME", System.currentTimeMillis());
					editor.commit();

				} else if (TextUtils.equals(reason, SYSTEM_DIALOG_REASON_LOCK)) {
					homePressed = 1;

					SharedPreferences homeKeyEvent = getSharedPreferences(
							"HOMEKEY", 0);
					Editor editor = homeKeyEvent.edit();
					editor.putInt("homePressed", homePressed);
					editor.putLong("TIME", System.currentTimeMillis());
					editor.commit();

				}
			}
		}

	};

	public BroadcastReceiver mConnectionStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: This method is called when the BroadcastReceiver is
			// receiving
			// an Intent broadcast.

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable()) {
					saveSid("NETSTATE", "NET_WORKS");
					Log.i("NOTICE", "The net is available!");
				}
				NetworkInfo.State state = connectivityManager.getNetworkInfo(
						connectivityManager.TYPE_MOBILE).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "GPRS is OK!");
					NetworkInfo mobNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				}
				state = connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					Log.i("NOTICE", "WIFI is OK!");
					NetworkInfo wifiNetInfo = connectivityManager
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				}
			} else {
				saveSid("NETSTATE", "NET_NOT_WORK");
				// Toast.makeText(context, R.string.no_network,
				// Toast.LENGTH_LONG).show();
			}
		}
	};

	class AddTagTask extends AsyncTask<Void, Void, String> {

		String tagString;
		String[] tags;

		public AddTagTask(String tag) {
			tagString = tag;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				TagManager.Result result = mPushAgent.getTagManager().add(
						tagString);
				MyDebugLog.e("result", result.toString());
				return result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Fail";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	public String loadSid() {
		SharedPreferences loadSid = CloudDoorMainActivity.this
				.getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	public void saveSid(String key, String value) {
		SharedPreferences savedSid = this.getSharedPreferences("SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString(key, value);
		editor.commit();
	}

	class Broadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			CloudDoorMainActivity.this.finish();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMChatManager.getInstance().unregisterEventListener(this);
	}

	public void onDestroy() {
		MyDebugLog.e("ThreadTest", "onDestroy");
		if (myThread != null) {
			myThread.stopThread();
		}
		super.onDestroy();
		unregisterReceiver(mHomeKeyEventReceiver);
		unregisterReceiver(mConnectionStatusReceiver);
		unregisterReceiver(mFinishActivityBroadcast);
		unregisterReceiver(refreshData);

	}

	@Override
	public void onEvent(EMNotifierEvent event) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mMsgFragment.refresh();
			}
		});

		switch (event.getEvent()) {
		case EventNewMessage: {
			// »ñÈ¡µ½message
			EMMessage message = (EMMessage) event.getData();

			String username = null;
			// Èº×éÏûÏ¢
			if (message.getChatType() == ChatType.GroupChat
					|| message.getChatType() == ChatType.ChatRoom) {
				username = message.getTo();
			} else {
				// µ¥ÁÄÏûÏ¢
				username = message.getFrom();

			}

			break;
		}
		case EventOfflineMessage: {
			break;
		}

		case EventConversationListChanged: {
			break;
		}
		default:
			break;
		}
	}

	public void getFriends() {

		String url = UrlUtils.HOST + "/user/im/getFriends.do" + "?sid="
				+ loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId();
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				url, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub

						MyFriendInfo friendInfo = GsonUtli.jsonToObject(
								response.toString(), MyFriendInfo.class);
						if (friendInfo != null) {
							UserDBHelper.getInstance(CloudDoorMainActivity.this).initTable(friendInfo.getData());

						} else {
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return null;
			}
		};
		mRequestQueue.add(mJsonRequest);
	}

	String currentUsername;
	String currentPassword;

	public void loginIM(){
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(!EMChatManager.getInstance().isConnected()){
			System.out.println("currentUsername  = "+currentUsername);
			System.out.println("currentPassword  = "+currentPassword);
			EMChatManager.getInstance().login(currentUsername, currentPassword,
					new EMCallBack() {

						@Override
						public void onSuccess() {
							System.out.println("IM________");
							cloudApplication.getInstance().setUserName(currentUsername);
							cloudApplication.getInstance().setPassword(currentPassword);
						}

						@Override
						public void onProgress(int progress, String status) {
						}

						@Override
						public void onError(final int code, final String message) {
							System.out.println("IM___**____");
							runOnUiThread(new Runnable() {
								public void run() {
//									showToast(R.string.Login_failed);
								}
							});
						}
					});
		}
		
	}
	
	int count;
	public void initLoginIM() {
		
		SharedPreferences preferences = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
		currentUsername = preferences.getString("IMUSERID", "");
		currentPassword = preferences.getString("IMPASSWORD", "");
		if(TextUtils.isEmpty(currentUsername)||TextUtils.isEmpty(currentPassword)){
			
			String url = UrlUtils.HOST + "/user/im/getAccount.do"+ "?sid=" + loadSid()+"&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId();
			
			MyRequestBody requestBody = new MyRequestBody( url, "{}",new Response.Listener<JSONObject>() {
				
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					System.out.println("getAccount = "+response);
					try {
						if(response.getInt("code")==1){
							JSONObject data = response.getJSONObject("data");
							currentUsername = data.getString("userId");
							currentPassword = data.getString("password");
							if(TextUtils.isEmpty(currentPassword.trim())){
								count++;
								if(count<=3){
									bottomTvMsg.postDelayed(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											initLoginIM();
										}
									}, (long) ((Math.pow(count, 3)*1000)));
								}
							}else{
								SharedPreferences preferences = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								preferences.edit().putString("IMUSERID", currentUsername);
								preferences.edit().putString("IMPASSWORD", currentPassword);
								loginIM();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			},new Response.ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					showToast(R.string.network_error);
				}
			});
			mQueue.add(requestBody);
			
		}else{
			loginIM();
		}

		
	}

	public void getBannerData() {
		URL bannerURL = null;
		try {
			bannerURL = new URL(UrlUtils.HOST
					+ "/user/prop/zone/getBannerRotate.do" + "?sid=" + sid
					+ "&ver=" + version.getVersionName() + "&imei="
					+ version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		JsonObjectRequest mBannerRequest = new JsonObjectRequest(Method.POST,
				bannerURL.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e("response", response.toString());
						try {

							MyDebugLog.e(TAG,
									"banner data request in main activity: "
											+ response.toString());

							if (response.getInt("code") == 1) {

								SharedPreferences banner = getSharedPreferences(
										"BANNER", 0);
								Editor editor = banner.edit();

								JSONArray data = response.getJSONArray("data");
								MyDebugLog.e(
										TAG,
										"banner count = "
												+ String.valueOf(data.length()));
								editor.putInt("COUNT", data.length());
								
								for(int index = 0; index < data.length(); index++) {
									if(index == 0) {
										if (data.getJSONObject(0).getString("type").equals("1")) {
											String bg = data.getJSONObject(0).getString("bgColor");
											String content = data.getJSONObject(0).getString("content");
											String title = data.getJSONObject(0).getString("title");
											String date = data.getJSONObject(0).getString("createDate");

											editor.putString("1bg", bg);
											editor.putString("1content", content);
											editor.putString("1title", title);
											editor.putString("1date", date);
											editor.putString("1type", "1");
										} else if (data.getJSONObject(0).getString("type").equals("2")) {
											String url = data.getJSONObject(0).getString("photoUrl");
											String link = data.getJSONObject(0).getString("link");
											editor.putString("1url", url);
											editor.putString("1link", link);
											editor.putString("1type", "2");
										}
									} else if(index == 1) {
										if (data.getJSONObject(1).getString("type").equals("1")) {
											String bg = data.getJSONObject(1).getString("bgColor");
											String content = data.getJSONObject(1).getString("content");
											String title = data.getJSONObject(1).getString("title");
											String date = data.getJSONObject(1).getString("createDate");

											editor.putString("2bg", bg);
											editor.putString("2content", content);
											editor.putString("2title", title);
											editor.putString("2date", date);
											editor.putString("2type", "1");
										} else if (data.getJSONObject(1).getString("type").equals("2")) {
											String url = data.getJSONObject(1).getString("photoUrl");
											String link = data.getJSONObject(1).getString("link");
											editor.putString("2url", url);
											editor.putString("2link", link);
											editor.putString("2type", "2");
										}
									} else if(index == 2) {
										if (data.getJSONObject(2).getString("type").equals("1")) {
											String bg = data.getJSONObject(2).getString("bgColor");
											String content = data.getJSONObject(2).getString("content");
											String title = data.getJSONObject(2).getString("title");
											String date = data.getJSONObject(2).getString("createDate");

											editor.putString("3bg", bg);
											editor.putString("3content", content);
											editor.putString("3title", title);
											editor.putString("3date", date);
											editor.putString("3type", "1");
										} else if (data.getJSONObject(2).getString("type").equals("2")) {
											String url = data.getJSONObject(2).getString("photoUrl");
											String link = data.getJSONObject(2).getString("link");
											editor.putString("3url", url);
											editor.putString("3link", link);
											editor.putString("3type", "2");
										}
									} else if(index == 3) {
										if (data.getJSONObject(3).getString("type").equals("1")) {
											String bg = data.getJSONObject(3).getString("bgColor");
											String content = data.getJSONObject(3).getString("content");
											String title = data.getJSONObject(3).getString("title");
											String date = data.getJSONObject(3).getString("createDate");

											editor.putString("4bg", bg);
											editor.putString("4content", content);
											editor.putString("4title", title);
											editor.putString("4date", date);
											editor.putString("4type", "1");
										} else if (data.getJSONObject(3).getString("type").equals("2")) {
											String url = data.getJSONObject(3).getString("photoUrl");
											String link = data.getJSONObject(3).getString("link");
											editor.putString("4url", url);
											editor.putString("4link", link);
											editor.putString("4type", "2");
										}
									}
								}
								editor.commit();

							} else if (response.getInt("code") == -2) {

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(CloudDoorMainActivity.this,
								R.string.network_error, Toast.LENGTH_SHORT)
								.show();
					}
				});
		mRequestQueue.add(mBannerRequest);
	}
	
	
	public BroadcastReceiver refreshData = new BroadcastReceiver() {
	
		public void onReceive(Context context, Intent intent) {
			
			List<String> setChat =(List<String>) intent.getSerializableExtra("setChat"); 
			for (int i = 0; i < setChat.size(); i++) {
				if(setChat.get(i).equals("getProfile")){
					KeyHelper.getInstance(context)
					.checkForUserStatus();
				}
				if (setChat.get(i).contains("download")) {
					KeyHelper.getInstance(context)
							.downLoadKey2();
				}
				if(setChat.get(i).contains("getTags")){
					System.out.println("getTags");
					JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url
							+ "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei="
							+ version.getDeviceId(), null,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									MyDebugLog.e("response", response.toString());
									try {

										if (response.getString("sid") != null)
											saveSid("SID", sid);

										if (response.getInt("code") == 1) {
											JSONArray tagJson = response
													.getJSONArray("data");
											for (int i = 0; i < tagJson.length(); i++) {
												tag = (String) tagJson.get(i);
												MyDebugLog.e("response", tag);
												new AddTagTask(tag).execute();
											}
										} else if (response.getInt("code") == -2) {
											Toast.makeText(getApplicationContext(),
													R.string.not_login, Toast.LENGTH_SHORT)
													.show();
											logoutToDo.logoutDoing();
											
											EMChatManager.getInstance().logout(new EMCallBack() {
												
												@Override
												public void onSuccess() {
													// TODO Auto-generated method stub
													Intent intent = new Intent();
													intent.setClass(getApplicationContext(),
															Login.class);
													startActivity(intent);
													finish();
												}
												
												@Override
												public void onProgress(int arg0, String arg1) {
													// TODO Auto-generated method stub
													
												}
												
												@Override
												public void onError(int arg0, String arg1) {
													// TODO Auto-generated method stub
													
												}
											});
											

											
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Toast.makeText(CloudDoorMainActivity.this,
												R.string.network_error, Toast.LENGTH_SHORT)
												.show();
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub
								}
							});
					
					mRequestQueue.add(mJsonObjectRequest);
					
				}
			}
		};
	};
}
