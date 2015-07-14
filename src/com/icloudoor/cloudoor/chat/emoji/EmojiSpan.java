package com.icloudoor.cloudoor.chat.emoji;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class EmojiSpan extends ImageSpan {
	
	public static final String REGULAR = "\\[(\\S+?)\\]" ;
	String mPhrase;
	int mSize;
	int resourceId;
	Context context;
	
	public EmojiSpan(Context context, int resourceId,
			int verticalAlignment, String phrase, int height) {
		super(context, resourceId, verticalAlignment);
		mPhrase = phrase;
		mSize = height + 10;
		this.resourceId = resourceId;
		this.context = context;
	}

	public EmojiSpan(Context context, Bitmap bitmap) {
		super(context, bitmap);
	}
	
	/**
	 * 设置表情图片的大小。
	 */
	@Override
	public Drawable getDrawable() {
		Drawable d = super.getDrawable();
		if (d != null && mSize != 0) {
			// 设置大小
			int w = d.getIntrinsicWidth();
			if (mSize < w) {
				d.setBounds(0, 0, mSize, mSize);
			}
		}
		return d;
	}
	
}
