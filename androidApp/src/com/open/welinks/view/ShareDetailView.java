package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchLinearLayoutView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.welinks.R;
import com.open.welinks.controller.ShareDetailController;
import com.open.welinks.customView.ListBody2;
import com.open.welinks.customView.ListBody2.MyListItemBody;
import com.open.welinks.model.Data.Boards.Comment;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.SubData.ImageListener;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.model.TaskManageHolder;

public class ShareDetailView {

	public String tag = "ShareDetailView";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public ShareDetailView thisView;
	public ShareDetailController thisController;
	public Activity thisActivity;

	public ListBody2 shareMessageDetailListBody;

	public ShareDetailView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisView = this;
		this.thisActivity = thisActivity;
	}

	public TouchView container;

	public DisplayMetrics displayMetrics;

	public LayoutInflater mInflater;

	public void initView() {
		this.displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.mInflater = this.thisActivity.getLayoutInflater();
		this.displayMetrics = new DisplayMetrics();
		this.thisActivity.getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);
		this.thisActivity.setContentView(R.layout.activity_share_detail);
		this.container = (TouchView) thisActivity.findViewById(R.id.container);

		this.shareMessageDetailListBody = new ListBody2();
		this.shareMessageDetailListBody.initialize(this.displayMetrics, this.container);
		this.shareMessageDetailListBody.containerHeight = (int) (this.displayMetrics.heightPixels - ViewManage.getStatusBarHeight(thisActivity) - displayMetrics.density * 10);
		this.showShareDetail();
	}

	public Gson gson = new Gson();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	boolean isFinish = false;

	public void showShareDetail() {
		isFinish = false;
		this.container.removeAllViews();
		this.shareMessageDetailListBody.listItemsSequence.clear();
		this.shareMessageDetailListBody.containerView.removeAllViews();
		this.shareMessageDetailListBody.height = 0;
		for (int i = 0; i < 1; i++) {
			String key = "_key" + i;
			SharesDetailItemBody body = (SharesDetailItemBody) shareMessageDetailListBody.listItemBodiesMap.get(key);
			log.e("-------------" + key + "-----------" + body);
			int height = (int) (300 * displayMetrics.density);
			if (body == null) {
				body = new SharesDetailItemBody(shareMessageDetailListBody);
				body.initialize();
				body.setContent(thisController.shareMessage, null);
				height = body.getCurrentTotalHeight();

				shareMessageDetailListBody.listItemsSequence.add(key);
				shareMessageDetailListBody.listItemBodiesMap.put(key, body);
			} else {
				height = body.getCurrentTotalHeight();
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (displayMetrics.widthPixels), height);
			body.y = this.shareMessageDetailListBody.height;
			body.cardView.setY(body.y);
			body.cardView.setX(0);
			body.itemHeight = height;
			this.shareMessageDetailListBody.height = this.shareMessageDetailListBody.height + body.itemHeight;
			this.shareMessageDetailListBody.containerView.addView(body.cardView, layoutParams);
		}
		// this.showComments();
		this.isFinish = true;
		log.e(this.shareMessageDetailListBody.containerHeight + "------------------------" + shareMessageDetailListBody.height);
	}

	public void showComments() {
		List<Comment> comments = thisController.shareMessage.comments;
		for (int i = 0; i < comments.size(); i++) {
			Comment comment = comments.get(i);
			String key = "_key" + i;
			SharesDetailItemBody body = (SharesDetailItemBody) shareMessageDetailListBody.listItemBodiesMap.get(key);
			log.e("-------------" + key + "-----------" + body);
			int height = (int) (300 * displayMetrics.density);
			if (body == null) {
				body = new SharesDetailItemBody(shareMessageDetailListBody);
				body.initialize();
				body.setContent(thisController.shareMessage, comment);
				height = body.getCurrentTotalHeight();
				shareMessageDetailListBody.listItemsSequence.add(key);
				shareMessageDetailListBody.listItemBodiesMap.put(key, body);
			} else {
				height = body.getCurrentTotalHeight();
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (displayMetrics.widthPixels), height);
			body.y = this.shareMessageDetailListBody.height;
			body.cardView.setY(body.y);
			body.cardView.setX(0);
			body.itemHeight = height;
			this.shareMessageDetailListBody.height = this.shareMessageDetailListBody.height + body.itemHeight;
			this.shareMessageDetailListBody.containerView.addView(body.cardView, layoutParams);
		}
	}

	public DisplayImageOptions displayImageOptions;

	public class SharesDetailItemBody extends MyListItemBody {

		SharesDetailItemBody(ListBody2 listBody) {
			listBody.super();
		}

		public TouchLinearLayoutView cardView = null;

		public ImageView headView;
		public TextView nickNameView;
		public TextView timeView;
		public ImageView decrementView;
		public ImageView incrementView;
		public TextView scoreView;
		public View commentBar;
		public View commentControlView;
		public LinearLayout contentContainer;

		public ArrayList<String> imagesFileName;

		public void initialize() {
			this.cardView = (TouchLinearLayoutView) mInflater.inflate(R.layout.share_detail_item, null);
			this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
			this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
			this.timeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
			this.decrementView = (ImageView) this.cardView.findViewById(R.id.num_picker_decrement);
			this.incrementView = (ImageView) this.cardView.findViewById(R.id.num_picker_increment);
			this.scoreView = (TextView) this.cardView.findViewById(R.id.totalScore);
			this.contentContainer = (LinearLayout) this.cardView.findViewById(R.id.contentContainer);
			this.commentBar = mInflater.inflate(R.layout.view_share_detail_comment, null);
			this.commentControlView = this.commentBar.findViewById(R.id.commentControl);
			// this.commentControlView.setAlpha(0.5f);

			this.imagesFileName = new ArrayList<String>();

			super.initialize(cardView);
		}

		public class ImageBean {
			public String fileName;
			public float width;
			public float height;
			public float radio;
		}

		public ArrayList<ImageBean> imageBeans = new ArrayList<ImageBean>();

		float textHeight = 0;

		int successCount = 0;

		boolean isReload = false;

		public int getCurrentTotalHeight() {
			float height = 60 * displayMetrics.density + 80 * displayMetrics.density + textHeight;
			for (int i = 0; i < imageBeans.size(); i++) {
				ImageBean imageBean = imageBeans.get(i);
				height += imageBean.height;
			}
			return (int) height;
		}

		int type = 0;

		ShareMessage shareMessage;
		Comment comment;

		public void setContent(ShareMessage shareMessage, Comment comment) {
			if (comment != null) {
				this.comment = comment;
				type = 2;
			} else {
				this.shareMessage = shareMessage;
				type = 1;
			}
			if (type == 1) {
				String content = shareMessage.content;
				List<ShareContentItem> shareContentItems = gson.fromJson(content, new TypeToken<ArrayList<ShareContentItem>>() {
				}.getType());
				if (shareContentItems == null) {
					log.e("帖子详情数据为空");
				}
				String textContent = "";
				String imageContent = "";
				for (int i = 0; i < shareContentItems.size(); i++) {
					final TouchImageView imageView = new TouchImageView(thisActivity);
					imageView.setTag(R.id.tag_first, "max");
					this.contentContainer.addView(imageView);
					ShareContentItem shareContentItem = shareContentItems.get(i);
					String type1 = shareContentItem.type;
					if (type1.equals("text")) {
						textContent = shareContentItem.detail;
						continue;
					}
					String imageFileName = shareContentItem.detail;
					if ("".equals(imageContent)) {
						imageContent = imageFileName;
						// thisView.imageView = imageView;
						imageView.setDrawingCacheEnabled(true);
					}

					final ImageBean imageBean = new ImageBean();
					imageBean.fileName = imageFileName;
					imageBean.width = displayMetrics.widthPixels - displayMetrics.density * 20;
					imageBeans.add(imageBean);
					imagesFileName.add(imageFileName);
					taskManageHolder.fileHandler.getImage(imageFileName, imageView, displayImageOptions, new ImageListener() {
						@Override
						public void onSuccess(float radio) {
							imageBean.height = imageBean.width * radio;
							imageBean.radio = radio;
							log.e("ImageListener");
							successCount++;
							if (successCount == imagesFileName.size() && !isReload && isFinish) {
								isReload = true;
								showShareDetail();
							}
						}
					});
				}

				if (!"".equals(textContent)) {
					TouchTextView textView = new TouchTextView(thisActivity);
					textView.setTextColor(Color.parseColor("#99000000"));
					textView.setBackgroundColor(Color.parseColor("#26ffffff"));
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					int padding = (int) (10 * displayMetrics.density + 0.5f);
					textView.setPadding(padding, padding, padding, 0);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					textView.setLayoutParams(params);
					textView.setWidth((int) (displayMetrics.widthPixels - 20 * displayMetrics.density));
					textView.setText(textContent);
					textView.setAutoLinkMask(Linkify.WEB_URLS);
					textView.setMovementMethod(LinkMovementMethod.getInstance());
					this.contentContainer.addView(textView);
					int widthMeasureSpec = 0, heightMeasureSpec = 0;
					textView.measure(widthMeasureSpec, heightMeasureSpec);
					textHeight = textView.getHeight();
					log.e("---" + textHeight);
					log.e("---" + textView.getLineCount());
					log.e("---" + textView.getLineHeight());
					textHeight = textView.getLineCount() * textView.getLineHeight();
				}
				this.contentContainer.addView(commentBar);
				if (!"".equals(textContent)) {
					LinearLayout.LayoutParams params0 = (android.widget.LinearLayout.LayoutParams) commentBar.getLayoutParams();
					params0.topMargin = (int) (-20 * displayMetrics.density);
					commentBar.setLayoutParams(params0);
				}
			} else if (type == 2) {
				this.scoreView.setText("0");
				TextView textView = new TextView(thisActivity);
				textView.setTextColor(Color.parseColor("#99000000"));
				textView.setBackgroundColor(Color.parseColor("#26ffffff"));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				int padding = (int) (10 * displayMetrics.density + 0.5f);
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
}
