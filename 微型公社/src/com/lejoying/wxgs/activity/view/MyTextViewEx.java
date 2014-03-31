package com.lejoying.wxgs.activity.view;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lejoying.wxgs.activity.utils.ExpressionUtilgif;
import com.lejoying.wxgs.activity.utils.GifDrawalbe;


public class MyTextViewEx extends TextView implements Runnable {
	public static boolean mRunning = true;
	private Vector<GifDrawalbe> drawables;
	private Hashtable<String, GifDrawalbe> cache;
	private final int SPEED = 100;
	private Context context = null;

	public MyTextViewEx(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;

		drawables = new Vector<GifDrawalbe>();
		cache = new Hashtable<String, GifDrawalbe>();

		new Thread(this).start();
	}

	public MyTextViewEx(Context context) {
		super(context);
		this.context = context;

		drawables = new Vector<GifDrawalbe>();
		cache = new Hashtable<String, GifDrawalbe>();

		new Thread(this).start();
	}

	public void insertGif(String str,Map<String, String> expressionFaceMap) {
		if (drawables.size() > 0)
			drawables.clear();
		SpannableString spannableString = ExpressionUtilgif.getExpressionString(
				context, str, cache, drawables,expressionFaceMap);
		setText(spannableString);
	}

	@Override
	public void run() {
		while (mRunning) {
			if (super.hasWindowFocus()) {
				for (int i = 0; i < drawables.size(); i++) {
					drawables.get(i).run();
				}
				postInvalidate();
			}
			sleep();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(SPEED);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		mRunning = false;
		drawables.clear();
		drawables = null;
	}

}
