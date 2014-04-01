package com.lejoying.wxgs.activity.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class ExpressionUtil {
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @param expressionFaceMap
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start,
			Map<String, String> expressionFaceMap) throws SecurityException,
			NoSuchFieldException, NumberFormatException,
			IllegalArgumentException, IllegalAccessException {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			String value = expressionFaceMap.get(key);
			if (value != null) {
				InputStream is = null;
				try {
					is = context.getResources().getAssets()
							.open("images/" + value);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ImageSpan imageSpan = new ImageSpan(context, bitmap); // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
				int end = matcher.start() + key.length(); // 计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中SPAN_INCLUSIVE_EXCLUSIVE
				if (end < spannableString.length()) { // 如果整个字符串还未验证完，则继续。。
					dealExpression(context, spannableString, patten, end,
							expressionFaceMap);
				}
				break;
			}
		}
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @param zhengze
	 * @param expressionFaceMap
	 * @return
	 */
	public static SpannableString getExpressionString(Context context,
			String str, String zhengze, Map<String, String> expressionFaceMap) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			dealExpression(context, spannableString, sinaPatten, 0,
					expressionFaceMap);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}
}