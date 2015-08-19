package com.icloudoor.cloudoor.utli;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.chat.entity.UserInfoTable;

public class UserDBHelper {
	
	Context context;
	
	public static UserDBHelper dbHelper;
	
	 public static UserDBHelper getInstance(Context context){
	        if(dbHelper==null){
	            synchronized(UserDBHelper.class){
	                if(dbHelper==null){
	                	dbHelper=new UserDBHelper(context);
	                }
	            }
	        }
	        return dbHelper;
	    }
	
	public UserDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	
	public void initTable(List<MyFriendsEn> data){
		SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS",Context.MODE_PRIVATE);
		UserinfoDaoImpl userinfoDaoImpl = new UserinfoDaoImpl(context);
		FriendDaoImpl friendDaoImpl = new FriendDaoImpl(context);
		userinfoDaoImpl.insert(new UserInfoTable());
		SQLiteDatabase db = userinfoDaoImpl.getDbHelper().getWritableDatabase();
		if(data==null||data.size() == 0){
			db.execSQL("delete from friends");
		}else{
			db.beginTransaction();
			try {
				db.execSQL("delete from friends");
				for (int i = 0; i < data.size(); i++) {
					MyFriendsEn friendsEn = data.get(i);
					db.execSQL("insert into friends (userId,myUserId) values(?,?)",new Object[] { friendsEn.getUserId(),loginStatus.getString("USERID", "")});
					Cursor c = db.rawQuery("select * from userinfo where userId=? ", new String[]{friendsEn.getUserId()});
					if(c==null||c.getCount()==0){
						db.execSQL("insert into userinfo(userId, nickname ,portraitUrl,provinceId,districtId,cityId,sex,type,myUserId) values(?,?,?,?,?,?,?,?,?)",
								new Object[] { friendsEn.getUserId(),friendsEn.getNickname(),friendsEn.getPortraitUrl(), 
								friendsEn.getProvinceId(), friendsEn.getDistrictId(), friendsEn.getCityId(), friendsEn.getSex(),1,loginStatus.getString("USERID", "")});
					}
					c.close();
				}
				db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
			} finally {
				db.endTransaction();
			}
		}
	}
	
	public void savaUser(UserInfoTable user){
		UserinfoDaoImpl userinfoDaoImpl = new UserinfoDaoImpl(context);
		List<UserInfoTable> list = userinfoDaoImpl.find(null, "where userId=?", new String[]{user.getUserId()}, null, null, null, null);
		if(list==null||list.size()==0){
			userinfoDaoImpl.insert(user);
		}
	}

}
