package com.icloudoor.cloudoor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

	private RequestQueue mQueue;
	private String HOST = UrlUtils.HOST;
	private URL getInfoURL;
	private URL setInfoURL;
	private int statusCode;
	private String sid;
	private JSONObject data;
	
	private int setPersonal = 0;
	
	private String name = null, nickname = null, birthday = null, id = null;
	private String province = null, city = null, district = null;
	private int sex = 0, provinceid = 0, cityid = 0, districtid = 0;
	private boolean isHasPropServ;
	
	private TextView TVName;
	private TextView TVNickName;
	private TextView TVSex;
	private TextView TVprovince;
	private TextView TVcity;
	private TextView TVdistrict;
	private TextView TVyear;
	private TextView TVmonth;
	private TextView TVday;
	private TextView TVid;
	private ImageView image;
	
	private RelativeLayout back;
	private RelativeLayout toModifyProfile;
	private RelativeLayout SetPersonalNickname;
	private RelativeLayout SetPersonalSex;
	private RelativeLayout SetBirthday;
	
	private RelativeLayout ChangePhoto;
	private SelectPicPopupWindow menuWindow;
	
	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";
	
	private Bitmap bitmap;
	private Thread mThread;
	
	private static final int MSG_SUCCESS = 0;// get the image success
	private static final int MSG_FAILURE = 1;// fail
	private static final int RESULT_REQUEST_CODE = 11;
	private static final int RESULT_SET_NICKNAME = 4;
	private static final int RESULT_SET_SEX = 5;
	
	private String portraitUrl;

	private ProgressBar upLoadBar;
	
	//
	private int userStatus;
	private ImageView certiImage;
	private TextView certiText;
	
	//
	private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Cloudoor/CacheImage/";
	private String imageName = "myImage.jpg";
	
	private  String formatID;
	
	boolean isDebug = DEBUG.isDebug;
	
	String imageUrl;
	DisplayImageOptions options;
	String tempURL;
	RelativeLayout QRcode_layout;
	private Version version;
	
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
        
        options = new DisplayImageOptions.Builder()
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
        .displayer(new RoundedBitmapDisplayer(10))
        .build();
		
        
		initViews();
		
//		setupUI(findViewById(R.id.personal_info_main));
		
		ChangePhoto = (RelativeLayout) findViewById(R.id.personal_photo_change);
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
	}
	File cameraFile;
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
			
			case RESULT_SET_NICKNAME:
				
				TVNickName.setText(getIntent().getStringExtra("CurrentNickname"));
				
				
				
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
					updatePortraiUrl();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null){
					Log.e("test for upload", "data != null  RESULT_REQUEST_CODE");
					sentPicToNext(data);
					updatePortraiUrl();
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
    File imageFile;
    private void sentPicToNext(Intent data) {

		Bundle bundle = data.getExtras();
		if (bundle != null) {
			Log.e("test for upload", "sentPic 1");
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
//				NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
						|| NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()) {
					Log.e("test for upload", "sentPic 2");
					imageFile = new File(PATH + System.currentTimeMillis() + ".jpg");
					imageFile.getParentFile().mkdirs();
					Bitmap photo = bundle.getParcelable("data");
					TakePicFileUtil.getInstance().saveBitmap(photo);
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

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
//									upLoadBar.setVisibility(View.VISIBLE);
								}
							});

							MyDebugLog.e(TAG, "thread run");
							Log.e("test for upload", "sentPic 3");

							try {
								sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}

							HttpClient httpClient = new DefaultHttpClient();
							HttpPost postRequest = new HttpPost(HOST
									+ "/user/api/uploadPortrait.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());

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
									JSONObject data = jsObj.getJSONObject("data");
									portraitUrl = data.getString("portraitUrl");
									SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
									Editor edit = loginStatus.edit();
									edit.putString("URL", portraitUrl);
									edit.commit();
									
									MyDebugLog.e(TAG, portraitUrl);
									Log.e("test for upload", "sentPic 5 " + portraitUrl);
									
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											MyDebugLog.e(TAG, "run here");
//											upLoadBar.setVisibility(View.INVISIBLE);
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
//				upLoadBar.setVisibility(View.VISIBLE);
				Toast.makeText(this, getString(R.string.no_network_try_again), Toast.LENGTH_SHORT).show();
			}
			
		}

	}
    
	
	public void initViews() {
		TVName = (TextView) findViewById(R.id.personal_info_name);
		TVNickName = (TextView) findViewById(R.id.personal_info_NickName);
		TVSex = (TextView) findViewById(R.id.personal_info_sexName);
		TVprovince = (TextView) findViewById(R.id.personal_info_province);
		TVcity = (TextView) findViewById(R.id.personal_info_city);
		TVdistrict = (TextView) findViewById(R.id.personal_info_district);
		TVyear = (TextView) findViewById(R.id.personal_info_year);
		TVmonth = (TextView) findViewById(R.id.personal_info_month);
		TVday = (TextView) findViewById(R.id.personal_info_day);
		TVid = (TextView) findViewById(R.id.personal_info_ID);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		image = (ImageView) findViewById(R.id.personal_info_small_image);
		SetPersonalNickname = (RelativeLayout) findViewById(R.id.set_nickname);
		SetPersonalSex = (RelativeLayout) findViewById(R.id.set_sex);

		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pictureZoom(image);
				
			}
		});
		
		//
		certiImage = (ImageView) findViewById(R.id.certi_or_not_image);

		
		toModifyProfile = (RelativeLayout) findViewById(R.id.tomodify_person_info);
			
		
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ShowPersonalInfo.this.finish();
			}
			
		});
		
