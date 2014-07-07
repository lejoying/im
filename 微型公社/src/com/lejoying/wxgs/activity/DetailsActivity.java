package com.lejoying.wxgs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.TimeUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class DetailsActivity extends Activity {
	MainApplication app = MainApplication.getMainApplication();

	Intent intent;
	GroupShare share;

	LinearLayout ll_message_info, ll_detailContent, ll_praise, ll_praiseMember,
			ll_messageDetailComments;
	RelativeLayout rl_sendComment, backView;
	TextView tv_praiseNum, tv_checkComment, tv_sendComment,
			tv_squareMessageSendUserName, tv_messageTime;
	ImageView iv_addPraise, iv_addComment, iv_comment,
			iv_squareMessageDetailBack, iv_messageUserHead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initLayout();
		initEvent();
		initData();
	}

	private void initEvent() {
		backView.setOnTouchListener(new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(
					DetailsActivity.this,
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							finish();
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					backView.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					backView.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				return backviewDetector.onTouchEvent(event);
			}
		});

	}

	private void initLayout() {
		ll_message_info = (LinearLayout) findViewById(R.id.ll_message_info);
		ll_detailContent = (LinearLayout) findViewById(R.id.ll_detailContent);
		ll_praise = (LinearLayout) findViewById(R.id.ll_praise);
		ll_praiseMember = (LinearLayout) findViewById(R.id.ll_praiseMember);
		ll_messageDetailComments = (LinearLayout) findViewById(R.id.ll_messageDetailComments);
		rl_sendComment = (RelativeLayout) findViewById(R.id.rl_sendComment);
		backView = (RelativeLayout) findViewById(R.id.backview);
		tv_praiseNum = (TextView) findViewById(R.id.tv_praiseNum);
		tv_checkComment = (TextView) findViewById(R.id.tv_checkComment);
		tv_sendComment = (TextView) findViewById(R.id.tv_sendComment);
		tv_squareMessageSendUserName = (TextView) findViewById(R.id.tv_squareMessageSendUserName);
		tv_messageTime = (TextView) findViewById(R.id.tv_messageTime);
		iv_addPraise = (ImageView) findViewById(R.id.iv_addPraise);
		iv_addComment = (ImageView) findViewById(R.id.iv_addComment);
		iv_comment = (ImageView) findViewById(R.id.iv_comment);
		iv_squareMessageDetailBack = (ImageView) findViewById(R.id.iv_squareMessageDetailBack);
		iv_messageUserHead = (ImageView) findViewById(R.id.iv_messageUserHead);

	}

	private void initData() {
		intent = getIntent();
		share = (GroupShare) intent.getSerializableExtra("content");
		tv_messageTime.setText(TimeUtils.getTime(share.time));
		tv_praiseNum.setText("共获得" + share.praiseusers.size() + "个赞");
		tv_checkComment.setText("查看全部" + share.comments.size() + "条评论...");

		app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
				new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						iv_messageUserHead.setImageBitmap(bitmap);
					}
				});
	}

}
