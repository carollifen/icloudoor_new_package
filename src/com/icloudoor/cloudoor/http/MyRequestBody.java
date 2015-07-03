package com.icloudoor.cloudoor.http;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class MyRequestBody extends JsonRequest<JSONObject> {
	
	Listener<JSONObject> listener;
	ErrorListener errorListener;
	public MyRequestBody(String url, String requestBody,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.POST, url, requestBody, listener, errorListener);
		// TODO Auto-generated constructor stub
		this.listener = listener;
		this.errorListener = errorListener;
	}
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		 try {
	            String jsonString = new String(response.data,
	                    HttpHeaderParser.parseCharset(response.headers));
	            return Response.success(new JSONObject(jsonString),
	                    HttpHeaderParser.parseCacheHeaders(response));
	        } catch (UnsupportedEncodingException e) {
	            return Response.error(new ParseError(e));
	        } catch (JSONException je) {
	            return Response.error(new ParseError(je));
	        }
	}
	@Override
	public void deliverError(VolleyError error) {
		// TODO Auto-generated method stub
		super.deliverError(error);
		errorListener.onErrorResponse(error);
	}
	@Override
	protected void deliverResponse(JSONObject response) {
		// TODO Auto-generated method stub
		super.deliverResponse(response);
		listener.onResponse(response);
	}

}
