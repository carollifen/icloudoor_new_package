package com.icloudoor.cloudoor.Interface;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface NetworkInterface {
	public void onSuccess(JSONObject response);
	public void onFailure(VolleyError error);

}
