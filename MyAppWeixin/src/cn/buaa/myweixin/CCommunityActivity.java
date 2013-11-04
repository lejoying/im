package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.CommunityManager;
import cn.buaa.myweixin.apiimpl.CommunityManagerImpl;
import cn.buaa.myweixin.apiutils.MCTools;

public class CCommunityActivity extends Activity {

	private CommunityManager communityManager;
	
	private TextView tv_community;
	private TextView tv_agent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (MCTools.getNOWCOMMUNITY() == null) {
			finish();
		}
		setContentView(R.layout.cc_community);
		initView();
	}

	public void initView() {
		tv_community = (TextView) findViewById(R.id.tv_community);
		tv_agent = (TextView) findViewById(R.id.tv_agent);
		tv_community.setText(MCTools.getNOWCOMMUNITY().getName());
		tv_agent.setText("社区站长:  "+MCTools.getNOWCOMMUNITY().getAgent().getNickName());
	}

	public void back(View v) {
		finish();
	}

	public void callcardRightDialog(View v) {
		Intent intent = new Intent(this, CCommunityRightDialog.class);
		startActivity(intent);
	}

	public void joinC(View v) {
		if (MCTools.INNEWCOMMUNITY) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone",
					MCTools.getLoginedAccount(CCommunityActivity.this)
							.getPhone());
			param.put("cid", String.valueOf(MCTools.getNOWCOMMUNITY().getCid()));
			communityManager = new CommunityManagerImpl(this);
			communityManager.join(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					MCTools.INNEWCOMMUNITY = false;
				}
			});
		} else {
			System.out.println("已加入");
		}
	}
}
