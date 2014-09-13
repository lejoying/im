package com.open.welinks;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.ViewManage;

public class GroupListActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "GroupListActivity";
	public MyLog log = new MyLog(tag, false);

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;
	public RelativeLayout rightContainer;

	public TextView createGroupButton;

	public ListView groupListContainer;

	public DisplayMetrics displayMetrics;

	public OnClickListener mOnClickListener;
	public OnItemClickListener mOnItemClickListener;

	public List<String> groups;
	public Map<String, Group> groupsMap;
	public GroupListAdapter groupListAdapter;

	public Bitmap bitmap;

	public ViewManage viewManage = ViewManage.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.groupListActivity = this;
		initView();
		initializeListeners();
		bindEvent();

		initData();
	}

	private void initData() {
		parser.check();
		groups = data.relationship.groups;
		groupsMap = data.relationship.groupsMap;
		groupListAdapter = new GroupListAdapter();
		groupListContainer.setAdapter(groupListAdapter);
	}

	private void initView() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		mInflater = this.getLayoutInflater();

		Resources resources = getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		setContentView(R.layout.activity_group_list);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.backTitileView.setText("群组列表");
		this.titleView = (TextView) findViewById(R.id.titleContent);
		this.rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);

		this.groupListContainer = (ListView) findViewById(R.id.groupListContainer);

		int dp_5 = (int) (5 * displayMetrics.density);
		this.createGroupButton = new TextView(this);
		this.createGroupButton.setGravity(Gravity.CENTER);
		this.createGroupButton.setTextColor(Color.WHITE);
		this.createGroupButton.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		this.createGroupButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		this.createGroupButton.setText("创建群组");
		this.createGroupButton.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.rightContainer.addView(this.createGroupButton, layoutParams);
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(createGroupButton)) {
					Intent intent = new Intent(GroupListActivity.this, CreateGroupStartActivity.class);
					startActivity(intent);
				} else if (view.equals(backView)) {
					finish();
				}
			}
		};
		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(GroupListActivity.this, BusinessCardActivity.class);
				intent.putExtra("type", "group");
				intent.putExtra("key", groups.get(position));
				startActivity(intent);
			}
		};
	}

	private void bindEvent() {
		this.createGroupButton.setOnClickListener(mOnClickListener);
		this.backView.setOnClickListener(mOnClickListener);
		this.groupListContainer.setOnItemClickListener(mOnItemClickListener);
	}

	public class GroupListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			groups = data.relationship.groups;
			groupsMap = data.relationship.groupsMap;
		}

		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GroupHolder holder = null;
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = mInflater.inflate(R.layout.activity_group_list_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.head);
				holder.nameView = (TextView) convertView.findViewById(R.id.title);
				holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			Group group = groupsMap.get(groups.get(position));
			holder.headView.setImageBitmap(bitmap);
			holder.nameView.setText(group.name);
			holder.descriptionView.setText(group.description);
			return convertView;
		}
	}

	public class GroupHolder {
		public ImageView headView;
		public TextView nameView;
		public TextView descriptionView;
	}
}
