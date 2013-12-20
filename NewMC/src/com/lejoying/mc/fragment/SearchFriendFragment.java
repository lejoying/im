package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.R;

public class SearchFriendFragment extends BaseListFragment {

	LayoutInflater mInflater;

	public static SearchFriendFragment instance;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		mInflater = inflater;
		mMCFragmentManager.showCircleMenuToTop(true, true);

		return inflater.inflate(R.layout.android_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new SearchFriendAdapter());
	}

	class SearchFriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (position) {
			case 0:
				convertView = mInflater.inflate(R.layout.f_group_panel, null);
				TextView tv_groupname = (TextView) convertView
						.findViewById(R.id.tv_groupname);
				tv_groupname.setText("附近好友");
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.f_button, null);
				((Button) convertView).setText("精确查找");
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mMCFragmentManager.replaceToContent(
								new ExactSearchFriendFragment(), true);
					}
				});
				break;
			case 2:
				convertView = mInflater.inflate(R.layout.f_button, null);
				((Button) convertView).setText("扫描名片");
				break;

			default:
				break;
			}
			return convertView;
		}
	}

}
