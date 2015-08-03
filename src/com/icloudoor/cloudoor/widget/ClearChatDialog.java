package com.icloudoor.cloudoor.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;

public class ClearChatDialog extends Dialog{

	private Window window = null;
	TextView report_tx;
	TextView cancel;
	public ClearChatDialog(Context context) {
		super(context,R.style.card_dialog);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_clearchat);
		report_tx = (TextView) findViewById(R.id.report_tx);
		cancel = (TextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
			
		});
		windowDeploy();
	}
	
	public void windowDeploy(){  
        window = getWindow(); //得到对话框  
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画  
        window.setBackgroundDrawableResource(android.R.color.transparent); //设置对话框背景为透明  
        WindowManager.LayoutParams wl = window.getAttributes();  
        wl.gravity = Gravity.BOTTOM;
        wl.width = LayoutParams.MATCH_PARENT;
        window.setAttributes(wl);  
    }  
	
	public void setOnlick(android.view.View.OnClickListener clickListener){
		report_tx.setOnClickListener(clickListener);
	}
}
