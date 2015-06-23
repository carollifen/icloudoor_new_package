/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.icloudoor.cloudoor.chat;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;

/**
 * 新消息提醒class
 * 2.1.8把新消息提示相关的api移除出sdk，方便开发�?�自由修�?
 * �?发�?�也可以继承此类实现相关的接�?
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class HXNotifier {
    private final static String TAG = "notify";
    Ringtone ringtone = null;

    protected final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
                                                "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
                                              };
    protected final static String[] msg_ch = { "发来�?条消�?", "发来�?张图�?", "发来�?段语�?", "发来位置信息", "发来�?个视�?", "发来�?个文�?",
                                               "%1个联系人发来%2条消�?"
                                             };

    protected static int notifyID = 0525; // start notification id
    protected static int foregroundNotifyID = 0555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;

    protected Context appContext;
    protected String packageName;
    protected String[] msgs;
    protected long lastNotifiyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    protected HXNotificationInfoProvider notificationInfoProvider;

    public HXNotifier() {
    }
    
    /**
     * �?发�?�可以重载此函数
     * this function can be override
     * @param context
     * @return
     */
    public HXNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            msgs = msg_ch;
        } else {
            msgs = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        
        return this;
    }
    
    /**
     * �?发�?�可以重载此函数
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }
    
    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    /**
     * 处理新收到的消息，然后发送�?�知
     * 
     * �?发�?�可以重载此函数
     * this function can be override
     * 
     * @param message
     */
    public synchronized void onNewMsg(EMMessage message) {
        if(EMChatManager.getInstance().isSlientMessage(message)){
            return;
        }
        
        // 判断app是否在后�?
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            sendNotification(message, true);

        }
        
        viberateAndPlayTone(message);
    }
    
    public synchronized void onNewMesg(List<EMMessage> messages) {
        if(EMChatManager.getInstance().isSlientMessage(messages.get(messages.size()-1))){
            return;
        }
        // 判断app是否在后�?
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(messages, false);
        } else {
            sendNotification(messages, true);
        }
        viberateAndPlayTone(messages.get(messages.size()-1));
    }

    /**
     * 发�?��?�知栏提�?
     * This can be override by subclass to provide customer implementation
     * @param messages
     * @param isForeground
     */
    protected void sendNotification (List<EMMessage> messages, boolean isForeground){
        for(EMMessage message : messages){
            if(!isForeground){
                notificationNum++;
                fromUsers.add(message.getFrom());
            }
        }
        sendNotification(messages.get(messages.size()-1), isForeground, false);
    }
    
    protected void sendNotification (EMMessage message, boolean isForeground){
        sendNotification(message, isForeground, true);
    }
    
    /**
     * 发�?��?�知栏提�?
     * This can be override by subclass to provide customer implementation
     * @param message
     */
    protected void sendNotification(EMMessage message, boolean isForeground, boolean numIncrease) {
        String username = message.getFrom();
        try {
            String notifyText = username + " 你有新消�?";
            switch (message.getType()) {
            case TXT:
                notifyText += msgs[0];
                break;
            case IMAGE:
                notifyText += msgs[1];
                break;
            case VOICE:

                notifyText += msgs[2];
                break;
            case LOCATION:
                notifyText += msgs[3];
                break;
            case VIDEO:
                notifyText += msgs[4];
                break;
            case FILE:
                notifyText += msgs[5];
                break;
            }
            
            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
            
            // notification titile
            String contentTitle = appname;
            if (notificationInfoProvider != null) {
                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customCotentTitle = notificationInfoProvider.getTitle(message);
                if (customNotifyText != null){
                    // 设置自定义的状�?�栏提示内容
                    notifyText = customNotifyText;
                }
                    
                if (customCotentTitle != null){
                    // 设置自定义的通知栏标�?
                    contentTitle = customCotentTitle;
                }   
            }

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (notificationInfoProvider != null) {
                // 设置自定义的notification点击跳转intent
                msgIntent = notificationInfoProvider.getLaunchIntent(message);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(numIncrease){
                // prepare latest event info section
                if(!isForeground){
                    notificationNum++;
                    fromUsers.add(message.getFrom());
                }
            }

            int fromUsersNum = fromUsers.size();
            String summaryBody = msgs[6].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));
            
            if (notificationInfoProvider != null) {
                // lastest text
                String customSummaryBody = notificationInfoProvider.getLatestText(message, fromUsersNum,notificationNum);
                if (customSummaryBody != null){
                    summaryBody = customSummaryBody;
                }
                
                // small icon
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0){
                    mBuilder.setSmallIcon(smallIcon);
                }
            }

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker("你有新消�?");
            mBuilder.setContentText(summaryBody);//消息�?
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提�?
     */
    public void viberateAndPlayTone(EMMessage message) {
        if(message != null){
            if(EMChatManager.getInstance().isSlientMessage(message)){
                return;
            } 
        }
        
        HXSDKModel model = HXSDKHelper.getInstance().getModel();
        if(!model.getSettingMsgNotification()){
            return;
        }
        
        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        
        try {
            lastNotifiyTime = System.currentTimeMillis();
            
            // 判断是否处于静音模式
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                EMLog.e(TAG, "in slient mode now");
                return;
            }
            
            if(model.getSettingMsgVibrate()){
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            if(model.getSettingMsgSound()){
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        EMLog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }
                
                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;
                    
                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置NotificationInfoProvider
     * 
     * @param provider
     */
    public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface HXNotificationInfoProvider {
        /**
         * 设置发�?�notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消�?)
         * 
         * @param message
         *            接收到的消息
         * @return null为使用默�?
         */
        String getDisplayedText(EMMessage message);

        /**
         * 设置notification持续显示的新消息提示(比如2个联系人发来�?5条消�?)
         * 
         * @param message
         *            接收到的消息
         * @param fromUsersNum
         *            发�?�人的数�?
         * @param messageNum
         *            消息数量
         * @return null为使用默�?
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         * 
         * @param message
         * @return null为使用默�?
         */
        String getTitle(EMMessage message);

        /**
         * 设置小图�?
         * 
         * @param message
         * @return 0使用默认图标
         */
        int getSmallIcon(EMMessage message);

        /**
         * 设置notification点击时的跳转intent
         * 
         * @param message
         *            显示在notification上最近的�?条消�?
         * @return null为使用默�?
         */
        Intent getLaunchIntent(EMMessage message);
    }
}
