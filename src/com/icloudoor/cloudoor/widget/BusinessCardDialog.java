package com.icloudoor.cloudoor.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;

public class BusinessCardDialog extends Dialog implements OnClickListener{
	
	
	Button cancel_bnt;
	Button ok_bnt;
	TextView msg_tx;
	Context context;

	public BusinessCardDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public BusinessCardDialog(Context context , int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_business_crad);
		cancel_bnt = (Button) findViewById(R.id.cancel_bnt);
		cancel_bnt.setOnClickListener(this);
		ok_bnt = (Button) findViewById(R.id.ok_bnt);
		msg_tx = (TextView) findViewById(R.id.msg_tx);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
	}
	public void setOKOnClickListener(android.view.View.OnClickListener onClickListener ){
		ok_bnt.setOnClickListener((android.view.View.OnClickListener) onClickListener);
	}
	public void setMSGText(String name){
		String msg1 = context.getString(R.string.msg_tx1);
		String msg2 = context.getString(R.string.msg_tx2);
		msg_tx.setText(msg1+name+msg2);
	}

}
