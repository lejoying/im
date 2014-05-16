package com.lejoying.wxgs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceInterface;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceSettings;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SquareMessageDetail extends Activity {
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater inflater;

	SquareMessage message;
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
	TextView squareMessageSendUserName;
	LinearLayout squareMessageDetailComments;
	LinearLayout detailContent;

	int SCROLL_TOP = 0X01;
	int SCROLL_BUTTOM = 0X02;
	int SCROLL_BETWEEN = 0X03;
	int scrollStatus = SCROLL_TOP;
	int scrollViewY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mCurrentSquareID = intent.getStringExtra("mCurrentSquareID");
		gmid = intent.getStringExtra("gmid");
		this.message = app.data.squareMessagesMap.get(mCurrentSquareID).get(
				gmid);
		inflater = this.getLayoutInflater();
		players = new ArrayList<MediaPlayer>();
		recordViews = new ArrayList<RecordView>();
		setContentView(R.layout.fragment_square_message_detail);
		sc_square_message_info = (SquareMessageInfoScrollView) findViewById(R.id.sc_square_message_info);
		sc_square_message_info_all = (SquareMessageInfoScrollView) findViewById(R.id.sc_square_message_info_all);
		rl_square_message_menu = (RelativeLayout) findViewById(R.id.rl_square_message_menu);
		squareMessageDetailBack = (ImageView) findViewById(R.id.iv_squareMessageDetailBack);
		squareMessageSendUserName = (TextView) findViewById(R.id.tv_squareMessageSendUserName);
		squareMessageDetailComments = (LinearLayout) findViewById(R.id.ll_squareMessageDetailComments);
		detailContent = (LinearLayout) findViewById(R.id.detailContent);
		addDetailBottomBarChildView();
		squareDetailBottomBar = (RelativeLayout) findViewById(R.id.squareDetailBottomBar);
		sc_square_message_info_all.setOverScrollMode(View.OVER_SCROLL_NEVER);
		sc_square_message_info.setOverScrollMode(View.OVER_SCROLL_NEVER);
		initData();
		generateMessageContent();
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
		List<String> images = message.content.images;
		List<String> voices = message.content.voices;
		String textContent = message.content.text != "" ? message.content.text
				: "";
		squareMessageSendUserName.setText(message.nickName);
		for (int i = 0; i < images.size(); i++) {
			final ImageView imageView = new ImageView(this);
			imageView.setBackgroundColor(Color.RED);

			detailContent.addView(imageView);
			final String fileName = images.get(i);
			app.fileHandler.getImage(fileName, new FileResult() {

				@Override
				public void onResult(String where) {
					Bitmap bitmap = app.fileHandler.bitmaps.get(fileName);
					int height = (int) ((int) bitmap.getHeight() * (width / bitmap
							.getWidth()));
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							(int) width, height);
					imageView.setLayoutParams(params);
					bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width),
							height, true);
					imageView.setImageBitmap(bitmap);

				}
			});

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
			app.fileHandler.saveVoice(new VoiceInterface() {

				@Override
				public void setParams(VoiceSettings settings) {
					settings.fileName = fileName;
				}

				@Override
				public void onSuccess(String filename, String base64,
						Boolean flag) {
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
		LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (sc_square_message_info.getHeight() * 0.917f));
		sc_square_message_info_all.setLayoutParams(paramsInfo);
		LinearLayout.LayoutParams paramsDetailBottomBar = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (sc_square_message_info.getHeight() * 0.083f));
		squareDetailBottomBar.setLayoutParams(paramsDetailBottomBar);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				squareMessageDetailBack.getLayoutParams());
		params.width = (int) (height * 0.046f);
		params.leftMargin = (int) (width * 0.051302298f);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		squareMessageDetailBack.setLayoutParams(params);

		initSquareDetailBottomBar((int) (sc_square_message_info.getHeight() * 0.083f));

		super.onWindowFocusChanged(hasFocus);
	}

	ImageView timeImage;
	TextPanel timeText;
	ImageView distanceImage;
	TextPanel distanceText;
	ImageView commentImage;
	TextPanel commentText;
	ImageView praiseImage;
	TextPanel praiseText;
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
		timeImage.setImageResource(R.drawable.time);
		timeText = new TextPanel(this);
		timeText.singleLine(true);
		timeText.setTextColor(Color.WHITE);
		timeText.setText(convertTime(System.currentTimeMillis(), message.time));
		distanceImage = new ImageView(this);
		distanceImage.setImageResource(R.drawable.distance);
		distanceText = new TextPanel(this);
		distanceText.singleLine(true);
		distanceText.setTextColor(Color.WHITE);
		distanceText.setText("000m");
		commentImage = new ImageView(this);
		commentImage.setImageResource(R.drawable.comment);
		commentText = new TextPanel(this);
		commentText.singleLine(true);
		commentText.setTextColor(Color.WHITE);
		commentText.setText("000");
		praiseImage = new ImageView(this);
		for (int i = 0; i < message.praiseusers.size(); i++) {
			if (message.praiseusers.get(i).equals(app.data.user.phone)) {
				praiseStatus = true;
				break;
			}
		}
		if (praiseStatus) {
			praiseImage.setImageResource(R.drawable.praised);
		} else {
			praiseImage.setImageResource(R.drawable.praise);
		}
		praiseText = new TextPanel(this);
		praiseText.singleLine(true);
		praiseText.setTextColor(Color.WHITE);
		praiseText.setText(message.praiseusers.size() + "");
		collectImage = new ImageView(this);
		collectImage.setImageResource(R.drawable.collect);
		collectText = new TextPanel(this);
		collectText.singleLine(true);
		collectText.setTextColor(Color.WHITE);
		collectText.setText("000");

		rl_square_message_menu.addView(timeImage);
		rl_square_message_menu.addView(timeText);
		rl_square_message_menu.addView(distanceImage);
		rl_square_message_menu.addView(distanceText);
		rl_square_message_menu.addView(commentImage);
		rl_square_message_menu.addView(commentText);
		rl_square_message_menu.addView(praiseImage);
		rl_square_message_menu.addView(praiseText);
		rl_square_message_menu.addView(collectImage);
		rl_square_message_menu.addView(collectText);

	}

	private void initSquareDetailBottomBar(int height) {
		int width = rl_square_message_menu.getWidth();
		// int height = rl_square_message_menu.getHeight();
		System.out.println(width + "::::" + height + ">>>>"
				+ squareDetailBottomBar.getHeight());
		int side = (int) (height * 0.35185f);
		int top = (height - side) / 2;
		float textSize = height * 0.234286f;
		int textTop = (int) ((height - textSize) / 2);
		int textLeftA = (int) (width * 0.01651917f);
		timeImage.setLayoutParams(generateLayoutParams(side, side,
				(int) (width * 0.03488f), top));

		timeText.setTextSize(textSize);
		timeText.setLayoutParams(generateLayoutParams((int) ((width * 0.2722f)
				- (width * 0.03488f) - side), side, (int) (width * 0.03488f)
				+ side + textLeftA, textTop));

		distanceImage.setLayoutParams(generateLayoutParams(side, side,
				(int) (width * 0.2722f), top));
		distanceText.setTextSize(textSize);
		distanceText.setLayoutParams(generateLayoutParams(
				(int) ((width * 0.55917f) - (width * 0.2722f) - side), side,
				(int) (width * 0.2722f) + side + textLeftA, textTop));
		commentImage.setLayoutParams(generateLayoutParams(side, side,
				(int) (width * 0.55917f), top));
		commentText.setTextSize(textSize);
		commentText.setLayoutParams(generateLayoutParams(
				(int) ((width * 0.68935f) - (width * 0.55917f) - side), side,
				(int) (width * 0.55917f) + side + textLeftA, textTop));
		praiseImage.setLayoutParams(generateLayoutParams(side, side,
				(int) (width * 0.68935f), top));
		praiseText.setTextSize(textSize);
		praiseText.setLayoutParams(generateLayoutParams(
				(int) ((width * 0.8284f) - (width * 0.68935f) - side), side,
				(int) (width * 0.68935f) + side + textLeftA, textTop));
		collectImage.setLayoutParams(generateLayoutParams(side, side,
				(int) (width * 0.8284f), top));
		collectText.setTextSize(textSize);
		collectText.setLayoutParams(generateLayoutParams((int) (width * 0.96f
				- (width * 0.8284f) - side), side, (int) (width * 0.8284f)
				+ side + textLeftA, textTop));

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

	private void initEvent() {
		praiseImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (praiseStatus) {
					praiseImage.setImageResource(R.drawable.praise);
					for (int i = 0; i < message.praiseusers.size(); i++) {
						if (message.praiseusers.get(i).equals(
								app.data.user.phone)) {
							message.praiseusers.remove(i);
							break;
						}
					}
					praiseText.setText(message.praiseusers.size() + "");
					praiseStatus = false;
					praiseSquareMessage(false);
				} else {
					praiseImage.setImageResource(R.drawable.praised);
					message.praiseusers.add(app.data.user.phone);
					praiseText.setText(message.praiseusers.size() + "");
					praiseStatus = true;
					praiseSquareMessage(true);
				}
			}
		});
		squareMessageDetailBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
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
				// try {
				// // String notice = jData.getString("提示信息");
				// // Log.e("Coolspan", notice + "-" + flag + "------点赞提示消息");
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
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
					final List<Comment> comments = JSONParser
							.generateCommentsFromJSON(jData
									.getJSONArray("comments"));
					// if (comments.size() > 0) {
					// TODO
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							generateCommentsViews(comments);
						}
					});
					// }

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
				// try {
				// final List<Comment> comments = JSONParser
				// .generateCommentsFromJSON(jData
				// .getJSONArray("comments"));
				// if (comments.size()) {
				// TODO
				// app.UIHandler.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// // generateCommentsViews(comments);
				// }
				// });
				// }

				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
			}
		});
	}

	public void generateCommentsViews(List<Comment> comments) {
		for (int i = 0; i < Long.valueOf(gmid) % 3; i++) {
			View mContent = inflater.inflate(
					R.layout.fragment_square_message_comments, null);
			// ImageView mHead = (ImageView)
			// mContent.findViewById(R.id.iv_head);
			// mHead.setImageBitmap(app.fileHandler.bitmaps.get(app.data.user.head));
			squareMessageDetailComments.addView(mContent);
		}

	}

	private float dp2px(float dp) {
		float px = getResources().getDisplayMetrics().density * dp + 0.5f;
		return px;
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
