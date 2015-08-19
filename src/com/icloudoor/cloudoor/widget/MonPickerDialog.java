package com.icloudoor.cloudoor.widget;

import com.icloudoor.cloudoor.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class MonPickerDialog extends DatePickerDialog {  
	
    public MonPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {  
        super(context,AlertDialog.THEME_HOLO_LIGHT, callBack, year, monthOfYear, dayOfMonth);  
        this.setTitle(year + context.getString(R.string.year) + (monthOfYear + 1) + context.getString(R.string.month));  
          
        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);  
    }  
  
    @Override  
    public void onDateChanged(DatePicker view, int year, int month, int day) {  
        super.onDateChanged(view, year, month, day);  
        this.setTitle(year + getContext().getString(R.string.year) + (month + 1) + getContext().getString(R.string.month));  
    }  
  
}  