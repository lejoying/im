package cn.buaa.myweixin.listener;

import org.json.JSONObject;

public interface ResponseListener {
	public void noInternet();
	public void success(JSONObject data);
	public void unsuccess(JSONObject data);
	public void failed();
}
