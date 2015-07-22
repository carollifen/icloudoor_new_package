package com.icloudoor.cloudoor;

import gov.nist.core.Match;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.ShakeEventManager;
import com.icloudoor.cloudoor.UartService;
import com.icloudoor.cloudoor.ChannelSwitchView.OnCheckedChangeListener;
import com.icloudoor.cloudoor.ShakeEventManager.OnShakeListener;
import com.icloudoor.cloudoor.SwitchButton.OnSwitchListener;
import com.icloudoor.cloudoor.activity.RedActivity;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.icdcrypto.ICDCrypto;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;

@SuppressLint("NewApi")
public class KeyFragment extends Fragment {
	private final String mPageName = "KeyFragment";
	private String TAG = this.getClass().getSimpleName();
	
	private MyDataBaseHelper mKeyDBHelper;
	private SQLiteDatabase mKeyDB;
	private final String DATABASE_NAME = "KeyDB.db";
	private final String TABLE_NAME = "KeyInfoTable";
	private final String CAR_TABLE_NAME = "CarKeyTable";
	private final String ZONE_TABLE_NAME = "ZoneTable";

	private ArrayList<HashMap<String, String>> carDoorList;
	private ArrayList<HashMap<String, String>> manDoorList;
	private ArrayList<HashMap<String, String>> officeDoorList;

	private RelativeLayout RlOpenKeyList;

//	private int canDisturb;
	private int haveSound;
	private int canShake;
    private Vibrator vibrator;
	
	// for new UI weather
	private LinearLayout weatherWidge;
	
	private WeatherClick mWeatherClick;
	
	private ImageView weatherBtnLeft;
	private ImageView weatherBtnRight;
	private TextView weatherTemperature;
	private TextView weatherStatus;
	private TextView contentYi;
	private TextView contentJi;
	private int showDay;  // 0 for day one; 1 for day two; 2 for day three
	private String D1, D2, D3;
	private TextView date;

//	private LocationManager locationManager;
//	private double longitude = 0.0;
//	private double latitude = 0.0;
	
	public final Calendar c =  Calendar.getInstance();
	
	public char centigrade = 176;
	
	private String HOST = "https://api.thinkpage.cn/v2/weather/all.json?";
	private URL weatherURL;
	private String Key = "XSI7AKYYBY";
	private RequestQueue mQueue;
	
	private String lhlHOST = UrlUtils.HOST;
	private URL lhlURL;
	private int lhlCode;
	private String sid;
	
	private String day1;
	private String lastRequestLHL;
	private boolean haveRequestLHL = false;
    public boolean mBTScanning = false;
    public boolean mBtStateOpen = false;
    private boolean mLogicScanning;
	private boolean mThisFragment;

	private long mLastRequestTime;
	private long mCurrentRequestTime;
	
	private ImageView keyRedDot;
	private int newNum;
	private String uuid;
	private URL downLoadKeyURL;
	
	// for new channel switch
	private SwitchButton switchBtn;
	private int isChooseCarChannel;   // 1 for car; 2 for man
    public int mOpenDoorState;	// 0 can open; 1 openning; 2 and 3 car door state
	private boolean onlyOneDoor = false;
	private StrokeTextView doorName;
    private ImageView doorNameFlag;
	private TextView scanStatus;
	private ImageView BtnOpenDoor;
    private Animation animation1;

	// for BLE
	private static final int REQUEST_ENABLE_BT = 0;
	private static final long SCAN_PERIOD = 1000; // ms
	private BluetoothAdapter mBluetoothAdapter;
	private UartService mUartService = null;
	private ShakeEventManager mShakeMgr;
	private List<BluetoothDevice> mDeviceList;
	private List<BluetoothDevice> tempCarDoorList;
	private List<BluetoothDevice> tempManDoorList;
	private List<BluetoothDevice> tempOfficeDoorList;
	private Map<String, Integer> mDevRssiValues;

	private int deviceIndexToOpen = 0;
	private Handler mHandlerReset = new Handler();
	private SoundPool mSoundPool;
		
	private String doorIdForOfficeDoor;
	
	private boolean mDoorState = false; // open is true, close is false

//    private Handler mHandler;
//    private MyThread  myThread = null;
    
    private RelativeLayout circleLayout;
    private ImageView circle;
    private ImageView radar;
    
    private SharedPreferences carNumAndPhoneNumShare;
   //
    private LinearLayout channelSwitchLayout;
    private ChannelSwitchView csv;

	public CloudDoorMainActivity activity;

	boolean isDebug = DEBUG.isDebug;
	
	//
	private int reloadTimes;
	private int reloadDays;

	//
	private String foldPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cloudoor/";
	private String fileName = "cacheTrace.txt";
	private String userId;
	private String openDoorDevicdId = null;
	private SimpleDateFormat sDateFormat;
	private String openDoorTime;	
	private String modelNameAndVersion;
	
	private List<HashMap<String, String>> openDoorInfoList;
	
	private UpLoadUtils upLoadUtils;
	
	private Version version;
	
	private Toast mToast;
	
	private String manRssi = null;
	private String carRssi = null;
	private String officeRssi = null;
	private String mandefault = "-85";
	private String cardefault = "-80";
	private String officedefault = "-100";

	Handler mHandler = new Handler();
	
	public KeyFragment() {
		// Required empty public constructor
	}

	private Runnable mRunnableReset = new Runnable() {
		@Override
		public void run() {
			MyDebugLog.e("test616", "car door fail1");
			if (mOpenDoorState > 1) {
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("TIME", openDoorTime);
				map.put("UserID", userId);
				map.put("DoorID", deviceIdTodoorId(openDoorDevicdId));
				map.put("MODEL", modelNameAndVersion);
				map.put("RESULT", String.valueOf(false));
				MobclickAgent.onEvent(getActivity(), "OpenDoorStatistics", map);

				upLoadUtils.writeOpenInfoToFile(openDoorTime, userId, deviceIdTodoorId(openDoorDevicdId), false, modelNameAndVersion);
				
				MyDebugLog.e("test616", "car door fail");
				scanStatus.setText(R.string.can_shake_to_open_door);
				mOpenDoorState = 0;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("test63", "onCreateView");

		activity = (CloudDoorMainActivity)getActivity();

		View view = inflater.inflate(R.layout.key_page, container, false);
		
		modelNameAndVersion = android.os.Build.MODEL + ":" + android.os.Build.VERSION.RELEASE;
		
        vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        
        openDoorInfoList = new ArrayList<HashMap<String, String>>();
        
        upLoadUtils = new UpLoadUtils();
        
        if(getActivity() != null)
			version = new Version(getActivity());
        
        SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
		userId = loginStatus.getString("USERID", null);
        
        carNumAndPhoneNumShare = getActivity().getSharedPreferences("carNumAndPhoneNum", 0);
        
		mKeyDBHelper = new MyDataBaseHelper(getActivity(), DATABASE_NAME);
		mKeyDB = mKeyDBHelper.getWritableDatabase();
		
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(mBluetoothStateReceiver, filter);
		
		RlOpenKeyList = (RelativeLayout) view.findViewById(R.id.open_key_list);

		InitFragmentViews();
		
		// for new UI weather
		date = (TextView) view.findViewById(R.id.date);
		weatherWidge = (LinearLayout) view.findViewById(R.id.weather_widge);
		weatherWidge.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), WeatherDetail.class);
				startActivity(intent);
			}
			
		});
		
		weatherBtnLeft = (ImageView) view.findViewById(R.id.weather_btn_left);
		weatherBtnRight = (ImageView) view.findViewById(R.id.weather_btn_right);

        mWeatherClick = new WeatherClick();
		weatherBtnLeft.setOnClickListener(mWeatherClick);
		weatherBtnRight.setOnClickListener(mWeatherClick);
		showDay = 0; // defaul to show today's weather status
		
		weatherBtnLeft.setVisibility(View.INVISIBLE);

        weatherTemperature = (TextView) view.findViewById(R.id.weather_temp);
		weatherStatus = (TextView) view.findViewById(R.id.weather_status);
		weatherStatus.setSelected(true);
		contentYi = (TextView) view.findViewById(R.id.weather_yi);
		contentJi = (TextView) view.findViewById(R.id.weather_ji);
		contentYi.setSelected(true);
		contentJi.setSelected(true);
		
		keyRedDot = (ImageView) view.findViewById(R.id.key_red_dot);
		SharedPreferences saveNewKeyState = getActivity().getSharedPreferences("SAVESIGN", Context.MODE_PRIVATE);
		if (saveNewKeyState.getString("newKeyState", "false").equals("true")) {
			keyRedDot.setVisibility(View.VISIBLE);
		} else {
			keyRedDot.setVisibility(View.INVISIBLE);
		}
		
		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();
		
		newNum = 0;
		checkForNewKey();		
		
		requestWeatherData();
		
		switchBtn = (SwitchButton) view.findViewById(R.id.btn_switch);
		if(isChooseCarChannel == 1){
			switchBtn.setSwitch(false, 0);
			MyDebugLog.e(TAG, String.valueOf(isChooseCarChannel));
		}else{
			switchBtn.setSwitch(true, 0);
			MyDebugLog.e(TAG, String.valueOf(isChooseCarChannel));
		}
			
		switchBtn.setOnSwitchListener(new OnSwitchListener() {
			@Override
			public boolean onSwitch(SwitchButton v, boolean isRight) {
				if (isRight) {
					isChooseCarChannel = 0;
				} else {
					isChooseCarChannel = 1;
				}
				if (!mBTScanning) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mBTScanning = false;
				}
				populateDeviceList(mBtStateOpen);
				MyDebugLog.e(TAG, "start scanning : " + String.valueOf(isChooseCarChannel));
				return false;
			}
		});
		
		channelSwitchLayout = (LinearLayout) view.findViewById(R.id.channel_switch_layout);
		csv = new ChannelSwitchView(getActivity());
		channelSwitchLayout.addView(csv);
		channelSwitchLayout.setVisibility(View.INVISIBLE);
		csv.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(boolean isChecked) {
				if(!isChecked){
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("doorIdToOpen", doorIdForOfficeDoor);
					intent.putExtras(bundle);
					intent.setClass(getActivity(), DakaDialog.class);
					startActivityForResult(intent, 2);		
				}
				
			}
			
		});
		
		circleLayout = (RelativeLayout) view.findViewById(R.id.circle_layout);
		circle = (ImageView) view.findViewById(R.id.circle);
		radar = (ImageView) view.findViewById(R.id.radar_light);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		LayoutParams param = (LayoutParams) circle.getLayoutParams();
		param.width = screenWidth;
		param.height = screenWidth;
		circle.setLayoutParams(param);
		radar.setLayoutParams(param);
		
		animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.run);
		LinearInterpolator lin1 = new LinearInterpolator();
		animation1.setInterpolator(lin1);

		BtnOpenDoor = (ImageView) view.findViewById(R.id.btn_open_door);
		BtnOpenDoor.setImageResource(R.drawable.door_normalll_v2);
		BtnOpenDoor.setEnabled(false);

		LayoutParams para = BtnOpenDoor.getLayoutParams();
		if(screenWidth > 1080 && screenWidth <= 1440){
			para.width = screenWidth - 246*2;
			para.height = screenWidth - 246*2;
		} else if(screenWidth > 720 && screenWidth <= 1080){
			para.width = screenWidth - 206*2;
			para.height = screenWidth - 206*2;
		} else if(screenWidth > 480 && screenWidth <= 720){
			para.width = screenWidth - 156*2;
			para.height = screenWidth - 156*2;
		} else {
			para.width = screenWidth - 98*2;
			para.height = screenWidth - 98*2;
		}
		
