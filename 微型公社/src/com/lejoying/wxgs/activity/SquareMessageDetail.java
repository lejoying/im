package com.lejoying.wxgs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.ChatFriendFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.view.RecordView;
import com.lejoying.wxgs.activity.view.RecordView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecordView.ProgressListener;
import com.lejoying.wxgs.activity.view.SquareMessageInfoScrollView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileSettings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SquareMessageDetail extends BaseActivity {
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater inflater;

	SquareMessage message;
	GroupShare share;
	String mCurrentSquareID;
	String gmid;
	TextView textPanel;
	List<MediaPlayer> players;
	List<RecordView> recordViews;

	float height, width, dip;
	float density;

	boolean praiseStatus = false;

	SquareMessageInfoScrollView sc_square_message_info;
	SquareMessageInfoScrollView sc_square_message_info_all;
	RelativeLayout rl_square_message_menu;
	RelativeLayout squareDetailBottomBar;
	ImageView squareMessageDetailBack;
	View backView;
	TextView squareMessageSendUserName;
	LinearLayout squareMessageDetailComments;
	LinearLayout detailContent;
	ImageView messageContentHead;
	LinearLayout timeContent;
	RelativeLayout topMenuBar;
	EditText et_comment;
	TextView releaseComment;

	TextView messageTime;

	int SCROLL_TOP = 0X01;
	int SCROLL_BUTTOM = 0X02;
	int SCROLL_BETWEEN = 0X03;
	int scrollStatus = SCROLL_TOP;
	int scrollViewY = 0;

	View vPopWindow;
	PopupWindow popWindow;

	InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if (type.equals("square")) {
			mCurrentSquareID = intent.getStringExtra("mCurrentSquareID");
			gmid = intent.getStringExtra("gmid");
		} else if (type.equals("share")) {
			share = (GroupShare) intent.getSerializableExtra("content");
		}
		this.message = app.data.squareMessagesMap.get(mCurrentSquareID).get(
				gmid);
		inflater = this.getLayoutInflater();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		players = new ArrayList<MediaPlayer>();
		recordViews = new ArrayList<RecordView>();
		setContentView(R.layout.fragment_square_message_detail);
		sc_square_message_info = (SquareMessageInfoScrollView) findViewById(R.id.sc_square_message_info);
		sc_square_message_info_all = (SquareMessageInfoScrollView) findViewById(R.id.sc_square_message_info_all);
		rl_square_message_menu = (RelativeLayout) findViewById(R.id.rl_square_message_menu);
		squareMessageDetailBack = (ImageView) findViewById(R.id.iv_squareMessageDetailBack);
		backView = findViewById(R.id.backview);
		squareMessageSendUserName = (TextView) findViewById(R.id.tv_squareMessageSendUserName);
		squareMessageDetailComments = (LinearLayout) findViewById(R.id.ll_squareMessageDetailComments);
		detailContent = (LinearLayout) findViewById(R.id.detailContent);
		messageContentHead = (ImageView) findViewById(R.id.iv_messageUserHead);
		messageTime = (TextView) findViewById(R.id.tv_messageTime);
		timeContent = (LinearLayout) findViewById(R.id.ll_rightTime);
		topMenuBar = (RelativeLayout) findViewById(R.id.rl_topMenuBar);
		et_comment = (EditText) findViewById(R.id.et_comment);
		releaseComment = (TextView) findViewById(R.id.tv_confirm_release_comment);
		squareDetailBottomBar = (RelativeLayout) findViewById(R.id.squareDetailBottomBar);
		sc_square_message_info_all.setOverScrollMode(View.OVER_SCROLL_NEVER);
		sc_square_message_info.setOverScrollMode(View.OVER_SCROLL_NEVER);
		initData();
		generateMessageContent();
		getSquareMessageDetail(message.phone, message.gmid);
		addDetailBottomBarChildView();
		getSquareMessageComments();
		initEvent();
		// CircleMenu.hide();
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer play = players.get(i);
			RecordView recordView = recordViews.get(i);
			if (play.isPlaying()) {
				recordView.pauseProgress();
				play.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer play = players.get(i);
			RecordView recordView = recordViews.get(i);
			if (play.isPlaying()) {
				recordView.stopProgress();
				play.stop();
			}
			play.release();
		}
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void initData() {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	public void generateMessageContent() {
		detailContent.removeAllViews();
		app.fileHandler.getHeadImage(message.head, "男", new FileResult() {

			@Override
			public void onResult(String where, Bitmap bitmap) {
				messageContentHead.setImageBitmap(app.fileHandler.bitmaps
						.get(message.head));
			}
		});
		final List<String> images = message.content.images;
		List<String> voices = message.content.voices;
		String textContent = message.content.text != "" ? message.content.text
				: "";
		squareMessageSendUserName.setText(message.nickName);
		messageTime.setText(convertTime(System.currentTimeMillis(),
				message.time));
		for (int i = 0; i < images.size(); i++) {
			final ImageView imageView = new ImageView(this);
			final int index = i;
			detailContent.addView(imageView);
			final String fileName = images.get(i);
			app.fileHandler.getSquareDetailImage(fileName, (int) width,
					new FileResult() {
						@Override
						public void onResult(String where, Bitmap bitmap) {
							int height = (int) (bitmap.getHeight() * (width / bitmap
									.getWidth()));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									(int) width, height);
							imageView.setLayoutParams(params);
							imageView.setImageBitmap(bitmap);
							// imageView.setOnClickListener(new
							// OnClickListener() {
							//
							// @Override
							// public void onClick(View v) {
							// Intent intent = new Intent(
							// SquareMessageDetail.this,
							// PicAndVoiceDetailActivity.class);
							// intent.putExtra("currentIndex", index);
							// intent.putExtra("Activity", "Browse");
							// intent.putStringArrayListExtra("content",
							// (ArrayList<String>) images);
							// startActivity(intent);
							// }
							// });
						}
					});
			imageView.setOnTouchListener(new OnTouchListener() {

				boolean flag = false;
				int count = 0;

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flag = false;
						count = 0;
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						count++;
						flag = true;
						sc_square_message_info
								.requestDisallowInterceptTouchEvent(flag);
						// sc_square_message_info.onTouchEvent(event);
					}
					if (count < 15) {
						flag = false;
					}
					return flag;
				}
			});

			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SquareMessageDetail.this,
							PicAndVoiceDetailActivity.class);
					intent.putExtra("currentIndex", index);
					intent.putExtra("Activity", "Browse");
					intent.putStringArrayListExtra("content",
							(ArrayList<String>) images);
					startActivity(intent);
				}
			});
			// app.fileHandler.getImage(fileName, new FileResult() {
			//
			// @Override
			// public void onResult(String where, Bitmap bitmap0) {
			// Bitmap bitmap = app.fileHandler.bitmaps.get(fileName);
			// int height = (int) (bitmap.getHeight() * (width / bitmap
			// .getWidth()));
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// (int) width, height);
			// imageView.setLayoutParams(params);
			// bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width),
			// height, true);
			// imageView.setImageBitmap(bitmap);
			// }
			// });

		}
		if (images.size() == 0 && voices.size() != 0) {
			TextView textView = new TextView(this);
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params1.topMargin = (int) dp2px(40);
			detailContent.addView(textView, params1);
		}
		for (int i = 0; i < voices.size(); i++) {
			final RecordView recordView = new RecordView(
					SquareMessageDetail.this);
			recordView.setDragEnable(false);
			recordView.setMode(RecordView.MODE_PROGRESS);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					(int) width, (int) (height * 0.48080331f));
			detailContent.addView(recordView, params);

			final String fileName = voices.get(i);
			app.fileHandler.getFile(new FileInterface() {

				@Override
				public void setParams(FileSettings settings) {
					settings.fileName = fileName;
					settings.folder = app.sdcardVoiceFolder;
				}

				@Override
				public void onSuccess(Boolean flag, String fileName) {
					if (flag) {
						File file = new File(app.sdcardVoiceFolder, fileName);
						if (file.exists()) {
							try {
								final MediaPlayer player = MediaPlayer.create(
										SquareMessageDetail.this,
										Uri.parse(file.getAbsolutePath()));
								if (player == null) {
									// Log.e("Coolspan", fileName);
									return;
								}
								players.add(player);
								recordViews.add(recordView);
								recordView.setProgressTime(player.getDuration());
								recordView
										.setPlayButtonClickListener(new PlayButtonClickListener() {

											@Override
											public void onPlay() {
												if (!player.isPlaying()) {
													player.start();
												}
											}

											@Override
											public void onPause() {
												if (player.isPlaying()) {
													player.pause();
												}

											}
										});
								recordView
										.setProgressListener(new ProgressListener() {

											@Override
											public void onProgressEnd() {
												player.pause();
												// player.reset();
											}

											@Override
											public void onDrag(float percent) {
												// mpPlayer.seekTo((int)
												// (mpPlayer
												// .getDuration() * percent));

											}
										});
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							}
						} else {
							// to do loading voice failed
						}
					} else {
						// to do loading voice failed
					}

				}
			});
		}
		textPanel = new TextView(this);
		textPanel.setTextColor(Color.WHITE);
		textPanel.setText(ExpressionUtil.getExpressionString(
				SquareMessageDetail.this, textContent,
				ChatFriendFragment.faceRegx, SquareFragment.expressionFaceMap));
		if (images.size() == 0 && voices.size() == 0) {
			TextView textView = new TextView(this);
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params1.topMargin = (int) dp2px(40);
			detailContent.addView(textView, params1);
		}
		detailContent.addView(textPanel);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO not delete after code
		// LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT,
		// (int) ((height - MainActivity.statusBarHeight) * 0.917f));
		// sc_square_message_info_all.setLayoutParams(paramsInfo);
		// LinearLayout.LayoutParams paramsDetailBottomBar = new
		// LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT,
		// (int) ((height - MainActivity.statusBarHeight) * 0.083f));
		// squareDetailBottomBar.setLayoutParams(paramsDetailBottomBar);
		// initSquareDetailBottomBar((int) ((height -
		// MainActivity.statusBarHeight) * 0.083f));

		// RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		// squareMessageDetailBack.getLayoutParams());
		// params.width = (int) (height * 0.046f);
		// params.leftMargin = (int) (width * 0.051302298f);
		// params.addRule(RelativeLayout.CENTER_VERTICAL);
		// squareMessageDetailBack.setLayoutParams(params);

		super.onWindowFocusChanged(hasFocus);
	}

	ImageView timeImage;
	TextView timeText;
	// ImageView distanceImage;
	// TextPanel distanceText;
	// ImageView commentImage;
	// TextPanel commentText;
	// ImageView praiseImage;
	// TextPanel praiseText;
	ImageView collectImage;
	TextPanel collectText;

	String convertTime(long currentTime, long targetTime) {
		long time = currentTime - targetTime;
		if (time > 0) {
			time /= 1000;
			if (time < 60) {
				return time + "秒前";
			}
			time /= 60;
			if (time < 60) {
				return time + "分钟前";
			}
			time /= 60;
			if (time < 24) {
				return time + "小时前";
			}
			time /= 24;
			if (time < 30) {
				return time + "天前";
			}
			time /= 30;
			if (time < 6) {
				return time + "个月前";
			} else if (time < 12) {
				return "半年前";
			}
			time /= 12;
			return time + "年前";
		}
		return "";
	}

	private void addDetailBottomBarChildView() {
		// rl_square_message_menu

		timeImage = new ImageView(this);
		for (int i = 0; i < message.praiseusers.size(); i++) {
			if (message.praiseusers.get(i).equals(app.data.user.phone)) {
				praiseStatus = true;
				break;
			}
		}
		if (praiseStatus) {
			timeImage.setImageResource(R.drawable.praised);
		} else {
			timeImage.setImageResource(R.drawable.praise);
		}
		timeText = new TextView(this);
		timeText.setSingleLine(true);
		timeText.setTextColor(Color.WHITE);
		timeText.setTextScaleX(1.2f);
		timeText.setText("共获得" + message.praiseusers.size() + "个赞");
		// distanceImage = new ImageView(this);
		// distanceImage.setImageResource(R.drawable.distance);
		// distanceText = new TextPanel(this);
		// distanceText.singleLine(true);
		// distanceText.setTextColor(Color.WHITE);
		// distanceText.setText("000m");
		// commentImage = new ImageView(this);
		// commentImage.setImageResource(R.drawable.comment);
		// commentText = new TextPanel(this);
		// commentText.singleLine(true);
		// commentText.setTextColor(Color.WHITE);
		// commentText.setText("000");
		// praiseImage = new ImageView(this);
		// for (int i = 0; i < message.praiseusers.size(); i++) {
		// if (message.praiseusers.get(i).equals(app.data.user.phone)) {
		// praiseStatus = true;
		// break;
		// }
		// }
		// if (praiseStatus) {
		// praiseImage.setImageResource(R.drawable.praised);
		// } else {
		// praiseImage.setImageResource(R.drawable.praise);
		// }
		// praiseText = new TextPanel(this);
		// praiseText.singleLine(true);
		// praiseText.setTextColor(Color.WHITE);
		// praiseText.setText(message.praiseusers.size() + "");
		collectImage = new ImageView(this);
		collectImage.setImageResource(R.drawable.comment);
		collectText = new TextPanel(this);
		collectText.singleLine(true);
		collectText.setTextColor(Color.WHITE);
		collectText.setText("000");

		rl_square_message_menu.addView(timeImage);
		rl_square_message_menu.addView(timeText);
		// rl_square_message_menu.addView(distanceImage);
		// rl_square_message_menu.addView(distanceText);
		// rl_square_message_menu.addView(commentImage);
		// rl_square_message_menu.addView(commentText);
		// rl_square_message_menu.addView(praiseImage);
		// rl_square_message_menu.addView(praiseText);
		rl_square_message_menu.addView(collectImage);
		rl_square_message_menu.addView(collectText);

		LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) ((height - MainActivity.statusBarHeight) * 0.917f));
		sc_square_message_info_all.setLayoutParams(paramsInfo);
		LinearLayout.LayoutParams paramsDetailBottomBar = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) ((height - MainActivity.statusBarHeight) * 0.083f));
		squareDetailBottomBar.setLayoutParams(paramsDetailBottomBar);
		initSquareDetailBottomBar((int) ((height - MainActivity.statusBarHeight) * 0.083f));
	}

	private void initSquareDetailBottomBar(int height) {
		// int width = rl_square_message_menu.getWidth();
		// System.out.println(width + "++++++++++++++");
		// int height = rl_square_message_menu.getHeight();
		// System.out.println(width + "::::" + height + ">>>>"
		// + squareDetailBottomBar.getHeight());
		int side = (int) (height * 0.35185f);
		// int top = (height - side) / 2;
		float textSize = height * 0.234286f;
		int textTop = (int) ((height - textSize) / 2);
		int textLeftA = (int) (width * 0.01651917f);
		timeImage.setLayoutParams(generateLayoutParams((int) (side * 1.4f),
				(int) (side * 1.4f), (int) (width * 0.04488f),
				(int) ((height - side * 1.4f) / 2)));

		timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		timeText.setLayoutParams(generateLayoutParams((int) ((width * 0.2722f)
				- (width * 0.03488f) - side) * 5, side,
				(int) ((int) (width * 0.03488f) + side * 1.4f + textLeftA),
				textTop));

		// distanceImage.setLayoutParams(generateLayoutParams(side, side,
		// (int) (width * 0.2722f), top));
		// distanceText.setTextSize(textSize);
		// distanceText.setLayoutParams(generateLayoutParams(
		// (int) ((width * 0.55917f) - (width * 0.2722f) - side), side,
		// (int) (width * 0.2722f) + side + textLeftA, textTop));
		// commentImage.setLayoutParams(generateLayoutParams(side, side,
		// (int) (width * 0.55917f), top));
		// commentText.setTextSize(textSize);
		// commentText.setLayoutParams(generateLayoutParams(
		// (int) ((width * 0.68935f) - (width * 0.55917f) - side), side,
		// (int) (width * 0.55917f) + side + textLeftA, textTop));
		// praiseImage.setLayoutParams(generateLayoutParams(side, side,
		// (int) (width * 0.68935f), top));
		// praiseText.setTextSize(textSize);
		// praiseText.setLayoutParams(generateLayoutParams(
		// (int) ((width * 0.8284f) - (width * 0.68935f) - side), side,
		// (int) (width * 0.68935f) + side + textLeftA, textTop));
		collectImage.setLayoutParams(generateLayoutParams((int) (side * 1.4f),
				(int) (side * 1.4f), (int) (width * 0.8184f),
				(int) ((height - side * 1.4f) / 2)));
		collectText.setTextSize(textSize);
		collectText.setLayoutParams(generateLayoutParams((int) (width * 0.96f
				- (width * 0.8284f) - side * 1.4f), (int) (side * 1.4f),
				(int) ((int) (width * 0.8284f) + side * 1.4f + textLeftA),
				textTop));

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) (width * 0.03791469f);
		params.rightMargin = (int) (width * 0.03791469f);
		params.topMargin = 20;
		params.bottomMargin = 20;
		textPanel.setLayoutParams(params);
		// setting sqaure message content font size
		textPanel.setTextSize(TypedValue.COMPLEX_UNIT_PX, width * 0.03513296f);
	}

	private RelativeLayout.LayoutParams generateLayoutParams(int w, int h,
			int left, int top) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w,
				h);
		params.leftMargin = left;
		params.topMargin = top;
		return params;
	}

	boolean isTouchOnContent;

	Friend currentCommentUser;

	private void initEvent() {
		et_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				String content = arg0.toString();
				if ("".equals(content)) {
					releaseComment
							.setBackgroundResource(R.drawable.squaredetail_comment_notselected);
					releaseComment.setTextColor(Color.WHITE);
				} else {
					releaseComment
							.setBackgroundResource(R.drawable.squaredetail_comment_selected);
					releaseComment.setTextColor(Color.BLACK);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

		});
		messageContentHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				currentCommentUser = null;
				et_comment.setHint(" 添加评论 ... ...");
			}
		});
		releaseComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(
							SquareMessageDetail.this.getCurrentFocus()
									.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				String commentContent = et_comment.getText().toString().trim();
				if ("".equals(commentContent)) {
					Alert.showMessage("评论内容不能为空");
					return;
				}
				et_comment.setHint(" 添加评论 ... ...");
				et_comment.setText("");
				JSONObject object = new JSONObject();
				try {
					if (currentCommentUser == null) {
						object.put("phoneTo", "");
						object.put("nickNameTo", "");
						object.put("headTo", "");
					} else {
						object.put("phoneTo", currentCommentUser.phone);
						object.put("nickNameTo", currentCommentUser.nickName);
						object.put("headTo", currentCommentUser.head);
					}
					object.put("content", commentContent);
					object.put("contentType", "text");
					addSquareMessageComments(object);
					// sc_square_message_info.scrollTo(0,
					// (int) sc_square_message_info.getScrollY());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		timeContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPopWindow(SquareMessageDetail.this, detailContent);
			}
		});
		timeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (praiseStatus) {
					praiseSquareMessage(false);
				} else {
					praiseSquareMessage(true);
				}
			}
		});

		final GestureDetector backViewDetector = new GestureDetector(
				SquareMessageDetail.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						finish();
						return true;
					}
				});
		backView.setOnTouchListener(new OnTouchListener() {

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
				backViewDetector.onTouchEvent(event);
				return true;
			}
		});

		sc_square_message_info.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		sc_square_message_info_all.setOnTouchListener(new OnTouchListener() {

			float lastY = 0;
			boolean overflow;
			boolean flag;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float currentY = event.getY();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					lastY = 0;
					overflow = detailContent.getHeight() > sc_square_message_info_all
							.getHeight();
					flag = true;
				}
				if (overflow) {
					if (sc_square_message_info.getScrollY() != 0
							|| (sc_square_message_info_all.getScrollY() == detailContent
									.getHeight()
									- sc_square_message_info_all.getHeight() && lastY
									- currentY > 0)) {
						flag = false;
					}
					lastY = currentY;
					sc_square_message_info
							.requestDisallowInterceptTouchEvent(flag);
				}
				return true;
			}
		});
	}

	public void praiseSquareMessage(final boolean flag) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_ADDSQUAREPRAISE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentSquareID);
				params.put("gmid", gmid);
				params.put("operation", flag + "");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					String notice = jData.getString("提示信息");
					if ("点赞广播成功".equals(notice)) {
						if (flag) {
							praiseStatus = true;
							app.dataHandler.exclude(new Modification() {

								@Override
								public void modifyData(Data data) {
									SquareMessage squareMessage = data.squareMessagesMap
											.get(SquareFragment.mCurrentSquareID)
											.get(message.gmid);
									squareMessage.praiseusers
											.add(app.data.user.phone);
									message = squareMessage;
								}

								@Override
								public void modifyUI() {
									timeImage
											.setImageResource(R.drawable.praised);
									timeText.setText("共获得"
											+ message.praiseusers.size() + "个赞");
								}
							});
						} else {
							praiseStatus = false;
							app.dataHandler.exclude(new Modification() {
								@Override
								public void modifyData(Data data) {
									SquareMessage squareMessage = data.squareMessagesMap
											.get(SquareFragment.mCurrentSquareID)
											.get(message.gmid);
									for (int i = 0; i < squareMessage.praiseusers
											.size(); i++) {
										if (squareMessage.praiseusers.get(i)
												.equals(app.data.user.phone)) {
											squareMessage.praiseusers.remove(i);
											break;
										}
									}
									message = squareMessage;
								}

								@Override
								public void modifyUI() {
									timeImage
											.setImageResource(R.drawable.praise);
									timeText.setText("共获得"
											+ message.praiseusers.size() + "个赞");
								}
							});
						}
					}
					// Log.e("Coolspan", notice + "-" + flag + "------点赞提示消息");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void getSquareMessageComments() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUARECOMMENTS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentSquareID);
				params.put("gmid", gmid);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final ArrayList<Comment> comments = JSONParser
							.generateCommentsFromJSON(jData
									.getJSONArray("comments"));
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							SquareMessage squareMessage = data.squareMessagesMap
									.get(mCurrentSquareID).get(message.gmid);
							squareMessage.comments = comments;
						}

						@Override
						public void modifyUI() {
							generateCommentsViews(comments);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void addSquareMessageComments(final JSONObject jsonObject) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_ADDSQUARECOMMENT;
				Map<String, String> params = new HashMap<String, String>();
				try {
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("gid", mCurrentSquareID);
					params.put("gmid", gmid);
					params.put("nickName", app.data.user.nickName);
					params.put("head", app.data.user.head);
					params.put("phoneTo", jsonObject.getString("phoneTo"));
					params.put("nickNameTo", jsonObject.getString("nickNameTo"));
					params.put("headTo", jsonObject.getString("headTo"));
					params.put("content", jsonObject.getString("content"));
					params.put("contentType",
							jsonObject.getString("contentType"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				// System.out.println("评论成功" + jData);
				getSquareMessageComments();
			}
		});
	}

	public void generateCommentsViews(List<Comment> comments) {
		squareMessageDetailComments.removeAllViews();
		for (int i = 0; i < comments.size(); i++) {
			final Comment comment = comments.get(i);
			View mContent = inflater.inflate(
					R.layout.fragment_square_message_comments, null);
			final ImageView mHead = (ImageView) mContent
					.findViewById(R.id.iv_commentHead);
			TextView sInitnative = (TextView) mContent
					.findViewById(R.id.tv_initiative);
			TextView sReply = (TextView) mContent.findViewById(R.id.tv_reply);
			TextView sPassivity = (TextView) mContent
					.findViewById(R.id.tv_passivity);
			TextView sCommentContent = (TextView) mContent
					.findViewById(R.id.tv_comment_content);
			sInitnative.setText(comment.nickName);
			sCommentContent.setText(comment.content);
			if (comment.phoneTo != null) {
				if (!"".equals(comment.phoneTo)) {
					sReply.setVisibility(View.VISIBLE);
					sPassivity.setVisibility(View.VISIBLE);
					sPassivity.setText(comment.nickNameTo);
				}
			}
			app.fileHandler.getHeadImage(comment.head, "男", new FileResult() {

				@Override
				public void onResult(String where, Bitmap bitmap) {
					mHead.setImageBitmap(app.fileHandler.bitmaps
							.get(comment.head));
				}
			});
			mContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (app.data.user.phone.equals(comment.phone)) {
						currentCommentUser = null;
						et_comment.setHint(" 添加评论 ... ...");
						et_comment.setText("");
						Alert.showMessage("不能评论自己");
					} else {
						Friend currentUser = new Friend();
						currentUser.phone = comment.phone;
						currentUser.nickName = comment.nickName;
						currentUser.head = comment.head;
						currentCommentUser = currentUser;
						et_comment.setHint(" 回复:" + comment.nickName);
						// inputMethodManager.showSoftInput(et_comment,
						// InputMethodManager.SHOW_FORCED);
						et_comment.requestFocus();
						inputMethodManager.showSoftInput(et_comment, 0);
						// inputMethodManager.showSoftInputFromInputMethod(
						// SquareMessageDetail.this.getCurrentFocus()
						// .getWindowToken(),
						// InputMethodManager.SHOW_FORCED);
					}
				}
			});
			// mHead.setImageBitmap(app.fileHandler.bitmaps.get(app.data.user.head));
			squareMessageDetailComments.addView(mContent);
		}
	}

	private float dp2px(float dp) {
		float px = getResources().getDisplayMetrics().density * dp + 0.5f;
		return px;
	}

	@SuppressWarnings("deprecation")
	private void showPopWindow(Context context, View parent) {
		vPopWindow = inflater.inflate(R.layout.activity_squaredetail_dialog,
				null, false);
		popWindow = new PopupWindow(vPopWindow, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		vPopWindow.setFocusable(true);
		vPopWindow.setFocusableInTouchMode(true);
		LinearLayout menuContent = (LinearLayout) vPopWindow
				.findViewById(R.id.ll_menuContent);

		LinearLayout rightMenuContent = (LinearLayout) vPopWindow
				.findViewById(R.id.ll_rightMenu);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				menuContent.getLayoutParams());
		params.topMargin = (int) (MainActivity.statusBarHeight + topMenuBar
				.getHeight());
		params.gravity = Gravity.RIGHT;
		params.rightMargin = 7;
		menuContent.setLayoutParams(params);

		TranslateAnimation animation = new TranslateAnimation(
				vPopWindow.getLeft() + dp2px(94), vPopWindow.getLeft(),
				vPopWindow.getTop() - dp2px(146), vPopWindow.getTop());
		animation.setDuration(300);
		rightMenuContent.setAnimation(animation);

		final RelativeLayout stickMenuAll = (RelativeLayout) vPopWindow
				.findViewById(R.id.rl_stick);
		final ImageView stickMenuImage = (ImageView) vPopWindow
				.findViewById(R.id.iv_stick);
		final TextView stickMenu = (TextView) vPopWindow
				.findViewById(R.id.tv_stick);
		boolean messageTypeFlag = message.messageTypes.contains("精华");
		if (messageTypeFlag) {
			stickMenuImage.setImageResource(R.drawable.cancle_stick);
			stickMenu.setText("取消置顶");
		} else {
			stickMenuImage.setImageResource(R.drawable.confirm_stick);
			stickMenu.setText("置顶");
		}
		final RelativeLayout deleteMenuAll = (RelativeLayout) vPopWindow
				.findViewById(R.id.rl_delete);
		// final TextView deleteMenu = (TextView) vPopWindow
		// .findViewById(R.id.tv_delete);

		stickMenuAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stickMenuAll.setBackgroundColor(Color.argb(204, 0, 0, 0));
				if ("置顶".equals(stickMenu.getText().toString())) {
					stickMenuImage.setImageResource(R.drawable.cancle_stick);
					stickMenu.setText("取消置顶");
					addSquareMessageType("精华", true);

				} else {
					stickMenuImage.setImageResource(R.drawable.confirm_stick);
					stickMenu.setText("置顶");
					addSquareMessageType("精华", false);
				}
				if (popWindow != null && popWindow.isShowing()) {
					popWindow.dismiss();
				}
			}
		});
		deleteMenuAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteMenuAll.setBackgroundColor(Color.argb(204, 0, 0, 0));
				deleteSquareMessage();
				if (popWindow != null && popWindow.isShowing()) {
					popWindow.dismiss();
				}
			}
		});
		rightMenuContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		vPopWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popWindow != null && popWindow.isShowing()) {
					popWindow.dismiss();
				}
			}
		});
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void addSquareMessageType(final String messageType,
			final boolean operation) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_MODIFYMESSAGETYPE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentSquareID);
				params.put("gmid", gmid);
				params.put("messageType", messageType);
				params.put("operation", operation + "");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				if (operation) {
					message.messageTypes.add(messageType);
				} else {
					message.messageTypes.remove(messageType);
				}
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						List<String> classify = data.squareMessagesClassify
								.get(mCurrentSquareID).get("精华");
						if (classify == null) {
							List<String> messages = new ArrayList<String>();
							data.squareMessagesClassify.get(mCurrentSquareID)
									.put("精华", messages);
						}
						if (operation) {
							data.squareMessagesClassify.get(mCurrentSquareID)
									.get("精华").add(message.gmid);
						} else {
							data.squareMessagesClassify.get(mCurrentSquareID)
									.get("精华").remove(message.gmid);
						}
					}

					@Override
					public void modifyUI() {
						if (MainActivity.instance.mMainMode.mSquareFragment
								.isAdded()) {
							MainActivity.instance.mMainMode.mSquareFragment
									.notifyViews();
						}
						super.modifyUI();
					}
				});
			}
		});
	}

	public void deleteSquareMessage() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_DELETESQUAREMESSAGE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentSquareID);
				params.put("gmid", gmid);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						List<String> types = message.messageTypes;
						data.squareMessages.get(mCurrentSquareID).remove(gmid);
						for (int i = 0; i < types.size(); i++) {
							String type = types.get(i);
							data.squareMessagesClassify.get(mCurrentSquareID)
									.get(type).remove(gmid);
						}
					}

					@Override
					public void modifyUI() {
						if (MainActivity.instance.mMainMode.mSquareFragment
								.isAdded()) {
							MainActivity.instance.mMainMode.mSquareFragment
									.notifyViews();
						}
						super.modifyUI();
					}
				});

				finish();
			}
		});
	}

	public void getSquareMessageDetail(final String detailPhone,
			final String gmid) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREIDMESSAGE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("detailphone", detailPhone);
				params.put("gid", mCurrentSquareID);
				params.put("gmid", gmid);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				List<SquareMessage> nowMessages = null;
				try {
					nowMessages = JSONParser
							.generateSquareMessagesFromJSON(jData
									.getJSONArray("messages"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (nowMessages != null) {
					final SquareMessage squareMessage = nowMessages.get(0);
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							Map<String, SquareMessage> squareMessageMap = data.squareMessagesMap
									.get(mCurrentSquareID);
							squareMessageMap.put(squareMessage.gmid,
									squareMessage);
							message = squareMessage;
						}

						@Override
						public void modifyUI() {
							// generateMessageContent();
							for (int i = 0; i < squareMessage.praiseusers
									.size(); i++) {
								if (squareMessage.praiseusers.get(i).equals(
										app.data.user.phone)) {
									praiseStatus = true;
									break;
								}
							}
							if (praiseStatus) {
								timeImage.setImageResource(R.drawable.praised);
							} else {
								timeImage.setImageResource(R.drawable.praise);
							}
							timeText.setText("共获得"
									+ squareMessage.praiseusers.size() + "个赞");
						}
					});
				}

			}
		});
	}
}

