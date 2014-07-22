package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class InviteOrSelectedFriendActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();

	LayoutInflater mInflater;

	EditText findFriendView;
	ListView friendsListView;

	InvitaFriendAdapter invitaFriendAdapter;

	List<String> friends = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_selected_friend);
		mInflater = this.getLayoutInflater();
		findFriendView = (EditText) findViewById(R.id.et_findfriend);
		friendsListView = (ListView) findViewById(R.id.ls_members);
		initData();
		invitaFriendAdapter = new InvitaFriendAdapter();
		friendsListView.setAdapter(invitaFriendAdapter);
	}

	private void initData() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < app.data.circles.size(); i++) {
			list.addAll(app.data.circlesMap.get(app.data.circles.get(i)).phones);
		}
		Map<String, Friend> friendsMap = app.data.friends;
		for (int i = 0; i < list.size(); i++) {
			Friend friend = friendsMap.get(list.get(i));
			friends.add(getFirstSpell(friend.nickName) + "#" + friend.phone);
		}
		Collections.sort(friends);
	}

	public static String getFirstSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (char curchar : arr) {
			if (curchar > 128) {
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							curchar, defaultFormat);
					if (temp != null) {
						pybf.append(temp[0]);// .charAt(0)
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(curchar);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

	class InvitaFriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return friends.size();
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
			FriendHolder friendHolder = null;
			if (convertView == null) {
				friendHolder = new FriendHolder();
				convertView = mInflater.inflate(
						R.layout.activity_invite_selected_friend_item, null);
				friendHolder.nickNameType = (LinearLayout) convertView
						.findViewById(R.id.ll_nickNameType);
				friendHolder.nickNameFirst = (TextView) convertView
						.findViewById(R.id.tv_nickNameFirst);
				friendHolder.friendHead = (ImageView) convertView
						.findViewById(R.id.iv_friendHead);
				friendHolder.friendNickName = (TextView) convertView
						.findViewById(R.id.tv_friendNickName);
				convertView.setTag(friendHolder);
			} else {
				friendHolder = (FriendHolder) convertView.getTag();
			}

			String content = friends.get(position);
			String phone = content.substring(content.lastIndexOf("#") + 1);
			if (position == 0) {
				friendHolder.nickNameType.setVisibility(View.VISIBLE);
				friendHolder.nickNameFirst.setText(content.substring(0, 1)
						.toUpperCase());
			} else {
				String content0 = friends.get(position - 1);
				if (!content0.subSequence(0, 1).equals(
						content.subSequence(0, 1))) {
					friendHolder.nickNameType.setVisibility(View.VISIBLE);
					friendHolder.nickNameFirst.setText(content.substring(0, 1)
							.toUpperCase());
				} else {
					friendHolder.nickNameType.setVisibility(View.GONE);
				}
			}
			Friend friend = app.data.friends.get(phone);
			final FriendHolder friendHolder0 = friendHolder;
			friendHolder.friendNickName.setText(friend.nickName);
			app.fileHandler.getHeadImage(friend.head, friend.sex,
					new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							friendHolder0.friendHead.setImageBitmap(bitmap);
						}
					});
			return convertView;
		}
	}

	class FriendHolder {
		LinearLayout nickNameType;
		TextView nickNameFirst;

		ImageView friendHead;
		TextView friendNickName;
	}
}