//		para.width = screenWidth - dip2px(160);
//		para.height = screenWidth - dip2px(160);
		
		BtnOpenDoor.setLayoutParams(para);
		
		BtnOpenDoor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(mBtStateOpen == false){
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}else {
					 if (mOpenDoorState == 0) {
						 //TODO
						 /*
							 *  only for test version, we need to judge the reloadTimes & checkWithIn7DaysOrNot 
							 */
						 	SharedPreferences userConfig = getActivity().getSharedPreferences("Config", 0);
						 	Editor editor = userConfig.edit();
						 	reloadTimes = userConfig.getInt("TIMES", 0);
						 	if(reloadTimes > 0 && checkWithin7DaysOrNot()){
						 		mOpenDoorState = 1; // doing opendoor
			                    Log.i("test", "doOpenDoor");
			                    
			                    if(haveSound == 1){
			                    	playOpenDoorSound();
			                    }

			                    doOpenDoor(mBtStateOpen); //ONLY FOR TEST
			                    
			                    MyDebugLog.e(TAG, "reloadTimes before open: " + String.valueOf(reloadTimes));			                    
			                    reloadTimes--;
			                    MyDebugLog.e(TAG, "reloadTimes after open: " + String.valueOf(reloadTimes));	
			                    editor.putInt("TIMES", reloadTimes);
			                    editor.commit();
						 	} else {
						 		if(getActivity() != null){
						 			toastShow(getString(R.string.plz_login_again));
//						 			Toast.makeText(getActivity(), R.string.plz_login_again, Toast.LENGTH_SHORT).show();
						 			
						 			SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
                                    Editor editor1 = loginStatus.edit();
                                    editor1.putInt("LOGIN", 0);
                                    editor1.commit();
                                    
                                    saveSid(null);
                                    
                                    SharedPreferences previousNum = getActivity().getSharedPreferences("PREVIOUSNUM", 0);
                                	previousNum.edit().putString("NUM", loginStatus.getString("PHONENUM", null)).commit();
                                    
//                                    String sql = "DELETE FROM " + TABLE_NAME +";";
//                                    mKeyDB.execSQL(sql);
//                                    
//                                    String sq2 = "DELETE FROM " + CAR_TABLE_NAME +";";
//                                    mKeyDB.execSQL(sq2);
//                                    
//                                    String sq3 = "DELETE FROM " + ZONE_TABLE_NAME +";";
//                                    mKeyDB.execSQL(sq3);
                                    
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), Login.class);
                                    startActivity(intent);
                                    
                                    CloudDoorMainActivity mainActivity = (CloudDoorMainActivity) getActivity();
                                    mainActivity.finish();
						 		}
						 	}
		                }
				}   
			}

		});
		
		doorName = (StrokeTextView) view.findViewById(R.id.door_name);
        doorNameFlag = (ImageView) view.findViewById(R.id.door_name_flag);
		scanStatus = (TextView) view.findViewById(R.id.scan_status);
		scanStatus.setText(R.string.can_shake_to_open_door);

		mShakeMgr = new ShakeEventManager(getActivity());
		mShakeMgr.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				if(canShake == 1 && mThisFragment){
					if(mBtStateOpen == false){
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}else {
						if (mOpenDoorState == 0) {
							if (!mLogicScanning) {
								mOpenDoorState = 1;
								Log.i("test", "doOpenDoor");
								doOpenDoor(mBtStateOpen);
							}
						}
					}
				}
			}
		});

		RlOpenKeyList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyRedDot.setVisibility(View.INVISIBLE); 
				SharedPreferences saveNewKeyState = getActivity().getSharedPreferences("SAVESIGN",
						Context.MODE_PRIVATE);
				if (saveNewKeyState.getString("newKeyState", "false").equals("true")) {
					Editor editor = saveNewKeyState.edit();
					editor.putString("newKeyState", "false");
					editor.commit();
				}

				Intent intent = new Intent();
				intent.setClass(getActivity(), KeyList.class);
				startActivity(intent);
			}
		});

		if(getActivity() != null)
			configRssiData();
		
		return view;
	}
	
