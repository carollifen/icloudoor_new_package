package com.icloudoor.cloudoor;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.widget.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity{
	
	private final  String mPageName = "BesaActivity";
	private RequestQueue mQueue;
	public NetworkInterface networkInterface;
	public MyProgressDialog dialog;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mQueue = Volley.newRequestQueue(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart( mPageName );
		MobclickAgent.onResume(this);
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd( mPageName );
		MobclickAgent.onPause(this);
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
				dialog = new MyProgressDialog(BaseActivity.this, R.style.mydialog);
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
	
	
	public void getNetworkData(NetworkInterface networkInterface,String httpurl, final Map<String, String> map){
	this.networkInterface = networkInterface;
		String url = UrlUtils.HOST + httpurl+ "?sid=" + loadSid();
		loading();
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				url, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						BaseActivity.this.networkInterface.onSuccess(response);
						destroyDialog();
						System.out.println(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						BaseActivity.this.networkInterface.onFailure(error);
						destroyDialog();
					}
				}){
			 
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				// TODO Auto-generated method stub
				return map;
			}
		};
		mQueue.add(mJsonRequest);
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
  
    /** 
     * ¸ù¾ÝÊÖ»úµÄ·Ö±æÂÊ´Ó px(ÏñËØ) µÄµ¥Î» ×ª³ÉÎª dp 
     */  
    public  int px2dip(float pxValue) {  
        final float scale = getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
