package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.utils.MCStaticData;

public class ChatFragment extends BaseListFragment {

	private View mContent;
	private ChatAdapter mAdapter;

	public ChatFragment() {
		mAdapter = new ChatAdapter();
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_chat, null);
		mMCFragmentManager.setNotifyListener(new NotifyListener() {
			@Override
			public void notifyDataChanged(int notify) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		});
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
	}

	private class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return MCStaticData.chatMessagesViewList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return MCStaticData.chatMessagesViewList.get(position);
		}

	}

}
