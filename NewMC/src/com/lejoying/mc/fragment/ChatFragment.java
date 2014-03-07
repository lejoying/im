package com.lejoying.mc.fragment;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.AnimationAdapter;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.data.handler.FileHandler.SaveBitmapInterface;
import com.lejoying.mc.data.handler.FileHandler.SaveSettings;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCImageUtils;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;
import com.lejoying.utils.SHA1;

public class ChatFragment extends BaseFragment {

	App app = App.getInstance();

	private View mContent;
	public ChatAdapter mAdapter;

	int RESULT_SELECTPICTURE = 0x124;
	int RESULT_TAKEPICTURE = 0xa3;
	int RESULT_CATPICTURE = 0x3d;

	LayoutInflater mInflater;

	Map<String, Bitmap> tempImages = new Hashtable<String, Bitmap>();

	Bitmap defaultImage;

	SHA1 sha1;

	View iv_send;
	View iv_more;
	View iv_more_select;
	EditText editText_message;
	RelativeLayout rl_chatbottom;
	RelativeLayout rl_message;
	RelativeLayout rl_select;
	View rl_selectpicture;

	View groupTopBar;
	TextView textView_groupName;
	TextView textView_memberCount;
	LinearLayout linearlayout_members;

	View groupCenterBar;
	TextView textView_groupNameAndMemberCount;
	LinearLayout linearlayout;

	int beforeHeight;
	int beforeLineHeight;

	final int MAXTYPE_COUNT = 3;

	Bitmap headman;
	Bitmap headwoman;

	public int showFirstPosition;

	public static ChatFragment instance;

	public ListView chatContent;

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initShowFirstPosition() {
		int initShowCount = 10;
		System.out.println(app.data.nowChatFriend.notReadMessagesCount);
		if (app.data.nowChatFriend.notReadMessagesCount > 10) {
			initShowCount = app.data.nowChatFriend.notReadMessagesCount;
		}
		int messagesTotalCount = app.data.nowChatFriend.messages.size();
		if (messagesTotalCount < 10) {
			initShowCount = messagesTotalCount;
		}
		showFirstPosition = messagesTotalCount - initShowCount;
		System.out.println(messagesTotalCount);
		System.out.println(initShowCount);
	}

	@Override
	public void onResume() {
		instance = this;
		if (sha1 == null) {
			sha1 = new SHA1();
		}
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initShowFirstPosition();
		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_chat, null);

		chatContent = (ListView) mContent.findViewById(R.id.chatContent);

		app.dataHandler.modifyData(new Modification() {

			@Override
			public void modify(Data data) {
				data.nowChatFriend.notReadMessagesCount = 0;
			}
		});

		iv_send = mContent.findViewById(R.id.iv_send);
		iv_more = mContent.findViewById(R.id.iv_more);
		iv_more_select = mContent.findViewById(R.id.iv_more_select);
		editText_message = (EditText) mContent.findViewById(R.id.et_message);
		rl_chatbottom = (RelativeLayout) mContent
				.findViewById(R.id.rl_chatbottom);
		rl_message = (RelativeLayout) mContent.findViewById(R.id.rl_message);
		rl_select = (RelativeLayout) mContent.findViewById(R.id.rl_select);
		rl_selectpicture = mContent.findViewById(R.id.rl_selectpicture);

		groupTopBar = mContent.findViewById(R.id.relativeLayout_topbar);
		textView_groupName = (TextView) mContent
				.findViewById(R.id.textview_groupname);
		textView_memberCount = (TextView) mContent
				.findViewById(R.id.textview_membercount);

		linearlayout_members = (LinearLayout) mContent
				.findViewById(R.id.linearlayout_members);

