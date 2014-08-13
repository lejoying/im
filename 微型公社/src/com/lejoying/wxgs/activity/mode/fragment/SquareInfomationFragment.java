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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BusinessCardActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SquareInfomationFragment extends BaseFragment implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	View mContent;
	RelativeLayout backView, onlineMembers, squareBusinessCard, joinSquare;
	TextView onlineMembersCount;

	List<Friend> users;
	public static String mSquareID = "98";

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.f_square_infomation, null);
		backView = (RelativeLayout) mContent.findViewById(R.id.backView);
		onlineMembers = (RelativeLayout) mContent
				.findViewById(R.id.onlinemembers);
		squareBusinessCard = (RelativeLayout) mContent
				.findViewById(R.id.squarebusinesscard);
		joinSquare = (RelativeLayout) mContent.findViewById(R.id.joinsquare);
		onlineMembersCount = (TextView) mContent
				.findViewById(R.id.onlinememberscount);
		initData();
		initEvent();
		return mContent;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	private void initData() {
		if (!"".equals(mSquareID)) {
			getSquareOnLineUsers();
		}
	}

	private void initEvent() {
		backView.setOnClickListener(this);
		onlineMembers.setOnClickListener(this);
		squareBusinessCard.setOnClickListener(this);
		joinSquare.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(backView)) {
			mMainModeManager.back();
		} else if (v.equals(onlineMembers)) {
			mMainModeManager.mSquareOnLineUserFragment.users = users;
			mMainModeManager
					.showNext(mMainModeManager.mSquareOnLineUserFragment);
		} else if (v.equals(squareBusinessCard)) {
			Intent intent = new Intent(getActivity(),
					BusinessCardActivity.class);
			intent.putExtra("type", BusinessCardActivity.TYPE_SQUARE);
			intent.putExtra("square", mSquareID);
			startActivity(intent);
		} else if (v.equals(joinSquare)) {

		}

	}

	public void getSquareOnLineUsers() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREUSERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mSquareID);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				users = new ArrayList<Friend>();
				try {
					users = JSONParser.generateFriendsFromJSON(jData
							.getJSONArray("users"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				app.UIHandler.post(new Runnable() {

					@Override
					public void run() {
						onlineMembersCount.setText("广场用户(" + users.size() + ")");
					}
				});
			}
		});
	}
}
