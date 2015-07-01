package com.icloudoor.cloudoor.utli;

import android.content.Context;

import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.db.BaseDaoImpl;

public class FriendDaoImpl extends BaseDaoImpl<MyFriendsEn>{

	public FriendDaoImpl(Context context) {
		super(new DBHelper(context));
		// TODO Auto-generated constructor stub
	}

}
