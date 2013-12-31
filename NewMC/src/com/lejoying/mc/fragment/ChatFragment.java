package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class ChatFragment extends BaseListFragment {

	App app = App.getInstance();

	private View mContent;
	public ChatAdapter mAdapter;

	LayoutInflater mInflater;

	View iv_send;
	View iv_more;
	EditText et_message;
	RelativeLayout rl_chatbottom;

	int beforeHeight;
	int beforeLineHeight;

	final int MAXTYPE_COUNT = 3;

	Bitmap headman;
	Bitmap headwoman;

	public static ChatFragment instance;

	public ChatFragment() {
		mAdapter = new ChatAdapter();
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResume() {
		app.mark = app.chatFragment;
		instance = this;
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_chat, null);
		mMCFragmentManager.setNotifyListener(new NotifyListener() {
			@Override
			public void notifyDataChanged(int notify) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		});
		app.nowChatFriend.notReadMessagesCount = 0;
		iv_send = mContent.findViewById(R.id.iv_send);
		iv_more = mContent.findViewById(R.id.iv_more);
		et_message = (EditText) mContent.findViewById(R.id.et_message);
		rl_chatbottom = (RelativeLayout) mContent
				.findViewById(R.id.rl_chatbottom);

		et_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (beforeHeight == 0) {
					beforeHeight = et_message.getHeight();
				}
				if (beforeLineHeight == 0) {
					beforeLineHeight = et_message.getLineHeight();
				}

				LayoutParams etparams = et_message.getLayoutParams();
				LayoutParams rlparams = rl_chatbottom.getLayoutParams();

				int lineCount = et_message.getLineCount();

				switch (lineCount) {
				case 3:
					etparams.height = beforeHeight;
					rlparams.height = beforeHeight;
					break;
				case 4:
					etparams.height = beforeHeight + beforeLineHeight;
					rlparams.height = beforeHeight + beforeLineHeight;
					break;
				case 5:
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
					break;

				default:
					break;
				}
				if (lineCount > 5) {
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
				}
				et_message.setLayoutParams(etparams);
				rl_chatbottom.setLayoutParams(rlparams);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		iv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = et_message.getText().toString();
				et_message.setText("");
				if (message != null && !message.equals("")) {
					Bundle params = generateParams(message);
					MCNetTools.ajax(getActivity(), API.MESSAGE_SEND, params,
							MCHttpTools.SEND_POST, 5000,
							new ResponseListener() {

								@Override
								public void success(JSONObject data) {
									System.out.println("success");
									app.isDataChanged = true;
									if (app.data.user.flag.equals("none")) {
										app.data.user.flag = String.valueOf(1);
									} else {
										app.data.user.flag = String
												.valueOf(Integer.valueOf(
														app.data.user.flag)
														.intValue() + 1);
									}
								}

								@Override
								public void noInternet() {
									// TODO Auto-generated method stub

								}

								@Override
								public void failed() {
									// TODO Auto-generated method stub

								}

								@Override
								public void connectionCreated(
										HttpURLConnection httpURLConnection) {
									// TODO Auto-generated method stub

								}
							});
				}
			}
		});

		headman = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);
		headwoman = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_woman), true, 10, Color.WHITE);

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}

	public class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.nowChatFriend.messages.size();
		}

		@Override
		public Object getItem(int position) {
			return app.nowChatFriend.messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return ((Message) getItem(position)).type;
		}

		@Override
		public int getViewTypeCount() {
			return MAXTYPE_COUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageHolder messageHolder;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					convertView = mInflater.inflate(R.layout.f_chat_item_right,
							null);
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					convertView = mInflater.inflate(R.layout.f_chat_item_left,
							null);
					break;

				default:
					break;
				}
				messageHolder = new MessageHolder();
				messageHolder.iv_head = (ImageView) convertView
						.findViewById(R.id.iv_head);
				messageHolder.tv_chat = (TextView) convertView
						.findViewById(R.id.tv_chat);
				messageHolder.tv_chattime = (TextView) convertView
						.findViewById(R.id.tv_chattime);
				convertView.setTag(messageHolder);
			} else {
				messageHolder = (MessageHolder) convertView.getTag();
			}
			messageHolder.tv_chat
					.setText(((Message) getItem(position)).content);
			switch (type) {
			case Message.MESSAGE_TYPE_SEND:
				messageHolder.iv_head.setImageBitmap(headman);
				break;
			case Message.MESSAGE_TYPE_RECEIVE:
				messageHolder.iv_head.setImageBitmap(headwoman);
				break;
			default:
				break;
			}
			return convertView;
		}
	}

	class MessageHolder {
		ImageView iv_head;
		TextView tv_chat;
		TextView tv_chattime;
	}

	public Bundle generateParams(String text) {
		Message message = new Message();
		message.type = Message.MESSAGE_TYPE_SEND;
		message.content = text;
		message.messageType = "text";
		message.time = String.valueOf(new Date().getTime());
		app.nowChatFriend.messages.add(message);
		mAdapter.notifyDataSetChanged();
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		JSONArray jFriends = new JSONArray();
		jFriends.put(app.nowChatFriend.phone);
		params.putString("phoneto", jFriends.toString());
		JSONObject jMessage = new JSONObject();
		try {
			jMessage.put("type", "text");
			jMessage.put("content", "{text:\"" + text + "\"}");
			params.putString("message", jMessage.toString());
		} catch (JSONException e) {
		}

		return params;
	}
}
