package com.icloudoor.cloudoor.utli;

import java.util.Comparator;

import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;

/**
 * 
 * @author FXX
 *
 */
public class PinyinComparator implements Comparator<MyFriendsEn> {

	public int compare(MyFriendsEn o1, MyFriendsEn o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
