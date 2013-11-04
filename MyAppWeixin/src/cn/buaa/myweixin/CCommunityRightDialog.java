package cn.buaa.myweixin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.CommunityManager;
import cn.buaa.myweixin.apiimpl.CommunityManagerImpl;
import cn.buaa.myweixin.apiutils.MCTools;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CCommunityRightDialog extends Activity {

	private TextView tv_joinorunjoin;

	private CommunityManager communityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cc_community_right_dialog);
		initView();
	}

	public void initView() {
		communityManager = new CommunityManagerImpl(this);
		tv_joinorunjoin = (TextView) findViewById(R.id.tv_joinorunjoin);
		if (MCTools.INNEWCOMMUNITY) {
			tv_joinorunjoin.setText("¼ÓÈë");
		} else {
			tv_joinorunjoin.setText("ÍË³ö");
		}

	}

	public void joinOrNot(View v) {
		if (MCTools.INNEWCOMMUNITY) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone",
					MCTools.getLoginedAccount(CCommunityRightDialog.this)
							.getPhone());
			param.put("cid", String.valueOf(MCTools.getNOWCOMMUNITY().getCid()));
			communityManager.join(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					MCTools.INNEWCOMMUNITY = false;
					finish();
				}
			});
		} else {

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onTouchEvent(event);
	}

}
