package com.lejoying.wxgs.activity.mode.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.ReleaseImageAndTextActivity;
import com.lejoying.wxgs.activity.ReleaseVoiceActivity;
import com.lejoying.wxgs.activity.ReleaseVoteActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.RefreshableView;
import com.lejoying.wxgs.activity.view.RefreshableView.PullToRefreshListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class GroupShareFragment extends BaseFragment implements OnClickListener {

	private ListAdapter GroupShareAdapter = null;
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;
	HorizontalScrollView gshare_scroll;
	LinearLayout gshare_scroll_ll;
	RelativeLayout gshare_send;
	ListView gshare_lv;
	RefreshableView refreshableView;
	PopupWindow pop;
	View popView;
	int height, width, dip;
	float density;

	Bitmap bm;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(true);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mContent == null) {
			mInflater = inflater;
			mContent = mInflater.inflate(R.layout.f_groupshare, null);
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			density = dm.density;
			dip = (int) (40 * density + 0.5f);
			height = dm.heightPixels;
			width = dm.widthPixels;
			initLayout();
			initData();
		}
		return mContent;
	}

	void initLayout() {
		gshare_scroll = (HorizontalScrollView) mContent
				.findViewById(R.id.gshare_scroll);
		gshare_scroll_ll = (LinearLayout) mContent
				.findViewById(R.id.gshare_scroll_ll);
		gshare_send = (RelativeLayout) mContent
				.findViewById(R.id.rl_gshare_send);
		gshare_lv = (ListView) mContent.findViewById(R.id.gshare_lv);
		refreshableView = (RefreshableView) mContent
				.findViewById(R.id.refreshable_view);

		LayoutParams scrollParams = gshare_scroll.getLayoutParams();
		LayoutParams lvlParams = gshare_lv.getLayoutParams();

		gshare_scroll.setLayoutParams(scrollParams);
		gshare_lv.setLayoutParams(lvlParams);

		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 0);
		gshare_send.setOnClickListener(this);
		initPopupWindow();
	}

	private void initPopupWindow() {
		final View imageandtext, voice, vote, activity, commodity, service, rl_imageandtext, rl_voice, rl_vote, rl_activity, rl_commodity, rl_service;
		popView = mInflater.inflate(R.layout.activity_release_option, null);
		pop = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		imageandtext = popView.findViewById(R.id.reloption_rl_imageandtext);
		voice = popView.findViewById(R.id.reloption_rl_voice);
		vote = popView.findViewById(R.id.reloption_rl_vote);
		activity = popView.findViewById(R.id.reloption_rl_activity);
		commodity = popView.findViewById(R.id.reloption_rl_commodity);
		service = popView.findViewById(R.id.reloption_rl_service);
		rl_imageandtext = popView.findViewById(R.id.rl_imageandtext);
		rl_voice = popView.findViewById(R.id.rl_voice);
		rl_vote = popView.findViewById(R.id.rl_vote);
		rl_activity = popView.findViewById(R.id.rl_activity);
		rl_commodity = popView.findViewById(R.id.rl_commodity);
		rl_service = popView.findViewById(R.id.rl_service);

		imageandtext.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							pop.dismiss();
							startActivity(new Intent(getActivity(),
									ReleaseImageAndTextActivity.class));
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		voice.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							pop.dismiss();
							startActivity(new Intent(getActivity(),
									ReleaseVoiceActivity.class));
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		vote.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							pop.dismiss();
							startActivity(new Intent(getActivity(),
									ReleaseVoteActivity.class));
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		activity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		commodity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		service.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});

		popView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();

			}
		});

	}

	void initData() {
		for (int i = 0; i < 10; i++) {
			View child = mInflater.inflate(R.layout.view_child, null);
			final ImageView iv = (ImageView) child.findViewById(R.id.iv_child);
			app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
					new FileResult() {
						public void onResult(String where, Bitmap bitmap) {
							iv.setImageBitmap(bitmap);
						}
					});
			LinearLayout.LayoutParams headParams = (android.widget.LinearLayout.LayoutParams) iv
					.getLayoutParams();
			headParams.height = (int) (height * 0.05078125f);
			headParams.width = (int) (width * 0.09027778f);
			headParams.rightMargin = (int) (width * 0.027777778f);
			iv.setLayoutParams(headParams);
			gshare_scroll_ll.addView(child);
		}
		bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(
				mInflater.getContext().getResources(), R.drawable.background1),
				width - (int) (22 * density + 0.5f), 410);
		GroupShareAdapter = new GroupShareAdapter();
		gshare_lv.setAdapter(GroupShareAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_gshare_send:
			pop.showAtLocation((View) mContent.getParent(), Gravity.CENTER, 0,
					0);
			break;

		default:
			break;
		}

	}

	public class GroupShareAdapter extends BaseAdapter {

		public GroupShareAdapter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GroupShareHolder groupShareHolder;
			if (convertView == null) {
				groupShareHolder = new GroupShareHolder();
				convertView = mInflater.inflate(
						R.layout.fragment_groupshare_show, null);
				groupShareHolder.gshare_ll = (LinearLayout) convertView
						.findViewById(R.id.gshare_ll);
				groupShareHolder.gshare_head = (ImageView) convertView
						.findViewById(R.id.gshare_head);
				groupShareHolder.gshare_time_iv = (ImageView) convertView
						.findViewById(R.id.gshare_time_iv);
				groupShareHolder.gshare_bigpic = (ImageView) convertView
						.findViewById(R.id.gshare_bigpic);
				groupShareHolder.gshare_name = (TextView) convertView
						.findViewById(R.id.gshare_name);
				groupShareHolder.gshare_time_tv = (TextView) convertView
						.findViewById(R.id.gshare_time_tv);
				groupShareHolder.gshare_content = (TextView) convertView
						.findViewById(R.id.gshare_content);
				groupShareHolder.gshare_praise = (TextView) convertView
						.findViewById(R.id.gshare_praise);
				groupShareHolder.gshare_comment = (TextView) convertView
						.findViewById(R.id.gshare_comment);
				groupShareHolder.gshare_date_tv = (TextView) convertView
						.findViewById(R.id.gshare_date_tv);
				LayoutParams headParams = groupShareHolder.gshare_head
						.getLayoutParams();
				headParams.height = (int) (height * 0.05078125f);
				headParams.width = (int) (width * 0.09027778f);
				groupShareHolder.gshare_head.setLayoutParams(headParams);
				groupShareHolder.gshare_date_tv.setTextSize(
						TypedValue.COMPLEX_UNIT_PX, width * 0.06944444f);
				convertView.setTag(groupShareHolder);
			} else {
				groupShareHolder = (GroupShareHolder) convertView.getTag();
			}
			if (position == 5) {
				groupShareHolder.gshare_date_tv.setVisibility(View.VISIBLE);
				groupShareHolder.gshare_date_tv.setText("3000.13.32");
			} else {
				groupShareHolder.gshare_bigpic.setImageBitmap(bm);
				groupShareHolder.gshare_name.setText(app.data.user.nickName);
				groupShareHolder.gshare_time_tv.setText("00:00");
				groupShareHolder.gshare_praise.setText("10");
				groupShareHolder.gshare_comment.setText("10");
				groupShareHolder.gshare_content.setText("123456");
				app.fileHandler.getHeadImage(app.data.user.head,
						app.data.user.sex, new FileResult() {

							public void onResult(String where, Bitmap bitmap) {
								groupShareHolder.gshare_head
										.setImageBitmap(bitmap);
							}
						});
			}
			return convertView;
		}

		class GroupShareHolder {
			LinearLayout gshare_ll;
			ImageView gshare_head, gshare_time_iv, gshare_bigpic;
			TextView gshare_name, gshare_time_tv, gshare_content,
					gshare_praise, gshare_comment, gshare_date_tv;
		}
	}

}