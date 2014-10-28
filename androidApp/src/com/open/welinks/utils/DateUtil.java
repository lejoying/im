package com.open.welinks.utils;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	public static String[] getDayMoth(long timeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d = new java.util.Date();
		String str = sdf.format(d);
		String nowmonth = str.substring(str.indexOf("-") + 1, str.lastIndexOf("-"));
		String nowday = str.substring(str.lastIndexOf("-") + 1);

		String str2 = sdf.format(timeMillis);
		String nowmonth2 = str2.substring(str2.indexOf("-") + 1, str2.lastIndexOf("-"));
		String nowday2 = str2.substring(str2.lastIndexOf("-") + 1);

		int differenceTime = Integer.parseInt(nowday) - Integer.parseInt(nowday2);

		String[] result = new String[2];
		if (differenceTime == 0 && nowmonth.equals(nowmonth2)) {
			result[0] = "今";
			result[1] = "天";
		} else if (differenceTime == 1 && nowmonth.equals(nowmonth2)) {
			result[0] = "昨";
			result[1] = "天";
		} else if (differenceTime == 2 && nowmonth.equals(nowmonth2)) {
			result[0] = "前";
			result[1] = "天";
		} else {
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd#MM月");
			String dataStr = sdf3.format(timeMillis);
			result[0] = dataStr.substring(0, dataStr.indexOf("#"));
			result[1] = dataStr.substring(dataStr.indexOf("#") + 1);
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long timeMillis) {
		long currentTime = System.currentTimeMillis();
		long differenceTime = currentTime - timeMillis;
		differenceTime = differenceTime / 1000;
		String result = "";
		if (differenceTime <= 0) {
			result = "刚刚";
		} else if (differenceTime >= 0 && differenceTime < 60) {
			result = differenceTime + "秒前";
		} else if (differenceTime > 60 && differenceTime < 60 * 60) {
			result = differenceTime / 60 + "分钟前";
		} else if (differenceTime > 60 * 60 && differenceTime < 24 * 60 * 60) {
			result = differenceTime / (60 * 60) + "小时前";
		} else if (differenceTime > 24 * 60 * 60 && differenceTime < 2 * 24 * 60 * 60) {
			result = "昨天   " + formatHourMinute(timeMillis);
		} else if (differenceTime > 2 * 24 * 60 * 60 && differenceTime < 3 * 24 * 60 * 60) {
			result = "前天   " + formatHourMinute(timeMillis);
		} else if (differenceTime > 3 * 24 * 60 * 60 && differenceTime < 365 * 24 * 60 * 60) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			result = sdf.format(timeMillis);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			result = sdf.format(timeMillis);
		}

		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getMessageSequeceTime(long timeMillis) {
		long currentTime = System.currentTimeMillis();
		long differenceTime = currentTime - timeMillis;
		differenceTime = differenceTime / 1000;
		String result = "";
		if (differenceTime > 60 * 60 && differenceTime < 24 * 60 * 60) {
			result = formatHourMinute(timeMillis);
		} else if (differenceTime > 24 * 60 * 60 && differenceTime < 2 * 24 * 60 * 60) {
			result = "昨天   " + formatHourMinute(timeMillis);
		} else if (differenceTime > 2 * 24 * 60 * 60 && differenceTime < 3 * 24 * 60 * 60) {
			result = getWeek(timeMillis);
		} else if (differenceTime > 3 * 24 * 60 * 60 && differenceTime < 4 * 24 * 60 * 60) {
			result = getWeek(timeMillis);
		} else if (differenceTime > 4 * 24 * 60 * 60 && differenceTime < 5 * 24 * 60 * 60) {
			result = getWeek(timeMillis);
		} else if (differenceTime > 5 * 24 * 60 * 60 && differenceTime < 6 * 24 * 60 * 60) {
			result = getWeek(timeMillis);
		} else if (differenceTime > 6 * 24 * 60 * 60 && differenceTime < 365 * 24 * 60 * 60) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			result = sdf.format(timeMillis);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			result = sdf.format(timeMillis);
		}

		return result;
	}

	public static String getWeek(long timeMillis) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMillis);
		// cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatYearMonthDay(long timeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date(timeMillis);
		String mTime = simpleDateFormat.format(date);
		return mTime;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatYearMonthDay2(String timeMillis) {
		if (timeMillis == null || "".equals(timeMillis)) {
			timeMillis = "0";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = new Date(Long.valueOf(timeMillis));
		String mTime = simpleDateFormat.format(date);
		return mTime;
	}

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
			result = "昨天 " + sdfSecond.format(oldDate);
		} else if (differentials == 2) {
			result = "前天  " + sdfSecond.format(oldDate);
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

	@SuppressLint("SimpleDateFormat")
	public static String formatHourMinute(long timeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date(timeMillis);
		String mTime = simpleDateFormat.format(date);
		return mTime;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDate(String month, String day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		java.util.Date d = new java.util.Date();
		String str = sdf.format(d);
		// String nowmonth = str.substring(5, 7);
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

	@SuppressLint("SimpleDateFormat")
	public static String getTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = null;
		try {
			// java.util.Date currentdate = new java.util.Date();// 当前时间

			// long i = (currentdate.getTime() / 1000 - timestamp) / (60);
			// Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系统当前时间

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
}