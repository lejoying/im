package com.lejoying.wxgs.activity.utils;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class ExpressionUtil {
	public static String zhengze = "^\\[f/d{2,3}\\]$"; // 正则表达式，用来判断消息内是否有表情//[\u4E00-\u9FFF]{2}+

	public static SpannableString getExpressionString(Context context,
			String str, Hashtable<String, GifDrawalbe> cache,
			Vector<GifDrawalbe> drawables, Map<String, String> expressionFaceMap) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		Log.v("index", sinaPatten + "-----sinaPatten");
		try {
			dealExpression(context, spannableString, sinaPatten, 0, cache,
					drawables, expressionFaceMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spannableString;
	}

	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start,
			Hashtable<String, GifDrawalbe> cache,
			Vector<GifDrawalbe> drawables, Map<String, String> expressionFaceMap)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			Log.v("index", "-------------------------------while");
			String key = matcher.group();
			Log.v("index", matcher.group() + "-----matcher.group()");
			if (matcher.start() < start) {
				continue;
			}
			// Field field = R.drawable.class.getDeclaredField(key);
			// int id = Integer.parseInt(field.get(null).toString());
			String str = expressionFaceMap.get(key);

			if (str != null) {
				Log.v("Coolspan", key + "---" + str);
				InputStream inputStream = context.getAssets().open(
						"images/" + str);
				GifDrawalbe mSmile = null;
				if (cache.containsKey(key)) {
					mSmile = cache.get(key);
				} else {
					mSmile = new GifDrawalbe(context, inputStream);
					cache.put(key, mSmile);
				}
				ImageSpan span = new ImageSpan(mSmile, ImageSpan.ALIGN_BASELINE);
				int mstart = matcher.start();
				int end = mstart + key.length();
				spannableString.setSpan(span, mstart, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (!drawables.contains(mSmile))
					drawables.add(mSmile);
			}
		}
	}
}