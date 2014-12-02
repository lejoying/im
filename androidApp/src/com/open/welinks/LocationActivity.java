package com.open.welinks;

import java.util.ArrayList;

import com.amap.api.a.af;
import com.amap.api.a.aj;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.autonavi.amap.mapcore2d.FPoint;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LocationActivity extends Activity implements OnClickListener {

	public AMap mAMap;

	public View backView;
	public TextView titleText;
	public MapView mapView;

	public String address, latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		latitude = getIntent().getStringExtra("latitude");
		longitude = getIntent().getStringExtra("longitude");
		address = getIntent().getStringExtra("address");
		backView = findViewById(R.id.backView);
		titleText = (TextView) findViewById(R.id.titleText);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		mAMap = mapView.getMap();

		LatLng latLonPoint = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLonPoint, 17));
		titleText.setText(address);

		MarkerOptions markOptions = new MarkerOptions();
		markOptions.position(latLonPoint);
		// markOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_location));
		mAMap.addMarker(markOptions);

		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(backView)) {
			finish();
		}

	}

}
