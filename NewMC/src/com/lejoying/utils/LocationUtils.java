package com.lejoying.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import com.lejoying.mc.data.App;

public class LocationUtils {

	private static LocationUtils instance;

	private LocationUtils() {

	}

	public static LocationUtils getInstance() {
		if (instance == null) {
			instance = new LocationUtils();
		}
		return instance;
	}

	public SItude getLastKnownLocation(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String providerName = lm.getBestProvider(criteria, true);
		SItude result = null;
		if (providerName != null) {
			Location location = lm.getLastKnownLocation(providerName);
			result = new SItude();
			result.latitude = String.valueOf(location.getLatitude());
			result.longitude = String.valueOf(location.getLongitude());
		} else {
			Toast.makeText(context, "can't find location", Toast.LENGTH_SHORT)
					.show();
		}
		return result;
	}

	public SCell getCellInfo() throws Exception {
		SCell cell = new SCell();

		/** 调用API获取基站信息 */
		TelephonyManager mTelNet = (TelephonyManager) App.getInstance().context
				.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null) {
			throw new Exception("获取基站信息失败");
		} else {

			String operator = mTelNet.getNetworkOperator();
			int mcc = Integer.parseInt(operator.substring(0, 3));
			int mnc = Integer.parseInt(operator.substring(3));
			int cid = location.getCid();
			int lac = location.getLac();

			/** 将获得的数据放到结构体中 */
			cell.MCC = mcc;
			cell.MNC = mnc;
			cell.LAC = lac;
			cell.CID = cid;
		}

		return cell;
	}

	public void getBaseStationLoaction() {
		try {
			SCell cell = getCellInfo();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class SCell {
		public int MCC;
		public int MNC;
		public int LAC;
		public int CID;
	}

	public class SItude {
		public String latitude;
		public String longitude;
	}
}
