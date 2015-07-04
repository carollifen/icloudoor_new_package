package com.icloudoor.cloudoor.chat.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.RoomListAdapter;
import com.icloudoor.cloudoor.chat.entity.ChatRoomInfo;
import com.icloudoor.cloudoor.chat.entity.ChatRoomList;
import com.icloudoor.cloudoor.utli.GsonUtli;

public class RoomListActivity extends BaseActivity implements NetworkInterface , OnClickListener{
	
	
	private ImageView btn_back;
	private ListView room_listView;
	private RoomListAdapter adapter;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_room_list);
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
		room_listView = (ListView) findViewById(R.id.room_listView);
		adapter = new RoomListAdapter(this);
		room_listView.setAdapter(adapter);
		btn_back.setOnClickListener(this);
//		getNetworkData(this, "/user/im/getChatrooms.do", null);
//		room_listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				ChatRoomList roomList = (ChatRoomList) adapter.getItem(position);
//				Intent intent = new Intent(RoomListActivity.this, ChatActivity.class);
//				intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
//				intent.putExtra("groupId", roomList.getChatroomId());
//				startActivity(intent);
//			}
//		});
		
		
	}

	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		ChatRoomInfo info = GsonUtli.jsonToObject(response.toString(), ChatRoomInfo.class);
		if(info!=null){
			adapter.setData(info.getData());
		}
	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
			
		default:
			break;
		}
		
	}

}
