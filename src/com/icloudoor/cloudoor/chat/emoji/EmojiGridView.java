package com.icloudoor.cloudoor.chat.emoji;


import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.icloudoor.cloudoor.R;

public class EmojiGridView extends GridView {

	// 包含删除键
	public static int CHILD_NUM = 21;

	/* 表情图标索引的列表 */
	private List<String> mEmojiNameList;

	// 这个grid要显示的page no
	private int mPageNo;

	/* 表情View被Click的监听 */
	private OnEmojiClickListener mListener;

	private EmojiAdapter mAdapter;

	private int mColumnNum = 7;
	private int mItemWidth;

	public EmojiGridView(Context context) {
		super(context);
		init(context);
	}

	public EmojiGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EmojiGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		setGravity(Gravity.CENTER);
		setNumColumns(mColumnNum);
		setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		
//		setSelector(R.drawable.bg_emoji_grid_selector);
		
		mItemWidth = getContext().getResources().getDisplayMetrics().widthPixels
				/ mColumnNum;

		mAdapter = new EmojiAdapter(context);
		setAdapter(mAdapter);

		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == CHILD_NUM - 1 && mListener != null) {
					// 点击删除键
					mListener.onClick(null, true);
				}
				EmojiAdapter adapter = (EmojiAdapter) parent.getAdapter();
				String phrase = adapter.getItem(position);
				if (phrase != null && mListener != null) {
					mListener.onClick(phrase, false);
				}
			}
		});
	}

	public void setEmoticonList(List<String> emojiNameList) {
		mEmojiNameList = emojiNameList;
	}

	/**
	 * 设置页码
	 */
	public void setPageNo(int pageNo) {
		mPageNo = pageNo;
	}

	public interface OnEmojiClickListener {
		public void onClick(String phrase, boolean isDelete);
	}

	public void setOnEmojiClickListener(OnEmojiClickListener l) {
		mListener = l;
	}

	private class EmojiAdapter extends BaseAdapter {

		private GridView.LayoutParams lp;
		private LayoutInflater inflater;

		public EmojiAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return CHILD_NUM;
		}

		@Override
		public String getItem(int position) {
			if (position == CHILD_NUM - 1) {
				return null;
			}
			// 计算这个item在list的位置
			int index = (CHILD_NUM - 1) * mPageNo + position;
			if (mEmojiNameList != null) {
				if (index >= 0 && index < mEmojiNameList.size()) {
					return mEmojiNameList.get(index);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_view_expression, null);
			}
			ImageView emojiIv = (ImageView) convertView.findViewById(R.id.iv_expression);
			lp = new GridView.LayoutParams(mItemWidth, mItemWidth);
			convertView.setLayoutParams(lp);
			
			String phrase = getItem(position);
			emojiIv.setEnabled(true);
			
			if (!TextUtils.isEmpty(phrase)) {// 格子里面是有内容的
				emojiIv.setImageResource(EmojiManager.getInstance(getContext()).getEmojiDrawableId(phrase));
				emojiIv.setTag(phrase);
				
			} else if (position == CHILD_NUM - 1) {// 最右下角，显示一个删除按钮
				emojiIv.setImageResource(R.drawable.delete_expression);
				emojiIv.setTag(null);
				
			} else {
				emojiIv.setImageResource(0);
				emojiIv.setTag(null);
				emojiIv.setBackgroundResource(0);
				emojiIv.setEnabled(false);
			}
			return convertView;
		}
		
		/**
		 * 用于控制EmoticonGrid中的空格子不能选择
		 */
		@Override
		public boolean isEnabled(int position) {
			if (position == CHILD_NUM - 1) {// 最右下角的格子
				return true;
			} else {
				String info = getItem(position);
				return info != null;
			}
		}

	}
	
}
