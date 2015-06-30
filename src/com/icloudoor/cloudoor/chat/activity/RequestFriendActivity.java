package com.icloudoor.cloudoor.chat.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;

public class RequestFriendActivity extends BaseActivity implements OnClickListener,NetworkInterface{
	
	TextView right_send;
	EditText msg_edit;
	ImageView delete_msg;
	ImageView btn_back;
	String trgUserId;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_requestfriend);
		trgUserId = getIntent().getExtras().getString("trgUserId", "");
		right_send = (TextView) findViewById(R.id.right_send);
		delete_msg = (ImageView) findViewById(R.id.delete_msg);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		msg_edit = (EditText) findViewById(R.id.msg_edit);
		msg_edit.setSelection(2);
		right_send.setOnClickListener(this);
		delete_msg.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.right_send:
			Map<String, String> map = new HashMap<String, String>();
			map.put("trgUserId", trgUserId);
			String comment = msg_edit.getText().toString();
			if(TextUtils.isEmpty(comment)){
				map.put("comment", "");
			}else{
				map.put("comment", comment);
			}
			
			getNetworkData(this, "/user/im/invite.do", map);
			break;
		case R.id.delete_msg:
			msg_edit.setText("");
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
			Toast.makeText(this, response.getString("code"), Toast.LENGTH_LONG).show();
			
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
