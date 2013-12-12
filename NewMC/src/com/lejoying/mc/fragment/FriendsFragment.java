package com.lejoying.mc.fragment;

import java.util.List;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCStaticData;

public class FriendsFragment extends BaseListFragment {

	private View mContent;
	private FriendsAdapter mFriendsAdapter;

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

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mFriendsAdapter);

		// loadView();

	}

	class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return MCStaticData.circlesViewList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return MCStaticData.circlesViewList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return MCStaticData.circlesViewList.get(arg0);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			if (observer != null) {
				super.unregisterDataSetObserver(observer);
			}
		}

	}

	public void loadView() {
		new Thread() {
			public void run() {
				List<Circle> circles = MCDataTools.getCircles(getActivity());
				MCStaticData.circles = circles;
				System.out.println("获取成功了吗？" + circles);
				List<Circle> circles2 = MCDataTools.getCircles(getActivity());
				System.out.println(MCStaticData.circles.equals(circles2));
				System.out.println(MCStaticData.circles.containsAll(circles2));
			};
		}.start();
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

}
