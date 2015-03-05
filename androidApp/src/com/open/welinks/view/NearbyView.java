package com.open.welinks.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
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
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.StreamParser;

public class NearbyView {

	public String tag = "NearbyView";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

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
				reflashFirst();
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
			log.e("停止了");
		}
	}

	public void reflashFirst() {
		// TODO 获取最新数据
	}

	public float transleteSpeed = 3f;

	public Gson gson = new Gson();

	public boolean isTranslate = false;

	public float touch_pre_x;
	public float touch_pre_y;

	public float percent;

	public float currentPosition;
	public float nextPosition;

	public void fillData() {

		nearbyAdapter = new NearbyAdapter();
		try {
			byte[] bytes = StreamParser.parseToByteArray(thisActivity.getAssets().open("testhot.js"));
			String result = new String(bytes);
			this.hots = gson.fromJson(result, new TypeToken<ArrayList<HotContent>>() {
			}.getType());
			nearbyListView.setAdapter(nearbyAdapter);
			this.nearbyListView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int id = event.getAction();
					if (id == MotionEvent.ACTION_DOWN && isTranslate) {
						openLooper.stop();
						touch_pre_x = event.getX();
						touch_pre_y = event.getY();
						percent = 0;
						currentPosition = -viewManage.screenWidth;
						nextPosition = -viewManage.screenWidth;
					} else if (id == MotionEvent.ACTION_MOVE && isTranslate) {
						float x = event.getX();
						float y = event.getY();
						if (touch_pre_y <= y) {
							if (percent < 0 && y - touch_pre_y > 10) {
								isTranslate = false;
								nextPosition = -viewManage.screenWidth;
								openLooper.start();
							}
							float Δy = y - touch_pre_y;
							touch_pre_x = x;
							touch_pre_y = y;
							View c = nearbyListView.getChildAt(0);
							int firstVisiblePosition = nearbyListView.getFirstVisiblePosition();
							int top = c.getTop();
							int topDistance = -top + firstVisiblePosition * c.getHeight();
							// log.e(nearbyListView.getScrollY() + ":y--" + a + ",,,," + c.getHeight());
							if (topDistance == 0) {
								// log.e("到顶部了，开始滑动");
								percent += Δy;
								currentPosition = (float) (-viewManage.screenWidth + percent * 2);
								if (currentPosition >= 0) {
									currentPosition = 0;
								}
								progressView.setTranslationX(currentPosition);
							}
						} else {
							float Δy = y - touch_pre_y;
							if (percent > 0 && y - touch_pre_y < -10) {
								// isTranslate = false;
								// nextPosition = -viewManage.screenWidth;
								// openLooper.start();
								percent += Δy;
								currentPosition = (float) (-viewManage.screenWidth + percent * 2);
								if (currentPosition >= 0) {
									currentPosition = 0;
								}
								progressView.setTranslationX(currentPosition);
								return false;
							}

							touch_pre_x = x;
							touch_pre_y = y;
							View c = nearbyListView.getChildAt(0);
							int firstVisiblePosition = nearbyListView.getFirstVisiblePosition();
							int top = c.getTop();
							int buttomDistance = -top + firstVisiblePosition * c.getHeight() + nearbyListView.getHeight();
							int totalHeight = c.getHeight() * nearbyListView.getCount();
							log.e(buttomDistance + "--" + totalHeight);
							if (buttomDistance == totalHeight - 2) {
								log.e("qin ,到底了。");
								percent += Δy;
								currentPosition = (float) (-viewManage.screenWidth + Math.abs(percent) * 2);
								if (currentPosition >= 0) {
									currentPosition = 0;
								}
								progressView.setTranslationX(currentPosition);
							}
						}
					} else if (id == MotionEvent.ACTION_UP) {
						log.e("ACTION_UP");
						if (isTranslate) {
							float distance = Math.abs(percent) * 2;
							if (distance > viewManage.screenWidth / 2) {
								nextPosition = 0;
							} else {
								nextPosition = -viewManage.screenWidth;
							}
							openLooper.start();
						}
						isTranslate = true;
					}
					return false;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class TouchStatus {
		public int None = 0, Down = 1, T = 2, B = 3, Up = 0;
		public int state = None;
	}

	public TouchStatus status = new TouchStatus();

	public void fillData2() {

		nearbyAdapter = new NearbyAdapter();
		try {
			byte[] bytes = StreamParser.parseToByteArray(thisActivity.getAssets().open("testhot.js"));
			String result = new String(bytes);
			this.hots = gson.fromJson(result, new TypeToken<ArrayList<HotContent>>() {
			}.getType());
			nearbyListView.setAdapter(nearbyAdapter);
			this.nearbyListView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int id = event.getAction();
					if (id == MotionEvent.ACTION_DOWN) {
						status.state = status.Down;
						openLooper.stop();
						touch_pre_x = event.getX();
						touch_pre_y = event.getY();
						percent = 0;
						currentPosition = -viewManage.screenWidth;
						nextPosition = -viewManage.screenWidth;
						isTranslate = false;
					} else if (id == MotionEvent.ACTION_MOVE) {
						float x = event.getX();
						float y = event.getY();
						if (status.state == status.Down) {
							View firstView = nearbyListView.getChildAt(0);
							int firstVisiblePosition = nearbyListView.getFirstVisiblePosition();
							int top = firstView.getTop();
							int firstViewHeight = firstView.getHeight();
							int topDistance = -top + firstVisiblePosition * firstViewHeight;
							int buttomDistance = topDistance + nearbyListView.getHeight();
							int totalHeight = firstViewHeight * nearbyListView.getCount();
							log.e(buttomDistance + ",,,,," + totalHeight);
							MarginLayoutParams params = (MarginLayoutParams) nearbyListView.getLayoutParams();
							int topMarigin = params.topMargin;
							int a = 2;
							if (topMarigin == (int) (84 * thisView.metrics.density)) {
								a = 4;
							} else {
								a = 2;
							}
							if (topDistance == 0) {
								status.state = status.T;
							} else if (buttomDistance == totalHeight - a) {
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
					} else if (id == MotionEvent.ACTION_UP) {
						log.e("ACTION_UP");
						float distance = Math.abs(percent) * 2;
						if (distance > viewManage.screenWidth / 2) {
							nextPosition = 0;
						} else {
							nextPosition = -viewManage.screenWidth;
						}
						openLooper.start();
						isTranslate = false;
					}
					return isTranslate;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HotContent> hots = new ArrayList<HotContent>();

	public class HotContent {
		public String imageCount;
		public String content;
		public String totalScore;
		public String time;
		public String distance;

	}

	public ViewManage viewManage = ViewManage.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public class NearbyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return hots.size();
		}

		@Override
		public Object getItem(int posotion) {
			return hots.get(posotion);
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
				holder.imageCountView = (TextView) convertView.findViewById(R.id.imageCount);
				holder.distanceView = (TextView) convertView.findViewById(R.id.distance);
				holder.num_picker_decrement = (ImageView) convertView.findViewById(R.id.num_picker_decrement);
				holder.num_picker_increment = (ImageView) convertView.findViewById(R.id.num_picker_increment);
				convertView.setTag(holder);
			} else {
				holder = (HotHolder) convertView.getTag();
			}
			HotContent hotContent = hots.get(position);
			if (hotContent != null) {
				Typeface face = Typeface.createFromAsset(thisActivity.getAssets(), "fonts/avenirroman.ttf");
				holder.scoreView.setTypeface(face);
				holder.textContentView.setText(hotContent.content);
				holder.scoreView.setText(hotContent.totalScore);
				if (Integer.valueOf(hotContent.imageCount) > 4) {
					holder.imageCountView.setText("(共" + hotContent.imageCount + "张)");
				} else {
					holder.imageCountView.setText("");
				}
				holder.distanceView.setText(hotContent.distance + "km");
				if (position % 7 == 0) {
					imageLoader.displayImage("drawable://" + R.drawable.a1, holder.imageContentView);
				} else if (position % 7 == 1) {
					imageLoader.displayImage("drawable://" + R.drawable.a2, holder.imageContentView);
				} else if (position % 7 == 2) {
					imageLoader.displayImage("drawable://" + R.drawable.a3, holder.imageContentView);
				} else if (position % 7 == 3) {
					imageLoader.displayImage("drawable://" + R.drawable.a4, holder.imageContentView);
				} else if (position % 7 == 4) {
					imageLoader.displayImage("drawable://" + R.drawable.a5, holder.imageContentView);
				} else if (position % 7 == 5) {
					imageLoader.displayImage("drawable://" + R.drawable.a6, holder.imageContentView);
				} else if (position % 7 == 6) {
					imageLoader.displayImage("drawable://" + R.drawable.a7, holder.imageContentView);
				}
				holder.num_picker_increment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});
				holder.num_picker_decrement.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});
			}
			return convertView;
		}

		public class HotHolder {
			public TextView scoreView;
			public TextView textContentView;
			public ImageView imageContentView;
			public TextView imageCountView;
			public TextView distanceView;
			public ImageView num_picker_increment;
			public ImageView num_picker_decrement;
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

	public class Address {
		public String name;
		public String address;
		public double longitude;
		public double latitude;
	}

	public void showGroupCircles() {
		if (dialogAdapter == null) {
			addressList = new ArrayList<Address>() {
				private static final long serialVersionUID = 1L;

				{
					Address address1 = new Address();
					address1.name = "哈尔滨市";
					address1.address = "黑龙江省哈尔滨市南岗区奋斗路街道红黄蓝亲子园(华山路)";
					address1.longitude = 126.681934;
					address1.latitude = 45.748676;

					Address address2 = new Address();
					address2.name = "沈阳市";
					address2.address = "辽宁省沈阳市浑南区东湖街道古城子村";
					address2.longitude = 123.583789;
					address2.latitude = 41.734767;

					Address address3 = new Address();
					address3.name = "兴城市";
					address3.address = "辽宁省葫芦岛市兴城市羊安满族乡佟屯村";
					address3.longitude = 120.688892;
					address3.latitude = 40.614296;

					Address address4 = new Address();
					address4.name = "秦皇岛市";
					address4.address = "河北省秦皇岛市海港区文化路街道秦缘宾馆(人民路)";
					address4.longitude = 119.598499;
					address4.latitude = 39.941678;

					Address address5 = new Address();
					address5.name = "北京市";
					address5.address = "北京市东城区天坛街道中华民族艺术珍品馆";
					address5.longitude = 116.409717;
					address5.latitude = 39.889013;

					Address address6 = new Address();
					address6.name = "保定市";
					address6.address = "河北省保定市南市区南关街道南仓路80号";
					address6.longitude = 115.492359;
					address6.latitude = 38.840061;

					Address address7 = new Address();
					address7.name = "定州市";
					address7.address = "河北省保定市定州市南城区街道清风南街71号";
					address7.longitude = 114.992481;
					address7.latitude = 38.511994;

					Address address8 = new Address();
					address8.name = "石家庄市";
					address8.address = "河北省石家庄市桥东区中山东路街道银宏花苑";
					address8.longitude = 114.507709;
					address8.latitude = 38.047367;

					Address address9 = new Address();
					address9.name = "邢台市";
					address9.address = "河北省邢台市桥东区大梁庄乡保险大厦";
					address9.longitude = 114.522815;
					address9.latitude = 37.055539;

					Address address0 = new Address();
					address0.name = "沙河市";
					address0.address = "河北省邢台市沙河市褡裢街道建设路70号";
					address0.longitude = 114.507709;
					address0.latitude = 36.856912;

					Address address01 = new Address();
					address01.name = "邯郸市";
					address01.address = "河北省邯郸市丛台区和平街道浴新北大街51号";
					address01.longitude = 114.478183;
					address01.latitude = 36.609829;

					add(address1);
					add(address2);
					add(address3);
					add(address4);
					add(address5);
					add(address6);
					add(address7);
					add(address8);
					add(address9);
					add(address0);
					add(address01);
				}
			};
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

	private List<Address> addressList;

	public class AddressDialogAdapter extends BaseAdapter {

		public AddressDialogAdapter() {
		}

		@Override
		public void notifyDataSetChanged() {
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
			Address address = addressList.get(position);
			holder.name.setText(address.name);
			holder.address.setText(address.address);
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
		}

		@Override
		public void remove(final int which) {
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
			Address address = addressList.get(position);
			Toast.makeText(thisActivity, address.name, Toast.LENGTH_SHORT).show();
			LatLng mLatLng = new LatLng(address.latitude, address.longitude);
			mAMap.animateCamera(CameraUpdateFactory.changeLatLng(mLatLng), 500, null);
		}
	}
}
