package com.icloudoor.cloudoor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.icloudoor.cloudoor.SlideView.OnSlideListener;
import com.icloudoor.cloudoor.chat.ChatAllHistoryAdapter;
import com.icloudoor.cloudoor.chat.activity.ChatActivity;
import com.icloudoor.cloudoor.chat.activity.ContactActivity;
import com.icloudoor.cloudoor.chat.activity.RoomListActivity;
import com.umeng.analytics.MobclickAgent;


public class MsgFragment extends Fragment implements OnItemClickListener, OnClickListener, OnSlideListener {
	private final String mPageName = "MsgFragment";
	private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	
	private TextView mPopupWindow;
	ListViewForScrollView msg_list;
	private ChatAllHistoryAdapter adapter;
	private RelativeLayout group_layout;
	private List<EMConversation> conversationList ;
	private ImageView add_friends;
	public MsgFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.msg_page, container,false);
		conversationList = new ArrayList<EMConversation>();
		conversationList.addAll(loadConversationsWithRecentChat());
		msg_list = (ListViewForScrollView) view.findViewById(R.id.msg_list);
		group_layout = (RelativeLayout) view.findViewById(R.id.group_layout);
		add_friends = (ImageView) view.findViewById(R.id.add_friends);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// ÉèÖÃadapter
		msg_list.setAdapter(adapter);
		
		
		msg_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(cloudApplication.getInstance().getUserName()))
					Toast.makeText(getActivity(), "sssssssssssss", 0).show();
				else {
				    // ½øÈëÁÄÌìÒ³Ãæ
				    Intent intent = new Intent(getActivity(), ChatActivity.class);
				    if(conversation.isGroup()){
				        if(conversation.getType() == EMConversationType.ChatRoom){
				         // it is group chat
	                        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
	                        intent.putExtra("groupId", username);
				        }else{
				         // it is group chat
	                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
	                        intent.putExtra("groupId", username);
				        }
				        
				    }else{
				        // it is single chat
                        intent.putExtra("userId", username);
				    }
				    startActivity(intent);
				}
			}
		});
		group_layout.setOnClickListener(this);
		add_friends.setOnClickListener(this);
		return view;
	}
	
	
	public void refresh(){
		if(adapter!=null){
			adapter.setData(loadConversationsWithRecentChat());
			adapter.notifyDataSetChanged();
		}
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
	
	
	
	private List<EMConversation> loadConversationsWithRecentChat() {
		
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					//if(conversation.getType() != EMConversationType.ChatRoom){
						sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
					//}
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
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

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
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }	
	
//	@Override
//	public void onSlide(View view, int status) {
//		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
//            mLastSlideViewWithStatusOn.shrink();
//        }
//
//        if (status == SLIDE_STATUS_ON) {
//            mLastSlideViewWithStatusOn = (SlideView) view;
//        }
//		
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.holder:
			Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
			break;
		case R.id.group_layout:
			Intent intent = new Intent(getActivity(),RoomListActivity.class);
			startActivity(intent);
			break;
		
		case R.id.add_friends:
			
			initPopupWindow();
			
			break;
		case R.id.sweep_layout:
			if(pw!=null&&pw.isShowing()){
				pw.dismiss();
			}
			
			break;
		case R.id.add_friend_layout:
			if(pw!=null&&pw.isShowing()){
				pw.dismiss();
			}
			
			break;
		case R.id.constat_layout:
			if(pw!=null&&pw.isShowing()){
				pw.dismiss();
			}
			Intent contactIntent = new Intent(getActivity(),ContactActivity.class);
			startActivity(contactIntent);
			break;

		default:
			break;
		}
	}
	
	
	PopupWindow pw;
	
	public void initPopupWindow(){
		if(pw==null){
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.pw_addfriend, null);
			view.findViewById(R.id.sweep_layout).setOnClickListener(this);
			view.findViewById(R.id.add_friend_layout).setOnClickListener(this);
			view.findViewById(R.id.constat_layout).setOnClickListener(this);
			pw = new PopupWindow(view);
			pw.setHeight(LayoutParams.WRAP_CONTENT);
			pw.setWidth(LayoutParams.WRAP_CONTENT);
			pw.setFocusable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.showAsDropDown(add_friends,0,0);
		}else{
			if(!pw.isShowing()){
				pw.showAsDropDown(add_friends);
			}
			
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
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();

    }
	
}
