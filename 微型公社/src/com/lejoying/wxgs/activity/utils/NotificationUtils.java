package com.lejoying.wxgs.activity.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Message;

public final class NotificationUtils {
	public static final int DEFAULT_ALL = Notification.DEFAULT_ALL;
	public static final int DEFAULT_LIGHTS = Notification.DEFAULT_LIGHTS;
	public static final int DEFAULT_SOUND = Notification.DEFAULT_SOUND;
	public static final int DEFAULT_VIBRATE = Notification.DEFAULT_VIBRATE;

	public static final long[] VIBRATE_COMMON = new long[] { 0, 115, 55, 115 };
	private static NotificationManager mNotificationManager;
	private static Vibrator mVibrator;
	private static MainApplication mApp;
	private static ActivityManager mActivityManager;

	private static Vibrator getVibrator(Context context) {
		if (mVibrator == null) {
			mVibrator = (Vibrator) context.getApplicationContext()
					.getSystemService(Service.VIBRATOR_SERVICE);
		}
		return mVibrator;
	}

	private static NotificationManager getNotificationManager(Context context) {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) context
					.getApplicationContext().getSystemService(
							Service.NOTIFICATION_SERVICE);
		}
		return mNotificationManager;
	}

	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getApplicationContext().getSystemService(
							Service.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	private static MainApplication getMainApplication() {
		if (mApp == null) {
			mApp = MainApplication.getMainApplication();
		}
		return mApp;
	}

	public static void vibrate(Context context, long milliseconds) {
		getVibrator(context).vibrate(milliseconds);
	}

	public static void vibrate(Context context, long[] pattern, boolean isRepeat) {
		getVibrator(context).vibrate(pattern, isRepeat ? 1 : -1);
	}

	public static void commonVibrate(Context context) {
		vibrate(context, VIBRATE_COMMON, false);
	}

	public static void showNotification(Context context, int notificationId,
			int icon, Uri sound, String tickerText, String contentTitle,
			String contentText, int notificationDefaults, int flags,
			Intent intent) {
		Notification notification = new Notification();
		notification.icon = icon;
		notification.sound = sound;
		notification.tickerText = tickerText;
		notification.defaults = notificationDefaults;
		notification.flags = flags;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		getNotificationManager(context).notify(notificationId, notification);
	}

	public static void cancelNotification(Context context, int notification) {
		getNotificationManager(context).cancel(notification);
	}

	public static void cancelNotification(Context context) {
		getNotificationManager(context).cancelAll();
	}

	public static boolean isLeave(Context context) {
		boolean flag = false;
		List<RunningTaskInfo> runningTaskInfos = getActivityManager(context)
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			flag = !f.getPackageName().equals(
					context.getApplicationContext().getPackageName());
		}
		return flag;
	}

	// Only applies to MicroCommune

	public static final int NOTIFICATION_NEWMESSAGE = 1;

	public static void showMessageNotification(Context context, Message message) {
		Uri sound = Uri.parse("android.resource://"
				+ context.getApplicationContext().getPackageName() + "/"
				+ R.raw.message);
		String tickerText = "";
		String contentTitle = "";
		String contentText = message.content;
		if (message.contentType.equals("image")) {
			contentText = "[图片]";
		} else if (message.contentType.equals("voice")) {
			contentText = "[声音]";
		}
		if (message.sendType.equals("point")) {
			String nickName = getMainApplication().data.friends
					.get(message.phone).nickName;
			tickerText = nickName + ":" + contentText;
			contentTitle = nickName;
		} else if (message.sendType.equals("group")
				|| message.sendType.equals("tempGroup")) {
			String groupName = getMainApplication().data.groupsMap
					.get(message.gid).name;
			tickerText = groupName + ":" + contentText;
			contentTitle = groupName;
		}
		Intent intent = new Intent(context, MainActivity.class);
		showNotification(context, NOTIFICATION_NEWMESSAGE, R.drawable.icon,
				sound, tickerText, contentTitle, contentText, DEFAULT_LIGHTS
						| DEFAULT_SOUND, Notification.FLAG_NO_CLEAR, intent);
		commonVibrate(context);
	}

}
