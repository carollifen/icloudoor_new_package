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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.cloudApplication;

/**
 * ����ҳ��
 * 
 */
public class ChatActivity extends FragmentActivity implements OnClickListener, EMEventListener{
	private static final String TAG = "ChatActivity";
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;

	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	// private ViewPager expressionViewpager;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private int position;
	private ClipboardManager clipboard;
	private ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
	public static ChatActivity activityInstance = null;
	// ��˭������Ϣ
	private String toChatUsername;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// �л�msg�л�ͼƬ
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	public EMGroup group;
	public EMChatRoom room;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		activityInstance = this;
		initView();
		setUpView();
	}

	/**
	 * initView
	 */
	protected void initView() {
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// ������Դ�ļ�,����¼������ʱ
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// ����list
		reslist = getExpressionRes(35);
		// ��ʼ������viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// �������ֿ�
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	private void setUpView() {
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		// �жϵ��Ļ���Ⱥ��
		chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);

		if (chatType == CHATTYPE_SINGLE) { // ����
			toChatUsername = getIntent().getStringExtra("userId");
			((TextView) findViewById(R.id.name)).setText(toChatUsername);
		} else {
			// Ⱥ��
			findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
			findViewById(R.id.container_remove).setVisibility(View.GONE);
			findViewById(R.id.container_voice_call).setVisibility(View.GONE);
			findViewById(R.id.container_video_call).setVisibility(View.GONE);
			toChatUsername = getIntent().getStringExtra("groupId");

			if(chatType == CHATTYPE_GROUP){
			    onGroupViewCreation();
			}else{ 
			    onChatRoomViewCreation();
			}
		}
        
		// for chatroom type, we only init conversation and create view adapter on success
		if(chatType != CHATTYPE_CHATROOM){
		    onConversationInit();
	        
	        onListViewCreation();
	        
	        // show forward message if the message is not null
	        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
	        if (forward_msg_id != null) {
	            // ��ʾ����Ҫת������Ϣ
	            forwardMessage(forward_msg_id);
	        }
		}
	}

	protected void onConversationInit(){
	    if(chatType == CHATTYPE_SINGLE){
	        conversation = EMChatManager.getInstance().getConversationByType(toChatUsername,EMConversationType.Chat);
	    }else if(chatType == CHATTYPE_GROUP){
	        conversation = EMChatManager.getInstance().getConversationByType(toChatUsername,EMConversationType.GroupChat);
	    }else if(chatType == CHATTYPE_CHATROOM){
	        conversation = EMChatManager.getInstance().getConversationByType(toChatUsername,EMConversationType.ChatRoom);
	    }
	     
        // �Ѵ˻Ự��δ������Ϊ0
        conversation.markAllMessagesAsRead();

        // ��ʼ��dbʱ��ÿ��conversation������Ŀ��getChatOptions().getNumberOfMessagesLoaded
        // �����Ŀ������û���������Ự����ʱ��ʾ�ĸ�����һ�����Ͷ����һЩ
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }
        
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener(){

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if(roomId.equals(toChatUsername)){
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {                
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                    String participant) {
                
            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                    String participant) {
                if(roomId.equals(toChatUsername)){
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if(curUser.equals(participant)){
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                        finish();
                    }
                }
            }
            
        });
	}
	
	protected void onListViewCreation(){
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
        // ��ʾ��Ϣ
        listView.setAdapter(adapter);
        
        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
	}
	
	protected void onGroupViewCreation(){
	    group = EMGroupManager.getInstance().getGroup(toChatUsername);
        
        if (group != null){
            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
        }else{
            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
        }
        
        // ������ǰ�Ự��Ⱥ�Ľ�ɢ��T�¼�
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
	}
	
	protected void onChatRoomViewCreation(){
        findViewById(R.id.container_to_group).setVisibility(View.GONE);
        
        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
        EMChatManager.getInstance().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
        
        @Override
        public void onSuccess(EMChatRoom value) {
            // TODO Auto-generated method stub
             runOnUiThread(new Runnable(){
                   @Override
                   public void run(){
                        pd.dismiss();
                        room = EMChatManager.getInstance().getChatRoom(toChatUsername);
                        if(room !=null){
                            ((TextView) findViewById(R.id.name)).setText(room.getName());
                        }else{
                            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
                        }
                        EMLog.d(TAG, "join room success : " + room.getName());
                        
                        onConversationInit();
                        
                        onListViewCreation();
                   }
               });
        }
        
        @Override
        public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
               runOnUiThread(new Runnable(){
                   @Override
                   public void run(){
                       pd.dismiss();
                   }
               });
               finish();
            }
        });
	}
	
	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // ������Ϣ
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
				// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
				// ((TextMessageBody) copyMsg.getBody()).getMessage()));
				clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
				break;
			case RESULT_CODE_DELETE: // ɾ����Ϣ
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
				break;

			case RESULT_CODE_FORWARD: // ת����Ϣ
