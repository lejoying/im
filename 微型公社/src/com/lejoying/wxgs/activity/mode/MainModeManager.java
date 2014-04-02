package com.lejoying.wxgs.activity.mode;

import android.support.v4.app.FragmentManager;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.fragment.AddFriendFragment;
import com.lejoying.wxgs.activity.mode.fragment.BusinessCardFragment;
import com.lejoying.wxgs.activity.mode.fragment.ChatFriendFragment;
import com.lejoying.wxgs.activity.mode.fragment.ChatGroupFragment;
import com.lejoying.wxgs.activity.mode.fragment.ChatMessagesFragment;
import com.lejoying.wxgs.activity.mode.fragment.CirclesFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupManagerFragment;
import com.lejoying.wxgs.activity.mode.fragment.ModifyFragment;
import com.lejoying.wxgs.activity.mode.fragment.NewFriendsFragment;
import com.lejoying.wxgs.activity.mode.fragment.ScanQRCodeFragment;
import com.lejoying.wxgs.activity.mode.fragment.SearchFriendFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;

public class MainModeManager extends BaseModeManager {

	boolean isInit;
	FragmentManager mFragmentManager;

	int mContentID = R.id.fragmentContent;
	// main
	public CirclesFragment mCirclesFragment;
	public GroupFragment mGroupFragment;
	public SquareFragment mSquareFragment;

	//
	public ScanQRCodeFragment mScanQRCodeFragment;
	public SearchFriendFragment mSearchFriendFragment;
	public BusinessCardFragment mBusinessCardFragment;
	public ChatMessagesFragment mChatMessagesFragment;
	public ChatFriendFragment mChatFragment;
	public ChatGroupFragment mChatGroupFragment;
	public NewFriendsFragment mNewFriendsFragment;
	public AddFriendFragment mAddFriendFragment;
	public GroupManagerFragment mGroupManagerFragment;
	public ModifyFragment mModifyFragment;

	public MainModeManager(MainActivity activity) {
		super(activity);
		mFragmentManager = activity.getSupportFragmentManager();
	}

	@Override
	public void initialize() {
		if (!isInit) {
			isInit = true;
			// main
			mCirclesFragment = new CirclesFragment();
			mCirclesFragment.setMode(this);
			mGroupFragment = new GroupFragment();
			mGroupFragment.setMode(this);
			mSquareFragment = new SquareFragment();
			mSquareFragment.setMode(this);

			//
			mScanQRCodeFragment = new ScanQRCodeFragment();
			mScanQRCodeFragment.setMode(this);
			mSearchFriendFragment = new SearchFriendFragment();
			mSearchFriendFragment.setMode(this);
			mBusinessCardFragment = new BusinessCardFragment();
			mBusinessCardFragment.setMode(this);
			mChatFragment = new ChatFriendFragment();
			mChatFragment.setMode(this);
			mChatMessagesFragment = new ChatMessagesFragment();
			mChatMessagesFragment.setMode(this);
			mChatGroupFragment = new ChatGroupFragment();
			mChatGroupFragment.setMode(this);
			mNewFriendsFragment = new NewFriendsFragment();
			mNewFriendsFragment.setMode(this);
			mAddFriendFragment = new AddFriendFragment();
			mAddFriendFragment.setMode(this);
			mGroupManagerFragment = new GroupManagerFragment();
			mGroupManagerFragment.setMode(this);
			mModifyFragment = new ModifyFragment();
			mModifyFragment.setMode(this);

		}
	}

	@Override
	public void release() {
		isInit = false;
		super.release();
	}

}
