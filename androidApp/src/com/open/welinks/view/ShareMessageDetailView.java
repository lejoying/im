package com.open.welinks.view;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.ShareMessageDetailController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

public class ShareMessageDetailView {

	public Data data = Data.getInstance();
	public String tag = "ShareMessageDetailView";

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions displayImageOptions;

	public float screenHeight;
	public float screenWidth;
	public float screenDip;
	public float screenDensity;
	public File mSdCardFile;
	public File mImageFile;

	public Gson gson = new Gson();

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleView;

	public LinearLayout shareMessageDetailContentView;
	public ScrollView mainScrollView;
	public InnerScrollView detailScrollView;

	public LinearLayout mainScrollInnerView;

	public LinearLayout praiseUserContentView;

	public RelativeLayout commentInputView;
	public EditText commentEditTextView;
	public RelativeLayout confirmSendCommentView;

	public LinearLayout commentContentView;

	public TextView commentNumberView;

	public TextView sendCommentView;

	public ImageView commentIconView;
	public ImageView praiseIconView;

	public TextView praiseusersNumView;

	// public TextView sendShareMessageUserNameView;
	public TextView shareMessageTimeView;
	// public ImageView shareMessageUserHeadView;

	public InputMethodManager inputMethodManager;

	// menu options
	public RelativeLayout menuOptionsView;
	public RelativeLayout stickOptionView;
	public ImageView stickImageOptionView;
	public TextView stickTextOptionView;
	public RelativeLayout deleteOptionView;
	public ImageView deleteImageOptionView;
	public TextView deleteTextOptionView;

	public ShareMessageDetailView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisView = this;

