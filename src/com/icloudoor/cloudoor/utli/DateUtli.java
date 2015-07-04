package com.icloudoor.cloudoor.utli;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.icloudoor.cloudoor.R;

public class DateUtli {

	/**
	 * 
	 * @param time style yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getTime(long time,Context context){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date(time));
		long newtime = System.currentTimeMillis();
		long difference=newtime-time;
		
		long ss = difference/1000;
		if(ss<=60){
			return 1+context.getString(R.string.time);
		}else if(ss>60&&ss<=3600){
			return (ss/60)+context.getString(R.string.time);
		}else if(ss>3600&&ss<=86400){
			return (ss/3600)+context.getString(R.string.time1);
		}else{
			return (ss/86400)+context.getString(R.string.time2);
		}
		
	}
	
	
}
