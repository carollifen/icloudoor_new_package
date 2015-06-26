package com.icloudoor.cloudoor.chat.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.MyJsonObjectRequest;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Interface.NetworkInterface;
import com.icloudoor.cloudoor.adapter.MyFriendsAdapter;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.utli.CharacterParser;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.PinyinComparator;
import com.icloudoor.cloudoor.widget.SideBar;
import com.icloudoor.cloudoor.widget.SideBar.OnTouchingLetterChangedListener;

public class ContactActivity extends BaseActivity implements OnClickListener,
		NetworkInterface {

	private RequestQueue mQueue;

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private MyFriendsAdapter adapter;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	LayoutInflater inflater;
	private ImageView btn_back;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_contact);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.friend_header_view, null);
		v.findViewById(R.id.add_friend).setOnClickListener(this);
		btn_back.setOnClickListener(this);

		sortListView.addHeaderView(v);
		adapter = new MyFriendsAdapter(this, null);
		sortListView.setAdapter(adapter);
		characterParser = new CharacterParser();
		setListener();
		mQueue = Volley.newRequestQueue(this);
		getNetworkData(this, "/user/im/getFriends.do", null);
	}

	public void setListener() {

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});
	}

	public void getMyFriends() { 

		String url = UrlUtils.HOST + "/user/im/listFriends.do" + "?sid="
				+ loadSid();

		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.POST,
				url, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
				});
		mQueue.add(mJsonRequest);
	}

	private List<MyFriendsEn> filledData(List<MyFriendsEn> data) {
		List<MyFriendsEn> mSortList = new ArrayList<MyFriendsEn>();

		for (int i = 0; i < data.size(); i++) {
			MyFriendsEn sortModel = data.get(i);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(data.get(i).getNickname());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_friend:
			Intent intent = new Intent(this, VerificationFrientsActivity.class);
			startActivity(intent);
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
		MyFriendInfo friendInfo = GsonUtli.jsonToObject(response.toString(),
				MyFriendInfo.class);
		if (friendInfo != null) {
			List<MyFriendsEn> data = friendInfo.getData();
			System.out.println(data.size()+" = ***");
			adapter.updateListView(filledData(data));
		} else {
			showToast(R.string.jsonerror);
		}

	}

	@Override
	public void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

}
