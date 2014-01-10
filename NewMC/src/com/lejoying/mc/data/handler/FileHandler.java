package com.lejoying.mc.data.handler;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.DownloadListener;

public class FileHandler {
	App app;

	public void initailize(App app) {
		this.app = app;
	}

	public void getFile(String fileName) {
		// get from web
		// get from sdcard
		// get from mem directly

		// save to sdcard
		// save to mem

	}

	public String FROM_SDCARD = "sdcard";
	public String FROM_MEMORY = "memory";
	public String FROM_WEB = "web";
	public String FROM_DEFAULT = "default";

	public Map<String, Bitmap> bitmaps = new Hashtable<String, Bitmap>();
	// TODO to be managed by native code
	public Bitmap defaultImage;
	public Bitmap defaultHead;

	// TODO sd_card space checking
	// TODO zip images in the sd_card
	public void getImageFile(String imageFileName, FileResult fileResult) {
		// get from mem directly
		// get from sdcard

		// get from web

		// save to sdcard
		// save to mem
		if (defaultImage == null) {
			defaultImage = BitmapFactory.decodeResource(
					app.context.getResources(), R.drawable.defaultimage);
		}
		if (defaultHead == null) {
			defaultHead = MCImageTools.getCircleBitmap(BitmapFactory
					.decodeResource(app.context.getResources(),
							R.drawable.face_man), true, 5, Color.WHITE);
		}
		String where = FROM_DEFAULT;
		if (bitmaps.get(imageFileName) != null
				&& !bitmaps.get(imageFileName).equals(defaultImage)) {
			// todo return bitmaps.get(imageFileName) ;
			where = FROM_MEMORY;
		} else {
			if (!imageFileName.equals("")) {
				final File imageFile = new File(app.sdcardImageFolder,
						imageFileName);
				if (imageFile.exists()) {
					Bitmap image = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					if (image != null) {
						bitmaps.put(imageFileName, image);
					} else {
						bitmaps.put(imageFileName, defaultImage);
					}
					where = FROM_SDCARD;
				} else {
					if (getImageFromWebStatus.get(imageFileName) == null) {
						// bitmaps.put(imageFileName, defaultImage);
						getImageFileFromWeb(imageFileName, fileResult);
					}
				}
			} else {
				bitmaps.put(imageFileName, defaultImage);
			}
		}
		fileResult.onResult(where);

	}

	// key:imageFilename value:"loading"|"success"|"failed"
	Map<String, String> getImageFromWebStatus = new Hashtable<String, String>();

	private void getImageFileFromWeb(final String imageFileName,
			final FileResult fileResult) {
		getImageFromWebStatus.put(imageFileName, "loading");
		MCNetTools.downloadFile(app.context, app.config.DOMAIN_IMAGE,
				imageFileName, app.sdcardImageFolder, null, 5000,
				new DownloadListener() {
					@Override
					public void success(File localFile, InputStream inputStream) {
						getImageFromWebStatus.put(imageFileName, "success");
						Bitmap bitmap = BitmapFactory.decodeFile(localFile
								.getAbsolutePath());
						bitmaps.put(imageFileName, bitmap);
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								fileResult.onResult(FROM_WEB);
							}
						});
					}

					public void noInternet() {
						getImageFromWebStatus.put(imageFileName, "failed");
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(app.context, "没有网络连接，网络不给力呀~",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					public void failed() {
						getImageFromWebStatus.put(imageFileName, "success");
						app.mUIThreadHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(app.context, "网络连接失败，网络不给力呀~",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO to be resolved.

					}

					@Override
					public void downloading(int progress) {
						// TODO to be resolved.
					}
				});

	}

	public interface FileResult {
		public void onResult(String where);
	}
}
