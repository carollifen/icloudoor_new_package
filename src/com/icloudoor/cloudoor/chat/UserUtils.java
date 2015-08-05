package com.icloudoor.cloudoor.chat;

import android.content.Context;
import android.widget.ImageView;

import com.icloudoor.cloudoor.cloudApplication;

public class UserUtils {
    public static User getUserInfo(String username){
        User user = cloudApplication.getInstance().getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            user.setNick(username);
//            user.setAvatar("http://downloads.easemob.com/downloads/57.png");
        }
        return user;
    }
    
    public static void setUserAvatar1(Context context, String username, ImageView imageView){
        User user = getUserInfo(username);
        if(user != null){
//            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.icon_boy_110).into(imageView);
        }else{
//            Picasso.with(context).load(R.drawable.icon_boy_110).into(imageView);
        }
    }
    
}
