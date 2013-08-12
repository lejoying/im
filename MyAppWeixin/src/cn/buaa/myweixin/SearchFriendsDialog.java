package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchFriendsDialog extends Activity {

	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends_dialog);
	}

	// 点击其他位置收起菜单
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void searchFriend(View v) {
		Intent intent = new Intent(SearchFriendsDialog.this,
				SearchFriendActivity.class);
		startActivity(intent);
		finish();
	}

}
