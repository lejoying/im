package com.lejoying.mc.fragment;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.utils.MCStaticData;

public class FriendsFragment extends BaseListFragment {

	private View mContent;
	private FriendsAdapter mFriendsAdapter;
	private FriendsHandler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);
		mFriendsAdapter = new FriendsAdapter();

		mHandler = new FriendsHandler();
		// getActivity().getSupportFragmentManager().beginTransaction()
		// .replace(R.id.fl_bottom, new FriendEditTabFragment()).commit();

		mMCFragmentManager.setNotifyListener(new NotifyListener() {
			@Override
			public synchronized void notifyDataChanged(int notify) {
				mHandler.sendEmptyMessage(notify);
				System.out.println(MCStaticData.circlesViewList.size()
						+ MCStaticData.messagesViewList.size());
			}
		});

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mFriendsAdapter);
	}

	class FriendsAdapter extends BaseAdapter {
    
		@Override
		public int getCount() {
			return MCStaticData.circlesViewList.size()
					+ MCStaticData.messagesViewList.size();
		}

		@Override
		public Object getItem(int arg0) {
			if (arg0 < MCStaticData.messagesViewList.size()) {
				return MCStaticData.messagesViewList.get(arg0);
			} else {
				return MCStaticData.circlesViewList.get(arg0
						- MCStaticData.messagesViewList.size());
			}
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg0 < MCStaticData.messagesViewList.size()) {
				return MCStaticData.messagesViewList.get(arg0);
			} else {
				return MCStaticData.circlesViewList.get(arg0
						- MCStaticData.messagesViewList.size());
			}
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	class FriendsHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case NotifyListener.NOTIFY_MESSAGEANDFRIEND:
				mFriendsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}

}
