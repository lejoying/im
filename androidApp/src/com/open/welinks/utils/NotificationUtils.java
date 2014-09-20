package com.open.welinks.utils;

import java.util.ArrayList;
import java.util.List;

import com.open.welinks.MainActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;

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

public final class NotificationUtils {
	public static final int DEFAULT_ALL = Notification.DEFAULT_ALL;
	public static final int DEFAULT_LIGHTS = Notification.DEFAULT_LIGHTS;
	public static final int DEFAULT_SOUND = Notification.DEFAULT_SOUND;
	public static final int DEFAULT_VIBRATE = Notification.DEFAULT_VIBRATE;

	public static final long[] VIBRATE_COMMON = new long[] { 0, 115, 55, 115 };
	private static NotificationManager mNotificationManager;
	private static Vibrator mVibrator;
	private static Data data;
	private static ActivityManager mActivityManager;

	private static List<Message> showMessages = new ArrayList<Message>();
	private static int friendCount;
	private static int messageCount;

	public static String showFragment = "";
	public static Message message;

	private static Vibrator getVibrator(Context context) {
		if (mVibrator == null) {
			mVibrator = (Vibrator) context.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		}
		return mVibrator;
	}

	private static NotificationManager getNotificationManager(Context context) {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
		}
		return mNotificationManager;
	}

	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context.getApplicationContext().getSystemService(Service.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	private static Data getData() {
		if (data == null) {
			data = Data.getInstance();
		}
		return data;
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

	@SuppressWarnings("deprecation")
	public static void showNotification(Context context, int notificationId, int icon, Uri sound, String tickerText, String contentTitle, String contentText, int notificationDefaults, int flags, Intent intent) {
		Notification notification = new Notification();
		notification.icon = icon;
		notification.sound = sound;
		notification.tickerText = tickerText;
		notification.defaults = notificationDefaults;
		notification.flags = flags;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		getNotificationManager(context).notify(notificationId, notification);
	}

	public static void cancelNotification(Context context, int notification) {
		getNotificationManager(context).cancel(notification);

		showMessages.clear();
		friendCount = 0;
		messageCount = 0;
	}

	public static void cancelNotification(Context context) {
		getNotificationManager(context).cancelAll();

		showMessages.clear();
		friendCount = 0;
		messageCount = 0;

		showFragment = "";
		message = null;
	}

	public static boolean isLeave(Context context) {
		boolean flag = false;
		List<RunningTaskInfo> runningTaskInfos = getActivityManager(context).getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			flag = !f.getPackageName().equals(context.getApplicationContext().getPackageName());
		}
		return flag;
	}

	public static final int NOTIFICATION_NEWMESSAGE = 1;

	private static void recordCount(Message message) {
		messageCount++;
		if (message.sendType.equals("point")) {
			for (Message msg : showMessages) {
				if (msg.sendType.equals(message.sendType) && msg.phone.equals(message.phone)) {
					return;
				}
			}
		} else if (message.sendType.equals("group") || message.sendType.equals("tempGroup")) {
			for (Message msg : showMessages) {
				if (msg.sendType.equals(message.sendType) && msg.gid.equals(message.gid)) {
					return;
				}
			}
		}
		friendCount++;
	}

	public static void showMessageNotification(Context context, Message message) {
		recordCount(message);
		showMessages.add(message);
		Uri sound = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.message);
		String tickerText = "";
		String contentTitle = "";
		String contentText;
		contentText = message.content.toString();
		if (message.contentType.equals("image")) {
			contentText = "[图片]";
		} else if (message.contentType.equals("voice")) {
			contentText = "[声音]";
		} else if (message.contentType.equals("share")) {
			contentText = "[分享]";
		}
		if (message.sendType.equals("point")) {
			String nickName = getData().relationship.friendsMap.get(message.phone).nickName;
			tickerText = nickName + ":" + contentText;
			contentTitle = nickName;
		} else if (message.sendType.equals("group") || message.sendType.equals("tempGroup")) {
			String groupName = getData().relationship.groupsMap.get(message.gid).name;
			tickerText = groupName + ":" + contentText;
			contentTitle = groupName;
		}
		Intent intent = new Intent(context, MainActivity.class);

		if (friendCount == 1 && messageCount == 1) {
			if (message.sendType.equals("point")) {
				showFragment = "chatFriend";
			} else if (message.sendType.equals("group")) {
				showFragment = "chatGroup";
			}
		} else if (friendCount == 1 && messageCount > 1) {
			contentText = "给您发来了" + messageCount + "条消息。";
			showFragment = "chatFriend";
		} else if (friendCount > 1 && messageCount > 1) {
			showFragment = "chatList";
			contentTitle = "微型公社";
			contentText = "有" + friendCount + "个好友给您发来了" + messageCount + "条消息。";
		}
		NotificationUtils.message = message;
		showNotification(context, NOTIFICATION_NEWMESSAGE, R.drawable.notifyicon, sound, tickerText, contentTitle, contentText, DEFAULT_LIGHTS, Notification.FLAG_NO_CLEAR, intent);
		commonVibrate(context);
	}

}
