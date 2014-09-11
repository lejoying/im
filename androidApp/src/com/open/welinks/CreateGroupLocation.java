package com.open.welinks;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateGroupLocation extends Activity {

	public View backView;
	public RelativeLayout rightContainer;
	public TextView titleContent, location, commit;
	public EditText search;
	public MapView mapView;
	public ListView groupList;

	public GroupListAdapter mGroupListAdapter;

	public AMap mAMap;
	public LatLng mLatLng;
	public RegeocodeQuery mRegeocodeQuery;
	public LocationManagerProxy mLocationManagerProxy;
	public LocationSource mLocationSource;
	public GeocodeSearch geocodeSearch;

	public AMapLocationListener mAMapLocationListener;
	public OnLocationChangedListener mOnLocationChangedListener;
	public OnCameraChangeListener mOnCameraChangeListener;
	public OnGeocodeSearchListener mOnGeocodeSearchListener;
	public OnClickListener mOnClickListener;

	public String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creategrouplocation);
		initView();
		initListener();
		initData();
		mapView.onCreate(savedInstanceState);
		requestMyLocation();
	}

	public void initData() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mAMap = mapView.getMap();
	}

	public void initView() {
		backView = findViewById(R.id.backView);
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		titleContent = (TextView) findViewById(R.id.backTitleView);
		location = (TextView) findViewById(R.id.location);
		search = (EditText) findViewById(R.id.search);
		mapView = (MapView) findViewById(R.id.mapView);
		groupList = (ListView) findViewById(R.id.grouplist);

		commit = new TextView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		commit.setText("完成");
		rightContainer.addView(commit, params);

		titleContent.setText("创建群组");

	}

	public void initListener() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(commit)) {

				}

			}
		};
		mAMapLocationListener = new AMapLocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

			}

			@Override
			public void onProviderEnabled(String arg0) {

			}

			@Override
			public void onProviderDisabled(String arg0) {

			}

			@Override
			public void onLocationChanged(Location arg0) {

			}

			@Override
			public void onLocationChanged(AMapLocation aMapLocation) {
				if (mOnLocationChangedListener != null && aMapLocation != null) {
					if (aMapLocation.getAMapException().getErrorCode() == 0) {
						mLocationManagerProxy.removeUpdates(mAMapLocationListener);
						mLocationManagerProxy.destroy();
						mOnLocationChangedListener.onLocationChanged(aMapLocation);
						mAMap.clear();
						location.setText("当前地址：" + aMapLocation.getAddress());
					}
				}

			}
		};
		mOnCameraChangeListener = new OnCameraChangeListener() {

			@Override
			public void onCameraChangeFinish(CameraPosition cameraPosition) {
				mLatLng = cameraPosition.target;
				LatLonPoint latLonPoint = new LatLonPoint(mLatLng.latitude, mLatLng.longitude);
				mRegeocodeQuery = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
				geocodeSearch.getFromLocationAsyn(mRegeocodeQuery);
			}

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

			}
		};

		mOnGeocodeSearchListener = new OnGeocodeSearchListener() {

			@Override
			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
						address = result.getRegeocodeAddress().getFormatAddress();
						location.setText("当前地址：" + address);
					}
				}

			}

			@Override
			public void onGeocodeSearched(GeocodeResult result, int rCode) {

			}
		};

		mLocationSource = new LocationSource() {

			@SuppressWarnings("deprecation")
			@Override
			public void activate(OnLocationChangedListener listener) {
				mOnLocationChangedListener = listener;
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.setGpsEnable(true);
				mLocationManagerProxy.requestLocationUpdates(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);

			}

			@Override
			public void deactivate() {
				if (mLocationManagerProxy != null) {
					mLocationManagerProxy.removeUpdates(mAMapLocationListener);
					mLocationManagerProxy.destroy();
				}
				mLocationManagerProxy = null;
			}

		};
		backView.setOnClickListener(mOnClickListener);
		commit.setOnClickListener(mOnClickListener);
	}

	public void requestMyLocation() {
		mAMap.setLocationSource(mLocationSource);
		mAMap.getUiSettings().setMyLocationButtonEnabled(true);
		mAMap.getUiSettings().setZoomControlsEnabled(false);
		mAMap.setMyLocationEnabled(true);
		mAMap.setOnCameraChangeListener(mOnCameraChangeListener);

		geocodeSearch = new GeocodeSearch(this);
		geocodeSearch.setOnGeocodeSearchListener(mOnGeocodeSearchListener);

	}

	class GroupListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
