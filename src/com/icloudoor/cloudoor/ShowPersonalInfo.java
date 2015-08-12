package com.icloudoor.cloudoor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.Entities.FilePart;
import com.icloudoor.cloudoor.Entities.MultipartEntity;
import com.icloudoor.cloudoor.Entities.Part;
import com.icloudoor.cloudoor.widget.QRCodeCreateDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

public class ShowPersonalInfo extends BaseActivity implements OnClickListener{

	private String TAG = this.getClass().getSimpleName();

	Calendar c;
	int mYear;
	int mMonth;
	int mDay;

	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";

	private String[] provinceSet;
	private String[][] citySet;
	private String[][][] districtSet;

	private int maxPlength;
	private int maxClength;
	private int maxDlength;

	private int  provinceId, cityId, districtId;

	private RelativeLayout setAddress;
	private TextView addText;
	
	private File cameraFile;
	private File imageFile;
	private RequestQueue mQueue;
	private String HOST = UrlUtils.HOST;
	private URL setInfoURL;
	private int statusCode;
	private String sid;

	private String nickname = null;

	private TextView TVNickName;
	private TextView TVSex;
	private TextView TVDate;
	private ImageView image;

	private RelativeLayout back;
	private RelativeLayout SetPersonalNickname;
	private RelativeLayout SetPersonalSex;
	private RelativeLayout SetBirthday;

	private RelativeLayout ChangePhoto;
	private SelectPicPopupWindow menuWindow;

//	private static final int MSG_SUCCESS = 0;// get the image success
//	private static final int MSG_FAILURE = 1;// fail
	private static final int RESULT_REQUEST_CODE = 11;
	private static final int RESULT_SET_NICKNAME = 4;
	private static final int RESULT_SET_SEX = 5;

	private String portraitUrl;

	private ProgressBar upLoadBar;
	private TextView mTextPercent;
	private ProgressBar InfoPercent;

	private int userStatus;
	private ImageView certiImage;

	//
	private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cloudoor/CacheImage/";
	private String imageName = "myImage.jpg";

	private String BirthDayDate;

	boolean isDebug = DEBUG.isDebug;

	String imageUrl;
	DisplayImageOptions options;
	String tempURL;
	RelativeLayout QRcode_layout;
	private Version version;

	int width;
    int height;
    
