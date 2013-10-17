package cn.buaa.myweixin.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import cn.buaa.myweixin.R;
import cn.buaa.myweixin.listener.ResponseListener;

public class MCResponseAdapter implements ResponseListener {

	private Activity activity;
	
	public MCResponseAdapter(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void noInternet() {
		Context context = activity;
		new AlertDialog.Builder(activity)
				.setIcon(
						context.getResources().getDrawable(
								R.drawable.login_error_icon))
				.setTitle("网络错误").setMessage("无网络连接，请连接网络后重试").create()
				.show();
	}

	@Override
	public void success(JSONObject data) {
		
	}

	@Override
	public void failed(JSONObject data) {
		Context context = activity;
		try {
			String err = data.getString("失败原因");
			new AlertDialog.Builder(activity)
			.setIcon(
					context.getResources().getDrawable(
							R.drawable.login_error_icon))
			.setTitle("操作失败").setMessage(err).create()
			.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
