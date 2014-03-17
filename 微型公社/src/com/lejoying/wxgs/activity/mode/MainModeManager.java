package com.lejoying.wxgs.activity.mode;

import android.support.v4.app.FragmentManager;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.fragment.BusinessCardFragment;
import com.lejoying.wxgs.activity.mode.fragment.ChatFragment;
import com.lejoying.wxgs.activity.mode.fragment.CirclesFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupFragment;
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
	public ChatFragment mChatFragment;
	public NewFriendsFragment mNewFriendsFragment;

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
			mChatFragment = new ChatFragment();
			mChatFragment.setMode(this);
			mNewFriendsFragment = new NewFriendsFragment();
			mNewFriendsFragment.setMode(this);
		}
	}

}
