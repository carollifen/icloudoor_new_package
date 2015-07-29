package com.icloudoor.cloudoor;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.widget.MyProgressDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseFragment extends Fragment{
	
	public NetworkInterface networkInterface;
	public Version version;
	public MyProgressDialog dialog;
	public RequestQueue mQueue;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		version = new Version(getActivity().getApplicationContext());
		mQueue = Volley.newRequestQueue(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	public void getNetworkData(NetworkInterface networkInterface,
			String httpurl, String josn, final boolean isShowLoadin) {
		this.networkInterface = networkInterface;
		String url = UrlUtils.HOST + httpurl + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		if (isShowLoadin)
			loading();

		MyRequestBody requestBody = new MyRequestBody(url, josn,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						BaseFragment.this.networkInterface.onSuccess(response);
						if (isShowLoadin)
							destroyDialog();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						BaseFragment.this.networkInterface.onFailure(error);
						if (isShowLoadin)
							destroyDialog();
						showToast(R.string.network_error);
					}
				});
		mQueue.add(requestBody);

	}
	
	
	public void getMyJsonObjectRequest(NetworkInterface networkInterface,
			String httpurl, final Map<String, String> map, final boolean isShowLoadin) {
		this.networkInterface = networkInterface;
		String url = UrlUtils.HOST + httpurl + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName() + "&imei=" + version.getDeviceId();
		if (isShowLoadin)
			loading();
		MyJsonObjectRequest requestBody = new MyJsonObjectRequest(Method.POST,url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						BaseFragment.this.networkInterface.onSuccess(response);
						destroyDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						BaseFragment.this.networkInterface.onFailure(error);
						destroyDialog();
						showToast(R.string.network_error);
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return map;
			}
		};
		mQueue.add(requestBody);

	}
	
	private int countDestroyDialogKeycodeBack = 0;
	
	
	public void destroyDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}

	public void showToast(int resid) {
		Toast.makeText(getActivity(), resid, Toast.LENGTH_SHORT).show();
	}

	public void saveSid(String sid) {	
		if(getActivity() != null) {
			SharedPreferences savedSid = getActivity().getSharedPreferences("SAVEDSID", 0);
			Editor editor = savedSid.edit();
			editor.putString("SID", sid);
			editor.commit();
		}
	}

	public void loading() {
		try {
			if (getActivity().isFinishing()) {
				return;
			}
			if (dialog == null || !dialog.isShowing()) {
				countDestroyDialogKeycodeBack = 0;
				dialog = new MyProgressDialog(getActivity(),
						R.style.mydialog);
				dialog.show();
				dialog.setCancelable(false);
				dialog.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialogs, int keyCode,
							KeyEvent event) {
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
	
	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences("SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}
	
}
