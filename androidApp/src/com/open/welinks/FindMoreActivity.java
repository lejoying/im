package com.open.welinks;

import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.customView.ThreeChoicesView.OnItemClickListener;

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
	public TextView backTitleView, textOneView, textTwoView, textThreeView;
	public RelativeLayout rightContainer;
	public LinearLayout layoutOneVIew, layoutTwoView, layoutThreeView;
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
		layoutOneVIew = (LinearLayout) findViewById(R.id.layout_one);
		layoutTwoView = (LinearLayout) findViewById(R.id.layout_two);
		layoutThreeView = (LinearLayout) findViewById(R.id.layout_three);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		textOneView = (TextView) findViewById(R.id.text_one);
		textTwoView = (TextView) findViewById(R.id.text_two);
		textThreeView = (TextView) findViewById(R.id.text_three);

		threeChoicesView = new ThreeChoicesView(this, selectType);
		threeChoicesView.setButtonOneText("最新");
		threeChoicesView.setButtonTwoText("最热");
		threeChoicesView.setButtonThreeText("关注");
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
				} else if (view.equals(layoutOneVIew)) {
					skipActivity(selectType, 1);
				} else if (view.equals(layoutTwoView)) {
					skipActivity(selectType, 2);
				} else if (view.equals(layoutThreeView)) {
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
		layoutOneVIew.setOnClickListener(mOnClickListener);
		layoutTwoView.setOnClickListener(mOnClickListener);
		layoutThreeView.setOnClickListener(mOnClickListener);
		fillData();
	}

	private void fillData() {
		backTitleView.setText("查找更多");
		changData(selectType);
	}

	private void changData(int selectType) {
		if (selectType == 1) {
			textOneView.setText("附近的最新分享");
			textTwoView.setText("查找社区");
			layoutTwoView.setVisibility(View.GONE);
			layoutThreeView.setVisibility(View.GONE);
		} else if (selectType == 2) {
			textOneView.setText("附近的最热分享");
			textTwoView.setText("查找群组");
			layoutTwoView.setVisibility(View.GONE);
			layoutThreeView.setVisibility(View.GONE);
		} else if (selectType == 3) {
			textOneView.setText("附近的关注分享");
			textTwoView.setText("查找好友");
			textThreeView.setText("推荐好友");
			layoutThreeView.setVisibility(View.GONE);
			layoutTwoView.setVisibility(View.VISIBLE);
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
