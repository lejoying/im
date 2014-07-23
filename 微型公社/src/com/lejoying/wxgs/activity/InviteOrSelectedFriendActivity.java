package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class InviteOrSelectedFriendActivity extends Activity implements
		OnClickListener {

	static int INVITA_FRIEND_GROUP = 0x01;// Invite friends to add to the group
	static int RECOMMEND_FRIEND_GROUP = 0x02;// Recommend group to friends
	static int FORWARD_MESSAGE_FRIEND = 0x03;// Forwarding messages to friends

	int currentOperationType = 0;

	MainApplication app = MainApplication.getMainApplication();

	LayoutInflater mInflater;

	LinearLayout backView;
	TextView invitaFriendCompleteView;
	EditText findFriendView;
	ListView friendsListView;

	InvitaFriendAdapter invitaFriendAdapter;

	List<String> friends = new ArrayList<String>();

	ArrayList<String> invitaFriends = new ArrayList<String>();

	Group currentGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		if (type == -1) {
			return;
		} else {
			currentOperationType = type;
		}
		String gid = getIntent().getStringExtra("gid");
		if (!"".equals(gid) && gid != null) {
			currentGroup = app.data.groupsMap.get(gid);
		}
		setContentView(R.layout.activity_invite_selected_friend);
		mInflater = this.getLayoutInflater();
		findFriendView = (EditText) findViewById(R.id.et_findfriend);
		friendsListView = (ListView) findViewById(R.id.ls_members);
		backView = (LinearLayout) findViewById(R.id.ll_invitafriendebackview);
		invitaFriendCompleteView = (TextView) findViewById(R.id.tv_invitafriendcommit);
		initData();
		initEvent();
		invitaFriendAdapter = new InvitaFriendAdapter(friends);
		friendsListView.setAdapter(invitaFriendAdapter);
		backView.setOnClickListener(this);
		invitaFriendCompleteView.setOnClickListener(this);
	}

	private void initEvent() {
		findFriendView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String findContent = s.toString().trim();
				List<String> findFriends = new ArrayList<String>();
				List<String> findStrs = new ArrayList<String>();
				if (!"".equals(findContent)) {
					String[] strs = findContent.split(" ");
					for (int i = 0; i < strs.length; i++) {
						if (!"".equals(strs[i].trim())) {
							findStrs.add(strs[i].trim());
						}
					}
					for (int j = 0; j < friends.size(); j++) {
						String content = friends.get(j);
						String phone = content.substring(content
								.lastIndexOf("#") + 1);
						Friend friend = app.data.friends.get(phone);
						String nickName = friend.nickName;
						A: for (int h = 0; h < findStrs.size(); h++) {
							boolean flag = nickName.contains(findStrs.get(h));
							if (!flag) {
								break A;
							}
							if (h == findStrs.size() - 1) {
								findFriends.add(content);
							}
						}
					}
					invitaFriendAdapter = new InvitaFriendAdapter(findFriends);
					friendsListView.setAdapter(invitaFriendAdapter);
				} else {
					invitaFriendAdapter = new InvitaFriendAdapter(friends);
					friendsListView.setAdapter(invitaFriendAdapter);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_invitafriendebackview:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.tv_invitafriendcommit:
			if (currentOperationType == INVITA_FRIEND_GROUP) {
				Intent intent = new Intent(InviteOrSelectedFriendActivity.this,
						GroupMemberManageActivity.class);
				intent.putStringArrayListExtra("invitafriends", invitaFriends);
				setResult(Activity.RESULT_OK, intent);
			}
			finish();
			break;

		default:
			break;
		}

	}

	private void initData() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < app.data.circles.size(); i++) {
			list.addAll(app.data.circlesMap.get(app.data.circles.get(i)).phones);
		}
		Map<String, Friend> friendsMap = app.data.friends;
		List<String> groupMembers = currentGroup.members;

		for (int i = 0; i < list.size(); i++) {
			Friend friend = friendsMap.get(list.get(i));
			if (currentOperationType == INVITA_FRIEND_GROUP) {
				if (groupMembers.contains(friend.phone)) {
					continue;
				}
			}
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

		List<String> friends;

		public InvitaFriendAdapter(List<String> friends) {
			this.friends = friends;
		}

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

		@SuppressLint("DefaultLocale")
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
				friendHolder.friendInfoLLView = (LinearLayout) convertView
						.findViewById(R.id.ll_friendinfo);
				friendHolder.friendHeadStatus = (ImageView) convertView
						.findViewById(R.id.iv_friendHeadStatus);
				convertView.setTag(friendHolder);
			} else {
				friendHolder = (FriendHolder) convertView.getTag();
			}

			String content = friends.get(position);
			String phone = content.substring(content.lastIndexOf("#") + 1);
			if (position == 0) {
				friendHolder.nickNameType.setVisibility(View.VISIBLE);
				String nickNameFirString = content.substring(0, 1);
				nickNameFirString = nickNameFirString.toUpperCase(Locale
						.getDefault());
				friendHolder.nickNameFirst.setText(nickNameFirString);
			} else {
				String content0 = friends.get(position - 1);
				if (!content0.subSequence(0, 1).equals(
						content.subSequence(0, 1))) {
					friendHolder.nickNameType.setVisibility(View.VISIBLE);
					String nickNameFirString = content.substring(0, 1);
					nickNameFirString = nickNameFirString.toUpperCase(Locale
							.getDefault());
					friendHolder.nickNameFirst.setText(nickNameFirString);
				} else {
					friendHolder.nickNameType.setVisibility(View.GONE);
				}
			}

			final Friend friend = app.data.friends.get(phone);
			final FriendHolder friendHolder0 = friendHolder;
			if (!"".equals(friend.alias)) {
				friendHolder.friendNickName.setText(friend.alias);
			} else {
				friendHolder.friendNickName.setText(friend.nickName);
			}
			app.fileHandler.getHeadImage(friend.head, friend.sex,
					new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							friendHolder0.friendHead.setImageBitmap(bitmap);
						}
					});

			if (invitaFriends.contains(friend.phone)) {
				friendHolder.friendHeadStatus.setVisibility(View.VISIBLE);
			} else {
				friendHolder.friendHeadStatus.setVisibility(View.GONE);
			}

			friendHolder.friendInfoLLView
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (friendHolder0.friendHeadStatus.getVisibility() == View.GONE) {
								friendHolder0.friendHeadStatus
										.setVisibility(View.VISIBLE);
								invitaFriends.add(friend.phone);
							} else {
								friendHolder0.friendHeadStatus
										.setVisibility(View.GONE);
								invitaFriends.remove(friend.phone);
							}
						}
					});
			return convertView;
		}
	}

	class FriendHolder {
		LinearLayout nickNameType;
		TextView nickNameFirst;

		ImageView friendHead;
		ImageView friendHeadStatus;
		TextView friendNickName;

		LinearLayout friendInfoLLView;
	}
}
