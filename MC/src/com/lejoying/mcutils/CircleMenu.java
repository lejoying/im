package com.lejoying.mcutils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lejoying.mc.R;

public class CircleMenu {
	public final static int SHOW_TOP = 0x0011;
	public final static int SHOW_BOTTOM = 0x0012;

	private final int top;
	private final int bottom;

	private Activity activity;
	private LayoutInflater inflater;

	private RelativeLayout rl_control;

	public CircleMenu(Activity activity) {
		this.activity = activity;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.rl_control = (RelativeLayout) inflater.inflate(
				R.layout.circlemenu, null);
		top = rl_control.getScrollY();
		bottom = -top;
	}

	public void showMenu(int showWhere) {
		ViewGroup contentView = getContentView(activity);
		if (contentView != null) {
			contentView.addView(rl_control,
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
		} else {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			activity.addContentView(rl_control, params);
		}
		if (showWhere == SHOW_TOP) {
			rl_control.scrollTo(0, top);
		}
		if (showWhere == SHOW_BOTTOM) {
			rl_control.scrollTo(0, bottom);
		}

		rl_control.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
	}

	public ViewGroup getContentView(Activity activity) {
		ViewGroup v = (ViewGroup) activity.getWindow().getDecorView()
				.findViewById(android.R.id.content);
		return (ViewGroup) v.getChildAt(0);
	}
}