		mSdCardFile = Environment.getExternalStorageDirectory();
		mImageFile = new File(mSdCardFile, "welinks/images/");
		if (!mImageFile.exists())
			mImageFile.mkdirs();
	}

	public Bitmap bitmap;
	public DisplayMetrics displayMetrics;

	public void initView() {
		initData();

		Resources resources = thisActivity.getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		inputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

		thisActivity.setContentView(R.layout.activity_share_message_detail);
		shareMessageDetailContentView = (LinearLayout) thisActivity.findViewById(R.id.shareMessageDetailContentView);
		mainScrollView = (ScrollView) thisActivity.findViewById(R.id.mainScrollView);
		detailScrollView = (InnerScrollView) thisActivity.findViewById(R.id.detailScrollView);
		mainScrollInnerView = (LinearLayout) thisActivity.findViewById(R.id.mainScrollInnerView);
		detailScrollView.parentScrollView = mainScrollView;
		mainScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		detailScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		backTitleView.setText("分享详情");

		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		shareMessageTimeView = new TextView(context);
		shareMessageTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		shareMessageTimeView.setTextColor(Color.WHITE);
		shareMessageTimeView.setSingleLine();
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		// layoutParams.rightMargin = (int) (20 * displayMetrics.density);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		// layoutParams.leftMargin = (int) (5 * displayMetrics.density);
		rightContainer.addView(shareMessageTimeView, layoutParams);
		// shareMessageTimeView = (TextView)
		// thisActivity.findViewById(R.id.shareMessageTime);
		// shareMessageUserHeadView = (ImageView)
		// thisActivity.findViewById(R.id.shareMessageUserHead);

		sendCommentView = (TextView) thisActivity.findViewById(R.id.sendComment);
		commentNumberView = (TextView) thisActivity.findViewById(R.id.checkCommentNumber);
		commentContentView = (LinearLayout) thisActivity.findViewById(R.id.messageDetailComments);

		praiseusersNumView = (TextView) thisActivity.findViewById(R.id.praiseusersNum);

		praiseUserContentView = (LinearLayout) thisActivity.findViewById(R.id.praiseUserContentView);

		commentInputView = (RelativeLayout) thisActivity.findViewById(R.id.commentInputView);
		commentEditTextView = (EditText) thisActivity.findViewById(R.id.commentEditTextView);
		confirmSendCommentView = (RelativeLayout) thisActivity.findViewById(R.id.rl_sendComment);

		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);

		commentIconView = (ImageView) thisActivity.findViewById(R.id.commentIcon);
		praiseIconView = (ImageView) thisActivity.findViewById(R.id.praiseIconView);

		// menu option
		menuOptionsView = (RelativeLayout) thisActivity.findViewById(R.id.menuOptions);
		stickOptionView = (RelativeLayout) thisActivity.findViewById(R.id.stickOption);
		stickImageOptionView = (ImageView) thisActivity.findViewById(R.id.stickImageOption);
		stickTextOptionView = (TextView) thisActivity.findViewById(R.id.stickTextOption);
		deleteOptionView = (RelativeLayout) thisActivity.findViewById(R.id.deleteOption);
		deleteImageOptionView = (ImageView) thisActivity.findViewById(R.id.deleteImageOption);
		deleteTextOptionView = (TextView) thisActivity.findViewById(R.id.deleteTextOption);

		android.view.ViewGroup.LayoutParams detailScrollViewParams = detailScrollView.getLayoutParams();
		detailScrollViewParams.height = (int) (screenHeight - getStatusBarHeight(thisActivity) - 150 * screenDensity + 0.5f);

		showShareMessageDetail();
	}

	public void initData() {
		mInflater = thisActivity.getLayoutInflater();
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenDensity = displayMetrics.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;
	}

	public ArrayList<String> showImages;
	public Friend friend;

	public void showShareMessageDetail() {
		friend = data.relationship.friendsMap.get(thisController.shareMessage.phone);
		if (friend != null) {
			// shareMessageUserHeadView.setImageBitmap(bitmap);
		}
		shareMessageTimeView.setText(DateUtil.getTime(thisController.shareMessage.time));

		String content = thisController.shareMessage.content;
		ShareContent shareContent = gson.fromJson("{shareContentItems:" + content + "}", ShareContent.class);
		List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
		String textContent = "";
		int index = 0;
		showImages = new ArrayList<String>();
		for (int i = 0; i < shareContentItems.size(); i++) {
			final ImageView imageView = new ImageView(thisActivity);
			shareMessageDetailContentView.addView(imageView);
			ShareContentItem shareContentItem = shareContentItems.get(i);
			String type = shareContentItem.type;
			if (type.equals("text")) {
				textContent = shareContentItem.detail;
				continue;
			}
			String imageFileName = shareContentItem.detail;

			imageView.setTag("ShareMessageDetailImage#" + index);
			index++;
			imageView.setOnClickListener(thisController.mOnClickListener);

			File currentImageFile = new File(mImageFile, imageFileName);
			String filepath = currentImageFile.getAbsolutePath();
			showImages.add(filepath);
			boolean isFlag = false;
			String path = "";
			if (currentImageFile.exists()) {
				BitmapFactory.Options boptions = new BitmapFactory.Options();
				boptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(currentImageFile.getAbsolutePath(), boptions);
				if (boptions.outWidth > 0) {
					isFlag = true;
				}
			}
			if (isFlag) {
				path = "file://" + filepath;
			} else {
				path = API.DOMAIN_COMMONIMAGE + "images/" + imageFileName;
			}

			if (!isFlag) {
				DownloadFile downloadFile = new DownloadFile(path, filepath);
				downloadFile.view = imageView;
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				thisController.downloadFileList.addDownloadFile(downloadFile);
			} else {
				imageLoader.displayImage(path, imageView, displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
						imageView.setLayoutParams(params);
					}
				});
			}
		}
		if (!"".equals(textContent)) {
			TextView textview = new TextView(thisActivity);
			textview.setTextColor(Color.WHITE);
			textview.setBackgroundColor(Color.parseColor("#26ffffff"));
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			int padding = (int) (10 * screenDensity + 0.5f);
			textview.setPadding(padding, padding, padding, padding);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			textview.setLayoutParams(params);
			textview.setText(textContent);
			shareMessageDetailContentView.addView(textview);
		}
		if (thisController.shareMessage.praiseusers.contains(thisController.currentUser.phone)) {
			praiseIconView.setImageResource(R.drawable.praised_icon);
		} else {
			praiseIconView.setImageResource(R.drawable.praise_icon);
		}
		showPraiseUsersContent();
		notifyShareMessageComments();
	}

	public void showPraiseUsersContent() {
		// TODO
		praiseUserContentView.removeAllViews();
		List<String> praiseUsers = thisController.shareMessage.praiseusers;

		int headWidth = praiseUserContentView.getWidth() / 5 - 5;
		int padding = (int) (5 * screenDensity);
		praiseusersNumView.setText("共获得" + praiseUsers.size() + "个赞");
		ImageView iv = new ImageView(thisActivity);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		param.weight = 1;
		iv.setLayoutParams(param);
		praiseUserContentView.addView(iv);
		for (int i = 0; i < praiseUsers.size(); i++) {
			final ImageView view = new ImageView(thisActivity);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(headWidth, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			view.setPadding(padding, 0, padding, 0);
			view.setLayoutParams(params);
			view.setImageBitmap(bitmap);

			praiseUserContentView.addView(view);
			if (i == 5)
				break;
		}
	}

	public void notifyShareMessageComments() {
		List<Comment> comments = thisController.shareMessage.comments;
		// Log.e(tag, comments.size() + "---------------size");
		commentIconView.setImageResource(R.drawable.comment_icon);
		for (Comment comment : comments) {
			if (comment.phone.equals(thisController.currentUser.phone)) {
				commentIconView.setImageResource(R.drawable.commented_icon);
				break;
			}
		}
		commentNumberView.setText("查看全部" + comments.size() + "条评论...");
		commentContentView.removeAllViews();
		for (final Comment comment : comments) {
			View view = mInflater.inflate(R.layout.groupshare_commentchild, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins((int) (10 * screenDensity), 0, (int) (10 * screenDensity), 0);
			view.setLayoutParams(params);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView content = (TextView) view.findViewById(R.id.content);
			TextView reply = (TextView) view.findViewById(R.id.reply);
			TextView receive = (TextView) view.findViewById(R.id.receive);
			TextView received = (TextView) view.findViewById(R.id.received);
			final ImageView head = (ImageView) view.findViewById(R.id.head);
			content.setText(comment.content);
			time.setText(DateUtil.getTime(comment.time));
			receive.setText(comment.nickName);
			received.setText(comment.nickNameTo);

			if ("".equals(comment.nickNameTo)) {
				reply.setVisibility(View.GONE);
				received.setVisibility(View.GONE);
			}
			head.setImageBitmap(bitmap);
			view.setTag("ShareComment#" + comment.phone);
			view.setTag(R.id.commentEditTextView, comment);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (rl_comment.getVisibility() == View.GONE) {
					// rl_comment.setVisibility(View.VISIBLE);
					// }
					// if (!comment.phone.equals(app.data.user.phone)) {
					// phoneTo = comment.phone;
					// nickNameTo = comment.nickName;
					// et_comment.setHint("回复" + nickNameTo);
					// } else {
					// phoneTo = "";
					// nickNameTo = "";
					// et_comment.setHint("添加评论 ... ...");
					// }
				}
			});
			commentContentView.addView(view);
		}
	}

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			menuOptionsView.setScaleX(mappedValue);
			menuOptionsView.setScaleY(mappedValue);
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

}
