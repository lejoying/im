package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lejoying.mc.R;
import com.lejoying.mc.utils.ImageTools;

public class MessageFragment extends BaseListFragment {

	private List<View> mMessageViewsList;
	private LayoutInflater mInflater;
	private MessageAdapter mAdapter;
	private Handler mHandler;

	private View mView_margin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager.setCircleMenuPageName("消息列表");
		mInflater = inflater;
		mAdapter = new MessageAdapter();
		mHandler = new MessagesHandler();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mView_margin = mInflater.inflate(R.layout.f_margin, null);
		mView_margin.setVisibility(View.GONE);
		getListView().setDivider(null);
		getListView().setDividerHeight(25);
		mMessageViewsList = new ArrayList<View>();
		setListAdapter(mAdapter);
		new Thread() {
			public void run() {
				mMessageViewsList.add(mInflater
						.inflate(R.layout.f_margin, null));
				for (int i = 0; i < 10; i++) {
					View v = mInflater.inflate(R.layout.f_messages_item, null);
					ImageView iv_head = (ImageView) v
							.findViewById(R.id.iv_head);
					Bitmap bm = ImageTools.getCircleBitmap(BitmapFactory
							.decodeResource(getActivity().getResources(),
									R.drawable.xiaohei), true, 5, Color.WHITE);
					iv_head.setImageBitmap(bm);
					mMessageViewsList.add(v);
				}
				mMessageViewsList.add(mInflater
						.inflate(R.layout.f_margin, null));
				mHandler.sendEmptyMessage(1);
			};
		}.start();
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMessageViewsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mMessageViewsList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return mMessageViewsList.get(arg0);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private class MessagesHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				setListAdapter(mAdapter);
				break;

			default:
				break;
			}

		}
	}
}
