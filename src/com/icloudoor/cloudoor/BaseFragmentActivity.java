package com.icloudoor.cloudoor;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.widget.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class BaseFragmentActivity extends FragmentActivity{
	
	
	public RequestQueue mQueue;
	public NetworkInterface networkInterface;
	public MyProgressDialog dialog;
	public Version version;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		version = new Version(getApplicationContext());
		mQueue = Volley.newRequestQueue(this);
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	public void getNetworkData(NetworkInterface networkInterface,String httpurl, String josn,final boolean isShowLoadin){
		this.networkInterface = networkInterface;
			String url = UrlUtils.HOST + httpurl+ "?sid=" + loadSid()+"&ver=" + version.getVersionName();
			if(isShowLoadin)
			loading();
			
			MyRequestBody requestBody = new MyRequestBody( url, josn,new Response.Listener<JSONObject>() {
				
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					BaseFragmentActivity.this.networkInterface.onSuccess(response);
					if(isShowLoadin)
					destroyDialog();
				}
				
			},new Response.ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					BaseFragmentActivity.this.networkInterface.onFailure(error);
					if(isShowLoadin)
					destroyDialog();
					showToast(R.string.network_error);
				}
			});
			mQueue.add(requestBody);
			
	}
	public String loadSid() {
		SharedPreferences loadSid = getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
	private int countDestroyDialogKeycodeBack = 0;
	public void loading() {
		try {
			if (this.isFinishing()) {
				return;
			}
			if (dialog == null || !dialog.isShowing()) {
				countDestroyDialogKeycodeBack = 0;
				dialog = new MyProgressDialog(BaseFragmentActivity.this, R.style.mydialog);
				dialog.show();
				dialog.setCancelable(false);
				dialog.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialogs, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							countDestroyDialogKeycodeBack++;
							if (countDestroyDialogKeycodeBack == 3) {
								dialog.dismiss();
							}
						}
						return false;
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void destroyDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}
	
	public void showToast(int resid){
		Toast.makeText(this, resid, Toast.LENGTH_SHORT).show();
	}
	
	
	 /** 
     * ¸ù¾ÝÊÖ»úµÄ·Ö±æÂÊ´Ó dp µÄµ¥Î» ×ª³ÉÎª px(ÏñËØ) 
     */  
    public  int dip2px(float dpValue) {  
        final float scale = getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

}
