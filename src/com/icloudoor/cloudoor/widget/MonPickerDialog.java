package com.icloudoor.cloudoor.widget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class MonPickerDialog extends DatePickerDialog {  
	
    public MonPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {  
        super(context,AlertDialog.THEME_HOLO_LIGHT, callBack, year, monthOfYear, dayOfMonth);  
        this.setTitle(year + "年" + (monthOfYear + 1) + "月");  
          
        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);  
    }  
  
    @Override  
    public void onDateChanged(DatePicker view, int year, int month, int day) {  
        super.onDateChanged(view, year, month, day);  
        this.setTitle(year + "年" + (month + 1) + "月");  
    }  
  
}  