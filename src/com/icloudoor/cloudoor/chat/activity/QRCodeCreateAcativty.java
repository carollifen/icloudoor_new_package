package com.icloudoor.cloudoor.chat.activity;

import java.util.Hashtable;

import u.aly.dp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.icloudoor.cloudoor.BaseActivity;
import com.icloudoor.cloudoor.R;

public class QRCodeCreateAcativty extends BaseActivity {
	
	ImageView qrcode_img;
	int QR_WIDTH;
	int QR_HEIGHT;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_qrcode);
		qrcode_img = (ImageView) findViewById(R.id.qrcode_img);
		QR_HEIGHT = QR_WIDTH = dip2px(200);
		createQRImage("http://blog.csdn.net/arui319/article/details/6777133");
	}

	public void createQRImage(String url) {
		try {
			// �ж�URL�Ϸ���
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// ͼ������ת����ʹ���˾���ת��
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// �������ﰴ�ն�ά����㷨��������ɶ�ά���ͼƬ��
			// ����forѭ����ͼƬ����ɨ��Ľ��
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// ���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// ��ʾ��һ��ImageView����
			qrcode_img.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
