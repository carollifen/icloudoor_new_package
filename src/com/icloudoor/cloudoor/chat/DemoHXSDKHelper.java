/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icloudoor.cloudoor.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.icloudoor.cloudoor.CloudDoorMainActivity;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.UrlUtils;
import com.icloudoor.cloudoor.Version;
import com.icloudoor.cloudoor.chat.HXNotifier.HXNotificationInfoProvider;
import com.icloudoor.cloudoor.chat.activity.ChatActivity;
import com.icloudoor.cloudoor.chat.activity.FriendDetailActivity;
import com.icloudoor.cloudoor.chat.entity.MyFriendInfo;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.http.MyRequestBody;
import com.icloudoor.cloudoor.utli.FriendDaoImpl;
import com.icloudoor.cloudoor.utli.GsonUtli;
import com.icloudoor.cloudoor.utli.VFDaoImpl;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * 
 * @author easemob
 * 
 */
public class DemoHXSDKHelper extends HXSDKHelper {

	private static final String TAG = "DemoHXSDKHelper";

	/**
	 * EMEventListener
	 */
	protected EMEventListener eventListener = null;

	/**
	 * contact list in cache
	 */
	private Map<String, User> contactList;
	private CallReceiver callReceiver;

	private Version version;

	/**
	 * 用来记录foreground Activity
	 */
	private List<Activity> activityList = new ArrayList<Activity>();

	public void pushActivity(Activity activity) {
		if (!activityList.contains(activity)) {
			activityList.add(0, activity);
		}
	}

	public void popActivity(Activity activity) {
		activityList.remove(activity);
	}

	@Override
	protected void initHXOptions() {
		super.initHXOptions();

		// you can also get EMChatOptions to set related SDK options
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		options.allowChatroomOwnerLeave(getModel()
				.isChatroomOwnerLeaveAllowed());
	}

	@Override
	protected void initListener() {
		super.initListener();
		IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
				.getIncomingCallBroadcastAction());
		if (callReceiver == null) {
			callReceiver = new CallReceiver();
		}

