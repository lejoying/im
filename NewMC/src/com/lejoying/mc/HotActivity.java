package com.lejoying.mc;

import com.lejoying.mc.utils.MCImageUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class HotActivity extends Activity {

	Bitmap head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot);
		head = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);
		initView();
	}

	RelativeLayout relativeLayout1;
	View menu1;
	ImageView imageView1;
	ScrollView ScrollView1;

	void initView() {
		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		menu1 = findViewById(R.id.menu1);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		ScrollView1 = (ScrollView) findViewById(R.id.ScrollView1);

		// heads
		ImageView imageView10 = (ImageView) findViewById(R.id.imageView10);
		imageView10.setImageBitmap(head);
		ImageView imageView11 = (ImageView) findViewById(R.id.imageView11);
		imageView11.setImageBitmap(head);
		ImageView imageView12 = (ImageView) findViewById(R.id.imageView12);
		imageView12.setImageBitmap(head);
		ImageView imageView13 = (ImageView) findViewById(R.id.imageView13);
		imageView13.setImageBitmap(head);
		ImageView imageView14 = (ImageView) findViewById(R.id.imageView14);
		imageView14.setImageBitmap(head);

		menu1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				relativeLayout1.setBackgroundColor(Color.TRANSPARENT);
				menu1.setVisibility(View.GONE);
				return true;
			}
		});

		imageView1.setOnTouchListener(new OnTouchListener() {

			int horizontalScrollViewRight;
			int horizontalScrollViewTop;
			int horizontalScrollViewBottom;
			float clickX;
			float clickY;
			float moveY = 0f;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					horizontalScrollViewRight = ScrollView1.getRight();
					horizontalScrollViewTop = ScrollView1.getTop();
					horizontalScrollViewBottom = ScrollView1.getBottom();
					clickX = event.getX();
					clickY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					moveY = clickY - event.getY();
					ScrollView1.layout(0,
							(int) (horizontalScrollViewTop - moveY),
							horizontalScrollViewRight,
							horizontalScrollViewBottom);

					break;
				case MotionEvent.ACTION_UP:

					break;
				default:
					break;
				}
				return true;
			}
		});

		ScrollView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					break;
				case MotionEvent.ACTION_MOVE:

					break;
				case MotionEvent.ACTION_UP:

					break;

				default:
					break;
				}
				return true;
			}
		});

	}

	public void hotClick(View v) {
		relativeLayout1.setBackgroundResource(R.drawable.frame_background);
		menu1.setVisibility(View.VISIBLE);
	}
}
