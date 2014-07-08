package com.lejoying.wxgs.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.TimeUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.parser.JSONParser;

public class DetailsActivity extends Activity implements OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	InputMethodManager inputMethodManager;

	Intent intent;
	LayoutInflater inflater;
	GroupShare share;

	float height, width, dip;
	float density;

	boolean praiseStatus = false;

	LinearLayout ll_message_info, ll_detailContent, ll_praise, ll_praiseMember,
			ll_messageDetailComments;
	RelativeLayout rl_sendComment, backView;
	TextView tv_praiseNum, tv_checkComment, tv_sendComment,
			tv_squareMessageSendUserName, tv_messageTime;
	ImageView iv_addPraise, iv_checkComment, iv_comment,
			iv_squareMessageDetailBack, iv_messageUserHead;
	EditText et_comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initLayout();
		initEvent();
		initData();
	}

	private void initEvent() {
		iv_addPraise.setOnClickListener(this);
		ll_praise.setOnClickListener(this);
		iv_checkComment.setOnClickListener(this);
		iv_comment.setOnClickListener(this);
		tv_sendComment.setOnClickListener(this);

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
		et_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if ("".equals(et_comment.getText().toString())) {
					tv_sendComment
							.setBackgroundResource(R.drawable.squaredetail_comment_notselected);
					tv_sendComment.setTextColor(Color.WHITE);
				} else {
					tv_sendComment
							.setBackgroundResource(R.drawable.squaredetail_comment_selected);
					tv_sendComment.setTextColor(Color.BLACK);
				}
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
		iv_checkComment = (ImageView) findViewById(R.id.iv_checkComment);
		iv_comment = (ImageView) findViewById(R.id.iv_comment);
		iv_squareMessageDetailBack = (ImageView) findViewById(R.id.iv_squareMessageDetailBack);
		iv_messageUserHead = (ImageView) findViewById(R.id.iv_messageUserHead);
		et_comment = (EditText) findViewById(R.id.et_comment);
	}

	private void initData() {
		inflater = getLayoutInflater();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		intent = getIntent();
		share = (GroupShare) intent.getSerializableExtra("content");
		final List<String> images = share.content.images;
		List<String> voices = share.content.voices;
		String textContent = share.content.text;
		tv_messageTime.setText(TimeUtils.getTime(share.time));
		for (String str : share.praiseusers) {
			if (str.equals(app.data.user.phone)) {
				praiseStatus = true;
				break;
			}
		}
		for (int i = 0; i < images.size(); i++) {
			final int index = i;
			final ImageView imageView = new ImageView(this);
			ll_detailContent.addView(imageView);
			app.fileHandler.getSquareDetailImage(images.get(i), (int) width,
					new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							int height = (int) (bitmap.getHeight() * (width / bitmap
									.getWidth()));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									(int) width, height);
							imageView.setLayoutParams(params);
							imageView.setImageBitmap(bitmap);
							imageView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(
											DetailsActivity.this,
											PicAndVoiceDetailActivity.class);
									intent.putExtra("currentIndex", index);
									intent.putExtra("Activity", "Browse");
									intent.putStringArrayListExtra("content",
											(ArrayList<String>) images);
									startActivity(intent);
								}
							});
						}
					});
		}
		for (String str : voices) {

		}
		if (!"".equals(textContent)) {
			TextView textview = new TextView(this);
			textview.setTextColor(Color.WHITE);
			textview.setBackgroundColor(Color.parseColor("#26ffffff"));
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			int padding = (int) (10 * density + 0.5f);
			textview.setPadding(padding, padding, padding, padding);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			textview.setLayoutParams(params);
			textview.setText(textContent);
			ll_detailContent.addView(textview);
		}

		app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
				new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						iv_messageUserHead.setImageBitmap(bitmap);
					}
				});
		resetPraises();
		resetComments();
	}

	private void resetPraises() {
		List<String> praiseusers = share.praiseusers;
		tv_praiseNum.setText("共获得" + praiseusers.size() + "个赞");
		ll_praiseMember.removeAllViews();
		for (int i = 0; i < praiseusers.size(); i++) {

		}
	}

	private void resetComments() {
		List<Comment> comments = share.comments;
		tv_checkComment.setText("查看全部" + comments.size() + "条评论...");
		ll_messageDetailComments.removeAllViews();
		// for (int i = 0; i < 10; i++) {
		// Comment fakecomment = new Comment();
		// fakecomment.content = "123456789";
		// fakecomment.head = app.data.user.head;
		// fakecomment.nickName = "哈哈哈";
		// fakecomment.nickNameTo="笑个屁";
		// if(i%3==0){
		// fakecomment.phoneTo = "";
		// }else{
		// fakecomment.phoneTo = "110";
		// }
		// fakecomment.time = 1404794252435l;
		// comments.add(fakecomment);
		// }
		for (Comment comment : comments) {
			View view = inflater
					.inflate(R.layout.groupshare_commentchild, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins((int) (10 * density + 0.5f), 0,
					(int) (10 * density + 0.5f), 0);
			view.setLayoutParams(params);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView content = (TextView) view.findViewById(R.id.content);
			TextView reply = (TextView) view.findViewById(R.id.reply);
			TextView receive = (TextView) view.findViewById(R.id.receive);
			TextView received = (TextView) view.findViewById(R.id.received);
			final ImageView head = (ImageView) view.findViewById(R.id.head);

			content.setText(comment.content);
			time.setText(TimeUtils.getTime(comment.time));
			receive.setText(comment.nickName);
			received.setText(comment.nickNameTo);

			if ("".equals(comment.phoneTo)) {
				reply.setVisibility(View.GONE);
				received.setVisibility(View.GONE);
			}
			app.fileHandler.getHeadImage(comment.head, "男", new FileResult() {

				@Override
				public void onResult(String where, Bitmap bitmap) {
					head.setImageBitmap(bitmap);
				}
			});
			ll_messageDetailComments.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_addPraise:
			addPraise(!praiseStatus);
			break;
		case R.id.ll_praise:
			// TODO show the praised members
			break;
		case R.id.iv_checkComment:
			if (ll_messageDetailComments.getVisibility() == View.VISIBLE) {
				ll_messageDetailComments.setVisibility(View.GONE);
			} else {
				ll_messageDetailComments.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_comment:
			// TODO show the faces
			break;
		case R.id.tv_sendComment:
			sendComment();
			break;
		default:
			break;
		}

	}

	private void addPraise(final boolean flag) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			public void success(JSONObject jData) {
				System.out.println("success");
				praiseStatus = !praiseStatus;
				// modifyShare();
				// resetPraises();
				// resetComments();
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				System.out.println(jData.toString());
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_ADDPRAISE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", app.data.currentGroup);
				params.put("gsid", share.gsid);
				params.put("option", flag + "");
				settings.params = params;

			}

		});

	}

	private void sendComment() {
		if (inputMethodManager.isActive()) {
			inputMethodManager.hideSoftInputFromWindow(DetailsActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		final String commentContent = et_comment.getText().toString().trim();
		if ("".equals(commentContent)) {
			Alert.showMessage("评论内容不能为空");
			return;
		}
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_ADDCOMMENT;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("phoneTo", share.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("nickName", app.data.user.nickName);
				params.put("head", app.data.user.head);
				params.put("gid", app.data.currentGroup);
				params.put("gsid", share.gsid);
				params.put("contentType", "text");
				params.put("content", commentContent);
				settings.params = params;

			}

			@Override
			public void success(JSONObject jData) {
				et_comment.setText("");
				// modifyShare();
				// resetPraises();
				// resetComments();
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				System.out.println("------" + jData.toString());
			}
		});
	}

	private void modifyShare() {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				final GroupShare newShare;
				try {
					ArrayList<GroupShare> shares = JSONParser
							.generateSharesFromJSON(jData
									.getJSONArray("shares"));
					newShare = shares.get(0);
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.groupsMap
									.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
									.put(newShare.gsid, newShare);
						}
					});
					share = newShare;
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_GETSHARE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", app.data.currentGroup);
				params.put("gsid", share.gsid);
				settings.params = params;

			}
		});

	}
}
