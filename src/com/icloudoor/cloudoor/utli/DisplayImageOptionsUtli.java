package com.icloudoor.cloudoor.utli;

import com.icloudoor.cloudoor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DisplayImageOptionsUtli {

	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_boy_110) // 设置图片在下载期间显示的图片
			.showImageForEmptyUri(R.drawable.icon_boy_110)// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.icon_boy_110) // 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
			// .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
			// .delayBeforeLoading(int delayInMillis)//int
			// delayInMillis为你设置的下载前的延迟时间
			// 设置图片加入缓存前，对bitmap进行设置
			// .preProcessor(BitmapProcessor preProcessor)
			.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
			.build();// 构建完成

}
