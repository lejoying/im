package com.lejoying.mc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.lejoying.mc.data.App;
import com.lejoying.utils.HttpTools;
import com.lejoying.utils.HttpTools.HttpListener;
import com.lejoying.utils.StreamTools;

public class MCNetTools {

	static App app = App.getInstance();

	private static Toast toast;

	public static class Settings {
		public String url = null;
		public int method = HttpTools.SEND_POST;
		public int timeout = 5000;
		public Bundle params = null;
	};

	public static void ajaxAPI(final AjaxInterface ajaxInterface) {

		Settings settings = new Settings();
		ajaxInterface.setParams(settings);

		ajax(null, settings.url, settings.params, settings.method, settings.timeout, new ResponseListener() {
			public void success(JSONObject data) {
				ajaxInterface.onSuccess(data);
			}

			public void noInternet() {
				Toast.makeText(app.context, "没有网络连接，网络不给力呀~", Toast.LENGTH_SHORT).show();
			}

			public void failed() {
				ajaxInterface.failed();
				Toast.makeText(app.context, "网络连接失败，网络不给力呀~", Toast.LENGTH_SHORT).show();
			}

			public void connectionCreated(HttpURLConnection httpURLConnection) {
				// TODO Auto-generated method stub
			}
		});

	}

	public interface AjaxInterface {
		public void setParams(Settings settings);

		public void onSuccess(JSONObject data);

		public void failed();

	}

	// public interface AjaxInterfaceAdvanced {
	// public void failed();
	// public void connectionCreated();
	// public void noInternet();
	// }

	public static void ajax(final Context context, final String url, final Bundle params, final int method, final int timeout, final ResponseListener responseListener) {
		// boolean hasNetwork = HttpTools.hasNetwork(context);
		if (app.networkStatus == "none") {
			NetworkInfo networkInfo = HttpTools.getActiveNetworkInfo(app.context);
			if (networkInfo != null) {
				app.networkStatus = networkInfo.getTypeName();
			}
		}
		if (app.networkStatus == "none") {
			responseListener.noInternet();
		} else {
			new Thread() {
				private byte[] b = null;

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {
						@Override
						public void handleInputStream(InputStream is) {
							b = StreamTools.isToData(is);
						}

						@Override
						public void connectionCreated(final HttpURLConnection httpURLConnection) {
							responseListener.connectionCreated(httpURLConnection);
						}

						@Override
						public void failed() {
							responseListener.failed();
						}
					};
					if (method == HttpTools.SEND_GET) {
						HttpTools.sendGetUseBundle(app.config.DOMAIN + url, timeout, params, httpListener);
					}
					if (method == HttpTools.SEND_POST) {
						HttpTools.sendPostUseBundle(app.config.DOMAIN + url, timeout, params, httpListener);
					}
					try {
						if (b == null) {

						} else {
							final JSONObject data = new JSONObject(new String(b));
							if (data != null) {
								responseListener.success(data);
							}
						}
					} catch (JSONException e) {
					}
				}
			}.start();
		}
	}

	public static void downloadFile(final Context context, final String location, final String fileName, final File savePath, final String rename, final int timeout, final DownloadListener downloadListener) {
		if (context == null) {
			return;
		}
		boolean hasNetwork = HttpTools.hasNetwork(context);
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
						public void connectionCreated(final HttpURLConnection httpURLConnection) {
							downloadListener.connectionCreated(httpURLConnection);
							fileLength = httpURLConnection.getContentLength();
						}

						@Override
						public void handleInputStream(final InputStream is) {
							if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
								downloadListener.success(null, is);
								return;
							}
							FileOutputStream fileOutputStream = null;
							String localFileName = fileName;
							if (rename != null && !rename.equals("")) {
								localFileName = rename;
							}
							final File file = new File(savePath, localFileName);
							try {
								fileOutputStream = new FileOutputStream(file);
								int length = 0;
								byte[] buffer = new byte[1024];
								downloadListener.downloading(0);

								float nowReadLength = 0;
								while ((length = is.read(buffer)) > 0) {
									fileOutputStream.write(buffer, 0, length);
									nowReadLength += length;
									downloadListener.downloading((int) (nowReadLength / fileLength * 100));
								}
								fileOutputStream.flush();
								downloadListener.success(file, null);

							} catch (FileNotFoundException e) {
							} catch (IOException e) {
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
							}
						}
						@Override
						public void failed() {
							downloadListener.failed();
						}
					};
					HttpTools.sendGetUseBundle(location + fileName, timeout, null, httpListener);
				}
			}.start();
		}
	}


	public interface ResponseListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void success(JSONObject data);

		public void failed();
	}

	public interface DownloadListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void downloading(int progress);

		public void success(File localFile, InputStream inputStream);

		public void failed();
	}

}
