package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.buaa.myweixin.utils.MCTools;
import cn.buaa.myweixin.utils.MCNowUser;
import cn.buaa.myweixin.utils.MCTools.HttpStatusListener;

public class CCommunityActivity extends Activity {

	private TextView tv_community;
	private TextView tv_agent;
	private JSONObject jo;
	private String cid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cc_community);
		initView();
	}

	public void initView() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			try {
				jo = new JSONObject(bundle.getString("nowcommunity"));
				cid = jo.getString("cid");
				tv_community = (TextView) findViewById(R.id.tv_community);
				tv_agent = (TextView) findViewById(R.id.tv_agent);
				tv_community.setText(jo.getString("name"));
				tv_agent.setText("社区站长：" + jo.getString("agent"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void back(View v) {
		finish();
	}

	public void callcardRightDialog(View v) {
		Intent intent = new Intent(this, CCommunityRightDialog.class);
		startActivity(intent);
	}

	public void joinC(View v) {

		Map<String, String> param = new HashMap<String, String>();
		param.put("phone", MCNowUser.getNowUser().getPhone());
		param.put("cid", cid);

		MCTools.postForJSON(this,
				"http://192.168.0.19:8071/api2/account/join", param, true,
				new HttpStatusListener() {

					@Override
					public void shortIntervalTime() {
						// TODO Auto-generated method stub

					}

					@Override
					public void noInternet() {
						new AlertDialog.Builder(CCommunityActivity.this)
								.setIcon(
										getResources().getDrawable(
												R.drawable.login_error_icon))
								.setTitle("网络错误")
								.setMessage("无网络连接,请连接网络后\n重试！").create()
								.show();
					}

					@Override
					public void getJSONSuccess(JSONObject data) {
						try {
							String info = data.getString("提示信息");
							if (info.equals("加入成功")) {
								Intent intent = new Intent(
										CCommunityActivity.this,
										MainWeixin.class);
								startActivity(intent);
								finish();
							} else {
								String err = data.getString("失败原因");
								new AlertDialog.Builder(
										CCommunityActivity.this)
										.setIcon(
												getResources()
														.getDrawable(
																R.drawable.login_error_icon))
										.setTitle("出错了").setMessage(err)
										.create().show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
	}
}
