package com.lejoying.mc.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lejoying.mc.R;
import com.lejoying.mc.utils.ImageTools;

public class MessageFragment extends BaseListFragment {

	private LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		this.inflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setListAdapter(new MessageAdapter());
		getListView().setDivider(null);
		getListView().setDividerHeight(20);
		super.onActivityCreated(savedInstanceState);
	}

	private class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 12;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = null;
			if (arg0 == 0 || arg0 == 11) {
				v = inflater.inflate(R.layout.f_margin, null);
				v.setVisibility(View.GONE);
			} else {
				v = inflater.inflate(R.layout.f_messages_item, null);
				final ImageView iv = (ImageView) v.findViewById(R.id.iv_head);
				new Thread() {
					@Override
					public void run() {
						final Bitmap bm = ImageTools.getCircleBitmap(BitmapFactory
								.decodeResource(getActivity().getResources(),
										R.drawable.xiaohei), true, 5,
								Color.WHITE);
						handler.post(new Runnable() {

							@Override
							public void run() {
								iv.setImageBitmap(bm);
							}
						});

					}
				}.start();
			}
			return v;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
