package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	InputMethodManager imm;

	int height, width, dip;
	float density;

	View sl_content, rl_back, rl_send, rl_sync;
	LinearLayout release_ll;
	EditText release_et;
	List<String> voteList;
	Map<Integer, String> voteMap;

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
		initVoteList();
	}

	private void initVoteList() {
		release_ll.removeAllViews();
		for (int i = 0; i < voteList.size(); i++) {
			release_ll.addView(getVoteView(i, voteList.get(i)));
		}
		release_ll.addView(getFooterView());
	}

	void modifyVoteOptionNumber() {
		for (int i = 0; i < release_ll.getChildCount(); i++) {
			View v = release_ll.getChildAt(i);
			TextView tv = (TextView) v.findViewById(R.id.release_vote_num);
			tv.setText(i + 1 + "");
		}
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
		JSONObject voteContent = new JSONObject();
		String voteTitle = release_et.getText().toString().trim();
		if (!"".equals(voteTitle)) {
			Alert.showMessage("投票标题不能为空");
		}
		try {
			voteContent.put("title", voteTitle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		boolean checkTrim = true;
		JSONArray voteOptionsContent = new JSONArray();
		for (int i = 0; i < release_ll.getChildCount(); i++) {
			View v = release_ll.getChildAt(i);
			EditText voteOptionView = (EditText) v
					.findViewById(R.id.release_vote_et);
			String voteOptionContent = voteOptionView.getText().toString()
					.trim();
			if (!"".equals(voteOptionContent)) {
				checkTrim = false;
				Alert.showMessage("投票选项不能为空");
				break;
			}
			JSONObject optionContent = new JSONObject();
			try {
				optionContent.put("id", (i + 1) + "");
				optionContent.put("content", voteOptionContent);
				optionContent.put("voteusers", new JSONArray());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			voteOptionsContent.put(optionContent);
		}
		if (checkTrim) {
			try {
				voteContent.put("options", voteOptionsContent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// generateMessageParams("vote", voteContent.toString());
		}
	}

	public Map<String, String> generateMessageParams(String type, String content) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("gid", GroupShareFragment.mCurrentGroupShareID);
		JSONObject messageObject = new JSONObject();
		try {
			messageObject.put("type", type);
			messageObject.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("message", messageObject.toString());
		return params;
	}

	void Sync() {

	}

	private View getVoteView(final int order, final String content) {

		View view = mInflater.inflate(R.layout.release_vote_child, null);

		final View ll_background = view.findViewById(R.id.ll_background);
		final ImageView release_vote_clear = (ImageView) view
				.findViewById(R.id.release_vote_clear);
		final TextView release_vote_num = (TextView) view
				.findViewById(R.id.release_vote_num);
		final EditText release_vote_et = (EditText) view
				.findViewById(R.id.release_vote_et);
		release_vote_num.setText(order + 1 + ".");

		if (!content.equals("")) {
			release_vote_et.setText(content);
		}
		release_vote_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				if (release_ll.getChildCount() > 3) {
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								release_vote_et.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					release_ll.removeView((View) release_vote_clear.getParent()
							.getParent().getParent());
					// initVoteList();
					modifyVoteOptionNumber();
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
					// initVoteList();
					// release_ll.requestFocus();
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
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						if (release_ll.getChildCount() < 6) {
							release_ll.addView(getVoteView(0, ""),
									release_ll.getChildCount() - 1);
							modifyVoteOptionNumber();
						} else {
							Alert.showMessage("最多不能超过5个选项");
						}
					}
				});
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
