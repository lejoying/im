package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class ScrollContent extends LinearLayout {
	public BaseAdapter adapter;

	public ScrollContent(Context context) {
		super(context);
	}

	public ScrollContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		bindLinearLayout();
	}

	public void bindLinearLayout() {
		int count = adapter.getCount();
		this.removeAllViews();
		for (int i = 0; i < count; i++) {
			View v = adapter.getDropDownView(i, null, null);
			if (v != null) {
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(layoutParams.leftMargin, 20,
						layoutParams.rightMargin, layoutParams.bottomMargin);
				v.setLayoutParams(layoutParams);
				this.addView(v);
			}
		}
	}

	public void setSelection(int position) {
		this.setSelection(position);
	}

}
