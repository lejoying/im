package com.lejoying.mc.adapter;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lejoying.mc.R;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public abstract class ResponseAdapter implements ResponseListener {

	private Context mContext;

	public ResponseAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public void connectionCreated(HttpURLConnection httpURLConnection) {

	}

	@Override
	public void noInternet() {

	}

	@Override
	public void success(JSONObject data) {
		if (data != null) {
			String info = null;
			try {
				info = data.getString(mContext.getString(R.string.app_notice));
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			if (info == null) {
				return;
			}
			info = info.substring(info.length() - 2, info.length());

			if (info.equals(mContext.getString(R.string.app_success))) {
				responseSuccess(data);
			}
			if (info.equals(mContext.getString(R.string.app_unsuccess))) {
				responseUnSuccess(data);
			}
		}
	}

	public abstract void responseSuccess(JSONObject data);

	public abstract void responseUnSuccess(JSONObject data);

	@Override
	public void failed() {

	}

}
