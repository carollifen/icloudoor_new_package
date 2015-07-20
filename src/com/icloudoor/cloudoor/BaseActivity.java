package com.icloudoor.cloudoor;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.icloudoor.cloudoor.chat.HXSDKHelper;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.widget.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

	private final String mPageName = "BesaActivity";
	public RequestQueue mQueue;
	public NetworkInterface networkInterface;
	public MyProgressDialog dialog;
	public Version version;

	private MyAreaDBHelper mAreaDBHelper;
	private SQLiteDatabase mAreaDB;
	private final String DATABASE_NAME = "area.db";
	private final String TABLE_NAME = "tb_core_area";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		version = new Version(getApplicationContext());
		mQueue = Volley.newRequestQueue(this);

		mAreaDBHelper = new MyAreaDBHelper(BaseActivity.this, DATABASE_NAME,
				null, 1);
		mAreaDB = mAreaDBHelper.getWritableDatabase();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HXSDKHelper.getInstance().getNotifier().reset();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
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
				dialog = new MyProgressDialog(BaseActivity.this,
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

	public void getNetworkData(NetworkInterface networkInterface,
			String httpurl, String josn, final boolean isShowLoadin) {
		this.networkInterface = networkInterface;
		String url = UrlUtils.HOST + httpurl + "?sid=" + loadSid() + "&ver="
				+ version.getVersionName();
		if (isShowLoadin)
			loading();

		MyRequestBody requestBody = new MyRequestBody(url, josn,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						BaseActivity.this.networkInterface.onSuccess(response);
						if (isShowLoadin)
							destroyDialog();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						BaseActivity.this.networkInterface.onFailure(error);
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
				+ version.getVersionName();
		if (isShowLoadin)
			loading();
		MyJsonObjectRequest requestBody = new MyJsonObjectRequest(Method.POST,url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						BaseActivity.this.networkInterface.onSuccess(response);
						destroyDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						BaseActivity.this.networkInterface.onFailure(error);
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

	public void destroyDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}

	public void showToast(int resid) {
		Toast.makeText(this, resid, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ¸ù¾ÝÊÖ»úµÄ·Ö±æÂÊ´Ó dp µÄµ¥Î» ×ª³ÉÎª px(ÏñËØ)
	 */
	public int dip2px(float dpValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * ¸ù¾ÝÊÖ»úµÄ·Ö±æÂÊ´Ó px(ÏñËØ) µÄµ¥Î» ×ª³ÉÎª dp
	 */
	public int px2dip(float pxValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public String getProvinceName(int provinceId) {
		String provinceName = null;
		Cursor mCursorP = mAreaDB.rawQuery("select * from " + TABLE_NAME, null);
		if (mCursorP.moveToFirst()) {
			int provinceIndex = mCursorP.getColumnIndex("province_short_name");
			int provinceIdIndex = mCursorP.getColumnIndex("province_id");
			do {
				int tempPID = mCursorP.getInt(provinceIdIndex);
				String tempPName = mCursorP.getString(provinceIndex);
				if (tempPID == provinceId) {
					provinceName = tempPName;
					break;
				}
			} while (mCursorP.moveToNext());
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
			do {
				int tempCID = mCursorC.getInt(cityIdIndex);
				String tempCName = mCursorC.getString(cityIndex);
				if (tempCID == cityId) {
					cityName = tempCName;
					break;
				}
			} while (mCursorC.moveToNext());
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
			do {
				int tempDID = mCursorD.getInt(districtIdIndex);
				String tempDName = mCursorD.getString(districtIndex);
				if (tempDID == districtId) {
					districtName = tempDName;
					break;
				}
			} while (mCursorD.moveToNext());
		}
		mCursorD.close();
		return districtName;
	}
}
