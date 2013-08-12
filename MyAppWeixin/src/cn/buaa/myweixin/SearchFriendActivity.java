package cn.buaa.myweixin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SearchFriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends);
	}

	public void back(View v) {
		finish();
	}
}
