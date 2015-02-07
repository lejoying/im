package com.open.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class TouchEditText extends EditText {

	public String tag = "TouchEditText";
	public MyLog log = new MyLog(tag, true);

	public TouchEditText(Context context) {
		super(context);
	}

	public TouchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		log.e(event.getAction() + "");
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			return false;
		} else if (action == MotionEvent.ACTION_UP) {
			super.setFocusable(true);
			int index = this.getSelectionStart();
			this.setSelection(index);
			// log.e("selection:" + index);
			// super.setFocusableInTouchMode(true);
			// super.requestFocus();
			return true;
		}
		return false;
	}
}