//	public int dip2px(float dpValue) {
//		final float scale = getResources().getDisplayMetrics().density;
//		return (int) (dpValue * scale + 0.5f);
//	}
	
	public void configRssiData() {
		manRssi = OnlineConfigAgent.getInstance().getConfigParams(getActivity(), "manDoorRssi");
		carRssi = OnlineConfigAgent.getInstance().getConfigParams(getActivity(), "carDoorRssi");
		officeRssi = OnlineConfigAgent.getInstance().getConfigParams(getActivity(), "officeDoorRssi");
		
		SharedPreferences rssi = getActivity().getSharedPreferences("RSSI", 0);
		Editor editor = rssi.edit();

		if(manRssi.length() > 0 && carRssi.length() > 0 && officeRssi.length() > 0){
			File f = new File(
					"/data/data/com.icloudoor.cloudoor/shared_prefs/RSSI.xml");
			if (f.exists()) {
				if (rssi.contains("mrssi")) {
					if (manRssi.equals(rssi.getString("mrssi", null))) {

					} else {
						editor.putString("mrssi", manRssi);
					}
				} else {
					editor.putString("mrssi", manRssi);
				}

				if (rssi.contains("crssi")) {
					if (carRssi.equals(rssi.getString("crssi", null))) {

					} else {
						editor.putString("crssi", carRssi);
					}
				} else {
					editor.putString("crssi", carRssi);
				}

				if (rssi.contains("orssi")) {
					if (officeRssi.equals(rssi.getString("orssi", null))) {

					} else {
						editor.putString("orssi", officeRssi);
					}
				} else {
					editor.putString("orssi", officeRssi);
				}
			} else {
				editor.putString("mrssi", manRssi);
				editor.putString("crssi", carRssi);
				editor.putString("orssi", officeRssi);
			}
		} else {
			manRssi = mandefault;
			carRssi = cardefault;
			officeRssi = officedefault;
			editor.putString("mrssi", manRssi);
			editor.putString("crssi", carRssi);
			editor.putString("orssi", officeRssi);
		}
		
		editor.commit();

		Log.e("********", manRssi + " " + carRssi + " " + officeRssi);
	}
	
	public boolean checkWithin7DaysOrNot(){
		boolean isWithin = false;
		int previousYear;
		int previousMonth;
		int previousDay;
		int nowYear;
		int nowMonth;
		int nowDay;
		
		SharedPreferences userConfig = getActivity().getSharedPreferences("Config", 0);
		previousYear = userConfig.getInt("YEAR", 0);
		previousMonth = userConfig.getInt("MONTH", 0);
		previousDay = userConfig.getInt("DAY", 0);
		if(previousYear == 0 || previousMonth == 0 || previousDay == 0){
			isWithin = false;
		} else {
			Time now = new Time();
			now.setToNow();
			nowYear = now.year;
			nowMonth = now.month + 1;
			nowDay = now.monthDay;
			
			MyDebugLog.e(TAG, "nowYear: " + String.valueOf(nowYear));
			MyDebugLog.e(TAG, "nowMonth: " + String.valueOf(nowMonth));
			MyDebugLog.e(TAG, "nowDay: " + String.valueOf(nowDay));
			
			// check start
			if(nowYear == previousYear && nowMonth == previousMonth && nowDay == previousDay){  // in the same day, same month, same year
				isWithin = true;
			} else if(nowYear == previousYear && nowMonth == previousMonth){  // in the same month, same year
				if(nowDay - previousDay <= 6)
					isWithin = true;
				else 
					isWithin = false;
			} else if(nowYear == previousYear){ // in the same year
				if(nowMonth - previousMonth > 1)
					isWithin = false;
				else{
					if(isSmallMonth(nowMonth) || (nowMonth == 2)){
						if(31 - previousDay + nowDay <= 6)
							isWithin = true;
						else 
							isWithin = false;
					} else if(isBigMonth(nowMonth)){
						if(nowMonth == 3){
							if(isLeapYear(previousMonth)){
								if(29 - previousDay + nowDay <= 6)
									isWithin = true;
								else 
									isWithin = false;
							}else{
								if(28 - previousDay + nowDay <= 6)
									isWithin = true;
								else 
									isWithin = false;
							}
						} else if(nowMonth == 8){
							if(31 - previousDay + nowDay <= 6)
								isWithin = true;
							else 
								isWithin = false;
						} else{
							if(30 - previousDay + nowDay <= 6)
								isWithin = true;
							else 
								isWithin = false;
						}
					}
				}
			} else {  // in diff year
				if(nowYear - previousYear > 1)
					isWithin = false;
				else{
					if(previousMonth < 12)
						isWithin = false;
					else if(nowMonth > 1)
						isWithin = false;
					else {
						if(31 - previousDay + nowDay <= 6)
							isWithin = true;
						else 
							isWithin = false;
					}
				}
			}
		}		
		return isWithin;
	}
	
	public class DownloadeKeyTask implements Runnable{

		private JSONObject response = null;

		public DownloadeKeyTask (JSONObject data) {
			this.response = data;
		}
		public void run(){

			try {
				parseKeyData(response);
				if (response.getString("sid") != null)
					saveSid(response.getString("sid"));

			}catch (JSONException e){
				Log.e("error", "There is a error");
			}

//			MyDebugLog.e(TAG, response.toString());

			if(carDoorList != null){
				carDoorList.clear();
				carDoorList = null;
			}
			if(manDoorList != null){
				manDoorList.clear();
				manDoorList = null;
			}
			if(officeDoorList != null){
				officeDoorList.clear();
				officeDoorList = null;
			}

			carDoorList = new ArrayList<HashMap<String, String>>();
			manDoorList = new ArrayList<HashMap<String, String>>();
			officeDoorList = new ArrayList<HashMap<String, String>>();

			//TODO DELETE TEMPARY
			if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
				if (DBCount() > 0) {

					Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
							null);
					if (mCursor.moveToFirst()) {
						int zoneIdIndex = mCursor.getColumnIndex("zoneId");
						int deviceIdIndex = mCursor.getColumnIndex("deviceId");
						int doorNamemIndex = mCursor.getColumnIndex("doorName");
						int doorTypeIndex = mCursor.getColumnIndex("doorType");
						int directionIndex = mCursor.getColumnIndex("direction");
						int doorIdIndex = mCursor.getColumnIndex("doorId");

						do {
							HashMap<String, String> temp = new HashMap<String, String>();
							String deviceId = mCursor.getString(deviceIdIndex);
							String doorName = mCursor.getString(doorNamemIndex);
							String doorType = mCursor.getString(doorTypeIndex);
							String direction = mCursor.getString(directionIndex);
							String zoneId = mCursor.getString(zoneIdIndex);
							String doorId = mCursor.getString(doorIdIndex);

												/*  Add new logic for car key
												 *  select the car doors can be opened,
												 *  and all the man doors
												 */
							if (doorType.equals("2")) {
								MyDebugLog.e(TAG, "add a car key");
								temp.put("CDdeviceid", deviceId);
								temp.put("CDdoorName", doorName);
								temp.put("CDdoorType", doorType);
								temp.put("CDDirection", direction);
								carDoorList.add(temp);
							} else if (doorType.equals("1")) {
								MyDebugLog.e(TAG, "add man key");
								temp.put("MDdeviceid", deviceId);
								temp.put("MDdoorName", doorName);
								temp.put("MDdoorType", doorType);
								temp.put("MDDirection", direction);
								manDoorList.add(temp);
							} else if (doorType.equals("3")) {
								MyDebugLog.e(TAG, "add office key");
								temp.put("ODdeviceid", deviceId);
								temp.put("ODdoorName", doorName);
								temp.put("ODdoorType", doorType);
								temp.put("ODDirection", direction);
								temp.put("ODdoorId", doorId);
								officeDoorList.add(temp);
							}
						} while (mCursor.moveToNext());
					}
					mCursor.close();
				}
			}
		}

	}
	
	public void checkForNewKey() {
		uuid = loadUUID();
		if (uuid == null) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			saveUUID(uuid);
		}

		try {
			downLoadKeyURL = new URL(
					UrlUtils.HOST + "/user/door/download2.do"
							+ "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				downLoadKeyURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getInt("code") == 1) {

//								parseKeyData(response);
//
								MyDebugLog.e(TAG, response.toString());
//
//								if (response.getString("sid") != null)
//									saveSid(response.getString("sid"));
//								
//								if(carDoorList != null){
//						        	carDoorList.clear();
//						        	carDoorList = null;
//						        }
//						        if(manDoorList != null){
//						        	manDoorList.clear();
//						        	manDoorList = null;
//						        }
//						        if(officeDoorList != null){
//						        	officeDoorList.clear();
//						        	officeDoorList = null;
//						        }
//						        
//						        carDoorList = new ArrayList<HashMap<String, String>>();
//								manDoorList = new ArrayList<HashMap<String, String>>();
//								officeDoorList = new ArrayList<HashMap<String, String>>();
//								
//								//TODO DELETE TEMPARY
//								if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
//									if (DBCount() > 0) {
//										
//										Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
//												null);
//										if (mCursor.moveToFirst()) {
//											int zoneIdIndex = mCursor.getColumnIndex("zoneId");
//											int deviceIdIndex = mCursor.getColumnIndex("deviceId");
//											int doorNamemIndex = mCursor.getColumnIndex("doorName");
//											int doorTypeIndex = mCursor.getColumnIndex("doorType");
//											int directionIndex = mCursor.getColumnIndex("direction");
//											int doorIdIndex = mCursor.getColumnIndex("doorId");
//
//											do {
//												HashMap<String, String> temp = new HashMap<String, String>();
//												String deviceId = mCursor.getString(deviceIdIndex);
//												String doorName = mCursor.getString(doorNamemIndex);
//												String doorType = mCursor.getString(doorTypeIndex);
//												String direction = mCursor.getString(directionIndex);
//												String zoneId = mCursor.getString(zoneIdIndex);
//												String doorId = mCursor.getString(doorIdIndex);
//
//												/*  Add new logic for car key
//												 *  select the car doors can be opened, 
//												 *  and all the man doors
//												 */
//												if (doorType.equals("2")) {						
//													MyDebugLog.e(TAG, "add a car key");
//													temp.put("CDdeviceid", deviceId);
//													temp.put("CDdoorName", doorName);
//													temp.put("CDdoorType", doorType);
//													temp.put("CDDirection", direction);
//													carDoorList.add(temp);		
//												} else if (doorType.equals("1")) {
//													MyDebugLog.e(TAG, "add man key");
//													temp.put("MDdeviceid", deviceId);
//													temp.put("MDdoorName", doorName);
//													temp.put("MDdoorType", doorType);
//													temp.put("MDDirection", direction);
//													manDoorList.add(temp);
//												} else if (doorType.equals("3")) {
//													MyDebugLog.e(TAG, "add office key");
//													temp.put("ODdeviceid", deviceId);
//													temp.put("ODdoorName", doorName);
//													temp.put("ODdoorType", doorType);
//													temp.put("ODDirection", direction);
//													temp.put("ODdoorId", doorId);
//													officeDoorList.add(temp);
//												}
//											} while (mCursor.moveToNext());
//										}
//						                mCursor.close();
//									}
//								}
                                new Thread(new DownloadeKeyTask(response)).start();
								
							} else if (response.getInt("code") == -81) {
								if (getActivity() != null)
									toastShow(getString(R.string.have_no_key_authorised));
//									Toast.makeText(getActivity(), R.string.have_no_key_authorised, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Log.e(TAG, "request error");
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(getActivity() != null){
							toastShow(getString(R.string.network_error));
						}
//							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("appId", uuid);
				return map;
			}
		};

		mQueue.add(mJsonRequest);
	}
	
	public void parseKeyData(JSONObject response) throws JSONException {
		MyDebugLog.e("test for new interface", "parseKeyData func");
		
		// for new key download interface
		JSONObject data = response.getJSONObject("data");
		JSONArray doorAuths = data.getJSONArray("doorAuths");
		JSONArray zones = data.getJSONArray("zones");
		JSONArray cars = data.getJSONArray("cars");
		
		// for doorauths table -- START
		for (int index = 0; index < doorAuths.length(); index++) {
			JSONObject doorData = (JSONObject) doorAuths.get(index);
			
			ContentValues value = new ContentValues();
			
			if(doorData.getString("deviceId").length() > 0){
				if(!hasData(mKeyDB, doorData.getString("deviceId").toUpperCase())){    //insert the new key
					
					newNum++;
					
					value.put("zoneId", doorData.getString("zoneId"));
					value.put("doorName", doorData.getString("doorName"));
					value.put("doorId", doorData.getString("doorId"));
					value.put("deviceId", doorData.getString("deviceId").toUpperCase());
					value.put("doorType", doorData.getString("doorType"));
					value.put("authFrom", doorData.getString("authFrom"));
					value.put("authTo", doorData.getString("authTo"));
					
					if (!doorData.getString("doorType").equals("2")) {
						MyDebugLog.e(TAG, "add a 1");
						value.put("direction", "none");
						value.put("plateNum", "none");
						mKeyDB.insert(TABLE_NAME, null, value);
					} else if (doorData.getString("doorType").equals("2")){
						MyDebugLog.e(TAG, "add a 2");
								value.put("plateNum", doorData.getString("plateNum"));
								value.put("direction", doorData.getString("direction"));
								mKeyDB.insert(TABLE_NAME, null, value);
					}
					MyDebugLog.e(TAG, "after parse: " + String.valueOf(DBCount()));
				} else {            // update the old key status
						ContentValues valueTemp = new ContentValues();
						valueTemp.put("doorName", doorData.getString("doorName"));
						valueTemp.put("authFrom", doorData.getString("authFrom"));
						valueTemp.put("authTo", doorData.getString("authTo"));
						
						mKeyDB.update("KeyInfoTable", valueTemp, "deviceId = ?", new String[] {doorData.getString("deviceId")});
				}
			}	
		}
		
		//need to delete the old key
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey;
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					
					do{
						keepKey = false;
						String deviceId = mCursor.getString(deviceIdIndex).toUpperCase();
						for (int index = 0; index < doorAuths.length(); index++) {
							JSONObject doorData = (JSONObject) doorAuths.get(index);
							
							if(doorData.getString("deviceId").length() > 0){
								if((doorData.getString("deviceId")).toUpperCase().equals(deviceId)){
									keepKey = true;
									break;
								}
							}
						}	
						
						if(!keepKey){
							MyDebugLog.e(TAG, "delete a");
							//delete in the table
							mKeyDB.delete("KeyInfoTable", "deviceId = ?", new String[] {deviceId});
						}
						
					}while(mCursor.moveToNext());				
				}	
				mCursor.close();
			}
		}
		// for doorauths table -- END
		
		// for zones table -- START
		for (int index = 0; index < zones.length(); index++) {
			JSONObject zoneData = (JSONObject) zones.get(index);
			ContentValues value = new ContentValues();
			
			if(zoneData.getString("zoneId").length() > 0){
				if(!hasZoneData(mKeyDB, zoneData.getString("zoneId"))){   // insert new
					MyDebugLog.e(TAG, "add a zone");
					value.put("zoneid", zoneData.getString("zoneId"));		
					value.put("zonename", zoneData.getString("zoneName"));
					value.put("parentzoneid", zoneData.getString("parentZoneId"));
					mKeyDB.insert(ZONE_TABLE_NAME, null, value);
				}
			}
		}
		
		// delete old
		if (mKeyDBHelper.tabIsExist(ZONE_TABLE_NAME)) {
			if (DBCountZone() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					boolean keepKey;
					int zoneidIndex = mCursor.getColumnIndex("zoneid");
					
					do{
						keepKey = false;
						String zoneid = mCursor.getString(zoneidIndex);
						for (int index = 0; index < zones.length(); index++) {
							JSONObject zoneData = (JSONObject) zones.get(index);
							
							if(zoneData.getString("zoneId").length() > 0){
								if(zoneData.getString("zoneId").equals(zoneid)){
									keepKey = true;
									break;
								}
							}
						}		
						
						if(!keepKey){
							MyDebugLog.e(TAG, "delete a zone");
							mKeyDB.delete("ZoneTable", "zoneId = ?", new String[] {zoneid});
						}
					}while(mCursor.moveToNext());
				}
				mCursor.close();
			}
		}
		// for zones table -- END
		
		// for cars table -- START
		for (int index = 0; index < cars.length(); index++) {
			JSONObject carData = (JSONObject) cars.get(index);
			
			if(carData.getString("l1ZoneId").length() > 0){
				if(!hasCarData(mKeyDB, carData.getString("l1ZoneId"), carData.getString("plateNum"))){   // insert new
					MyDebugLog.e(TAG, "add a car");
					ContentValues value = new ContentValues();
					value.put("l1ZoneId", carData.getString("l1ZoneId"));
					value.put("plateNum", carData.getString("plateNum"));
					value.put("carStatus", carData.getString("carStatus"));
					value.put("carPosStatus", carData.getString("carPosStatus"));
					mKeyDB.insert(CAR_TABLE_NAME, null, value);
					
					// refresh the carPosStatus to "0" if carPosStatus not "0" and you hava a car key (own or borrowed) when you get the new car key
					if(!carData.getString("carPosStatus").equals("0") && !carData.getString("carStatus").equals("3")){
						updatePosStatus(carData.getString("l1ZoneId"), carData.getString("plateNum"));
					}
				}else{   // update old
					ContentValues value = new ContentValues();
					
					if(carData.getString("plateNum").equals(carNumAndPhoneNumShare.getString("CARNUM", null))){
						value.put("carStatus", carData.getString("carStatus"));
						value.put("carPosStatus", carData.getString("carPosStatus"));
						
						mKeyDB.update("CarKeyTable", value, "l1ZoneId = ? and plateNum = ?", new String[] {carData.getString("l1ZoneId"), carData.getString("plateNum")});
					}
				}
			}
		}
		
		// delete old
		
			if (mKeyDBHelper.tabIsExist("CarKeyTable")) {
				if(DBCountCar() > 0){
					Cursor mCursor = mKeyDB.rawQuery("select * from " + "CarKeyTable", null);
					if(mCursor.moveToFirst()){
						boolean keepKey;
						int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
						int plateNumIndex = mCursor.getColumnIndex("plateNum");

						do{
							keepKey = false;
							String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
							String plateNum = mCursor.getString(plateNumIndex);
							for (int index = 0; index < cars.length(); index++) {
								JSONObject carData = (JSONObject) cars.get(index);
								
								if(carData.getString("l1ZoneId").length() > 0){
									if(carData.getString("l1ZoneId").equals(l1ZoneId) && carData.getString("plateNum").equals(plateNum)){
										keepKey = true;
										break;
									}
								}
							}
							if(!keepKey){
								MyDebugLog.e(TAG, "delete a car");
								mKeyDB.delete("CarKeyTable", "l1ZoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
								mKeyDB.delete("KeyInfoTable", "zoneId = ? and plateNum = ?", new String[] {l1ZoneId, plateNum});
							}
						}while(mCursor.moveToNext());
					}
					mCursor.close();
				}
			}
		// for cars table -- END
			
		if (newNum > 0) {
			if(getActivity() != null){
				SharedPreferences saveNewKeyState = getActivity().getSharedPreferences("SAVESIGN", Context.MODE_PRIVATE);
				Editor editor = saveNewKeyState.edit();
				editor.putString("newKeyState", "true");
				editor.commit();
			}
				
			if(getActivity() != null){
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						keyRedDot.setVisibility(View.VISIBLE);
					}
				});
			}
		}
	}
	
	public void updatePosStatus(final String zoneid, final String carnum) {
		URL updateCarPosStatusURL = null;
		String sid2 = null;
		RequestQueue mQueue2;
		
		sid2 = loadSid();
		try {
			updateCarPosStatusURL = new URL(HOST + "/user/api/updateCarPosStatus.do" + "?sid=" + sid2 + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		mQueue2 = Volley.newRequestQueue(getActivity());
		
		MyJsonObjectRequest mJsonRequest2 = new MyJsonObjectRequest(Method.POST, updateCarPosStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e(TAG, "test " + response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(getActivity() != null){
							toastShow(getString(R.string.network_error));
						}
//							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("l1ZoneId", zoneid);
				map.put("plateNum", carnum);
				map.put("carPosStatus", "0");
				return map;
			}
		};
		mQueue2.add(mJsonRequest2);
	}

	// for new channel switch
	private int getState(boolean state) {
    	if(state) {
    		return 1;
    	} 
    	return 0;
    }
	
//	// for new UI weather
//	private void toggleGPS() {
//		Intent gpsIntent = new Intent();
//		gpsIntent.setClassName("com.android.settings",
//				"com.android.settings.widget.SettingsAppWidgetProvider");
//		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
//		gpsIntent.setData(Uri.parse("custom:3"));
//		try {
//			PendingIntent.getBroadcast(getActivity(), 0, gpsIntent, 0).send();
//		} catch (CanceledException e) {
//			e.printStackTrace();
//			locationManager
//					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//							1000, 0, locationListener);
//			Location location1 = locationManager
//					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			if (location1 != null) {
//				latitude = location1.getLatitude(); 
//				longitude = location1.getLongitude(); 
//			}
//		}
//	}
//	
//	private void getLocation() {
//		Location location = locationManager
//				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (location != null) {
//			latitude = location.getLatitude();
//			longitude = location.getLongitude();
//		} else {
//
//			locationManager.requestLocationUpdates(
//					LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
//		}
//	}

//	LocationListener locationListener = new LocationListener() {
//		
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//		}
//
//		
//		@Override
//		public void onProviderEnabled(String provider) {
//			
//		}
//
//		
//		@Override
//		public void onProviderDisabled(String provider) {
//			
//		}
//
//		
//		@Override
//		public void onLocationChanged(Location location) {
//			if (location != null) {
//				Log.e("Map",
//						"Location changed : Lat: " + location.getLatitude()
//								+ " Lng: " + location.getLongitude());
//				latitude = location.getLatitude(); 
//				longitude = location.getLongitude(); 
//			}
//		}
//	};

	public boolean isBigMonth(int m) {
		if(m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) 
			return true;	
		return false;
	}
	
	public boolean isSmallMonth(int m) {
		if(m == 4 || m == 6 || m == 9 || m == 11) 
			return true;	
		return false;
	}
	
	public boolean isLeapYear(int y) {
		if((y%4==0 && y%100!=0) || y%400==0) 
			return true;	
		return false;
	}
	
	@SuppressLint("SimpleDateFormat")
	public void requestWeatherData() {
		
        DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int screenWidth = dm.widthPixels;
		final Calendar c =  Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); 
		
		D1 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month)
				+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + getString(R.string.day);
		
		if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 
			if(c.get(Calendar.DAY_OF_MONTH) == 31){
				if((c.get(Calendar.MONTH) + 1) == 12){   
					D2 = String.valueOf(1) + getString(R.string.month)
							+ String.valueOf(1) + getString(R.string.day);
				}else{
					D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
							+ String.valueOf(1) + getString(R.string.day);
				}
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);	
			}
		}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){     
			if(c.get(Calendar.DAY_OF_MONTH) == 30){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {        
			if(c.get(Calendar.DAY_OF_MONTH) == 29){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     
			if(c.get(Calendar.DAY_OF_MONTH) == 28){
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf(1) + getString(R.string.day);
			}else{
				D2 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+1) + getString(R.string.day);
			}
		}
		
		if(isBigMonth(c.get(Calendar.MONTH) + 1)){                 
			if(c.get(Calendar.DAY_OF_MONTH) == 31 || c.get(Calendar.DAY_OF_MONTH) == 30){
				if((c.get(Calendar.MONTH) + 1) == 12){  
					D3 = String.valueOf(1) + getString(R.string.month)
							+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31) + getString(R.string.day);
				}else{
					D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
							+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%31) + getString(R.string.day);
				}
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);	
			}
		}else if(isSmallMonth(c.get(Calendar.MONTH) + 1)){     
			if(c.get(Calendar.DAY_OF_MONTH) == 30 || c.get(Calendar.DAY_OF_MONTH) == 29){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%30) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}else if(isLeapYear(c.get(Calendar.MONTH) + 1)) {        
			if(c.get(Calendar.DAY_OF_MONTH) == 29 || c.get(Calendar.DAY_OF_MONTH) == 28){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%29) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}else if(!(isLeapYear(c.get(Calendar.MONTH) + 1))){     
			if(c.get(Calendar.DAY_OF_MONTH) == 28 || c.get(Calendar.DAY_OF_MONTH) == 27){
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1 + 1) + getString(R.string.month) 
						+ String.valueOf((c.get(Calendar.DAY_OF_MONTH)+2)%28) + getString(R.string.day);
			}else{
				D3 = String.valueOf(c.get(Calendar.MONTH) + 1) + getString(R.string.month) 
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH)+2) + getString(R.string.day);
			}
		}	
		
		MyDebugLog.e(TAG, D1 + " " + D2 + " " + D3);
		
		date.setText(D1);
		
		// To get the longitude and latitude
