package com.lejoying.wxgs.activity.utils;

import android.annotation.SuppressLint;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtils {
	public static String getDate(String month, String day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		java.util.Date d = new java.util.Date();
		;
		String str = sdf.format(d);
		String nowmonth = str.substring(5, 7);
		String nowday = str.substring(8, 10);
		String result = null;

		int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
		switch (temp) {
		case 0:
			result = "今天";
			break;
		case 1:
			result = "昨天";
			break;
		case 2:
			result = "前天";
			break;
		default:
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.parseInt(month) + "月");
			sb.append(Integer.parseInt(day) + "日");
			result = sb.toString();
			break;
		}
		return result;
	}

	public static String getTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = null;
		try {
			java.util.Date currentdate = new java.util.Date();// 当前时间

			long i = (currentdate.getTime() / 1000 - timestamp) / (60);
			Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系统当前时间

			String str = sdf.format(new Timestamp(timestamp));
			time = str.substring(11, 16);

			String month = str.substring(5, 7);
			String day = str.substring(8, 10);
			time = getDate(month, day) + time;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getChatMessageListTime(long temestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
		SimpleDateFormat sdfSecond = new SimpleDateFormat("HH:mm");// 24小时制
		Date nowDate = new Date();
		String str = sdf.format(nowDate);
		String nowmonth = str.substring(5, 7);
		String nowday = str.substring(8, 10);

		Date oldDate = new Date(temestamp);
		String oldstr = sdf.format(oldDate);
		String oldnowmonth = oldstr.substring(5, 7);
		String oldnowday = oldstr.substring(8, 10);

		String result = "";
		int differentials = Integer.parseInt(nowday)
				- Integer.parseInt(oldnowday);
		if (differentials == 0) {
			result = sdfSecond.format(oldDate);
		} else if (differentials == 1) {
			result = "昨天";
		} else {
			String[] old = getWeekOfDate(oldDate);
			String[] now = getWeekOfDate(nowDate);
			if ((Integer.parseInt(old[0]) < Integer.parseInt(now[0]))
					&& Integer.parseInt(old[0]) > 0) {
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