		// 注册通话广播接收�?
		appContext.registerReceiver(callReceiver, callFilter);
		// 注册消息事件监听
		initEventListener();
	}

	public String loadSid() {
		SharedPreferences loadSid = appContext.getSharedPreferences("SAVEDSID",
				0);
		return loadSid.getString("SID", null);
	}

	public void getFriends() {
		RequestQueue mRequestQueue = Volley.newRequestQueue(appContext.getApplicationContext());
		version = new Version(appContext);
		String url = UrlUtils.HOST + "/user/im/getFriends.do" + "?sid="
				+ loadSid() + "&ver=" + version.getVersionName() + "&imei=" + version.getDeviceId();
		MyRequestBody requestBody = new MyRequestBody(url, "{}",
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						
						MyFriendInfo friendInfo = GsonUtli.jsonToObject(
								response.toString(), MyFriendInfo.class);
						if (friendInfo != null) {
							
							
							List<MyFriendsEn> data = friendInfo.getData();
							FriendDaoImpl daoImpl = new FriendDaoImpl(
									appContext);
							SQLiteDatabase db = daoImpl.getDbHelper()
									.getWritableDatabase();
							if(data==null||data.size() == 0){
								db.execSQL("delete from friends");
							}else{
								db.beginTransaction();
								try {
									db.execSQL("delete from friends");
									for (int i = 0; i < data.size(); i++) {
										MyFriendsEn friendsEn = data.get(i);
										db.execSQL("insert into friends(userId, nickname ,portraitUrl,provinceId,districtId,cityId,sex) values(?,?,?,?,?,?,?)",
												new Object[] { friendsEn.getUserId(),friendsEn.getNickname(),friendsEn.getPortraitUrl(), 
												friendsEn.getProvinceId(), friendsEn.getDistrictId(), friendsEn.getCityId(), friendsEn.getSex()});
									}
									db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
								} finally {
									db.endTransaction();
								}
							}
							

						} 

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(appContext, R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});

		mRequestQueue.add(requestBody);
	}

	public void setExt(EMMessage message){
		SharedPreferences loginStatus = appContext.getSharedPreferences("LOGINSTATUS", Context.MODE_PRIVATE);
		JSONObject ext = new JSONObject();
		try {
			ext.put("userId", loginStatus.getString("USERID", ""));
			ext.put("nickname", loginStatus.getString("NICKNAME", ""));
			ext.put("portraitUrl", loginStatus.getString("URL", ""));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message.setAttribute("userInfo", ext);
	}
	
	/**
	 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处�? activityList.size()
	 * <= 0 意味�?�?有页面都已经在后台运行，或�?�已经离�?Activity Stack
	 */
	protected void initEventListener() {
		eventListener = new EMEventListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = null;
				if (event.getData() instanceof EMMessage) {
					message = (EMMessage) event.getData();
					EMLog.d(TAG, "receive the event : " + event.getEvent()
							+ ",id : " + message.getMsgId());
				}

				switch (event.getEvent()) {
				case EventNewMessage:
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if (activityList.size() <= 0) {
						HXSDKHelper.getInstance().getNotifier()
								.onNewMsg(message);
					}
					break;
				case EventOfflineMessage:
					if (activityList.size() <= 0) {
						EMLog.d(TAG, "received offline messages");
						List<EMMessage> messages = (List<EMMessage>) event
								.getData();
						HXSDKHelper.getInstance().getNotifier()
								.onNewMesg(messages);
					}
					break;
				// below is just giving a example to show a cmd toast, the app
				// should not follow this
				// so be careful of this
				case EventNewCMDMessage: {
					CmdMessageBody cmdMsgBody = (CmdMessageBody) message
							.getBody();
					String action = cmdMsgBody.action;
					System.out.println(" EMaction = "+action);
					if (action.equals("invite")) {
						try {
							JSONObject vfoj = message
									.getJSONObjectAttribute("data");
							VFDaoImpl daoImpl = new VFDaoImpl(appContext);

							List<VerificationFrientsList> vfData = daoImpl
									.find(null,
											"invitationId = ?",
											new String[] { vfoj
													.getString("invitationId") },
											null, null, null, null);
							if (vfData == null || vfData.size() == 0) {
								VerificationFrientsList vf = new VerificationFrientsList();
								vf.setPortraitUrl(vfoj.getString("portraitUrl"));
								vf.setComment(vfoj.getString("comment"));
								vf.setNickname(vfoj.getString("nickname"));
								vf.setUserId(vfoj.getString("userId"));
								vf.setInvitationId(vfoj
										.getString("invitationId"));
								vf.setStatus("0");
								daoImpl.insert(vf);
							}

						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if(action.equals("acceptInvite")){
						EMConversation emConversation = EMChatManager.getInstance().getConversation(message.getFrom());
						EMMessage txtMessage =  EMMessage.createSendMessage(EMMessage.Type.TXT);
						TextMessageBody messageBody = new TextMessageBody("我们已经成为好友，可以聊天了");
						setExt(message);
						txtMessage.setAttribute("type", 4);
						txtMessage.addBody(messageBody);
						txtMessage.setReceipt(message.getFrom());
						emConversation.addMessage(txtMessage);
						 try {
							EMChatManager.getInstance().sendMessage(txtMessage);
						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						getFriends();
					}else{
						getFriends();
					}

					// daoImpl.insert(entity);

					// message.getStringAttribute("");
					// EMLog.d(TAG, String.format("透传消息：action:%s,message:%s",
					// action,message.toString()));
					// final String str =
					// appContext.getString(R.string.receive_the_passthrough);
					//
					// final String CMD_TOAST_BROADCAST =
					// "easemob.demo.cmd.toast";
					// IntentFilter cmdFilter = new
					// IntentFilter(CMD_TOAST_BROADCAST);
					//
					// if(broadCastReceiver == null){
					// broadCastReceiver = new BroadcastReceiver(){
					//
					// @Override
					// public void onReceive(Context context, Intent intent) {
					// // TODO Auto-generated method stub
					// Toast.makeText(appContext,
					// intent.getStringExtra("cmd_value"),
					// Toast.LENGTH_SHORT).show();
					// }
					// };
					//
					// //注册通话广播接收�?
					// appContext.registerReceiver(broadCastReceiver,cmdFilter);
					// }
					//
					// Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
					// broadcastIntent.putExtra("cmd_value", str+action);
					// appContext.sendBroadcast(broadcastIntent, null);

					break;
				}
				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				// add other events in case you are interested in
				default:
					break;
				}

			}
		};

		EMChatManager.getInstance().registerEventListener(eventListener);

		EMChatManager.getInstance().addChatRoomChangeListener(
				new EMChatRoomChangeListener() {
					private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
					private final IntentFilter filter = new IntentFilter(
							ROOM_CHANGE_BROADCAST);
					private boolean registered = false;

					private void showToast(String value) {
						if (!registered) {
							// 注册通话广播接收�?
							appContext.registerReceiver(
									new BroadcastReceiver() {

										@Override
										public void onReceive(Context context,
												Intent intent) {
											Toast.makeText(
													appContext,
													intent.getStringExtra("value"),
													Toast.LENGTH_SHORT).show();
										}

									}, filter);

							registered = true;
						}

						Intent broadcastIntent = new Intent(
								ROOM_CHANGE_BROADCAST);
						broadcastIntent.putExtra("value", value);
						appContext.sendBroadcast(broadcastIntent, null);
					}

					@Override
					public void onChatRoomDestroyed(String roomId,
							String roomName) {
						showToast(" room : " + roomId + " with room name : "
								+ roomName + " was destroyed");
						Log.i("info", "onChatRoomDestroyed=" + roomName);
					}

					@Override
					public void onMemberJoined(String roomId, String participant) {
						showToast("member : " + participant
								+ " join the room : " + roomId);
						Log.i("info", "onmemberjoined=" + participant);

					}

					@Override
					public void onMemberExited(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " leave the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberExited=" + participant);

					}

					@Override
					public void onMemberKicked(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " was kicked from the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberKicked=" + participant);

					}

				});
	}

	
	public String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
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
	@Override
	protected HXNotificationInfoProvider getNotificationListener() {
		// 可以覆盖默认的设�?
		return new HXNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// 设置小图标，这里为默�?
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// 设置状�?�栏的消息提示，可以根据message的类型做相应提示
				String ticker = CommonUtils.getMessageDigest(message,
						appContext);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}

				return message.getFrom() + ": " + ticker;
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum,
					int messageNum) {
				
				
				
				return getMessageDigest(message, appContext);
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// 设置点击通知栏跳转事�?
				Intent intent = new Intent(appContext, ChatActivity.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				} else { // 群聊信息
					// message.getTo()为群聊id
					intent.putExtra("groupId", message.getTo());
					if (chatType == ChatType.GroupChat) {
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					} else {
						intent.putExtra("chatType",
								ChatActivity.CHATTYPE_CHATROOM);
					}

				}
				return intent;
			}
		};
	}

	@Override
	protected void onConnectionConflict() {
		Intent intent = new Intent(appContext, CloudDoorMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("conflict", true);
		appContext.startActivity(intent);
	}

	@Override
	protected void onCurrentAccountRemoved() {
		Intent intent = new Intent(appContext, CloudDoorMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constant.ACCOUNT_REMOVED, true);
		appContext.startActivity(intent);
	}

	@Override
	protected HXSDKModel createModel() {
		return new DemoHXSDKModel(appContext);
	}

	@Override
	public HXNotifier createNotifier() {
		return new HXNotifier() {
			public synchronized void onNewMsg(final EMMessage message) {
				if (EMChatManager.getInstance().isSlientMessage(message)) {
					return;
				}

				String chatUsename = null;
				List<String> notNotifyIds = null;
				// 获取设置的不提示新消息的用户或�?�群组ids
				if (message.getChatType() == ChatType.Chat) {
					chatUsename = message.getFrom();
					notNotifyIds = ((DemoHXSDKModel) hxModel)
							.getDisabledGroups();
				} else {
					chatUsename = message.getTo();
					notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
				}

				if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
					// 判断app是否在后�?
					if (!EasyUtils.isAppRunningForeground(appContext)) {
						EMLog.d(TAG, "app is running in backgroud");
						sendNotification(message, false);
					} else {
						sendNotification(message, true);

					}

					viberateAndPlayTone(message);
				}
			}
		};
	}

	/**
	 * get demo HX SDK Model
	 */
	public DemoHXSDKModel getModel() {
		return (DemoHXSDKModel) hxModel;
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		if (getHXId() != null && contactList == null) {
			contactList = ((DemoHXSDKModel) getModel()).getContactList();
		}

		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	@Override
	public void logout(final EMCallBack callback) {
		endCall();
		super.logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				setContactList(null);
				getModel().closeDB();
				if (callback != null) {
					callback.onSuccess();
				}
			}

			@Override
			public void onError(int code, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int progress, String status) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

		});
	}

	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
