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
import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.RelationManager;
import cn.buaa.myweixin.apiimpl.RelationManagerImpl;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.utils.HttpTools;

public class CCommunityActivity extends Activity {

	private TextView tv_community;
	private TextView tv_agent;
	private JSONObject jo;
	private String cid;
	private RelationManager relationManager;

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

		Map<String, String> param = new HashMap<String, String>();
		param.put("phone", MCTools.getLoginedAccount(CCommunityActivity.this).getPhone());
		param.put("cid", cid);

		relationManager = new RelationManagerImpl(this);

		relationManager.join(param, new MCResponseAdapter(this) {

			@Override
			public void success(JSONObject data) {
				Intent intent = new Intent(CCommunityActivity.this,
						MainWeixin.class);
				startActivity(intent);
				finish();
			}

		});
	}
}