//				EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
//				Intent intent = new Intent(this, ForwardMessageActivity.class);
//				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//				startActivity(intent); ��ʱ����
				
				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // �����Ϣ
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// ��ջỰ
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // ������Ƭ
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // ���ͱ���ѡ�����Ƶ

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

			} else if (requestCode == REQUEST_CODE_LOCAL) { // ���ͱ���ͼƬ
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // ����ѡ����ļ�
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // ��ͼ
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					String st = getResources().getString(R.string.unable_to_get_loaction);
					Toast.makeText(this, st, 0).show();
				}
				// �ط���Ϣ
			} else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
					|| requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
					|| requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// ճ��
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// ��ͼƬǰ׺ȥ������ԭ��������path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // ���������
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				addUserToBlacklist(deleteMsg.getFrom());
			} else if (conversation.getMsgCount() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}
	}

	/**
	 * ��Ϣͼ�����¼�
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		String st1 = getResources().getString(R.string.not_connect_to_server);
		int id = view.getId();
		if (id == R.id.btn_send) {// ������Ͱ�ť(�����ֺͱ���)
			String s = mEditTextContent.getText().toString();
			sendText(s);
		} else if (id == R.id.btn_take_picture) {
			selectPicFromCamera();// �������ͼ��
		} else if (id == R.id.btn_picture) {
			selectPicFromLocal(); // ���ͼƬͼ��
		} else if (id == R.id.btn_location) { // λ��
			startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
		} else if (id == R.id.iv_emoticons_normal) { // �����ʾ�����
			more.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
		} else if (id == R.id.iv_emoticons_checked) { // ������ر����
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

		} else if (id == R.id.btn_video) {
			// �������ͼ��
			Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
		} else if (id == R.id.btn_file) { // ����ļ�ͼ��
			selectFileFromLocal();
		} else if (id == R.id.btn_voice_call) { // ��������绰ͼ��
			if (!EMChatManager.getInstance().isConnected())
				Toast.makeText(this, st1, 0).show();
			else
				startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).putExtra("username",
						toChatUsername).putExtra("isComingCall", false));
		} else if (id == R.id.btn_video_call) { // ��Ƶͨ��
			if (!EMChatManager.getInstance().isConnected())
				Toast.makeText(this, st1, 0).show();
			else
				startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername).putExtra(
						"isComingCall", false));
		}
	}

	/**
	 * �¼�����
	 * 
	 * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
        case EventNewMessage:
        {
            //��ȡ��message
            EMMessage message = (EMMessage) event.getData();
            
            String username = null;
            //Ⱥ����Ϣ
            if(message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom){
                username = message.getTo();
            }
            else{
                //������Ϣ
                username = message.getFrom();
            }

            //����ǵ�ǰ�Ự����Ϣ��ˢ������ҳ��
            if(username.equals(getToChatUsername())){
                refreshUIWithNewMessage();
                //����������ʾ������Ϣ
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
            }else{
                //�����Ϣ���Ǻ͵�ǰ����ID����Ϣ
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
            }

            break;
        }
        case EventDeliveryAck:
        {
            //��ȡ��message
            EMMessage message = (EMMessage) event.getData();
            refreshUI();
            break;
        }
        case EventReadAck:
        {
            //��ȡ��message
            EMMessage message = (EMMessage) event.getData();
            refreshUI();
            break;
        }
        case EventOfflineMessage:
        {
            //a list of offline messages 
            //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
            refreshUI();
            break;
        }
        default:
            break;
        }
        
    }
	
	
	private void refreshUIWithNewMessage(){
	    if(adapter == null){
	        return;
	    }
	    
	    runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
	}

	private void refreshUI() {
	    if(adapter == null){
            return;
        }
	    
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refresh();
			}
		});
	}

	/**
	 * �����ȡͼƬ
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(), cloudApplication.getInstance().getUserName()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * ѡ���ļ�
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * ��ͼ���ȡͼƬ
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * �����ı���Ϣ
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
			if (chatType == CHATTYPE_GROUP){
			    message.setChatType(ChatType.GroupChat);
			}else if(chatType == CHATTYPE_CHATROOM){
			    message.setChatType(ChatType.ChatRoom);
			}
			
			TextMessageBody txtBody = new TextMessageBody(content);
			// ������Ϣbody
			message.addBody(txtBody);
			// ����Ҫ����˭,�û�username����Ⱥ��groupid
			message.setReceipt(toChatUsername);
			// ��messgage�ӵ�conversation��
			conversation.addMessage(message);
			// ֪ͨadapter����Ϣ�䶯��adapter����ݼ��������message��ʾ��Ϣ�͵���sdk�ķ��ͷ���
			adapter.refreshSelectLast();
			mEditTextContent.setText("");

			setResult(RESULT_OK);

		}
	}

	/**
	 * ��������
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
			if (chatType == CHATTYPE_GROUP){
				message.setChatType(ChatType.GroupChat);
				}else if(chatType == CHATTYPE_CHATROOM){
				    message.setChatType(ChatType.ChatRoom);
				}
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			conversation.addMessage(message);
			adapter.refreshSelectLast();
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// Ĭ�ϳ���100k��ͼƬ��ѹ���󷢸��Է����������óɷ���ԭͼ
		// body.setSendOriginalImage(true);
		message.addBody(body);
		conversation.addMessage(message);

		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * ������Ƶ��Ϣ
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
			if (chatType == CHATTYPE_GROUP){
				message.setChatType(ChatType.GroupChat);
			}else if(chatType == CHATTYPE_CHATROOM){
			    message.setChatType(ChatType.ChatRoom);
			}
			String to = toChatUsername;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
			message.addBody(body);
			conversation.addMessage(message);
			listView.setAdapter(adapter);
			adapter.refreshSelectLast();
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ����ͼ��ͼƬuri����ͼƬ
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * ����λ����Ϣ
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);

	}

	/**
	 * �����ļ�
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.File_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, 0).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6, 0).show();
			return;
		}

		// ����һ���ļ���Ϣ
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// �����Ⱥ�ģ�����chattype,Ĭ���ǵ���
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);
	}

	/**
	 * �ط���Ϣ
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = conversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;

		adapter.refreshSeekTo(resendPos);
	}

	/**
	 * ��ʾ����ͼ�갴ť
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * ��ʾ����ͼ��
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * �����������¼
	 * 
	 * @param view
	 */
	public void emptyHistory(View view) {
		String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
		startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
				.putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 
	 * click group detil
	 * @param view
	 */
	public void toGroupDetails(View view) {
//		if (room == null && group == null) {
//			Toast.makeText(getApplicationContext(), R.string.gorup_not_found, 0).show();
//			return;
//		}
//		if(chatType == CHATTYPE_GROUP){
//			startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//					REQUEST_CODE_GROUP_DETAIL);
//		}else{
//			startActivityForResult((new Intent(this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername)),
//					REQUEST_CODE_GROUP_DETAIL);
//		}
	}

	/**
	 * ��ʾ������ͼ�갴ťҳ
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			EMLog.d(TAG, "more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * ������������
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	private PowerManager.WakeLock wakeLock;

	/**
	 * ��ס˵��listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * ��ȡ�����gridview����view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// ���������ɼ�ʱ���ſ��������
					// ��ס˵���ɼ��������������
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // ����ɾ��������ʾ����
							// �����õķ��䣬���Ի�����ʱ��Ҫ����SmileUtils�����
							Class clz = Class.forName("com.easemob.chatuidemo.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
									(String) field.get(null)));
						} else { // ɾ�����ֻ��߱���
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent.getSelectionStart();// ��ȡ����λ��
								if (selectionStart > 0) {
									String body = mEditTextContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// ��ȡ���һ�������λ��
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditTextContent.getEditableText().delete(i, selectionStart);
										else
											mEditTextContent.getEditableText().delete(selectionStart - 1,
													selectionStart);
									} else {
										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
		if(groupListener != null){
		    EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (group != null)
			((TextView) findViewById(R.id.name)).setText(group.getGroupName());

		 if(adapter != null){
		     adapter.refresh();
	     }

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck });
	}

	@Override
	protected void onStop() {
		// unregister this event listener when this activity enters the
		// background
		EMChatManager.getInstance().unregisterEventListener(this);

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

		// �Ѵ�activity ��foreground activity �б����Ƴ�
		sdkHelper.popActivity(this);
		
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// ֹͣ��������
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// ֹͣ¼��
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * ����������
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * ���뵽������
	 * 
	 * @param username
	 */
	private void addUserToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.Is_moved_into_blacklist));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().addUserToBlackList(username, false);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, 0).show();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, 0).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * ����
	 * 
	 * @param view
	 */
	public void back(View view) {
		EMChatManager.getInstance().unregisterEventListener(this);
		if(chatType == CHATTYPE_CHATROOM){
			EMChatManager.getInstance().leaveChatRoom(toChatUsername);
		}
		finish();
	}

	/**
	 * �����ֻ����ؼ�
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		} else {
			super.onBackPressed();
			if(chatType == CHATTYPE_CHATROOM){
				EMChatManager.getInstance().leaveChatRoom(toChatUsername);
			}
		}
	}

	/**
	 * listview��������listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk��ʼ�����ص������¼Ϊ20��������ʱȥdb���ȡ����					
					List<EMMessage> messages;
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// ��ȡ����messges�����ô˷�����ʱ���db��ȡ��messages
						// sdk���Զ����뵽��conversation��
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// ˢ��ui
						if (messages.size() > 0) {
							adapter.refreshSeekTo(messages.size() - 1);
						}
						
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// ���notification bar��������ҳ�棬��ֻ֤��һ������ҳ��
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}

	/**
	 * ת����Ϣ
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// ��ȡ��Ϣ���ݣ�������Ϣ
			String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
			sendText(content);
			break;
		case IMAGE:
			// ����ͼƬ
			String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// �����ڴ�ͼ��������ͼ
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				sendPicture(filePath);
			}
			break;
		default:
			break;
		}
		
		if(forward_msg.getChatType() == EMMessage.ChatType.ChatRoom){
			EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
		}
	}
	
	/**
	 * ���Ⱥ���ɢ���߱�T�¼�
	 * 
	 */
	class GroupListener implements EMGroupChangeListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
				String st13 = getResources().getString(R.string.you_are_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(ChatActivity.this, st13, 1).show();
//						if (GroupDetailsActivity.instance != null)
//							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// Ⱥ���ɢ�����ڴ�ҳ�棬��ʾȺ�鱻��ɢ����finish��ҳ��
			runOnUiThread(new Runnable() {
				String st14 = getResources().getString(R.string.the_current_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(ChatActivity.this, st14, 1).show();
//						if (GroupDetailsActivity.instance != null)
//							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                String inviter, String reason) {            
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                String applyer, String reason) {            
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                String accepter) {
            
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                String decliner, String reason) {            
        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter,
                String reason) {            
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                String reason) {            
        }

	}

	public String getToChatUsername() {
		return toChatUsername;
	}

	public ListView getListView() {
		return listView;
	}

}