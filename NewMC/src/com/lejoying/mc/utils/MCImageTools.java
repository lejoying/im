package com.lejoying.mc.utils;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.lejoying.utils.StreamTools;

public final class MCImageTools {

	public static Bitmap getCircleBitmap(Bitmap source) {
		return getCircleBitmap(source, false, null, null);
	}

	public static Bitmap getCircleBitmap(Bitmap source, boolean showBorder,
			Integer borderWidth, Integer borderColor) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		if (!showBorder || borderWidth == null) {
			borderWidth = 0;
		}
		Bitmap bitmap = Bitmap.createBitmap(sourceWidth + borderWidth * 2,
				sourceHeight + borderWidth * 2, Config.ARGB_8888);

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
						newColor = Color.argb(255, Color.red(color),
								Color.green(color), Color.blue(color));
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

	public static boolean isNeedToZoom(byte[] imageByte, Integer maxWidth,
			Integer maxHeight) {
		boolean flag = false;
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		boptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, boptions);
		int width = boptions.outWidth;
		int height = boptions.outHeight;
		if (width > maxWidth || height > maxHeight) {
			flag = true;
		}
		return flag;
	}

	public static Bitmap getZoomBitmapFromStream(InputStream is,
			Integer maxWidth, Integer maxHeight) {
		byte[] data = StreamTools.isToData(is);
		return getZoomBitmapFromStream(data, maxWidth, maxHeight);
	}

	public static Bitmap getZoomBitmapFromStream(byte[] imageByte,
			Integer maxWidth, Integer maxHeight) {
		if (imageByte == null) {
			return null;
		}
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		boptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
				imageByte.length, boptions);
		int width = boptions.outWidth;
		int height = boptions.outHeight;

		if (maxWidth == null || maxWidth <= 0) {
			maxWidth = width;
		}
		if (maxHeight == null || maxWidth <= 0) {
			maxHeight = height;
		}
		if (maxWidth > width && maxHeight > height) {
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length);
		} else {
			int scale = 1;
			int scaleHeight = boptions.outHeight % maxHeight != 0 ? boptions.outHeight
					/ maxHeight + 1
					: boptions.outHeight / maxHeight;
			int scaleWidth = boptions.outWidth % maxWidth != 0 ? boptions.outWidth
					/ maxWidth + 1
					: boptions.outWidth / maxWidth;
			scale = scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
			boptions.inJustDecodeBounds = false;
			boptions.inSampleSize = scale;
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length, boptions);
		}
		return bitmap;
	}
}
