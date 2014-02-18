package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.ToTry;
import com.lejoying.mc.utils.ToTry.ToTryListener;

public class EditFragment extends BaseFragment {

	App app = App.getInstance();
	View mContent;
	View rl_control;

	ListView listView;

	ViewGroup vg = (ViewGroup) FriendsFragment.editView;

	LinearLayout linearlayout_user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_friends, null);
		rl_control = mContent.findViewById(R.id.rl_control);
		rl_control.setVisibility(View.VISIBLE);
		listView = (ListView) mContent.findViewById(android.R.id.list);
		listView.setAdapter(new MyAdapter());
		linearlayout_user = (LinearLayout) mContent
				.findViewById(R.id.linearlayout_user);
		for (int i = 0; i < 14; i++) {
			View userView = inflater.inflate(
					R.layout.f_friend_edit_panelitem_gridpage_item, null);
			ImageView iv_head = (ImageView) userView.findViewById(R.id.iv_head);
			TextView tv_nickname = (TextView) userView
					.findViewById(R.id.tv_nickname);
			iv_head.setImageBitmap(app.fileHandler.defaultHead);
			tv_nickname.setText("测试" + i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			params.setMargins(40, 0, 0, 0);

			if (i == 13) {
				params.setMargins(40, 0, 40, 0);
			}
			
			userView.setLayoutParams(params);
			linearlayout_user.addView(userView);
		}
		return mContent;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			return vg;
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ToTry.tryDoing(10, 500, new ToTryListener() {

			@Override
			public void successed(long time) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setLayoutParams(new LayoutParams(vg.getWidth(), vg
						.getHeight()));
				imageView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return true;
					}
				});
				vg.addView(imageView);
			}

			@Override
			public boolean isSuccess() {
				// TODO Auto-generated method stub
				return vg.getWidth() != 0;
			}

			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeDoing() {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

}
