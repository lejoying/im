package com.lejoying.wxgs.activity.mode.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BusinessCardActivity;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class GroupSharePraisesFragment extends BaseFragment {
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	private View mContent;

	RelativeLayout rl_back;
	ListView lv_praises;
	float height, width, dip, density;
	public static List<String> praiseUsers;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mInflater = inflater;
		mContent = mInflater
				.inflate(R.layout.fragment_groupshare_praises, null);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		rl_back = (RelativeLayout) mContent.findViewById(R.id.rl_back);
		lv_praises = (ListView) mContent.findViewById(R.id.lv_praises);

		lv_praises.setAdapter(new GroupSharePraisesAdapter());

		rl_back.setOnTouchListener(new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(
					getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							getFragmentManager().popBackStack();
							return true;
						}
					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_back.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					rl_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				return backviewDetector.onTouchEvent(event);
			}
		});

		return mContent;
	}

	class GroupSharePraisesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return praiseUsers.size();
		}

		@Override
		public Object getItem(int position) {
			return praiseUsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GroupSharePraisesHolder holder;
			final Friend friend = app.data.groupFriends
					.get(praiseUsers.get(position));
			if (convertView == null) {
				holder = new GroupSharePraisesHolder();
				convertView = mInflater.inflate(
						R.layout.groupshare_commentchild, null);
				holder.head = (ImageView) convertView.findViewById(R.id.head);
				holder.name = (TextView) convertView.findViewById(R.id.receive);
				holder.sign = (TextView) convertView.findViewById(R.id.content);
				holder.reply = (TextView) convertView.findViewById(R.id.reply);

				convertView.setTag(holder);
			} else {
				holder = (GroupSharePraisesHolder) convertView.getTag();
			}
			// convertView.setPadding(5, 0, 5, 0);
			holder.reply.setVisibility(View.GONE);
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			holder.sign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			holder.name.setTextColor(Color.WHITE);
			holder.sign.setTextColor(Color.GRAY);
			holder.sign.setSingleLine();
			convertView.setPadding(0, (int) (10 * density + 0.5f), 0, 0);
			RelativeLayout.LayoutParams signparams = (RelativeLayout.LayoutParams) holder.sign
					.getLayoutParams();
			signparams.topMargin = (int) (5 * density + 0.5f);
			RelativeLayout.LayoutParams headparams = (RelativeLayout.LayoutParams) holder.head
					.getLayoutParams();
			headparams.width = (int) (40 * density + 0.5f);
			headparams.height = (int) (40 * density + 0.5f);

			app.fileHandler.getHeadImage(friend.head, friend.sex,
					new FileResult() {
						@Override
						public void onResult(String where, Bitmap bitmap) {
							holder.head.setImageBitmap(bitmap);
						}
					});

			holder.name.setText(friend.nickName);

			holder.sign.setText(friend.mainBusiness);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							BusinessCardActivity.class);
					if (friend.phone.equals(app.data.user.phone)) {
						intent.putExtra("type", BusinessCardActivity.TYPE_SELF);
					} else if (app.data.friends.get(friend.phone) != null) {
						intent.putExtra("type",
								BusinessCardActivity.TYPE_FRIEND);
					} else {
						intent.putExtra("type",
								BusinessCardActivity.TYPE_TEMPFRIEND);
						intent.putExtra("friend", friend);
					}
					intent.putExtra("phone", friend.phone);
					startActivity(intent);
				}
			});
			return convertView;
		}

		class GroupSharePraisesHolder {
			ImageView head;
			TextView name, sign, reply;
		}
	}
}
