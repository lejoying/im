package com.open.welinks;

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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.utils.MCImageUtils;

public class InviteFriendActivity extends Activity implements OnClickListener {

	public Data data = Data.getInstance();
	public String tag = "InviteFriendActivity";
	public static int INVITA_FRIEND_GROUP = 0x01;// Invite friends to add to the group
	public static int RECOMMEND_FRIEND_GROUP = 0x02;// Recommend group to friends
	public static int FORWARD_MESSAGE_FRIEND = 0x03;// Forwarding messages to friends

	public int currentOperationType = 0;

	public LayoutInflater mInflater;

	public LinearLayout backView;
	public TextView invitaFriendCompleteView;
	public EditText findFriendView;
	public ListView friendsListView;

	public InvitaFriendAdapter invitaFriendAdapter;

	public List<String> friends = new ArrayList<String>();

	public ArrayList<String> invitaFriends = new ArrayList<String>();

	public Group currentGroup;

	public Bitmap defaultBitmapHead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		if (type == -1) {
			return;
		} else {
			currentOperationType = type;
		}
		String gid = data.localStatus.localData.currentSelectedGroup;
		if (gid != null && !"".equals(gid)) {
			currentGroup = data.relationship.groupsMap.get(gid);
			if (currentGroup == null) {
				finish();
			} else {
				// thisView.showCurrentGroupMembers();
			}
		} else {
			finish();
		}
		setContentView(R.layout.activity_group_invite_selected_friend);
		mInflater = this.getLayoutInflater();
		findFriendView = (EditText) findViewById(R.id.et_findfriend);
		friendsListView = (ListView) findViewById(R.id.ls_members);
		backView = (LinearLayout) findViewById(R.id.backView);
		invitaFriendCompleteView = (TextView) findViewById(R.id.confirmButton);
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
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
						String phone = content.substring(content.lastIndexOf("#") + 1);
						Friend friend = data.relationship.friendsMap.get(phone);
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
		case R.id.backView:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.confirmButton:
			if (currentOperationType == INVITA_FRIEND_GROUP) {
				Intent intent = new Intent(InviteFriendActivity.this, GroupMemberManageActivity.class);
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
		// default head
		Resources resources = getResources();
		defaultBitmapHead = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		defaultBitmapHead = MCImageUtils.getCircleBitmap(defaultBitmapHead, true, 5, Color.WHITE);

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < data.relationship.circles.size(); i++) {
			// list.addAll(app.data.circlesMap.get(app.data.circles.get(i)).phones);
			list.addAll(data.relationship.circlesMap.get(data.relationship.circles.get(i)).friends);
		}
		Map<String, Friend> friendsMap = data.relationship.friendsMap;
		List<String> groupMembers = currentGroup.members;

		List<String> alreadyAddFriend = new ArrayList<String>();

		for (int i = 0; i < list.size(); i++) {
			Friend friend = friendsMap.get(list.get(i));
			if (currentOperationType == INVITA_FRIEND_GROUP) {
				if (groupMembers.contains(friend.phone) || alreadyAddFriend.contains(friend.phone)) {
					continue;
				}
			}
			alreadyAddFriend.add(friend.phone);
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
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);
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
				convertView = mInflater.inflate(R.layout.activity_invite_selected_friend_item, null);
				friendHolder.nickNameType = (LinearLayout) convertView.findViewById(R.id.ll_nickNameType);
				friendHolder.nickNameFirst = (TextView) convertView.findViewById(R.id.tv_nickNameFirst);
				friendHolder.friendHead = (ImageView) convertView.findViewById(R.id.iv_friendHead);
				friendHolder.friendNickName = (TextView) convertView.findViewById(R.id.tv_friendNickName);
				friendHolder.friendInfoLLView = (LinearLayout) convertView.findViewById(R.id.ll_friendinfo);
				friendHolder.friendHeadStatus = (ImageView) convertView.findViewById(R.id.iv_friendHeadStatus);
				convertView.setTag(friendHolder);
			} else {
				friendHolder = (FriendHolder) convertView.getTag();
			}

			String content = friends.get(position);
			String phone = content.substring(content.lastIndexOf("#") + 1);
			if (position == 0) {
				friendHolder.nickNameType.setVisibility(View.VISIBLE);
				String nickNameFirString = content.substring(0, 1);
				nickNameFirString = nickNameFirString.toUpperCase(Locale.getDefault());
				friendHolder.nickNameFirst.setText(nickNameFirString);
			} else {
				String content0 = friends.get(position - 1);
				if (!content0.subSequence(0, 1).equals(content.subSequence(0, 1))) {
					friendHolder.nickNameType.setVisibility(View.VISIBLE);
					String nickNameFirString = content.substring(0, 1);
					nickNameFirString = nickNameFirString.toUpperCase(Locale.getDefault());
					friendHolder.nickNameFirst.setText(nickNameFirString);
				} else {
					friendHolder.nickNameType.setVisibility(View.GONE);
				}
			}

			final Friend friend = data.relationship.friendsMap.get(phone);
			final FriendHolder friendHolder0 = friendHolder;
			if (!"".equals(friend.alias)) {
				friendHolder.friendNickName.setText(friend.alias);
			} else {
				friendHolder.friendNickName.setText(friend.nickName);
			}
			friendHolder0.friendHead.setImageBitmap(defaultBitmapHead);

			if (invitaFriends.contains(friend.phone)) {
				friendHolder.friendHeadStatus.setVisibility(View.VISIBLE);
			} else {
				friendHolder.friendHeadStatus.setVisibility(View.GONE);
			}

			friendHolder.friendInfoLLView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (friendHolder0.friendHeadStatus.getVisibility() == View.GONE) {
						friendHolder0.friendHeadStatus.setVisibility(View.VISIBLE);
						invitaFriends.add(friend.phone);
					} else {
						friendHolder0.friendHeadStatus.setVisibility(View.GONE);
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
