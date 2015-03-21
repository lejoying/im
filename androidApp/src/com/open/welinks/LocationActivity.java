package com.open.welinks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.open.welinks.R;

public class LocationActivity extends Activity implements OnClickListener, OnGeocodeSearchListener {

	public AMap mAMap;

	public View backView;
	public TextView titleText;
	public MapView mapView;

	public String latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		latitude = getIntent().getStringExtra("latitude");
		longitude = getIntent().getStringExtra("longitude");
		backView = findViewById(R.id.backView);
		titleText = (TextView) findViewById(R.id.titleText);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		mAMap = mapView.getMap();

		LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

		MarkerOptions markOptions = new MarkerOptions();
		markOptions.position(latLng);
		mAMap.addMarker(markOptions);

		backView.setOnClickListener(this);

		GeocodeSearch geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		LatLonPoint latLonPoint = new LatLonPoint(Double.valueOf(latitude), Double.valueOf(longitude));
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(backView)) {
			finish();
		}

	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
				String address = result.getRegeocodeAddress().getFormatAddress() + "附近";
				titleText.setText(address);
			}
		}
	}
}
