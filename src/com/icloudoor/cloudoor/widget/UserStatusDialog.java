package com.icloudoor.cloudoor.widget;

import com.icloudoor.cloudoor.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserStatusDialog extends Dialog{
	
	Button cancel_bnt;
	Button ok_bnt;

	public UserStatusDialog(Context context) {
		super(context , R.style.card_dialog);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_userstatus);
		cancel_bnt = (Button) findViewById(R.id.cancel_bnt);
		ok_bnt = (Button) findViewById(R.id.ok_bnt);
		cancel_bnt.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void setOKOnClickListener(android.view.View.OnClickListener onClickListener ){
		ok_bnt.setOnClickListener((android.view.View.OnClickListener) onClickListener);
	}
}
