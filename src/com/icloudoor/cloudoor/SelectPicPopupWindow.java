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
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectPicPopupWindow extends PopupWindow {

	private RelativeLayout btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwindow, null);
		btn_take_photo = (RelativeLayout) mMenuView.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (RelativeLayout) mMenuView.findViewById(R.id.btn_pick_photo);
		btn_cancel = (RelativeLayout) mMenuView.findViewById(R.id.btn_cancel);
		
		btn_take_photo.setBackgroundResource(R.drawable.selector_pic_pick);
		btn_pick_photo.setBackgroundResource(R.drawable.selector_pic_pick);
		btn_cancel.setBackgroundResource(R.drawable.selector_pic_pick);
		
		// ȡ����ť
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ���ٵ�����
				dismiss();
			}
		});
		// ���ð�ť����
		btn_pick_photo.setOnClickListener(itemsOnClick);
		btn_take_photo.setOnClickListener(itemsOnClick);
		// ����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.MATCH_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
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