package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.service.PushService;

public class SquareFragment extends BaseFragment implements OnClickListener {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	View mContentView;

	LayoutInflater mInflater;

	ListView mSqureMessageView;

	public SquareMessageAdapter mAdapter;

	EditText mViewBroadcast;
	View mButtonSend;

	List<Message> mSquareMessages;

	public String mCurrendSquareID = "91";

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		String flag = app.data.squareFlags.get(mCurrendSquareID);
		flag = flag == null ? "0" : flag;
		PushService.startSquareLongPull(getActivity(), mCurrendSquareID, flag);

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_square));
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_square, null);
		mViewBroadcast = (EditText) mContentView
				.findViewById(R.id.square_broadcast);
		mSqureMessageView = (ListView) mContentView
				.findViewById(R.id.squareMessages);

		mSquareMessages = app.data.squareMessages.get(mCurrendSquareID);
		mSquareMessages = mSquareMessages != null ? mSquareMessages
				: new ArrayList<Message>();

		mAdapter = new SquareMessageAdapter();

		mSqureMessageView.setAdapter(mAdapter);

		mButtonSend = mContentView.findViewById(R.id.button_send);
		mButtonSend.setOnClickListener(this);
		return mContentView;
	}

	public class SquareMessageAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			mSquareMessages = app.data.squareMessages.get(mCurrendSquareID);
			mSquareMessages = mSquareMessages != null ? mSquareMessages
					: new ArrayList<Message>();
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSquareMessages.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageHolder messageHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.fragment_square_message_item, null);
				messageHolder = new MessageHolder();
				messageHolder.nickName = (TextView) convertView
						.findViewById(R.id.nickName);
				messageHolder.message = (TextView) convertView
						.findViewById(R.id.message);
				convertView.setTag(messageHolder);
			} else {
				messageHolder = (MessageHolder) convertView.getTag();
			}

			messageHolder.nickName
					.setText(mSquareMessages.get(position).nickName);
			messageHolder.message
					.setText(mSquareMessages.get(position).content);

			return convertView;
		}
	}

	class MessageHolder {
		TextView nickName;
		TextView message;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_send:
			final String broadcast = mViewBroadcast.getText().toString();
			if (broadcast == null || broadcast.equals("")) {
				Alert.showMessage("广播内容不能为空");
				return;
			}
			mViewBroadcast.setText("");
			app.networkHandler.connection(new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.SQUARE_SENDSQUAREMESSAGE;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("nickName", app.data.user.nickName);
					params.put("gid", mCurrendSquareID);
					params.put("message",
							"{\"contentType\":\"text\",\"content\":\""
									+ broadcast + "\"}");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {

				}
			});
			mSqureMessageView.setSelection(mAdapter.getCount() - 1);
			break;
		default:
			break;
		}
	}

}
