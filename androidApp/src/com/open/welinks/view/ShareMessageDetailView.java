package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.controller.ShareMessageDetailController;
import com.open.welinks.customView.ShareView;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Comment;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.DateUtil;

public class ShareMessageDetailView {

	public Data data = Data.getInstance();
	public String tag = "ShareMessageDetailView";
	public MyLog log = new MyLog(tag, true);

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public DisplayImageOptions displayImageOptions;

	public float screenHeight;
	public float screenWidth;
	public float screenDip;
	public float screenDensity;

	public Gson gson = new Gson();

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleView;
	public View backMaxView;
	public ImageView backImageView;

	public TextView shareMessageTimeView;

	public InputMethodManager inputMethodManager;

	public ShareView shareView;
	public PopupWindow sharePopupWindow;

	public File mImageFile;

	// menu options
	public RelativeLayout menuOptionsView;
	public RelativeLayout shareOptionView;
	public ImageView stickImageOptionView;
	public TextView stickTextOptionView;
	public RelativeLayout deleteOptionView;
	public ImageView deleteImageOptionView;
	public TextView deleteTextOptionView;

	public ShareMessageDetailView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisView = this;
		taskManageHolder.viewManage.shareMessageDetailView = this;
		mImageFile = taskManageHolder.fileHandler.sdcardImageFolder;
	}

	public DisplayMetrics displayMetrics;

	public ImageView menuImage;

	public LinearLayout contentContainer;
	public LinearLayout commentContainer;

	public View maxView;

	public SmallBusinessCardPopView businessCardPopView;

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenDensity = displayMetrics.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;

		inputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

		thisActivity.setContentView(R.layout.activity_share_message_detail);
		this.maxView = thisActivity.findViewById(R.id.maxView);
		ScrollView scrollView = (ScrollView) thisActivity.findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (thisView.menuOptionsView.getVisibility() == View.VISIBLE) {
					thisView.menuOptionsView.setVisibility(View.GONE);
				}
				return false;
			}
		});

		// thisActivity.findViewById(R.id.container).setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View view) {
		// Toast.makeText(thisActivity, "onLongClick", Toast.LENGTH_SHORT).show();
		// return true;
		// }
		// });
		backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		backTitleView.setText("分享详情");
		backImageView = (ImageView) thisActivity.findViewById(R.id.backImageView);

		this.businessCardPopView = new SmallBusinessCardPopView(thisActivity, this.maxView);
		businessCardPopView.cardView.setHot(false);

		this.contentContainer = (LinearLayout) thisActivity.findViewById(R.id.contentContainer);
		this.commentContainer = (LinearLayout) thisActivity.findViewById(R.id.commentContainer);

		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		RelativeLayout.LayoutParams layoutParams1 = (android.widget.RelativeLayout.LayoutParams) rightContainer.getLayoutParams();
		layoutParams1.rightMargin = (int) (displayMetrics.density * 35 + 0.5f);
		shareMessageTimeView = new TextView(context);
		shareMessageTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		shareMessageTimeView.setTextColor(Color.parseColor("#0099cd"));
		shareMessageTimeView.setPadding(10, 10, 10 + (int) (10 * displayMetrics.density), 10);// 30
		shareMessageTimeView.setSingleLine();
		// shareMessageTimeView.setBackgroundResource(R.drawable.backview_background);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// layoutParams.rightMargin = (int) (20 * displayMetrics.density);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		// layoutParams.leftMargin = (int) (5 * displayMetrics.density);
		rightContainer.addView(shareMessageTimeView, layoutParams);
		// shareMessageTimeView = (TextView)
		// thisActivity.findViewById(R.id.shareMessageTime);
		// shareMessageUserHeadView = (ImageView)
		// thisActivity.findViewById(R.id.shareMessageUserHead);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		backView.setBackgroundResource(R.drawable.selector_back_white);

		menuImage = new ImageView(thisActivity);
		menuImage.setImageResource(R.drawable.chat_more);
		menuImage.setColorFilter(Color.parseColor("#0099cd"));
		RelativeLayout view = (RelativeLayout) backView.getParent();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) BaseDataUtils.dpToPx(25.5f), android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.rightMargin = (int) (15 * displayMetrics.density + 0.5f);
		int padding = (int) (5 * displayMetrics.density + 0.5f);
		menuImage.setPadding(2 * padding, padding, 2 * padding, padding);
		menuImage.setBackgroundResource(R.drawable.backview_background);
		view.addView(menuImage, params);
		// menu option
		menuOptionsView = (RelativeLayout) thisActivity.findViewById(R.id.menuOptions);
		shareOptionView = (RelativeLayout) thisActivity.findViewById(R.id.shareOption);
		stickImageOptionView = (ImageView) thisActivity.findViewById(R.id.stickImageOption);
		stickTextOptionView = (TextView) thisActivity.findViewById(R.id.stickTextOption);
		deleteOptionView = (RelativeLayout) thisActivity.findViewById(R.id.deleteOption);
		deleteImageOptionView = (ImageView) thisActivity.findViewById(R.id.deleteImageOption);
		deleteTextOptionView = (TextView) thisActivity.findViewById(R.id.deleteTextOption);
		shareView = new ShareView(thisActivity);
		// shareView.firstPath = API.DOMAIN_COMMONIMAGE + "images/" + imagePath;
		// shareView.phone = data.userInformation.currentUser.phone;
		// shareView.gid = thisController.gid;
		// shareView.gsid = thisController.gsid;
		// shareView.content = thisController.textContent;
		sharePopupWindow = new PopupWindow(shareView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		thisController.initShareListener();
		// android.view.ViewGroup.LayoutParams detailScrollViewParams = detailScrollView.getLayoutParams();
		// detailScrollViewParams.height = (int) (screenHeight - getStatusBarHeight(thisActivity) - 150 * screenDensity + 0.5f);
		backMaxView = thisActivity.findViewById(R.id.backMaxView);

		backMaxView.setBackgroundColor(Color.WHITE);
		backTitleView.setTextColor(Color.parseColor("#0099cd"));
		backImageView.setColorFilter(Color.parseColor("#0099cd"));

		if (thisController.shareMessage != null) {
			showShareMessageDetails();
			thisController.getShareMessageDetail();
		}
	}

	public ArrayList<String> showImages;
	public Friend friend;

	public String imagePath = "";
	public ImageView imageView;

	public void showShareMessageDetails() {
		shareMessageTimeView.setText("");

		// this.scoreView.setText(thisController.shareMessage.totalScore);
		// if (thisController.shareMessage.phone.equals(data.userInformation.currentUser.phone)) {
		deleteOptionView.setVisibility(View.VISIBLE);
		// } else {
		// deleteOptionView.setVisibility(View.GONE);
		// }
		if (!"sent".equals(thisController.shareMessage.status)) {
			if (deleteOptionView.getVisibility() == View.GONE) {
				menuOptionsView.setVisibility(View.GONE);
			} else {
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) deleteOptionView.getLayoutParams();
				layoutParams.topMargin = 0;
			}
		}
		if (body == null) {
			body = new ShareBody();
			body.initViews();
		}
		this.contentContainer.removeAllViews();
		body.setContent(thisController.shareMessage, null);
		this.contentContainer.addView(body.cardView);
		this.showShareComments();
	}

	public ShareBody body;

	public class ShareBody {

		public View cardView;

		public ImageView headView;
		public TextView nickNameView;
		public TextView timeView;
		public ImageView decrementView;
		public ImageView incrementView;
		public TextView scoreView;

		public LinearLayout contentContainer;

		public ShareMessage shareMessage;
		public Comment comment;

		public View commentBar;
		public View commentControlView;

		public ArrayList<String> imageList;

		public User currentUser;

		public void initViews() {
			this.cardView = mInflater.inflate(R.layout.share_detail_item, null);
			this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
			this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
			this.timeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
			this.decrementView = (ImageView) this.cardView.findViewById(R.id.num_picker_decrement);
			this.incrementView = (ImageView) this.cardView.findViewById(R.id.num_picker_increment);
			this.scoreView = (TextView) this.cardView.findViewById(R.id.totalScore);
			this.contentContainer = (LinearLayout) this.cardView.findViewById(R.id.contentContainer);
			// commentControl
			this.commentBar = mInflater.inflate(R.layout.view_share_detail_comment, null);
			this.commentControlView = this.commentBar.findViewById(R.id.commentControl);
			this.commentControlView.setAlpha(0.5f);
			this.currentUser = data.userInformation.currentUser;
			this.imageList = new ArrayList<String>();
		}

		public void setContent(ShareMessage shareMessage, Comment comment) {
			this.shareMessage = shareMessage;
			this.comment = comment;
			String phone = null, nickName = null, head = null;
			long time = 0;
			int type = 1;
			if (this.shareMessage == null) {
				friend = data.relationship.friendsMap.get(this.comment.phone);
				if (friend != null) {
					phone = friend.phone;
					nickName = friend.nickName;
					head = friend.head;
				} else {
					phone = this.comment.phone;
					nickName = this.comment.nickName;
					head = this.comment.head;
				}
				time = this.comment.time;
				type = 2;
			} else if (this.comment == null) {
				friend = data.relationship.friendsMap.get(this.shareMessage.phone);
				if (friend != null) {
					phone = friend.phone;
					nickName = friend.nickName;
					head = friend.head;
				} else {
					phone = this.shareMessage.phone;
					nickName = this.shareMessage.nickName;
					head = this.shareMessage.head;
				}
				time = this.shareMessage.time;
				type = 1;
			}
			this.contentContainer.removeAllViews();
			taskManageHolder.fileHandler.getHeadImage(head, this.headView, taskManageHolder.viewManage.options40, null);
			this.headView.setOnClickListener(thisController.mOnClickListener);
			this.headView.setTag(R.id.tag_class, "HeadView");
			this.headView.setTag(R.id.tag_first, phone);
			this.nickNameView.setText(nickName);
			if (currentUser.phone.equals(phone)) {
				taskManageHolder.fileHandler.getHeadImage(currentUser.head, this.headView, taskManageHolder.viewManage.options40, null);
				this.nickNameView.setText(currentUser.nickName);
			}
			this.timeView.setText(DateUtil.getNearShareTime(time));
			this.timeView.setOnClickListener(thisController.mOnClickListener);
			this.timeView.setTag(R.id.tag_class, "TimeView");
			this.timeView.setTag(R.id.tag_first, time);
			this.timeView.setTag(R.id.tag_second, 1);
			if (type == 1) {
				if (shareMessage.type != "imagetext") {
				}
				String content = this.shareMessage.content;

				List<ShareContentItem> shareContentItems = gson.fromJson(content, new TypeToken<ArrayList<ShareContentItem>>() {
				}.getType());
				if (shareContentItems == null) {
					Log.e(tag, content);
					return;
				}
				this.scoreView.setText(this.shareMessage.totalScore + "");
				thisController.textContent = "";
				thisController.imageContent = "";
				int index = 0;
				showImages = new ArrayList<String>();
				for (int i = 0; i < shareContentItems.size(); i++) {
					final ImageView imageView = new ImageView(thisActivity);
					imageView.setTag(R.id.tag_first, "max");
					this.contentContainer.addView(imageView);
					ShareContentItem shareContentItem = shareContentItems.get(i);
					String type1 = shareContentItem.type;
					if (type1.equals("text")) {
						thisController.textContent = shareContentItem.detail;
						continue;
					}
					String imageFileName = shareContentItem.detail;
					if ("".equals(thisController.imageContent)) {
						thisController.imageContent = imageFileName;
						thisView.imageView = imageView;
						imageView.setDrawingCacheEnabled(true);
					}
					this.imageList.add(imageFileName);
					imageView.setOnClickListener(thisController.mOnClickListener);
					imageView.setTag(R.id.tag_class, "ShareMessageDetailImage");
					imageView.setTag(R.id.tag_first, this);
					imageView.setTag(R.id.tag_second, index);
					index++;

					taskManageHolder.fileHandler.getImage(imageFileName, imageView, displayImageOptions, null);

					File currentImageFile = new File(mImageFile, imageFileName);
					String filepath = currentImageFile.getAbsolutePath();
					showImages.add(filepath);
				}

				if (!"".equals(thisController.textContent)) {
					TextView textView = new TextView(thisActivity);
					textView.setTextColor(Color.parseColor("#99000000"));
					textView.setBackgroundColor(Color.parseColor("#26ffffff"));
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					int padding = (int) (10 * screenDensity + 0.5f);
					textView.setPadding(padding, padding, padding, 0);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					textView.setLayoutParams(params);

					textView.setText(thisController.textContent);
					textView.setAutoLinkMask(Linkify.WEB_URLS);
					textView.setMovementMethod(LinkMovementMethod.getInstance());
					this.contentContainer.addView(textView);
				}
				this.contentContainer.addView(commentBar);
				if (!"".equals(thisController.textContent)) {
					LinearLayout.LayoutParams params0 = (android.widget.LinearLayout.LayoutParams) commentBar.getLayoutParams();
					params0.topMargin = (int) (-20 * displayMetrics.density);
					commentBar.setLayoutParams(params0);
				}

				scoreState();
				this.decrementView.setOnClickListener(thisController.mOnClickListener);
				this.decrementView.setTag(R.id.tag_class, "DecrementView");
				this.incrementView.setOnClickListener(thisController.mOnClickListener);
				this.incrementView.setTag(R.id.tag_class, "IncrementView");
				this.commentControlView.setOnClickListener(thisController.mOnClickListener);
				this.commentControlView.setTag(R.id.tag_class, "CommentControlView");

				shareView.phone = data.userInformation.currentUser.phone;
				shareView.sid = thisController.sid;
				shareView.gsid = thisController.gsid;
				shareView.content = thisController.textContent;
			} else if (type == 2) {
				this.scoreView.setText("0");
				TextView textView = new TextView(thisActivity);
				textView.setTextColor(Color.parseColor("#99000000"));
				textView.setBackgroundColor(Color.parseColor("#26ffffff"));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				int padding = (int) (10 * screenDensity + 0.5f);
				textView.setPadding(padding, padding, padding, 0);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				textView.setLayoutParams(params);

				// SpannableString spannableString = new SpannableString(thisController.textContent);

				textView.setText(this.comment.content);
				textView.setAutoLinkMask(Linkify.WEB_URLS);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
				this.contentContainer.addView(textView);
				this.contentContainer.addView(commentBar);
				LinearLayout.LayoutParams params0 = (android.widget.LinearLayout.LayoutParams) commentBar.getLayoutParams();
				params0.topMargin = (int) (-35 * displayMetrics.density);
				commentBar.setLayoutParams(params0);
			}
			this.cardView.findViewById(R.id.share).setBackgroundResource(R.drawable.card_login_background_white);
		}
	}

	public void scoreState() {
		User currentUser = data.userInformation.currentUser;
		if (thisController.shareMessage.scores != null) {
			Score score = thisController.shareMessage.scores.get(currentUser.phone);
			body.scoreView.setText(thisController.shareMessage.totalScore + "");
			if (score != null) {
				if (score.positive > 0) {
					body.incrementView.setImageResource(R.drawable.num_picker_increment_on);
				}
				if (score.negative > 0) {
					body.decrementView.setImageResource(R.drawable.num_picker_decrement_on);
				}
			} else {
				body.incrementView.setImageResource(R.drawable.selector_num_picker_increment);
				body.decrementView.setImageResource(R.drawable.selector_num_picker_decrement);
			}
		} else {
			body.incrementView.setImageResource(R.drawable.selector_num_picker_increment);
			body.decrementView.setImageResource(R.drawable.selector_num_picker_decrement);
		}
		if (thisController.shareMessage.totalScore < 10 && thisController.shareMessage.totalScore >= 0) {
			body.scoreView.setTextColor(Color.parseColor("#0099cd"));
		} else if (thisController.shareMessage.totalScore < 100 && thisController.shareMessage.totalScore >= 0) {
			body.scoreView.setTextColor(Color.parseColor("#0099cd"));
		} else if (thisController.shareMessage.totalScore < 1000 && thisController.shareMessage.totalScore >= 0) {
			body.scoreView.setTextColor(Color.parseColor("#0099cd"));
			body.scoreView.setText("999");
		} else if (thisController.shareMessage.totalScore < 0) {
			body.scoreView.setTextColor(Color.parseColor("#00a800"));
		}
	}

	public HashMap<String, ShareBody> commentsMap = new HashMap<String, ShareBody>();

	public void showShareComments() {
		this.commentContainer.removeAllViews();
		List<Comment> comments = thisController.shareMessage.comments;
		for (int i = comments.size() - 1; i >= 0; i--) {
			Comment comment = comments.get(i);
			String key = i + "_" + comment.phone + "_" + comment.time;
			ShareBody body = this.commentsMap.get(key);
			if (body == null) {
				body = new ShareBody();
				body.initViews();
				commentsMap.put(key, body);
			}
			body.setContent(null, comment);

			this.commentContainer.addView(body.cardView);
		}
		TextView textView = new TextView(thisActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (10 * this.displayMetrics.density));
		textView.setLayoutParams(params);
		this.commentContainer.addView(textView);
	}
}
