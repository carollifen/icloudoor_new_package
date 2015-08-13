package com.icloudoor.cloudoor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WuYeDialog extends Dialog implements android.view.View.OnClickListener {
	
	
        public interface WuYeDialogCallBack{
                public void back();
        }
	
	 private String name;
     private WuYeDialogCallBack wuYeDialogCallBack;
 
     private Button boundTV;
   private  Context context;
     public WuYeDialog (Context context,int theme,String name,WuYeDialogCallBack wuYeDialogCallBack) {
             super(context,theme);
             this.name = name;
             this.context=context;
             this.wuYeDialogCallBack  = wuYeDialogCallBack ;
            
     }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wuyedialog_layout);
		boundTV=(Button) findViewById(R.id.id_queding);
		boundTV.setOnClickListener(this);
		
		
	}
	@Override
	public void onClick(View v) {
		wuYeDialogCallBack.back();
		WuYeDialog.this.dismiss();
		
	}
     
     

}
