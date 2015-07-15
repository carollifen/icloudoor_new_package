package com.icloudoor.cloudoor.widget;

import com.icloudoor.cloudoor.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ShareRedDialog extends Dialog {
	
	LinearLayout weixin_layout;
	LinearLayout weixin_circle_layout;

	public ShareRedDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ShareRedDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sharered);
		weixin_layout = (LinearLayout) findViewById(R.id.weixin_layout);
		weixin_circle_layout = (LinearLayout) findViewById(R.id.weixin_circle_layout);
	}

	// …Ë÷√¥∞ø⁄œ‘ æ
	public void windowDeploy() {
		Window window;
		window = getWindow();
		window.setWindowAnimations(R.style.dialogWindowAnim);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.gravity = Gravity.BOTTOM;
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;
		window.setAttributes(wl);
	}

	public void setClickListener(android.view.View.OnClickListener clickListener) {
		weixin_layout.setOnClickListener(clickListener);
		weixin_circle_layout.setOnClickListener(clickListener);
	}
	
	public interface OnDismissListener{
		public void onDismiss();
	}
	OnDismissListener dismissListener;
	public void setOnDismiss(OnDismissListener dismissListener){
		this.dismissListener = dismissListener;
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		dismissListener.onDismiss();
	}
}
