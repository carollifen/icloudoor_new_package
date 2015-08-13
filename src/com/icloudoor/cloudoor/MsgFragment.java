package com.icloudoor.cloudoor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.util.NetUtils;
import com.icloudoor.cloudoor.SlideView.OnSlideListener;
import com.icloudoor.cloudoor.adapter.ChatAllHistoryAdapter1;
import com.icloudoor.cloudoor.chat.CommonUtils;
import com.icloudoor.cloudoor.chat.activity.ChatActivity;
import com.icloudoor.cloudoor.chat.activity.ContactActivity;
import com.icloudoor.cloudoor.chat.activity.MipcaActivityCapture;
import com.icloudoor.cloudoor.chat.activity.VerificationFrientsActivity;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.utli.VFDaoImpl;
import com.icloudoor.cloudoor.widget.DeleteChatDialog;
import com.umeng.analytics.MobclickAgent;

public class MsgFragment extends Fragment implements OnItemClickListener,
		OnClickListener, OnSlideListener {
	private final String mPageName = "MsgFragment";
	private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();

	private TextView mPopupWindow;
	public TextView errorItem;
	ListViewForScrollView msg_list;
	private ChatAllHistoryAdapter1 adapter;
	private RelativeLayout group_layout;
	private List<EMConversation> conversationList;
	private ImageView add_friends;
	private ImageView push_current;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	CloudDoorMainActivity activity;

	public MsgFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.msg_page, container, false);
		activity = (CloudDoorMainActivity) getActivity();
		conversationList = new ArrayList<EMConversation>();
		conversationList.addAll(loadConversationsWithRecentChat());
		msg_list = (ListViewForScrollView) view.findViewById(R.id.msg_list);
		group_layout = (RelativeLayout) view.findViewById(R.id.group_layout);
		add_friends = (ImageView) view.findViewById(R.id.add_friends);
		push_current = (ImageView) view.findViewById(R.id.push_current);
		errorItem = (TextView) view.findViewById(R.id.errorItem);
		adapter = new ChatAllHistoryAdapter1(getActivity());
		// ÉèÖÃadapter
		msg_list.setAdapter(adapter);
		adapter.setData(conversationList);
		msg_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EMConversation conversation = (EMConversation) adapter
						.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(cloudApplication.getInstance()
						.getUserName()))
					Toast.makeText(getActivity(), "sssssssssssss", 0).show();
				else {
					// ½øÈëÁÄÌìÒ³Ãæ
					Intent intent = new Intent(getActivity(),
							ChatActivity.class);
					if (conversation.isGroup()) {
						if (conversation.getType() == EMConversationType.ChatRoom) {
							// it is group chat
							intent.putExtra("chatType",
									ChatActivity.CHATTYPE_CHATROOM);
							intent.putExtra("groupId", username);
						} else {
							// it is group chat
							intent.putExtra("chatType",
									ChatActivity.CHATTYPE_GROUP);
							intent.putExtra("groupId", username);
						}

					} else {
						// it is single chat
						intent.putExtra("userId", username);
					}
					startActivity(intent);
				}
			}
		});
		
		
		msg_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				
				final DeleteChatDialog chatDialog = new DeleteChatDialog(getActivity());
				chatDialog.show();
				chatDialog.setOKOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EMConversation conversation = (EMConversation) adapter.getItem(position);
						EMChatManager.getInstance().clearConversation(conversation.getUserName());
						refresh();
						chatDialog.dismiss();
					}
				});
				
				return true;
			}
			
			
			
		});
		
		
		
		group_layout.setOnClickListener(this);
		add_friends.setOnClickListener(this);
		registerBoradcastReceiver();
		registerRemoveFriendBoradcastReceiver();
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		activity = (CloudDoorMainActivity) activity;
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"LOGINSTATUS", Context.MODE_PRIVATE);
		currentUsername = preferences.getString("IMUSERID", "");
		currentPassword = preferences.getString("IMPASSWORD", "");
		if (TextUtils.isEmpty(currentUsername)
				|| TextUtils.isEmpty(currentPassword)) {
		} else {
			loginIM();
		}
	}

	public void refresh() {
		if (adapter != null) {
			adapter.setData(loadConversationsWithRecentChat());
		}
	}

	String currentUsername;
	String currentPassword;

	public void loginIM() {
		if (!CommonUtils.isNetWorkConnected(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!EMChatManager.getInstance().isConnected()) {
			System.out.println("currentUsername  = " + currentUsername);
			System.out.println("currentPassword  = " + currentPassword);
			EMChatManager.getInstance().login(currentUsername, currentPassword,
					new EMCallBack() {

						@Override
						public void onSuccess() {
							System.out.println("IM________");
							cloudApplication.getInstance().setUserName(
									currentUsername);
							cloudApplication.getInstance().setPassword(
									currentPassword);
						}

						@Override
						public void onProgress(int progress, String status) {
						}

						@Override
						public void onError(final int code, final String message) {
							System.out.println("IM___**____");
							if(getActivity()!=null){
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										activity.showToast(R.string.Login_failed);
										System.out.println("code = "+code +"   message = "+message);
									}
								});
							}
						}
					});
		}

	}

	boolean flag = false;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flag = false;
		MobclickAgent.onPageStart(mPageName);
		VFDaoImpl daoImpl = new VFDaoImpl(getActivity());
		List<VerificationFrientsList> data = daoImpl.find();
		for (int i = 0; i < data.size(); i++) {
			VerificationFrientsList frientsList = data.get(i);
			if(frientsList.getStatus().equals("0")){
				flag = true;
				break;
			}
		}
		if(flag){
			push_current.setVisibility(View.VISIBLE);
		}else{
			push_current.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	private List<EMConversation> loadConversationsWithRecentChat() {

		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					// if(conversation.getType() !=
					// EMConversationType.ChatRoom){
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
					// }
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}

				});
	}

	private class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = getLayoutInflater(null);
		}

		@Override
		public int getCount() {
			return mMessageItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			if (slideView == null) {
				View itemView = mInflater.inflate(R.layout.msg_list_item, null);

				slideView = new SlideView(getActivity().getApplicationContext());
				slideView.setContentView(itemView);

				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(MsgFragment.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}
			MessageItem item = mMessageItems.get(position);
			item.slideView = slideView;
			item.slideView.shrink();

			holder.image.setImageResource(item.image);
			holder.name.setText(item.name);
			holder.content.setText(item.content);
			holder.time.setText(item.time);
			holder.deleteHolder.setOnClickListener(MsgFragment.this);

			return slideView;
		}

	}

	public class MessageItem {
		public int image;
		public String name;
		public String content;
		public String time;
		public SlideView slideView;
	}

	private static class ViewHolder {
		public ImageView image;
		public TextView name;
		public TextView content;
		public TextView time;
		public ViewGroup deleteHolder;
		ViewHolder(View view) {
			image = (ImageView) view.findViewById(R.id.msg_image);
			name = (TextView) view.findViewById(R.id.msg_name);
			content = (TextView) view.findViewById(R.id.msg_content);
			time = (TextView) view.findViewById(R.id.msg_time);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.holder:
			Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
			break;
		case R.id.group_layout:
//			Intent intent = new Intent(getActivity(), DynamicActivity.class);
//			startActivity(intent);
			break;

		case R.id.add_friends:
			initPopupWindow();

			break;
		case R.id.sweep_layout:
			if (pw != null && pw.isShowing()) {
				pw.dismiss();
			}
			Intent sweepIntent = new Intent();
			sweepIntent.setClass(getActivity(), MipcaActivityCapture.class);
			sweepIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(sweepIntent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.add_friend_layout:
			if (pw != null && pw.isShowing()) {
				pw.dismiss();
			}
			Intent VerificationIntent = new Intent(getActivity(),
					VerificationFrientsActivity.class);
			startActivity(VerificationIntent);

			break;
		case R.id.constat_layout:
			if (pw != null && pw.isShowing()) {
				pw.dismiss();
			}
			Intent contactIntent = new Intent(getActivity(),
					ContactActivity.class);
			contactIntent.putExtra("type", 0);
			startActivity(contactIntent);
			break;

		default:
			break;
		}
	}

	PopupWindow pw;

	public void initPopupWindow() {
		if (pw == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.pw_addfriend, null);
			view.findViewById(R.id.sweep_layout).setOnClickListener(this);
			view.findViewById(R.id.add_friend_layout).setOnClickListener(this);
			view.findViewById(R.id.constat_layout).setOnClickListener(this);
			pw = new PopupWindow(view);
			pw.setHeight(LayoutParams.WRAP_CONTENT);
			pw.setWidth(LayoutParams.WRAP_CONTENT);
			pw.setFocusable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.showAsDropDown(add_friends, 0, 0);
		} else {
			if (!pw.isShowing()) {
				pw.showAsDropDown(add_friends);
			}
		}
		if(flag){
			pw.getContentView().findViewById(R.id.push_current).setVisibility(View.VISIBLE);
		}else{
			pw.getContentView().findViewById(R.id.push_current).setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onSlide(View view, int status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDetach() {
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		super.onDetach();

	}
	
	
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(MsgFragment.class.getName());
        //ע��㲥        
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  

	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (errorItem != null) {

						errorItem.setVisibility(View.GONE);
					}
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(
					R.string.Less_than_chat_server_connection);
			final String st2 = getResources().getString(
					R.string.the_current_network);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (errorItem != null) {

						errorItem.setVisibility(View.VISIBLE);
						if (NetUtils.hasNetwork(getActivity()))
							errorItem.setText(st1);
						else
							errorItem.setText(st2);

					}
				}

			});
		}
	}
	
	
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			flag = false;
			MobclickAgent.onPageStart(mPageName);
			VFDaoImpl daoImpl = new VFDaoImpl(getActivity());
			List<VerificationFrientsList> data = daoImpl.find();
			for (int i = 0; i < data.size(); i++) {
				VerificationFrientsList frientsList = data.get(i);
				if(frientsList.getStatus().equals("0")){
					flag = true;
					break;
				}
			}
			if(flag){
				push_current.setVisibility(View.VISIBLE);
			}else{
				push_current.setVisibility(View.GONE);
			}
		}
		
	};
	
	
	public void registerRemoveFriendBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("removeFriend");
        //ע��㲥        
        getActivity().registerReceiver(removeFriend, myIntentFilter);  
    }
	
	BroadcastReceiver removeFriend = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			refresh();
		}
		
	};

}
