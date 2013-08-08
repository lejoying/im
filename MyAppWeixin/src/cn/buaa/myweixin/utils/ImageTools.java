package cn.buaa.myweixin.utils;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class ImageTools {
	
	/**
	 * 返回圆形图片,source须为正方形图片
	 * @param source
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source){
		return getCircleBitmap(source, null);
	}
	/**
	 * 返回直径为diameter个像素的圆形图片,source须为正方形图片
	 * @param source
	 * @param diameter
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap source,Integer diameter ) {
		// 创建新的bitmap
		Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Config.ARGB_8888);
		// 获取半径
		int radius = 0;
		if(diameter==null||diameter==0){
			radius = source.getWidth() / 2;
		}else{
			radius = diameter/2;
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
}
