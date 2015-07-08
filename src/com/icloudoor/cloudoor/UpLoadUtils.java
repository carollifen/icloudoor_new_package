package com.icloudoor.cloudoor;

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
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class UpLoadUtils {
	
	private Context context;
	
	private String TAG = this.getClass().getSimpleName();
	
	private String foldPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cloudoor/";
	private String fileName = "cacheTrace.txt";
	
	private Version version = new Version(context);
	
	public void writeOpenInfoToFile(String time, String userId, String doorId, boolean successOrNot, String modelNameAndVersion) {

		String content = null;
		String tempStr = null;
		
		JSONObject object;

		File file = new File(foldPath + fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
				
				object = new JSONObject();
				object.put("time", time);
				object.put("userId", userId);
				object.put("doorId", doorId);
				object.put("model", modelNameAndVersion);
				object.put("result", String.valueOf(successOrNot));
				object.toString();
				
				content = object.toString().replace("null", "");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;

					while ((line = buffreader.readLine()) != null) {

						tempStr += line + "\r\n";

					}
					instream.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			object = new JSONObject();
			
			try {
				object.put("TIME", time);
				object.put("UserID", userId);
				object.put("DoorID", doorId);
				object.put("MODEL", modelNameAndVersion);
				object.put("RESULT", String.valueOf(successOrNot));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			object.toString();
			
			content = (tempStr + object.toString()).replace("null", "");
		}

		OutputStream outstream;
		try {
			outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(content);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public void upLoadInfo(String sid, RequestQueue mQueue) {
		MyDebugLog.e(TAG, "upLoadInfo");
		final File file = new File(foldPath + fileName);
		if (file.exists()) {
			final HttpParams mParams = new HttpParams();
			mParams.add("type", "1");
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;

					while ((line = buffreader.readLine()) != null) {
						mParams.add("datas", line);
					}
					instream.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			URL upLoadURL = null;
			try {
				upLoadURL = new URL(UrlUtils.HOST + "/user/stat/add.do" + "?sid=" + sid + "&ver=" + version.getVersionName());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(
					Method.POST, upLoadURL.toString(), null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							MyDebugLog.e(TAG, "test upload: " + response.toString());
							file.delete();
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

						}
					}) {
				@Override
				public byte[] getBody() throws AuthFailureError {
					if (mParams != null && mParams.size() > 0) {
						return mParams.encodeParameters(getParamsEncoding());
					}
					return null;
				}
			};
			mQueue.add(mJsonRequest);
		} else {
			MyDebugLog.e(TAG, "no file to upload");
		}
	}
}
