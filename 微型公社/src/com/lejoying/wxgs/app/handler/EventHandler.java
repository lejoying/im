package com.lejoying.wxgs.app.handler;

import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Event;

public class EventHandler {
	MainApplication app;

	public void initialize(MainApplication app) {
		this.app = app;
	}

	public void handleEvent(Event event) {
		if (event == null) {
			return;
		}
		if (event.event.equals("message")) {
			try {
				DataUtil.getMessages(new GetDataListener() {
					@Override
					public void getSuccess() {
						if (MainActivity.instance != null
								&& MainActivity.instance.mode
										.equals(MainActivity.MODE_MAIN)) {
							if (MainActivity.instance.mMainMode.mCirclesFragment
									.isAdded()) {
								// TODO refresh
								MainActivity.instance.mMainMode.mCirclesFragment
										.notifyViews();
							}
							if (MainActivity.instance.mMainMode.mGroupFragment
									.isAdded()) {
								MainActivity.instance.mMainMode.mGroupFragment
										.notifyViews();
							}
							if (MainActivity.instance.mMainMode.mChatFragment
									.isAdded()) {
								MainActivity.instance.mMainMode.mChatFragment.mAdapter
										.notifyDataSetChanged();
							}
							if (MainActivity.instance.mMainMode.mChatGroupFragment
									.isAdded()) {
								MainActivity.instance.mMainMode.mChatGroupFragment.mAdapter
										.notifyDataSetChanged();
							}
						}
					}

					@Override
					public void getFailed() {
						// TODO Auto-generated method stub

					}
				});
			} catch (Exception e) {

			}
		} else if (event.event.equals("newfriend")) {
			DataUtil.getAskFriends(new GetDataListener() {
				@Override
				public void getSuccess() {
					// TODO refresh
					if (MainActivity.instance != null
							&& MainActivity.instance.mode
									.equals(MainActivity.MODE_MAIN)) {
						if (MainActivity.instance != null
								&& MainActivity.instance.mode
										.equals(MainActivity.MODE_MAIN)) {
							if (MainActivity.instance.mMainMode.mCirclesFragment
									.isAdded()) {
								// TODO refresh
								MainActivity.instance.mMainMode.mCirclesFragment
										.notifyViews();
							}
							if (MainActivity.instance.mMainMode.mNewFriendsFragment
									.isAdded()) {
								MainActivity.instance.mMainMode.mNewFriendsFragment.mAdapter
										.notifyDataSetChanged();
							}
						}
					}
				}

				@Override
				public void getFailed() {
					// TODO Auto-generated method stub

				}
			});

		}
	}
}
