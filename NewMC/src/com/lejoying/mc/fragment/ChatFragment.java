package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.lejoying.mc.R;

public class ChatFragment extends BaseListFragment {

	private View mContent;

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
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ChatAdapter());
	}

	private class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
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
			int layout = R.layout.f_chat_item_left;
			if (position % 2 == 1) {
				layout = R.layout.f_chat_item_right;
			}
			return getActivity().getLayoutInflater().inflate(layout, null);
		}

	}

}
