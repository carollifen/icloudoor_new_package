package com.icloudoor.cloudoor.animationUtils;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;


public class MyAnimationView extends View {
	
	Paint paint;

	public MyAnimationView(Context context) {  
        super(context);  
       
          
         
    }  
	
	public MyAnimationView(Context context, AttributeSet attr) {
		super(context, attr); 
		
		// ���ȶ���һ��paint   
        paint = new Paint();   
          
        // ���ƾ�������-ʵ�ľ���   
        // ������ɫ   
        paint.setColor(Color.WHITE);  
        paint.setAntiAlias(true);
        // ������ʽ-���   
        paint.setStyle(Style.STROKE); 
	}
      
    public void setColor(int color) {  
        // ������ɫ   
        paint.setColor(color);  
    }  
    @Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
        float m_width=getWidth();  
        float m_heigh=getHeight(); 
        
         float x_center=m_width/2;
         float y_center=m_heigh/2;
         
         float r=(y_center-140)/5;
         Resources res = this.getContext().getResources();  
       //���������ķ�ʽ��ȡ��Դ  
//       InputStream is = res.openRawResource(R.drawable.radar);  
//       BitmapDrawable  bmpDraw = new BitmapDrawable(is);  
//       Bitmap bmp = bmpDraw.getBitmap();  
        //canvas.drawBitmap(bmp,x_center/2, 0, null);
         
     //   canvas.drawLine(x_center, 0,x_center,m_heigh, paint);
        // draw circle  
        canvas.drawCircle(x_center,y_center, r*4, paint);  
        canvas.drawCircle(x_center,y_center, r*3, paint);  
        canvas.drawCircle(x_center,y_center, r*5, paint);  
       // canvas.drawCircle(x_center,y_center, x_center-50, paint);  
    }  

}
