package com.open.welinks.view;

import com.open.welinks.R;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Shares.Share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ShareView extends FrameLayout {
	private Context context;
	private Activity activity;
	private Message message;
	private Share share;
	private LinearLayout layout_one, layout_two, layout_three;
	private View square_share, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone;

	public int RESULT_SHAREVIEW = 0x99;

	private enum Status {
		square_share, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone
	}

	private OnClickListener mOnClickListener;

	public ShareView(Context context) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public ShareView(Context context, Share share) {
		super(context);
		this.share = share;
		this.context = context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		onCreate();
	}

	private void onCreate() {
		LayoutInflater.from(context).inflate(R.layout.layout_share, this);
		((ViewGroup) activity.findViewById(android.R.id.content)).addView(this);
		this.setClickable(false);
		layout_one = (LinearLayout) this.findViewById(R.id.layout_one);
		layout_two = (LinearLayout) this.findViewById(R.id.layout_two);
		layout_three = (LinearLayout) this.findViewById(R.id.layout_three);

		square_share = this.findViewById(R.id.square_share);
		friend_group = this.findViewById(R.id.friend_group);
		wechat_friend = this.findViewById(R.id.wechat_friend);
		wechat_circle = this.findViewById(R.id.wechat_circle);
		sina_weibo = this.findViewById(R.id.sina_weibo);
		qq_qzone = this.findViewById(R.id.qq_qzone);

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(square_share)) {
					shareToSquareLocal(Status.square_share);
				} else if (view.equals(friend_group)) {
					shareToSquareLocal(Status.friend_group);
				} else if (view.equals(wechat_friend)) {
					shareToWechat(Status.wechat_friend);
				} else if (view.equals(wechat_circle)) {
					shareToWechat(Status.wechat_circle);
				} else if (view.equals(sina_weibo)) {

				} else if (view.equals(qq_qzone)) {

				} else if (view.equals(this)) {
					dismiss();
				}

			}
		};

		square_share.setOnClickListener(mOnClickListener);
		friend_group.setOnClickListener(mOnClickListener);
		wechat_friend.setOnClickListener(mOnClickListener);
		wechat_circle.setOnClickListener(mOnClickListener);
		sina_weibo.setOnClickListener(mOnClickListener);
		qq_qzone.setOnClickListener(mOnClickListener);
		this.setOnClickListener(mOnClickListener);
	}

	private void shareToSquareLocal(Status status) {
		System.out.println("showing-----------");
	}

	private void shareToWechat(Status status) {
		System.out.println("showing-----------");
	}

	public ShareView setShareContent(Share share) {
		this.share = share;
		return this;
	}

	public void show() {
		if (this.getVisibility() == View.GONE) {
			this.setClickable(true);
			this.setVisibility(View.VISIBLE);
		}
	}

	public void dismiss() {
		if (this.getVisibility() == View.VISIBLE) {
			this.setClickable(false);
			this.setVisibility(View.GONE);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

}
