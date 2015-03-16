package com.open.welinks.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.utils.MyGson;

public class UpdateManager {
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	// private int progress;
	private boolean cancelUpdate = false;

	private Context mContext;
	// private ProgressBar mProgress;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				// mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public class VersionClass {
		public String version;
		public String name;
		public String url;
	}

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	public void checkUpdate() {
		try {
			sendMessageToServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	HttpClient httpClient = new HttpClient();
	public MyGson gson = new MyGson();
	public VersionClass versionClass;

	public void sendMessageToServer() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		httpUtils.send(HttpMethod.GET, API.API_DOMAIN + "version", params, httpClient.new ResponseHandler<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				super.onSuccess(responseInfo);
				int versionCode = getVersionCode(mContext);
				versionClass = gson.fromJson(responseInfo.result, VersionClass.class);
				if (null != versionClass) {
					int serviceCode = Integer.valueOf(versionClass.version);
					if (serviceCode > versionCode) {
						showNoticeDialog();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				super.onFailure(error, msg);
			}
		});
	}

	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo("com.open.welinks", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	private void showNoticeDialog() {
		Alert.createDialog(mContext).setTitle("检查到新版本,立即更新吗？").setOnConfirmClickListener(new AlertInputDialog.OnDialogClickListener() {
			@Override
			public void onClick(AlertInputDialog dialog) {
				downloadApk();
			}
		}).setLeftButtonText("更新").setRightButtonText("稍后更新").show();
	}

	private void downloadApk() {
		new downloadApkThread().start();
	}

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					URL url = new URL(versionClass.url);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = taskManageHolder.fileHandler.sdcardFolder;
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(taskManageHolder.fileHandler.sdcardFolder, versionClass.name);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					do {
						int numread = is.read(buf);
						count += numread;
						// progress = (int) (((float) count / length) * 100);
						Log.e("APK", (int) (((float) count / length) * 100) + "");
						// 更新进度
						// mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			// mDownloadDialog.dismiss();
		}
	};

	private void installApk() {
		File apkfile = new File(taskManageHolder.fileHandler.sdcardFolder, versionClass.name);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
