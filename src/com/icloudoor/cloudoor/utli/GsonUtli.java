package com.icloudoor.cloudoor.utli;

import com.google.gson.Gson;

public class GsonUtli {
	
	
	public static <T> T jsonToObject(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }

}
