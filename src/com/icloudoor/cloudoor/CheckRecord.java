package com.icloudoor.cloudoor;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.Entities.Signs;
import com.icloudoor.cloudoor.Entities.SignsEn;
import com.icloudoor.cloudoor.Entities.SignsInfo;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.widget.MonPickerDialog;

public class CheckRecord extends BaseActivity implements NetworkInterface,
		OnClickListener {

	private String TAG = this.getClass().getSimpleName();

	private RelativeLayout back;
	private RelativeLayout work_date_pick;

	private String sid;
	private String HOST = UrlUtils.HOST;
	private URL getTimeUrl;
	private RequestQueue mQueue;
	ListView work_time;
	TextView time_day;
	MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_record);
		back = (RelativeLayout) findViewById(R.id.btn_back);
		work_date_pick = (RelativeLayout) findViewById(R.id.work_date_pick);
		work_date_pick.setOnClickListener(this);
		work_time = (ListView) findViewById(R.id.work_time);
		time_day = (TextView) findViewById(R.id.time_day);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		time_day.setText(format.format(getFirstDayOfMonth(new Date(System
					.currentTimeMillis()))));
		adapter = new MyAdapter();
		work_time.setAdapter(adapter);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckRecord.this.finish();
			}

		});
		getcheckdata(time_day.getText().toString());
	}

	private void getcheckdata(String time) {

		 SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		 JSONObject josn = new JSONObject();
		 try {
			Date date = format.parse(time+"-01");
			josn.put("from", format.format(getFirstDayOfMonth(date)));
			josn.put("to", format.format(getLastDayOfMonth(date)));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getNetworkData(this, "/user/api/office/getSigns.do", josn.toString(),
				true);

	}

	public Date getFirstDayOfMonth(Date date) {

		Calendar queryCal = Calendar.getInstance();
		queryCal.setTime(date);
		int year = queryCal.get(Calendar.YEAR);
		int month = queryCal.get(Calendar.MONTH);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	public int getDay(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (date == null) {
			return 0;
		}
		Calendar queryCal = Calendar.getInstance();
		queryCal.setTime(date);
		int day = queryCal.get(Calendar.DAY_OF_MONTH);
		return day;

	}

	/**
	 * »ñÈ¡Ä³¸öÔÂµÄ×îºóÒ»Ìì
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public Date getLastDayOfMonth(Date date) {

		Calendar queryCal = Calendar.getInstance();
		queryCal.setTime(date);
		queryCal.set(Calendar.DAY_OF_MONTH,
				queryCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return queryCal.getTime();
	}

	public void saveSid(String sid) {
		SharedPreferences savedSid = getSharedPreferences("SAVEDSID",
				MODE_PRIVATE);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	
	public int getDayOfWeek(String time) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (date == null) {
			return 0;
		}
		Calendar queryCal = Calendar.getInstance();
		queryCal.setTime(date);
		int day = queryCal.get(Calendar.DAY_OF_WEEK);
		return day;

	}

	class MyAdapter extends BaseAdapter {
		List<Signs> signs;
		List<String> earlies;
		List<String> lates;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (signs == null)
				return 0;
			return signs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return signs.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void setData(SignsInfo signsInfo) {
			signs = signsInfo.getSigns();
			earlies = signsInfo.getEarlies();
			lates = signsInfo.getLates();
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHodler hodler;

			if (convertView == null) {
				hodler = new ViewHodler();
				convertView = LayoutInflater.from(CheckRecord.this).inflate(
						R.layout.item_work_time, null);
				hodler.day_tx = (TextView) convertView
						.findViewById(R.id.day_tx);
				hodler.week_tx = (TextView) convertView
						.findViewById(R.id.week_tx);
				hodler.to_work_tx = (TextView) convertView
						.findViewById(R.id.to_work_tx);
				hodler.off_work_tk = (TextView) convertView
						.findViewById(R.id.off_work_tk);
				convertView.setTag(hodler);
			} else {
				hodler = (ViewHodler) convertView.getTag();
			}
			Signs signInfo = signs.get(position);
			hodler.day_tx.setText(getDay(signInfo.getDate()) + "");
			int week = getDayOfWeek(signInfo.getDate());
			switch (week) {
			case 2:
				hodler.week_tx.setText(R.string.week1);
				break;
			case 3:
				hodler.week_tx.setText(R.string.week2);
				break;
			case 4:
				hodler.week_tx.setText(R.string.week3);
				break;
			case 5:
				hodler.week_tx.setText(R.string.week4);
				break;
			case 6:
				hodler.week_tx.setText(R.string.week5);
				break;
			case 7:
				hodler.week_tx.setText(R.string.week6);
				break;
			case 1:
				hodler.week_tx.setText(R.string.week7);
				break;
			default:
				hodler.week_tx.setText("");
				break;
			}
			String off = signInfo.getOff();
			if (!TextUtils.isEmpty(off)) {
				hodler.off_work_tk.setText(off);
			} else {
				hodler.off_work_tk.setText("");
			}
			String on = signInfo.getOn();
			if (!TextUtils.isEmpty(on)) {
				hodler.to_work_tx.setText(on);
			} else {
				hodler.to_work_tx.setText("");
			}
			for (int i = 0; i < earlies.size(); i++) {
				if(signInfo.getDate().equals(earlies.get(i))){
					hodler.off_work_tk.setTextColor(Color.parseColor("#ff608d"));
				}else{
					hodler.off_work_tk.setTextColor(Color.parseColor("#33acf5"));
				}
			}
			for (int i = 0; i < lates.size(); i++) {
				if(signInfo.getDate().equals(lates.get(i))){
					hodler.to_work_tx.setTextColor(Color.parseColor("#ff608d"));
				}else{
					hodler.to_work_tx.setTextColor(Color.parseColor("#33acf5"));
				}
			}

			return convertView;
		}
	}

	class ViewHodler {
		TextView day_tx;
		TextView week_tx;
		TextView to_work_tx;
		TextView off_work_tk;
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		SignsEn signsEn = GsonUtli.jsonToObject(response.toString(),
				SignsEn.class);
		if (signsEn != null) {
			if (signsEn.getCode() == 1) {
				adapter.setData(signsEn.getData());
			}
		}
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	int mYear;
	int mMonth;
	int mDay;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.work_date_pick:
			String time = time_day.getText().toString().trim();
			String[] dates = time.split("-");
			mYear = Integer.parseInt(dates[0]);
			mMonth = Integer.parseInt(dates[1]);
			DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					String s;
					if(monthOfYear<9){
						s = year + "-0" + (monthOfYear + 1);
					}else{
						
						s = year + "-" + (monthOfYear + 1);
					}
					time_day.setText(s);
					getcheckdata(s);
				}

			};
			new MonPickerDialog(this,
					onDateSetListener, mYear, mMonth-1, 1).show();
			break;

		default:
			break;
		}
	}

}
