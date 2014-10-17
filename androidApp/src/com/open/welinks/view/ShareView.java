package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.open.welinks.GroupListActivity;
import com.open.welinks.R;
import com.open.welinks.utils.WeChatShareUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ShareView extends FrameLayout {
	private Context context;
	private Activity activity;
	private LinearLayout layout_one, layout_two, layout_three;
	private View square_share, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone;

	private WeChatShareUtils weChatShareUtils;

	private onWeChatClickListener mWeChatClickListener;
	private Bitmap bitmap;
	public String content, phone, gid, gsid;
	public int RESULT_SHAREVIEW = 0x99;

	public List<String> firstPath = new ArrayList<String>();

	private enum Status {
		square_group, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone
	}

	private OnClickListener mOnClickListener;

	public ShareView(Context context) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public class onWeChatClickListener {
		public void onWeChatClick() {
		}
	}

	public void setOnWeChatClickListener(onWeChatClickListener mListener) {
		this.mWeChatClickListener = mListener;
	}

	public void setWeChatContent(Bitmap bitmap, String content, String phone, String gid, String gsid) {
		this.bitmap = bitmap;
		this.content = content;
		this.phone = phone;
		this.gid = gid;
		this.gsid = gsid;
	}

	private void onCreate() {
		LayoutInflater.from(context).inflate(R.layout.view_dialog_share, this);

		weChatShareUtils = WeChatShareUtils.getInstance(context);

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
					shareToLocal(Status.square_group);
				} else if (view.equals(friend_group)) {
					shareToLocal(Status.friend_group);
				} else if (view.equals(wechat_friend)) {
					mWeChatClickListener.onWeChatClick();
					shareToWechat(Status.wechat_friend);
				} else if (view.equals(wechat_circle)) {
					mWeChatClickListener.onWeChatClick();
					shareToWechat(Status.wechat_circle);
				} else if (view.equals(sina_weibo)) {

				} else if (view.equals(qq_qzone)) {
					shareToQQzone();
				}
			}
		};

		mWeChatClickListener = new onWeChatClickListener();

		square_share.setOnClickListener(mOnClickListener);
		friend_group.setOnClickListener(mOnClickListener);
		wechat_friend.setOnClickListener(mOnClickListener);
		wechat_circle.setOnClickListener(mOnClickListener);
		sina_weibo.setOnClickListener(mOnClickListener);
		qq_qzone.setOnClickListener(mOnClickListener);
	}

	private void shareToQQzone() {
		String url = "http://www.we-links.com/share/share.html?phone=" + phone + "&gid=" + gid + "&gsid=" + gsid;// 收到分享的好友点击信息会跳转到这个地址去
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "微型社区群分享");
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
		ArrayList<String> imageUrls = new ArrayList<String>();
		// if (firstPath.size()=0) {
		// firstPath = "http://images.liqucn.com/h015/h01/img201405280717020842_info72X72.png";
		// }
		imageUrls.addAll(firstPath);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		Tencent mTencent = Tencent.createInstance("101133279", ((Activity) context).getApplicationContext());
		mTencent.shareToQzone((Activity) context, params, new BaseUiListener());
	}

	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(Object response) {
			// V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
			// 分享成功
			Log.e("share", "分享成功");
		}

		@Override
		public void onError(UiError e) {
			Log.e("share", "分享出现错误");
		}

		@Override
		public void onCancel() {
			// 分享被取消
			Log.e("share", "分享被取消");
		}
	}

	private void shareToLocal(Status status) {
		Intent intent = new Intent(activity, GroupListActivity.class);
		if (status == Status.friend_group) {
			intent.putExtra("type", "message");
		} else if (status == Status.square_group) {
			intent.putExtra("type", "share");
		}
		this.activity.startActivityForResult(intent, RESULT_SHAREVIEW);
	}

	private void shareToWechat(Status status) {
		if (status == Status.wechat_friend) {
			weChatShareUtils.shareMessageToWXFriends(bitmap, content, phone, gid, gsid);
		} else if (status == Status.wechat_circle) {
			weChatShareUtils.shareMessageToWXMoments(bitmap, content, phone, gid, gsid);
		}

	}
}