//		toModifyProfile.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//
//				Intent intent = new Intent();
//				
//
//				if (userStatus == 1) {
//					intent.setClass(ShowPersonalInfo.this, SetPersonalInfoNotCerti.class);
//					startActivityForResult(intent, 0);
//				} else if (userStatus == 2) {
//					intent.setClass(ShowPersonalInfo.this, SetPersonalInfo.class);
//					intent.putExtra("Whereis", "settingFace");
//					startActivityForResult(intent, 0);
//				}				
//			}
//			
//		});
	}
	
	private void pictureZoom(View v) {

//      click fullscreen
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
        		Drawable drawable = getResources().getDrawable(R.drawable.default_icon_male);
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
	private void updatePortraiUrl () {
		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			setInfoURL = new URL(HOST + "/user/manage/updateProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
				Method.POST, setInfoURL.toString(), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getString("sid") != null) {
								sid = response.getString("sid");
								saveSid(sid);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						MyDebugLog.e("TEST", response.toString());
						
						try {
							statusCode = response.getInt("code");
						} catch (JSONException e) {
							e.printStackTrace();
						}if(statusCode == 1) {
							setPersonal = 1;
							SharedPreferences personalInfo = getSharedPreferences("PERSONSLINFO", MODE_PRIVATE);
							Editor editor = personalInfo.edit();
							editor.putInt("SETINFO", setPersonal);
							editor.commit();
							
							SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
							Editor editor1 = loginStatus.edit();
							editor1.putString("URL", portraitUrl);
							editor1.commit();
														
							
						} else if (statusCode == -1) {
							Toast.makeText(getApplicationContext(), R.string.not_enough_params, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -2) {
							Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -99) {
							Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -42) {
							Toast.makeText(getApplicationContext(), R.string.nick_name_already, Toast.LENGTH_SHORT).show();
						}
													
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}) {
			
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("portraitUrl", portraitUrl);
				return map;
			}
		};
		
			mQueue.add(mJsonRequest);
				
	}
	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		if (requestCode == 0 && resultCode == RESULT_OK) {
//			mQueue = Volley.newRequestQueue(this);
//			sid = loadSid();
//			try {
//				getInfoURL = new URL(HOST + "/user/manage/getProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//			MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
//					Method.POST, getInfoURL.toString(), null,
//					new Response.Listener<JSONObject>() {
//
//						@Override
//						public void onResponse(JSONObject response) {
//							try {
//								if (response.getString("sid") != null) {
//									sid = response.getString("sid");
//									saveSid(sid);
//								}
//								statusCode = response.getInt("code");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//
//							MyDebugLog.e("TEST", response.toString());
//
//							if (statusCode == 1) {
//								try {
//									JSONObject Data = response.getJSONObject("data");
//
//									name = Data.getString("userName");
//									nickname = Data.getString("nickname");
//									birthday = Data.getString("birthday");
//									id = Data.getString("idCardNo");
//									sex = Data.getInt("sex");
//									provinceid = Data.getInt("provinceId");
//									cityid = Data.getInt("cityId");
//									districtid = Data.getInt("districtId");
//									portraitUrl = Data.getString("portraitUrl");
//									isHasPropServ = Data.getBoolean("isHasPropServ");
//
//									SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
//									Editor edit = loginStatus.edit();
//									edit.putString("NAME", name);
//									edit.putString("NICKNAME",nickname);
//									edit.putString("ID", id);
//									edit.putString("BIRTH", birthday);
//									edit.putInt("SEX", sex);
//									edit.putInt("PROVINCE",provinceid);
//									edit.putInt("CITY", cityid);
//									edit.putInt("DIS", districtid);
//									edit.putString("URL", portraitUrl);
//									edit.putBoolean("isHasPropServ", isHasPropServ);
//									edit.commit();
//
//									SharedPreferences saveProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);
//									Editor editor = saveProfile.edit();
//									editor.putString("NAME", name);
//									editor.putString("NICKNAME", nickname);
//									editor.putString("ID", id);
//									editor.putString("PROVINCE", province);
//									editor.putString("CITY", city);
//									editor.putString("DISTRICT", district);
//									editor.putInt("PROVINCEID", provinceid);
//									editor.putInt("CITYID", cityid);
//									editor.putInt("DISTRICTID", districtid);
//									editor.putInt("SEX", sex);
//									if(birthday.length() > 0){
//										editor.putString("YEAR", birthday.substring(0, 4));
//										editor.putString("MONTH", birthday.substring(5, 7));
//										editor.putString("DAY", birthday.substring(8));
//									}
//									editor.putBoolean("isHasPropServ", isHasPropServ);
//									editor.commit();
//
//									File f = new File(PATH + imageName);
//									MyDebugLog.e(TAG, PATH + imageName);
//									if (f.exists() && !tempURL.equals(portraitUrl)) {
//										tempURL = portraitUrl;
//										ImageLoader.getInstance().displayImage(imageUrl, image, options);
//										
//										MyDebugLog.e(TAG, "use local");
////										BitmapFactory.Options opts = new BitmapFactory.Options();
////										opts.inTempStorage = new byte[100 * 1024];
////										opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
////										opts.inPurgeable = true;
//////										opts.inSampleSize = 4;
////										Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
////										image.setImageBitmap(bm);
//									} else {
//										// request bitmap in the new thread
//										if (portraitUrl != null && !tempURL.equals(portraitUrl)) {
//											MyDebugLog.e(TAG, "use net");
//											tempURL = portraitUrl;
//											ImageLoader.getInstance().displayImage(portraitUrl, image, options);
////											if (mThread == null) {
////												mThread = new Thread(runnable);
////												mThread.start();
////											}
//										}
//									}
//
//									if (provinceid != 0) {
//										province = getProvinceName(provinceid);
//										TVprovince.setText(province);
//									}
//
//									if (cityid != 0) {
//										city = getCityName(cityid);
//										TVcity.setText(city);
//									}
//
//									if (districtid != 0) {
//										district = getDistrictName(districtid);
//										TVdistrict.setText(district);
//									}
//
//									if (name != null)
//										TVName.setText(name);
//									if (nickname != null)
//										TVNickName.setText(nickname);
//
//									if (sex == 1) {
//										TVSex.setText(R.string.male);
//									} else if (sex == 2) {
//										TVSex.setText(R.string.female);
//									}
//
//									if (id != null) {
//										formatID = changeNum(id);
//										TVid.setText(formatID);
//									}
//
//									if (birthday != null) {
//										TVyear.setText(birthday.substring(0, 4));
//										TVmonth.setText(birthday.substring(5, 7));
//										TVday.setText(birthday.substring(8));
//									}
//
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//							} else if (statusCode == -1) {
//								Toast.makeText(getApplicationContext(),
//										R.string.wrong_params,
//										Toast.LENGTH_SHORT).show();
//							} else if (statusCode == -2) {
//								Toast.makeText(getApplicationContext(),
//										R.string.not_login, Toast.LENGTH_SHORT)
//										.show();
//							} else if (statusCode == -99) {
//								Toast.makeText(getApplicationContext(),
//										R.string.unknown_err,
//										Toast.LENGTH_SHORT).show();
//							}
//						}
//					}, new Response.ErrorListener() {
//
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
//						}
//					});
//			mQueue.add(mJsonRequest);
//		}
//	}

	public String getProvinceName(int provinceId) {
		String provinceName = null;
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorP.moveToFirst()) {
			int provinceIndex = mCursorP.getColumnIndex("province_short_name");
			int provinceIdIndex = mCursorP.getColumnIndex("province_id");
			do{
				int tempPID = mCursorP.getInt(provinceIdIndex);
			    String tempPName = mCursorP.getString(provinceIndex);
				if(tempPID == provinceId){
					provinceName = tempPName;
					break;
				}		
			}while(mCursorP.moveToNext());		
		}
		mCursorP.close();
		return provinceName;
	}
	
	public String getCityName(int cityId) {
		String cityName = null;
		Cursor mCursorC = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorC.moveToFirst()) {
			int cityIndex = mCursorC.getColumnIndex("city_short_name");
			int cityIdIndex = mCursorC.getColumnIndex("city_id");
			do{
				int tempCID = mCursorC.getInt(cityIdIndex);
			    String tempCName = mCursorC.getString(cityIndex);
				if(tempCID == cityId){
					cityName = tempCName;
					break;
				}		
			}while(mCursorC.moveToNext());		
		}
		mCursorC.close();
		return cityName;
	}
	
	public String getDistrictName(int districtId) {
		String districtName = null;
		Cursor mCursorD = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorD.moveToFirst()) {
			int districtIndex = mCursorD.getColumnIndex("district_short_name");
			int districtIdIndex = mCursorD.getColumnIndex("district_id");
			do{
				int tempDID = mCursorD.getInt(districtIdIndex);
			    String tempDName = mCursorD.getString(districtIndex);
				if(tempDID == districtId){
					districtName = tempDName;
					break;
				}		
			}while(mCursorD.moveToNext());		
		}
		mCursorD.close();
		return districtName;
	}

	@Override
	public void onResume(){
		super.onResume();

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
//			certiText.setText(R.string.not_certi);
		} else if(userStatus == 2) {
			certiImage.setImageResource(R.drawable.auth_icon);
//			certiText.setText(R.string.certi);
		}

		File Imagefile = new File(PATH + imageName);
		if(Imagefile.exists()){
			MyDebugLog.e(TAG, "use local on resume");
			ImageLoader.getInstance().displayImage(imageUrl, image, options);
		} else {
			MyDebugLog.e(TAG, "ON RESUME file not exists, use net");
			if(portraitUrl != null){
				ImageLoader.getInstance().displayImage(portraitUrl, image, options);
			}
		}

		File f = new File("/data/data/com.icloudoor.cloudoor/shared_prefs/LOGINSTATUS.xml");
		if(f.exists()){
			SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);

			String tempString;