//		locationManager = (LocationManager) getActivity().getSystemService(
//				Context.LOCATION_SERVICE);
//		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			getLocation();
//		} else {
//			toggleGPS();
//			new Handler() {
//			}.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					getLocation();
//				}
//			}, 2000);
//		}

		// INIT
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		day1 = formatter.format(date);

		SharedPreferences saveRequestLHL = getActivity().getSharedPreferences(
				"LHLREQUESTDATE", 0);
		lastRequestLHL = saveRequestLHL.getString("LHLlastrequestdate", null);
		if (day1.equals(lastRequestLHL))
			haveRequestLHL = true;
		else
			haveRequestLHL = false;

		try {
//					weatherURL = new URL(HOST + "city=" + String.valueOf(latitude)
//							+ ":" + String.valueOf(longitude)
//							+ "&language=zh-chs&unit=c&aqi=city&key=" + Key);
					
			weatherURL = new URL(HOST + "city=ip&language=zh-chs&unit=c&aqi=city&key=" + Key);

			lhlURL = new URL(lhlHOST + "/user/data/laohuangli/get.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		MyJsonObjectRequest mLhlRequest = new MyJsonObjectRequest(Method.POST,
				lhlURL.toString(), null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e(TAG, response.toString());
						
						try {
							if (response.getString("sid") != null) {
								sid = response.getString("sid");
								saveSid(sid);
							}
							lhlCode = response.getInt("code");
							
							if(lhlCode == 1){
								JSONArray data = response.getJSONArray("data");
								JSONObject Day1 = (JSONObject) data.get(0);
								JSONObject Day2 = (JSONObject) data.get(1);
								JSONObject Day3 = (JSONObject) data.get(2);
								if (getActivity() != null) {
                                    SharedPreferences savedLHL = getActivity().getSharedPreferences("SAVEDLHL",
                                            0);
                                    Editor editor = savedLHL.edit();
                                    editor.putString("D1YI", Day1.getString("yi"));
                                    editor.putString("D1JI", Day1.getString("ji"));
                                    editor.putString("D1YINLI", Day1.getString("yinli"));
                                    editor.putString("D2YI", Day2.getString("yi"));
                                    editor.putString("D2JI", Day2.getString("ji"));
                                    editor.putString("D2YINLI", Day2.getString("yinli"));
                                    editor.putString("D3YI", Day3.getString("yi"));
                                    editor.putString("D3JI", Day3.getString("ji"));
                                    editor.putString("D3YINLI", Day3.getString("yinli"));
                                    editor.commit();
                                }
								contentYi.setText(Day1.getString("yi"));
								contentJi.setText(Day1.getString("ji"));
								
								if (getActivity() != null) {
									SharedPreferences saveRequestLHL = getActivity().getSharedPreferences("LHLREQUESTDATE",
											0);
									Editor editor1 = saveRequestLHL.edit();
									editor1.putString("LHLlastrequestdate", day1);
									editor1.commit();
								}	
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(getActivity() != null){
							toastShow(getString(R.string.network_error));
						}
//							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("date", day1);
				map.put("days", "3");
				return map;
			}
		};
		if (!haveRequestLHL) {
			mQueue.add(mLhlRequest);
		} else {
			SharedPreferences loadLHL = getActivity().getSharedPreferences("SAVEDLHL", 0);
			contentYi.setText(loadLHL.getString("D1YI", null));
			contentJi.setText(loadLHL.getString("D1JI", null));
		}

		JsonObjectRequest mWeatherRequest = new JsonObjectRequest(Method.GET,
				weatherURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e(TAG, response.toString());
						
						try {
							if(response.getString("status").equals("OK")){
								JSONArray weather= response.getJSONArray("weather");
								JSONObject data = (JSONObject)weather.get(0);							
								JSONObject now = data.getJSONObject("now");
								JSONArray future = data.getJSONArray("future");
								JSONObject tomorrow= (JSONObject)future.get(0);	
								JSONObject tomorrow2= (JSONObject)future.get(1);
								if (getActivity() != null) {
                                    SharedPreferences savedWeather = getActivity().getSharedPreferences("SAVEDWEATHER",
                                            0);
                                    Editor editor = savedWeather.edit();
                                    editor.putString("City", data.getString("city_name"));
                                    editor.putString("Day1Temp", now.getString("temperature"));
                                    editor.putString("Day1Weather", now.getString("text"));
                                    editor.putString("Day1IconIndex", now.getString("code"));
                                    editor.putString("Day2TempLow", tomorrow.getString("low"));
                                    editor.putString("Day2TempHigh", tomorrow.getString("high"));
                                    editor.putString("Day2Weather", tomorrow.getString("text"));
                                    editor.putString("Day2IconIndexDay", tomorrow.getString("code1"));
                                    editor.putString("Day2IconIndexNight", tomorrow.getString("code2"));
                                    editor.putString("Day3TempLow", tomorrow2.getString("low"));
                                    editor.putString("Day3TempHigh", tomorrow2.getString("high"));
                                    editor.putString("Day3Weather", tomorrow2.getString("text"));
                                    editor.putString("Day3IconIndexDay", tomorrow2.getString("code1"));
                                    editor.putString("Day3IconIndexNight", tomorrow2.getString("code2"));

                                    editor.commit();
                                }
                                weatherTemperature.setText(now.getString("temperature") + String.valueOf(centigrade));
								weatherStatus.setText(now.getString("text"));
								weatherTemperature.setTextSize(19);
								weatherStatus.setTextSize(13);
							} else {
								weatherTemperature.setText(getString(R.string.weather_not_available));
								if(screenWidth <= 480)
									weatherTemperature.setTextSize(12);
								else
								weatherTemperature.setTextSize(16);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						
						if(getActivity() != null){
							toastShow(getString(R.string.network_error));
							weatherTemperature.setText(getString(R.string.weather_not_available));
							if(screenWidth <= 480)
								weatherTemperature.setTextSize(12);
							else
							weatherTemperature.setTextSize(16);
						}
//							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
		};
		mLastRequestTime = loadLastRequestTime();
		mCurrentRequestTime = System.currentTimeMillis();
		if ((mCurrentRequestTime - mLastRequestTime) / 1000 >= 10800) {
			saveLastRequestTime(mCurrentRequestTime);
			mQueue.add(mWeatherRequest);
		} else {
			SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER", 0);
			
			if(loadWeather.getString("Day1Temp", "N/A").equals("N/A")){
				weatherTemperature.setText(getString(R.string.weather_not_available));
				if(screenWidth <= 480)
					weatherTemperature.setTextSize(12);
				else
				weatherTemperature.setTextSize(16);
			}else{
				weatherTemperature.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade)); //TODO
				weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
				
				weatherTemperature.setTextSize(19);
				weatherStatus.setTextSize(13);
			}
			
		}

	}

	public class WeatherClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			SharedPreferences loadWeather = getActivity().getSharedPreferences("SAVEDWEATHER", 0);
			SharedPreferences loadLHL = getActivity().getSharedPreferences("SAVEDLHL", 0);
			
			if(v.getId() == R.id.weather_btn_left) {
				Log.e(TAG, "click left");
				if(showDay == 1) {    // now the day two weather, to show the day one weather
					showDay--;
					if(loadWeather.getString("Day1Temp", "N/A").equals("N/A")){
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day1Temp", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day1Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
						weatherStatus.setTextSize(13);
					}

					date.setText(D1);
					
					contentYi.setText(loadLHL.getString("D1YI", null));
					contentJi.setText(loadLHL.getString("D1JI", null));
					
					weatherBtnLeft.setVisibility(View.INVISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				} else if(showDay == 2) {   // now the day three weather, to show the day two weather
					showDay--;
					if(loadWeather.getString("Day2TempHigh", "N/A").equals("N/A")){
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
					
					date.setText(D2);
                    
					contentYi.setText(loadLHL.getString("D2YI", null));
					contentJi.setText(loadLHL.getString("D2JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				}
			}else if(v.getId() == R.id.weather_btn_right) {
				Log.e(TAG, "click right");
				if(showDay == 0) {    // now the day one weather, to show the day two weather
					showDay++;
					if(loadWeather.getString("Day2TempHigh", "N/A").equals("N/A")){
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day2TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day2Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
					
					date.setText(D2);
                    
					contentYi.setText(loadLHL.getString("D2YI", null));
					contentJi.setText(loadLHL.getString("D2JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.VISIBLE);
				} else if(showDay == 1) {   // now the day two weather, to show the day three weather
					showDay++;
					if(loadWeather.getString("Day3TempHigh", "N/A").equals("N/A")){
						weatherTemperature.setText(getString(R.string.weather_not_available));
						weatherTemperature.setTextSize(16);
					}else{
						weatherTemperature.setText(loadWeather.getString("Day3TempHigh", "N/A") + String.valueOf(centigrade));
						weatherStatus.setText(loadWeather.getString("Day3Weather", "N/A"));
						
						weatherTemperature.setTextSize(19);
						weatherStatus.setTextSize(13);
					}
                    
					date.setText(D3);
					
					contentYi.setText(loadLHL.getString("D3YI", null));
					contentJi.setText(loadLHL.getString("D3JI", null));
					
					weatherBtnLeft.setVisibility(View.VISIBLE);
					weatherBtnRight.setVisibility(View.INVISIBLE);
				}
			}
		}
		
	};
	
	@Override
	public void onStart() {
		MyDebugLog.e("TEST63", "keyFragment onStart()");
		super.onStart();
		mThisFragment = true;
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        BluetoothManager mBluetoothManager = (BluetoothManager) getActivity()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            if (getActivity() != null)
            	toastShow(getString(R.string.bt_not_supported));
//                Toast.makeText(getActivity(), R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
        }

//        mHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                switch(msg.what) {
//                    case 10:
//                        if (mOpenDoorState == 0) {
//                            Log.i(TAG, "Thread handler");
//							if (!mBTScanning) {
//								populateDeviceList(mBtStateOpen);
//							}
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };

        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                } else {
                    mBtStateOpen = true;
					service_init();
                    Log.i("ThreadTest", "myThread111");
					if (!mBTScanning) {
						populateDeviceList(mBtStateOpen);
					}
                }
            }
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart(mPageName);
		
		MobclickAgent.onEvent(getActivity(), "OpenDoorStatistics");
		
		if (getActivity() != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					checkForUserStatus();
					mHandler.postDelayed(this, 30 * 1000);
				}

			});
		}
		
		if (getActivity() != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					upLoadUtils.upLoadInfo(loadSid(), mQueue);						
				}
			}
		}
		
		/*
		 *   getUserConfig() only for test version
		 */

		getUserConfig();
		
		MyDebugLog.e("TEST", "keyFragment onResume()");
        mOpenDoorState = 0;
