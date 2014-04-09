package com.lejoying.wxgs.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.Base64;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;

@SuppressLint("DefaultLocale")
public class FileHandler {
	MainApplication app;

	public void initialize(MainApplication app) {
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
	public Map<String, GifMovie> gifs = new Hashtable<String, GifMovie>();
	public Map<String, Bitmap> bitmaps = new Hashtable<String, Bitmap>();
	// TODO to be managed by native code
	public Bitmap defaultImage;
	public Bitmap defaultHead;

	public final int TYPE_IMAGE_COMMON = 0x01;
	public final int TYPE_IMAGE_HEAD = 0x02;

	public void getImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, TYPE_IMAGE_COMMON, fileResult);
	}

	public void getHeadImage(String imageFileName, FileResult fileResult) {
		getImageFile(imageFileName, TYPE_IMAGE_HEAD, fileResult);
	}

	// TODO sd_card space checking
	// TODO zip images in the sd_card
	public void getImageFile(String imageFileName, int type,
			FileResult fileResult) {
		// get from mem directly
		// get from sdcard

		// get from web

		// save to sdcard
		// save to mem
		if (defaultImage == null) {
			defaultImage = BitmapFactory.decodeResource(app.getResources(),
					R.drawable.defaultimage);
		}
		if (defaultHead == null) {
			defaultHead = MCImageUtils.getCircleBitmap(BitmapFactory
					.decodeResource(app.getResources(), R.drawable.face_man),
					true, 5, Color.WHITE);
		}
		Bitmap dImage = defaultImage;
		if (type == TYPE_IMAGE_HEAD) {
			dImage = defaultHead;
		}
		String where = FROM_DEFAULT;
		if (bitmaps.get(imageFileName) != null
				&& !bitmaps.get(imageFileName).equals(defaultImage)) {
			// todo return bitmaps.get(imageFileName) ;
			where = FROM_MEMORY;
		} else {
			if (bitmaps.get(imageFileName) == null) {
				bitmaps.put(imageFileName, dImage);
			}
			if (!imageFileName.equals("")) {
				File imageFile = new File(app.sdcardImageFolder, imageFileName);
				if (type == TYPE_IMAGE_HEAD) {
					imageFile = new File(app.sdcardHeadImageFolder,
							imageFileName);
				}
				if (imageFile.exists()) {
					Bitmap image = BitmapFactory.decodeFile(imageFile
							.getAbsolutePath());
					if (image != null) {
						if (type == TYPE_IMAGE_COMMON) {
							bitmaps.put(imageFileName, image);
						} else if (type == TYPE_IMAGE_HEAD) {
							bitmaps.put(imageFileName, MCImageUtils
									.getCircleBitmap(image, true, 5,
											Color.WHITE));
						}
						where = FROM_SDCARD;
					} else {
						if (getFromWebResults.get(imageFileName) == null) {
							getFromWebResults.put(imageFileName,
									new ArrayList<FileHandler.FileResult>());
						}
						getFromWebResults.get(imageFileName).add(fileResult);
						if (getImageFromWebStatus.get(imageFileName) == null
								|| getImageFromWebStatus.get(imageFileName)
										.equals("failed")) {
							getImageFileFromWeb(imageFileName, type);
						}
					}
				} else {
					if (getFromWebResults.get(imageFileName) == null) {
						getFromWebResults.put(imageFileName,
								new ArrayList<FileHandler.FileResult>());
					}
					getFromWebResults.get(imageFileName).add(fileResult);
					if (getImageFromWebStatus.get(imageFileName) == null
							|| getImageFromWebStatus.get(imageFileName).equals(
									"failed")) {
						getImageFileFromWeb(imageFileName, type);
					}
				}
			}
		}
		fileResult.onResult(where);
	}

	// key:imageFilename value:"loading"|"success"|"failed"|"error"
	Map<String, String> getImageFromWebStatus = new Hashtable<String, String>();
	Map<String, List<FileResult>> getFromWebResults = new HashMap<String, List<FileResult>>();

	private void getImageFileFromWeb(final String imageFileName, final int type) {
		getImageFromWebStatus.put(imageFileName, "loading");
		File folder = app.sdcardImageFolder;
		if (type == TYPE_IMAGE_HEAD) {
			folder = app.sdcardHeadImageFolder;
		}
		final File f = folder;
		app.networkHandler.connection(new NetConnection() {

			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				try {
					StreamParser.parseToFile(is, new FileOutputStream(new File(
							f, imageFileName)));
					httpURLConnection.disconnect();
					Bitmap bitmap = BitmapFactory.decodeFile(new File(f,
							imageFileName).getAbsolutePath());
					if (type == TYPE_IMAGE_HEAD) {
						bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5,
								Color.WHITE);
					}
					bitmaps.put(imageFileName, bitmap);

					for (final FileResult result : getFromWebResults
							.get(imageFileName)) {
						app.UIHandler.post(new Runnable() {
							@Override
							public void run() {
								result.onResult(FROM_WEB);
							}
						});
					}
				} catch (FileNotFoundException e) {
					if (getImageFromWebStatus.get(imageFileName).equals(
							"failed")) {
						getImageFromWebStatus.put(imageFileName, "error");
					}
					getImageFromWebStatus.put(imageFileName, "failed");
				}
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				if (getImageFromWebStatus.get(imageFileName).equals("failed")) {
					getImageFromWebStatus.put(imageFileName, "error");
				}
				getImageFromWebStatus.put(imageFileName, "failed");
				super.failed(failedType, responseCode);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN_IMAGE + imageFileName;
				settings.method = GET;
			}
		});

	}

	public void saveBitmap(final SaveBitmapInterface saveBitmapInterface) {
		final SaveSettings settings = new SaveSettings();
		saveBitmapInterface.setParams(settings);
		final Bitmap source = settings.source;
		if (source != null) {
			new Thread() {
				public void run() {
					byte[] imageByteArray = MCImageUtils
							.getByteArrayFromBitmap(source,
									settings.compressFormat, 100);
					if (imageByteArray != null) {
						String base64 = Base64.encodeToString(imageByteArray,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.mSHA1.getDigestOfString(base64
								.getBytes());
						sha1 = sha1.toLowerCase(Locale.getDefault());
						String format = settings.compressFormat == settings.PNG ? ".png"
								: ".jpg";
						File localFile = new File(settings.folder, sha1
								+ format);
						if (!localFile.exists()) {
							FileOutputStream fileOutputStream = null;
							try {
								fileOutputStream = new FileOutputStream(
										localFile);
								fileOutputStream.write(imageByteArray);
								fileOutputStream.flush();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
							}
						}
						saveBitmapInterface.onSuccess(sha1 + format, base64);
					}
				}
			}.start();

		}
	}

	public void saveBitmapAndUpload(SaveBitmapInterface saveBitmapInterface) {

	}

	public class SaveSettings {
		public Bitmap.CompressFormat PNG = Bitmap.CompressFormat.PNG;
		public Bitmap.CompressFormat JPG = Bitmap.CompressFormat.JPEG;
		public Bitmap source;
		public File folder = app.sdcardImageFolder;
		public Bitmap.CompressFormat compressFormat = JPG;
	}

	public interface SaveBitmapInterface {

		public void setParams(SaveSettings settings);

		public void onSuccess(String fileName, String base64);
	}

	public interface FileResult {
		public void onResult(String where);
	}

	@SuppressLint("DefaultLocale")
	public void getVoice(final VoiceInterface getVoiceInterface) {
		final VoiceSettings settings = new VoiceSettings();
		getVoiceInterface.setParams(settings);
		final File voiceFile = new File(settings.folder, settings.fileName);
		if (voiceFile.exists()) {
			new Thread() {
				@SuppressLint("DefaultLocale")
				public void run() {
					try {
						FileInputStream fis = null;
						fis = new FileInputStream(voiceFile);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = fis.read(buffer)) != -1) {
							bos.write(buffer, 0, len);
						}
						bos.flush();
						byte[] data = bos.toByteArray();
						bos.close();
						fis.close();
						String base64 = Base64.encodeToString(data,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.mSHA1.getDigestOfString(base64
								.getBytes());
						getVoiceInterface.onSuccess(
								(sha1 + settings.format).toLowerCase(), base64,
								null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	};

	@SuppressLint("DefaultLocale")
	public void saveVoice(final VoiceInterface saveVoiceInterface) {
		final VoiceSettings settings = new VoiceSettings();
		saveVoiceInterface.setParams(settings);
		File voiceFile = new File(settings.folder, settings.fileName);
		final File folder = settings.folder;
		final String voiceFileName = (settings.fileName).toLowerCase();
		if (!voiceFile.exists()) {
			new Thread() {
				@Override
				public void run() {
					app.networkHandler.connection(new NetConnection() {

						@Override
						protected void success(InputStream is,
								HttpURLConnection httpURLConnection) {
							try {
								StreamParser.parseToFile(is,
										new FileOutputStream(new File(folder,
												voiceFileName)));
								httpURLConnection.disconnect();
								saveVoiceInterface.onSuccess(null, null, true);
							} catch (FileNotFoundException e) {
								saveVoiceInterface.onSuccess(null, null, false);
								e.printStackTrace();
							}
						}

						@Override
						protected void failed(int failedType, int responseCode) {
							saveVoiceInterface.onSuccess(null, null, false);
							super.failed(failedType, responseCode);
						}

						@Override
						protected void settings(Settings settings) {
							settings.url = API.DOMAIN_IMAGE + voiceFileName;
							settings.method = GET;
						}
					});
				}
			}.start();
		} else {
			saveVoiceInterface.onSuccess(null, null, true);
		}
	}

	public class VoiceSettings {
		public String fileName;
		public String format;
		public File folder = app.sdcardVoiceFolder;
	}

	public interface VoiceInterface {
		public void setParams(VoiceSettings settings);

		public void onSuccess(String filename, String base64, Boolean flag);
	}

	@SuppressLint("DefaultLocale")
	public void getBigFaceImgBASE64(final Context context,
			final BigFaceImgInterface bigFaceImg) {
		final BigFaceImgSettings settings = new BigFaceImgSettings();
		bigFaceImg.setParams(settings);
		final String assetsPath = settings.assetsPath;
		if (assetsPath != "") {
			new Thread() {
				public void run() {
					try {
						InputStream is = context.getResources().getAssets()
								.open(assetsPath + settings.fileName);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							bos.write(buffer, 0, len);
						}
						bos.flush();
						bos.close();
						is.close();
						byte[] data = bos.toByteArray();
						String base64 = Base64.encodeToString(data,
								Base64.DEFAULT);
						base64 = base64.trim();
						String sha1 = app.mSHA1.getDigestOfString(base64
								.getBytes());
						File file = new File(settings.folder, sha1
								+ settings.format);
						if (!file.exists()) {
							InputStream is1 = context.getResources()
									.getAssets()
									.open(assetsPath + settings.fileName);
							FileOutputStream fos = new FileOutputStream(file);
							while ((len = is1.read(buffer)) != -1) {
								fos.write(buffer, 0, len);
							}
							fos.flush();
							fos.close();
							is1.close();
						}
						bigFaceImg.onSuccess(
								(sha1 + settings.format).toLowerCase(), base64);
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	public class BigFaceImgSettings {
		public String format;
		public String fileName;
		public String assetsPath;
		public File folder = app.sdcardImageFolder;
	}

	public interface BigFaceImgInterface {
		public void setParams(BigFaceImgSettings settings);

		public void onSuccess(String fileName, String base64);
	}

	public void getGifImgFromWebOrSdCard(final String imageFileName,
			final FileResult gifFileResult) {
		final File folder = app.sdcardImageFolder;
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder, imageFileName);
		if (!file.exists()) {
			app.networkHandler.connection(new NetConnection() {
				@Override
				protected void success(InputStream is,
						HttpURLConnection httpURLConnection) {
					try {
						StreamParser.parseToFile(is, new FileOutputStream(
								new File(folder, imageFileName)));
						httpURLConnection.disconnect();
						GifMovie gifMovie = new GifMovie();
						File file = new File(folder, imageFileName);
						FileInputStream fileInputStream = new FileInputStream(
								file);
						gifMovie.bytes = streamToBytes(fileInputStream);
						gifMovie.movie = Movie.decodeByteArray(gifMovie.bytes,
								0, gifMovie.bytes.length);
						gifs.put(imageFileName, gifMovie);
						gifFileResult.onResult(FROM_WEB);
						try {
							// fileInputStream.close();
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
					}
				}

				@Override
				protected void failed(int failedType, int responseCode) {
					super.failed(failedType, responseCode);
				}

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN_IMAGE + imageFileName;
					settings.method = GET;
				}
			});
		} else {
			if (gifs.get(imageFileName) != null) {
				gifFileResult.onResult(FROM_MEMORY);
			} else {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					GifMovie gifMovie = new GifMovie();
					gifMovie.bytes = streamToBytes(fileInputStream);
					gifMovie.movie = Movie.decodeByteArray(gifMovie.bytes, 0,
							gifMovie.bytes.length);
					gifs.put(imageFileName, gifMovie);
					gifFileResult.onResult(FROM_SDCARD);
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
				}
			}
		}
	}

	private byte[] streamToBytes(InputStream is) {
		byte[] bytes = null;
		try {
			bytes = new byte[is.available()];
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			is.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public class GifMovie {
		public Movie movie;
		public byte[] bytes;
	}
}
