package com.icloudoor.cloudoor.chat.emoji;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;

public class EmojiManager {

	private static EmojiManager sInstance;

	private Context mContext;

	private static Pattern mEmojiPattern = Pattern.compile(EmojiSpan.REGULAR);

	private static LinkedHashMap<String, String> mEmojiMap;
	
	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();

	public static EmojiManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new EmojiManager(context);
		}
		return sInstance;
	}

	public EmojiManager(Context context) {
		mContext = context;
	}

	/**
	 * 通过emoji转义文字获取对应emoji的资源id
	 * 
	 * @param emojiName
	 * @return
	 */
	public int getEmojiDrawableId(String emojiName) {
		String emojiId = getEmojiId(emojiName);
		if (!TextUtils.isEmpty(emojiId)) {
			int resIdentifier = mContext.getResources().getIdentifier(emojiId,
					"drawable", mContext.getPackageName());
			if (resIdentifier != 0) {
				return resIdentifier;
			}
		}
		return 0;
	}

	/**
	 * 通过emoji转义文字获取对应emoji的文件名
	 * 
	 * @param emojiName
	 * @return
	 */
	public String getEmojiId(String emojiName) {
		if (mEmojiMap != null && mEmojiMap.get(emojiName) != null) {
			return mEmojiMap.get(emojiName);
		} else {
			return "";
		}
	}
	
	/**
	 * 获取所有emoji转义文字列表
	 * @return
	 */
	public ArrayList<String> getAllEmojiName() {
		ArrayList<String> emojiNames = new ArrayList<String>();
		Iterator<String> iterator = mEmojiMap.keySet().iterator();
		while (iterator.hasNext()) {
			emojiNames.add((String) iterator.next());
		}
		return emojiNames;
	}

	public Spannable setEmojiSpan(CharSequence c, float textHeight) {
		Spannable spannable = spannableFactory.newSpannable(c);
		return setEmojiSpan(spannable, textHeight);
	}
	
	/**
	 * 将一段文字中的表情（名称字符）转换为表情Span。
	 */
	public Spannable setEmojiSpan(Spannable sp, float textHeight) {
		if (sp == null || sp.length() < 1) {
			return null;
		}
		
		Matcher emoMatcher = mEmojiPattern.matcher(sp);

		EmojiSpan[] spans = sp.getSpans(0, sp.length(), EmojiSpan.class);

		while (true) {
			if (!emoMatcher.find()) {
				break;
			}

			int begin = emoMatcher.start();
			int end = emoMatcher.end();
			if (begin < end) {
				String phrase = sp.subSequence(begin, end).toString();
				// 已经设置过的表情Span则不再处理
				if (isSpanExist(spans, sp, phrase, begin, end)) {
					continue;
				}
				int resId = getEmojiDrawableId(phrase);
				if (resId > 0) {
					/** 去掉背景Span */
					BackgroundColorSpan[] bgcSpns = sp.getSpans(begin, end,
							BackgroundColorSpan.class);
					if (bgcSpns != null) {
						for (int i = 0; i < bgcSpns.length; i++)
							sp.removeSpan(bgcSpns[i]);
					}

					/** 换上表情Span */
					sp.setSpan(
							new EmojiSpan(mContext, resId,
									ImageSpan.ALIGN_BOTTOM, phrase,
									(int) (textHeight)), begin, end,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return sp;
	}

	private boolean isSpanExist(EmojiSpan[] spans, Spannable sp, String phrase,
			int begin, int end) {
		if (spans == null || spans.length == 0)
			return false;

		int count = spans.length;
		for (int i = 0; i < count; i++) {
			int spanStart = sp.getSpanStart(spans[i]);
			int spanEnd = sp.getSpanEnd(spans[i]);

			if (spanStart != begin || spanEnd != end)
				continue;

			if (spanStart == begin && spanEnd == end
					&& spans[i].mPhrase.equals(phrase))
				return true;
		}

		return false;
	}
	
	static {
		mEmojiMap = new LinkedHashMap<String, String>();
		
		// 初始化map
		mEmojiMap.put("[笑脸]", "ee_1");
		mEmojiMap.put("[流汗笑]", "ee_2");
		mEmojiMap.put("[害羞]", "ee_3");
		mEmojiMap.put("[花心]", "ee_4");
		mEmojiMap.put("[飞吻]", "ee_5");
		mEmojiMap.put("[害怕]", "ee_6");
		mEmojiMap.put("[哭]", "ee_7");
		mEmojiMap.put("[亲]", "ee_8");
		mEmojiMap.put("[瞪眼]", "ee_9");
		mEmojiMap.put("[苦瓜脸]", "ee_10");
		mEmojiMap.put("[呲牙]", "ee_11");
		mEmojiMap.put("[吐舌]", "ee_12");
		mEmojiMap.put("[不屑]", "ee_13");
		mEmojiMap.put("[愤怒]", "ee_14");
		mEmojiMap.put("[哼哼]", "ee_15");
		mEmojiMap.put("[汗]", "ee_16");
		mEmojiMap.put("[苦逼]", "ee_17");
		mEmojiMap.put("[紧张]", "ee_18");
		mEmojiMap.put("[吃惊]", "ee_19");
		mEmojiMap.put("[口罩]", "ee_20");
		mEmojiMap.put("[哭笑]", "ee_21");
		mEmojiMap.put("[刺瞎]", "ee_22");
		mEmojiMap.put("[恶魔]", "ee_23");
		mEmojiMap.put("[高兴]", "ee_24");
		mEmojiMap.put("[鬼脸]", "ee_25");
		mEmojiMap.put("[囧]", "ee_26");
		mEmojiMap.put("[难过]", "ee_27");
		mEmojiMap.put("[不看]", "ee_28");
		mEmojiMap.put("[不说]", "ee_29");
		mEmojiMap.put("[不听]", "ee_30");
		mEmojiMap.put("[爱心]", "ee_31");
		mEmojiMap.put("[心碎]", "ee_32");
		mEmojiMap.put("[丘比特]", "ee_33");
		mEmojiMap.put("[星星]", "ee_34");
		mEmojiMap.put("[生气]", "ee_35");
		mEmojiMap.put("[大便]", "ee_36");
		mEmojiMap.put("[厉害]", "ee_37");
		mEmojiMap.put("[差劲]", "ee_38");
		mEmojiMap.put("[合十]", "ee_39");
		mEmojiMap.put("[男女]", "ee_40");
		mEmojiMap.put("[跳舞]", "ee_41");
		mEmojiMap.put("[反对]", "ee_42");
		mEmojiMap.put("[支持]", "ee_43");
		mEmojiMap.put("[爱情]", "ee_44");
		mEmojiMap.put("[在一起]", "ee_45");
		mEmojiMap.put("[嘴唇]", "ee_46");
		mEmojiMap.put("[狗]", "ee_47");
		mEmojiMap.put("[猫]", "ee_48");
		mEmojiMap.put("[猪]", "ee_49");
		mEmojiMap.put("[兔]", "ee_50");
		mEmojiMap.put("[鸟]", "ee_51");
		mEmojiMap.put("[鸡]", "ee_52");
		mEmojiMap.put("[鬼]", "ee_53");
		mEmojiMap.put("[圣诞老人]", "ee_54");
		mEmojiMap.put("[外星人]", "ee_55");
		mEmojiMap.put("[钻石]", "ee_56");
		mEmojiMap.put("[礼物]", "ee_57");
		mEmojiMap.put("[男孩]", "ee_58");
		mEmojiMap.put("[女孩]", "ee_59");
		mEmojiMap.put("[蛋糕]", "ee_60");
		mEmojiMap.put("[18x]", "ee_61");
		mEmojiMap.put("[o]", "ee_62");
		mEmojiMap.put("[x]", "ee_63");
		mEmojiMap.put("[涂指甲]", "ee_64");
		mEmojiMap.put("[足球]", "ee_65");
		mEmojiMap.put("[篮球]", "ee_66");
		mEmojiMap.put("[网球]", "ee_67");
		mEmojiMap.put("[游戏]", "ee_68");
		mEmojiMap.put("[海浪]", "ee_69");
		mEmojiMap.put("[教堂]", "ee_70");
		mEmojiMap.put("[信]", "ee_71");
		mEmojiMap.put("[炸弹]", "ee_72");
		mEmojiMap.put("[100分]", "ee_73");
		mEmojiMap.put("[钱]", "ee_74");
		mEmojiMap.put("[火车]", "ee_75");
		mEmojiMap.put("[taxi]", "ee_76");
		mEmojiMap.put("[自行车]", "ee_77");
		mEmojiMap.put("[搀扶]", "ee_78");
		
	}
}
