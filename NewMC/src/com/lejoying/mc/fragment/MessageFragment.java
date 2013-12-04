package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.listener.NotifyListener;
import com.lejoying.mc.utils.MCStaticData;

public class MessageFragment extends BaseListFragment {

	private View mContent;

	private MessageAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_messages, null);
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.app_messages));
		mAdapter = new MessageAdapter();
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMCFragmentManager.relpaceToContent(new ChatFragment(), true);
			}
		});

		mMCFragmentManager.setNotifyListener(new NotifyListener() {
			@Override
			public void notifyChanged() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return MCStaticData.messages.size();
		}

		@Override
		public Object getItem(int arg0) {
			return MCStaticData.messages.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return MCStaticData.messages.get(arg0);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}
}
