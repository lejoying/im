package com.lejoying.wxgs.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.ReleaseActivity.MyGridAdapter;
import com.lejoying.wxgs.activity.ReleaseActivity.MyPageAdapter;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.utils.TimeUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.data.entity.GroupShare.VoiceContent;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.parser.JSONParser;

public class DetailsActivity extends Activity implements OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	InputMethodManager inputMethodManager;
	Intent intent;
	LayoutInflater inflater;
	Handler handler;

	View release_iv_face_left, release_iv_face_right, release_iv_face_delete;
	LinearLayout ll_message_info, ll_detailContent, ll_praise, ll_praiseMember,
			ll_messageDetailComments, ll_facemenu;
	RelativeLayout rl_sendComment, backView, rl_comment, rl_face;
	TextView tv_praiseNum, tv_checkComment, tv_sendComment,
			tv_squareMessageSendUserName, tv_messageTime;
	ImageView iv_addPraise, iv_checkComment, iv_comment,
			iv_squareMessageDetailBack, iv_messageUserHead;
	ScrollView sv_message_info;
	HorizontalScrollView horizontalScrollView;
	ViewPager chat_vPager;
	EditText et_comment;

	LayoutParams commentLayoutParams;

	GroupShare share;

	float height, width, dip, density;

	int initialHeight, headWidth, chat_vPager_now;

	String nickNameTo, phoneTo;
	String faceRegx = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";

	boolean praiseStatus = false;

	List<String[]> faceNamesList;
	List<List<String>> faceNameList;
	List<ImageView> faceMenuShowList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initLayout();
		initEvent();
		initData();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		initialHeight = et_comment.getHeight();
		rl_comment.setVisibility(View.GONE);
		super.onWindowFocusChanged(hasFocus);
	}

	private void initEvent() {
		iv_addPraise.setOnClickListener(this);
		ll_praise.setOnClickListener(this);
		iv_checkComment.setOnClickListener(this);
		iv_comment.setOnClickListener(this);
		tv_sendComment.setOnClickListener(this);
		release_iv_face_left.setOnClickListener(this);
		release_iv_face_right.setOnClickListener(this);
		release_iv_face_delete.setOnClickListener(this);

		et_comment.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rl_face.setVisibility(View.GONE);
				}
			}
		});
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
			String content = "";

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				content = s.toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				commentLayoutParams = rl_comment.getLayoutParams();
				commentLayoutParams.height = (int) ((45 * density + 0.5f)
						+ et_comment.getHeight() - initialHeight);
				rl_comment.setLayoutParams(commentLayoutParams);
			}

			@Override
			public void afterTextChanged(Editable s) {
				int selectionIndex = et_comment.getSelectionStart();
				if (!(s.toString()).equals(content)) {
					SpannableString spannableString = ExpressionUtil
							.getExpressionString(getBaseContext(),
									s.toString(), faceRegx, expressionFaceMap);
					et_comment.setText(spannableString);
					et_comment.setSelection(selectionIndex);
				}
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
		chat_vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				chat_vPager_now = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

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
		rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
		backView = (RelativeLayout) findViewById(R.id.backview);
		sv_message_info = (ScrollView) findViewById(R.id.sv_message_info);
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

		ll_facemenu = (LinearLayout) findViewById(R.id.release_ll_facemenu);
		rl_face = (RelativeLayout) findViewById(R.id.release_rl_face);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		chat_vPager = (ViewPager) findViewById(R.id.release_chat_vPager);
		release_iv_face_left = findViewById(R.id.release_iv_face_left);
		release_iv_face_right = findViewById(R.id.release_iv_face_right);
		release_iv_face_delete = findViewById(R.id.release_iv_face_delete);
	}

	private void initData() {
		inflater = getLayoutInflater();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		handler = new Handler();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		intent = getIntent();
		share = (GroupShare) intent.getSerializableExtra("content");
		nickNameTo = "";
		phoneTo = "";
		chat_vPager_now = 0;
		final List<String> images = share.content.images;
		List<VoiceContent> voices = share.content.voices;
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
		for (VoiceContent str : voices) {

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
		initFace();
		resetPraises();
		resetComments();
	}

	private void initFace() {
		faceMenuShowList = new ArrayList<ImageView>();
		faceNamesList = new ArrayList<String[]>();
		faceNamesList = MainModeManager.faceNamesList;
		List<View> mListViews = new ArrayList<View>();
		List<String> images1 = new ArrayList<String>();
		for (int i = 0; i < 105; i++) {
			expressionFaceMap.put(faceNamesList.get(0)[i], "smiley_" + i
					+ ".png");
			images1.add("smiley_" + i + ".png");
		}
		List<String> images2 = new ArrayList<String>();
		for (int i = 0; i < 77; i++) {
			expressionFaceMap.put(faceNamesList.get(1)[i], "emoji_" + i
					+ ".png");
			images2.add("emoji_" + i + ".png");
		}
		faceNameList = new ArrayList<List<String>>();
		faceNameList.add(images1);
		faceNameList.add(images2);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,
				LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		for (int i = 0; i < 2; i++) {
			try {
				ImageView iv = new ImageView(this);
				iv.setImageBitmap(BitmapFactory.decodeStream(this.getAssets()
						.open("images/" + faceNameList.get(i).get(0))));
				iv.setLayoutParams(lp);
				iv.setTag(i);
				ll_facemenu.addView(iv);
				faceMenuShowList.add(iv);
				ImageView iv_1 = new ImageView(this);
				iv_1.setBackgroundColor(Color.WHITE);
				iv_1.setMinimumWidth(1);
				iv_1.setMinimumHeight(80);
				iv_1.setMaxWidth(1);
				ll_facemenu.addView(iv_1);
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag();
						chat_vPager_now = position;
						chat_vPager.setCurrentItem(chat_vPager_now);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			View v = inflater.inflate(R.layout.f_chat_face_base_gridview, null);
			GridView chat_base_gv = (GridView) v
					.findViewById(R.id.chat_base_gv);
			chat_base_gv.setAdapter(new MyGridAdapter(faceNameList.get(i)));
			mListViews.add(chat_base_gv);
		}
		chat_vPager.setAdapter(new MyPageAdapter(mListViews));
	}

	private void resetPraises() {
		if (praiseStatus) {
			iv_addPraise.setImageResource(R.drawable.gshare_praised);
		} else {
			iv_addPraise.setImageResource(R.drawable.gshare_praise);
		}
		ll_praiseMember.post(new Runnable() {
			@Override
			public void run() {
				headWidth = ll_praiseMember.getWidth() / 5;
				int padding = (int) (5 * density + 0.5f);
				List<String> praiseusers = share.praiseusers;
				tv_praiseNum.setText("共获得" + praiseusers.size() + "个赞");
				ll_praiseMember.removeAllViews();
				ImageView iv = new ImageView(DetailsActivity.this);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						0, LayoutParams.WRAP_CONTENT);
				param.weight = 1;
				iv.setLayoutParams(param);
				ll_praiseMember.addView(iv);
				for (int i = 0; i < praiseusers.size(); i++) {
					final ImageView view = new ImageView(DetailsActivity.this);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							headWidth, headWidth);
					params.gravity = Gravity.CENTER;
					view.setPadding(padding, padding, padding, padding);
					view.setLayoutParams(params);
					app.fileHandler.getHeadImage(
							app.data.groupFriends.get(praiseusers.get(i)).head,
							app.data.groupFriends.get(praiseusers.get(i)).sex,
							new FileResult() {

								@Override
								public void onResult(String where, Bitmap bitmap) {
									view.setImageBitmap(bitmap);
								}
							});
					ll_praiseMember.addView(view);
					if (i == 5)
						break;
				}
			}
		});
	}

	private void resetComments() {
		List<Comment> comments = share.comments;
		iv_checkComment.setImageResource(R.drawable.gshare_comment);
		for (Comment comment : comments) {
			if (comment.phone.equals(app.data.user.phone)) {
				iv_checkComment.setImageResource(R.drawable.gshare_commented);
				break;
			}
		}
		tv_checkComment.setText("查看全部" + comments.size() + "条评论...");
		ll_messageDetailComments.removeAllViews();
		for (final Comment comment : comments) {
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
			SpannableString spannableString = ExpressionUtil
					.getExpressionString(getBaseContext(), comment.content,
							faceRegx, expressionFaceMap);
			content.setText(spannableString);
			time.setText(TimeUtils.getTime(comment.time));
			receive.setText(comment.nickName); 
			received.setText(comment.nickNameTo);

			if ("".equals(comment.nickNameTo)) {
				reply.setVisibility(View.GONE);
				received.setVisibility(View.GONE);
			}
			app.fileHandler.getHeadImage(comment.head, "男", new FileResult() {
				@Override
				public void onResult(String where, Bitmap bitmap) {
					head.setImageBitmap(bitmap);
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (rl_comment.getVisibility() == View.GONE) {
						rl_comment.setVisibility(View.VISIBLE);
					}
					if (!comment.phone.equals(app.data.user.phone)) {
						phoneTo = comment.phone;
						nickNameTo = comment.nickName;
						et_comment.setHint("回复" + nickNameTo);
					} else {
						phoneTo = "";
						nickNameTo = "";
						et_comment.setHint("添加评论 ... ...");
					}

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
			if (!"".equals(phoneTo)) {
				et_comment.setText("");
				et_comment.setHint("添加评论 ... ...");
				phoneTo = "";
				nickNameTo = "";
			} else {
				if (rl_comment.getVisibility() == View.VISIBLE) {
					et_comment.setText("");
					et_comment.setHint("添加评论 ... ...");
					commentLayoutParams = rl_comment.getLayoutParams();
					commentLayoutParams.height = (int) (45 * density + 0.5f);
					rl_comment.setLayoutParams(commentLayoutParams);
					rl_comment.setVisibility(View.GONE);
				} else {
					phoneTo = "";
					nickNameTo = "";
					rl_comment.setVisibility(View.VISIBLE);
				}
			}
			break;
		case R.id.iv_comment:
			if (rl_face.getVisibility() == View.VISIBLE) {
				rl_face.setVisibility(View.GONE);
			} else {
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(
							DetailsActivity.this.getCurrentFocus()
									.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				rl_face.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.et_comment:
			rl_face.setVisibility(View.GONE);

			break;
		case R.id.tv_sendComment:
			sendComment();
			break;
		case R.id.release_iv_face_left:
			int start1 = et_comment.getSelectionStart();
			String content1 = et_comment.getText().toString();
			if (start1 - 1 < 0)
				return;
			String faceEnd1 = content1.substring(start1 - 1, start1);
			if ("]".equals(faceEnd1) || ">".equals(faceEnd1)) {
				String str = content1.substring(0, start1);
				int index = "]".equals(faceEnd1) ? str.lastIndexOf("[") : str
						.lastIndexOf("<");
				if (index != -1) {
					String faceStr = content1.substring(index, start1);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_comment.setSelection(start1 - faceStr.length());
					} else {
						if (start1 - 1 >= 0) {
							et_comment.setSelection(start1 - 1);
						}
					}
				}
			} else {
				if (start1 - 1 >= 0) {
					et_comment.setSelection(start1 - 1);
				}
			}

			break;
		case R.id.release_iv_face_right:

			int start2 = et_comment.getSelectionStart();
			String content2 = et_comment.getText().toString();
			if (start2 + 1 > content2.length())
				return;
			String faceEnd2 = content2.substring(start2, start2 + 1);
			if ("[".equals(faceEnd2) || "<".equals(faceEnd2)) {
				String str = content2.substring(start2);
				int index = "[".equals(faceEnd2) ? str.indexOf("]") : str
						.indexOf(">");
				if (index != -1) {
					String faceStr = content2.substring(start2, index + start2
							+ 1);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_comment.setSelection(start2 + faceStr.length());
					} else {
						if (start2 + 1 <= content2.length()) {
							et_comment.setSelection(start2 + 1);
						}
					}
				}
			} else {
				if (start2 + 1 <= content2.length()) {
					et_comment.setSelection(start2 + 1);
				}
			}

			break;
		case R.id.release_iv_face_delete:

			int start = et_comment.getSelectionStart();
			String content = et_comment.getText().toString();
			if (start - 1 < 0)
				return;
			String faceEnd = content.substring(start - 1, start);
			if ("]".equals(faceEnd) || ">".equals(faceEnd)) {
				String str = content.substring(0, start);
				int index = "]".equals(faceEnd) ? str.lastIndexOf("[") : str
						.lastIndexOf("<");
				if (index != -1) {
					String faceStr = content.substring(index, start);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_comment.setText(content.substring(0,
								start - faceStr.length())
								+ content.substring(start));
						et_comment.setSelection(start - faceStr.length());
					} else {
						if (start - 1 >= 0) {
							et_comment.setText(content.substring(0, start - 1)
									+ content.substring(start));
							et_comment.setSelection(start - 1);
						}
					}
				}
			} else {
				if (start - 1 >= 0) {
					et_comment.setText(content.substring(0, start - 1)
							+ content.substring(start));
					et_comment.setSelection(start - 1);
				}
			}

			break;
		default:
			break;
		}

	}

	private void addPraise(final boolean flag) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			public void success(JSONObject jData) {
				praiseStatus = !praiseStatus;
				modifyShare();
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
		final long time = new Date().getTime();
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_ADDCOMMENT;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("phoneTo", share.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("nickName", app.data.user.nickName);
				params.put("nickNameTo", nickNameTo);
				params.put("head", app.data.user.head);
				params.put("gid", app.data.currentGroup);
				params.put("gsid", share.gsid);
				params.put("time", String.valueOf(time));
				params.put("contentType", "text");
				params.put("content", commentContent);
				settings.params = params;

			}

			@Override
			public void success(JSONObject jData) {
				modifyShare();
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
					share = newShare;
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.groupsMap
									.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
									.put(newShare.gsid, newShare);
							phoneTo = "";
							nickNameTo = "";
						}

						@Override
						public void modifyUI() {
							et_comment.setText("");
							et_comment.setHint("添加评论 ... ...");
							resetPraises();
							resetComments();
							handler.post(new Runnable() {
								@Override
								public void run() {
									sv_message_info
											.fullScroll(ScrollView.FOCUS_DOWN);
								}
							});
						}
					});
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

	class MyGridAdapter extends BaseAdapter {
		List<String> list;

		public MyGridAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = inflater.inflate(R.layout.f_chat_base_gridview_item,
					null);
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.chat_base_iv);
			try {
				iv.setImageBitmap(BitmapFactory.decodeStream(getBaseContext()
						.getAssets().open("images/" + list.get(position))));
			} catch (IOException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					et_comment.getText().insert(et_comment.getSelectionStart(),
							faceNamesList.get(chat_vPager_now)[position]);
				}
			});
			return convertView;
		}
	}

	class MyPageAdapter extends PagerAdapter {
		List<View> mListViews;

		public MyPageAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return (view == obj);
		}

		@Override
		public Object instantiateItem(View view, int position) {
			try {
				if (mListViews.get(position).getParent() == null)
					((ViewPager) view).addView(mListViews.get(position), 0);
				else {
					((ViewGroup) mListViews.get(position).getParent())
							.removeView(mListViews.get(position));
					((ViewPager) view).addView(mListViews.get(position), 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mListViews.get(position);
		}

		@Override
		public void destroyItem(View view, int position, Object obj) {
			((ViewPager) view).removeView(mListViews.get(position));
		}
	}
}
