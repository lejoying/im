package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.controller.SharePraiseusersController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.MCImageUtils;

public class SharePraiseusersView {

	public Data data = Data.getInstance();
	public String tag = "SharePraiseusersView";

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	public RelativeLayout backView;
	public ListView listView;

	public float screentHeight, screentWidth, screenDip, screenDensity;
	public LayoutInflater mInflater;

	public Bitmap bitmap;

	public PraiseUsersAdapter praiseUsersAdapter;

	public SharePraiseusersView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		thisView = this;
	}

	public void initView() {
		thisActivity.setContentView(R.layout.share_message_praiseusers);

		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		TextView backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		backTitleView.setText("称赞者");

		listView = (ListView) thisActivity.findViewById(R.id.praiseusersContent);

		DisplayMetrics dm = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenDensity = dm.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screentHeight = dm.heightPixels;
		screentWidth = dm.widthPixels;

		mInflater = thisActivity.getLayoutInflater();

		Resources resources = thisActivity.getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		praiseUsersAdapter = new PraiseUsersAdapter();
		listView.setAdapter(praiseUsersAdapter);
	}

	public class PraiseUsersAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return thisController.praiseusersList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final SharePraisesHolder holder;
			final Friend friend = data.relationship.friendsMap.get(thisController.praiseusersList.get(position));
			if (convertView == null) {
				holder = new SharePraisesHolder();
				convertView = mInflater.inflate(R.layout.groupshare_commentchild, null);
				holder.head = (ImageView) convertView.findViewById(R.id.head);
				holder.name = (TextView) convertView.findViewById(R.id.receive);
				holder.sign = (TextView) convertView.findViewById(R.id.content);
				holder.reply = (TextView) convertView.findViewById(R.id.reply);

				convertView.setTag(holder);
			} else {
				holder = (SharePraisesHolder) convertView.getTag();
			}
			// convertView.setPadding(5, 0, 5, 0);
			holder.reply.setVisibility(View.GONE);
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			holder.sign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			holder.name.setTextColor(Color.WHITE);
			holder.sign.setTextColor(Color.GRAY);
			holder.sign.setSingleLine();
			convertView.setPadding(0, (int) (10 * screenDensity + 0.5f), 0, 0);
			RelativeLayout.LayoutParams signparams = (RelativeLayout.LayoutParams) holder.sign.getLayoutParams();
			signparams.topMargin = (int) (5 * screenDensity + 0.5f);
			RelativeLayout.LayoutParams headparams = (RelativeLayout.LayoutParams) holder.head.getLayoutParams();
			headparams.width = (int) (40 * screenDensity + 0.5f);
			headparams.height = (int) (40 * screenDensity + 0.5f);

			holder.head.setImageBitmap(bitmap);

			holder.name.setText(friend.nickName);

			holder.sign.setText(friend.mainBusiness);

			return convertView;
		}

		class SharePraisesHolder {
			ImageView head;
			TextView name, sign, reply;
		}
	}
}
