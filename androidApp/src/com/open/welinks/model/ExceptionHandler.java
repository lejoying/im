package com.open.welinks.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.open.lib.MyLog;
import com.open.welinks.service.ExceptionService;

public class ExceptionHandler implements UncaughtExceptionHandler {
	public String tag = "ExceptionHandler";
	public MyLog log = new MyLog(tag, true);

	private static ExceptionHandler exceptionHandler;
	private Context context;
	private ExceptionService service;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private ExceptionHandler() {

	}

	public static synchronized ExceptionHandler getInstance() {
		if (exceptionHandler != null) {
			return exceptionHandler;
		} else {
			exceptionHandler = new ExceptionHandler();
			return exceptionHandler;
		}
	}

	public void init(Context context, ExceptionService service) {
		this.context = context;
		this.service = service;
	}

	public Data data = Data.getInstance();

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		log.e("程序挂掉了 ");
		// 1.获取当前程序的版本号. 版本的id
		String versioninfo = getVersionInfo();

		// 2.获取手机的硬件信息.
		String mobileInfo = getMobileInfo();

		// 3.把错误的堆栈信息 获取出来
		String errorinfo = getErrorInfo(arg1);

		// 4.把所有的信息 还有信息对应的时间 提交到服务器
		try {
			log.e(errorinfo);
			if (!"NONE".equals(data.localStatus.sendBug)) {
				service.sendContent("Data：\n" + dataFormat.format(new Date()), "版本信息：\n" + versioninfo + "\n手机信息：\n" + mobileInfo, "bug内容：\n" + errorinfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 干掉当前的程序
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, 1 * 1000);
	}

	public void kill() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	// ************************Exception System*************************
	public static String printStackTrace(Context context, Exception e) {
		StackTraceElement[] parentStack = null;
		String indent = "";
		StringBuffer err = new StringBuffer();
		err.append(toString2(e, context));
		err.append("\n");
		StackTraceElement[] stack = e.getStackTrace();
		if (stack != null) {
			int duplicates = parentStack != null ? countDuplicates(stack, parentStack) : 0;
			for (int i = 0; i < stack.length - duplicates; i++) {
				err.append(indent);
				err.append("\tat ");
				err.append(stack[i].toString());
				err.append("\n");
			}

			if (duplicates > 0) {
				err.append(indent);
				err.append("\t... ");
				err.append(Integer.toString(duplicates));
				err.append(" more\n");
			}
		}

		Throwable cause = e.getCause();
		if (cause != null) {
			err.append(indent);
			err.append("Caused by: ");
			printStackTrace(context, e);
		}
		return err.toString();
	}

	public static String toString2(Exception e, Context context) {
		String msg = e.getLocalizedMessage();
		String name = context.getClass().getName();
		if (msg == null) {
			return name;
		}
		return name + ": " + msg;
	}

	private static int countDuplicates(StackTraceElement[] currentStack, StackTraceElement[] parentStack) {
		int duplicates = 0;
		int parentIndex = parentStack.length;
		for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
			StackTraceElement parentFrame = parentStack[parentIndex];
			if (parentFrame.equals(currentStack[i])) {
				duplicates++;
			} else {
				break;
			}
		}
		return duplicates;
	}
	// ****** **** **** *** ***** ********** ********* ************ ********* ************
}