    private int[] sexRole = {R.drawable.default_icon_female, R.drawable.default_icon_male, R.drawable.default_icon_female};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_personal_info);
		QRcode_layout = (RelativeLayout) findViewById(R.id.QRcode_layout);
		QRcode_layout.setOnClickListener(this);
		
		SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
		userStatus = loginStatus.getInt("STATUS", 1);
		portraitUrl = loginStatus.getString("URL", null);
		tempURL = portraitUrl;

		version = new Version(getApplicationContext());

		mAreaDBHelper = new MyAreaDBHelper(ShowPersonalInfo.this, DATABASE_NAME, null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();	

		String imagePath = PATH + imageName;
		imageUrl = Scheme.FILE.wrap(imagePath);

		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		ImageLoader.getInstance().init(configuration);

		provinceId = loginStatus.getInt("PROVINCE", 0);
		cityId = loginStatus.getInt("CITY", 0);
		districtId = loginStatus.getInt("DIS", 0);
		
		initViews();

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pictureZoom(image);
			}
		});

		ChangePhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				menuWindow = new SelectPicPopupWindow(ShowPersonalInfo.this, itemsOnClick); 
				menuWindow.showAtLocation(ShowPersonalInfo.this.findViewById(R.id.personal_info_main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}

		});

		SetPersonalNickname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ShowPersonalInfo.this, SetNickname.class);
				intent.putExtra("textnickname", nickname);
				startActivityForResult(intent, RESULT_SET_NICKNAME);
			}
		});

		SetPersonalSex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowPersonalInfo.this, SetSex.class);
				TVSex.getText();
				Log.e(TAG, TVSex.getText().toString());
				intent.putExtra("textsex",TVSex.getText().toString());
				startActivityForResult(intent, RESULT_SET_SEX);
			}
		});

		SetBirthday.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						String s = (monthOfYear + 1) + " " + getString(R.string.month) + " " + dayOfMonth + " " + getString(R.string.day);
						TVDate.setText((String.valueOf(monthOfYear + 1).length() == 1 ? ("0" + String.valueOf(monthOfYear + 1)) : String.valueOf(monthOfYear + 1)) + getString(R.string.month)
								+ (String.valueOf(dayOfMonth).length() == 1 ? ("0" + String.valueOf(dayOfMonth)) : String.valueOf(dayOfMonth)) + getString(R.string.day));
						BirthDayDate = String.valueOf(year) + "-" 
								+ (String.valueOf(monthOfYear + 1).length() == 1 ? ("0" + String.valueOf(monthOfYear + 1)) : String.valueOf(monthOfYear + 1)) + "-"
								+ (String.valueOf(dayOfMonth).length() == 1 ? ("0" + String.valueOf(dayOfMonth)) : String.valueOf(dayOfMonth));
						Map<String, String> map = new HashMap<String, String>();
						map.put("birthday", BirthDayDate);
						updateProfile(map);
						loading();
					}


				};

				new DatePickerDialog(ShowPersonalInfo.this, AlertDialog.THEME_HOLO_LIGHT, onDateSetListener, mYear, mMonth, mDay).show();
			}

		});	
		
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ShowPersonalInfo.this.finish();
			}

		});

		initSpinnerData();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
	    height = dm.heightPixels;
	}
	
	public void initViews() {
		TVNickName = (TextView) findViewById(R.id.personal_info_NickName);
		TVSex = (TextView) findViewById(R.id.personal_info_sexName);
		TVDate = (TextView) findViewById(R.id.personal_info_month);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		image = (ImageView) findViewById(R.id.personal_info_small_image);
		SetPersonalNickname = (RelativeLayout) findViewById(R.id.set_nickname);
		SetPersonalSex = (RelativeLayout) findViewById(R.id.set_sex);
		SetBirthday = (RelativeLayout) findViewById(R.id.set_birthday);
		
		setAddress = (RelativeLayout) findViewById(R.id.set_address);
		setAddress.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(ShowPersonalInfo.this, SelectAddActivity.class);
				startActivity(intent);
			}
			
		});
		addText = (TextView) findViewById(R.id.personal_info_address);
		
		certiImage = (ImageView) findViewById(R.id.certi_or_not_image);
		ChangePhoto = (RelativeLayout) findViewById(R.id.personal_photo_change);

		upLoadBar = (ProgressBar) findViewById(R.id.uploadBar);
		upLoadBar.setVisibility(View.INVISIBLE);
		
		mTextPercent = (TextView) findViewById(R.id.person_info_percent);
		InfoPercent = (ProgressBar) findViewById(R.id.progressBar);
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:


				if (!isExitsSdcard()) {
					Toast.makeText(getApplicationContext(), "SD¿¨²»¿ÉÓÃ", 0).show();
					return;
				}
				//				cameraFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cloudoor", 
				//						+ System.currentTimeMillis() + ".jpg");
				cameraFile = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/Cloudoor/CacheImage",
						+System.currentTimeMillis() + ".jpg");
				cameraFile.getParentFile().mkdirs();
				startActivityForResult(
						new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),1);
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*"); 
				startActivityForResult(intent, 0);
				break;
			default:
				menuWindow.dismiss();
				break;
			}
		}

	};	


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				if (cameraFile != null) {
					Log.e("test for upload", "cameraFile != null");
					startPhotoZoom(Uri.fromFile(cameraFile));
				}
				break;
			case 0:
				if (data != null){
					Log.e("test for upload", "data != null");
					startPhotoZoom(data.getData());
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null){
					Log.e("test for upload", "data != null  RESULT_REQUEST_CODE");
					sentPicToNext(data);
				}
				break;
			}
		}
	}

	public void startPhotoZoom(Uri uri) {

		Log.e("test for upload", "startPhotoZoom");

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);

	}
	
	private void sentPicToNext(Intent data) {

		Bundle bundle = data.getExtras();
		if (bundle != null) {
			Log.e("test for upload", "sentPic 1");
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
						|| NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()) {
					Log.e("test for upload", "sentPic 2");
					imageFile = new File(PATH + System.currentTimeMillis() + ".jpg");
					imageFile.getParentFile().mkdirs();
					Bitmap photo = bundle.getParcelable("data");
					TakePicFileUtil.saveBitmap(photo);
					image.setImageBitmap(photo);
					FileOutputStream foutput = null;
					try {
						foutput = new FileOutputStream(this.imageFile);
						photo.compress(Bitmap.CompressFormat.PNG, 100, foutput);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						if (null != foutput) {
							try {
								foutput.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					new Thread() {
						@Override
						public void run() {

							MyDebugLog.e(TAG, "thread run");
							
							runOnUiThread(new Runnable() {
							@Override
							public void run() {
//								upLoadBar.setVisibility(View.VISIBLE);
								loading();
								}
							});
							
							Log.e("test for upload", "sentPic 3");

							try {
								sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}

							HttpClient httpClient = new DefaultHttpClient();
							HttpPost postRequest = new HttpPost(HOST
									+ "/user/api/uploadPortrait.do" + "?sid=" + loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());

							Part[] parts = null;
							FilePart filePart = null;
							try {
								filePart = new FilePart("portrait", imageFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							parts = new Part[] { filePart };

							postRequest.setEntity(new MultipartEntity(parts));
							try {
								HttpResponse response = httpClient.execute(postRequest);
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(response.getEntity()
												.getContent(), "UTF-8"));
								String sResponse;
								StringBuilder s = new StringBuilder();
								while ((sResponse = reader.readLine()) != null) {
									s = s.append(sResponse);
								}
								MyDebugLog.e("TEst StringBuilder", s.toString());
								Log.e("test for upload", "sentPic 4 " + s.toString());

								//
								JSONObject jsObj = new JSONObject(s.toString());

								if (jsObj.getInt("code") == 1) {
									
									if(!jsObj.getString("sid").equals(null))
										saveSid(jsObj.getString("sid"));
									
									JSONObject data = jsObj.getJSONObject("data");
									portraitUrl = data.getString("portraitUrl");
//									SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
//									Editor edit = loginStatus.edit();
//									edit.putString("URL", portraitUrl);
//									edit.commit();
									
									Map<String, String> map = new HashMap<String, String>();
									map.put("portraitUrl", portraitUrl);
									updateProfile(map);

									MyDebugLog.e(TAG, portraitUrl);
									Log.e("test for upload", "sentPic 5 " + portraitUrl);

									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											MyDebugLog.e(TAG, "run here");
//											upLoadBar.setVisibility(View.INVISIBLE);
											destroyDialog();
										}
									});
								}

							} catch (ClientProtocolException e) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(ShowPersonalInfo.this,
												R.string.network_error, Toast.LENGTH_SHORT)
												.show();
									}

								});
								e.printStackTrace();
							} catch (IOException e) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(ShowPersonalInfo.this,
												R.string.network_error, Toast.LENGTH_SHORT)
												.show();
									}

								});
								e.printStackTrace();
							} catch (JSONException e) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(ShowPersonalInfo.this,
												R.string.network_error, Toast.LENGTH_SHORT)
												.show();
									}

								});
								e.printStackTrace();
							}
						}

					}.start();
				}else {

				}
			} else {
				upLoadBar.setVisibility(View.VISIBLE);
				Toast.makeText(this, getString(R.string.no_network_try_again), Toast.LENGTH_SHORT).show();
			}

		}

	}

	private void pictureZoom(View v) {

		// click fullscreen
		final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		ImageView imgView = getView();
		dialog.setContentView(imgView);
		dialog.show();

		// click dismiss
		imgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	private ImageView getView() {
		ImageView imgView = new ImageView(this);
		imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		File file = new File(PATH +imageName);
		if (!file.exists()) {
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
			Drawable drawable = getResources().getDrawable(sexRole[loginStatus.getInt("SEX", 0)]);
			imgView.setImageDrawable(drawable);
		}
		else
			try {
				FileInputStream is = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				imgView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return imgView;
	}
	
	public  boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	private void updateProfile (final Map<String, String> profileMap) {
		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			setInfoURL = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
				Method.POST, setInfoURL.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						destroyDialog();
						try {
							if(response.getInt("code") == 1) {

								if(!response.getString("sid").equals(null)) 
									saveSid(response.getString("sid"));
	
								SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								Editor editor = loginStatus.edit();
								if(profileMap.containsKey("birthday")) {
									editor.putString("BIRTH", BirthDayDate).commit();
								} else if(profileMap.containsKey("provinceId")) {
									editor.putInt("PROVINCE", Integer.parseInt(profileMap.get("provinceId")));
									editor.putInt("CITY", Integer.parseInt(profileMap.get("cityId")));
									editor.putInt("DIS", Integer.parseInt(profileMap.get("districtId")));
									editor.commit();
								} else if(profileMap.containsKey("portraitUrl")) {
									editor.putString("URL", profileMap.get("portraitUrl"));
									editor.commit();
									
									SharedPreferences savedUrl = getSharedPreferences("PreviousURL", 0);
									Editor editor1 = savedUrl.edit();
									editor1.putString("Url", profileMap.get("portraitUrl")).commit();
								}
								
								int infoCount = 0;
								if(loginStatus.getString("NICKNAME", null).length() > 0)
									infoCount++;
								if(loginStatus.getInt("SEX", 0) != 0)
									infoCount++;
								if(loginStatus.getString("BIRTH", null).length() > 0)
									infoCount++;
								if(loginStatus.getString("URL", null).length() > 0)
									infoCount++;
								if(loginStatus.getInt("PROVINCE", 0) != 0)
									infoCount++;
								
								mTextPercent.setText(getString(R.string.profile_complete) + String.valueOf((int) infoCount * 100 / 5) + "%");
								InfoPercent.setProgress((int) infoCount * 100 / 5);

							} else if (statusCode == -1) {
								Toast.makeText(getApplicationContext(), R.string.not_enough_params, Toast.LENGTH_SHORT).show();
							} else if (statusCode == -2) {
								Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
							} else if (statusCode == -99) {
								Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
							} else if (statusCode == -42) {
								Toast.makeText(getApplicationContext(), R.string.nick_name_already, Toast.LENGTH_SHORT).show();
							}
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						destroyDialog();
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {

			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map = profileMap;
				return map;
			}
		};

		mQueue.add(mJsonRequest);

	}

	@Override
	public void onResume(){
		super.onResume();
		Log.e("@@@@@@@", "onResume()");

		SharedPreferences homeKeyEvent = getSharedPreferences("HOMEKEY", 0);
		int homePressed = homeKeyEvent.getInt("homePressed", 0);
		SharedPreferences setSign = getSharedPreferences("SETTING", 0);
		int useSign = setSign.getInt("useSign", 0);
		if (homePressed == 1 && useSign == 1) {
			if(System.currentTimeMillis() - homeKeyEvent.getLong("TIME", 0) > 60 * 1000){
				Intent intent = new Intent();
				intent.setClass(ShowPersonalInfo.this, VerifyGestureActivity.class);
				startActivity(intent);
			}
		}
		
		if(userStatus == 1) {
			certiImage.setImageResource(R.drawable.unauth_icon);
		} else if(userStatus == 2) {
			certiImage.setImageResource(R.drawable.auth_icon);
		}
		
		File f = new File("/data/data/com.icloudoor.cloudoor/shared_prefs/LOGINSTATUS.xml");
		if(f.exists()){
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);

			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(sexRole[loginStatus.getInt("SEX", 0)]) // resource or drawable
			.showImageForEmptyUri(sexRole[loginStatus.getInt("SEX", 0)]) // resource or drawable
			.showImageOnFail(sexRole[loginStatus.getInt("SEX", 0)]) // resource or drawable
			.resetViewBeforeLoading(false)  // default
			.delayBeforeLoading(10)
			.cacheInMemory(false) // default
			.cacheOnDisk(false) // default
			.considerExifParams(false) // default
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
			.bitmapConfig(Bitmap.Config.ARGB_8888) // default
			.displayer(new SimpleBitmapDisplayer()) // default
			.handler(new Handler()) // default
			.displayer(new RoundedBitmapDisplayer(10))
			.build();
			
			String tempString = null;
			
			tempString = loginStatus.getString("NICKNAME", null);
			if (tempString != null){
				if (tempString.length() > 0){
					TVNickName.setText(loginStatus.getString("NICKNAME", null));
				}
			}

			if (loginStatus.getInt("SEX", 0) == 1) {
				TVSex.setText(R.string.male);
			} else if (loginStatus.getInt("SEX", 0) == 2) {
				TVSex.setText(R.string.female);
			} else if (loginStatus.getInt("SEX", 0) == 0) {
				TVSex.setText(R.string.female);
			}

			tempString = loginStatus.getString("BIRTH", null);
			if (tempString != null){
				if (tempString.length() > 0){
					TVDate.setText(tempString.substring(5,7) + getString(R.string.month) + tempString.substring(8) + getString(R.string.day));
					mYear = Integer.parseInt(tempString.substring(0, 4));
					mMonth = Integer.parseInt(tempString.substring(5,7)) - 1;
					mDay = Integer.parseInt(tempString.substring(8));
				} else {
					c = Calendar.getInstance();
					mYear = c.get(Calendar.YEAR);
					mMonth = c.get(Calendar.MONTH);
					mDay = c.get(Calendar.DAY_OF_MONTH);
				}
			} else {
				c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
			}
			
			int infoCount = 0;
			if(loginStatus.getString("NICKNAME", null).length() > 0)
				infoCount++;
			if(loginStatus.getInt("SEX", 0) != 0)
				infoCount++;
			if(loginStatus.getString("BIRTH", null).length() > 0)
				infoCount++;
			if(loginStatus.getString("URL", null).length() > 0)
				infoCount++;
			if(loginStatus.getInt("PROVINCE", 0) != 0)
				infoCount++;
			
			mTextPercent.setText(getString(R.string.profile_complete) + String.valueOf((int) infoCount * 100 / 5) + "%");
			InfoPercent.setProgress((int) infoCount * 100 / 5);
			
			provinceId = loginStatus.getInt("PROVINCE", 0);
			cityId = loginStatus.getInt("CITY", 0);
			districtId = loginStatus.getInt("DIS", 0);
			if(provinceId != 0) {
				if(getCityName(cityId).equals(getString(R.string.area))) {
					addText.setText(getProvinceName(provinceId) + getString(R.string.city) + getDistrictName(districtId) + getString(R.string.district));
				} else if(getCityName(cityId).equals(getString(R.string.coutry))) {
					addText.setText(getProvinceName(provinceId) + getString(R.string.city) + getDistrictName(districtId) + getString(R.string.coutry));
				} else if(getProvinceName(provinceId).equals(getString(R.string.neimeng)) || getProvinceName(provinceId).equals(getString(R.string.xizang)) 
						|| getProvinceName(provinceId).equals(getString(R.string.xinjiang))) {
					addText.setText(getProvinceName(provinceId) + getCityName(cityId) + getString(R.string.city) + getDistrictName(districtId) + getString(R.string.district));
				} else if (getProvinceName(provinceId).equals(getString(R.string.taiwan)) || getProvinceName(provinceId).equals(getString(R.string.hongkong)) 
						|| getProvinceName(provinceId).equals(getString(R.string.macao))) {
					addText.setText(getProvinceName(provinceId) + getCityName(cityId) + getDistrictName(districtId));
				} else {
					addText.setText(getProvinceName(provinceId) + getString(R.string.province) + getCityName(cityId) + getString(R.string.city) + getDistrictName(districtId) + getString(R.string.district));
				}
			} else {
				addText.setText(getString(R.string.default_address));
			}
			
			image.setImageResource(sexRole[loginStatus.getInt("SEX", 0)]);
		}
		
		File Imagefile = new File(PATH + imageName);
		if(Imagefile.exists()){
			MyDebugLog.e(TAG, "use local on resume");
			Bitmap bitmap = getLoacalBitmap(PATH + imageName);
			image.setImageBitmap(bitmap);
					
//			ImageLoader.getInstance().displayImage(imageUrl, image, options);
		} else {
			MyDebugLog.e(TAG, "ON RESUME file not exists, use net");
			if(portraitUrl != null){
				ImageLoader.getInstance().displayImage(portraitUrl, image, options);
			}
		}
	}
	
	public static Bitmap getLoacalBitmap(String url) {
	     try {
	          FileInputStream fis = new FileInputStream(url);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}

	public void initSpinnerData() {
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIdIndex = mCursorP.getColumnIndex("province_id");
		int cityIdIndex = mCursorP.getColumnIndex("city_id");
		int districtIdIndex = mCursorP.getColumnIndex("district_id");
		maxPlength = 1;
		maxClength = 1;
		maxDlength = 1;

		if (mCursorP.moveToFirst()) {
			int tempPId = mCursorP.getInt(provinceIdIndex);
			while(mCursorP.moveToNext()){
				if (mCursorP.getInt(provinceIdIndex) != tempPId) {
					tempPId = mCursorP.getInt(provinceIdIndex);
					maxPlength++;
				}
			}
			mCursorP.close();
		}

		if(mCursorC.moveToFirst()){
			int tempCcount = 1;
			int tempPId = mCursorC.getInt(provinceIdIndex);
			int tempCId = mCursorC.getInt(cityIdIndex);
			while (mCursorC.moveToNext()) {
				if(mCursorC.getInt(provinceIdIndex) == tempPId && mCursorC.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorC.getInt(cityIdIndex);
					tempCcount++;
				}else if(mCursorC.getInt(provinceIdIndex) != tempPId && mCursorC.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorC.getInt(provinceIdIndex);
					tempCId = mCursorC.getInt(cityIdIndex);
					if(tempCcount > maxClength) {
						maxClength = tempCcount;
					}
					tempCcount = 1;
				}
			}
			mCursorC.close();
		}

		if(mCursorD.moveToFirst()){
			int tempDcount = 1;
			int tempPId = mCursorD.getInt(provinceIdIndex);
			int tempCId = mCursorD.getInt(cityIdIndex);
			while (mCursorD.moveToNext()) {
				if(mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) == tempCId){
					tempDcount++;
				}else if(mCursorD.getInt(provinceIdIndex) == tempPId && mCursorD.getInt(cityIdIndex) != tempCId){
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}else if(mCursorD.getInt(provinceIdIndex) != tempPId && mCursorD.getInt(cityIdIndex) != tempCId){
					tempPId = mCursorD.getInt(provinceIdIndex);
					tempCId = mCursorD.getInt(cityIdIndex);
					if(tempDcount > maxDlength) {
						maxDlength = tempDcount;
					}
					tempDcount = 1;
				}
			}
			mCursorD.close();
		}

		provinceSet = new String[maxPlength];
		citySet = new String[maxPlength][maxClength];
		districtSet = new String[maxPlength][maxClength][maxDlength];
		int a = 0, b = 0, c = 0;
		Cursor mCursor = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		int provinceIndex = mCursor.getColumnIndex("province_short_name");
		int cityIndex = mCursor.getColumnIndex("city_short_name");
		int disdrictIndex = mCursor.getColumnIndex("district_short_name");
		if(mCursor.moveToFirst()){
			provinceSet[a] = mCursor.getString(provinceIndex);
			citySet[a][b] = mCursor.getString(cityIndex);
			districtSet[a][b][c] = mCursor.getString(disdrictIndex);

			while(mCursor.moveToNext()){
				if(mCursor.getString(provinceIndex).equals(provinceSet[a])){
					if(mCursor.getString(cityIndex).equals(citySet[a][b])){
						c++;
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}else{
						c = 0;
						b++;
						citySet[a][b] = mCursor.getString(cityIndex);
						districtSet[a][b][c] = mCursor.getString(disdrictIndex);
					}
				}else{
					b = 0;
					c = 0;
					a++;
					provinceSet[a] = mCursor.getString(provinceIndex);
					citySet[a][b] = mCursor.getString(cityIndex);
					districtSet[a][b][c] = mCursor.getString(disdrictIndex);
				}
			}
		}
		mCursor.close();
	}
	
	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

	QRCodeCreateDialog dialog;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.QRcode_layout:
			dialog = new QRCodeCreateDialog(this, R.style.QRCode_dialog);
			dialog.show();
			dialog.windowDeploy();
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
			String USERID = loginStatus.getString("USERID", "");
			String url = USERID;
			dialog.createQRImage(url);
			break;

		default:
			break;
		}
	}	
}
