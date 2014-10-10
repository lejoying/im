package com.open.welinks.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public final class MCImageUtils {
	public static SHA1 sha1 = new SHA1();

	public static Bitmap getCircleBitmap(Bitmap source) {
		return getCircleBitmap(source, false, null, null);
	}

	public static Bitmap getCircleBitmap(Bitmap source, boolean showBorder, Integer borderWidth, Integer borderColor) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		if (!showBorder || borderWidth == null) {
			borderWidth = 0;
		}
		Bitmap bitmap = Bitmap.createBitmap(sourceWidth + borderWidth * 2, sourceHeight + borderWidth * 2, Config.ARGB_8888);

		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();

		int radius = 0;
		radius = source.getWidth() / 2;

		int newRandius = 0;
		newRandius = bitmap.getWidth() / 2;

		for (int i = 0; i < bitmapWidth; i++) {
			for (int j = 0; j < bitmapHeight; j++) {
				int newColor = 0;
				if (i < sourceWidth && j < sourceHeight) {
					int color = source.getPixel(i, j);
					if (Math.hypot(radius - i, radius - j) > radius) {
						newColor = Color.argb(0, 0, 0, 0);

					} else {
						newColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
					}
					bitmap.setPixel(i + borderWidth, j + borderWidth, newColor);
				}
				if (showBorder) {
					double sqrt = Math.hypot(newRandius - i, newRandius - j);
					if (sqrt <= newRandius && sqrt > radius) {
						if (borderColor == null) {
							newColor = Color.rgb(0, 0, 0);
						} else {
							newColor = borderColor;
						}
						bitmap.setPixel(i, j, newColor);
					}
				}
			}
		}
		return bitmap;
	}

	public static Map<String, Object> processImagesInformation(String filePath, File targetFolder) {
		Map<String, Object> map = new HashMap<String, Object>();
		String suffixName = filePath.substring(filePath.lastIndexOf("."));
		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			suffixName = ".osp";
		} else if (suffixName.equals(".aac")) {

		}
		String fileName = "";
		File fromFile = new File(filePath);
		FileInputStream fileInputStream;
		FileOutputStream fileOutputStream;
		try {
			fileInputStream = new FileInputStream(fromFile);
			byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
			map.put("bytes", bytes);
			String sha1FileName = sha1.getDigestOfString(bytes);
			fileName = sha1FileName + suffixName;
			map.put("fileName", fileName);
			File toFile = new File(targetFolder, fileName);
			fileOutputStream = new FileOutputStream(toFile);
			StreamParser.parseToFile(bytes, fileOutputStream);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static Bitmap createQEcodeImage(String type, String message) {
		int width = 200;
		int height = 200;
		try {
			QRCodeWriter writer = new QRCodeWriter();
			String text = "mc:" + type + ":" + message;
			if (text == null || "".equals(text) || text.length() < 1) {
				return null;
			}

			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
