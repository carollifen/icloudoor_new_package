package com.icloudoor.cloudoor;

import java.io.File;

import com.icloudoor.cloudoor.utli.JustifyTextView;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUs extends Activity {

	private RelativeLayout textLayout;
	private TextView text;
	private TextView versionName;
	private String versionname, versioncode;
	private RelativeLayout update;
	private RelativeLayout back;
	private View view1, view2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionname = pi.versionName == null ? "null" : pi.versionName;
				versioncode = pi.versionCode + "";
			}
		} catch (PackageManager.NameNotFoundException e) {
			
		}
		
		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);
		
		textLayout = (RelativeLayout) findViewById(R.id.text_about_us_layout);
		
		LayoutParams param = (LayoutParams) textLayout.getLayoutParams();
		LayoutParams param1 = (LayoutParams) view1.getLayoutParams();
		LayoutParams param2 = (LayoutParams) view2.getLayoutParams();
		param.width = screenWidth - 32*2;
		textLayout.setLayoutParams(param);
		
		text = (TextView) findViewById(R.id.text_about_us);
		text.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;云门是一款基于最先进互联网理念“万物互联”的软、" +
				"硬结合技术的手机应用，通过免费上门安装的“小盒子”+云门app即可实现手机摇一摇开门、打卡记录、电子门票等高级功能，并已实现开门无需网络，电子钥匙加密等技术。<br><br>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;云门app以安全方便为根基，着力于构建新型的智慧社区，令你轻松掌握物业信息、社区交流等最新社区资讯，更可实现公司打卡、线下娱乐设施电子票等日常工作、生活、娱乐的全方位服务，是您最贴心的私人助理！"));
		
//		new Thread(){
//        	
//        	@Override
//			public void run() {
//        		TextJustification.justify(text, screenWidth - 64);
//        	}
//        }.start();
        
        versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText("云门"+versionname+" Build"+versioncode);
        
        if(screenHeight <= 860) {
        	param1.height = 128 - 80;
        	param2.height = 198 - 120;
		} else if(screenHeight > 860 && screenHeight <= 1280) {
			param1.height = 128 - 20;
        	param2.height = 198 - 60;
		} else {
			param1.height = 128;
        	param2.height = 198;
		}
        view1.setLayoutParams(param1);
        view2.setLayoutParams(param2);
        
        back = (RelativeLayout) findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
        	
        });
        
        update = (RelativeLayout) findViewById(R.id.update);
        update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        switch (updateStatus) {
				        case UpdateStatus.Yes: // has update
				        	UmengUpdateAgent.showUpdateDialog(AboutUs.this, updateInfo);
				            break;
				        case UpdateStatus.No: // has no update
				        	Toast.makeText(AboutUs.this, getString(R.string.latest_version_now), Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.NoneWifi: // none wifi
				        	Toast.makeText(AboutUs.this, R.string.update_only_in_wifi, Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.Timeout: // time out
				        	Toast.makeText(AboutUs.this, R.string.get_update_timeout, Toast.LENGTH_SHORT).show();
				            break;
				        }
				    }
				});
				
				UmengUpdateAgent.setDownloadListener(new UmengDownloadListener(){

				    @Override
				    public void OnDownloadStart() {
				    }

				    @Override
				    public void OnDownloadUpdate(int progress) {
				    }

				    @Override
				    public void OnDownloadEnd(int result, String file) {
				    	
				    	SharedPreferences setting = getSharedPreferences("com.icloudoor.clouddoor", 0);
				    	setting.edit().putBoolean("FIRST", true).commit();
				    	
				    	File f = new File(file);
				    	UmengUpdateAgent.startInstall(AboutUs.this, f);

				    }           
				});
				
				UmengUpdateAgent.update(AboutUs.this);
			}
        	
        });
	}

}
