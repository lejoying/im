package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.SquareMessageDetail;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.SquareContentView;
import com.lejoying.wxgs.activity.view.SquareContentView.OnItemClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.service.PushService;

public class SquareFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	View mContentView;

	LayoutInflater mInflater;

	ListView mSqureMessageView;

	public SquareContentView squareContentView;

	RelativeLayout currentSquareEssenceMessage;
	RelativeLayout currentSquareAllMessage;
	RelativeLayout currentSquareActivityMessage;
	RelativeLayout currentSquareShitsMessage;
	TextView currentSquareNoReadEssence;
	TextView currentSquareNoReadAll;
	TextView currentSquareNoReadActivity;
	TextView currentSquareNoReadShits;
	ImageView currentSquareStatusEssence;
	ImageView currentSquareStatusAll;
	ImageView currentSquareStatusActivity;
	ImageView currentSquareStatusShits;

	ImageView currentSquareMessageClassify;

	EditText mViewBroadcast;
	View mButtonSend;

	List<SquareMessage> mSquareMessages;

	public String mCurrentSquareID = "98";

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		String flag = app.data.squareFlags.get(mCurrentSquareID);
		flag = flag == null ? "0" : flag;
		PushService.startSquareLongPull(getActivity(), mCurrentSquareID, flag);
	}

	@Override
	public void onResume() {
		// CircleMenu.show();
		// CircleMenu.setPageName(getString(R.string.circlemenu_page_square));
		notifyViews();
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_square, null);
		squareContentView = (SquareContentView) mContentView
				.findViewById(R.id.stv_squrecontentview);
		currentSquareEssenceMessage = (RelativeLayout) mContentView
				.findViewById(R.id.current_square_essence_message);
		currentSquareAllMessage = (RelativeLayout) mContentView
				.findViewById(R.id.current_square_all_message);
		currentSquareActivityMessage = (RelativeLayout) mContentView
				.findViewById(R.id.current_square_activity_message);
		currentSquareShitsMessage = (RelativeLayout) mContentView
				.findViewById(R.id.current_square_shits_message);
		currentSquareStatusEssence = (ImageView) mContentView
				.findViewById(R.id.current_square_status_essence);
		currentSquareStatusAll = (ImageView) mContentView
				.findViewById(R.id.current_square_status_all);
		currentSquareStatusActivity = (ImageView) mContentView
				.findViewById(R.id.current_square_status_activity);
		currentSquareStatusShits = (ImageView) mContentView
				.findViewById(R.id.current_square_status_shits);
		currentSquareNoReadEssence = (TextView) mContentView
				.findViewById(R.id.current_square_noread_essence);
		currentSquareNoReadAll = (TextView) mContentView
				.findViewById(R.id.current_square_noread_all);
		currentSquareNoReadActivity = (TextView) mContentView
				.findViewById(R.id.current_square_noread_activity);
		currentSquareNoReadShits = (TextView) mContentView
				.findViewById(R.id.current_square_noread_shits);
		currentSquareMessageClassify = currentSquareStatusAll;
		final List<String> messages = app.data.squareMessages
				.get(mCurrentSquareID);
		final Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
				.get(mCurrentSquareID);
		if (messages != null) {
			app.UIHandler.post(new Runnable() {

				@Override
				public void run() {
					squareContentView.setSquareMessageList(messages,
							squareMessageMap);
					squareContentView.notifyDataSetChanged();
				}
			});
		}
		initEvent();
		return mContentView;
	}

	private void initEvent() {
		currentSquareShitsMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSquareMessageClassify != currentSquareStatusShits) {
					currentSquareMessageClassify.setVisibility(View.GONE);
					currentSquareStatusShits.setVisibility(View.VISIBLE);
					currentSquareMessageClassify = currentSquareStatusShits;
					// squareContentView.removeAllViews();
					List<String> ShitsMessageClassify = app.data.squareMessagesClassify
							.get(mCurrentSquareID).get("吐槽");
					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
							.get(mCurrentSquareID);
					if (ShitsMessageClassify == null) {
						ShitsMessageClassify = new ArrayList<String>();
					}
					if (squareMessageMap == null) {
						squareMessageMap = new HashMap<String, SquareMessage>();
					}
					// squareContentView.justSetSquareMessageList(
					// ShitsMessageClassify, squareMessageMap);
					squareContentView.setSquareMessageList(
							ShitsMessageClassify, squareMessageMap);
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							squareContentView.notifyDataSetChanged();
						}
					});
				}
			}
		});
		currentSquareActivityMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSquareMessageClassify != currentSquareStatusActivity) {
					currentSquareMessageClassify.setVisibility(View.GONE);
					currentSquareStatusActivity.setVisibility(View.VISIBLE);
					currentSquareMessageClassify = currentSquareStatusActivity;
					// squareContentView.removeAllViews();
					List<String> ActivityMessageClassify = app.data.squareMessagesClassify
							.get(mCurrentSquareID).get("活动");
					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
							.get(mCurrentSquareID);
					if (ActivityMessageClassify == null) {
						ActivityMessageClassify = new ArrayList<String>();
					}
					if (squareMessageMap == null) {
						squareMessageMap = new HashMap<String, SquareMessage>();
					}
					// squareContentView.justSetSquareMessageList(
					// ActivityMessageClassify, squareMessageMap);
					squareContentView.setSquareMessageList(
							ActivityMessageClassify, squareMessageMap);
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							squareContentView.notifyDataSetChanged();
						}
					});
				}
			}
		});
		currentSquareAllMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSquareMessageClassify != currentSquareStatusAll) {
					currentSquareMessageClassify.setVisibility(View.GONE);
					currentSquareStatusAll.setVisibility(View.VISIBLE);
					currentSquareMessageClassify = currentSquareStatusAll;
					// squareContentView.removeAllViews();
					List<String> AllMessages = app.data.squareMessages
							.get(mCurrentSquareID);
					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
							.get(mCurrentSquareID);
					if (AllMessages == null) {
						AllMessages = new ArrayList<String>();
					}
					if (squareMessageMap == null) {
						squareMessageMap = new HashMap<String, SquareMessage>();
					}
					squareContentView.setSquareMessageList(AllMessages,
							squareMessageMap);
					// squareContentView.justSetSquareMessageList(AllMessages,
					// squareMessageMap);
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							squareContentView.notifyDataSetChanged();
						}
					});
				}
			}
		});
		currentSquareEssenceMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSquareMessageClassify != currentSquareStatusEssence) {
					currentSquareMessageClassify.setVisibility(View.GONE);
					currentSquareStatusEssence.setVisibility(View.VISIBLE);
					currentSquareMessageClassify = currentSquareStatusEssence;
					// squareContentView.removeAllViews();
					List<String> EssenceMessageClassify = app.data.squareMessagesClassify
							.get(mCurrentSquareID).get("精华");
					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
							.get(mCurrentSquareID);
					if (EssenceMessageClassify == null) {
						EssenceMessageClassify = new ArrayList<String>();
					}
					if (squareMessageMap == null) {
						squareMessageMap = new HashMap<String, SquareMessage>();
					}
					squareContentView.setSquareMessageList(
							EssenceMessageClassify, squareMessageMap);
					// squareContentView.justSetSquareMessageList(
					// EssenceMessageClassify, squareMessageMap);
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							squareContentView.notifyDataSetChanged();
						}
					});
				}
			}
		});
		squareContentView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(SquareMessage message) {
				Intent intent = new Intent(getActivity(),
						SquareMessageDetail.class);
				intent.putExtra("mCurrentSquareID", mCurrentSquareID);
				intent.putExtra("gmid", message.gmid);
				startActivity(intent);
			}
		});
	}

	public void notifyViews() {
		squareContentView.notifyDataSetChanged();
	}

	public void search(final String phone) {

		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", "[\"" + phone + "\"]");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final Friend friend = JSONParser
							.generateFriendFromJSON(jData.getJSONArray(
									"accounts").getJSONObject(0));

					if (phone.equals(app.data.user.phone)) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
						app.dataHandler.exclude(new Modification() {
							@Override
							public void modifyData(Data data) {
								data.user.nickName = friend.nickName;
								data.user.mainBusiness = friend.mainBusiness;
								data.user.head = friend.head;
							}
						});
					} else if (app.data.friends.get(phone) != null) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
						app.dataHandler.exclude(new Modification() {

							@Override
							public void modifyData(Data data) {
								friend.messages = data.friends.get(phone).messages;
								data.friends.put(phone, friend);
							}
						});
					} else {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_TEMPFRIEND;
					}
					mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
					mMainModeManager
							.showNext(mMainModeManager.mBusinessCardFragment);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	class MessageHolder {
		TextView nickName;
		TextView message;
	}

}
