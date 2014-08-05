package com.lejoying.wxgs.activity;

import java.io.File;
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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupSharePraisesFragment;
import com.lejoying.wxgs.activity.mode.fragment.ModifyFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.utils.TimeUtils;
import com.lejoying.wxgs.activity.view.InnerScrollView;
import com.lejoying.wxgs.activity.view.InnerScrollView.OnScrollChangedListener;
import com.lejoying.wxgs.activity.view.RecoderVoiceView;
import com.lejoying.wxgs.activity.view.RecoderVoiceView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecoderVoiceView.ProgressListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.data.entity.GroupShare.VoiceContent;
import com.lejoying.wxgs.app.data.entity.GroupShare.VoteContent;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileSettings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class DetailsActivity extends AbsListViewBaseActivity implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	InputMethodManager inputMethodManager;
	FragmentManager mFragmentManager;
	Intent intent;
	LayoutInflater inflater;
	Handler handler;
	GroupSharePraisesFragment mGroupSharePraisesFragment;

	MediaPlayer player;

	View release_iv_face_left, release_iv_face_right, release_iv_face_delete;
	LinearLayout ll_message_info, ll_detailContent, ll_praise, ll_praiseMember,
			ll_messageDetailComments, ll_facemenu;
	RelativeLayout rl_sendComment, backView, rl_comment, rl_face;
	TextView tv_praiseNum, tv_checkComment, tv_sendComment,
			tv_squareMessageSendUserName, tv_messageTime;
	ImageView iv_addPraise, iv_checkComment, iv_comment,
			iv_squareMessageDetailBack, iv_messageUserHead;
	ScrollView sv_message_info;
	InnerScrollView insv_message_info;
	HorizontalScrollView horizontalScrollView;
	ViewPager chat_vPager;
	EditText et_comment;
	TextView voteTv;
	LayoutParams commentLayoutParams;

	GroupShare share;

	float height, width, dip, density;

	int initialHeight, chat_vPager_now, selected;

	String nickNameTo, phoneTo, inputContent;
	String faceRegx = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";

	boolean praiseStatus = false, playing = false, voted = false;

	List<String[]> faceNamesList;
	List<List<String>> faceNameList;
	List<ImageView> faceMenuShowList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();

	String gsid = "";

	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		intent = getIntent();
		gsid = intent.getStringExtra("gsid");
		share = app.data.groupsMap.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
				.get(gsid);
		initLayout();
		if (share == null) {
			getGroupShares();
			return;
		}
		initEvent();
		initData();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		initialHeight = et_comment.getHeight();
		rl_comment.setVisibility(View.GONE);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onBackPressed() {
		if (mGroupSharePraisesFragment.isAdded()) {
			mFragmentManager.popBackStack();
		} else {
			mFinish();
		}
	}

	private void mFinish() {
		if (playing) {
			if (player != null) {
				player.release();
			}
		}
		finish();
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

		insv_message_info
				.setOnScrollChangedListener(new OnScrollChangedListener() {

					@Override
					public void onScrollChangedListener(int w, int h, int oldw,
							int oldh) {
						if (rl_comment.getVisibility() == View.VISIBLE) {
							inputContent = et_comment.getText().toString();
							rl_comment.setVisibility(View.GONE);
							if (inputMethodManager.isActive()) {
								if (rl_comment.getWindowToken() != null) {
									inputMethodManager.hideSoftInputFromWindow(
											rl_comment.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}
							if (rl_face.getVisibility() == View.VISIBLE) {
								rl_face.setVisibility(View.GONE);
							}
						}
					}
				});

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
							mFinish();
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
		insv_message_info = (InnerScrollView) findViewById(R.id.insv_message_info);
		insv_message_info.parentScrollView = sv_message_info;
		sv_message_info.setOverScrollMode(View.OVER_SCROLL_NEVER);
		insv_message_info.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		inflater = getLayoutInflater();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		handler = new Handler();
		mFragmentManager = this.getSupportFragmentManager();
		mGroupSharePraisesFragment = new GroupSharePraisesFragment();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		LayoutParams insvparams = insv_message_info.getLayoutParams();
		insvparams.height = (int) (height - MainActivity.statusBarHeight - dp2px(150));
		nickNameTo = "";
		phoneTo = "";
		chat_vPager_now = 0;

		final List<String> images = share.content.images;
		List<VoiceContent> voices = share.content.voices;
		List<VoteContent> voteoptions = share.content.voteoptions;
		String voteTitle = share.content.title;
		String textContent = share.content.text;
		tv_squareMessageSendUserName.setText(app.data.groupFriends
				.get(share.phone).nickName);
		tv_messageTime.setText(TimeUtils.getTime(share.time));
		for (String str : share.praiseusers) {
			if (str.equals(app.data.user.phone)) {
				praiseStatus = true;
				break;
			}
		}
		if (!"".equals(voteTitle)) {
			TextView titleText = new TextView(DetailsActivity.this);
			titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			titleText.setTextColor(Color.GRAY);
			titleText.setBackgroundColor(Color.WHITE);
			titleText.setText("投票主题:" + voteTitle);
			titleText.setGravity(Gravity.CENTER_VERTICAL);
			titleText.setPadding(dp2px(10), 0, 0, 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, dp2px(50));
			params.gravity = Gravity.CENTER;
			params.setMargins(dp2px(10), dp2px(45), dp2px(10), 0);
			ll_detailContent.addView(titleText, params);
			for (int i = 0; i < voteoptions.size(); i++) {
				initVoteoption(voteoptions.get(i), i + 1);
			}
			voteTv = new TextView(this);
			if (voted) {
				voteTv.setBackgroundResource(R.drawable.gshare_voted_bt);
				voteTv.setText("已投票");
			} else {
				voteTv.setText("投票");
				voteTv.setBackgroundResource(R.drawable.gshare_vote_bt);
			}
			voteTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			voteTv.setTextColor(Color.WHITE);
			voteTv.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams btParams = new LinearLayout.LayoutParams(
					dp2px(146), dp2px(37));
			btParams.gravity = Gravity.CENTER;
			btParams.setMargins(0, dp2px(30), 0, dp2px(35));
			voteTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!voted) {
						modifyVoteCount(
								GroupShareFragment.mCurrentGroupShareID,
								share.gsid, selected, true);
					}

				}
			});
			ll_detailContent.addView(voteTv, btParams);
		}
		for (int i = 0; i < images.size(); i++) {
			final int index = i;
			final ImageView imageView = new ImageView(this);
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// (int) width, Integer.MAX_VALUE);
			// imageView.setLayoutParams(params);
			ll_detailContent.addView(imageView);

			imageLoader.displayImage(API.DOMAIN_COMMONIMAGE + "images/"
					+ images.get(i), imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							int height = (int) (loadedImage.getHeight() * (width / loadedImage
									.getWidth()));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									(int) width, height);
							imageView.setLayoutParams(params);
						}
					});

			// app.fileHandler.getSquareDetailImage(images.get(i), (int) width,
			// new FileResult() {
			//
			// @Override
			// public void onResult(String where, Bitmap bitmap) {
			// int height = (int) (bitmap.getHeight() * (width / bitmap
			// .getWidth()));
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// (int) width, height);
			// imageView.setLayoutParams(params);
			// imageView.setImageBitmap(bitmap);
			// imageView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// Intent intent = new Intent(
			// DetailsActivity.this,
			// PicAndVoiceDetailActivity.class);
			// intent.putExtra("currentIndex", index);
			// intent.putExtra("Activity", "Browse");
			// intent.putStringArrayListExtra("content",
			// (ArrayList<String>) images);
			// startActivity(intent);
			// }
			// });
			// }
			// });
		}
		for (final VoiceContent voiceContent : voices) {
			final RecoderVoiceView recoderVoiceView = new RecoderVoiceView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					(int) width, dp2px(300));
			params.gravity = Gravity.CENTER;
			recoderVoiceView.setLayoutParams(params);
			TextView tv = new TextView(this);
			ll_detailContent.addView(tv, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, dp2px(50)));
			ll_detailContent.addView(recoderVoiceView);
			app.fileHandler.getFile(new FileInterface() {
				@Override
				public void setParams(FileSettings settings) {
					settings.directory = "voices";
					settings.fileName = voiceContent.fileName;
					settings.folder = app.sdcardVoiceFolder;
				}

				@Override
				public void onSuccess(Boolean flag, String fileName) {
					initVoice(fileName, recoderVoiceView);
				}

			});
		}

		if (!"".equals(textContent)) {
			TextView textview = new TextView(this);
			textview.setTextColor(Color.WHITE);
			textview.setBackgroundColor(Color.parseColor("#26ffffff"));
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			int padding = dp2px(10);
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

	private void initVoteoption(VoteContent option, final int location) {
		String users = "";
		View view = inflater.inflate(R.layout.fragment_groupshare_vote_item,
				null);
		RelativeLayout rl_voteOptionContent = (RelativeLayout) view
				.findViewById(R.id.rl_voteOptionContent);
		RelativeLayout rl_operationVote = (RelativeLayout) view
				.findViewById(R.id.rl_operationVote);
		TextView tv_voteOptionNumPlan = (TextView) view
				.findViewById(R.id.tv_voteOptionNumPlan);
		TextView tv_voteOptionContent = (TextView) view
				.findViewById(R.id.tv_voteOptionContent);
		TextView tv_voteOptionNumber = (TextView) view
				.findViewById(R.id.tv_voteOptionNumber);
		TextView tv_operationVote = (TextView) view
				.findViewById(R.id.tv_operationVote);
		TextView tv_voteUsers = (TextView) view.findViewById(R.id.tv_voteUsers);

		rl_operationVote.setVisibility(View.VISIBLE);
		tv_voteOptionNumPlan.setVisibility(View.GONE);
		tv_voteUsers.setVisibility(View.VISIBLE);

		rl_voteOptionContent.setBackgroundColor(Color.parseColor("#26ffffff"));
		rl_operationVote.setBackgroundColor(Color.parseColor("#4dffffff"));

		if (!voted) {
			if (option.voteUsers.contains(app.data.user.phone)) {
				voted = true;
				rl_voteOptionContent.setBackgroundColor(Color
						.parseColor("#2613b6ed"));
				rl_operationVote.setBackgroundColor(Color
						.parseColor("#4d13b6ed"));
				tv_operationVote.setText("");
				tv_operationVote.setBackgroundResource(R.drawable.voted);
				tv_operationVote.getLayoutParams().width = dp2px(20);
				tv_operationVote.getLayoutParams().height = dp2px(15);
			}
		}

		tv_voteOptionContent.setText(option.content);
		tv_voteOptionNumber.setText(option.voteUsers.size() + "票");

		for (int i = 0; i < option.voteUsers.size(); i++) {
			if (i == option.voteUsers.size() - 1) {
				users += app.data.groupFriends.get(option.voteUsers.get(i)).nickName;
			} else {
				users += app.data.groupFriends.get(option.voteUsers.get(i)).nickName
						+ "、";
			}
		}
		tv_voteUsers.setSingleLine();
		tv_voteUsers.setText(users);

		RelativeLayout.LayoutParams tvParams = (android.widget.RelativeLayout.LayoutParams) tv_voteOptionNumber
				.getLayoutParams();
		tvParams.rightMargin = dp2px(65);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) width - dp2px(20), LayoutParams.WRAP_CONTENT);// dp2px(45)
		// params.setMargins(dp2px(10), 0, dp2px(10), 0);
		params.gravity = Gravity.CENTER;

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!voted) {
					for (int i = 1; i < ll_detailContent.getChildCount() - 1; i++) {
						if (i != location) {
							ll_detailContent
									.getChildAt(i)
									.findViewById(R.id.rl_voteOptionContent)
									.setBackgroundColor(
											Color.parseColor("#26ffffff"));
							ll_detailContent
									.getChildAt(i)
									.findViewById(R.id.rl_operationVote)
									.setBackgroundColor(
											Color.parseColor("#4dffffff"));
						} else {
							ll_detailContent
									.getChildAt(i)
									.findViewById(R.id.rl_voteOptionContent)
									.setBackgroundColor(
											Color.parseColor("#2613b6ed"));
							ll_detailContent
									.getChildAt(i)
									.findViewById(R.id.rl_operationVote)
									.setBackgroundColor(
											Color.parseColor("#4d13b6ed"));
							selected = i;
							voteTv.setBackgroundResource(R.drawable.gshare_voted_bt);
						}
					}

				}
			}
		});

		ll_detailContent.addView(view, params);
	}

	private void initVoice(String fileName,
			final RecoderVoiceView recoderVoiceView) {

		player = MediaPlayer.create(DetailsActivity.this, Uri.parse((new File(
				app.sdcardVoiceFolder, fileName)).getAbsolutePath()));
		recoderVoiceView.setMode(RecoderVoiceView.MODE_PROGRESS);
		recoderVoiceView.setShowDelete(false);
		recoderVoiceView.setCenterColor();
		recoderVoiceView.setProgressTime(player.getDuration());

		try {
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recoderVoiceView
				.setPlayButtonClickListener(new PlayButtonClickListener() {

					@Override
					public void onPlay() {
						recoderVoiceView.startProgress();
						player.start();
						playing = true;
					}

					@Override
					public void onPause() {
						recoderVoiceView.pauseProgress();
						if (player != null) {
							player.pause();
						}

					}
				});
		recoderVoiceView.setProgressListener(new ProgressListener() {

			@Override
			public void onProgressEnd() {
				recoderVoiceView.stopProgress();
				playing = false;
			}

			@Override
			public void onDrag(float percent) {
				if (player != null) {
					player.seekTo((int) (player.getDuration() * percent));
				}

			}
		});

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
				int headWidth = ll_praiseMember.getWidth() / 5 - 5;
				int padding = dp2px(5);
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
							headWidth, LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					view.setPadding(padding, 0, padding, 0);
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
			params.setMargins(dp2px(10), 0, dp2px(10), 0);
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

	void modifyVoteCount(final String gid, final String gsid, final int vid,
			final boolean operation) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_MODIFYVOTE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", gid);
				params.put("gsid", gsid);
				params.put("vid", "" + (vid - 1));
				params.put("operation", operation + "");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				voted = true;
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						GroupShare newShare = data.groupsMap
								.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
								.get(share.gsid);
						newShare.content.voteoptions.get(selected - 1).voteUsers
								.add(data.user.phone);
					}

					@Override
					public void modifyUI() {
						TextView view = (TextView) ll_detailContent.getChildAt(
								selected).findViewById(R.id.tv_voteUsers);
						view.setText(("".equals(view.getText().toString()) ? ""
								: view.getText().toString() + "、")
								+ app.data.user.nickName);
						voteTv.setText("已投票");
						TextView num = (TextView) (ll_detailContent
								.getChildAt(selected)
								.findViewById(R.id.tv_voteOptionNumber));
						num.setText(app.data.groupsMap
								.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
								.get(share.gsid).content.voteoptions
								.get(selected - 1).voteUsers.size()
								+ "票");
						TextView voted = (TextView) (ll_detailContent
								.getChildAt(selected)
								.findViewById(R.id.tv_operationVote));
						voted.setText("");
						voted.setBackgroundResource(R.drawable.voted);
						voted.getLayoutParams().width = dp2px(20);
						voted.getLayoutParams().height = dp2px(15);
					}
				});
			}
		});
	}

	private int dp2px(int dp) {
		return (int) (dp * density + 0.5f);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_addPraise:
			addPraise(!praiseStatus);
			break;
		case R.id.ll_praise:
			GroupSharePraisesFragment.praiseUsers = share.praiseusers;
			FragmentTransaction transaction = mFragmentManager
					.beginTransaction();
			transaction.setCustomAnimations(R.anim.translate_new,
					R.anim.translate_out);
			if (mGroupSharePraisesFragment.isAdded()) {
				transaction.show(mGroupSharePraisesFragment);
			} else {
				transaction.replace(R.id.fragment_praises,
						mGroupSharePraisesFragment);
				transaction.addToBackStack(null);
			}
			transaction.commit();
			break;
		case R.id.iv_checkComment:
			if (rl_comment.getVisibility() == View.VISIBLE) {
				if (!"".equals(phoneTo)) {
					et_comment.setText("");
					et_comment.setHint("添加评论 ... ...");
					phoneTo = "";
					nickNameTo = "";
				} else {
					inputContent = et_comment.getText().toString();
					commentLayoutParams = rl_comment.getLayoutParams();
					commentLayoutParams.height = dp2px(45);
					rl_comment.setVisibility(View.GONE);
				}
			} else {
				if (!"".equals(phoneTo)) {
					et_comment.setHint("回复"
							+ app.data.groupFriends.get(phoneTo).nickName);
				}
				et_comment.setText(inputContent);
				rl_comment.setVisibility(View.VISIBLE);
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
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						if (praiseStatus) {
							data.groupsMap
									.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
									.get(share.gsid).praiseusers
									.add(app.data.user.phone);
						} else {
							data.groupsMap
									.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
									.get(share.gsid).praiseusers
									.remove(app.data.user.phone);
						}
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
		final Comment comment = new Comment();
		comment.content = commentContent;
		comment.contentType = "text";
		comment.head = app.data.user.head;
		comment.nickName = app.data.user.nickName;
		comment.nickNameTo = nickNameTo;
		comment.phone = app.data.user.phone;
		comment.phoneTo = share.phone;
		comment.time = new Date().getTime();
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_ADDCOMMENT;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", comment.phone);
				params.put("phoneTo", comment.phoneTo);
				params.put("accessKey", app.data.user.accessKey);
				params.put("nickName", comment.nickName);
				params.put("nickNameTo", comment.nickNameTo);
				params.put("head", comment.head);
				params.put("gid", app.data.currentGroup);
				params.put("gsid", share.gsid);
				params.put("time", String.valueOf(comment.time));
				params.put("contentType", comment.contentType);
				params.put("content", comment.content);
				settings.params = params;

			}

			@Override
			public void success(JSONObject jData) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						data.groupsMap
								.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap
								.get(share.gsid).comments.add(comment);
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

	public void getGroupShares() {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_GETSHARES;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", GroupShareFragment.mCurrentGroupShareID);
				params.put("gsid", gsid);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final ArrayList<GroupShare> shares = JSONParser
							.generateSharesFromJSON(jData
									.getJSONArray("shares"));
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							HashMap<String, GroupShare> groupSharesMap = data.groupsMap
									.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap;

							if (groupSharesMap == null) {
								data.groupsMap
										.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap = new HashMap<String, GroupShare>();
								groupSharesMap = data.groupsMap
										.get(GroupShareFragment.mCurrentGroupShareID).groupSharesMap;
							}
							groupSharesMap.put(shares.get(0).gsid,
									shares.get(0));
						}

						@Override
						public void modifyUI() {
							initLayout();
							initEvent();
							initData();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
