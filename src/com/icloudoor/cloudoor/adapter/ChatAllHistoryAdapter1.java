package com.icloudoor.cloudoor.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.Constant;
import com.icloudoor.cloudoor.chat.emoji.EmojiManager;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.UserInfoTable;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.icloudoor.cloudoor.utli.Uitls;
import com.icloudoor.cloudoor.utli.UserinfoDaoImpl;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatAllHistoryAdapter1 extends BaseAdapter {

	Context context;
	private List<EMConversation> conversationList;
	UserinfoDaoImpl daoImpl;

	public ChatAllHistoryAdapter1(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (conversationList == null)
			return 0;
		return conversationList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return conversationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView
					.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView
					.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}

		EMConversation conversation = conversationList.get(position);
		String username = conversation.getUserName();
		List<UserInfoTable> list = daoImpl.find(null, "userId = ?",
				new String[] { username }, null, null, null, null);
		UserInfoTable friendsEn = list.get(0);

		
		if (conversation.getType() == EMConversationType.GroupChat) {
//			holder.avatar.setImageResource(R.drawable.group_icon);
			EMGroup group = EMGroupManager.getInstance().getGroup(username);
			holder.name.setText(group != null ? group.getGroupName() : username);
		} else if(conversation.getType() == EMConversationType.ChatRoom){
//		    holder.avatar.setImageResource(R.drawable.group_icon);
            EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
		}else {
//		    UserUtils.setUserAvatar(getContext(), friendsEn.getPortraitUrl(), holder.avatar); 
			if (username.equals(Constant.GROUP_USERNAME)) {
				holder.name.setText("Ⱥ��");

			} else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
				holder.name.setText("������֪ͨ");
			}
			ImageLoader.getInstance().displayImage(friendsEn.getPortraitUrl(), holder.avatar, DisplayImageOptionsUtli.options);
			holder.name.setText(friendsEn.getNickname());
		}
		
		if (conversation.getUnreadMsgCount() > 0) {
			holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			EMMessage lastMessage = conversation.getLastMessage();
//			holder.message.setText(SmileUtils.getSmiledText(context, getMessageDigest(lastMessage, context)),
//					BufferType.SPANNABLE);
			holder.message.setText(EmojiManager.getInstance(context).setEmojiSpan(getMessageDigest(lastMessage, context), Uitls.dip2px(context, 15)));

			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	
	
	
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: 
			if (message.direct == EMMessage.Direct.RECEIVE) {
//				message.get
//				digest = getStrng(context, R.string.location_recv);
//				digest = String.format(digest, message.getFrom());
				digest = getStrng(context, R.string.location_prefix);
				return digest;
			} else {
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: 
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture);
			break;
		case VOICE:
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: 
			digest = getStrng(context, R.string.video);
			break;
		case TXT: 
			if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: 
			digest = getStrng(context, R.string.file);
			break;
		default:
			return "";
		}

		return digest;
	}
	
	public void setData(List<EMConversation> objects){
		
		System.out.println("消息个数："+objects.size());
		
		daoImpl = new UserinfoDaoImpl(context);
		List<EMConversation> haveFriendData = new ArrayList<EMConversation>();
		for (int i = 0; i < objects.size(); i++) {
			List<UserInfoTable> list = daoImpl.find(null, "userId = ?", new String[]{objects.get(i).getUserName()}, null, null, null, null);
			if(list!=null && list.size()>0){
				haveFriendData.add(objects.get(i));
			}
		}
		if(conversationList!=null){
			this.conversationList.clear();
			this.conversationList.addAll(haveFriendData);
		}else{
			this.conversationList = haveFriendData;
		}
		notifyDataSetChanged();
	}
	
	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	private static class ViewHolder {
		TextView name;
		TextView unreadLabel;
		TextView message;
		TextView time;
		ImageView avatar;
		View msgState;
		RelativeLayout list_item_layout;

	}

}
