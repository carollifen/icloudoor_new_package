package com.icloudoor.cloudoor.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;

public class MyProgressDialog extends Dialog {
//	private TextView tvMsg;
	ImageView animationIV;

	public MyProgressDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public MyProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Window window = getWindow();
		WindowManager m = window.getWindowManager();
		Display d = m.getDefaultDisplay(); 
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.height = (int) (d.getHeight() * 0.25);
		p.width = (int) (d.getWidth() * 0.25); 
		this.getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) p);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u_loading);
//		tvMsg = (TextView) findViewById(R.id.tv_message);
		animationIV = (ImageView) findViewById(R.id.animationIV);
	}

//	public void setMessage(String msg) {
//		tvMsg.setText(msg);
//		
//
//	}

	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			Animation animationDrawable=AnimationUtils.loadAnimation(getContext(),R.anim.animation_doctor_walking);
			LinearInterpolator lir = new LinearInterpolator();  
			animationDrawable.setInterpolator(lir);
			animationIV.startAnimation(animationDrawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
