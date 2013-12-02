package com.lejoying.mc.adapter;

import org.json.JSONObject;

import android.app.Activity;

import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;

public class MCResponseAdapter implements ResponseListener {

	private Activity activity;

	public MCResponseAdapter(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void noInternet() {
		MCNetTools.showMsg(activity, "没有网络，请检查网络后重试");
	}

	@Override
	public void success(JSONObject data) {

	}

	@Override
	public void unsuccess(JSONObject data) {
		MCNetTools.showMsg(activity, "数据异常,操作失败");
	}

	@Override
	public void failed() {
		MCNetTools.showMsg(activity, "连接服务器异常");
	}

}
