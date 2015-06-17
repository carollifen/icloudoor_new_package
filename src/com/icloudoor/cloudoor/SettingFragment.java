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
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

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

	private TextView logOut;
	
	private TextView showName;
	private String name;

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

	public SettingFragment() {
		// Required empty public constructor
	}

	private RelativeLayout back_from_user;

	private FeedbackAgent agent;

	boolean isDebug = DEBUG.isDebug;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.set_page, container, false);

		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		share();
		back_from_user = (RelativeLayout) view
				.findViewById(R.id.back_from_user);
		back_from_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
				
				agent = new FeedbackAgent(getActivity());
				UserInfo info = agent.getUserInfo();
				if (info == null)
					info = new UserInfo();
				Map<String, String> contact = info.getContact();
				if (contact == null)
					contact = new HashMap<String, String>();

				
				if(loginStatus.getString("NAME", null).length() > 0)
					contact.put("name", loginStatus.getString("NAME", null));
				if(loginStatus.getString("PHONENUM", null).length() > 0)
					contact.put("phone", loginStatus.getString("PHONENUM", null));
				info.setContact(contact);
				agent.setUserInfo(info);

				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean result = agent.updateUserInfo();
					}
					
				}).start();
				
				agent.setWelcomeInfo(getString(R.string.umeng_fb_reply_content_default));
				agent.startFeedbackActivity();
			}
		});
		
		mQueue = Volley.newRequestQueue(getActivity());
		myClickListener = new MyOnClickListener();

		showInfo = (RelativeLayout) view.findViewById(R.id.btn_show_personal_info);
		RLSet = (RelativeLayout) view.findViewById(R.id.btn_set);
		RLSig = (RelativeLayout) view.findViewById(R.id.btn_sig);
		RLShare = (RelativeLayout) view.findViewById(R.id.btn_share);
		RLUpdate = (RelativeLayout) view.findViewById(R.id.btn_update);
		showName = (TextView) view.findViewById(R.id.show_name);
		
		
		image = (CircularImage) view.findViewById(R.id.person_image);
		image.setImageResource(R.drawable.default_image);
		
		
		SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);	
		name = loginStatus.getString("NAME", null);
		portraitUrl = loginStatus.getString("URL", null);
		
		if(name != null)
			showName.setText(name);
		
		logOut = (TextView) view.findViewById(R.id.btn_logout);

		RLSet.setOnClickListener(myClickListener);
		RLSig.setOnClickListener(myClickListener);
		RLShare.setOnClickListener(myClickListener);
		RLUpdate.setOnClickListener(myClickListener);

		showInfo.setOnClickListener(myClickListener);
		logOut.setOnClickListener(myClickListener);

		return view;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
		portraitUrl = loginStatus.getString("URL", null);	
		File f = new File(PATH + imageName);
		Log.e(TAG, PATH + imageName);
		if(f.exists()){
			Log.e(TAG, "use local");
			BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 4;
			Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
			image.setImageBitmap(bm);
		}else{
			// request bitmap in the new thread
			if(portraitUrl != null){
				Log.e(TAG, "use net");
				if (mThread == null) {
					mThread = new Thread(runnable);
					mThread.start();
				}
			}
		}
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
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inSampleSize = 4;
				
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
				Intent intent2 = new Intent();
				intent2.setClass(getActivity(), SignActivity.class);
				startActivity(intent2);
				break;
			case R.id.btn_share:
