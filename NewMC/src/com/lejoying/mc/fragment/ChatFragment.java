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
import android.os.Handler;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.Settings;
import com.lejoying.utils.SHA1;

public class ChatFragment extends BaseListFragment {

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

		app.data.nowChatFriend.notReadMessagesCount = 0;
		iv_send = mContent.findViewById(R.id.iv_send);
		iv_more = mContent.findViewById(R.id.iv_more);
		iv_more_select = mContent.findViewById(R.id.iv_more_select);
		editText_message = (EditText) mContent.findViewById(R.id.et_message);
		rl_chatbottom = (RelativeLayout) mContent
				.findViewById(R.id.rl_chatbottom);
		rl_message = (RelativeLayout) mContent.findViewById(R.id.rl_message);
		rl_select = (RelativeLayout) mContent.findViewById(R.id.rl_select);
		rl_selectpicture = mContent.findViewById(R.id.rl_selectpicture);

		rl_selectpicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});

		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showSelectTab();
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
						// TODO Auto-generated method stub
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

		editText_message.requestFocus();

		iv_more.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
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
			return app.data.nowChatFriend.messages.size();
		}

		@Override
		public Object getItem(int position) {
			return app.data.nowChatFriend.messages.get(position);
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
			if (message.messageType.equals("text")) {
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
			} else if (message.messageType.equals("image")) {
				messageHolder.text.setVisibility(View.GONE);
				messageHolder.image.setVisibility(View.VISIBLE);
				final String imageFileName = message.content;
				final ImageView iv_image = messageHolder.iv_image;
				app.fileHandler.getImage(imageFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_image.setImageBitmap(app.fileHandler.bitmaps
								.get(imageFileName));
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
			} else if (message.messageType.equals("voice")) {

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
		message.content = content;
		message.messageType = type;
		message.status = "sending";
		message.time = String.valueOf(new Date().getTime());

		app.dataHandler.modifyData(new Modification() {
			public void modify(Data data) {
				data.nowChatFriend.messages.add(message);
			}
		}, new UIModification() {
			public void modifyUI() {
				mAdapter.notifyDataSetChanged();
				getListView().setTranscriptMode(
						ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			}
		});

		MCNetTools.ajax(new AjaxAdapter() {
			public void setParams(Settings settings) {
				settings.url = API.MESSAGE_SEND;
				settings.params = generateParams(type, content);
			}

			public void onSuccess(JSONObject data) {
				app.dataHandler.modifyData(new Modification() {
					public void modify(Data data) {
						if (app.data.user.flag.equals("none")) {
							app.data.user.flag = String.valueOf(1);
						} else {
							app.data.user.flag = String
									.valueOf(Integer
											.valueOf(app.data.user.flag)
											.intValue() + 1);
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

	public Bundle generateParams(String type, String content) {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		JSONArray jFriends = new JSONArray();
		jFriends.put(app.data.nowChatFriend.phone);
		params.putString("phoneto", jFriends.toString());
		JSONObject jMessage = new JSONObject();
		try {
			jMessage.put("type", type);
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

			final Bitmap bitmap = MCImageTools.getZoomBitmapFromFile(new File(
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
						System.out.println(fileName);
						checkImage(fileName, base64);
					}
				});
			}

		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {

		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {
			final Bitmap image = (Bitmap) data.getExtras().get("data");

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
		MCNetTools.ajax(new AjaxAdapter() {

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
					System.out.println(jData);
					System.out.println(fileName);
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
		MCNetTools.ajax(new AjaxAdapter() {

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

}
