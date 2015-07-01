package com.icloudoor.cloudoor.utli;

import android.content.Context;

import com.icloudoor.cloudoor.chat.activity.FriendDetailActivity;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.db.MyDBHelper;

public class DBHelper extends MyDBHelper{
	
	
	private static final String DBNAME = "icloudoorvf.db";// 数据库名  
    private static final int DBVERSION = 1;  
    private static final Class<?>[] clazz = {VerificationFrientsList.class,MyFriendsEn.class};// 要初始化的

	public DBHelper(Context context) {
		super(context,  DBNAME, null, DBVERSION, clazz);
		// TODO Auto-generated constructor stub
	}

}
