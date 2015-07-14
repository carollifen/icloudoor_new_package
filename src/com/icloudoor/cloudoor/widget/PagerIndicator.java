package com.icloudoor.cloudoor.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.icloudoor.cloudoor.R;

public class PagerIndicator extends AbstractIndicator {

	private Drawable mIndBmp;
	private Drawable mHighlightBmp;
	private int mCount;

	public PagerIndicator(Context context) {
		super(context);
	}

	public PagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PagerIndicator, 0, 0);
		try {
			mIndBmp = a.getDrawable(R.styleable.PagerIndicator_normalIndicator);
			mHighlightBmp = a.getDrawable(R.styleable.PagerIndicator_highlightIndicator);
			mIndBmp.setBounds(0, 0, mIndBmp.getIntrinsicWidth(), mIndBmp.getIntrinsicHeight());
	        mHighlightBmp.setBounds(0, 0, mHighlightBmp.getIntrinsicWidth(),
	                mHighlightBmp.getIntrinsicHeight());
		} finally {
			a.recycle();
		}
	}

	public void setCount(int count) {
		mCount = count;
	}

	@Override
	public int getCount() {
		return mCount;
	}
	
	public void setPagerIndicatorRes(int normalResId, int highlightResId) {
		mIndBmp = getResources().getDrawable(normalResId);
		mIndBmp.setBounds(0, 0, mIndBmp.getIntrinsicWidth(), mIndBmp.getIntrinsicHeight());
		
		mHighlightBmp = getResources().getDrawable(highlightResId);
		mHighlightBmp.setBounds(0, 0, mHighlightBmp.getIntrinsicWidth(),
                mHighlightBmp.getIntrinsicHeight());
		invalidate();
	}
	
	@Override
	public Drawable getIndicator() {
		return mIndBmp;
	}

	@Override
	public Drawable getHighlight() {
		return mHighlightBmp;
	}

	public void clear() {
		mIndBmp = null;
		mHighlightBmp = null;
	}

}
