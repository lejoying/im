package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.Alert;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReleaseVoteActivity extends Activity implements OnClickListener,
		OnTouchListener {

	LayoutInflater mInflater;
	InputMethodManager imm;

	int height, width, dip;
	float density;

	View sl_content, rl_back, rl_send, rl_sync;
	LinearLayout release_ll;
	EditText release_et;
	List<String> voteList;
	Map<Integer, String> voteMap;

	private List<Map<String, Object>> subVoteList;
	GestureDetector backViewDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_vote);
		mInflater = (LayoutInflater) ReleaseVoteActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();
	}

	void initData() {
		voteList = new ArrayList<String>();
		voteList.add(0, "");
		voteList.add(1, "");
		voteList.add(2, "");
		subVoteList = new ArrayList<Map<String, Object>>();
		initVoteList();
	}

	private void initVoteList() {
		subVoteList.clear();
		for (int i = 0; i < voteList.size(); i++) {
			if (voteList.get(i) != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("location", i);
				map.put("content", voteList.get(i));
				subVoteList.add(map);
			}
		}

		release_ll.removeAllViews();
		for (int i = 0; i < subVoteList.size(); i++) {
			release_ll.addView(getVoteView(i, subVoteList.get(i)));
		}
		release_ll.addView(getFooterView());

	}

	void initLayout() {
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
		release_ll = (LinearLayout) findViewById(R.id.release_ll);
		release_et = (EditText) findViewById(R.id.release_et);
		sl_content = findViewById(R.id.sl_content);
		// LayoutParams params = sl_content.getLayoutParams();
		// params.height = height - MainActivity.statusBarHeight
		// - (int) (157 * density + 0.5f);
		// sl_content.setLayoutParams(params);
	}

	void initEvent() {
		rl_back.setOnTouchListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
		backViewDetector = new GestureDetector(ReleaseVoteActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						finish();
						return true;
					}
				});
	}

	void Send() {

	}

	void Sync() {

	}

	private View getVoteView(final int order, final Map<String, Object> map) {

		View view = mInflater.inflate(R.layout.release_vote_child, null);

		final View ll_background = view.findViewById(R.id.ll_background);
		final ImageView release_vote_clear = (ImageView) view
				.findViewById(R.id.release_vote_clear);
		final TextView release_vote_num = (TextView) view
				.findViewById(R.id.release_vote_num);
		final EditText release_vote_et = (EditText) view
				.findViewById(R.id.release_vote_et);
		release_vote_num.setText(order + 1 + ".");

		if (!map.get("content").equals("")) {
			release_vote_et.setText((String) map.get("content"));
		}
		release_vote_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				if (release_ll.getChildCount() > 3) {
					voteList.set((Integer) (map.get("location")), null);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								release_vote_et.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					release_ll.removeViewAt(order);
					initVoteList();
				} else {
					Alert.showMessage("最少不能少于2个选项");
				}
			}
		});
		release_vote_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					ll_background.setBackgroundResource(R.drawable.border);
					release_vote_clear.setVisibility(View.VISIBLE);
				} else {
					ll_background.setBackgroundColor(Color
							.parseColor("#00000000"));
					release_vote_clear.setVisibility(View.GONE);
					if (!release_vote_et.getText().toString().equals("")) {
						voteList.set((Integer) (map.get("location")),
								release_vote_et.getText().toString());
					}
					initVoteList();
					release_ll.requestFocus();
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
		TextView release_vote_tv = (TextView) view
				.findViewById(R.id.release_vote_tv);
		EditText release_vote_et = (EditText) view
				.findViewById(R.id.release_vote_et);

		release_vote_numadd.setVisibility(View.VISIBLE);
		release_vote_num.setVisibility(View.GONE);
		release_vote_et.setVisibility(View.GONE);
		release_vote_tv.setVisibility(View.VISIBLE);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voteList.add(voteList.size(), "");
				initVoteList();
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.rl_back:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_back.setBackgroundColor(Color.argb(143, 0, 0, 0));
				break;
			case MotionEvent.ACTION_UP:
				// playSoundEffect(SoundEffectConstants.CLICK);
				rl_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
				break;
			}
			break;
		}
		return backViewDetector.onTouchEvent(event);
	}

}
