package com.icloudoor.cloudoor.widget;

import java.util.Hashtable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.activity.MipcaActivityCapture;
import com.icloudoor.cloudoor.utli.Uitls;

public class QRCodeCreateDialog extends Dialog{

	private Window window = null;
	int QR_WIDTH;
	int QR_HEIGHT;
	Context context;
	ImageView qrcode_img;
	
	public QRCodeCreateDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public QRCodeCreateDialog(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		QR_WIDTH = QR_HEIGHT = Uitls.dip2px(context, 200);
		qrcode_img = (ImageView) findViewById(R.id.qrcode_img);
		findViewById(R.id.sweep_bnt).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(context, MipcaActivityCapture.class));
				dismiss();
			}
		});
		
		findViewById(R.id.cancel_bnt).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	
	 //���ô�����ʾ  
    public void windowDeploy(){  
        window = getWindow(); //�õ��Ի���  
        window.setWindowAnimations(R.style.dialogWindowAnim); //���ô��ڵ�������  
        window.setBackgroundDrawableResource(android.R.color.transparent); //���öԻ��򱳾�Ϊ͸��  
        WindowManager.LayoutParams wl = window.getAttributes();  
        wl.gravity = Gravity.BOTTOM;
        wl.width = LayoutParams.MATCH_PARENT;
        //����x��y�������ô�����Ҫ��ʾ��λ��  
//        wl.alpha = 0.6f; //����͸����  
//        wl.gravity = Gravity.BOTTOM; //��������  
        window.setAttributes(wl);  
    }  
    
    public void createQRImage(String url) {
		try {
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qrcode_img.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
