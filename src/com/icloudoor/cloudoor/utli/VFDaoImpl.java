package com.icloudoor.cloudoor.utli;

import android.content.Context;

import com.icloudoor.cloudoor.chat.entity.VerificationFrientsList;
import com.icloudoor.cloudoor.db.BaseDaoImpl;

public class VFDaoImpl extends BaseDaoImpl<VerificationFrientsList> {

	public VFDaoImpl(Context context) {
		super(new DBHelper(context));
		// TODO Auto-generated constructor stub
	}

}
