package com.open.welinks.view;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
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
import com.open.welinks.R;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.controller.NearbyController.LBSStatus;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Data.UserInformation.User.Location;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.ResponseHandlers.Share_scoreCallBack2;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.view.NearbyView.NearbyShareAdapter.HotHolder;

public class NearbyView {

	public String tag = "NearbyView";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public Activity thisActivity;
	public Context context;

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleContent;
	public RelativeLayout centerContainer;
	public ListView nearbyListView;
	public ThreeChoicesView threeChoicesView;

	public NearbyShareAdapter nearbyShareAdapter;
	public NearbyRelationAdapter nearbyRelationAdapter;

	public View maxView;

	public NearbyView(Activity thisActivity) {
		thisView = this;
		this.thisActivity = thisActivity;
		this.context = thisActivity;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public DisplayMetrics metrics;
	public ImageView shareMenuImage, releationMenuImage;

	public MapView mapView;
	public AMap mAMap;
	public Circle ampCircle;
	public CircleOptions circleOptions;

	public TextView addressView;
	public TextView sortView;

	public View lbsMapView;

	public View lineView;

	public View searChView;
	public View locationView;

	public View positionView;

	public ImageView screen;
	public ImageView ico_map_pin;
	public ImageView ico_map_pin2;
	public ImageView ico_map_pin_shadow2;
	public TextView img_btn_set_start;

	public View menuOptions, optionOne, optionTwo;
	public TextView optionOneText, optionTwoText;
	public ImageView optionOneImage, optionTwoImage;

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
		this.searChView.setVisibility(View.GONE);
		this.locationView = thisActivity.findViewById(R.id.location);
		this.positionView = thisActivity.findViewById(R.id.position);
		this.screen = (ImageView) thisActivity.findViewById(R.id.screen);

		this.lbsMapView = thisActivity.findViewById(R.id.lbsmap);

		this.progressView = thisActivity.findViewById(R.id.progress);
		this.progressView.setVisibility(View.VISIBLE);
		this.progressView.setTranslationX(-viewManage.screenWidth);

		this.ico_map_pin = (ImageView) thisActivity.findViewById(R.id.ico_map_pin);
		this.ico_map_pin2 = (ImageView) thisActivity.findViewById(R.id.ico_map_pin2);
		this.ico_map_pin_shadow2 = (ImageView) thisActivity.findViewById(R.id.ico_map_pin_shadow2);
		this.img_btn_set_start = (TextView) thisActivity.findViewById(R.id.img_btn_set_start);

		this.menuOptions = thisActivity.findViewById(R.id.menuOptions);
		this.optionOne = thisActivity.findViewById(R.id.optionOne);
		this.optionTwo = thisActivity.findViewById(R.id.optionTwo);
		this.optionOneImage = (ImageView) thisActivity.findViewById(R.id.optionOneImage);
		this.optionTwoImage = (ImageView) thisActivity.findViewById(R.id.optionTwoImage);
		this.optionOneText = (TextView) thisActivity.findViewById(R.id.optionOneText);
		this.optionTwoText = (TextView) thisActivity.findViewById(R.id.optionTwoText);

		int width = (int) (this.metrics.density * 48);

		RelativeLayout.LayoutParams centerContainerParams = (LayoutParams) this.centerContainer.getLayoutParams();
		// this.centerContainer.setBackgroundColor(Color.parseColor("#380099cd"));
		this.centerContainer.setGravity(Gravity.CENTER_VERTICAL);
		centerContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		centerContainerParams.leftMargin = (int) (this.metrics.density * 53);

		RelativeLayout.LayoutParams threeChoicesViewParams = (LayoutParams) this.threeChoicesView.getLayoutParams();
		threeChoicesViewParams.leftMargin = (int) (((this.metrics.widthPixels - this.metrics.density * 180) / 2) - 48 * this.metrics.density);

		RelativeLayout.LayoutParams params = (LayoutParams) this.backView.getLayoutParams();
		params.height = width;
		params.width = (int) (width + 5 * this.metrics.density);
		this.backView.setPadding(0, 0, (int) (10 * this.metrics.density), 0);
		ImageView backImageView = (ImageView) thisActivity.findViewById(R.id.backImageView);

		if (thisController.status == LBSStatus.account || thisController.status == LBSStatus.group) {
			ImageView lineView = new ImageView(thisActivity);
			lineView.setBackgroundColor(Color.parseColor("#0099cd"));
			int lineWidth = (int) (1 * this.metrics.density);
			int linePadding = (int) (5 * this.metrics.density);
			RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(lineWidth, width - linePadding * 4);
			this.centerContainer.addView(lineView, lineParams);

			backImageView.setImageResource(R.drawable.ab3);
			backImageView.setColorFilter(Color.parseColor("#0099cd"));
			backImageView.setAlpha(0.875f);
			RelativeLayout.LayoutParams backImageViewParams = (LayoutParams) backImageView.getLayoutParams();
			backImageViewParams.width = width;
			backImageViewParams.height = width;

			this.releationMenuImage = new ImageView(thisActivity);
			this.releationMenuImage.setImageResource(R.drawable.chat_add_off);
			this.releationMenuImage.setColorFilter(Color.parseColor("#0099cd"));
			this.releationMenuImage.setAlpha(0.875f);
			int moreWidth = (int) (29 * this.metrics.density);
			RelativeLayout.LayoutParams menuImageParams = new RelativeLayout.LayoutParams(moreWidth, moreWidth);
			int padding = (int) (5 * this.metrics.density);
			this.releationMenuImage.setBackgroundResource(R.drawable.backview_background);
			this.rightContainer.addView(this.releationMenuImage, menuImageParams);
			this.rightContainer.setPadding(padding, padding, 3 * padding, padding);
			this.threeChoicesView.setButtonOneText("附近的群");
			this.threeChoicesView.setButtonThreeText("附近的人");
		} else {
			backImageView.setVisibility(View.GONE);
			TextView textView = new TextView(thisActivity);
			textView.setText("发现");
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
			textView.setTextColor(Color.parseColor("#0099cd"));
			RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			int padding2 = (int) (10 * metrics.density);
			textView.setPadding(padding2 * 2, padding2, padding2 * 2, padding2);
			backMaxView.addView(textView, textViewParams);

			this.shareMenuImage = new ImageView(thisActivity);
			this.shareMenuImage.setImageResource(R.drawable.button_modifygroupname);
			this.shareMenuImage.setColorFilter(Color.parseColor("#0099cd"));
			this.shareMenuImage.setAlpha(0.875f);
			// int moreWidth = (int) (48 * this.metrics.density);
			// RelativeLayout.LayoutParams menuImageParams = new RelativeLayout.LayoutParams(10, 10);
			int padding = (int) (5 * this.metrics.density);
			this.shareMenuImage.setPadding(padding, padding, padding, padding);
			this.shareMenuImage.setBackgroundResource(R.drawable.backview_background);
			this.rightContainer.addView(this.shareMenuImage);
			this.threeChoicesView.setButtonOneText("最新");
			this.threeChoicesView.setButtonThreeText("最热");
		}
		this.threeChoicesView.setTwoChoice();
		if (thisController.status == LBSStatus.account || thisController.status == LBSStatus.hottest) {
			this.threeChoicesView.setDefaultItem(3);
		} else {
			this.threeChoicesView.setDefaultItem(1);
		}
		RelativeLayout.LayoutParams rightLayoutParams = (LayoutParams) this.rightContainer.getLayoutParams();
		rightLayoutParams.rightMargin = 0;

		this.businessCardPopView = new SmallBusinessCardPopView(thisActivity, this.maxView);

		mapView = (MapView) thisActivity.findViewById(R.id.mapView);
		this.mAMap = thisView.mapView.getMap();

		this.initializationGroupCirclesDialog();
		this.initializationScreenDialog();

		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;
		openLooper1 = new OpenLooper();
		openLooper1.createOpenLooper();
		loopCallback1 = new ListLoopCallback1(openLooper1);
		openLooper1.loopCallback = loopCallback1;
	}

