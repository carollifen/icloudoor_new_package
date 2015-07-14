package com.icloudoor.cloudoor.chat.emoji;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.EditText;

import com.icloudoor.cloudoor.utli.Uitls;

@SuppressLint("ClickableViewAccessibility")
public class EmojiEditText extends EditText {
	
	//文字高度
    private float mTextHeight;
    
    private boolean mTxtNumTip ;
    
    private int mPadding = 8;
    
    private int paddingBottom;
    private int paddingRight;
    private int mNum ;
    
    private TextPaint mPaint ;
    private int mWidth ;
    private int mHeight ;
    private Rect mRect ;
    
    public EmojiEditText(Context context) {
        this(context, null);
    }
    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
	
    private void init(Context context) {
    	addTextChangedListener(mTextChange);
    	
    	mPadding = Uitls.dip2px(context, mPadding);
    	setPadding(mPadding, mPadding, mPadding, mPadding);
    	
    	mRect = new Rect();
        mPaint = new TextPaint();
        mPaint.getTextBounds("00", 0, 2, mRect);
        
        paddingBottom = mRect.height();
		paddingRight = mRect.width() ;
		mTextHeight = getTextSize();
		
//		setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View arg0, boolean focus) {
//				if (focus) {
//					View parent = (View) getParent();
//					if (parent != null) {
//						parent.setBackgroundResource(R.drawable.shape_line_bbbbbb);
//					}
//				} else {
//					View parent = (View) getParent();
//					if (parent != null) {
//						parent.setBackgroundResource(R.drawable.shape_line_e5e5e5);
//					}
//				}
//			}
//		});
    }
    
    TextWatcher mTextChange = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }                                                                                                      
        
        @Override
        public void afterTextChanged(Editable s) {
        	
        	//表情替换
            int selBegin = getSelectionStart();
            int selEnd = getSelectionEnd();
            int[] newSelect = new int[]{selBegin, selEnd};
            
            EmojiManager.getInstance(getContext()).setEmojiSpan(s, mTextHeight);
            if(newSelect[0] != selBegin || newSelect[1] != selEnd){
            	setSelection(newSelect[0], newSelect[1]);
            }
        }
    };
    
    @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mTxtNumTip){
			mWidth = getWidth();
			mHeight = getHeight();
			canvas.drawText(
					""+mNum, 
					mWidth - paddingRight - mPadding, 
					getScrollY() + mHeight - paddingBottom + mPadding/2,
					mPaint
			);
		}
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            ViewParent parent = getParent();
            if(parent != null)
                parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }
    
}
