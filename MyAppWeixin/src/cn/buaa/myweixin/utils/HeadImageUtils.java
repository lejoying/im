package cn.buaa.myweixin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class HeadImageUtils {

	private int[] colorArray;

	public HeadImageUtils() {

	}

	public Bitmap returnHeadBitmap(Bitmap source) {
		Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Config.ARGB_8888);
		colorArray = new int[source.getHeight() * source.getWidth()];
		int count = 0;
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				int radius = source.getWidth()/2;
				int color = source.getPixel(i, j);
				colorArray[count] = color;
				int newColor;
				if ((radius-i)*(radius-i)+(radius-j)*(radius-j)>radius*radius)
					newColor = Color.argb(0, Color.red(color),
							Color.green(color), Color.blue(color));
				else
					newColor = Color.argb(255, Color.red(color),
							Color.green(color), Color.blue(color));
				bitmap.setPixel(i, j, newColor);
				count++;
			}
		}

		
		return bitmap;
	}
}
