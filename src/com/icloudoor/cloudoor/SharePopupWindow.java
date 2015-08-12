package com.icloudoor.cloudoor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class SharePopupWindow extends PopupWindow {
	
	private LinearLayout weixin_layout;
	private LinearLayout weixin_circle_layout;
	private View mMenuView;
	
	public SharePopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.share_popup_layout, null);
		weixin_layout = (LinearLayout) mMenuView.findViewById(R.id.weixin_layout);
		weixin_circle_layout = (LinearLayout) mMenuView.findViewById(R.id.weixin_circle_layout);
		
		weixin_layout.setOnClickListener(itemsOnClick);
		weixin_circle_layout.setOnClickListener(itemsOnClick);
		
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
}