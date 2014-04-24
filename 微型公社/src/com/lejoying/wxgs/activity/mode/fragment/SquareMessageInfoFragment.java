package com.lejoying.wxgs.activity.mode.fragment;

import android.graphics.Movie;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.SquareMessageInfoScrollView;
import com.lejoying.wxgs.activity.view.SquareMessageInfoScrollView.SizeChangedListener;
import com.lejoying.wxgs.activity.view.SquareMessageInfoScrollView.onScrollChanged;
import com.lejoying.wxgs.app.MainApplication;

public class SquareMessageInfoFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();

	MainModeManager mMainModeManager;

	LayoutInflater inflater;

	SquareMessageInfoScrollView sc_square_message_info;
	SquareMessageInfoScrollView sc_square_message_info_all;
	RelativeLayout rl_square_message_menu;
	RelativeLayout rl_square_message_menu_buttom1;
	int SCROLL_TOP = 0X01;
	int SCROLL_BUTTOM = 0X02;
	int SCROLL_BETWEEN = 0X03;
	int scrollStatus = SCROLL_TOP;
	int scrollViewY = 0;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View v = inflater
				.inflate(R.layout.fragment_square_message_infoes, null);
		sc_square_message_info = (SquareMessageInfoScrollView) v
				.findViewById(R.id.sc_square_message_info);
		sc_square_message_info_all = (SquareMessageInfoScrollView) v
				.findViewById(R.id.sc_square_message_info_all);
		rl_square_message_menu = (RelativeLayout) v
				.findViewById(R.id.rl_square_message_menu_buttom);
		rl_square_message_menu_buttom1 = (RelativeLayout) v
				.findViewById(R.id.rl_square_message_menu_buttom1);
		initEvent();
		return v;
	}

	private void initEvent() {
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		final int screenHeight = getActivity().getWindowManager()
				.getDefaultDisplay().getHeight();
		System.out.println(screenHeight + "----------" + screenWidth);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight - 260);
		sc_square_message_info_all.setLayoutParams(layoutParams);
		sc_square_message_info
				.setSizeChangedListener(new SizeChangedListener() {

					@Override
					public void sizeChanged(int w, int h, int oldw, int oldh) {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.MATCH_PARENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						layoutParams.setMargins(layoutParams.leftMargin, 700,
								layoutParams.rightMargin, -Integer.MAX_VALUE);
						// rl_square_message_menu_buttom1
						// .setLayoutParams(layoutParams);
					}
				});
		sc_square_message_info.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (sc_square_message_info.getScrollY() == 0) {
						scrollStatus = SCROLL_TOP;
					} else if (sc_square_message_info.getScrollY()
							- scrollViewY < 2
							&& sc_square_message_info.getScrollY() >= scrollViewY) {
						scrollStatus = SCROLL_BUTTOM;
					} else {
						scrollStatus = SCROLL_BETWEEN;
					}
				}
				return false;
			}
		});
		sc_square_message_info_all.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				boolean flag = false;
				if (scrollStatus == SCROLL_TOP) {
					sc_square_message_info
							.requestDisallowInterceptTouchEvent(true);
				} else {
					sc_square_message_info
							.requestDisallowInterceptTouchEvent(false);
				}
				// if (sc_square_message_info_all.getScrollY() == 0) {
				// if (scrollStatus == SCROLL_TOP) {
				// sc_square_message_info
				// .requestDisallowInterceptTouchEvent(true);
				// } else {
				// sc_square_message_info
				// .requestDisallowInterceptTouchEvent(false);
				// }
				//
				// } else if (sc_square_message_info_all.getScrollY()
				// - scrollViewY < 2
				// && sc_square_message_info_all.getScrollY() >= scrollViewY) {
				// if (scrollStatus == SCROLL_TOP
				// || scrollStatus == SCROLL_BUTTOM) {
				// sc_square_message_info
				// .requestDisallowInterceptTouchEvent(false);
				// } else {
				// sc_square_message_info
				// .requestDisallowInterceptTouchEvent(true);
				// }
				// } else {
				// sc_square_message_info
				// .requestDisallowInterceptTouchEvent(true);
				// }
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (sc_square_message_info_all.getScrollY() == 0) {
						// Toast.makeText(getActivity(), "到达顶部了",
						// Toast.LENGTH_SHORT).show();
						// sc_square_message_info.setont
					} else if (sc_square_message_info_all.getScrollY()
							- scrollViewY < 2
							&& sc_square_message_info_all.getScrollY() >= scrollViewY) {
						scrollStatus = SCROLL_BETWEEN;
						sc_square_message_info
								.requestDisallowInterceptTouchEvent(false);
						Toast.makeText(
								getActivity(),
								"继续拖动有评论"
										+ sc_square_message_info_all
												.getScrollY(),
								Toast.LENGTH_SHORT).show();
					} else {
						scrollViewY = sc_square_message_info_all.getScrollY();
						flag = true;
					}
				}
				return flag;
			}
		});
		sc_square_message_info_all.onScrollChanged(new onScrollChanged() {

			@Override
			public void ScrollChanged(int l, int y, int oldl, int oldt) {

			}
		});
	}
}
