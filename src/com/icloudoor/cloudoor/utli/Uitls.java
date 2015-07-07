package com.icloudoor.cloudoor.utli;

import android.content.Context;
import android.view.WindowManager;

/**
 * @author wanggang
 * 
 */
public class Uitls {

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int[] getWH(Context context) {
		
		int[] wh = new int[2];
		WindowManager wm = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		wh[0]=width;
		wh[1]=height;
		return wh;
	}

}