//                if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
//                    Intent sendIntent = new Intent();
//                    sendIntent.setAction(Intent.ACTION_SEND);
//                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Welcome using Cloudoor.");
//                    sendIntent.setType("text/plain");
//                    startActivity(Intent.createChooser(sendIntent, "Shared"));
//                }else {
//                    if (getActivity() != null) {
//                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
//                    }
//                }
				
				mController.openShare(getActivity(), false);
				break;
			case R.id.btn_update:
                if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
                    updateMan = new UpdateManager(getActivity().getApplicationContext(), appUpdateCb);
                    updateMan.checkUpdate();
                }else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                }
				break;
			case R.id.btn_logout:
                if ("NET_WORKS".equals(loadSid("NETSTATE"))) {
                    sid = loadSid("SID");

                    try {
                        logOutURL = new URL(HOST + "/user/manage/logout.do"
                                + "?sid=" + sid);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
                            Method.POST, logOutURL.toString(), null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                            saveSid("SID", null);
                                            
                                        statusCode = response.getInt("code");

                                        isLogin = 0;
                                        if(getActivity() != null){
                                        	SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
                                        Editor editor1 = loginStatus.edit();
                                        editor1.putInt("LOGIN", isLogin);
                                        editor1.commit();
                                        
                                        Intent intent3 = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phone", loginStatus.getString("PHONENUM", ""));
                                        intent3.putExtras(bundle);
                                        intent3.setClass(getActivity(), Login.class);
                                        startActivity(intent3);
                                        }
                                        
                                        
                                        String sql = "DELETE FROM " + TABLE_NAME +";";
                                        mKeyDB.execSQL(sql);
                                        
                                        String sq2 = "DELETE FROM " + CAR_TABLE_NAME +";";
                                        mKeyDB.execSQL(sq2);
                                        
                                        String sq3 = "DELETE FROM " + ZONE_TABLE_NAME +";";
                                        mKeyDB.execSQL(sq3);                                        
                                                                  
                                        CloudDoorMainActivity mainActivity = (CloudDoorMainActivity) getActivity();
                                        mainActivity.finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        	if(getActivity() != null)
    							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    mQueue.add(mJsonRequest);
                }else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                }
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

		// Ìí¼ÓÎ¢ÐÅÆ½Ì¨
		wxHandler = new UMWXHandler(getActivity(), appID, appSecret);
		wxHandler.addToSocialSDK();
		// Ìí¼ÓÎ¢ÐÅÅóÓÑÈ¦
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
		// ÉèÖÃÅóÓÑÈ¦title
		circleMedia.setTitle(getActivity().getResources().getString(R.string.shareTitle));
		circleMedia.setShareImage(new UMImage(getActivity(),
				R.drawable.logo_deep144));
		circleMedia.setTargetUrl("http://m.70c.com/w/SZQQSC");
		
		mController.setShareMedia(circleMedia);
		
		
		WeiXinShareContent weiXinleMedia = new WeiXinShareContent();
		
		weiXinleMedia.setShareContent(getActivity().getResources().getString(
				R.string.shareContent));
		// ÉèÖÃÅóÓÑÈ¦title
		weiXinleMedia.setTitle(getActivity().getResources().getString(R.string.shareTitle));
		weiXinleMedia.setShareImage(new UMImage(getActivity(),
				R.drawable.logo_deep144));
		weiXinleMedia.setTargetUrl("http://m.70c.com/w/SZQQSC");
		mController.setShareMedia(weiXinleMedia);
		
		mController.getConfig().removePlatform(SHARE_MEDIA.SINA,
				SHARE_MEDIA.TENCENT);

	}

	UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() {
		@Override
		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo) {
			if (hasUpdate) {
				DialogHelper.Confirm(
						getActivity(),
						getText(R.string.dialog_update_title),
						getText(R.string.dialog_update_msg).toString()
								+ updateInfo
								+ getText(R.string.dialog_update_msg2)
										.toString(),
						getText(R.string.dialog_update_btnupdate),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

                                updateProgressDialog = new ProgressDialog(getActivity());
                                updateProgressDialog
                                        .setMessage(getText(R.string.dialog_downloading_msg));
                                updateProgressDialog.setIndeterminate(false);
                                updateProgressDialog
                                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                updateProgressDialog.setMax(100);
                                updateProgressDialog.setProgress(0);
                                updateProgressDialog.show();

                                updateMan.downloadPackage();
                            }
                        },getText( R.string.dialog_update_btnnext), null);
            }
        }

        @Override
        public void downloadProgressChanged(int progress) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.setProgress(progress);
            }
        }

        @Override
        public void downloadCanceled() {

        }

        @Override
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.dismiss();
            }
            if (sucess) {
                updateMan.update();
            } else {
                DialogHelper.Confirm(getActivity(),
                        R.string.dialog_error_title,
                        R.string.dialog_downfailed_msg,
                        R.string.dialog_downfailed_btnnext,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateMan.downloadPackage();

                            }
                        }, R.string.dialog_update_btnnext, null);
            }
        }
    };
}
