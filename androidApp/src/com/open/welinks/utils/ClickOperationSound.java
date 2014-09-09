package com.open.welinks.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ClickOperationSound {

	public static OnTouchListener mOnTouchListener;

	public static void click(final Context context, final View view) {
		if (mOnTouchListener == null) {
			mOnTouchListener = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						view.setBackgroundColor(Color.argb(143, 0, 0, 0));
						// view.playSoundEffect(SoundEffectConstants.CLICK);
						// Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
						// long[] pattern = { 100 };
						// vibrator.vibrate(pattern, -1);
						break;
					case MotionEvent.ACTION_UP:
						view.setBackgroundColor(Color.argb(0, 0, 0, 0));
						break;
					}
					return false;
				}
			};
		}
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// view.playSoundEffect(SoundEffectConstants.CLICK);
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				}
				return false;
			}
		});
	}
}
