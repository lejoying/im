package com.lejoying.mc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import com.lejoying.mc.data.App;
import com.lejoying.utils.HttpUtils;
import com.lejoying.utils.HttpUtils.HttpListener;
import com.lejoying.utils.StreamUtils;

public class MCNetUtils {

	static App app = App.getInstance();

	public static class Settings {
		public String url = null;
		public int method = HttpUtils.SEND_POST;
		public int timeout = 5000;
		public Bundle params = null;
	};

	public interface AjaxInterface {
		public void setParams(Settings settings);

		public void onSuccess(JSONObject jData);

		public void failed();

		public void noInternet();

		public void timeout();

		public void connectionCreated(HttpURLConnection httpURLConnection);
	}

	public static void ajax(final AjaxInterface ajaxInterface) {

		final Settings settings = new Settings();
		final long startTime = new Date().getTime();
		ajaxInterface.setParams(settings);
		if (app.networkStatus == "none") {
			NetworkInfo networkInfo = HttpUtils
					.getActiveNetworkInfo(app.context);
			if (networkInfo != null) {
				app.networkStatus = networkInfo.getTypeName();
			}
		}
		if (app.networkStatus == "none") {
			ajaxInterface.noInternet();
		} else {
			new Thread() {
				private byte[] b = null;

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {
						@Override
						public void handleInputStream(InputStream is) {
							b = StreamUtils.getByteArrayFromInputStream(is);
						}

						@Override
						public void connectionCreated(
								final HttpURLConnection httpURLConnection) {
							ajaxInterface.connectionCreated(httpURLConnection);
						}
					};
					if (settings.method == HttpUtils.SEND_GET) {
						HttpUtils.sendGetUseBundle(app.config.DOMAIN
								+ settings.url, settings.timeout,
								settings.params, httpListener);
					}
					if (settings.method == HttpUtils.SEND_POST) {
						HttpUtils.sendPostUseBundle(app.config.DOMAIN
								+ settings.url, settings.timeout,
								settings.params, httpListener);
					}
					try {
						if (b == null) {
							long endTime = new Date().getTime();
							if (endTime - startTime < settings.timeout) {
								ajaxInterface.failed();
							} else {
								ajaxInterface.timeout();
							}
						} else {
							final JSONObject jData = new JSONObject(new String(
									b));
							if (jData != null) {
								ajaxInterface.onSuccess(jData);
							}
						}
					} catch (JSONException e) {
					}
				}
			}.start();
		}
	}

	public static void downloadFile(final Context context,
			final String location, final String fileName, final File savePath,
			final String rename, final int timeout,
			final DownloadListener downloadListener) {
		if (context == null) {
			return;
		}
		boolean hasNetwork = HttpUtils.hasNetwork(context);
		if (!hasNetwork) {
			downloadListener.noInternet();
		} else {
			new Thread() {

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {
						float fileLength = 0;

						@Override
						public void connectionCreated(
								final HttpURLConnection httpURLConnection) {
							downloadListener
									.connectionCreated(httpURLConnection);
							fileLength = httpURLConnection.getContentLength();
						}

						@Override
						public void handleInputStream(final InputStream is) {
							if (is == null) {
								downloadListener.failed();
								return;
							}
							if (!Environment.getExternalStorageState().equals(
									Environment.MEDIA_MOUNTED)) {
								downloadListener.success(null, is);
								return;
							}
							FileOutputStream fileOutputStream = null;
							String localFileName = fileName;
							if (rename != null && !rename.equals("")) {
								localFileName = rename;
							}
							File file = new File(savePath, localFileName
									+ ".tmp");
							try {
								fileOutputStream = new FileOutputStream(file);
								int length = 0;
								byte[] buffer = new byte[1024];
								downloadListener.downloading(0);

								float nowReadLength = 0;
								while ((length = is.read(buffer)) > 0) {
									fileOutputStream.write(buffer, 0, length);
									nowReadLength += length;
									downloadListener
											.downloading((int) (nowReadLength
													/ fileLength * 100));
								}
								fileOutputStream.flush();
								File localFile = new File(savePath,
										localFileName);
								file.renameTo(localFile);
								downloadListener.success(localFile, null);
							} catch (FileNotFoundException e) {
								downloadListener.failed();
							} catch (IOException e) {
								downloadListener.failed();
							} finally {
								if (fileOutputStream != null) {
									try {
										fileOutputStream.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if (is != null) {
									try {
										is.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								File tempFile = new File(savePath,
										localFileName + ".tmp");
								if (tempFile.exists()) {
									tempFile.delete();
								}
							}
						}
					};
					HttpUtils.sendGetUseBundle(location + fileName, timeout,
							null, httpListener);
				}
			}.start();
		}
	}

	public interface DownloadListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void downloading(int progress);

		public void success(File localFile, InputStream inputStream);

		public void failed();
	}

}
