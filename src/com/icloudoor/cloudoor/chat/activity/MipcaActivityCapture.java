package com.icloudoor.cloudoor.chat.activity;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.chat.entity.SearchUserInfo;
import com.icloudoor.cloudoor.chat.entity.SearchUserList;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback ,NetworkInterface{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private RequestQueue mQueue;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mQueue = Volley.newRequestQueue(this);
		setContentView(R.layout.activity_capture);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		
		ImageView mButtonBack = (ImageView) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();
				
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		System.out.println("resultString = "+resultString);
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
//			String url = UrlUtils.HOST + "/user/im/searchUser.do"+ "?sid=" + loadSid()+resultString;
			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray();
			try {
				array.put(resultString);
				object.put("userIds", array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getNetworkData(this, "/user/im/getUsersDetailWsIsFriend.do", object.toString(), true);
//			Intent resultIntent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putString("result", resultString);
//			bundle.putParcelable("bitmap", barcode);
//			resultIntent.putExtras(bundle);
		}
		
	}
	
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		
		SearchUserInfo searchUserInfo = GsonUtli.jsonToObject(response.toString(), SearchUserInfo.class);
		if(searchUserInfo!=null){
			List<SearchUserList> data = searchUserInfo.getData();
			if(data!=null && data.size()>0){
				SearchUserList searchUserList = data.get(0);
				Boolean isFirend = searchUserList.getIsFriend();
				if(isFirend){
					Intent intent = new Intent(MipcaActivityCapture.this,FriendDetailActivity.class);
					intent.putExtra("CityId", searchUserList.getCityId());
					intent.putExtra("DistrictId", searchUserList.getDistrictId());
					intent.putExtra("ProvinceId", searchUserList.getProvinceId());
					intent.putExtra("Nickname", searchUserList.getNickname());
					intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
					intent.putExtra("Sex", searchUserList.getSex());
					intent.putExtra("UserId", searchUserList.getUserId());
					startActivity(intent);
				}else{
					Intent intent = new Intent(MipcaActivityCapture.this,UsersDetailActivity.class);
					intent.putExtra("CityId", searchUserList.getCityId());
					intent.putExtra("DistrictId", searchUserList.getDistrictId());
					intent.putExtra("ProvinceId", searchUserList.getProvinceId());
					intent.putExtra("Nickname", searchUserList.getNickname());
					intent.putExtra("PortraitUrl", searchUserList.getPortraitUrl());
					intent.putExtra("Sex", searchUserList.getSex());
					intent.putExtra("UserId", searchUserList.getUserId());
					startActivity(intent);
				}
				MipcaActivityCapture.this.finish();
			}else{
				MipcaActivityCapture.this.finish();
				showToast(R.string.search_result);
			}
		}
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		finish();
	}

}