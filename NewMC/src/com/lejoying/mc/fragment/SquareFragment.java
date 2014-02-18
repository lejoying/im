package com.lejoying.mc.fragment;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

public class SquareFragment extends BaseFragment {

	View mContent;
	App app = App.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager
				.setCircleMenuPageName("广场");
		mContent = inflater.inflate(R.layout.f_square, null);
		ScrollView scrollView2 = (ScrollView) mContent
				.findViewById(R.id.scrollView2);

		((ScrollView) mContent).requestDisallowInterceptTouchEvent(true);
		return mContent;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		app.mark = app.squareFragment;
		super.onResume();
	}
	
	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

}