	public OpenLooper openLooper;
	public ListLoopCallback loopCallback;
	public OpenLooper openLooper1;
	public ListLoopCallback1 loopCallback1;

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
			transleteSpeed = 3f;
			// log.e("停止了");
		}
	}

	public void reflashFirst() {
		thisController.nowpage = 0;
		thisController.searchNearbyLBS(true);

	}

	public void nextPageData() {
		thisController.searchNearbyLBS(true);
	}

	public float transleteSpeed = 3f;// 3f

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

	public void fillData() {
		if (thisController.status == LBSStatus.account || thisController.status == LBSStatus.group) {
			nearbyRelationAdapter = new NearbyRelationAdapter();
			nearbyListView.setAdapter(nearbyRelationAdapter);
		} else {
			nearbyShareAdapter = new NearbyShareAdapter();
			nearbyListView.setAdapter(nearbyShareAdapter);
		}
	}

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public void modifyPraiseusersToMessage(boolean option, String gsid, double[] location) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gsid", gsid);
		params.addBodyParameter("option", option + "");
		if (location != null) {
			params.addBodyParameter("location", "[" + location[0] + "," + location[1] + "]");
		} else {
			log.e("location == null:::::::::::");
		}
		Share_scoreCallBack2 callBack = responseHandlers.new Share_scoreCallBack2();
		callBack.option = option;
		httpUtils.send(HttpMethod.POST, API.SHARE_SCORE, params, callBack);
	}

	public ViewManage viewManage = ViewManage.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public class NearbyShareAdapter extends BaseAdapter {

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
				holder.imageContainer = (RelativeLayout) convertView.findViewById(R.id.imageContainer);
				holder.imageCountView = (TextView) convertView.findViewById(R.id.imageCount);
				holder.distanceView = (TextView) convertView.findViewById(R.id.distance);
				holder.timeView = (TextView) convertView.findViewById(R.id.time);
				holder.num_picker_decrement = (ImageView) convertView.findViewById(R.id.num_picker_decrement);
				holder.num_picker_increment = (ImageView) convertView.findViewById(R.id.num_picker_increment);
				holder.imageTextContentView = (TextView) convertView.findViewById(R.id.imageTextContent);
				holder.progressView = convertView.findViewById(R.id.progress);
				convertView.setTag(holder);
			} else {
				holder = (HotHolder) convertView.getTag();
			}
			ShareMessage message = (ShareMessage) getItem(position);
			if (message != null) {
				holder.imageTextContentView.setText("");
				Typeface face = Typeface.createFromAsset(thisActivity.getAssets(), "fonts/avenirroman.ttf");
				holder.scoreView.setTypeface(face);
				holder.scoreView.setText(String.valueOf(message.totalScore));
				String content = message.content;
				// content = new String(Base64Coder.decode(content));
				// // log.e(content);
				// if (content.lastIndexOf("@") == content.length() - 1) {
				// content = content.substring(0, content.length() - 1);
				// }
				List<ShareContentItem> shareContentItems = gson.fromJson(content, new TypeToken<ArrayList<ShareContentItem>>() {
				}.getType());
				List<String> images = new ArrayList<String>();
				for (ShareContentItem item : shareContentItems) {
					if (item.type.equals("text")) {
						holder.textContentView.setText(item.detail);
						holder.imageTextContentView.setText(item.detail);
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
				} else if (message.distance <= Constant.DEFAULMINDISTANCE) {
					holder.distanceView.setText(Constant.DEFAULMINDISTANCESTRING);
				} else {
					holder.distanceView.setText((int) message.distance + "m");
				}
				holder.timeView.setText(DateUtil.getNearShareTime(message.time));
				if ("synchronous".equals(message.getStatus)) {
					holder.timeView.setTextColor(Color.parseColor("#580099cd"));
				} else {
					holder.timeView.setTextColor(Color.parseColor("#B9B6AF"));
				}
				if (message.scores != null) {
					Score score = message.scores.get(data.userInformation.currentUser.phone);
					if (score != null) {
						if (score.positive > 0) {
							holder.num_picker_increment.setImageResource(R.drawable.num_picker_increment_on);
							holder.num_picker_decrement.setImageResource(R.drawable.selector_num_picker_decrement);
						} else if (score.negative > 0) {
							holder.num_picker_increment.setImageResource(R.drawable.selector_num_picker_increment);
							holder.num_picker_decrement.setImageResource(R.drawable.num_picker_decrement_on);
						}
						if (score.positive > 0 && score.negative > 0) {
							log.e(gson.toJson(message.scores));
						}
					} else {
						holder.num_picker_increment.setImageResource(R.drawable.selector_num_picker_increment);
						holder.num_picker_decrement.setImageResource(R.drawable.selector_num_picker_decrement);
					}
				} else {
					holder.num_picker_increment.setImageResource(R.drawable.selector_num_picker_increment);
					holder.num_picker_decrement.setImageResource(R.drawable.selector_num_picker_decrement);
				}
				if (message.totalScore < 10 && message.totalScore >= 0) {
					holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
				} else if (message.totalScore < 100 && message.totalScore >= 0) {
					holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
				} else if (message.totalScore < 1000 && message.totalScore >= 0) {
					holder.scoreView.setTextColor(Color.parseColor("#0099cd"));
					holder.scoreView.setText("999");
				} else if (message.totalScore < 0) {
					holder.scoreView.setTextColor(Color.parseColor("#00a800"));
				}
				holder.imageContainer.removeAllViews();
				showImages(message.gsid, images, holder.imageContainer);
				if (images.size() == 0) {
					holder.imageContainer.setBackgroundColor(Color.parseColor("#380099cd"));
					holder.imageTextContentView.setVisibility(View.VISIBLE);
				} else {
					holder.imageContainer.setBackgroundColor(Color.parseColor("#000099cd"));
					holder.imageTextContentView.setVisibility(View.GONE);
				}

				if ("sent".equals(message.status)) {
					holder.progressView.setTranslationX(viewManage.screenWidth);
				} else {

				}

				holder.num_picker_increment.setTag(R.id.tag_class, "IncrementView");
				holder.num_picker_increment.setOnClickListener(thisController.mOnClickListener);
				holder.num_picker_increment.setTag(R.id.tag_first, position);
				holder.num_picker_decrement.setTag(R.id.tag_class, "DecrementView");
				holder.num_picker_decrement.setOnClickListener(thisController.mOnClickListener);
				holder.num_picker_decrement.setTag(R.id.tag_first, position);
			}
			return convertView;
		}

		public class HotHolder {
			public TextView scoreView, textContentView, imageCountView, distanceView, timeView, imageTextContentView;
			public ImageView num_picker_increment, num_picker_decrement;
			public RelativeLayout imageContainer;
			public View progressView;
		}
	}

	public HashMap<String, RelativeLayout> imagesStack = new HashMap<String, RelativeLayout>();

	public void showImages(String key, List<String> list, RelativeLayout container) {
		container.removeAllViews();
		RelativeLayout layout = imagesStack.get(key);
		if (layout == null) {
			layout = new RelativeLayout(thisActivity);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (90 * thisView.metrics.density));
			layout.setLayoutParams(layoutParams);
			imagesStack.put(key, layout);
			for (int i = 0; i < list.size(); i++) {
				if (list.size() == 1) {
					ImageView imageView = new ImageView(context);
					int width = (int) (metrics.density * 90);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
					layout.addView(imageView, params);
					File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + "@2_2");
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width + "w_" + width + "h_1c_1e_100q", file.getAbsolutePath());
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
					layout.addView(imageView, params);
					File file = new File(taskManageHolder.fileHandler.sdcardCacheImageFolder, list.get(i) + "@1_2");
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width + "w_" + height + "h_1c_1e_100q", file.getAbsolutePath());
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
						suffix = "@" + width + "w_" + height + "h_1c_1e_100q";
					} else if (i == 1) {
						name = "@1_1";
						params.leftMargin = (int) (width + (metrics.density * 1));
						params.height = width;
						suffix = "@" + width + "w_" + width + "h_1c_1e_100q";
					} else {
						name = "@1_1";
						params.leftMargin = (int) (width + (metrics.density * 1));
						params.topMargin = (int) (width + (metrics.density * 1));
						params.height = width;
						suffix = "@" + width + "w_" + width + "h_1c_1e_100q";
					}
					layout.addView(imageView, params);
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
					suffix = "@" + width + "w_" + width + "h_1c_1e_100q";
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
					layout.addView(imageView, params);
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
		if (layout.getParent() != null) {
			((ViewGroup) layout.getParent()).removeView(layout);
			// layout.getParent().recomputeViewAttributes(layout);
		}
		try {
			container.addView(layout);
		} catch (Exception e) {
			// e.printStackTrace();
			ArrayList<String> list21 = new ArrayList<String>();
			for (int i = 0; i < thisController.mInfomations.size(); i++) {
				ShareMessage message = (ShareMessage) thisController.mInfomations.get(i);
				list21.add(message.gsid);
			}
			log.e("数据错误----------:" + gson.toJson(list21));
		}
	}

	public class ListLoopCallback1 extends LoopCallback {

		public int state;// T B

		public ListLoopCallback1(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {
			updateShareProgress(ellapsedMillis);
		}
	}

	public float progressDeep = 0.6f;

	public void updateShareProgress(double millis) {
		float distance = (float) (millis * this.progressDeep);
		ArrayList<String> needDelete = null;
		boolean isRunning = false;
		for (int i = 0; i < this.sendingSequence.size(); i++) {
			String key = this.sendingSequence.get(i);
			SendShare sendShare = this.sendingShareMessage.get(key);
			if (sendShare != null) {
				int position = sendShare.position;
				sendShare.percent += distance;
				if (sendShare.percent > viewManage.screenWidth) {
					sendShare.percent = viewManage.screenWidth;
					if (needDelete == null) {
						needDelete = new ArrayList<String>();
					}
					needDelete.add(key);
				}
				int firstVisiblePosition = this.nearbyListView.getFirstVisiblePosition();
				int lastVisiblePosition = this.nearbyListView.getLastVisiblePosition();
				if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
					View view = this.nearbyListView.getChildAt(position - firstVisiblePosition);
					if (view.getTag() instanceof HotHolder) {
						HotHolder holder = (HotHolder) view.getTag();
						holder.progressView.setTranslationX(sendShare.percent);
					}
				}
				isRunning = true;
			} else {
				if (needDelete == null) {
					needDelete = new ArrayList<String>();
				}
				needDelete.add(key);
			}
		}
		if (needDelete != null) {
			this.sendingSequence.removeAll(needDelete);
			for (int i = 0; i < this.sendingSequence.size(); i++) {
				String key = this.sendingSequence.get(i);
				SendShare sendShare = this.sendingShareMessage.get(key);
				if (sendShare != null) {
					sendShare.position = i;
				}
			}
		}
		if (!isRunning) {
			this.openLooper1.stop();
			notifyData(false);
			log.e("Stop.....................");
		}
	}

	public ArrayList<String> sendingSequence = new ArrayList<String>();
	public HashMap<String, SendShare> sendingShareMessage = new HashMap<String, SendShare>();

	public class SendShare {
		public ShareMessage shareMessage;
		public int position;
		public int percent;
	}

	public void initShareMessage(ShareMessage shareMessage, int percent) {
		this.openLooper1.start();
		this.sendingSequence.add(shareMessage.gsid);
		SendShare sendShare = new SendShare();
		sendShare.shareMessage = shareMessage;
		sendShare.position = this.sendingSequence.indexOf(shareMessage.gsid);
		sendShare.percent = percent;
		this.sendingShareMessage.put(shareMessage.gsid, sendShare);
		thisController.mInfomations.add(0, shareMessage);
		this.nearbyShareAdapter.notifyDataSetChanged();
		log.e("initShareMessage");
	}

	public void updateProgress(ShareMessage shareMessage, int percent) {
		SendShare sendShare = this.sendingShareMessage.get(shareMessage.gsid);
		if (sendShare != null) {
			this.openLooper1.start();
			sendShare.position = this.sendingSequence.indexOf(shareMessage.gsid);
		}
		log.e("updateProgress");
	}

	public class NearbyRelationAdapter extends BaseAdapter {

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
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.nearby_item, null);
				holder.buttomLine = convertView.findViewById(R.id.buttomLine);
				holder.head = (ImageView) convertView.findViewById(R.id.head);
				holder.sex = (ImageView) convertView.findViewById(R.id.sex);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.age = (TextView) convertView.findViewById(R.id.age);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
				holder.mainBusiness = (TextView) convertView.findViewById(R.id.mainBusiness);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			int distance = 0;
			String head = "";
			if (thisController.status == LBSStatus.account) {
				holder.age.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				holder.buttomLine.setVisibility(View.VISIBLE);
				Friend friend = (Friend) getItem(position);
				holder.age.setText(String.valueOf(friend.age));
				holder.name.setText(friend.nickName);
				holder.mainBusiness.setText(BaseDataUtils.generateMainBusiness("point", friend.mainBusiness));
				if (BaseDataUtils.determineSex(friend.sex)) {
					holder.age.setBackgroundResource(R.drawable.personalinfo_male);
				} else {
					holder.age.setBackgroundResource(R.drawable.personalinfo_female);
				}
				if (!"".equals(friend.lastLoginTime)) {
					holder.time.setText(DateUtil.getNearShareTime(Long.valueOf(friend.lastLoginTime)));
				}
				head = friend.head;
				distance = friend.distance;
			} else if (thisController.status == LBSStatus.group) {
				holder.age.setVisibility(View.GONE);
				holder.time.setVisibility(View.GONE);
				holder.buttomLine.setVisibility(View.GONE);
				Group group = (Group) getItem(position);
				holder.name.setText(group.name);
				holder.mainBusiness.setText(BaseDataUtils.generateMainBusiness("group", group.description));
				distance = group.distance;
				head = group.icon;
			}
			thisController.taskManageHolder.fileHandler.getHeadImage(head, holder.head, viewManage.options40);
			if (distance >= 1000) {
				String distanceStr = new BigDecimal(distance / 1000d, new MathContext(4)).toPlainString();
				if (distanceStr.length() == 1) {
					distanceStr += ".000";
				}
				if (distanceStr.length() == 2) {
					distanceStr += ".00";
				}
				holder.distance.setText(distanceStr + "km");
			} else if (distance <= Constant.DEFAULMINDISTANCE) {
				holder.distance.setText(Constant.DEFAULMINDISTANCESTRING);
			} else {
				holder.distance.setText(distance + "m");
			}
			return convertView;
		}

		class Holder {
			View buttomLine;
			ImageView head, sex;
			TextView name, age, distance, mainBusiness, time;
		}
	}

	public void notifyData(boolean flag) {
		if (nearbyRelationAdapter != null) {
			nearbyRelationAdapter.notifyDataSetChanged();
		}
		if (nearbyShareAdapter != null) {
			nearbyShareAdapter.notifyDataSetChanged();
			if (flag) {
				nearbyListView.setSelection(0);
			}
		}
	}

	public void onResume() {
		businessCardPopView.dismissUserCardDialogView();
		mapView.onResume();
		if (thisController.mLocationManagerProxy != null && thisController.mAMapLocationListener != null)
			thisController.mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 1000, thisController.mAMapLocationListener);
		// if (thisController.latitude != 0 && thisController.longitude != 0) {
		// thisController.nowpage = 0;
		// thisController.searchNearbyLBS(false);
		// }
	}

	public void onPause() {
		mapView.onPause();
	}

	public void onDestroy() {
		if (thisController.isNearbyActivity)
			viewManage.nearbyView = null;
		data.localStatus.localData.currentSearchRadius = thisController.searchRadius;
		data.localStatus.localData.currentSearchTime = thisController.searchTime;
		data.localStatus.localData.isModified = true;
		// taskManageHolder.viewManage.shareSubView.setGroupsDialogContent(taskManageHolder.viewManage.shareSubView.currentGroupCircle);
		thisController.mLocationManagerProxy.removeUpdates(thisController.mAMapLocationListener);
		thisController.mLocationManagerProxy.destroy();
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

		popDialogView = new PopupWindow(dialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popDialogView.setBackgroundDrawable(new BitmapDrawable());
		showAddressDialog();
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

	// screen pop
	public PopupWindow screenPopDialog;
	public View screenDialogView, screenBackground;
	public LinearLayout scopeLayout, timeLayout;
	public TextView scopeOne, scopeTwo, scopeThree, scopeFour, timeOne, timeTwo, timeThree, timeFour, screenConfirm, screenCancel, titleTwo;

	@SuppressWarnings("deprecation")
	public void initializationScreenDialog() {
		screenDialogView = mInflater.inflate(R.layout.dialog_screen, null);
		screenBackground = screenDialogView.findViewById(R.id.background);
		scopeLayout = (LinearLayout) screenDialogView.findViewById(R.id.scopeLayout);
		timeLayout = (LinearLayout) screenDialogView.findViewById(R.id.timeLayout);
		scopeOne = (TextView) screenDialogView.findViewById(R.id.scopeOne);
		scopeTwo = (TextView) screenDialogView.findViewById(R.id.scopeTwo);
		scopeThree = (TextView) screenDialogView.findViewById(R.id.scopeThree);
		scopeFour = (TextView) screenDialogView.findViewById(R.id.scopeFour);
		timeOne = (TextView) screenDialogView.findViewById(R.id.timeOne);
		timeTwo = (TextView) screenDialogView.findViewById(R.id.timeTwo);
		timeThree = (TextView) screenDialogView.findViewById(R.id.timeThree);
		timeFour = (TextView) screenDialogView.findViewById(R.id.timeFour);
		screenConfirm = (TextView) screenDialogView.findViewById(R.id.confirm);
		screenCancel = (TextView) screenDialogView.findViewById(R.id.cancel);
		titleTwo = (TextView) screenDialogView.findViewById(R.id.titleTwo);

		if (thisController.status == LBSStatus.group) {
			timeLayout.setVisibility(View.GONE);
			titleTwo.setVisibility(View.GONE);
			titleTwo.setText("最近登录");
		} else if (thisController.status == LBSStatus.account) {
			titleTwo.setText("最近登录");
		}

		screenPopDialog = new PopupWindow(screenDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		screenPopDialog.setBackgroundDrawable(new BitmapDrawable());
	}

	public void changeScreenPopupWindow() {
		if (screenPopDialog.isShowing()) {
			screenPopDialog.dismiss();
		} else {
			if (thisController.status == LBSStatus.group) {
				timeLayout.setVisibility(View.GONE);
				titleTwo.setVisibility(View.GONE);
			} else if (thisController.status == LBSStatus.account) {
				timeLayout.setVisibility(View.VISIBLE);
				titleTwo.setVisibility(View.VISIBLE);
			}
			screenPopDialog.showAtLocation(maxView, Gravity.CENTER, 0, 0);
			changeScreenText();
		}
	}

	public void changeScreenText() {
		for (int i = 0; i < scopeLayout.getChildCount(); i += 2) {
			TextView text = (TextView) scopeLayout.getChildAt(i);
			if (thisController.tempSearchRadius == thisController.radius[(i + 1) / 2]) {
				text.setTextColor(thisActivity.getResources().getColor(R.color.text_color_blue));
			} else {
				text.setTextColor(thisActivity.getResources().getColor(R.color.black70));
			}
		}
		for (int i = 0; i < timeLayout.getChildCount(); i += 2) {
			TextView text = (TextView) timeLayout.getChildAt(i);
			if (thisController.tempSearchTime == thisController.times[(i + 1) / 2]) {
				text.setTextColor(thisActivity.getResources().getColor(R.color.text_color_blue));
			} else {
				text.setTextColor(thisActivity.getResources().getColor(R.color.black70));
			}
		}
	}

	public AddressDialogAdapter dialogAdapter;
	public ListController listController;

	public void showAddressDialog() {
		// if (dialogAdapter == null) {
		dialogAdapter = new AddressDialogAdapter();
		groupCircleList.setAdapter(dialogAdapter);
		listController = new ListController(groupCircleList, dialogAdapter);
		groupCircleList.setDropListener(listController);
		groupCircleList.setRemoveListener(listController);
		groupCircleList.setFloatViewManager(listController);
		groupCircleList.setOnTouchListener(listController);
		groupCircleList.setOnItemClickListener(listController);
		// } else {
		// dialogAdapter.notifyDataSetChanged();
		// }
	}

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public class AddressDialogAdapter extends BaseAdapter {
		public List<Location> addressList;

		public AddressDialogAdapter() {
			parser.check();
			if (data.userInformation.currentUser.commonUsedLocations == null) {
				data.userInformation.currentUser.commonUsedLocations = new ArrayList<Data.UserInformation.User.Location>();
			}
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
			List<Location> locations = data.userInformation.currentUser.commonUsedLocations;
			locations.add(to, locations.remove(from));
			adapter.notifyDataSetChanged();
			thisController.modifyUserCommonUsedLocations();
		}

		@Override
		public void remove(final int which) {
			Location location = data.userInformation.currentUser.commonUsedLocations.get(which);
			Alert.createDialog(thisActivity).setTitle("是否删除常用地址【" + location.remark + "】?").setOnConfirmClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					data.userInformation.currentUser.commonUsedLocations.remove(which);
					adapter.notifyDataSetChanged();
					thisController.modifyUserCommonUsedLocations();
				}
			}).setOnCancelClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					adapter.notifyDataSetChanged();
				}
			}).show();
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

	public void changeAmapCircle(double longitude, double latitude) {
		LatLng mLatLng = new LatLng(latitude, longitude);
		if (thisController.searchRadius == thisController.radius[thisController.radius.length - 1]) {
			if (ampCircle != null)
				ampCircle.setVisible(false);
		} else {
			if (circleOptions == null) {
				circleOptions = new CircleOptions().center(mLatLng).radius(thisController.searchRadius).fillColor(thisActivity.getResources().getColor(R.color.card_color)).strokeWidth(0);
				ampCircle = mAMap.addCircle(circleOptions);
			} else {
				ampCircle.setCenter(mLatLng);
				ampCircle.setRadius(thisController.searchRadius);
			}
			ampCircle.setVisible(true);
		}

	}

	public void changeMenuOptions(boolean setGone) {
		if (setGone) {
			menuOptions.setVisibility(View.GONE);
		} else {
			if (menuOptions.getVisibility() == View.VISIBLE) {
				menuOptions.setVisibility(View.GONE);
			} else {
				if (thisController.status == LBSStatus.account) {
					// optionLine
					optionOneText.setText("精确查找");
					optionTwoText.setText("动态列表");
					optionOneImage.setImageResource(R.drawable.search_bar_icon_normal);
					optionTwoImage.setImageResource(R.drawable.group);
				} else if (thisController.status == LBSStatus.group) {
					optionOneText.setText("新建群组");
					optionTwoText.setText("分类推荐");
					optionOneImage.setImageResource(R.drawable.chat_add_off);
					optionTwoImage.setImageResource(R.drawable.sidebar_icon_category_normal);
				}
				menuOptions.setVisibility(View.VISIBLE);
			}
		}
	}
}