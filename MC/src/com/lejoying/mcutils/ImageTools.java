package com.lejoying.mcutils;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.lejoying.utils.StreamTools;

public final class ImageTools {

	/**
	 * 返回圆形图片,source必须为正方形图片
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source) {
		return getCircleBitmap(source, false, null, null);
	}

	/**
	 * 返回带1px颜色为borderColor的边框的圆形图片，source必须为正方形图片
	 * 
	 * @param source
	 * @param showBorder
	 * @param borderColor
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source, boolean showBorder,
			Integer borderWidth, Integer borderColor) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		if (!showBorder || borderWidth == null) {
			borderWidth = 0;
		}
		// 创建新的bitmap
		Bitmap bitmap = Bitmap.createBitmap(sourceWidth + borderWidth * 2,
				sourceHeight + borderWidth * 2, Config.ARGB_8888);

		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();

		// 获取原图片半径
		int radius = 0;
		radius = source.getWidth() / 2;

		// 获取生成图片半径
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

	/**
	 * 缩放图片，返回图片宽度小于等于maxWidth,高度小于等于maxHeight.
	 * 如果maxWidth大于原图片宽度并且maxHeight大于原图片高度则返回原图片
	 * 
	 * @param is
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap getZoomBitmapFromStream(InputStream is,
			Integer maxWidth, Integer maxHeight) {
		byte[] data = null;
	    data = StreamTools.isToData(is);
		// 得到options对象
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		// 只解析图片边框
		boptions.inJustDecodeBounds = true;
		// 解析流
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				boptions);
		// 得到原图片的宽高
		int width = boptions.outWidth;
		int height = boptions.outHeight;

		if (maxWidth == null || maxWidth == 0) {
			maxWidth = width;
		}
		if (maxHeight == null || maxWidth == 0) {
			maxHeight = height;
		}
		// 初始化缩放比例为1
		int scale = 1;
		// 如果要缩放的最大宽高大于原图片，则直接返回原图片
		if (maxWidth > width && maxHeight > height) {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		// 如果要缩放的最大宽高小于原图片，则进行缩放
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
