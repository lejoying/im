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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCHttpTools.HttpListener;
import com.lejoying.utils.StreamTools;

public class MCNetTools {

	static App app = App.getInstance();

	public static Handler handler = new Handler();

	private static Toast toast;

	public static void ajax(final Context context, final String url,
			final Bundle params, final int method, final int timeout,
			final ResponseListener responseListener) {
		if (context == null) {
			return;
		}
		boolean hasNetwork = MCHttpTools.hasNetwork(context);

		if (!hasNetwork) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					responseListener.noInternet();
				}
			});
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
						public void connectionCreated(
								final HttpURLConnection httpURLConnection) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									responseListener
											.connectionCreated(httpURLConnection);
								}
							});
						}
					};
					if (method == MCHttpTools.SEND_GET) {
						MCHttpTools.sendGet(app.config.DOMAIN + url, timeout,
								params, httpListener);
					}
					if (method == MCHttpTools.SEND_POST) {
						MCHttpTools.sendPost(app.config.DOMAIN + url, timeout,
								params, httpListener);
					}
					try {
						if (b == null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									responseListener.failed();
								}
							});
						} else {
							final JSONObject data = new JSONObject(
									new String(b));
							if (data != null) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										responseListener.success(data);
									}
								});
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.post(new Runnable() {
							@Override
							public void run() {
								responseListener.failed();
							}
						});
					}
				}
			}.start();
		}
	}

	public static void getImage(final Context context,
			final String imageFileName, final int timeout,
			final ImageResponseListener imageResponseListener) {
		if (context == null) {
			return;
		}
		boolean hasNetwork = MCHttpTools.hasNetwork(context);
		if (!hasNetwork) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					imageResponseListener.noInternet();
				}
			});
		} else {
			new Thread() {

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {

						@Override
						public void handleInputStream(InputStream is) {
							FileOutputStream fileOutputStream = null;
							File imageFile = new File(app.sdcardImageFolder,
									imageFileName);
							try {
								fileOutputStream = new FileOutputStream(
										imageFile);
								int length = 0;
								byte[] buffer = new byte[1024];
								while ((length = is.read(buffer)) > 0) {
									fileOutputStream.write(buffer, 0, length);
								}
								fileOutputStream.flush();
							} catch (FileNotFoundException e) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										imageResponseListener.failed();
									}
								});
								e.printStackTrace();
							} catch (IOException e) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										imageResponseListener.failed();
									}
								});
								e.printStackTrace();
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
							final Bitmap bitmap = BitmapFactory
									.decodeFile(imageFile.getAbsolutePath());
							if (bitmap == null) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										imageResponseListener.failed();
									}
								});
							} else {
								handler.post(new Runnable() {
									@Override
									public void run() {
										imageResponseListener.success(bitmap);
									}
								});
							}
						}

						@Override
						public void connectionCreated(
								final HttpURLConnection httpURLConnection) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									imageResponseListener
											.connectionCreated(httpURLConnection);
								}
							});
						}
					};
					MCHttpTools.sendGet(
							app.config.DOMAIN_IMAGE + imageFileName, timeout,
							null, httpListener);
				}
			}.start();
		}
	}

	public static void showMsg(final Context context, final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (text == null || text.equals("")) {
					return;
				}
				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

	public static void cleanMsg() {
		if (toast != null) {
			toast.cancel();
		}
	}

	public interface ResponseListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void success(JSONObject data);

		public void failed();
	}

	public interface ImageResponseListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void success(Bitmap bitmap);

		public void failed();
	}

}
