package com.open.welinks.utils;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	public static String getGMTDate() {
		return getGMTDate(new Date());
	}

	public static String getGMTDate(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getChatMessageListTime(long temestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
		SimpleDateFormat sdfSecond = new SimpleDateFormat("HH:mm");// 24小时制
		Date nowDate = new Date();
		String str = sdf.format(nowDate);
		// String nowmonth = str.substring(5, 7);
		String nowday = str.substring(8, 10);

		Date oldDate = new Date(temestamp);
		String oldstr = sdf.format(oldDate);
		// String oldnowmonth = oldstr.substring(5, 7);
		String oldnowday = oldstr.substring(8, 10);

		String result = "";
		int differentials = Integer.parseInt(nowday) - Integer.parseInt(oldnowday);
		if (differentials == 0) {
			result = sdfSecond.format(oldDate);
		} else if (differentials == 1) {
			result = "昨天";
		} else {
			String[] old = getWeekOfDate(oldDate);
			String[] now = getWeekOfDate(nowDate);
			if ((Integer.parseInt(old[0]) < Integer.parseInt(now[0])) && Integer.parseInt(old[0]) > 0) {
				result = old[1];
			} else {
				result = sdfYMD.format(oldDate);
			}
		}

		return result;
	}

	public static String[] getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return new String[] { w + "", weekDays[w] };
	}
}
