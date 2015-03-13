package com.open.welinks.view;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Data.UserInformation.User.Location;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.ResponseHandlers.Share_scoreCallBack2;
import com.open.welinks.model.SubData.ShareContent;
import com.open.welinks.model.SubData.ShareContent.ShareContentItem;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.view.NearbyView.NearbyAdapter.HotHolder;

public class NearbyView {

	public String tag = "NearbyView";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;
	public Context context;

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleContent;
	public RelativeLayout centerContainer;
	public ListView nearbyListView;
	public ThreeChoicesView threeChoicesView;

	public NearbyAdapter nearbyAdapter;

	public int NearbyLayoutID;

	public View maxView;

	public NearbyView(NearbyActivity thisActivity) {
		thisView = this;
		this.thisActivity = thisActivity;
		this.context = thisActivity;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public DisplayMetrics metrics;
	public ImageView menuImage;

	public MapView mapView;
	public AMap mAMap;

	public TextView addressView;
	public TextView sortView;

	public View lbsMapView;

	public View lineView;

	public View searChView;
	public View locationView;

	public View positionView;

	public ImageView ico_map_pin;
	public ImageView ico_map_pin2;
	public ImageView ico_map_pin_shadow2;
	public TextView img_btn_set_start;

	public View progressView;

	public void initView() {

		this.metrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(this.metrics);

		this.mInflater = thisActivity.getLayoutInflater();
		this.thisActivity.setContentView(R.layout.activity_nearby);
		this.backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		RelativeLayout backMaxView = (RelativeLayout) thisActivity.findViewById(R.id.backMaxView);
		backMaxView.setBackgroundColor(Color.parseColor("#eeffffff"));
		this.backTitleContent = (TextView) thisActivity.findViewById(R.id.backTitleView);
		this.nearbyListView = (ListView) thisActivity.findViewById(R.id.nearby);
		this.rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		this.centerContainer = (RelativeLayout) thisActivity.findViewById(R.id.centerContainer);
		this.maxView = thisActivity.findViewById(R.id.maxView);
		this.backTitleContent.setText("");
		this.threeChoicesView = new ThreeChoicesView(thisActivity, 0);
		this.centerContainer.addView(this.threeChoicesView);

		this.addressView = (TextView) thisActivity.findViewById(R.id.address);
		this.sortView = (TextView) thisActivity.findViewById(R.id.sort);

		// this.lineView = thisActivity.findViewById(R.id.line);
		this.searChView = thisActivity.findViewById(R.id.search);
		this.locationView = thisActivity.findViewById(R.id.location);
		this.positionView = thisActivity.findViewById(R.id.position);

		this.lbsMapView = thisActivity.findViewById(R.id.lbsmap);

		this.progressView = thisActivity.findViewById(R.id.progress);
		this.progressView.setVisibility(View.VISIBLE);
		this.progressView.setTranslationX(-viewManage.screenWidth);

		this.ico_map_pin = (ImageView) thisActivity.findViewById(R.id.ico_map_pin);
		this.ico_map_pin2 = (ImageView) thisActivity.findViewById(R.id.ico_map_pin2);
		this.ico_map_pin_shadow2 = (ImageView) thisActivity.findViewById(R.id.ico_map_pin_shadow2);
		this.img_btn_set_start = (TextView) thisActivity.findViewById(R.id.img_btn_set_start);

		int width = (int) (this.metrics.density * 48);

		ImageView lineView = new ImageView(thisActivity);
		lineView.setBackgroundColor(Color.parseColor("#0099cd"));
		int lineWidth = (int) (1 * this.metrics.density);
		int linePadding = (int) (5 * this.metrics.density);
		RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(lineWidth, width - linePadding * 4);
		this.centerContainer.addView(lineView, lineParams);

		RelativeLayout.LayoutParams centerContainerParams = (LayoutParams) this.centerContainer.getLayoutParams();
		// this.centerContainer.setBackgroundColor(Color.parseColor("#380099cd"));
		this.centerContainer.setGravity(Gravity.CENTER_VERTICAL);
		centerContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		centerContainerParams.leftMargin = (int) (this.metrics.density * 53);

		RelativeLayout.LayoutParams threeChoicesViewParams = (LayoutParams) this.threeChoicesView.getLayoutParams();
		threeChoicesViewParams.leftMargin = (int) (((this.metrics.widthPixels - this.metrics.density * 180) / 2) - 48 * this.metrics.density);

		ImageView backImageView = (ImageView) thisActivity.findViewById(R.id.backImageView);
		backImageView.setImageResource(R.drawable.ab3);
		backImageView.setColorFilter(Color.parseColor("#0099cd"));
		backImageView.setAlpha(0.875f);
		RelativeLayout.LayoutParams backImageViewParams = (LayoutParams) backImageView.getLayoutParams();
		backImageViewParams.width = width;
		backImageViewParams.height = width;

		RelativeLayout.LayoutParams params = (LayoutParams) this.backView.getLayoutParams();
		params.height = width;
		params.width = (int) (width + 5 * this.metrics.density);
		this.backView.setPadding(0, 0, (int) (10 * this.metrics.density), 0);

		this.menuImage = new ImageView(thisActivity);
		this.menuImage.setImageResource(R.drawable.button_modifygroupname);
		this.menuImage.setColorFilter(Color.parseColor("#0099cd"));
		this.menuImage.setAlpha(0.875f);
		int moreWidth = (int) (53 * this.metrics.density);
		RelativeLayout.LayoutParams menuImageParams = new RelativeLayout.LayoutParams(moreWidth, width);
		int padding = (int) (5 * this.metrics.density);
		this.menuImage.setPadding(padding, padding, padding, padding);
		this.menuImage.setBackgroundResource(R.drawable.backview_background);
		this.rightContainer.addView(this.menuImage, menuImageParams);

		RelativeLayout.LayoutParams rightLayoutParams = (LayoutParams) this.rightContainer.getLayoutParams();
		rightLayoutParams.rightMargin = 0;

		this.businessCardPopView = new SmallBusinessCardPopView(thisActivity, this.maxView);

		mapView = (MapView) thisActivity.findViewById(R.id.mapView);

		this.initializationGroupCirclesDialog();

		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;
	}

	public OpenLooper openLooper;
	public ListLoopCallback loopCallback;

	public class ListLoopCallback extends LoopCallback {

		public int state;// T B

		public ListLoopCallback(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {
			nextTranslate(ellapsedMillis);
		}
	}

	public void nextTranslate(double ellapsedMillis) {
		float distance = (float) ellapsedMillis * transleteSpeed;
		boolean isStop = false;
		if (nextPosition >= currentPosition) {
			currentPosition += distance;
			if (nextPosition <= currentPosition) {
				currentPosition = nextPosition;
				isStop = true;
				if (loopCallback.state == status.T) {
					reflashFirst();
				} else if (loopCallback.state == status.B) {
					nextPageData();
				}
			}
			progressView.setTranslationX(currentPosition);
		} else {
			currentPosition -= distance;
			if (nextPosition >= currentPosition) {
				currentPosition = nextPosition;
				isStop = true;
			}
			progressView.setTranslationX(currentPosition);
		}
		if (isStop) {
			openLooper.stop();
			isTranslate = false;
			// log.e("停止了");
		}
	}

	public void reflashFirst() {
		thisController.nowpage = 0;
		thisController.searchNearby();
	}

	public void nextPageData() {
		thisController.searchNearby();
	}

	public float transleteSpeed = 3f;

	public Gson gson = new Gson();

	public boolean isTranslate = false;

	public float touch_pre_x;
	public float touch_pre_y;

	public float percent;

	public float currentPosition;
	public float nextPosition;

	public class TouchStatus {
		public int None = 0, Down = 1, T = 2, B = 3, Up = 0;
		public int state = None;
	}

	public TouchStatus status = new TouchStatus();

	public void fillData2() {

		nearbyAdapter = new NearbyAdapter();
		nearbyListView.setAdapter(nearbyAdapter);
		this.nearbyListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int id = event.getAction();
				if (id == MotionEvent.ACTION_DOWN) {
					status.state = status.Down;
					openLooper.stop();
					touch_pre_x = event.getX();
					touch_pre_y = event.getY();
					percent = 0;
					currentPosition = -viewManage.screenWidth;
					nextPosition = -viewManage.screenWidth;
					// isTranslate = false;
					log.e("listview ACTION_DOWN");
					// String view_class = (String) view.getTag(R.id.tag_class);
					// if (view_class != null && view_class.equals("DecrementView")) {
					// onTouchDownView = view;
					// onTouchDownView.setAlpha(1f);
					// } else if (view_class != null && view_class.equals("IncrementView")) {
					// onTouchDownView = view;
					// onTouchDownView.setAlpha(1f);
					// }
				} else if (id == MotionEvent.ACTION_MOVE) {
					float x = event.getX();
					float y = event.getY();
					if (status.state == status.Down) {
						View firstView = nearbyListView.getChildAt(0);
						if (firstView == null) {
							return false;
						}
						int firstVisiblePosition = nearbyListView.getFirstVisiblePosition();
						int top = firstView.getTop();
						int firstViewHeight = firstView.getHeight();
						int topDistance = -top + firstVisiblePosition * firstViewHeight;
						int buttomDistance = topDistance + nearbyListView.getHeight();
						int totalHeight = firstViewHeight * nearbyListView.getCount();
						MarginLayoutParams params = (MarginLayoutParams) nearbyListView.getLayoutParams();
						int topMarigin = params.topMargin;
						int error = 2;
						if (topMarigin == (int) (84 * thisView.metrics.density)) {
							error = 4;
						} else {
							error = 2;
						}
						if (topDistance == 0) {
							status.state = status.T;
						} else if (buttomDistance == totalHeight - error) {
							status.state = status.B;
						}
					} else if (status.state == status.T || status.state == status.B) {
						float Δy = y - touch_pre_y;
						touch_pre_x = x;
						touch_pre_y = y;
						isTranslate = true;
						percent += Δy;
						if (status.state == status.T && percent < 0) {
							status.state = status.Down;
							percent = 0;
							isTranslate = false;
						}
						if (status.state == status.B && percent > 0) {
							status.state = status.Down;
							percent = 0;
							isTranslate = false;
						}
						currentPosition = (float) (-viewManage.screenWidth + Math.abs(percent) * 2);
						if (currentPosition >= 0) {
							currentPosition = 0;
						}
						progressView.setTranslationX(currentPosition);
					}
					log.e("listview ACTION_MOVE");
					onclickScore(MotionEvent.ACTION_MOVE);
				} else if (id == MotionEvent.ACTION_UP) {
					log.e("listview ACTION_UP");
					onclickScore(MotionEvent.ACTION_UP);
					float distance = Math.abs(percent) * 2;
					if (distance > viewManage.screenWidth / 2) {
						nextPosition = 0;
					} else {
						nextPosition = -viewManage.screenWidth;
					}
					isTranslate = false;
					openLooper.start();
					loopCallback.state = status.state;
					status.state = status.Up;
					// isTranslate = false;
				}
				return isTranslate;
			}
		});
	}

	public void onclickScore(int state) {
		if (thisController.onTouchDownView != null) {
			String view_class = (String) thisController.onTouchDownView.getTag(R.id.tag_class);
			if (state == MotionEvent.ACTION_MOVE) {
				if (view_class != null && view_class.equals("DecrementView")) {
					thisController.onTouchDownView.setAlpha(0.125f);
				} else if (view_class != null && view_class.equals("IncrementView")) {
					thisController.onTouchDownView.setAlpha(0.125f);
				}
			}
			if (state == MotionEvent.ACTION_UP) {
				if (view_class != null && view_class.equals("DecrementView")) {
					int position = (Integer) thisController.onTouchDownView.getTag(R.id.tag_first);
					View view = nearbyListView.getChildAt(position);
					if (view == null) {
						return;
					}
					HotHolder holder = (HotHolder) view.getTag();
					ShareMessage shareMessage = thisController.mInfomations.get(position);
					if (shareMessage.scores == null) {
						shareMessage.scores = new HashMap<String, Data.Boards.Score>();
					}
					Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
					if (score == null) {
						score = data.boards.new Score();
					} else {
						if (score.negative > 0) {
							holder.num_picker_decrement.setAlpha(1f);
						} else {
							holder.num_picker_decrement.setAlpha(0.125f);
						}
						if (score.remainNumber == 0) {
							Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					shareMessage.totalScore = shareMessage.totalScore - 1;
					score.phone = data.userInformation.currentUser.phone;
					score.time = new Date().getTime();
					score.negative = 1;
					score.remainNumber = 0;
					shareMessage.scores.put(score.phone, score);
					data.boards.isModified = true;
					int num = shareMessage.totalScore;
					holder.scoreView.setText("" + num);
					if (num < 10 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
					} else if (num < 100 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
					} else if (num < 1000 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
						holder.scoreView.setText("999");
					} else if (num < 0) {
						holder.scoreView.setTextColor(Color.parseColor("#00a800"));
					}
					if (score.negative > 0) {
						holder.num_picker_decrement.setAlpha(1f);
					} else {
						holder.num_picker_decrement.setAlpha(0.125f);
					}
					modifyPraiseusersToMessage(false, shareMessage.gsid);
				} else if (view_class != null && view_class.equals("IncrementView")) {
					int position = (Integer) thisController.onTouchDownView.getTag(R.id.tag_first);
					View view = nearbyListView.getChildAt(position);
					if (view == null) {
						return;
					}
					HotHolder holder = (HotHolder) view.getTag();
					ShareMessage shareMessage = thisController.mInfomations.get(position);
					if (shareMessage.scores == null) {
						shareMessage.scores = new HashMap<String, Data.Boards.Score>();
					}
					Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
					if (score == null) {
						score = data.boards.new Score();
					} else {
						if (score.positive > 0) {
							holder.num_picker_increment.setAlpha(1f);
						} else {
							holder.num_picker_increment.setAlpha(0.125f);
						}
						if (score.remainNumber == 0) {
							Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					shareMessage.totalScore = shareMessage.totalScore + 1;
					score.phone = data.userInformation.currentUser.phone;
					score.time = new Date().getTime();
					score.positive = 1;
					score.remainNumber = 0;
					shareMessage.scores.put(score.phone, score);
					data.boards.isModified = true;
					int num = shareMessage.totalScore;
					holder.scoreView.setText("" + num);
					if (num < 10 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
					} else if (num < 100 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
					} else if (num < 1000 && num >= 0) {
						holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
						holder.scoreView.setText("999");
					} else if (num < 0) {
						holder.scoreView.setTextColor(Color.parseColor("#00a800"));
					}
					if (score.positive > 0) {
						holder.num_picker_increment.setAlpha(1f);
					} else {
						holder.num_picker_increment.setAlpha(0.125f);
					}
					modifyPraiseusersToMessage(true, shareMessage.gsid);
				}
			}
			thisController.onTouchDownView = null;
			thisController.isTouchDown = false;
		}
	}

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public void modifyPraiseusersToMessage(boolean option, String gsid) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("option", option + "");
		Share_scoreCallBack2 callBack = responseHandlers.new Share_scoreCallBack2();
		callBack.option = option;
		httpUtils.send(HttpMethod.POST, API.SHARE_SCORE, params, callBack);
	}

	public ViewManage viewManage = ViewManage.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public class NearbyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return thisController.mInfomations.size();
		}

		@Override
		public Object getItem(int posotion) {
			return thisController.mInfomations.get(posotion);
		}

		@Override
		public long getItemId(int posotion) {
			return posotion;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HotHolder holder;
			if (convertView == null) {
				holder = new HotHolder();
				convertView = mInflater.inflate(R.layout.view_location_hot, null);
				holder.scoreView = (TextView) convertView.findViewById(R.id.score);
				holder.textContentView = (TextView) convertView.findViewById(R.id.textContent);
				holder.imageContentView = (ImageView) convertView.findViewById(R.id.imageContent);
				holder.imageContainer = (RelativeLayout) convertView.findViewById(R.id.imageContainer);
				holder.imageCountView = (TextView) convertView.findViewById(R.id.imageCount);
				holder.distanceView = (TextView) convertView.findViewById(R.id.distance);
				holder.timeView = (TextView) convertView.findViewById(R.id.time);
				holder.num_picker_decrement = (ImageView) convertView.findViewById(R.id.num_picker_decrement);
				holder.num_picker_increment = (ImageView) convertView.findViewById(R.id.num_picker_increment);
				convertView.setTag(holder);
			} else {
				holder = (HotHolder) convertView.getTag();
			}
			ShareMessage message = (ShareMessage) getItem(position);
			if (message != null) {
				Typeface face = Typeface.createFromAsset(thisActivity.getAssets(), "fonts/avenirroman.ttf");
				holder.scoreView.setTypeface(face);
				holder.scoreView.setText(String.valueOf(message.totalScore));
				ShareContent shareContent = thisController.subData.new ShareContent();
				shareContent.shareContentItems = gson.fromJson(message.content, new TypeToken<ArrayList<ShareContentItem>>() {
				}.getType());
				List<String> images = new ArrayList<String>();
				for (ShareContentItem item : shareContent.shareContentItems) {
					if (item.type.equals("text")) {
						holder.textContentView.setText(item.detail);
					} else if (item.type.equals("image")) {
						images.add(item.detail);
					}
				}
				if (images.size() > 4) {
					holder.imageCountView.setText("(共" + images.size() + "张)");
				} else {
					holder.imageCountView.setText("");
				}
				String distance;
				if (message.distance >= 1000) {
					distance = new BigDecimal(message.distance / 1000d, new MathContext(4)).toPlainString();
					if (distance.length() == 1) {
						distance += ".000";
					}
					if (distance.length() == 2) {
						distance += ".00";
					}
					holder.distanceView.setText(distance + "km");
				} else {
					distance = String.valueOf(message.distance);
					holder.distanceView.setText(distance + "m");
				}
				holder.timeView.setText(DateUtil.getChatMessageListTime(message.time));
				// if (position % 7 == 0) {
				// imageLoader.displayImage("drawable://" + R.drawable.a1, holder.imageContentView);
				// } else if (position % 7 == 1) {
				// imageLoader.displayImage("drawable://" + R.drawable.a2, holder.imageContentView);
				// } else if (position % 7 == 2) {
				// imageLoader.displayImage("drawable://" + R.drawable.a3, holder.imageContentView);
				// } else if (position % 7 == 3) {
				// imageLoader.displayImage("drawable://" + R.drawable.a4, holder.imageContentView);
				// } else if (position % 7 == 4) {
				// imageLoader.displayImage("drawable://" + R.drawable.a5, holder.imageContentView);
				// } else if (position % 7 == 5) {
				// imageLoader.displayImage("drawable://" + R.drawable.a6, holder.imageContentView);
				// } else if (position % 7 == 6) {
				// imageLoader.displayImage("drawable://" + R.drawable.a7, holder.imageContentView);
				// }
				if (message.scores != null) {
					Score score = message.scores.get(data.userInformation.currentUser.phone);
					if (score != null) {
						if (score.positive > 0) {
							holder.num_picker_increment.setAlpha(1f);
						}
						if (score.negative > 0) {
							holder.num_picker_decrement.setAlpha(1f);
						}
					} else {
						holder.num_picker_increment.setAlpha(0.125f);
						holder.num_picker_decrement.setAlpha(0.125f);
					}
				} else {
					holder.num_picker_increment.setAlpha(0.125f);
					holder.num_picker_decrement.setAlpha(0.125f);
				}
				holder.imageContainer.removeAllViews();
				showImages(images, holder.imageContainer);
				if (images.size() == 0) {
					holder.imageContainer.setBackgroundColor(Color.parseColor("#380099cd"));
				} else {
					holder.imageContainer.setBackgroundColor(Color.parseColor("#000099cd"));
				}

				holder.num_picker_increment.setTag(R.id.tag_class, "IncrementView");
				holder.num_picker_increment.setOnTouchListener(thisController.mOnTouchListener);
				// holder.num_picker_increment.setOnClickListener(thisController.mOnClickListener);
				holder.num_picker_increment.setColorFilter(Color.parseColor("#0099cd"));
				holder.num_picker_increment.setTag(R.id.tag_first, position);
				holder.num_picker_decrement.setTag(R.id.tag_class, "DecrementView");
				holder.num_picker_decrement.setOnTouchListener(thisController.mOnTouchListener);
				// holder.num_picker_decrement.setOnClickListener(thisController.mOnClickListener);
				holder.num_picker_decrement.setColorFilter(Color.parseColor("#0099cd"));
				holder.num_picker_decrement.setTag(R.id.tag_first, position);
			}
			return convertView;
		}

		public class HotHolder {
			public TextView scoreView;
			public TextView textContentView;
			public ImageView imageContentView;
			public TextView imageCountView;
			public TextView distanceView;
			public TextView timeView;
			public ImageView num_picker_increment;
			public ImageView num_picker_decrement;
			public RelativeLayout imageContainer;
		}
	}

	public void showImages(List<String> list, RelativeLayout container) {
		container.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			if (list.size() == 1) {
				ImageView imageView = new ImageView(context);
				int width = (int) (metrics.density * 90);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
				container.addView(imageView, params);
				File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + "@2_2");
				if (file.exists()) {
					imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
				} else {
					DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q", file.getAbsolutePath());
					downloadFile.view = imageView;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					taskManageHolder.downloadFileList.addDownloadFile(downloadFile);
				}
			} else if (list.size() == 2) {
				ImageView imageView = new ImageView(context);
				int width = (int) (metrics.density * 44.5f);
				int height = (int) (metrics.density * 90);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
				if (i == 0) {
					params.leftMargin = 0;
				} else {
					params.leftMargin = (int) (45.5f * metrics.density);
				}
				container.addView(imageView, params);
				File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + "@1_2");
				if (file.exists()) {
					imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
				} else {
					DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width / 2 + "w_" + width + "h_1c_1e_100q", file.getAbsolutePath());
					downloadFile.view = imageView;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					taskManageHolder.downloadFileList.addDownloadFile(downloadFile);
				}
			} else if (list.size() == 3) {
				ImageView imageView = new ImageView(context);
				int width = (int) (metrics.density * 44.5f);
				int height = (int) (metrics.density * 90);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width * 2);
				String suffix = "";
				String name = "";
				if (i == 0) {
					name = "@1_2";
					params.leftMargin = 0;
					params.height = height;
					suffix = "@" + width / 2 + "w_" + width + "h_1c_1e_100q";
				} else if (i == 1) {
					name = "@1_1";
					params.leftMargin = (int) (width + (metrics.density * 1));
					params.height = width;
					suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
				} else {
					name = "@1_1";
					params.leftMargin = (int) (width + (metrics.density * 1));
					params.topMargin = (int) (width + (metrics.density * 1));
					params.height = width;
					suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
				}
				container.addView(imageView, params);
				File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + name);
				if (file.exists()) {
					imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
				} else {
					DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + suffix, file.getAbsolutePath());
					downloadFile.view = imageView;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					taskManageHolder.downloadFileList.addDownloadFile(downloadFile);
				}
			} else {
				ImageView imageView = new ImageView(context);
				int width = (int) (metrics.density * 44.5f);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
				String suffix = "";
				suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
				String name = "@1_1";
				if (i == 0) {
				} else if (i == 1) {
					params.leftMargin = (int) (width + (metrics.density * 1));
				} else if (i == 2) {
					params.topMargin = (int) (width + (metrics.density * 1));
				} else if (i == 3) {
					params.leftMargin = (int) (width + (metrics.density * 1));
					params.topMargin = (int) (width + (metrics.density * 1));
				} else {
					break;
				}
				container.addView(imageView, params);
				File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + name);
				if (file.exists()) {
					imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
				} else {
					DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + suffix, file.getAbsolutePath());
					downloadFile.view = imageView;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					taskManageHolder.downloadFileList.addDownloadFile(downloadFile);
				}
			}
		}
	}

	public void onResume() {
		businessCardPopView.dismissUserCardDialogView();
		mapView.onResume();
	}

	public void onPause() {
		mapView.onPause();
	}

	public void onDestroy() {
		viewManage.nearbyView = null;
		mapView.onDestroy();
	}

	public void onSaveInstanceState(Bundle outState) {
		mapView.onSaveInstanceState(outState);
	}

	// TODO address list
	public View groupEditor, dialogGroupEditor, dialogView, buttons, manage, buttonOne, buttonTwo, buttonThree, background, onTouchDownView, onLongPressView;
	public PopupWindow popDialogView;
	public DragSortListView groupCircleList;
	public ImageView moreView, rditorLine;
	public TextView dialogGroupEditorConfirm, dialogGroupEditorCancel, groupEditorConfirm, groupEditorCancel, backTitileView, titleView, sectionNameTextView, buttonOneText, buttonTwoText, buttonThreeText;

	public View dialogContainer;

	public View singleButton;

	@SuppressWarnings("deprecation")
	public void initializationGroupCirclesDialog() {
		dialogView = mInflater.inflate(R.layout.dialog_listview, null);
		groupCircleList = (DragSortListView) dialogView.findViewById(R.id.content);
		dialogContainer = dialogView.findViewById(R.id.container);
		buttons = dialogView.findViewById(R.id.buttons);
		manage = dialogView.findViewById(R.id.manage);
		manage.setVisibility(View.GONE);
		background = dialogView.findViewById(R.id.background);
		buttonOne = dialogView.findViewById(R.id.buttonOne);
		buttonTwo = dialogView.findViewById(R.id.buttonTwo);
		buttonThree = dialogView.findViewById(R.id.buttonThree);
		dialogGroupEditor = dialogView.findViewById(R.id.groupEditor);
		rditorLine = (ImageView) dialogView.findViewById(R.id.rditorLine);
		buttonOneText = (TextView) dialogView.findViewById(R.id.buttonOneText);
		buttonTwoText = (TextView) dialogView.findViewById(R.id.buttonTwoText);
		buttonThreeText = (TextView) dialogView.findViewById(R.id.buttonThreeText);
		dialogGroupEditorConfirm = (TextView) dialogView.findViewById(R.id.confirm);
		dialogGroupEditorCancel = (TextView) dialogView.findViewById(R.id.cancel);

		this.singleButton = dialogView.findViewById(R.id.singleButton);
		this.singleButton.setVisibility(View.VISIBLE);

		// buttonOneText.setText("添加地址");
		// buttonOneText.setVisibility(View.INVISIBLE);
		// buttonThreeText.setVisibility(View.INVISIBLE);
		// buttonTwoText.setText("添加地址");
		// buttonThreeText.setText("删除分组");

		popDialogView = new PopupWindow(dialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popDialogView.setBackgroundDrawable(new BitmapDrawable());
		showGroupCircles();
	}

	public void changePopupWindow(boolean isEditor) {
		if (popDialogView.isShowing()) {
			popDialogView.dismiss();
		} else {
			if (buttons.getVisibility() == View.VISIBLE)
				buttons.setVisibility(View.GONE);
			if (isEditor) {
				dialogGroupEditor.setVisibility(View.VISIBLE);
				rditorLine.setVisibility(View.VISIBLE);
				// manage.setVisibility(View.GONE);
			} else {
				dialogGroupEditor.setVisibility(View.GONE);
				rditorLine.setVisibility(View.GONE);
				// manage.setVisibility(View.VISIBLE);
			}

			popDialogView.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	public AddressDialogAdapter dialogAdapter;
	public ListController listController;

	public void showGroupCircles() {
		if (dialogAdapter == null) {
			dialogAdapter = new AddressDialogAdapter();
			groupCircleList.setAdapter(dialogAdapter);
			listController = new ListController(groupCircleList, dialogAdapter);
			groupCircleList.setDropListener(listController);
			groupCircleList.setRemoveListener(listController);
			groupCircleList.setFloatViewManager(listController);
			groupCircleList.setOnTouchListener(listController);
			groupCircleList.setOnItemClickListener(listController);
		} else {
			dialogAdapter.notifyDataSetChanged();
		}
	}

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public class AddressDialogAdapter extends BaseAdapter {
		public List<Location> addressList;

		public AddressDialogAdapter() {
			parser.check();
			if (data.userInformation.currentUser.commonUsedLocations == null)
				data.userInformation.currentUser.commonUsedLocations = new ArrayList<Data.UserInformation.User.Location>();
			addressList = data.userInformation.currentUser.commonUsedLocations;
		}

		@Override
		public void notifyDataSetChanged() {
			parser.check();
			// addressList = data.userInformation.currentUser.commonUsedLocations;
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {
			return addressList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.address_list_dialog_item, null, false);
				holder.selectedStatus = (ImageView) convertView.findViewById(R.id.selectedStatus);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.address = (TextView) convertView.findViewById(R.id.address);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			Location location = addressList.get(position);
			holder.name.setText(location.remark);
			holder.address.setText(location.address);
			return convertView;
		}

		class Holder {
			public ImageView status, selectedStatus;
			public TextView name, address;
		}
	}

	public class ListController extends DragSortController implements DragSortListView.DropListener, DragSortListView.RemoveListener, android.widget.AdapterView.OnItemClickListener {
		private AddressDialogAdapter adapter;

		// private DragSortListView listView;

		public ListController(DragSortListView dslv, AddressDialogAdapter dialogAdapter) {
			super(dslv);
			this.adapter = dialogAdapter;
			// this.listView = dslv;
			setRemoveEnabled(true);
			setRemoveMode(DragSortController.FLING_REMOVE);
			setDragInitMode(DragSortController.ON_LONG_PRESS);
		}

		@Override
		public void drop(int from, int to) {
			List<Location> locations = thisController.data.userInformation.currentUser.commonUsedLocations;
			locations.add(to, locations.remove(from));
			adapter.notifyDataSetChanged();
			thisController.modifyUserCommonUsedLocations();
		}

		@Override
		public void remove(final int which) {
			thisController.data.userInformation.currentUser.commonUsedLocations.remove(which);
			adapter.notifyDataSetChanged();
			thisController.modifyUserCommonUsedLocations();
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			return super.onDown(ev);
		}

		@Override
		public int startDragPosition(MotionEvent ev) {
			return super.dragHandleHitPosition(ev);
		}

		@Override
		public View onCreateFloatView(int position) {
			Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
			long[] pattern = { 100, 100, 300 };
			vibrator.vibrate(pattern, -1);

			View view = adapter.getView(position, null, thisView.groupCircleList);
			view.setBackgroundResource(R.drawable.card_login_background_press);
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			changePopupWindow(false);
			Location location = (Location) dialogAdapter.getItem(position);
			Toast.makeText(thisActivity, location.remark, Toast.LENGTH_SHORT).show();
			LatLng mLatLng = new LatLng(location.latitude, location.longitude);
			mAMap.animateCamera(CameraUpdateFactory.changeLatLng(mLatLng), 500, null);
		}
	}
}