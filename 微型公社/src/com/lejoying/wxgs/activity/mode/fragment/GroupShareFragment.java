package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.ReleaseImageAndTextActivity;
import com.lejoying.wxgs.activity.ReleaseVoiceActivity;
import com.lejoying.wxgs.activity.ReleaseVoteActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.RefreshableView;
import com.lejoying.wxgs.activity.view.RefreshableView.PullToRefreshListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.parser.JSONParser;

public class GroupShareFragment extends BaseFragment implements OnClickListener {

	private GroupShareAdapter groupShareAdapter = null;
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

	public static String mCurrentGroupShareID = "228";
	int nowpage = 0;
	int pagesize = 30;

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
			// mCurrentGroupShareID = app.data.groups.get(0);
			initData();
			getGroupShares();
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
				getGroupShares();
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
		// ArrayList<GroupShare> groupShares = app.data.groupsMap
		// .get(mCurrentGroupShareID).groupShares;
		// if (groupShares != null)
		// groupShareAdapter = new GroupShareAdapter(groupShares);
		// gshare_lv.setAdapter(groupShareAdapter);
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

	class GroupShareAdapter extends BaseAdapter {

		ArrayList<GroupShare> groupShares = new ArrayList<GroupShare>();

		public GroupShareAdapter(ArrayList<GroupShare> groupShares) {
			this.groupShares = groupShares;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return groupShares.size();
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
			GroupShare groupShare = groupShares.get(position);
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
				groupShareHolder.gshare_name.setText(groupShare.time + "");
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

	public void getGroupShares() {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_GETSHARES;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentGroupShareID);
				params.put("nowpage", nowpage + "");
				params.put("pagesize", pagesize + "");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final ArrayList<GroupShare> shares = JSONParser
							.generateSharesFromJSON(jData
									.getJSONArray("shares"));
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
//							ArrayList<GroupShare> groupShares = data.groupsMap
//									.get(mCurrentGroupShareID).groupShares;
//							int index = 0;
//							for (int i = 0; i < shares.size(); i++) {
//								GroupShare share = shares.get(i);
//								if (!groupShares.contains(share)) {
//									if (nowpage == 0) {
//										groupShares.add(index, share);
//										index++;
//									} else if (nowpage > 0) {
//										groupShares.add(share);
//									}
//								}
//							}
						}

						@Override
						public void modifyUI() {
							refreshableView.finishRefreshing();
							groupShareAdapter = new GroupShareAdapter(shares);
							gshare_lv.setAdapter(groupShareAdapter);
							// groupShareAdapter.notifyDataSetChanged();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}