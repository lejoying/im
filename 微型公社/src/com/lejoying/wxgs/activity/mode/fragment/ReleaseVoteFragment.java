package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;

public class ReleaseVoteFragment extends BaseFragment implements
		OnClickListener {
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;

	int height, width, dip;
	float density;

	View sl_content, rl_back, rl_send, rl_sync;
	LinearLayout release_ll;
	EditText release_et;
	int voteCount;
	List<String> voteList;
	Map<Integer, String> voteMap;
	MyListViewAdapter myAdapter = null;

	private List<String> subVoteList;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.release_vote, null);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();

		return mContent;
	}

	void initData() {
		voteCount = 3;
		voteList = new ArrayList<String>();
		voteList.add(0, "");
		voteList.add(1, "");
		voteList.add(2, "");
		subVoteList = new ArrayList<String>();
		// voteMap = new HashMap<Integer, String>();
		// voteMap.put(1, "");
		// voteMap.put(2, "");
		// voteMap.put(3, "");
		// myAdapter = new MyListViewAdapter();
		// release_lv.addFooterView(getFooterView());
		// release_lv.setAdapter(myAdapter);

		// release_ll.addView(child, index);
		initVoteList();
	}

	private void initVoteList() {
		subVoteList.clear();
		release_ll.removeAllViews();
		for (int i = 0; i < voteList.size(); i++) {
			if (voteList.get(i) != null) {
				subVoteList.add(voteList.get(i));
			}
		}
		for (int i = 0; i < voteCount; i++) {
			release_ll.addView(getVoteView(i,subVoteList.get(i)));
		}
		release_ll.addView(getFooterView());

	}

	void initLayout() {
		rl_back = mContent.findViewById(R.id.rl_back);
		rl_send = mContent.findViewById(R.id.rl_send);
		rl_sync = mContent.findViewById(R.id.rl_sync);
		release_ll = (LinearLayout) mContent.findViewById(R.id.release_ll);
		release_et = (EditText) mContent.findViewById(R.id.release_et);
		sl_content = mContent.findViewById(R.id.sl_content);
		LayoutParams params = sl_content.getLayoutParams();
		params.height = height - MainActivity.statusBarHeight
				- (int) (157 * density + 0.5f);
		sl_content.setLayoutParams(params);
	}

	void initEvent() {
		rl_back.setOnClickListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
	}

	void Send() {

	}

	void Sync() {

	}

	private View getVoteView(final int num, String content) {

		View view = mInflater.inflate(R.layout.release_vote_child, null);

		final View ll_background = view.findViewById(R.id.ll_background);
		final ImageView release_vote_clear = (ImageView) view
				.findViewById(R.id.release_vote_clear);
		final TextView release_vote_num = (TextView) view
				.findViewById(R.id.release_vote_num);
		final EditText release_vote_et = (EditText) view
				.findViewById(R.id.release_vote_et);
		release_vote_num.setText(num + 1 + ".");

		if (!content.equals("")) {
			release_vote_et.setText(content);
		}

		release_vote_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					ll_background.setBackgroundResource(R.drawable.border);
					release_vote_clear.setVisibility(View.VISIBLE);
					release_vote_clear
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									voteCount--;
									voteList.set(num, null);
									release_ll.removeViewAt(num);
									initVoteList();
								}
							});
				} else {
					ll_background.setBackgroundColor(Color
							.parseColor("#00000000"));
					release_vote_clear.setVisibility(View.GONE);
					if (!release_vote_et.getText().toString().equals("")) {
						voteList.set(num, release_vote_et.getText().toString());
					}
				}

			}
		});
		return view;
	}

	private View getFooterView() {
		View view = mInflater.inflate(R.layout.release_vote_child, null);

		ImageView release_vote_numadd = (ImageView) view
				.findViewById(R.id.release_voew_numadd);
		TextView release_vote_num = (TextView) view
				.findViewById(R.id.release_vote_num);
		EditText release_vote_et = (EditText) view
				.findViewById(R.id.release_vote_et);
		release_vote_numadd.setVisibility(View.VISIBLE);
		release_vote_num.setVisibility(View.GONE);
		release_vote_et.setHint("添加选项");
		release_vote_et.setEnabled(false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voteCount++;
				voteList.add(voteList.size(), "");
				initVoteList();
			}
		});
		return view;
	}

	public class MyListViewAdapter extends BaseAdapter {

		public MyListViewAdapter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return voteList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return voteList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ListViewHolder listViewHolder;
			if (convertView == null) {
				listViewHolder = new ListViewHolder();
				convertView = mInflater.inflate(R.layout.release_vote_child,
						null);
				listViewHolder.ll_background = convertView
						.findViewById(R.id.ll_background);
				listViewHolder.release_vote_numadd = (ImageView) convertView
						.findViewById(R.id.release_voew_numadd);
				listViewHolder.release_vote_clear = (ImageView) convertView
						.findViewById(R.id.release_vote_clear);
				listViewHolder.release_vote_num = (TextView) convertView
						.findViewById(R.id.release_vote_num);
				listViewHolder.release_vote_et = (EditText) convertView
						.findViewById(R.id.release_vote_et);

				convertView.setTag(listViewHolder);
			} else {
				listViewHolder = (ListViewHolder) convertView.getTag();
			}
			listViewHolder.release_vote_num.setText(position + 1 + ".");

			if (voteList.get(position) != null
					&& !voteList.get(position).equals("")) {
				listViewHolder.release_vote_et.setText(voteList.get(position));
			}

			listViewHolder.release_vote_et
					.setOnFocusChangeListener(new OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								listViewHolder.ll_background
										.setBackgroundResource(R.drawable.border);
								listViewHolder.release_vote_clear
										.setVisibility(View.VISIBLE);
								listViewHolder.release_vote_clear
										.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												voteList.remove(position);
												notifyDataSetChanged();
											}
										});
							} else {
								listViewHolder.ll_background
										.setBackgroundColor(Color
												.parseColor("#00000000"));
								listViewHolder.release_vote_clear
										.setVisibility(View.GONE);
								if (!listViewHolder.release_vote_et.getText()
										.toString().equals("")) {
									voteList.remove(position);
									voteList.add(position,
											listViewHolder.release_vote_et
													.getText().toString());
								}
								notifyDataSetChanged();
							}

						}
					});

			return convertView;
		}

		class ListViewHolder {
			View ll_background;
			ImageView release_vote_numadd, release_vote_clear;
			TextView release_vote_num;
			EditText release_vote_et;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			mMainModeManager.back();
			break;
		case R.id.rl_send:
			Send();
			break;
		case R.id.rl_sync:
			Sync();
			break;

		default:
			break;
		}

	}
}