//			if (tempString != null){
//				if (tempString.length() > 0){
//					TVName.setText(loginStatus.getString("NAME", null));
//				}
//			}
//			tempString = loginStatus.getString("NICKNAME", null);
//			if (tempString != null){
//				if (tempString.length() > 0){
//					TVNickName.setText(loginStatus.getString("NICKNAME", null));
//				}
//			}

//			if(loginStatus.getInt("SEX", 1) == 1){
//				TVSex.setText(R.string.male);
//			} else if(loginStatus.getInt("SEX", 1) == 2){
//				TVSex.setText(R.string.female);
//			}

			tempString = loginStatus.getString("BIRTH", null);
			if (tempString != null){
				if (tempString.length() > 0){
//					TVyear.setText(tempString.substring(0, 4));
					TVmonth.setText(tempString.substring(5, 7));
					TVday.setText(tempString.substring(8));
				}
			}

//			tempString = loginStatus.getString("ID", null);
//			if (tempString != null){
//				if (tempString.length() > 0){
//					String tempFormatID = changeNum(loginStatus.getString("ID", null));
//					TVid.setText(tempFormatID);
//				}
//			}

			if (loginStatus.getInt("PROVINCE", 0) != 0) {
				province = getProvinceName(loginStatus.getInt("PROVINCE", 0));
				TVprovince.setText(province);
			}

			if (loginStatus.getInt("CITY", 0) != 0) {
				city = getCityName(loginStatus.getInt("CITY", 0));
				TVcity.setText(city);
			}

			if (loginStatus.getInt("DIS", 0) != 0) {
				district = getDistrictName(loginStatus.getInt("DIS", 0));
				TVdistrict.setText(district);
			}
		}

		mQueue = Volley.newRequestQueue(this);
		sid = loadSid();
		try {
			getInfoURL = new URL(HOST + "/user/manage/getProfile.do" + "?sid=" + sid + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
				Method.POST, getInfoURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getString("sid") != null) {
								sid = response.getString("sid");
								saveSid(sid);
							}
							statusCode = response.getInt("code");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						MyDebugLog.e("TEST", response.toString());

						if (statusCode == 1) {
							try {
								JSONObject Data = response.getJSONObject("data");

								name = Data.getString("userName");
								nickname = Data.getString("nickname");
								birthday = Data.getString("birthday");
								id = Data.getString("idCardNo");
								sex = Data.getInt("sex");
								provinceid = Data.getInt("provinceId");
								cityid = Data.getInt("cityId");
								districtid = Data.getInt("districtId");
								portraitUrl = Data.getString("portraitUrl");
								isHasPropServ = Data.getBoolean("isHasPropServ");

								SharedPreferences loginStatus = getSharedPreferences("LOGINSTATUS", MODE_PRIVATE);
								Editor edit = loginStatus.edit();
								edit.putString("URL", portraitUrl);
								edit.putBoolean("isHasPropServ", isHasPropServ);
								edit.commit();

								SharedPreferences saveProfile = getSharedPreferences("PROFILE", MODE_PRIVATE);
								Editor editor = saveProfile.edit();
								editor.putString("NAME", name);
								editor.putString("NICKNAME", nickname);
								editor.putString("ID", id);
								editor.putString("PROVINCE", province);
								editor.putString("CITY", city);
								editor.putString("DISTRICT", district);
								editor.putInt("PROVINCEID", provinceid);
								editor.putInt("CITYID", cityid);
								editor.putInt("DISTRICTID", districtid);
								editor.putInt("SEX", sex);
								edit.putBoolean("isHasPropServ", isHasPropServ);
								if(birthday.length() > 0){
									editor.putString("YEAR", birthday.substring(0, 4));
									editor.putString("MONTH", birthday.substring(5, 7));
									editor.putString("DAY", birthday.substring(8));
								}
								editor.commit();

								File f = new File(PATH + imageName);
								MyDebugLog.e(TAG, PATH + imageName);
								if (f.exists() && !tempURL.equals(portraitUrl)) {
									tempURL = portraitUrl;
									
									ImageLoader.getInstance().displayImage(imageUrl, image, options);
									
									MyDebugLog.e(TAG, "use local on resume request");
//									BitmapFactory.Options opts = new BitmapFactory.Options();
//									opts.inTempStorage = new byte[100 * 1024];
//									opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//									opts.inPurgeable = true;
////									opts.inSampleSize = 4;
//									Bitmap bm = BitmapFactory.decodeFile(PATH + imageName, opts);
//									image.setImageBitmap(bm);
								} else {
									// request bitmap in the new thread
									if (portraitUrl != null && !tempURL.equals(portraitUrl)) {
										tempURL = portraitUrl;
										MyDebugLog.e(TAG, "use net");
										ImageLoader.getInstance().displayImage(portraitUrl, image, options);
//										if (mThread == null) {
//											mThread = new Thread(runnable);
//											mThread.start();
//										}
									}
								}

								if (provinceid != 0) {
									province = getProvinceName(provinceid);
									TVprovince.setText(province);
								}

								if (cityid != 0) {
									city = getCityName(cityid);
									TVcity.setText(city);
								}

								if (districtid != 0) {
									district = getDistrictName(districtid);
									TVdistrict.setText(district);
								}

//								if (name.length() > 0)
//									TVName.setText(name);
								if (nickname.length() > 0)
									TVNickName.setText(nickname);

								if (sex == 1) {
									TVSex.setText(R.string.male);
								} else if (sex == 2) {
									TVSex.setText(R.string.female);
								}

//								if (id.length() > 0) {
//									formatID = changeNum(id);
//									TVid.setText(formatID);
//								}

								if (birthday.length() > 0) {
//									TVyear.setText(birthday.substring(0, 4));
									TVmonth.setText(birthday.substring(5, 7));
									TVday.setText(birthday.substring(8));
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (statusCode == -1) {
							Toast.makeText(getApplicationContext(), R.string.wrong_params, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -2) {
							Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
						} else if (statusCode == -99) {
							Toast.makeText(getApplicationContext(),
									R.string.unknown_err,
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(mJsonRequest);
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
				org.apache.http.HttpResponse httpResponse = httpClient.execute(httpGet);

				BitmapFactory.Options opts=new BitmapFactory.Options();
				opts.inTempStorage = new byte[100 * 1024];
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
				opts.inPurgeable = true;
//				opts.inSampleSize = 4;
				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity().getContent(), null, opts);
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}

			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};
	
	public String changeNum(String str) {
		if (str != null) {
			StringBuilder sb = new StringBuilder(str);

			for (int i = 6; i < sb.length(); i++) {
				sb.setCharAt(i, '*');
			}
			str = sb.toString();
		}
		return str;
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
