package com.open.welinks.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.model.API;
import com.open.welinks.model.ExceptionHandler;
import com.open.welinks.utils.MyGson;

public class ExceptionService extends Service {
	public String tag = "ExceptionService";
	public MyLog log = new MyLog(tag, true);

	public ExceptionService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void sendContent(String content) {
		// log.e(content);
		sendMessageToServer(content);
	}

	public MyGson gson = new MyGson();
	HttpClient httpClient = new HttpClient();
	ExceptionHandler handler = ExceptionHandler.getInstance();

	public void sendMessageToServer(String content) {

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("content", content);

		httpUtils.send(HttpMethod.POST, API.BUG_SEND, params, httpClient.new ResponseHandler<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				super.onSuccess(responseInfo);
				log.e("发送异常成功");
				Toast.makeText(getApplicationContext(), "aaa", Toast.LENGTH_LONG).show();
				// handler.kill();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				super.onFailure(error, msg);
				log.e("发送异常失败" + msg);
				Toast.makeText(getApplicationContext(), "bbb", Toast.LENGTH_LONG).show();
				// handler.kill();
			}
		});
		// handler.kill();
	}
}
