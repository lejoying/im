package com.lejoying.mc.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import android.util.Base64;
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
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.DownloadListener;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.SHA1;
import com.lejoying.utils.StreamTools;

public class ChatFragment extends BaseListFragment {

	App app = App.getInstance();

	private View mContent;
	public ChatAdapter mAdapter;

	public final int RESULT_SELECTPICTURE = 0x4232;

	LayoutInflater mInflater;

	Map<String, Bitmap> tempImages = new Hashtable<String, Bitmap>();

	Bitmap defaultImage;

	SHA1 sha1;

	View iv_send;
	View iv_more;
	View iv_more_select;
	EditText et_message;
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
			final String picturePath = cursor.getString(columnIndex);
			final String format = picturePath.substring(picturePath
					.lastIndexOf("."));
			cursor.close();

			new Thread() {
				InputStream inputStream = null;
				FileOutputStream fileOutputStream = null;
				File tempImage = null;
				String base64;
				String uploadFileName;
				File imageFile;
				InputStream zoomImageStream;

				public void run() {
					try {
						File sourceFile = new File(picturePath);
						inputStream = new FileInputStream(sourceFile);
						byte[] tempBytes = StreamTools.isToData(inputStream);
						if (!MCImageTools.isNeedToZoom(tempBytes, 960, 540)) {
							base64 = Base64.encodeToString(tempBytes,
									Base64.DEFAULT);
							String uploadFile = sha1.getDigestOfString(base64
									.trim().getBytes()) + format;
							uploadFileName = uploadFile.toLowerCase(Locale
									.getDefault());
							tempImages.put(uploadFileName, BitmapFactory
									.decodeByteArray(tempBytes, 0,
											tempBytes.length));
							imageFile = new File(app.sdcardImageFolder,
									uploadFileName);

							if (!imageFile.exists()) {
								StreamTools.copyFile(sourceFile, imageFile,
										true);
							}

						} else {
							Bitmap tempImageBitmap = MCImageTools
									.getZoomBitmapFromStream(tempBytes, 960,
											540);
							tempImage = new File(app.sdcardImageFolder,
									"tempimage.jpg");
							int i = 1;
							while (tempImage.exists()) {
								tempImage = new File(app.sdcardImageFolder,
										"tempimage" + (i++) + ".jpg");
							}
							fileOutputStream = new FileOutputStream(tempImage);
							tempImageBitmap.compress(
									Bitmap.CompressFormat.JPEG, 70,
									fileOutputStream);
							try {
								fileOutputStream.flush();
								fileOutputStream.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							zoomImageStream = new FileInputStream(tempImage);

							byte[] b = StreamTools.isToData(zoomImageStream);

							if (b != null) {
								base64 = Base64.encodeToString(b,
										Base64.DEFAULT);
								String uploadFile = sha1
										.getDigestOfString(base64.trim()
												.getBytes())
										+ ".jpg";

								uploadFileName = uploadFile.toLowerCase(Locale
										.getDefault());
								tempImages.put(uploadFileName, BitmapFactory
										.decodeByteArray(b, 0, b.length));
								imageFile = new File(app.sdcardImageFolder,
										uploadFileName);
								if (imageFile.exists()) {
									tempImage.delete();
								} else {
									tempImage.renameTo(imageFile);
								}

							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (zoomImageStream != null) {
							try {
								zoomImageStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					Bundle params = new Bundle();
					params.putString("phone", app.data.user.phone);
					params.putString("accessKey", app.data.user.accessKey);
					params.putString("filename", uploadFileName);

					MCNetTools.ajax(getActivity(), API.IMAGE_CHECK, params,
							MCHttpTools.SEND_POST, 5000,
							new ResponseListener() {

								@Override
								public void success(JSONObject data) {
									try {
										boolean isExists = data
												.getBoolean("exists");

										if (!isExists) {
											Bundle params = new Bundle();
											params.putString("phone",
													app.data.user.phone);
											params.putString("accessKey",
													app.data.user.accessKey);
											params.putString("filename",
													uploadFileName);
											params.putString("imagedata",
													base64);
											MCNetTools.ajax(getActivity(),
													API.IMAGE_UPLOAD, params,
													MCHttpTools.SEND_POST,
													5000,
													new ResponseListener() {

														@Override
														public void success(
																JSONObject data) {
															try {
																data.get(getString(R.string.app_reason));
																return;
															} catch (JSONException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}

															Bundle params = generateParams(
																	"image",
																	uploadFileName);
															MCNetTools
																	.ajax(getActivity(),
																			API.MESSAGE_SEND,
																			params,
																			MCHttpTools.SEND_POST,
																			5000,
																			new ResponseListener() {

																				@Override
																				public void success(
																						JSONObject data) {
																					app.isDataChanged = true;
																					if (app.data.user.flag
																							.equals("none")) {
																						app.data.user.flag = String
																								.valueOf(1);
																					} else {
																						app.data.user.flag = String
																								.valueOf(Integer
																										.valueOf(
																												app.data.user.flag)
																										.intValue() + 1);
																					}
																				}

																				@Override
																				public void noInternet() {
																					// TODO
																					// Auto-generated
																					// method
																					// stub

																				}

																				@Override
																				public void failed() {
																					// TODO
																					// Auto-generated
																					// method
																					// stub

																				}

																				@Override
																				public void connectionCreated(
																						HttpURLConnection httpURLConnection) {
																					// TODO
																					// Auto-generated
																					// method
																					// stub

																				}
																			});
														}

														@Override
														public void noInternet() {
															// TODO
															// Auto-generated
															// method
															// stub
														}

														@Override
														public void failed() {
															// TODO
															// Auto-generated
															// method
															// stub
														}

														@Override
														public void connectionCreated(
																HttpURLConnection httpURLConnection) {
															// TODO
															// Auto-generated
															// method
															// stub
														}
													});
										} else {
											Bundle params = generateParams(
													"image", uploadFileName);
											MCNetTools.ajax(getActivity(),
													API.MESSAGE_SEND, params,
													MCHttpTools.SEND_POST,
													5000,
													new ResponseListener() {

														@Override
														public void success(
																JSONObject data) {
															app.isDataChanged = true;
															if (app.data.user.flag
																	.equals("none")) {
																app.data.user.flag = String
																		.valueOf(1);
															} else {
																app.data.user.flag = String
																		.valueOf(Integer
																				.valueOf(
																						app.data.user.flag)
																				.intValue() + 1);
															}
														}

														@Override
														public void noInternet() {
															// TODO
															// Auto-generated
															// method
															// stub

														}

														@Override
														public void failed() {
															// TODO
															// Auto-generated
															// method
															// stub

														}

														@Override
														public void connectionCreated(
																HttpURLConnection httpURLConnection) {
															// TODO
															// Auto-generated
															// method
															// stub

														}
													});
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}

								@Override
								public void noInternet() {
									// TODO Auto-generated method
									// stub
								}

								@Override
								public void failed() {
									// TODO Auto-generated method
									// stub
								}

								@Override
								public void connectionCreated(
										HttpURLConnection httpURLConnection) {
									// TODO Auto-generated method
									// stub
								}
							});

				}
			}.start();

		}
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
		iv_more_select = mContent.findViewById(R.id.iv_more_select);
		et_message = (EditText) mContent.findViewById(R.id.et_message);
		rl_chatbottom = (RelativeLayout) mContent
				.findViewById(R.id.rl_chatbottom);
		rl_message = (RelativeLayout) mContent.findViewById(R.id.rl_message);
		rl_select = (RelativeLayout) mContent.findViewById(R.id.rl_select);
		rl_selectpicture = mContent.findViewById(R.id.rl_selectpicture);

		rl_selectpicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_SELECTPICTURE);
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

		et_message.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		iv_more.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

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
					Bundle params = generateParams("text", message);
					MCNetTools.ajax(getActivity(), API.MESSAGE_SEND, params,
							MCHttpTools.SEND_POST, 5000,
							new ResponseListener() {

								@Override
								public void success(JSONObject data) {
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
		et_message.requestFocus();
		rl_message.startAnimation(inAnimation);
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
			} else if (message.messageType.equals("image")) {
				messageHolder.text.setVisibility(View.GONE);
				messageHolder.image.setVisibility(View.VISIBLE);
				final String imageFileName = message.content;
				if (tempImages.get(imageFileName) == null) {
					final File imageFile = new File(app.sdcardImageFolder,
							imageFileName);
					if (imageFile.exists()) {
						Bitmap image = BitmapFactory.decodeFile(imageFile
								.getAbsolutePath());
						if (image != null) {
							tempImages.put(imageFileName, image);
						}
						mAdapter.notifyDataSetChanged();
					} else {
						if (defaultImage == null) {
							defaultImage = BitmapFactory.decodeResource(
									getResources(), R.drawable.defaultimage);
						}
						tempImages.put(imageFileName, defaultImage);
						MCNetTools.downloadFile(getActivity(),
								app.config.DOMAIN_IMAGE, imageFileName,
								app.sdcardImageFolder, null, 5000,
								new DownloadListener() {
									@Override
									public void success(File localFile,
											InputStream inputStream) {
										Bitmap bitmap = BitmapFactory
												.decodeFile(localFile
														.getAbsolutePath());
										tempImages.put(imageFileName, bitmap);
										mAdapter.notifyDataSetChanged();
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

									@Override
									public void downloading(int progress) {
										// TODO Auto-generated method stub
									}
								});
					}
				}
				messageHolder.iv_image.setImageBitmap(tempImages
						.get(imageFileName));
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					messageHolder.tv_nickname
							.setText(app.nowChatFriend.nickName);
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

	public Bundle generateParams(String type, String content) {
		Message message = new Message();
		message.type = Message.MESSAGE_TYPE_SEND;
		message.content = content;
		message.messageType = type;
		message.status = "sending";
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
			jMessage.put("type", type);
			jMessage.put("content", content);
			params.putString("message", jMessage.toString());
		} catch (JSONException e) {
		}

		return params;
	}
}
