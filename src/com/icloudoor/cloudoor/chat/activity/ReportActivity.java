package com.icloudoor.cloudoor.chat.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;

public class ReportActivity extends BaseActivity implements OnClickListener,
		NetworkInterface {

	private ImageView btn_back;
	private TextView submit;
	private RadioGroup radioGroup1;
	private int type;
	private String trgUserId;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_report);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		submit = (TextView) findViewById(R.id.submit);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		trgUserId = getIntent().getExtras().getString("trgUserId");
		submit.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (group.getCheckedRadioButtonId()) {
				case R.id.radio_1:

					type = 1;

					break;

				case R.id.radio_2:
					type = 2;
					break;

				case R.id.radio_3:
					type = 3;
					break;

				case R.id.radio_4:
					type = 4;
					break;

				case R.id.radio_5:
					type = 5;
					break;

				case R.id.radio_6:
					type = 6;
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submit:
			Map<String, String> map = new HashMap<String, String>();
			map.put("trgUserId", trgUserId);
			map.put("type", type+"");
			getNetworkData(this, "/user/im/complain.do", map);
			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			int code = response.getInt("code");
			if(code==1){
				finish();
				showToast(R.string.reportsuccess);
			}else{
				showToast(R.string.reportfail);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

}
