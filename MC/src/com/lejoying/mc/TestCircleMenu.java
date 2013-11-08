package com.lejoying.mc;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TestCircleMenu extends Activity {

	private RelativeLayout rl_circleMenu;
	private ImageView iv_control;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circlemenu);
		rl_circleMenu = (RelativeLayout) findViewById(R.id.rl_circleMenu);
		iv_control = (ImageView) findViewById(R.id.iv_control);
		rl_circleMenu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float clickX = event.getX();
				float clickY = event.getY();

				int[] location = new int[2];
				iv_control.getLocationInWindow(location);
				int height = iv_control.getHeight();
				int width = iv_control.getWidth();
				int lX = location[0];
				int lY = location[1];

				int circleX = lX+width/2;
				int circleY = lY+height/2;
				int radius = width/2;
				
				if ((clickX-circleX)*(clickX-circleX)+(clickY-circleY)*(clickY-circleY)<radius*radius) {
					
					return true;
				};

				return false;
			}
		});
	}

}
