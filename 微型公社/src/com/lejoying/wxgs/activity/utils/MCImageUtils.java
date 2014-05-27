package com.lejoying.wxgs.activity.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public final class MCImageUtils {
	static MainApplication app;

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

	public static byte[] getByteArrayFromBitmap(Bitmap source,
			Bitmap.CompressFormat format, int quality) {
		byte[] data = null;
		if (source != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			source.compress(format, quality, byteArrayOutputStream);
			data = byteArrayOutputStream.toByteArray();
		}
		return data;
	}

	public static Bitmap getZoomBitmapFromFile(File imageFile,
			Integer maxWidth, Integer maxHeight) {
		if (!imageFile.exists()) {
			return null;
		}
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		boptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
				boptions);
		int width = boptions.outWidth;
		int height = boptions.outHeight;

		if (maxWidth == null || maxWidth <= 0) {
			maxWidth = Integer.valueOf(width);
		}
		if (maxHeight == null || maxHeight <= 0) {
			maxHeight = Integer.valueOf(height);
		}
		if (maxWidth > width && maxHeight > height) {
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
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
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
					boptions);

		}
		return bitmap;
	}

	public static Bitmap getCutBitmap(Bitmap bitmap, int width, int height,
			int style) {
		if (bitmap == null) {
			bitmap =BitmapFactory.decodeResource(app.getResources(),
					R.drawable.defaultimage);
		}else{
			int btwidth = bitmap.getWidth();
			int btheight = bitmap.getHeight();
			float scaleWidth = ((float) width) / btwidth;
			float scaleHeight = ((float) height) / btheight;
			if (width > btwidth || height > btheight) {
				if (scaleWidth > scaleHeight) {
					bitmap = Bitmap.createScaledBitmap(bitmap, width,
							(int) (((float) width) / btwidth * btheight), true);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
					return bitmap;
				} else {
					bitmap = Bitmap.createScaledBitmap(bitmap,
							(int) (((float) height) / btheight * btwidth), height,
							true);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
					return bitmap;
				}
			}
			bitmap = Bitmap.createBitmap(bitmap, (btwidth - width) / 2,
					(btheight - height) / 2, width, height);
		}
		return bitmap;
	}

	public static Bitmap compressImage(Bitmap bitmap, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return newBitmap;
	}
}
