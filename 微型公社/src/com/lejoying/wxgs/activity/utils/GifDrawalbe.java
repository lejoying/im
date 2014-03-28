package com.lejoying.wxgs.activity.utils;

import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

public class GifDrawalbe extends AnimationDrawable {

	public GifDrawalbe(Context context, InputStream is) {
		GifHelper helper = new GifHelper();
		helper.read(is);
		int gifCount = helper.getFrameCount();
		if (gifCount <= 0) {
			return;
		}
		BitmapDrawable bd = new BitmapDrawable(null, helper.getImage());
		addFrame(bd, helper.getDelay(0));
		for (int i = 1; i < helper.getFrameCount(); i++) {
			addFrame(new BitmapDrawable(null, helper.nextBitmap()), helper.getDelay(i));
		}
		setBounds(0, 0, helper.getImage().getWidth(), helper.getImage().getHeight());
		bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
		invalidateSelf();
	}

}
