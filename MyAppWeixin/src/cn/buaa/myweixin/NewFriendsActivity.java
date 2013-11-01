package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.buaa.myweixin.apiutils.Friend;
import cn.buaa.myweixin.apiutils.MCTools;

public class NewFriendsActivity extends Activity {

	private ListView lv_newfriends;
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newfriends);
		initView();
	}

	public void initView() {
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lv_newfriends = (ListView) findViewById(R.id.lv_newfriends);
		List<Friend> newFriends = MCTools.getNewFriends();
		lv_newfriends.setAdapter(new FriendAdapter(newFriends));
		lv_newfriends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("aaaa");
			}
		});
	}


	public void back(View v) {
		finish();
	}

	public void clean(View v) {

	}

	class FriendAdapter extends BaseAdapter {
		List<Friend> newFriends = new ArrayList<Friend>();

		public FriendAdapter(List<Friend> newFriends) {
			super();
			this.newFriends = newFriends;
		}

		@Override
		public int getCount() {
			return newFriends.size();
		}

		@Override
		public Object getItem(int position) {
			return newFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return newFriends.get(position).getUid();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout rl_item = (RelativeLayout) inflater.inflate(
					R.layout.newfriendsitem, null);
			ImageView iv_head = (ImageView) rl_item.findViewById(R.id.iv_head);
			TextView tv_nickname = (TextView) rl_item
					.findViewById(R.id.tv_nickname);
			TextView tv_message = (TextView) rl_item
					.findViewById(R.id.tv_message);

			tv_nickname.setText(newFriends.get(position).getNickName());

			return rl_item;
		}

	}

}
