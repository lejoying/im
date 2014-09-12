package com.open.welinks.view;

import com.open.welinks.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class ThreeChoicesView extends FrameLayout {
	private Context context;
	private Button button_one, button_two, button_three;
	private OnClickListener mOnClickListener;
	private OnTouchListener mOnTouchListener;
	private OnItemClickListener mOnItemClickListener;
	private GestureDetector detector;
	private SimpleOnGestureListener mSimpleOnGestureListener;
	private int clickedItem = 1;

	private Button currentButton;

	public ThreeChoicesView(Context context, int defaultItem) {
		super(context);
		if (defaultItem > 0 && defaultItem < 4) {
			clickedItem = defaultItem;
		}
		this.context = context;
		OnCreate(context);
	}

	public ThreeChoicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		OnCreate(context);
	}

	public ThreeChoicesView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		OnCreate(context);
	}

	@SuppressLint("NewApi")
	public void OnCreate(Context context) {
		LayoutInflater.from(context).inflate(R.layout.three_choices, this);
		button_one = (Button) this.findViewById(R.id.button_one);
		button_two = (Button) this.findViewById(R.id.button_two);
		button_three = (Button) this.findViewById(R.id.button_three);
		mOnItemClickListener = new OnItemClickListener();
		setViewStyle(clickedItem);
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(button_one)) {
					setViewStyle(1);
					mOnItemClickListener.onButtonCilck(1);
				} else if (view.equals(button_two)) {
					setViewStyle(2);
					mOnItemClickListener.onButtonCilck(2);
				} else if (view.equals(button_three)) {
					setViewStyle(3);
					mOnItemClickListener.onButtonCilck(3);
				}

			}
		};
		mSimpleOnGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// currentButton.setTextColor(Color.parseColor("#ffffff"));
				System.out.println("showPress" + e.getAction());
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				System.out.println("tapup");
				if (currentButton.equals(button_one)) {
					setViewStyle(1);
					mOnItemClickListener.onButtonCilck(1);
				} else if (currentButton.equals(button_two)) {
					setViewStyle(2);
					mOnItemClickListener.onButtonCilck(2);
				} else if (currentButton.equals(button_three)) {
					setViewStyle(3);
					mOnItemClickListener.onButtonCilck(3);
				}
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				System.out.println("fling");
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				System.out.println("onSingleTapConfirmed");
				return super.onSingleTapConfirmed(e);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				System.out.println("onScroll");
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
		};
		detector = new GestureDetector(context, mSimpleOnGestureListener);
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					System.out.println("outside");
				}
				if (view.equals(button_one)) {
					currentButton = button_one;
				} else if (view.equals(button_two)) {
					currentButton = button_two;
				} else if (view.equals(button_three)) {
					currentButton = button_three;
				}
				detector.onTouchEvent(event);
				return false;
			}
		};
		button_one.setOnClickListener(mOnClickListener);
		button_two.setOnClickListener(mOnClickListener);
		button_three.setOnClickListener(mOnClickListener);
		// button_one.setOnTouchListener(mOnTouchListener);
		// button_two.setOnTouchListener(mOnTouchListener);
		// button_three.setOnTouchListener(mOnTouchListener);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public void setButtonOneText(String text) {
		button_one.setText(text);
	}

	public void setButtonTwoText(String text) {
		button_two.setText(text);
	}

	public void setButtonThreeText(String text) {
		button_three.setText(text);
	}

	public void setDefaultItem(int item) {
		setViewStyle(item);
	}

	public class OnItemClickListener {
		public void onButtonCilck(int position) {
		};
	}

	private void setViewStyle(int item) {
		if (1 == item) {
			button_one.setClickable(false);
			button_two.setClickable(true);
			button_three.setClickable(true);
			button_two.clearFocus();
			button_three.clearFocus();

			button_one.setTextColor(Color.parseColor("#99000000"));
			button_one.setBackgroundResource(R.drawable.threechoice_one_sel);

			button_two.setTextColor(Color.parseColor("#ffffffff"));
			button_two.setBackgroundResource(R.drawable.threechoice_two);

			button_three.setTextColor(Color.parseColor("#ffffffff"));
			button_three.setBackgroundResource(R.drawable.threechoice_three);

		} else if (2 == item) {
			button_one.setClickable(true);
			button_two.setClickable(false);
			button_three.setClickable(true);

			button_one.clearFocus();
			button_three.clearFocus();

			button_two.setTextColor(Color.parseColor("#99000000"));
			button_two.setBackgroundResource(R.drawable.threechoice_two_sel);

			button_one.setTextColor(Color.parseColor("#ffffffff"));
			button_one.setBackgroundResource(R.drawable.threechoice_one);

			button_three.setTextColor(Color.parseColor("#ffffffff"));
			button_three.setBackgroundResource(R.drawable.threechoice_three);

		} else if (3 == item) {
			button_one.setClickable(true);
			button_two.setClickable(true);
			button_three.setClickable(false);
			button_one.clearFocus();
			button_two.clearFocus();

			button_three.setTextColor(Color.parseColor("#99000000"));
			button_three.setBackgroundResource(R.drawable.threechoice_three_sel);

			button_two.setTextColor(Color.parseColor("#ffffffff"));
			button_two.setBackgroundResource(R.drawable.threechoice_two);

			button_one.setTextColor(Color.parseColor("#ffffffff"));
			button_one.setBackgroundResource(R.drawable.threechoice_one);

		}
	}
}
