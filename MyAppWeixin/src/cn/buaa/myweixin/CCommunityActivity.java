package cn.buaa.myweixin;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CCommunityActivity extends Activity {

	private TextView tv_community;
	private TextView tv_agent;
	private JSONObject jo;

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
				tv_community = (TextView) findViewById(R.id.tv_community);
				tv_agent = (TextView) findViewById(R.id.tv_agent);
				tv_community.setText(jo.getString("name"));
				tv_agent.setText("ÉçÇøÕ¾³¤£º" + jo.getString("agent"));
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
		Intent intent = new Intent(this, MainWeixin.class);
		startActivity(intent);
		finish();
	}
}
