package com.icloudoor.cloudoor.chat.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;

import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Entities.FilePart;
import com.icloudoor.cloudoor.Entities.MultipartEntity;
import com.icloudoor.cloudoor.Entities.Part;
import com.icloudoor.cloudoor.Entities.StringPart;

public class SendDynamicActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_send_dynamic);
		
	}
	
	
	
	
	
	
	
	
//	class MyAsyncTask extends AsyncTask<String, Integer, String> {
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//
//			Log.e(TAG, "’‚ «≤‚ ‘≤‚ ‘");
//			StringBuilder sb = new StringBuilder();
//			sb.append("javascript:").append(callback).append('(').append(0)
//					.append(",").append("'").append(upphotoUrl).append("');");
//			fixwebview.loadUrl(sb.toString());
//			Log.e("string dsklfjkl", sb.toString());
//			upcode = "0";
//
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			// TODO Auto-generated method stub
//			super.onProgressUpdate(values);
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost postRequest = new HttpPost(params[0]);
//
//			File file = null;
//			if (URLUtil.isFileUrl(mList.get(0).getAbsolutePath())) {
//				file = new File(URI.create(mList.get(0).getAbsolutePath()));
//			} else {
//				file = new File(mList.get(0).getAbsolutePath());
//			}
//
//			// MultipartEntity myMul=new MultipartEntity();
//
//			Part[] parts = null;
//			FilePart photoPart;
//			try {
//				photoPart = new FilePart("file", file);
//				StringPart policyPart = new StringPart("policy", upPolicy);
//				StringPart signaturePart = new StringPart("signature",
//						upSignature);
//
//				parts = new Part[] { policyPart, signaturePart, photoPart };
//
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//			postRequest.setEntity(new MultipartEntity(parts));
//			HttpResponse response;
//
//			// try {
//			try {
//				response = httpClient.execute(postRequest);
//
//				String jsonString = EntityUtils.toString(response.getEntity());
//				fromUPjson = new JSONObject(jsonString);
//				postToServer = fromUPjson.getString("url");
//				upcode = fromUPjson.getString("code");
//
//				Log.e(TAG, upcode);
//				Log.e("TEst StringBuilder", postToServer);
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//	}
	
}
