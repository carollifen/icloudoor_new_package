package com.icloudoor.cloudoor.chat.emoji;


import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.emoji.EmojiGridView.OnEmojiClickListener;
import com.icloudoor.cloudoor.widget.PagerIndicator;

public class ExpressionView extends LinearLayout {

	private ViewPager mEmojiPager;
	private PagerAdapter mEmojiPagerAdapter;

	private OnEmojiClickListener mListener;
	
	private EmojiPagerChangeListener mEmojiPagerChangedListener;
	
	private PagerIndicator mPagerIndicator;

	public ExpressionView(Context context) {
		super(context);
		init();
	}

	public ExpressionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_new_expression, this, true);

		mEmojiPager = (ViewPager) findViewById(R.id.pager);
		
		mEmojiPagerChangedListener = new EmojiPagerChangeListener();
		mEmojiPager.setOnPageChangeListener(mEmojiPagerChangedListener);
		
		mPagerIndicator = (PagerIndicator) findViewById(R.id.indicator);
		
		mEmojiPagerAdapter = new EmojiPagerAdapter();
		mEmojiPager.setAdapter(mEmojiPagerAdapter);
		if (mEmojiPagerAdapter.getCount() > 1) {
			mPagerIndicator.setCount(mEmojiPagerAdapter.getCount());
			mPagerIndicator.setVisibility(View.VISIBLE);
			mPagerIndicator.setCurrentItem(0);
		}
		
	}

	public void setOnEmojiClickListener(OnEmojiClickListener l) {
		mListener = l;
	}

	private final class EmojiPagerChangeListener extends
			ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			mPagerIndicator.setCurrentItem(position);
		}
	}
	
	
	private final class EmojiPagerAdapter extends PagerAdapter {
		
		private ArrayList<String> mEmoList;
		
		public EmojiPagerAdapter() {
			mEmoList = EmojiManager.getInstance(getContext()).getAllEmojiName();
		}
		
		@Override
		public int getCount() {
			if (mEmoList != null) {
				int size = mEmoList.size();
				int pageNum = size / (EmojiGridView.CHILD_NUM - 1);
				if (size % (EmojiGridView.CHILD_NUM - 1) == 0) {
					return pageNum;
				} else {
					return pageNum + 1;
				}
			}
			return 0;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			EmojiGridView gridView = new EmojiGridView(getContext());
			gridView.setEmoticonList(mEmoList);
			gridView.setPageNo(position);
			container.addView(gridView);
			gridView.setOnEmojiClickListener(mListener);
			return gridView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}
}
