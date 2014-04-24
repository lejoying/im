package com.lejoying.wxgs.app.handler;

import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;

public class EventHandler {
	MainApplication app;

	public void initialize(MainApplication app) {
		this.app = app;
	}

	public void handleEvent(final Event event) {
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
							if (MainActivity.instance.mMainMode.mChatMessagesFragment
									.isAdded()) {
								MainActivity.instance.mMainMode.mChatMessagesFragment
										.notifyViews();
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

		} else if (event.event.equals("friendaccept")) {
			// String phone = (String) event.eventContent;
			DataUtil.getCircles(new GetDataListener() {

				@Override
				public void getSuccess() {
					if (MainActivity.instance.mMainMode.mCirclesFragment
							.isAdded()) {
						MainActivity.instance.mMainMode.mCirclesFragment
								.notifyViews();
					}
					System.out.println("wxgs----------" + event.event);
				}
			});
		} else if (event.event.equals("groupinformationchanged")
				|| event.event.equals("groupmemberchanged")
				|| event.event.equals("groupstatuschanged")) {
			DataUtil.getGroups(new GetDataListener() {

				@Override
				public void getSuccess() {
					if (MainActivity.instance.mMainMode.mGroupFragment
							.isAdded()) {
						MainActivity.instance.mMainMode.mGroupFragment
								.notifyViews();
					}
					System.out.println("wxgs----------" + event.event);
				}
			});
		} else if (event.event.equals("friendstatuschanged")) {
			final String phone = (String) event.eventContent;
			final String operation = (String) event.operation;
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					data.friends.get(phone).friendStatus = operation;
					System.out.println("wxgs----------" + event.event);
				}

			});
		} else if (event.event.equals("userinformationchanged")) {
			DataUtil.getUser(new GetDataListener() {

				@Override
				public void getSuccess() {
					// TODO Auto-generated method stub
					System.out.println("wxgs----------" + event.event);
				}
			});
		}
	}
}
