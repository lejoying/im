package cn.buaa.myweixin.apiutils;

import org.json.JSONObject;

public class Friend extends Account {
	
	public Friend() {
		super();
	}

	public Friend(JSONObject jaccount) {
		super(jaccount);
		
	}

	public Friend(String phone, String head, String nickName,
			String mainBusiness, String status, String accessKey) {
		super(phone, head, nickName, mainBusiness, status, accessKey);
	}

	
}
