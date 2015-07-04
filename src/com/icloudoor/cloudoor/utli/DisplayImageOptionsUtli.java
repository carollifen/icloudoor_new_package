package com.icloudoor.cloudoor.utli;

import com.icloudoor.cloudoor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DisplayImageOptionsUtli {

	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_boy_110) // ����ͼƬ�������ڼ���ʾ��ͼƬ
			.showImageForEmptyUri(R.drawable.icon_boy_110)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
			.showImageOnFail(R.drawable.icon_boy_110) // ����ͼƬ����/��������д���ʱ����ʾ��ͼƬ
			.cacheInMemory(true)// �������ص�ͼƬ�Ƿ񻺴����ڴ���
			.cacheOnDisc(true)// �������ص�ͼƬ�Ƿ񻺴���SD����
			// .considerExifParams(true) //�Ƿ���JPEGͼ��EXIF��������ת����ת��
			// .delayBeforeLoading(int delayInMillis)//int
			// delayInMillisΪ�����õ�����ǰ���ӳ�ʱ��
			// ����ͼƬ���뻺��ǰ����bitmap��������
			// .preProcessor(BitmapProcessor preProcessor)
			.displayer(new FadeInBitmapDisplayer(100))// �Ƿ�ͼƬ���غú���Ķ���ʱ��
			.build();// �������

}
