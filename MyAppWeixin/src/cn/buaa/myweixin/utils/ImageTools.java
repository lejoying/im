package cn.buaa.myweixin.utils;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ImageTools {

	/**
	 * 返回圆形图片,source须为正方形图片
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source) {
		return getCircleBitmap(source, null);
	}

	/**
	 * 返回直径为diameter个像素的圆形图片,source须为正方形图片
	 * 
	 * @param source
	 * @param diameter
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source, Integer diameter) {
		// 创建新的bitmap
		Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Config.ARGB_8888);
		// 获取半径
		int radius = 0;
		if (diameter == null || diameter == 0) {
			radius = source.getWidth() / 2;
		} else {
			radius = diameter / 2;
		}

		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {

				int color = source.getPixel(i, j);
				int newColor;
				if ((radius - i) * (radius - i) + (radius - j) * (radius - j) > radius
						* radius)
					newColor = Color.argb(0, Color.red(color),
							Color.green(color), Color.blue(color));
				else
					newColor = Color.argb(255, Color.red(color),
							Color.green(color), Color.blue(color));
				synchronized (bitmap) {
					bitmap.setPixel(i, j, newColor);
				}
			}
		}

		return bitmap;
	}

	/**
	 * 缩放图片，返回图片宽度小于等于maxWidth,高度小于等于maxHeight.如果maxWidth大于原图片宽度并且maxHeight大于原图片高度则返回原图片
	 * @param is
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap getZoomBitmapFromStream(InputStream is,
			Integer maxWidth, Integer maxHeight) {
		byte[] data = null;
		try {
			data = StreamTools.isToData(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 得到options对象
		BitmapFactory.Options boptions = new BitmapFactory.Options();
		// 只解析图片边框
		boptions.inJustDecodeBounds = true;
		// 解析流
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, boptions);
		// 得到原图片的宽高
		int width = boptions.outWidth;
		int height = boptions.outHeight;
		System.out.println(maxWidth+".."+maxHeight+".."+width+".."+height);
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
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,boptions );
		}
		
		return bitmap;
	}
}
