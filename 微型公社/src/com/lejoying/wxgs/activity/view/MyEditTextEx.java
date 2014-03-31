package com.lejoying.wxgs.activity.view;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.lejoying.wxgs.activity.utils.ExpressionUtilgif;
import com.lejoying.wxgs.activity.utils.GifDrawalbe;

public class MyEditTextEx extends EditText implements Runnable {
	private Vector<GifDrawalbe> drawables = new Vector<GifDrawalbe>();
	private Hashtable<String, GifDrawalbe> cache = new Hashtable<String, GifDrawalbe>();

	private boolean mRunning = true;
	private Context context = null;
	private static final int SPEED = 100;

	public MyEditTextEx(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		new Thread(this).start();
	}

	public MyEditTextEx(Context context) {
		super(context);
		this.context = context;
		new Thread(this).start();
	}

	public void insertGif(String str, Map<String, String> expressionFaceMap) {
		if (drawables.size() > 0)
			drawables.clear();
		// 在光标处插入动态表情
		int index = getSelectionStart();
		Editable editable = getText();
		editable.insert(index, str);
		this.setSelection(index + str.length());
		Log.v("index", index + "----" + context);
		SpannableString spannableString = ExpressionUtilgif.getExpressionString(
				context, getText().toString(), cache, drawables,
				expressionFaceMap);
		// System.out.println(getText().toString());
		Log.v("index", spannableString + "");
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