//		checkBlueToothState();

        if(carDoorList != null){
        	carDoorList.clear();
        	carDoorList = null;
        }
        if(manDoorList != null){
        	manDoorList.clear();
        	manDoorList = null;
        }
        if(officeDoorList != null){
        	officeDoorList.clear();
        	officeDoorList = null;
        }
        
        carDoorList = new ArrayList<HashMap<String, String>>();
		manDoorList = new ArrayList<HashMap<String, String>>();
		officeDoorList = new ArrayList<HashMap<String, String>>();
        
		//TODO DELETE TEMPARY
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,
						null);
				if (mCursor.moveToFirst()) {
					int zoneIdIndex = mCursor.getColumnIndex("zoneId");
					int deviceIdIndex = mCursor.getColumnIndex("deviceId");
					int doorNamemIndex = mCursor.getColumnIndex("doorName");
					int doorTypeIndex = mCursor.getColumnIndex("doorType");
					int directionIndex = mCursor.getColumnIndex("direction");
					int doorIdIndex = mCursor.getColumnIndex("doorId");

					do {
						HashMap<String, String> temp = new HashMap<String, String>();
						String deviceId = mCursor.getString(deviceIdIndex);
						String doorName = mCursor.getString(doorNamemIndex);
						String doorType = mCursor.getString(doorTypeIndex);
						String direction = mCursor.getString(directionIndex);
						String zoneId = mCursor.getString(zoneIdIndex);
						String doorId = mCursor.getString(doorIdIndex);

						/*  Add new logic for car key
						 *  select the car doors can be opened, 
						 *  and all the man doors
						 */
						if (doorType.equals("2")) {						
							MyDebugLog.e(TAG, "add a car key");
							temp.put("CDdeviceid", deviceId);
							temp.put("CDdoorName", doorName);
							temp.put("CDdoorType", doorType);
							temp.put("CDDirection", direction);
							carDoorList.add(temp);		
						} else if (doorType.equals("1")) {
							MyDebugLog.e(TAG, "add man key");
							temp.put("MDdeviceid", deviceId);
							temp.put("MDdoorName", doorName);
							temp.put("MDdoorType", doorType);
							temp.put("MDDirection", direction);
							manDoorList.add(temp);
						} else if (doorType.equals("3")) {
							MyDebugLog.e(TAG, "add office key");
							temp.put("ODdeviceid", deviceId);
							temp.put("ODdoorName", doorName);
							temp.put("ODdoorType", doorType);
							temp.put("ODDirection", direction);
							temp.put("ODdoorId", doorId);
							officeDoorList.add(temp);
						}
					} while (mCursor.moveToNext());
				}
                mCursor.close();
			}
		}
	}

	// TODO
	// getUserConfig
	public void getUserConfig(){
		URL getConfigUrl = null;
		
		sid = loadSid();
		
		try {
			getConfigUrl = new URL(UrlUtils.HOST + "/user/config/default.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, getConfigUrl.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						
						try {
							if(response.getInt("code") == 1){
								
								if(response.getString("sid") != null)
									saveSid(sid);
								
								JSONObject data = response.getJSONObject("data");
								reloadTimes = data.getInt("reloadTimes");
								reloadDays = data.getInt("reloadDays");
								
								MyDebugLog.e(TAG, "reloadTimes: " + String.valueOf(reloadTimes));
								MyDebugLog.e(TAG, "reloadDays: " + String.valueOf(reloadDays));

								Time t = new Time();
								t.setToNow();
								int year = t.year;
								int month = t.month + 1;
								int day = t.monthDay;
								
								MyDebugLog.e(TAG, "year: " + String.valueOf(year));
								MyDebugLog.e(TAG, "month: " + String.valueOf(month));
								MyDebugLog.e(TAG, "day: " + String.valueOf(day));
								
								if(getActivity() != null){
									SharedPreferences userConfig = getActivity().getSharedPreferences("Config", 0);
									Editor editor = userConfig.edit();
									editor.putInt("TIMES", reloadTimes);
									editor.putInt("DAYS", reloadDays);
									editor.putInt("YEAR", year);
									editor.putInt("MONTH", month);
									editor.putInt("DAY", day);
									editor.commit();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						MyDebugLog.e(TAG, error.toString());
					}
				});
		mQueue.add(mJsonRequest);
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyDebugLog.e("test63", "onActivityResult ");
        if (requestCode == REQUEST_ENABLE_BT && resultCode == 1){
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                } else{
                    mBtStateOpen = true;
					service_init();
                    Log.i("ThreadTest", "myThread onActivityResult");
					if (!mBTScanning){
						populateDeviceList(mBtStateOpen);
					}
                }
            }
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == 0){
            mBtStateOpen = false;
        	BtnOpenDoor.setEnabled(true);
        	BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
        }else if(resultCode == 2){
        	getActivity().runOnUiThread(new Runnable(){

    			@Override
    			public void run() {
    				// TODO Auto-generated method stub
    				csv.changeChecked(true);
    			}
    			
    		});	
        }
    }

    @Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		
		if (getActivity() != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == state) {
					upLoadUtils.upLoadInfo(loadSid(), mQueue);						
				}
			}
		}
		

		if(mBluetoothAdapter.isEnabled())
			scanLeDevice(false);
		
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mBluetoothStateReceiver);
        }
        try {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception e) {

        }

        if(mUartService != null){
            if (getActivity() != null) {
                getActivity().unbindService(mServiceConnection);
            }
            mUartService.stopSelf();
            mUartService = null;
        }



	}

	private void service_init() {
		Intent bindIntent = new Intent(getActivity(), UartService.class);
		if(!getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
			getActivity().finish();
		}
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

	public void populateDeviceList(final boolean btStateOpen) {
		MyDebugLog.e("BLE", "populateDeviceList");
		if (btStateOpen && mThisFragment) {
            BtnOpenDoor.setImageResource(R.drawable.door_normalll_v2);
            BtnOpenDoor.setEnabled(false);

            mLogicScanning = true;

			if (mDeviceList != null) {
				mDeviceList.clear();
				mDeviceList = null;
			}
			if (mDevRssiValues != null) {
				mDevRssiValues.clear();
				mDevRssiValues = null;
			}
			mDeviceList = new ArrayList<BluetoothDevice>();
			mDevRssiValues = new HashMap<String, Integer>();
			
			if (tempManDoorList != null){
				tempManDoorList.clear();
				tempManDoorList = null;
			}
			if (tempCarDoorList != null){
				tempCarDoorList.clear();
				tempCarDoorList = null;
			}
			if (tempOfficeDoorList != null){
				tempOfficeDoorList.clear();
				tempOfficeDoorList = null;
			}
			tempCarDoorList = new ArrayList<BluetoothDevice>();
			tempManDoorList = new ArrayList<BluetoothDevice>();
			tempOfficeDoorList = new ArrayList<BluetoothDevice>();

			deviceIndexToOpen = 0;

			radar.startAnimation(animation1);
            circle.setVisibility(View.VISIBLE);
            radar.setVisibility(View.VISIBLE);

            doorName.setText("");
            doorNameFlag.setVisibility(View.INVISIBLE);

            switchBtn.setVisibility(View.VISIBLE);
			channelSwitchLayout.setVisibility(View.INVISIBLE);
            
			scanStatus.setText(R.string.scanning);
            scanLeDevice(true);
		}
	}

	private void scanLeDevice(final boolean enable) {
		MyDebugLog.e("BLE", "scanLeDevice");
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			new Handler().postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					boolean findKey = false;
					boolean bValidKey = false;
					if (mBTScanning) {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mBTScanning = false;
					}

					MyDebugLog.e(TAG, "mDeviceList.size() =" + String.valueOf(mDeviceList.size()));
				
						//for office door		
						for (int i = 0; i < officeDoorList.size(); i++) {
							MyDebugLog.e(TAG, "office door here");
							String tempDID = officeDoorList.get(i).get("ODdeviceid");
							tempDID = tempDID.toUpperCase();
							char[] data = tempDID.toCharArray();
							String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
									+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
									+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
									+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
									+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
									+ String.valueOf(data[10]) + String.valueOf(data[11]);
							
							for(int index = 0; index < mDeviceList.size(); index++){
								MyDebugLog.e(TAG, "check mDeviceList uuid");
								MyDebugLog.e(TAG, mDeviceList.get(index).getAddress() + " : " + formatDeviceId);
								if (mDeviceList.get(index).getAddress().equals(formatDeviceId)) {
									if(mDevRssiValues.get(mDeviceList.get(index).getAddress()) > Integer.parseInt(officeRssi)){
										MyDebugLog.e(TAG, "add tempoffice");
										tempOfficeDoorList.add(mDeviceList.get(index));
										findKey = true;
										break;
									}
								}
							}
						}
					
						if(findKey){
							// compare rssi for office doors
							if(tempOfficeDoorList.size() == 1){
								deviceIndexToOpen = 0;
							}else if(tempOfficeDoorList.size() > 1){
								int maxRssiIndexForOfficeDoor = 0;
								int maxRssiForOfficeDoor = -128;

								for (int i = 0; i < tempOfficeDoorList.size(); i++) {
									MyDebugLog.e("TEST", "checking rssi");
									String tempAdd = tempOfficeDoorList.get(i).getAddress();
									int tempRssi = mDevRssiValues.get(tempAdd);
									if (tempRssi > maxRssiForOfficeDoor) {
										maxRssiForOfficeDoor = tempRssi;
										maxRssiIndexForOfficeDoor = i;
									}
								}
								deviceIndexToOpen = maxRssiIndexForOfficeDoor;
							}

						//
							for (int i = 0; i < officeDoorList.size(); i++) {
								String tempDID = officeDoorList.get(i).get("ODdeviceid");
								tempDID = tempDID.toUpperCase();
								char[] data = tempDID.toCharArray();
								String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
										+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
										+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
										+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
										+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
										+ String.valueOf(data[10]) + String.valueOf(data[11]);
								MyDebugLog.e("TEST", "ODdeviceID:" + formatDeviceId);

								if (tempOfficeDoorList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {							
									bValidKey = true;
									BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
									BtnOpenDoor.setEnabled(true);
									doorName.setText(officeDoorList.get(i).get("ODdoorName"));
									doorNameFlag.setVisibility(View.VISIBLE);	
									
									doorIdForOfficeDoor = officeDoorList.get(i).get("ODdoorId");
									
									switchBtn.setVisibility(View.INVISIBLE);
									channelSwitchLayout.setVisibility(View.VISIBLE);
									
									break;
								}
							}
						}
					
					if (!findKey) {
						// add for the case of only one door -- START
						if (mDeviceList != null && mDeviceList.size() == 1) {	// 1.just one door
							onlyOneDoor = true;
							for (int i = 0; i < carDoorList.size(); i++) {
								String tempDID = carDoorList.get(i).get("CDdeviceid");
								tempDID = tempDID.toUpperCase();
								char[] data = tempDID.toCharArray();
								String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
										+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
										+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
										+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
										+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
										+ String.valueOf(data[10]) + String.valueOf(data[11]);

								if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {// 2.one car door
									if (mDevRssiValues.get(mDeviceList.get(0).getAddress()) > Integer.parseInt(carRssi)) {
										bValidKey = true;
										tempCarDoorList.add(mDeviceList.get(0));
										BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
										BtnOpenDoor.setEnabled(true);
										doorName.setText(carDoorList.get(i).get("CDdoorName"));
										doorNameFlag.setVisibility(View.VISIBLE);

										switchBtn.setVisibility(View.VISIBLE);
										channelSwitchLayout.setVisibility(View.INVISIBLE);
										isChooseCarChannel = 1;
										switchBtn.setSwitch(false);
										
										deviceIndexToOpen = 0;
									}
									findKey = true;
									break;
								}
							}
						
							if (!findKey) {// 3.one man door
								for (int i = 0; i < manDoorList.size(); i++) {
									String tempDID = manDoorList.get(i).get("MDdeviceid");
									tempDID = tempDID.toUpperCase();
									char[] data = tempDID.toCharArray();
									String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
											+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
											+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
											+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
											+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
											+ String.valueOf(data[10]) + String.valueOf(data[11]);
									MyDebugLog.e("TEST", "MDdeviceID:" + formatDeviceId);

									if (mDeviceList.get(0).getAddress().equals(formatDeviceId)) {// one man door
										if (mDevRssiValues.get(mDeviceList.get(0).getAddress()) > Integer.parseInt(manRssi)) {
											bValidKey = true;
											tempManDoorList.add(mDeviceList.get(0));
											BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
											BtnOpenDoor.setEnabled(true);
											doorName.setText(manDoorList.get(i).get("MDdoorName"));
											doorNameFlag.setVisibility(View.VISIBLE);

											switchBtn.setVisibility(View.VISIBLE);
											channelSwitchLayout.setVisibility(View.INVISIBLE);
											isChooseCarChannel = 0;
											switchBtn.setSwitch(true);
											
											deviceIndexToOpen = 0;
										}
										findKey = true;
										break;
									}
								}
							}
						}	
					}
				// add for the case of only one door -- END	
					
					if (!findKey) { // 4.more one door
						if (mDeviceList != null && mDeviceList.size() > 1) {
							onlyOneDoor = false;
							if (isChooseCarChannel == 1) {// 5.when choose car door
								for (int temp = 0; temp < mDeviceList.size(); temp++) {
									for (int i = 0; i < carDoorList.size(); i++) {
										String tempDID = carDoorList.get(i).get("CDdeviceid");
										tempDID = tempDID.toUpperCase();
										char[] data = tempDID.toCharArray();
										String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
												+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
												+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
												+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
												+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
												+ String.valueOf(data[10]) + String.valueOf(data[11]);
										if (formatDeviceId.equals(mDeviceList.get(temp).getAddress())) {
											tempCarDoorList.add(mDeviceList.get(temp));
											findKey = true;
										}
									}
								}
								if (findKey) {
									if (tempCarDoorList.size() == 1) {// 5.1.just one car door
										for (int i = 0; i < carDoorList.size(); i++) {
											String tempDID = carDoorList.get(i).get("CDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);

											if (tempCarDoorList.get(0).getAddress().equals(formatDeviceId)) {
												if (mDevRssiValues.get(tempCarDoorList.get(0).getAddress()) > Integer.parseInt(carRssi)) {
													bValidKey = true;
													BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
													BtnOpenDoor.setEnabled(true);
													doorName.setText(carDoorList.get(i).get("CDdoorName"));
													doorNameFlag.setVisibility(View.VISIBLE);
													
													switchBtn.setVisibility(View.VISIBLE);
													channelSwitchLayout.setVisibility(View.INVISIBLE);
												}
											}
										}
									}else if (tempCarDoorList.size() > 1){// 5.2.more one car door

											int maxRssiIndex = 0;
											int maxRssi = -128;

											for (int i = 0; i < tempCarDoorList.size(); i++) {
												MyDebugLog.e("TEST", "checking rssi");
												String tempAdd = tempCarDoorList.get(i).getAddress();
												int tempRssi = mDevRssiValues.get(tempAdd);
												if (tempRssi > maxRssi) {
													maxRssi = tempRssi;
													maxRssiIndex = i;
												}
											}
											deviceIndexToOpen = maxRssiIndex;

										for (int i = 0; i < carDoorList.size(); i++) {
											String tempDID = carDoorList.get(i).get("CDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
//											MyDebugLog.e("TEST", "CDdeviceID:" + formatDeviceId);

											if (tempCarDoorList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
												if (mDevRssiValues.get(tempCarDoorList.get(deviceIndexToOpen).getAddress()) > Integer.parseInt(carRssi)) {
													bValidKey = true;
													BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
													BtnOpenDoor.setEnabled(true);
													doorName.setText(carDoorList.get(i).get("CDdoorName"));
													doorNameFlag.setVisibility(View.VISIBLE);
													
													switchBtn.setVisibility(View.VISIBLE);
													channelSwitchLayout.setVisibility(View.INVISIBLE);
												}
											}
										}
									}
								}
							}else {// 6.choose man door
								for (int temp = 0; temp < mDeviceList.size(); temp++) {
									for (int i = 0; i < manDoorList.size(); i++) {
										String tempDID = manDoorList.get(i).get("MDdeviceid");
										tempDID = tempDID.toUpperCase();
										char[] data = tempDID.toCharArray();
										String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
												+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
												+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
												+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
												+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
												+ String.valueOf(data[10]) + String.valueOf(data[11]);
										if (formatDeviceId.equals(mDeviceList.get(temp).getAddress())) {
											tempManDoorList.add(mDeviceList.get(temp));
											findKey = true;
										}
									}
								}
								if (findKey) {
									if (tempManDoorList.size() == 1) {// 6.1.just one door
										for (int i = 0; i < manDoorList.size(); i++) {
											String tempDID = manDoorList.get(i).get("MDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
//											MyDebugLog.e("TEST", "MDdeviceID:" + formatDeviceId);

											if (tempManDoorList.get(0).getAddress().equals(formatDeviceId)) {
//												MyDebugLog.e("TEST69", "man rssi:"+String.valueOf(mDevRssiValues.get(tempManDoorList.get(0).getAddress())));
												if (mDevRssiValues.get(tempManDoorList.get(0).getAddress()) > Integer.parseInt(manRssi)) {
													bValidKey = true;
													BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
													BtnOpenDoor.setEnabled(true);
													doorName.setText(manDoorList.get(i).get("MDdoorName"));
													doorNameFlag.setVisibility(View.VISIBLE);
													
													switchBtn.setVisibility(View.VISIBLE);
													channelSwitchLayout.setVisibility(View.INVISIBLE);
												}
											}
										}
									}else if (tempManDoorList.size() > 1) {// 6.2.more one man door

										int maxRssiIndex = 0;
										int maxRssi = -128;

										for (int i = 0; i < tempManDoorList.size(); i++) {
											MyDebugLog.e("TEST", "checking rssi");
											String tempAdd = tempManDoorList.get(i).getAddress();
											int tempRssi = mDevRssiValues.get(tempAdd);
											if (tempRssi > maxRssi) {
												maxRssi = tempRssi;
												maxRssiIndex = i;
											}
										}
										deviceIndexToOpen = maxRssiIndex;

										for (int i = 0; i < manDoorList.size(); i++) {
											String tempDID = manDoorList.get(i).get("MDdeviceid");
											tempDID = tempDID.toUpperCase();
											char[] data = tempDID.toCharArray();
											String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
													+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
													+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
													+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
													+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
													+ String.valueOf(data[10]) + String.valueOf(data[11]);
											MyDebugLog.e("TEST", "MDdeviceID:" + formatDeviceId);

											if (tempManDoorList.get(deviceIndexToOpen).getAddress().equals(formatDeviceId)) {
												if (mDevRssiValues.get(tempManDoorList.get(deviceIndexToOpen).getAddress()) > Integer.parseInt(manRssi)) {
													bValidKey = true;
													BtnOpenDoor.setImageResource(R.drawable.selector_open_door);
													BtnOpenDoor.setEnabled(true);
													doorName.setText(manDoorList.get(i).get("MDdoorName"));
//													MyDebugLog.e("TEST", manDoorList.get(i).get("MDdoorName") + ",i = " + String.valueOf(i));
													doorNameFlag.setVisibility(View.VISIBLE);
													
													switchBtn.setVisibility(View.VISIBLE);
													channelSwitchLayout.setVisibility(View.INVISIBLE);
												}
											}
										}
									}
								}
							}
						}
					}
					if (bValidKey/*mDeviceList.size() != 0*/) {
						scanStatus.setText(R.string.can_shake_to_open_door);
//						myThread.mKeyFindState = true;

						activity.myThread.mKeyFindState = true;
						circle.setVisibility(View.INVISIBLE);
						radar.setVisibility(View.INVISIBLE);
						radar.clearAnimation();
                        mLogicScanning = false;
					} else {
                        mLogicScanning = true;
//						myThread.mKeyFindState = false;
						activity.myThread.mKeyFindState = false;
						circle.setVisibility(View.VISIBLE);
						radar.setVisibility(View.VISIBLE);
					}
				}
			}, SCAN_PERIOD);
			if (mBluetoothAdapter.startLeScan(mLeScanCallback)){
                mBTScanning = true;
                Log.i(TAG, "mBTScanning is true");
            }
		} else {
            if (mBTScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mBTScanning = false;
            }
		}
	}

	private void addDevice(BluetoothDevice device, int rssi) {
		MyDebugLog.e("BLE", "addDevice");
		boolean deviceFound = false;
		boolean bFindKey = false;

		for (BluetoothDevice listDev : mDeviceList) {
			if (listDev.getAddress().equals(device.getAddress())) {
				deviceFound = true;
				break;
			}
		}
		if (!deviceFound) {
			
			for (int i = 0; i < officeDoorList.size(); i++) {
				String tempDID = officeDoorList.get(i).get("ODdeviceid");
				tempDID = tempDID.toUpperCase();
				char[] data = tempDID.toCharArray();
				String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
						+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
						+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
						+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
						+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
						+ String.valueOf(data[10]) + String.valueOf(data[11]);
//				MyDebugLog.e("TEST", "CDdeviceID:" + formatDeviceId);

				if (device.getAddress().equals(formatDeviceId)) {
					mDevRssiValues.put(device.getAddress(), rssi);
					mDeviceList.add(device);
					bFindKey = true;
					break;
				}
			}
			
			
			if (!bFindKey) {
				for (int i = 0; i < carDoorList.size(); i++) {
					String tempDID = carDoorList.get(i).get("CDdeviceid");
					tempDID = tempDID.toUpperCase();
					char[] data = tempDID.toCharArray();
					String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
							+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
							+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
							+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
							+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
							+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					MyDebugLog.e("TEST", "CDdeviceID:" + formatDeviceId);

					if (device.getAddress().equals(formatDeviceId)) {
						mDevRssiValues.put(device.getAddress(), rssi);
//						MyDebugLog.e("TEST69", "add a car door, rssi = " + String.valueOf(rssi));
						mDeviceList.add(device);
						bFindKey = true;
						break;
					}
				}
			}
			
				
				if (!bFindKey) {
					for (int i = 0; i < manDoorList.size(); i++) {
						String tempDID = manDoorList.get(i).get("MDdeviceid");
						tempDID = tempDID.toUpperCase();
						char[] data = tempDID.toCharArray();
						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
								+ String.valueOf(data[10]) + String.valueOf(data[11]);
//					MyDebugLog.e("TEST", "MDdeviceID:" + formatDeviceId);

						if (device.getAddress().equals(formatDeviceId)) {
							mDevRssiValues.put(device.getAddress(), rssi);
							MyDebugLog.e("TEST", "add a man door");
							mDeviceList.add(device);
							break;
						}
					}
				}
		}
	}
	
	private void doOpenDoor(final boolean btStateOpen) {
		MyDebugLog.e("BLE", "doOpenDoor");
       	if (btStateOpen) {
			onlyOneDoor = !onlyOneDoor;
			//TODO
			
			Date date = new Date();
			sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			openDoorTime = sDateFormat.format(date);			
			
			/*
			 *  if find office door, we open it
			 *  only when no office found, we open car or man door
			 */
			if(tempOfficeDoorList.size() > 0){
				
				scanStatus.setText(R.string.door_openning);
				if (mUartService != null) {
					if(mUartService.connect(tempOfficeDoorList.get(deviceIndexToOpen).getAddress())) {
						openDoorDevicdId = tempOfficeDoorList.get(deviceIndexToOpen).getAddress();
						mOpenDoorState = 4;
					}
				}
				
			}else{
				if (isChooseCarChannel == 1) {

					for (int index = 0; index < carDoorList.size(); index++) {
						String deviceId = null;
						String tempDeviceId = null;
						deviceId = carDoorList.get(index).get("CDdeviceid");
						tempDeviceId = deviceId.toUpperCase();
						char[] data = tempDeviceId.toCharArray();
						String formatDeviceId = String.valueOf(data[0]) + String.valueOf(data[1]) + ":"
								+ String.valueOf(data[2]) + String.valueOf(data[3]) + ":"
								+ String.valueOf(data[4]) + String.valueOf(data[5]) + ":"
								+ String.valueOf(data[6]) + String.valueOf(data[7]) + ":"
								+ String.valueOf(data[8]) + String.valueOf(data[9]) + ":"
								+ String.valueOf(data[10]) + String.valueOf(data[11]);

						if (formatDeviceId.equals(tempCarDoorList.get(deviceIndexToOpen).getAddress())) {
							if (carDoorList.get(index).get("CDDirection").equals("1")) {  // go in
								if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
									if (DBCountCar() > 0) {
										Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
										if (mCursor.moveToFirst()) {
											int carStatusIndex = mCursor.getColumnIndex("carStatus");
											int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
											int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
											int plateNumIndex = mCursor.getColumnIndex("plateNum");

											do {
												String carStatus = mCursor.getString(carStatusIndex);
												String carPosStatus = mCursor.getString(carPosStatusIndex);
												final String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
												final String plateNum = mCursor.getString(plateNumIndex);

												if ((carStatus.equals("1") || carStatus.equals("2"))
														&& (carPosStatus.equals("0") || carPosStatus.equals("2"))) {
													// can open
													scanStatus.setText(R.string.door_openning);
													if (mUartService != null) {
														if(mUartService.connect(tempCarDoorList.get(deviceIndexToOpen).getAddress())) {
															openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
															mOpenDoorState = 4;
														}

														// 30s
														new Handler().postDelayed(new Runnable() {
															@Override
															public void run() {
																// update
																// the
																// carPosStatus
																ContentValues value = new ContentValues();
																value.put("carPosStatus", "1");
																mKeyDB.update(CAR_TABLE_NAME, value, "l1ZoneId=? and plateNum=?",
																		new String[]{l1ZoneId, plateNum});
															}
														}, 30 * 1000);
													}

												} else if(carStatus.equals("3")){
													if (getActivity() != null) {
														openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
														toastShow(getString(R.string.car_already_lend));
//														Toast.makeText(getActivity(), R.string.car_already_lend, Toast.LENGTH_SHORT).show();
													}
												} else {
													// can not open
													if (getActivity() != null) {
														openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
														MyDebugLog.e(TAG, "sorry, cannot open");
														toastShow(getString(R.string.sorry_you_are_in_the_zone));
//														Toast.makeText(getActivity(), R.string.sorry_you_are_in_the_zone, Toast.LENGTH_SHORT).show();
													}
												}

											} while (mCursor.moveToNext());
										}
										mCursor.close();
									}
								}
							} else if (carDoorList.get(index).get("CDDirection").equals("2")) {  // go out
								if (mKeyDBHelper.tabIsExist(CAR_TABLE_NAME)) {
									if (DBCountCar() > 0) {
										Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
										if (mCursor.moveToFirst()) {
											int carStatusIndex = mCursor.getColumnIndex("carStatus");
											int carPosStatusIndex = mCursor.getColumnIndex("carPosStatus");
											int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
											int plateNumIndex = mCursor.getColumnIndex("plateNum");

											do {
												String carStatus = mCursor.getString(carStatusIndex);
												String carPosStatus = mCursor.getString(carPosStatusIndex);
												final String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
												final String plateNum = mCursor.getString(plateNumIndex);

												if ((carStatus.equals("1") || carStatus.equals("2"))
														&& (carPosStatus.equals("0") || carPosStatus.equals("1"))) {
													// can open
													scanStatus.setText(R.string.door_openning);
													if (mUartService != null) {
														if (mUartService.connect(tempCarDoorList.get(deviceIndexToOpen).getAddress())) {
															openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
															mOpenDoorState = 4;
														}
													}

													// 30s
													new Handler().postDelayed(new Runnable() {
														@Override
														public void run() {
															// update the carPosStatus
															ContentValues value = new ContentValues();
															value.put("carPosStatus", "2");
															mKeyDB.update(CAR_TABLE_NAME, value, "l1ZoneId=? and plateNum=?", new String[]{l1ZoneId, plateNum});
														}
													}, 30 * 1000);
												} else if(carStatus.equals("3")){
													mOpenDoorState = 2;
													if (getActivity() != null) {
														toastShow(getString(R.string.car_already_lend));
														openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
//														Toast.makeText(getActivity(), R.string.car_already_lend, Toast.LENGTH_SHORT).show();
													}
												} else {
													mOpenDoorState = 3;
													// can not open
													if (getActivity() != null) {
														MyDebugLog.e(TAG, "sorry, cannot open");
														toastShow(getString(R.string.sorry_you_are_out_the_zone));
														openDoorDevicdId = tempCarDoorList.get(deviceIndexToOpen).getAddress();
//														Toast.makeText(getActivity(), R.string.sorry_you_are_out_the_zone, Toast.LENGTH_SHORT).show();
													}
												}

											} while (mCursor.moveToNext());
										}
										mCursor.close();
									}
								}
							}
						}
					}
				} else {
					if (mUartService != null) {
						// man door, can open
						scanStatus.setText(R.string.door_openning);
						if (mUartService.connect(tempManDoorList.get(deviceIndexToOpen).getAddress())) {
							openDoorDevicdId = tempManDoorList.get(deviceIndexToOpen).getAddress();
							mOpenDoorState = 4;
						}
					}
				}
			}

			mHandlerReset.postDelayed(mRunnableReset, 6000);
		}
    }

	public String deviceIdTodoorId(String deviceId){
		String doorIDToOpen = null;
		String formatDeviceId = deviceId.replace(":", "");
		if (mKeyDBHelper.tabIsExist(TABLE_NAME)) {
			if (DBCount() > 0) {
				Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME, null);
				if (mCursor.moveToFirst()) {
					int doorIdOpenIndex = mCursor.getColumnIndex("doorId");
					int devicdIdOpenIndex = mCursor.getColumnIndex("deviceId");
					
					do{
						String doorIdOpen = mCursor.getString(doorIdOpenIndex);
						String deviceIdOpen = mCursor.getString(devicdIdOpenIndex);
						
						if(deviceIdOpen.equals(formatDeviceId)){
							doorIDToOpen = doorIdOpen;
							break;
						}			
					}while(mCursor.moveToNext());
				}
			}
		}
		return doorIDToOpen;
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			MyDebugLog.e("BLE", "onLeScan");
			if(getActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						if (getActivity() != null) {
//							getActivity().runOnUiThread(new Runnable() {
//								@Override
//								public void run() {
//									addDevice(device, rssi);
//								}
//							});
//						}
						addDevice(device, rssi);
					}
				});
			}
		}
	};

	public void InitFragmentViews() {

		SharedPreferences setting = getActivity().getSharedPreferences(
				"SETTING", 0);
		isChooseCarChannel = setting.getInt("chooseCar", 1);
//		canDisturb = setting.getInt("disturb", 0);
		haveSound = setting.getInt("sound", 1);
		canShake = setting.getInt("shake", 1);
	}

	private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			MyDebugLog.e("BLE", "mBluetoothStateReceiver");
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					MyDebugLog.e("BLE", "BluetoothAdapter.STATE_OFF");
                    mBtStateOpen = false;
