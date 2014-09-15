package com.open.welinks;

import com.open.welinks.view.ThreeChoicesView;
import com.open.welinks.view.ThreeChoicesView.OnItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindMoreActivity extends Activity {

	public View backView;
	public TextView title, text_one, text_two, text_three;
	public RelativeLayout rightContainer;
	public LinearLayout layout_one, layout_two, layout_three;
	public ThreeChoicesView threeChoicesView;
	public OnClickListener mOnClickListener;
	public OnItemClickListener mOnItemClickListener;

	public int selectType = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_more);
		int defaultItem = getIntent().getIntExtra("type", 1);
		if (defaultItem > 0 && defaultItem < 4) {
			selectType = defaultItem;
		}
		initView();
	}

	private void initView() {
		backView = findViewById(R.id.backView);
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		layout_one = (LinearLayout) findViewById(R.id.layout_one);
		layout_two = (LinearLayout) findViewById(R.id.layout_two);
		layout_three = (LinearLayout) findViewById(R.id.layout_three);
		title = (TextView) findViewById(R.id.backTitleView);
		text_one = (TextView) findViewById(R.id.text_one);
		text_two = (TextView) findViewById(R.id.text_two);
		text_three = (TextView) findViewById(R.id.text_three);

		threeChoicesView = new ThreeChoicesView(this, selectType);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainer.addView(threeChoicesView, params);

		initListener();
	}

	private void initListener() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(layout_one)) {
					skipActivity(selectType, 1);
				} else if (view.equals(layout_two)) {
					skipActivity(selectType, 2);
				} else if (view.equals(layout_three)) {
					skipActivity(selectType, 3);
				}

			}

		};
		mOnItemClickListener = threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				selectType = position;
				changData(selectType);
			}
		};
		bindEvent();

	}

	private void bindEvent() {
		threeChoicesView.setOnItemClickListener(mOnItemClickListener);
		backView.setOnClickListener(mOnClickListener);
		layout_one.setOnClickListener(mOnClickListener);
		layout_two.setOnClickListener(mOnClickListener);
		layout_three.setOnClickListener(mOnClickListener);
		fillData();
	}

	private void fillData() {
		title.setText("查找更多");
		changData(selectType);
	}

	private void changData(int selectType) {
		if (selectType == 1) {
			text_one.setText("附近的广场");
			text_two.setText("查找广场");
			layout_two.setVisibility(View.GONE);
			layout_three.setVisibility(View.GONE);
		} else if (selectType == 2) {
			text_one.setText("附近的群组");
			text_two.setText("查找群组");
			layout_two.setVisibility(View.GONE);
			layout_three.setVisibility(View.GONE);
		} else if (selectType == 3) {
			text_one.setText("附近的人");
			text_two.setText("查找好友");
			text_three.setText("推荐好友");
			layout_three.setVisibility(View.GONE);
			layout_two.setVisibility(View.VISIBLE);
		}

	}

	private void skipActivity(int selectType, int selectItem) {
		String type = "";
		if (selectType == 1) {
			type = "square";
		} else if (selectType == 2) {
			type = "group";
		} else if (selectType == 3) {
			type = "account";
		}
		if (selectItem == 1) {
			Intent intent = new Intent(FindMoreActivity.this, NearbyActivity.class);
			intent.putExtra("type", type);
			startActivity(intent);
		} else if (selectItem == 2) {
			startActivity(new Intent(FindMoreActivity.this, SearchFriendActivity.class));
		}

	}

}
