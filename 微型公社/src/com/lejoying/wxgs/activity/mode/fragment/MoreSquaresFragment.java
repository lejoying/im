package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.CreateSquareActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.AmapLocationHandler.CreateLocationListener;
import com.lejoying.wxgs.app.handler.AmapLocationHandler.LocationListener;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class MoreSquaresFragment extends BaseFragment implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	View mContent;

	ImageView search, delete;
	EditText findrecord;
	TextView location;
	RelativeLayout backView, search_layout, map_layout;
	LinearLayout create;
	FrameLayout map;
	ListView near;
	ImageView zoom;

	private MapView mapView;

	int mapHeight, height, width, dip;
	float density;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.f_more_squares, null);
		initLayout();
		initEvent();
		initData();
		mapView.onCreate(savedInstanceState);
		return mContent;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		mapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		app.amapLocationHandler.deactivate();
		super.onDestroy();
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;

	}

	private void initEvent() {
		search.setOnClickListener(this);
		delete.setOnClickListener(this);
		backView.setOnClickListener(this);
		zoom.setOnClickListener(this);
		create.setOnClickListener(this);
		findrecord.addTextChangedListener(new TextWatcher() {
			String content = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				content = s.toString();

			}

			@Override
			public void afterTextChanged(Editable s) {
				if ("".equals(content)) {
					delete.setVisibility(View.GONE);
				} else {
					delete.setVisibility(View.VISIBLE);
				}
			}
		});
		initMap();
	}

	private void initMap() {
		app.amapLocationHandler.requestMapLocation(mapView.getMap(),
				new LocationListener() {

					@Override
					public void onLocationChangedListener(
							AMapLocation aMapLocation) {
						location.setText(aMapLocation.getAddress());
						modifylocation(aMapLocation);
//						app.amapLocationHandler.searchAccountsByBound(mapView
//								.getMap());
						app.amapLocationHandler.searchSquaresByBound(mapView
								.getMap());

					}
				});
	}

	private void modifylocation(AMapLocation aMapLocation) {
		final double longitude = aMapLocation.getLongitude(), latitude = aMapLocation
				.getLatitude();
		final String address = aMapLocation.getAddress();
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {

			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.LBS_MODIFYACCOUNTLOCATION;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("nickName", app.data.user.nickName);
				params.put("sex", app.data.user.sex);
				params.put("mainBusiness", app.data.user.mainBusiness);
				params.put("head", app.data.user.head);
				params.put("online", "1");
				JSONObject location = new JSONObject();
				try {
					location.put("longitude", longitude);
					location.put("latitude", latitude);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params.put("location", location.toString());
				params.put("address", address);
				settings.params = params;
			}

		});
	}

	private void Zoom() {
		if (near.getVisibility() == View.VISIBLE) {
			near.setVisibility(View.GONE);
			search_layout.setVisibility(View.GONE);
			mapHeight = map.getHeight();
			map.getLayoutParams().height = LayoutParams.MATCH_PARENT;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) map
					.getLayoutParams();
			params.setMargins(0, 0, 0, 0);
			// Animation scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.5f,
			// 3.0f);
			// scaleAnimation.setDuration(500);
			// scaleAnimation.setFillAfter(true);
			// map.startAnimation(scaleAnimation);
			// scaleAnimation.setAnimationListener(new AnimationListener() {
			//
			// @Override
			// public void onAnimationStart(Animation animation) {
			//
			// }
			//
			// @Override
			// public void onAnimationRepeat(Animation animation) {
			//
			// }
			//
			// @Override
			// public void onAnimationEnd(Animation animation) {
			// map.clearAnimation();
			//
			// }
			// });
		} else {
			near.setVisibility(View.VISIBLE);
			search_layout.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) map
					.getLayoutParams();
			params.setMargins(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
			map.getLayoutParams().height = mapHeight;
		}

	}

	private void initLayout() {
		search = (ImageView) mContent.findViewById(R.id.search);
		delete = (ImageView) mContent.findViewById(R.id.delete);
		findrecord = (EditText) mContent.findViewById(R.id.findrecord);
		location = (TextView) mContent.findViewById(R.id.location);
		backView = (RelativeLayout) mContent.findViewById(R.id.backView);
		search_layout = (RelativeLayout) mContent
				.findViewById(R.id.search_layout);
		map_layout = (RelativeLayout) mContent.findViewById(R.id.map_layout);
		map = (FrameLayout) mContent.findViewById(R.id.map);
		near = (ListView) mContent.findViewById(R.id.near);
		mapView = (MapView) mContent.findViewById(R.id.mapView);
		zoom = (ImageView) mContent.findViewById(R.id.zoom);
		create = (LinearLayout) mContent.findViewById(R.id.create);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.search) {

		} else if (id == R.id.delete) {
			findrecord.setText("");
		} else if (id == R.id.backView) {
			mMainModeManager.back();
		} else if (id == R.id.zoom) {
			Zoom();
		} else if (id == R.id.create) {
			startActivity(new Intent(getActivity(), CreateSquareActivity.class));
		}

	}

	private int dp2px(int dpValue) {
		return (int) (dpValue * density + 0.5f);
	}
}