//					if (myThread != null) {
//						MyDebugLog.e("ThreadTest", "BluetoothAdapter.STATE_OFF1");
//						myThread.stopThread();
//						myThread = null;
//					}
					break;

				case BluetoothAdapter.STATE_TURNING_OFF:
					MyDebugLog.e("ThreadTest", "BluetoothAdapter.STATE_TURNING_OFF");
					if (mBTScanning){
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mBTScanning = false;
					}
					break;

				case BluetoothAdapter.STATE_ON:
					MyDebugLog.e("BLE", "BluetoothAdapter.STATE_ON");
                    mBtStateOpen = true;
					service_init();
					MyDebugLog.i("ThreadTest", "myThread111 STATE_ON");
//					if (myThread == null) {
//						myThread = new MyThread();
//						myThread.start();
//					}
					if (!mBTScanning) {
						populateDeviceList(mBtStateOpen);
					}
					break;

				case BluetoothAdapter.STATE_TURNING_ON:
					MyDebugLog.e("BLE", "BluetoothAdapter.STATE_TURNING_ON");
					break;
				}
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		MyDebugLog.e("BLE", "makeGattUpdateIntentFilter");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(UartService.ACTION_MAKESURE_DOOROPENED);
		return intentFilter;
	}
	
	public void grab(){
//		openDoorDevicdId
		
		String doorId = deviceIdTodoorId(openDoorDevicdId);
		JSONObject parm = new JSONObject();
		String url = UrlUtils.HOST + "/user/activity/rp/grab.do" + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		try {
			parm.put("doorId", doorId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MyRequestBody requestBody = new MyRequestBody(url, parm.toString(),
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						System.out.println("���response = "+response);
//						{"data":false,"message":"successful","sid":"0641983572e74fc7abf73edb0fe51749","code":1}
						try {
							int code =response.getInt("code");
							if(code==1){
								boolean data = response.getBoolean("data");
								if(data){
									startActivity(new Intent(getActivity(), RedActivity.class));
									getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
								}
							}else{
								activity.showToast(R.string.network_error);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(requestBody);
	}
	
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			MyDebugLog.e("BLE", "UARTStatusChangeReceiver");
			String action = intent.getAction();

			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				MyDebugLog.e("test", "UartService.ACTION_GATT_CONNECTED");
			}

            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            	MyDebugLog.e("test", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (mUartService != null) {
                            mUartService.readRXCharacteristic(mUartService.RX_CHAR_UUID);
                        }
                    }
                });
            }

            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
            	MyDebugLog.e("BLE", "UartService.ACTION_GATT_DISCONNECTED");
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mUartService.close();
							if (mOpenDoorState != 0) {
								if (!mDoorState) {
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("TIME", openDoorTime);
									map.put("UserID", userId);
									map.put("DoorID", deviceIdTodoorId(openDoorDevicdId));
									map.put("MODEL", modelNameAndVersion);
									map.put("RESULT", String.valueOf(false));
									MobclickAgent.onEvent(getActivity(), "OpenDoorStatistics", map);

									upLoadUtils.writeOpenInfoToFile(openDoorTime, userId, deviceIdTodoorId(openDoorDevicdId), false, modelNameAndVersion);
									if (getActivity() != null)
										Toast.makeText(getActivity(), R.string.open_door_fail, Toast.LENGTH_SHORT).show();
									// toastShow(getString(R.string.open_door_fail));
								}
								MyDebugLog.e("test for open door", "Gatt close");
								// mHandlerReset.getLooper().quit();
								mHandlerReset.removeCallbacks(mRunnableReset);
								mOpenDoorState = 0;
								mDoorState = false;
								scanStatus.setText(R.string.can_shake_to_open_door);
							}
						}
					});
				}
            }

            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            	MyDebugLog.e("test", "UartService.ACTION_GATT_SERVICES_DISCOVERED");
                mUartService.enableTXNotification();
            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
            	MyDebugLog.e("test", "UartService.ACTION_DATA_AVAILABLE");

                @SuppressWarnings("unused")
                final byte[] txValue = intent
                        .getByteArrayExtra(UartService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (mUartService != null) {
                            String message = new String(
                                    Character.toChars(new Random()
                                            .nextInt(90 - 65) + 65));
                            try {
                                byte[] value = message.getBytes("UTF-8");
                                mUartService.writeRXCharacteristic(value);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
            }

            if (action.equals(UartService.ACTION_MAKESURE_DOOROPENED)) {//new add for response
            	MyDebugLog.e("test", "UartService.ACTION_MAKESURE_DOOROPENED");
                final byte[] txValue = intent
                        .getByteArrayExtra(UartService.EXTRA_DATA);

                if(ICDCrypto.checkIfOpenDoorSuccess(txValue)) {
                	
                	if (getActivity() != null) {
            			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            			if (networkInfo != null) {
            				grab();
            			}
            		}
                	
                	MyDebugLog.e(TAG, "**************receive feedback from bt");
                	
                    vibrator.vibrate(500);
                    // door had opened. go on ...
                    if(getActivity() != null)
                    	Toast.makeText(getActivity(), R.string.open_door_success, Toast.LENGTH_SHORT).show();
					scanStatus.setText(R.string.can_shake_to_open_door);
					mDoorState = true;
					
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("TIME", openDoorTime);
					map.put("UserID", userId);
					map.put("DoorID", deviceIdTodoorId(openDoorDevicdId));
					map.put("MODEL", modelNameAndVersion);
					map.put("RESULT", String.valueOf(true));
					MobclickAgent.onEvent(getActivity(), "OpenDoorStatistics", map);

					upLoadUtils.writeOpenInfoToFile(openDoorTime, userId, deviceIdTodoorId(openDoorDevicdId), true, modelNameAndVersion);
                }
            }

            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
            	MyDebugLog.e("BLE", "UartService.DEVICE_DOES_NOT_SUPPORT_UART");
                mUartService.disconnect();
            }
		}
	};

    @Override
    public void onStop() {
    	MyDebugLog.e("TEST63", "onStop");
		super.onStop();
		mOpenDoorState = 5;
		mThisFragment = false;
        vibrator.cancel();

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			MyDebugLog.e("BLE", "onServiceConnected");
			mUartService = ((UartService.LocalBinder) rawBinder).getService();
			if (!mUartService.initialize()) {
				getActivity().finish();
			}
		}

		public void onServiceDisconnected(ComponentName classname) {
			MyDebugLog.e("BLE", "onServiceDisconnected");
			mUartService = null;
			scanStatus.setText(R.string.can_not_use_open_door);
		}
	};

	private class DeviceAdapter extends BaseAdapter {
		private Context context;
		private List<BluetoothDevice> devices;

		public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
			this.context = context;
			this.devices = devices;
		}

		@Override
		public int getCount() {
			int ret = 0;
			if (devices != null && devices.size() > 0) {
				ret = devices.size();
			}
			return ret;
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}

	}

	public void playOpenDoorSound() {
		MyDebugLog.e(TAG, "play open door sound");

		if (getActivity() != null) {
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

			mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			AudioManager audioManager = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
			final float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			final int soundID = mSoundPool.load(getActivity(), R.raw.ring, 0);

			mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

				@Override
				public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					MyDebugLog.e(TAG, "load open door sound complete");
					mSoundPool.play(soundID, volume, volume, 0, 0, 1);
				}

			});
		}
	}
		

	private boolean hasData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + TABLE_NAME,null);
		
		if(mCursor.moveToFirst()){
			int deviceIdIndex = mCursor.getColumnIndex("deviceId");
			do{
				 String deviceId = mCursor.getString(deviceIdIndex);
				 
				 if(deviceId.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasZoneData(SQLiteDatabase mDB, String str){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + ZONE_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int zoneidIndex = mCursor.getColumnIndex("zoneid");
			do{
				 String zoneid = mCursor.getString(zoneidIndex);
				 
				 if(zoneid.equals(str)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private boolean hasCarData(SQLiteDatabase mDB, String str, String str1){
		boolean hasData = false;
		Cursor mCursor = mKeyDB.rawQuery("select * from " + CAR_TABLE_NAME, null);
		
		if(mCursor.moveToFirst()){
			int l1ZoneIdIndex = mCursor.getColumnIndex("l1ZoneId");
			int plateNumIndex = mCursor.getColumnIndex("plateNum");
			do{
				 String l1ZoneId = mCursor.getString(l1ZoneIdIndex);
				 String plateNum = mCursor.getString(plateNumIndex);
				 
				 if(l1ZoneId.equals(str) && plateNum.equals(str1)) {
					 hasData = true;
					 break;
				 }
				 
			}while(mCursor.moveToNext());
		}
		mCursor.close();
		return hasData;
	}
	
	private long DBCount() {  
	    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountZone() {  
	    String sql = "SELECT COUNT(*) FROM " + ZONE_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
	}
	
	private long DBCountCar() {  
	    String sql = "SELECT COUNT(*) FROM " + CAR_TABLE_NAME;
	    SQLiteStatement statement = mKeyDB.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    return count;
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
	
	public void saveLastRequestTime(long time) {
		if (getActivity() != null) {
			SharedPreferences savedTime = getActivity().getSharedPreferences("SAVEDTIME", 0);
			Editor editor = savedTime.edit();
			editor.putLong("TIME", time);
			editor.commit();
		}
	}

	public long loadLastRequestTime() {
		if (getActivity() != null) {
			SharedPreferences loadTime = getActivity().getSharedPreferences("SAVEDTIME", 0);
			return loadTime.getLong("TIME", 0);
		}
		return 0;
	}
	
	public void saveSid(String sid) {
        if (getActivity() != null) {
            SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
            Editor editor = savedSid.edit();
            editor.putString("SID", sid);
            editor.commit();
        }
	}

	public String loadSid() {
		if (getActivity() != null) {
			SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
			return loadSid.getString("SID", null);
		}
		return null;
	}
	
	public void saveUUID(String uuid){	
		if(getActivity() != null) {
			SharedPreferences savedUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
			Editor editor = savedUUID.edit();
			editor.putString("UUID", uuid);
			editor.commit();
		}		
	}
	
	public String loadUUID(){
		if(getActivity() != null) {
			SharedPreferences loadUUID = getActivity().getSharedPreferences("SAVEDUUID", 0);
			return loadUUID.getString("UUID", null);
		}
		return null;
	}
	
	public void toastShow(String arg) {
        if (mToast == null) {
        	mToast = Toast.makeText(getActivity(), arg, Toast.LENGTH_SHORT);
        } else {
        	mToast.cancel();
        	mToast.setText(arg);
        }
        mToast.show();
    }
	
	public void checkForUserStatus() {
		MyDebugLog.e(TAG, "checkForUserStatus()");
		URL getUserStatusURL = null;
		sid = loadSid();
		try {
			getUserStatusURL = new URL(UrlUtils.HOST + "/user/manage/getProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST, getUserStatusURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MyDebugLog.e(TAG, "checkForUserStatus: " + response.toString());
						try {
							if(response.getInt("code") == 1){
								if(response.getString("sid") != null)
									saveSid(response.getString("sid"));
								
								if (getActivity() != null) {
									SharedPreferences loginStatus = getActivity().getSharedPreferences("LOGINSTATUS", 0);
									Editor editor = loginStatus.edit();
									editor.putInt("STATUS", response.getJSONObject("data").getInt("userStatus"));
									editor.putBoolean( "isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									editor.putString( "l1Zones", response.getJSONObject("data").getString("l1Zones"));
									editor.commit();

									SharedPreferences saveProfile = getActivity().getSharedPreferences("PROFILE", 0);
									Editor edit = saveProfile.edit();
									edit.putInt("userStatus", response.getJSONObject("data").getInt("userStatus"));
									edit.putBoolean("isHasPropServ", response.getJSONObject("data").getBoolean("isHasPropServ"));
									edit.commit();
									
									MyDebugLog.e(TAG, String.valueOf(response.getJSONObject("data").getInt("userStatus")) + "in KeyFragment***********");
									MyDebugLog.e(TAG, String.valueOf(response.getJSONObject("data").getBoolean("isHasPropServ")) + "in KeyFragment***********");
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
