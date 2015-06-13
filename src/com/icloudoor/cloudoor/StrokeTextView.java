package com.icloudoor.cloudoor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/* StrokeTextView��Ŀ���Ǹ�������� 
 * ʵ�ַ���������TextView����,ֻ����ߵ�TextViewΪ��,ʵ��TextView����������
 * ����ȥ���־��и���ͬ��ɫ�ı߿��� 
 * */

public class StrokeTextView extends TextView {
	private TextView borderText = null;// /������ߵ�TextView

	public StrokeTextView(Context context) {
		super(context);
		borderText = new TextView(context);
		init();
	}

	public StrokeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		borderText = new TextView(context, attrs);
		init();
	}

	public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		borderText = new TextView(context, attrs, defStyle);
		init();
	}

	public void init() {
		TextPaint tp1 = borderText.getPaint();
		tp1.setStrokeWidth(10); // ������߿��
		tp1.setStyle(Style.STROKE); // ������ֻ���
		borderText.setTextColor(0x12ffffff); // //���������ɫ
		borderText.setGravity(getGravity());
	}

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		borderText.setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		CharSequence tt = borderText.getText();
		// ����TextView�ϵ����ֱ���һ��
		if (tt == null || !tt.equals(this.getText())) {
			borderText.setText(getText());
			this.postInvalidate();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		borderText.measure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		borderText.layout(left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		borderText.draw(canvas);
		super.onDraw(canvas);
	}

}