class TextPanel extends View {

	Paint mPaint;
	String drawText;
	float baseLineHeight;
	float lineSpace;
	boolean singleLine;

	public TextPanel(Context context) {
		super(context);
		mPaint = new Paint();
	}

	public void setTextColor(int color) {
		mPaint.setColor(color);
	}

	public void setText(String text) {
		drawText = text;
		postInvalidate();
	}

	public void setTextSize(float textSize) {
		mPaint.setTextSize(textSize);
		FontMetrics fm = mPaint.getFontMetrics();
		float fFontHeight = fm.descent - fm.ascent;
		baseLineHeight = textSize + textSize - fFontHeight;
		postInvalidate();
	}

	public void setLineSpace(float lineSpace) {
		this.lineSpace = lineSpace;
		postInvalidate();
	}

	public void singleLine(boolean singleLine) {
		this.singleLine = singleLine;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		if (drawText != null) {
			float y = 0;
			y += baseLineHeight;
			if (singleLine) {
				String str = drawText;
				float textWidth = mPaint.measureText(str);
				if (textWidth > width) {
					str = drawText.substring(0, 1);
					int i = 1;
					while (mPaint.measureText(str + "...", 0,
							(str + "...").length()) < width) {
						str = drawText.substring(0, ++i);
					}
					str += "...";
				}
				canvas.drawText(str, 0, y, mPaint);
			} else {
				String[] strLines = autoSplit(drawText, mPaint, width);
				int i = 0;
				float nextY = 0;
				for (String str : strLines) {
					if (str == null) {
						continue;
					}
					if (i < strLines.length) {
						nextY = y + baseLineHeight + lineSpace;
					}
					if (nextY > height) {
						String strLast = str;
						float textWidth = mPaint.measureText(str + "...");
						// TODO can't show "..."
						if (textWidth > width) {
							strLast = str.substring(0, 1);
							int j = 0;
							while (mPaint.measureText(strLast + "...", 0,
									(strLast + "...").length()) < width) {
								strLast = drawText.substring(0, ++j);
							}
							strLast += "...";
						}
						canvas.drawText(strLast, 0, y, mPaint);
						break;
					} else {
						canvas.drawText(str, 0, y, mPaint);
					}
					y = nextY;
					i++;
				}
			}
		}
	}

	private String[] autoSplit(String content, Paint p, float width) {
		// TODO add '\n'
		int length = content.length();
		float textWidth = p.measureText(content);
		if (textWidth <= width) {
			return new String[] { content };
		}

		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width);
		lines += 1;
		String[] lineTexts = new String[lines];
		while (start < length) {
			if (p.measureText(content, start, end) > width) {
				end -= 1;
				lineTexts[i++] = content.substring(start, end);
				start = end;
			}
			if (end == length) {
				lineTexts[i] = (String) content.substring(start, end);
				break;
			}
			end += 1;
		}
		return lineTexts;
	}
}
