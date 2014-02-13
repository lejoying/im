package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.data.handler.ViewHandler.GenerateViewListener;
import com.lejoying.mc.service.PushService;

public class Copy_2_of_FriendsFragment extends BaseListFragment {

	public static Copy_2_of_FriendsFragment instance;

	App app = App.getInstance();

	public FriendsAdapter mAdapter;

	private View mContent;

	View rl_control;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent service = new Intent(getActivity(), PushService.class);
		service.putExtra("objective", "start");
		getActivity().startService(service);
		mAdapter = new FriendsAdapter(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);
		rl_control = mContent.findViewById(R.id.rl_control);
		return mContent;
	}

	public List<View> viewList = new ArrayList<View>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getListAdapter() == null) {
			app.sDcardDataResolver.readLocalData(new UIModification() {
				@Override
				public void modifyUI() {
					setListAdapter(mAdapter);
					app.viewHandler
							.generateFriendView(new GenerateViewListener() {
								@Override
								public void success(List<View> views) {
									mAdapter = new FriendsAdapter(views);
									// viewList = views;
									app.mUIThreadHandler.post(new Runnable() {
										@Override
										public void run() {
											getListView().setAdapter(mAdapter);
										}
									});
								}
							});
					app.serverHandler.getAllData();
				}
			});
		}
	}

	public void onResume() {
		super.onResume();
		instance = this;
		app.mark = app.friendsFragment;
		mAdapter.notifyDataSetChanged();
	}

	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}

	public class FriendsAdapter extends BaseAdapter {

		List<View> views;

		public FriendsAdapter(List<View> views) {
			if (views == null) {
				this.views = new ArrayList<View>();
			} else {
				this.views = views;
			}
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object getItem(int arg0) {
			return views.get(arg0).getTag();
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return views.get(arg0);
		}

	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}
}