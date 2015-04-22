package com.open.welinks.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.utils.StreamParser;

public class CrashHandler implements UncaughtExceptionHandler {

	/** Debug Log tag */
	public static final String LOGTAG = "CrashHandler";
	public MyLog log = new MyLog(LOGTAG, true);
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = false;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".dat";

	/** SD卡错误报告文件路径 */
	private static String SDCARD_PATH = "/sdcard/data/welinks/";

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
		initAddress();
	}

	private void initAddress() {
		SDCARD_PATH = Environment.getExternalStorageDirectory() + "/welinks/";// +"/data/sxc/";
		log.e(LOGTAG, "SDCARD_PATH>>" + SDCARD_PATH);
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// Log.e(LOGTAG, "Error : ", e);
			// }
//			Timer timer = new Timer();
//			timer.schedule(new TimerTask() {
//
//				@Override
//				public void run() {
//					android.os.Process.killProcess(android.os.Process.myPid());
//					System.exit(100);
//				}
//			}, 1 * 1000);
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(100);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			Log.w(LOGTAG, "handleException --- ex==null");
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		if (msg == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		// new Thread() {
		// @Override
		// public void run() {
		// Looper.prepare();
		//
		// log.e("error", msg);
		//
		// // Toast toast = Toast.makeText(mContext, "程序出错，即将退出:\r\n" + msg,
		// // Toast.LENGTH_LONG);
		// // toast.setGravity(Gravity.CENTER, 0, 0);
		// // toast.show();
		//
		// // MsgPrompt.showMsg(mContext, "程序出错啦", msg+"\n点确认退出");
		// Looper.loop();
		//
		// }
		// }.start();
		// 收集设备信息
		collectCrashDeviceInfo(mContext);
		// 保存错误报告文件
		saveCrashInfoToFile(ex);
		// 发送错误报告到服务器
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		// sendCrashReportsToServer(mContext);
		SendCrashLog sendLog = new SendCrashLog();
		sendLog.execute("ex");
		// }
		// }).start();
		log.e("--------finish");
		return true;
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		log.e("sendCrashReportsToServer");
		String[] crFiles = getCrashReportFiles(ctx);
		log.e("****" + crFiles.length);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));
			for (String fileName : sortedFiles) {
				File cr = new File(SDCARD_PATH, fileName);
				postReport(cr);
				cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		// TODO 发送错误报告到服务器
		String result = new String(StreamParser.parseToByteArray(file));
		log.e(result);
		sendMessageToServer("", "", result);
	}

	public class SendCrashLog extends AsyncTask<String, String, Boolean> {
		public SendCrashLog() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				sendPreviousReportsToServer();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Log.d("aaa", "Device model sent.");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

		}
	}

	void sendMessageToServer(String time, String info, String bug) {

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("time", time);
		params.addBodyParameter("info", info);
		params.addBodyParameter("bug", bug);
		HttpClient httpClient = new HttpClient();
		log.e("----");
		httpUtils.send(HttpMethod.POST, API.BUG_SEND, params, httpClient.new ResponseHandler<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				super.onSuccess(responseInfo);
				log.e("发送异常成功");
				Toast.makeText(mContext, "aaa", Toast.LENGTH_LONG).show();
				// handler.kill();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				super.onFailure(error, msg);
				log.e("发送异常失败" + msg);
				Toast.makeText(mContext, "bbb", Toast.LENGTH_LONG).show();
				// handler.kill();
			}
		});
		// handler.kill();
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		// File filesDir = ctx.getFilesDir();
		File filesDir = new File(SDCARD_PATH);
		// log.e(filesDir.getAbsolutePath() + "---");
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.put("EXEPTION", ex.getLocalizedMessage());
		mDeviceCrashInfo.put(STACK_TRACE, getErrorInfo(ex));
		try {
			// long timestamp = System.currentTimeMillis();

			Time t = new Time("GMT+8");
			t.setToNow(); // 取得系统时间
			int date = t.year * 10000 + t.month * 100 + t.monthDay;
			int time = t.hour * 10000 + t.minute * 100 + t.second;
			String fileName = "crash-" + date + "-" + time + CRASH_REPORTER_EXTENSION;
			saveConfig(mContext, fileName, mDeviceCrashInfo);
			// FileOutputStream trace = mContext.openFileOutput(fileName,
			// Context.MODE_PRIVATE);
			// mDeviceCrashInfo.store(trace, "");
			// trace.flush();
			// trace.close();
			return fileName;
		} catch (Exception e) {
			Log.e(LOGTAG, "an error occured while writing report file...", e);
		}
		return null;
	}

	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * 保存错误信息
	 * 
	 * @param context
	 * @param file
	 * @param properties
	 * @throws Exception
	 */
	private void saveConfig(Context context, String file, Properties properties) throws Exception {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = SDCARD_PATH + file;
			FileOutputStream trace = new FileOutputStream(file, false);
			properties.store(trace, "");
			trace.flush();
			trace.close();
		} else {
			FileOutputStream trace = context.openFileOutput(file, Context.MODE_PRIVATE);
			properties.store(trace, "");
			trace.flush();
			trace.close();
		}
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(LOGTAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
				if (DEBUG) {
					Log.d(LOGTAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(LOGTAG, "Error while collect crash info", e);
			}
		}
	}

}
