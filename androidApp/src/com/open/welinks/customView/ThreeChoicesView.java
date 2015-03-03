package com.open.welinks.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.open.welinks.R;

public class ThreeChoicesView extends FrameLayout {
	private Button button_one, button_two, button_three;
	private View layout_two;
	private OnClickListener mOnClickListener;
	private OnItemClickListener mOnItemClickListener;
	private int clickedItem = 1;

	public ThreeChoicesView(Context context) {
		super(context);
		OnCreate(context);
	}

	public ThreeChoicesView(Context context, int defaultItem) {
		super(context);
		if (defaultItem > 0 && defaultItem < 4) {
			clickedItem = defaultItem;
		}
		OnCreate(context);
	}

	public ThreeChoicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		OnCreate(context);
	}

	public ThreeChoicesView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		OnCreate(context);
	}

	@SuppressLint("NewApi")
	public void OnCreate(Context context) {
		LayoutInflater.from(context).inflate(R.layout.three_choices, this);
		button_one = (Button) this.findViewById(R.id.button_one);
		button_two = (Button) this.findViewById(R.id.button_two);
		button_three = (Button) this.findViewById(R.id.button_three);
		layout_two = this.findViewById(R.id.layout_two);
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
		button_one.setOnClickListener(mOnClickListener);
		button_two.setOnClickListener(mOnClickListener);
		button_three.setOnClickListener(mOnClickListener);
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

	public void setTwoChoice() {
		layout_two.setVisibility(View.GONE);
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

			button_one.setTextColor(Color.parseColor("#eeffffff"));
			button_one.setBackgroundResource(R.drawable.threechoice_focus_left);

			button_two.setTextColor(Color.parseColor("#0099cd"));
			button_two.setBackgroundResource(R.drawable.threechoice_two);

			button_three.setTextColor(Color.parseColor("#0099cd"));
			button_three.setBackgroundResource(R.drawable.threechoice_three);

		} else if (2 == item) {
			button_one.setClickable(true);
			button_two.setClickable(false);
			button_three.setClickable(true);

			button_one.clearFocus();
			button_three.clearFocus();

			button_two.setTextColor(Color.parseColor("#eeffffff"));
			button_two.setBackgroundResource(R.drawable.threechoice_focus);

			button_one.setTextColor(Color.parseColor("#0099cd"));
			button_one.setBackgroundResource(R.drawable.threechoice_one);

			button_three.setTextColor(Color.parseColor("#0099cd"));
			button_three.setBackgroundResource(R.drawable.threechoice_three);

		} else if (3 == item) {
			button_one.setClickable(true);
			button_two.setClickable(true);
			button_three.setClickable(false);
			button_one.clearFocus();
			button_two.clearFocus();

			button_three.setTextColor(Color.parseColor("#eeffffff"));
			button_three.setBackgroundResource(R.drawable.threechoice_focus_right);

			button_two.setTextColor(Color.parseColor("#0099cd"));
			button_two.setBackgroundResource(R.drawable.threechoice_two);

			button_one.setTextColor(Color.parseColor("#0099cd"));
			button_one.setBackgroundResource(R.drawable.threechoice_one);

		}
	}
}
