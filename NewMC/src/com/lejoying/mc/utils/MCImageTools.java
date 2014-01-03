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

	public static Bitmap getZoomBitmapFromStream(InputStream is,
			Integer maxWidth, Integer maxHeight) {
		byte[] data = null;
		data = StreamTools.isToData(is);
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		boptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				boptions);
		int width = boptions.outWidth;
		int height = boptions.outHeight;

		if (maxWidth == null || maxWidth == 0) {
			maxWidth = width;
		}
		if (maxHeight == null || maxWidth == 0) {
			maxHeight = height;
		}
		int scale = 1;
		if (maxWidth > width && maxHeight > height) {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		if (maxWidth <= width && maxHeight <= height) {
			while (boptions.outHeight / scale / 2 > maxHeight
					|| boptions.outWidth / scale / 2 > maxWidth) {
				scale *= 2;
			}

			boptions.inJustDecodeBounds = false;
			boptions.inSampleSize = scale;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					boptions);
		}
		return bitmap;
	}
}
