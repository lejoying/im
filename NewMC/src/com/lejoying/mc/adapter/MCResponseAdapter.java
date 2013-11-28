package com.lejoying.mc.adapter;

import org.json.JSONObject;

import android.app.Activity;

import com.lejoying.mc.LoadingActivity;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCTools;

public class MCResponseAdapter implements ResponseListener {

	private Activity activity;

	public MCResponseAdapter(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void noInternet() {
		if (LoadingActivity.instance != null) {
			LoadingActivity.instance.finish();
		}
		MCTools.showMsg(activity, "没有网络，请检查网络后重试");
	}

	@Override
	public void success(JSONObject data) {
		if (LoadingActivity.instance != null) {
			LoadingActivity.instance.finish();
		}
	}

	@Override
	public void unsuccess(JSONObject data) {
		if (LoadingActivity.instance != null) {
			LoadingActivity.instance.finish();
		}
		MCTools.showMsg(activity, "数据异常,操作失败");
	}

	@Override
	public void failed() {
		if (LoadingActivity.instance != null) {
			LoadingActivity.instance.finish();
		}
		MCTools.showMsg(activity, "连接服务器异常");
	}

}
