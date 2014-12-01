package com.open.welinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.cloud.model.AMapCloudException;
import com.amap.api.cloud.model.CloudItem;
import com.amap.api.cloud.model.CloudItemDetail;
import com.amap.api.cloud.search.CloudResult;
import com.amap.api.cloud.search.CloudSearch;
import com.amap.api.cloud.search.CloudSearch.OnCloudSearchListener;
import com.amap.api.cloud.search.CloudSearch.Query;
import com.amap.api.cloud.search.CloudSearch.SearchBound;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.open.welinks.model.Constant;
import com.open.welinks.model.LBSHandlers;

public class CreateGroupLocationActivity extends Activity {

	public View backView;
	public RelativeLayout rightContainer;
	public TextView titleContent, location, commit;
	public AutoCompleteTextView search;
	public MapView mapView;
	public ListView groupList;

	public LayoutInflater mInflater;

	public GroupListAdapter mGroupListAdapter;

	public AMap mAMap;
	public Query mQuery;
	public LatLng mLatLng;
	public RegeocodeQuery mRegeocodeQuery;
	public GeocodeQuery mGeocodeQuery;
	public LocationManagerProxy mLocationManagerProxy;
	public LocationSource mLocationSource;
	public GeocodeSearch mGeocodeSearch;
	public CloudSearch mCloudSearch;
	public ArrayList<CloudItem> mCloudItems;

	public Inputtips mInputtips;
	public List<Tip> tipsList;

	public TipsAdapter mTipsAdapter;

	public AMapLocationListener mAMapLocationListener;
	public OnLocationChangedListener mOnLocationChangedListener;
	public OnCameraChangeListener mOnCameraChangeListener;
	public OnGeocodeSearchListener mOnGeocodeSearchListener;
	public OnCloudSearchListener mCloudSearchListener;
	public InputtipsListener mInputtipsListener;
	public OnClickListener mOnClickListener;

	public String address, latitude, longitude;
	public boolean positioned = true;

	public TextWatcher mTextWatcher;

	public ArrayList<Map<String, Object>> mInfomations;

	public DisplayMetrics displayMetrics;

