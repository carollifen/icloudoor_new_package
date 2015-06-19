package com.icloudoor.cloudoor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParams extends HashMap<String, List<String>> {
	private static final long serialVersionUID = 1L;

    public HttpParams() {
        super();
    }

    public HttpParams(int capacity) {
        super(capacity);
    }

    public HttpParams(Map<String, List<String>> map) {
        super(map);
    }

    public HttpParams(int capacity, float loadFactor) {
        super(capacity, loadFactor);
    }

    /*
     * This is the method to use for adding post parameters
     */
    public void add(String key, String value) {
        if (containsKey(key)) {
            get(key).add(value);
        }
        else {
            ArrayList<String> list = new ArrayList<String>();
            list.add(value);
            put(key, list);
        }
    }

    /**
     * Converts the Map into an application/x-www-form-urlencoded encoded string.
     */
    public byte[] encodeParameters(String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, List<String>> entry : entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), paramsEncoding);
                for (String value : entry.getValue()) {
                    encodedParams.append(key);
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(value, paramsEncoding));
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding,     uee);
        }
    }

}
