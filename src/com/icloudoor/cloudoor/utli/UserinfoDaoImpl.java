package com.icloudoor.cloudoor.utli;

import android.content.Context;

import com.icloudoor.cloudoor.chat.entity.UserInfoTable;
import com.icloudoor.cloudoor.db.BaseDaoImpl;

public class UserinfoDaoImpl extends BaseDaoImpl<UserInfoTable>{

	public UserinfoDaoImpl(Context context) {
		super(new DBHelper(context));
		// TODO Auto-generated constructor stub
	}
}
