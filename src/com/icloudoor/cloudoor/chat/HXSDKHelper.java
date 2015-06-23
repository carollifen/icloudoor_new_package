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

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.icloudoor.cloudoor.chat.HXNotifier.HXNotificationInfoProvider;

/**
 * The developer can derive from this class to talk with HuanXin SDK
 * All the Huan Xin related initialization and global listener are implemented in this class which will 
 * help developer to speed up the SDK integration�?
 * this is a global instance class which can be obtained in any codes through getInstance()
 * 
 * 
 * @author easemob
 *
 */
public abstract class HXSDKHelper {
    private static final String TAG = "HXSDKHelper";
    /**
     * application context
     */
    protected Context appContext = null;
    
    /**
     * HuanXin mode helper, which will manage the user data and user preferences
     */
    protected HXSDKModel hxModel = null;
    
    /**
     * MyConnectionListener
     */
    protected EMConnectionListener connectionListener = null;
    
    /**
     * HuanXin ID in cache
     */
    protected String hxId = null;
    
    /**
     * password in cache
     */
    protected String password = null;
    
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    /**
     * the global HXSDKHelper instance
     */
    private static HXSDKHelper me = null;
    
    /**
     * the notifier
     */
    protected HXNotifier notifier = null;
    
    protected HXSDKHelper(){
        me = this;
    }
    
    /**
     * this function will initialize the HuanXin SDK
     * 
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     * 
     * 环信初始化SDK帮助函数
     * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代�?
     * 
     * for example:
     * 例子�?
     * 
     * public class DemoHXSDKHelper extends HXSDKHelper
     * 
     * HXHelper = new DemoHXSDKHelper();
     * if(HXHelper.onInit(context)){
     *     // do HuanXin related work
     * }
     */
    public synchronized boolean onInit(Context context){
        if(sdkInited){
            return true;
        }

        appContext = context;
        
        // create HX SDK model
        hxModel = createModel();
        
        // create a defalut HX SDK model in case subclass did not provide the model
        if(hxModel == null){
            hxModel = new DefaultHXSDKModel(appContext);
        }
        
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        
        Log.d(TAG, "process app name : " + processAppName);
        
        // 如果app启用了远程的service，此application:onCreate会被调用2�?
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1�?
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返�?
        if (processAppName == null || !processAppName.equalsIgnoreCase(hxModel.getAppProcessName())) {
            Log.e(TAG, "enter the service process!");
            
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }

        // 初始化环信SDK,�?定要先调用init()
        EMChat.getInstance().init(context);
        
        // 设置sandbox测试环境
        // 建议�?发�?�开发时设置此模�?
        if(hxModel.isSandboxMode()){
            EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
        }
        
        if(hxModel.isDebugMode()){
            // set debug mode in development process
            EMChat.getInstance().setDebugMode(true);    
        }

        Log.d(TAG, "initialize EMChat SDK");
                
        initHXOptions();
        initListener();
        sdkInited = true;
        return true;
    }
    
    /**
     * get global instance
     * @return
     */
    public static HXSDKHelper getInstance(){
        return me;
    }
    
    public HXSDKModel getModel(){
        return hxModel;
    }
    
    public String getHXId(){
        if(hxId == null){
            hxId = hxModel.getHXId();
        }
        return hxId;
    }
    
    public String getPassword(){
        if(password == null){
            password = hxModel.getPwd();
        }
        return password;    
    }
    
    public void setHXId(String hxId){
        if (hxId != null) {
            if(hxModel.saveHXId(hxId)){
                this.hxId = hxId;
            }
        }
    }
    
    public void setPassword(String password){
        if(hxModel.savePassword(password)){
            this.password = password;
        }
    }
    
    /**
     * the subclass must override this class to provide its own model or directly use {@link DefaultHXSDKModel}
     * @return
     */
    abstract protected HXSDKModel createModel();
    
    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    protected void initHXOptions(){
        Log.d(TAG, "init HuanXin Options");
        
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不�?要验证的，改成需要验�?
        options.setAcceptInvitationAlways(hxModel.getAcceptInvitationAlways());
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(hxModel.getUseHXRoster());
        // 设置是否�?要已读回�?
        options.setRequireAck(hxModel.getRequireReadAck());
        // 设置是否�?要已送达回执
        options.setRequireDeliveryAck(hxModel.getRequireDeliveryAck());
        // 设置从db初始化加载时, 每个conversation�?要加载msg的个�?
        options.setNumberOfMessagesLoaded(1);
        
        notifier = createNotifier();
        notifier.init(appContext);
        
        notifier.setNotificationInfoProvider(getNotificationListener());
    }
    
    /**
     * subclass can override this api to return the customer notifier
     * 
     * @return
     */
    protected HXNotifier createNotifier(){
        return new HXNotifier();
    }
    
    public HXNotifier getNotifier(){
        return notifier;
    }
    
    /**
     * logout HuanXin SDK
     */
    public void logout(final EMCallBack callback){
        setPassword(null);
        EMChatManager.getInstance().logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                if(callback != null){
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
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
    
    /**
     * �?查是否已经登录过
     * @return
     */
    public boolean isLogined(){
       return EMChat.getInstance().isLoggedIn();
    }
    
    protected HXNotificationInfoProvider getNotificationListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    protected void initListener(){
        Log.d(TAG, "init listener");
        
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
            	if (error == EMError.USER_REMOVED) {
            		onCurrentAccountRemoved();
            	}else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }else{
                    onConnectionDisconnected(error);
                }
            }

            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };
        
        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);       
    }

    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}

    
    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}
    
    
    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}
    
    /**
     * handle the connection disconnect
     * @param error see {@link EMError}
     */
    protected void onConnectionDisconnected(int error){}

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
