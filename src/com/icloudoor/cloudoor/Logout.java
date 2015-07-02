package com.icloudoor.cloudoor;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/7/1.
 */
public class Logout {
    private Context context;

    public Logout(Context context){
        this.context = context;
    }

    public void logoutDoing() {
        SQLiteDatabase mKeyDB;
        final String TABLE_NAME = "KeyInfoTable";
        final String CAR_TABLE_NAME = "CarKeyTable";
        final String ZONE_TABLE_NAME = "ZoneTable";

        MyDataBaseHelper mKeyDBHelper;
        final String DATABASE_NAME = "KeyDB.db";

        mKeyDBHelper = new MyDataBaseHelper(context, DATABASE_NAME);
        mKeyDB = mKeyDBHelper.getWritableDatabase();

        SharedPreferences loginStatus = context.getSharedPreferences("LOGINSTATUS", 0);
        SharedPreferences.Editor editor1 = loginStatus.edit();
        editor1.putInt("LOGIN", 0);
        editor1.commit();

        String sql = "DELETE FROM " + TABLE_NAME +";";
        mKeyDB.execSQL(sql);

        String sq2 = "DELETE FROM " + CAR_TABLE_NAME +";";
        mKeyDB.execSQL(sq2);

        String sq3 = "DELETE FROM " + ZONE_TABLE_NAME +";";
        mKeyDB.execSQL(sq3);

    }
}
