package cn.buaa.myweixin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class HeadImageUtils {

	public HeadImageUtils() {

	}

	public Bitmap returnHeadBitmap(final Bitmap source) {
		// 创建新的bitmap
		final Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Config.ARGB_8888);
		// 获取半径
		final int radius = source.getWidth() / 2;

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
}