		for (int i = 0; i < 4; i++) {
			ImageView iv_head = new ImageView(getActivity());
			iv_head.setImageBitmap(app.fileHandler.defaultHead);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					40, 40);
			if (i != 3)
				params.setMargins(0, 0, 10, 0);
			iv_head.setLayoutParams(params);
			linearlayout_members.addView(iv_head);
		}

		groupCenterBar = mContent.findViewById(R.id.relativeLayout_group);
		textView_groupNameAndMemberCount = (TextView) mContent
				.findViewById(R.id.textView_groupNameAndMemberCount);
		linearlayout = (LinearLayout) groupCenterBar
				.findViewById(R.id.linearlayout_user);

		groupTopBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				groupCenterBar.setVisibility(View.VISIBLE);
			}
		});

		groupCenterBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				groupCenterBar.setVisibility(View.GONE);
			}
		});

		for (int i = 0; i < 14; i++) {
			View userView = inflater.inflate(
					R.layout.f_friend_panelitem_gridpage_item, null);
			ImageView iv_head = (ImageView) userView.findViewById(R.id.iv_head);
			TextView tv_nickname = (TextView) userView
					.findViewById(R.id.tv_nickname);
			iv_head.setImageBitmap(app.fileHandler.defaultHead);
			tv_nickname.setText("测试" + i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			params.setMargins(40, 0, 0, 0);

			if (i == 13) {
				params.setMargins(40, 0, 40, 0);
			}

			userView.setLayoutParams(params);
			linearlayout.addView(userView);
		}

		rl_selectpicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});

		iv_more_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSelectTab();
			}
		});

		final GestureDetector gestureDetector = new GestureDetector(
				getActivity(), new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						boolean flag = false;
						if (e2.getX() - e1.getX() > 0 && velocityX > 2000) {
							showSelectTab();
							flag = true;
						}
						return flag;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return false;
					}
				});

		editText_message.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		editText_message.setVisibility(View.GONE);
		editText_message.setVisibility(View.VISIBLE);
		// editText_message.requestFocus();

		iv_more.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showSelectTab();
			}
		});

		editText_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (beforeHeight == 0) {
					beforeHeight = editText_message.getHeight();
				}
				if (beforeLineHeight == 0) {
					beforeLineHeight = editText_message.getLineHeight();
				}

				LayoutParams etparams = editText_message.getLayoutParams();
				LayoutParams rlparams = rl_chatbottom.getLayoutParams();

				int lineCount = editText_message.getLineCount();

				switch (lineCount) {
				case 4:
					etparams.height = beforeHeight + beforeLineHeight;
					rlparams.height = beforeHeight + beforeLineHeight;
					break;
				case 5:
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
					break;

				default:
					if (lineCount <= 3) {
						etparams.height = beforeHeight;
						rlparams.height = beforeHeight;
					}
					break;
				}
				if (lineCount > 5) {
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
				}
				editText_message.setLayoutParams(etparams);
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
				final String message = editText_message.getText().toString();
				editText_message.setText("");
				if (message != null && !message.equals("")) {
					sendMessage("text", message);
				}
			}
		});

		headman = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);
		headwoman = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_woman), true, 10, Color.WHITE);

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mAdapter == null) {
			mAdapter = new ChatAdapter();
		}
		chatContent.setAdapter(mAdapter);
		chatContent.setSelection(mAdapter.getCount() - 1);
		chatContent.setOnScrollListener(new OnScrollListener() {
			boolean isFirst = true;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0 && showFirstPosition != 0 && !isFirst) {
					int old = showFirstPosition;
					showFirstPosition = showFirstPosition > 10 ? showFirstPosition - 10
							: 0;
					mAdapter.notifyDataSetChanged();
					chatContent.setSelection(old - showFirstPosition);
				}
				isFirst = false;
			}
		});
	}

	public void showSelectTab() {
		hideSoftInput();
		Animation outAnimation = new TranslateAnimation(0,
				rl_chatbottom.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_message.setVisibility(View.GONE);
				rl_message.clearAnimation();
			}
		});
		rl_message.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(
				-rl_chatbottom.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		rl_select.setVisibility(View.VISIBLE);
		rl_select.startAnimation(inAnimation);
	}

	public void hideSelectTab() {
		Animation outAnimation = new TranslateAnimation(0,
				-rl_chatbottom.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_select.setVisibility(View.GONE);
				rl_message.clearAnimation();
			}
		});
		rl_select.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(
				rl_chatbottom.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		rl_message.setVisibility(View.VISIBLE);
		editText_message.requestFocus();
		rl_message.startAnimation(inAnimation);
	}

	public class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.data.nowChatFriend.messages.size() - showFirstPosition;
		}

		@Override
		public Object getItem(int position) {
			return app.data.nowChatFriend.messages.get(showFirstPosition
					+ position);
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
				messageHolder = new MessageHolder();
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					convertView = mInflater.inflate(R.layout.f_chat_item_right,
							null);
					messageHolder.text = convertView
							.findViewById(R.id.rl_chatright);
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					convertView = mInflater.inflate(R.layout.f_chat_item_left,
							null);
					messageHolder.text = convertView
							.findViewById(R.id.rl_chatleft);
					break;
				default:
					break;
				}
				messageHolder.image = convertView
						.findViewById(R.id.rl_chatleft_image);
				messageHolder.iv_image = (ImageView) convertView
						.findViewById(R.id.iv_image);
				messageHolder.tv_nickname = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				messageHolder.iv_head = (ImageView) convertView
						.findViewById(R.id.iv_head);
				messageHolder.tv_chat = (TextView) convertView
						.findViewById(R.id.tv_chat);
				convertView.setTag(messageHolder);
			} else {
				messageHolder = (MessageHolder) convertView.getTag();
			}
			Message message = (Message) getItem(position);
			if (message.contentType.equals("text")) {
				messageHolder.text.setVisibility(View.VISIBLE);
				messageHolder.image.setVisibility(View.GONE);
				messageHolder.tv_chat.setText(message.content);
				String fileName = app.data.user.head;
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					fileName = app.data.user.head;
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					fileName = app.data.nowChatFriend.head;
					break;
				default:
					break;
				}
				final String headFileName = fileName;
				final ImageView iv_head = messageHolder.iv_head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
						if (where == app.fileHandler.FROM_WEB) {
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			} else if (message.contentType.equals("image")) {
				messageHolder.text.setVisibility(View.GONE);
				messageHolder.image.setVisibility(View.VISIBLE);
				final String imageFileName = message.content;
				final ImageView iv_image = messageHolder.iv_image;
				app.fileHandler.getImage(imageFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_image.setImageBitmap(app.fileHandler.bitmaps
								.get(imageFileName));
						if (where == app.fileHandler.FROM_WEB) {
							mAdapter.notifyDataSetChanged();
						}
					}
				});
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					messageHolder.tv_nickname
							.setText(app.data.nowChatFriend.nickName);
					break;
				default:
					break;
				}
			} else if (message.contentType.equals("voice")) {

			}
			return convertView;
		}
	}

	class MessageHolder {
		View text;
		ImageView iv_head;
		TextView tv_chat;

		View image;
		ImageView iv_image;
		TextView tv_nickname;
	}

	public void sendMessage(final String type, final String content) {
		final Message message = new Message();
		message.type = Message.MESSAGE_TYPE_SEND;
		message.sendType = "point";
		message.content = content;
		message.contentType = type;
		message.status = "sending";
		message.friendPhone = app.data.nowChatFriend.phone;
		message.time = String.valueOf(new Date().getTime());

		app.dataHandler.modifyData(new Modification() {
			public void modify(Data data) {
				data.nowChatFriend.messages.add(message);
			}
		}, new UIModification() {
			public void modifyUI() {
				mAdapter.notifyDataSetChanged();
				chatContent.setSelection(mAdapter.getCount() - 1);
			}
		});

		MCNetUtils.ajax(new AjaxAdapter() {
			public void setParams(Settings settings) {
				settings.url = API.MESSAGE_SEND;
				settings.params = generateMessageParams(type, content);
			}

			public void onSuccess(final JSONObject jData) {
				try {
					jData.getString(getString(R.string.app_reason));
					message.status = "failed";
					return;
				} catch (JSONException e) {
				}
				app.dataHandler.modifyData(new Modification() {
					public void modify(Data data) {
						try {
							String time = jData.getString("time");
							message.time = time;
							message.status = "sent";
						} catch (JSONException e) {
							message.status = "failed";
						}
						if (app.data.lastChatFriends
								.indexOf(app.data.nowChatFriend.phone) != 0) {
							app.data.lastChatFriends
									.remove(app.data.nowChatFriend.phone);
							app.data.lastChatFriends.add(0,
									app.data.nowChatFriend.phone);
						}
					}
				}, new UIModification() {
					public void modifyUI() {
						// ?
					}
				});
			}
		});
	}

	public Bundle generateMessageParams(String type, String content) {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("sendType", "point");
		JSONArray jFriends = new JSONArray();
		jFriends.put(app.data.nowChatFriend.phone);
		params.putString("phoneto", jFriends.toString());
		JSONObject jMessage = new JSONObject();
		try {
			jMessage.put("contentType", type);
			jMessage.put("content", content);
			params.putString("message", jMessage.toString());
		} catch (JSONException e) {
		}

		return params;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex)
					.toLowerCase(Locale.getDefault());
			final String format = picturePath.substring(picturePath
					.lastIndexOf("."));
			cursor.close();

			final Bitmap bitmap = MCImageUtils.getZoomBitmapFromFile(new File(
					picturePath), 960, 540);
			if (bitmap != null) {
				app.fileHandler.saveBitmap(new SaveBitmapInterface() {

					@Override
					public void setParams(SaveSettings settings) {
						settings.compressFormat = format.equals(".jpg") ? settings.JPG
								: settings.PNG;
						settings.source = bitmap;
					}

					@Override
					public void onSuccess(String fileName, String base64) {
						checkImage(fileName, base64);
					}
				});
			}

		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {

		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {

		}
	}

	void selectPicture() {
		Intent selectFromGallery = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(selectFromGallery, RESULT_SELECTPICTURE);
	}

	void takePicture() {
		tempFile = new File(app.sdcardImageFolder, "tempimage");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++));
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
	}

	public void checkImage(final String fileName, final String base64) {
		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.IMAGE_CHECK;
				Bundle params = new Bundle();
				params.putString("phone", app.data.user.phone);
				params.putString("accessKey", app.data.user.accessKey);
				params.putString("filename", fileName);
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						sendMessage("image", fileName);
					} else {
						uploadImage(fileName, base64);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void uploadImage(final String fileName, final String base64) {
		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.IMAGE_UPLOAD;
				Bundle params = new Bundle();
				params.putString("phone", app.data.user.phone);
				params.putString("accessKey", app.data.user.accessKey);
				params.putString("filename", fileName);
				params.putString("imagedata", base64);
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				try {
					jData.getString(getString(R.string.app_reason));
					return;
				} catch (JSONException e) {
				}
				sendMessage("image", fileName);
			}
		});
	}

	File tempFile;

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.chatFragment;
	}

}
