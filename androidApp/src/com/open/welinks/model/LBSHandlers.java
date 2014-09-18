package com.open.welinks.model;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.model.Data.UserInformation.User;

public class LBSHandlers {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "LBSHandlers";
	public MyLog log = new MyLog(tag, true);

	public HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	public void uplodUserLbsData() {
		parser.check();
		User user = data.userInformation.currentUser;

	}

	public void chackLBSAccount() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tableid", Constant.ACCOUNTTABLEID);
		params.addQueryStringParameter("filter", "phone:" + data.userInformation.currentUser.phone);
		params.addQueryStringParameter("key", Constant.LBS_KSY);
		httpUtils.send(HttpMethod.GET, API.LBS_DATA_SEARCH, params, LBSDataSearch);
	}

	public RequestCallBack<String> LBSDataSearch = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
			public int count;
			public ArrayList<data> datas;

			class data {
				public String _id;
			}
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {

				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.status == 1) {
					if (response.count == 0) {
						// viewManage.mainView.thisController.creataLBSAccount();
					} else {
						// viewManage.mainView.thisController.modifyLBSAccount(response.datas.get(0)._id);
					}
				}
			} catch (Exception e) {

			}
		};
	};

}