	public boolean isScanAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creategrouplocation);
		mInflater = getLayoutInflater();
		String type = getIntent().getStringExtra("type");
		if (type != null) {
			isScanAddress = true;
		} else {
			isScanAddress = false;
		}
		latitude = getIntent().getStringExtra("latitude");
		longitude = getIntent().getStringExtra("longitude");
		address = getIntent().getStringExtra("address");
		initData();
		initView();
		initListener();
		mapView.onCreate(savedInstanceState);
		if ("".equals(latitude) || "".equals(longitude) || "".equals(address)) {
			positioned = false;
		} else {
			GeocodeSearch geocoderSearch = new GeocodeSearch(this);
			geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

				@Override
				public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
					if (rCode == 0) {
						if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
							address = result.getRegeocodeAddress().getFormatAddress() + "附近";
							location.setText("当前地址：" + address);
						} else {
						}
					} else {
					}
				}

				@Override
				public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
				}
			});
			LatLonPoint latLonPoint = new LatLonPoint(Double.valueOf(latitude), Double.valueOf(longitude));
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
			geocoderSearch.getFromLocationAsyn(query);
		}
		requestMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	public void initData() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mInputtips = new Inputtips(this, mInputtipsListener);
		mInfomations = new ArrayList<Map<String, Object>>();
		tipsList = new ArrayList<Tip>();
		mGroupListAdapter = new GroupListAdapter();
		mTipsAdapter = new TipsAdapter();
	}

	public void initView() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		backView = findViewById(R.id.backView);
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		titleContent = (TextView) findViewById(R.id.backTitleView);
		location = (TextView) findViewById(R.id.location);
		search = (AutoCompleteTextView) findViewById(R.id.search);
		mapView = (MapView) findViewById(R.id.mapView);
		groupList = (ListView) findViewById(R.id.grouplist);

		mAMap = mapView.getMap();

		commit = new TextView(this);
		commit.setTextColor(Color.WHITE);
		commit.setPadding((int) (10 * displayMetrics.density), (int) (5 * displayMetrics.density), (int) (10 * displayMetrics.density), (int) (5 * displayMetrics.density));
		commit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		commit.setGravity(Gravity.CENTER);
		commit.setBackgroundResource(R.drawable.textview_bg);
		commit.setText("完成");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);

		if (isScanAddress) {
			titleContent.setText("群组位置");
		} else {
			titleContent.setText("创建群组");
			rightContainer.addView(commit, params);
		}

		search.setAdapter(mTipsAdapter);
		groupList.setAdapter(mGroupListAdapter);
	}

	public void initListener() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_second) != null) {
					LatLng latLng = (LatLng) view.getTag(R.id.tag_second);
					LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
					mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
					mRegeocodeQuery = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
					mGeocodeSearch.getFromLocationAsyn(mRegeocodeQuery);
				} else if (view.getTag(R.id.tag_third) != null) {
					Tip tip = (Tip) view.getTag(R.id.tag_third);
					mGeocodeQuery = new GeocodeQuery(tip.getDistrict() + tip.getName(), tip.getAdcode());
					mGeocodeSearch.getFromLocationNameAsyn(mGeocodeQuery);
					String discription = tip.getDistrict() + tip.getName();
					search.setText(discription);
					search.setSelection(discription.length());
					search.dismissDropDown();
				} else if (view.equals(backView)) {
					finish();
				} else if (view.equals(commit)) {
					Intent intent = new Intent();
					intent.putExtra("address", address);
					intent.putExtra("latitude", String.valueOf(mLatLng.latitude));
					intent.putExtra("longitude", String.valueOf(mLatLng.longitude));
					setResult(Activity.RESULT_OK, intent);
					finish();
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
						address = aMapLocation.getAddress();
						mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
						searchGroups();
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
				mGeocodeSearch.getFromLocationAsyn(mRegeocodeQuery);
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
				if (rCode == 0) {
					if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
						GeocodeAddress point = result.getGeocodeAddressList().get(0);
						mLatLng = new LatLng(point.getLatLonPoint().getLatitude(), point.getLatLonPoint().getLongitude());
						mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 13));
						address = point.getFormatAddress();
						location.setText("当前地址：" + address);
					}
				}
			}
		};

		mCloudSearchListener = new OnCloudSearchListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onCloudSearched(CloudResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getQuery() != null) {
						if (result.getQuery().equals(mQuery)) {
							mCloudItems = result.getClouds();
							mInfomations.clear();
							for (CloudItem item : mCloudItems) {
								Map<String, Object> map = new HashMap<String, Object>();
								LatLng point2 = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
								map.put("location", item.getLatLonPoint());
								map.put("name", item.getTitle());
								map.put("address", item.getSnippet());
								map.put("distance", item.getDistance() == -1 ? (int) AMapUtils.calculateLineDistance(mLatLng, point2) : item.getDistance());
								Iterator iter = item.getCustomfield().entrySet().iterator();
								while (iter.hasNext()) {
									Map.Entry entry = (Map.Entry) iter.next();
									map.put(entry.getKey().toString(), entry.getValue());
								}
								mInfomations.add(map);
							}
							mGroupListAdapter.notifyDataSetChanged();
						}
					}
				}

			}

			@Override
			public void onCloudItemDetailSearched(CloudItemDetail arg0, int rCode) {

			}
		};

		mLocationSource = new LocationSource() {

			@SuppressWarnings("deprecation")
			@Override
			public void activate(OnLocationChangedListener listener) {
				mOnLocationChangedListener = listener;
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.setGpsEnable(true);
				if (positioned) {
					mLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
					mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
					location.setText("当前地址：" + address);
					positioned = false;
				} else {
					mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);
				}
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

		mInputtipsListener = new InputtipsListener() {

			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {
				if (rCode == 0) {
					tipsList = tipList;
					mTipsAdapter.notifyDataSetChanged();
				}
			}
		};

		mTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				String newText = s.toString().trim();
				Inputtips inputTips = new Inputtips(CreateGroupLocationActivity.this, mInputtipsListener);
				if ("".equals(newText)) {

				} else {
					try {
						inputTips.requestInputtips(newText, "");
					} catch (AMapException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};
		backView.setOnClickListener(mOnClickListener);
		commit.setOnClickListener(mOnClickListener);
		search.addTextChangedListener(mTextWatcher);
	}

	public void requestMyLocation() {
		mAMap.setLocationSource(mLocationSource);
		mAMap.getUiSettings().setMyLocationButtonEnabled(true);
		mAMap.getUiSettings().setZoomControlsEnabled(false);
		mAMap.setMyLocationEnabled(true);
		mAMap.setOnCameraChangeListener(mOnCameraChangeListener);

		mGeocodeSearch = new GeocodeSearch(this);
		mGeocodeSearch.setOnGeocodeSearchListener(mOnGeocodeSearchListener);

	}

	public void searchGroups() {
		mCloudSearch = new CloudSearch(this);
		mCloudSearch.setOnCloudSearchListener(mCloudSearchListener);

		List<com.amap.api.cloud.model.LatLonPoint> points = new ArrayList<com.amap.api.cloud.model.LatLonPoint>();
		points.add(new com.amap.api.cloud.model.LatLonPoint(5.965754, 70.136719));
		points.add(new com.amap.api.cloud.model.LatLonPoint(56.170023, 140.097656));
		try {
			mQuery = new Query(Constant.GROUPTABLEID, "", new SearchBound(points));
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.setPageSize(50);
		// mQuery.setPageNum(0);
		mCloudSearch.searchCloudAsyn(mQuery);
	}

	class GroupListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mInfomations.size();
		}

		@Override
		public Object getItem(int position) {
			return mInfomations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Map<String, Object> infomation = mInfomations.get(position);
			GroupListHolder holder = null;
			if (convertView == null) {
				holder = new GroupListHolder();
				convertView = mInflater.inflate(R.layout.creategrouplist_item, null);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
				convertView.setTag(holder);
			} else {
				holder = (GroupListHolder) convertView.getTag();
			}
			holder.name.setText((String) infomation.get("name"));
			holder.distance.setText(LBSHandlers.getDistance((Integer) infomation.get("distance")));

			com.amap.api.cloud.model.LatLonPoint point = (com.amap.api.cloud.model.LatLonPoint) infomation.get("location");
			LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
			convertView.setTag(R.id.tag_second, latLng);
			convertView.setOnClickListener(mOnClickListener);
			return convertView;
		}

		class GroupListHolder {
			public TextView name, distance;
		}
	}

	class TipsAdapter extends BaseAdapter implements Filterable {
		TipsFilter mfilFilter;

		@Override
		public int getCount() {
			return tipsList.size();
		}

		@Override
		public Object getItem(int position) {
			return tipsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tip tip = (Tip) tipsList.get(position);
			convertView = mInflater.inflate(R.layout.tips_item, null);
			((TextView) convertView).setText(tip.getName());
			convertView.setTag(R.id.tag_third, tip);
			convertView.setOnClickListener(mOnClickListener);
			return convertView;
		}

		@Override
		public Filter getFilter() {
			if (mfilFilter == null) {
				mfilFilter = new TipsFilter();
			}
			return mfilFilter;
		}

		class TipsFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();
				return results;
			}

			@Override
			protected void publishResults(CharSequence charSequence, FilterResults results) {
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

		}

	}
}
