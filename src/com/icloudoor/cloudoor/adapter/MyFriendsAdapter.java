package com.icloudoor.cloudoor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.entity.MyFriendsEn;
import com.icloudoor.cloudoor.utli.DisplayImageOptionsUtli;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyFriendsAdapter extends BaseAdapter implements SectionIndexer{
	
	private List<MyFriendsEn> list = null;
	private Context mContext;
	
	public MyFriendsAdapter(Context mContext, List<MyFriendsEn> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	 * @param list
	 */
	public void updateListView(List<MyFriendsEn> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		if(list==null)
			return 0;
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final MyFriendsEn mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_myfriends, null);
			viewHolder.friend_name = (TextView) view.findViewById(R.id.friend_name);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.head_img = (ImageView) view.findViewById(R.id.head_img);
			viewHolder.friend_layout = (RelativeLayout) view.findViewById(R.id.friend_layout);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
		//�����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.friend_name.setText(mContent.getNickname());
		ImageLoader.getInstance().displayImage(mContent.getPortraitUrl(), viewHolder.head_img, DisplayImageOptionsUtli.options);
		return view;

	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView friend_name;
		ImageView head_img;
		RelativeLayout friend_layout;
	}
	
	
	
	


	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * ��ȡӢ�ĵ�����ĸ����Ӣ����ĸ��#���档
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
