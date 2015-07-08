package com.icloudoor.cloudoor;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class FragmentManEntrance extends Fragment implements OnClickListener{
	private RelativeLayout call_contact;
	private static final int REQUEST_CONTACT = 1;
	private final String mPageName = "FragmentManEntrance";
	private SharedPreferences dateAndPhoneShare;
	private Editor dateAndPhoneEditor;
	
	private SharedPreferences zoneIdShare;
	private String phonenum;
	final Calendar c = Calendar.getInstance();
	int mYear = c.get(Calendar.YEAR);
	int mMonth = c.get(Calendar.MONTH);
	int mDay = c.get(Calendar.DAY_OF_MONTH);
	private RelativeLayout call_datepicker;
	private EditText phoneEdit;
	//
	private boolean havePhone = false;
	
	private TextView date_show;
	//
	private boolean haveDate = false;
	
	SharedPreferences submitStatus;
	Editor editor;
	
	boolean isDebug = DEBUG.isDebug;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_man_entrance, container, false);
		
		//
		submitStatus =  getActivity().getSharedPreferences("SUBMITSTATUS", 0);
		editor = submitStatus.edit();
		
		dateAndPhoneShare=getActivity().getSharedPreferences("DATEANDPHONESHARE", 0);
		dateAndPhoneEditor=dateAndPhoneShare.edit();
		zoneIdShare=getActivity().getSharedPreferences("ZONESHARE", 0);
		call_contact=(RelativeLayout) view.findViewById(R.id.id_call_contacts);
		call_contact.setOnClickListener(this);
		phoneEdit=(EditText) view.findViewById(R.id.id_manentrance_phonenum);
		call_datepicker=(RelativeLayout) view.findViewById(R.id.icon_dateshow);
		call_datepicker.setOnClickListener(this);
		date_show=(TextView) view.findViewById(R.id.date_show_textview);
		String str=mYear+"/"+(mMonth+1)+"/"+mDay;
		date_show.setText(str + " " + getString(R.string.only_one_day));
		
		phoneEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				phoneEdit.setTextColor(0xff333333);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String temp = s.toString();
				if(temp.length() > 1){
					String tem = temp.substring(temp.length()-1, temp.length());
					char[] temC = tem.toCharArray();
					int mid = temC[0];
					
					if(mid>=48 && mid<=57){
						
					}else{
						s.delete(temp.length()-1, temp.length());
					}
				}else if(temp.length() == 1){
					char[] temC = temp.toCharArray();
					int mid = temC[0];
					
					if(mid>=48 && mid<=57){
						
					}else{
						s.clear();
					}
				}
				
				dateAndPhoneEditor.putString("PHONENUM", phoneEdit.getText().toString()).commit();
				phoneEdit.setTextColor(0xff333333);
				
				//TODO
				if(phoneEdit.getText().toString().length() > 10){
					havePhone = true;
					editor.putBoolean("ManPhone", havePhone);
					editor.commit();
				}
			}
		});

		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.id_call_contacts)
		{
			phoneEdit.setText("");
			
			Intent intent = new Intent();

			intent.setAction(Intent.ACTION_PICK);

			intent.setData(ContactsContract.Contacts.CONTENT_URI);

			getParentFragment().startActivityForResult(intent, REQUEST_CONTACT);
		}
		if(v.getId()==R.id.icon_dateshow)
		{
		
		  DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {  
		        public void onDateSet(DatePicker view, int year, int monthOfYear,  
		                int dayOfMonth) {
		            String s=year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
		            dateAndPhoneEditor.putString("DATE", year+"-"+(monthOfYear+1)+"-"+dayOfMonth).commit();
		            date_show.setText(s + " " + getString(R.string.only_one_day));
		            date_show.setTextColor(0xff333333);
		        }  
		    };  
		    new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,onDateSetListener, mYear	, mMonth	, mDay).show();
			
		    //TODO
			if(date_show.length() > 0){
				haveDate = true;
				editor.putBoolean("ManDate", haveDate);
				editor.commit();
			}
		}
		
	}
	
	public void getData(String phonenum) {
		MyDebugLog.e("phone", phonenum);

		phoneEdit.setText(phonenum.replace(" ", "").replace("-", "").replace("+86", ""));

		dateAndPhoneEditor.putString("PHONENUM", phonenum).commit();

		phoneEdit.setTextColor(0xff333333);
	}
}
