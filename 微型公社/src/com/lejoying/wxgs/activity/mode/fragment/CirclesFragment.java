package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.BaseModeManager.KeyDownListener;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation.AnimatingView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.DialogListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.AnimationAdapter;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class CirclesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	View mContentView;
	ScrollRelativeLayout circlesViewContenter;
	View editControl;
	LinearLayout tempFriendsList;
	View save;
	View copy;
	View newGroup;
	View modifyCircleName;
	View deleteCircle;

	RelativeLayout animationLayout;
	HorizontalScrollView tempFriendScroll;

	LayoutInflater mInflater;

	public String copyStatus = "move";// "move"||"copy"
	public String mode = "normal";// "normal"||"edit"

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_circles));
		super.onResume();
	}

	FrictionAnimation decelerationAnimation = new FrictionAnimation();
	AnimatingView animatingView = new AnimatingView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_circles_scroll, null);
		circlesViewContenter = (ScrollRelativeLayout) mContentView
				.findViewById(R.id.circlesViewContainer);
		editControl = mContentView.findViewById(R.id.editControl);

		save = mContentView.findViewById(R.id.save);
		copy = mContentView.findViewById(R.id.copy);
		newGroup = mContentView.findViewById(R.id.newGroup);
		modifyCircleName = mContentView.findViewById(R.id.modifyCircleName);
		deleteCircle = mContentView.findViewById(R.id.deleteCircle);

		tempFriendsList = (LinearLayout) mContentView
				.findViewById(R.id.tempFriendsList);
		animationLayout = (RelativeLayout) mContentView
				.findViewById(R.id.animationLayout);
		tempFriendScroll = (HorizontalScrollView) mContentView
				.findViewById(R.id.tempFriendScroll);

		animatingView.view = circlesViewContenter;

		density = getActivity().getResources().getDisplayMetrics().density;

		circleViewCommonAnimation();

		notifyViews();

		initEvent();

		return mContentView;
	}

	public void notifyViews() {
		notifyViews(true);
	}

	public void notifyViews(boolean initShowMessageCount) {
		generateViews(initShowMessageCount);
		if (mode.equals("normal")) {
			int top = 25;
			int scrollToY = 0;
			for (int i = 0; i < normalShow.size(); i++) {
				View v = views.get(normalShow.get(i));
				int height = (int) dp2px(((Integer) v.getTag()).floatValue());
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(layoutParams.leftMargin, top,
						layoutParams.rightMargin, -Integer.MAX_VALUE);

				if (currentEditPosition != -1
						&& v.equals(views.get(circles.get(currentEditPosition)))) {
					scrollToY = top;
					scrollToY -= dp2px(20);
				}

				top = top + height + 25;
				v.setLayoutParams(layoutParams);
				if (v.getParent() == null) {
					circlesViewContenter.addView(v, i);
				}
			}
			circlesViewContenter.scrollTo(0, scrollToY);
		} else if (mode.equals("edit")) {

			final int screenWidth = getScreenWidth();

			for (int i = 0; i < circles.size(); i++) {
				String group = circles.get(i);
				View v = views.get(group);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						screenWidth, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(i * screenWidth, (int) dp2px(20),
						-Integer.MAX_VALUE, 0);
				v.setLayoutParams(layoutParams);
				TextView manager = (TextView) v
						.findViewById(R.id.panel_right_button);
				manager.setText("分组管理");
				manager.setVisibility(View.VISIBLE);
				v.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);

				View buttonPreviousGroup = v
						.findViewById(R.id.buttonPreviousGroup);
				buttonPreviousGroup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentEditPosition > 0) {
							View currentView = views.get(circles
									.get(currentEditPosition));
							currentEditPosition--;
							View previousView = views.get(circles
									.get(currentEditPosition));

							TranslateAnimation animation = new TranslateAnimation(
									-screenWidth, 0, 0, 0);
							animation.setDuration(300);

							circlesViewContenter.scrollTo(currentEditPosition
									* screenWidth, 0);

							currentView.startAnimation(animation);
							previousView.startAnimation(animation);

						}
					}
				});

				View buttonNextGroup = v.findViewById(R.id.buttonNextGroup);
				buttonNextGroup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentEditPosition < circles.size() - 1) {
							View currentView = views.get(circles
									.get(currentEditPosition));
							currentEditPosition++;
							View nextView = views.get(circles
									.get(currentEditPosition));

							TranslateAnimation animation = new TranslateAnimation(
									screenWidth, 0, 0, 0);
							animation.setDuration(300);

							circlesViewContenter.scrollTo(currentEditPosition
									* screenWidth, 0);
							currentView.startAnimation(animation);
							nextView.startAnimation(animation);
						}
					}
				});

				if (circles.size() == 1) {
					v.findViewById(R.id.bottomBar).setVisibility(View.GONE);
				} else {
					if (i == 0) {
						buttonPreviousGroup.setVisibility(View.GONE);
					}
					if (i == circles.size() - 1) {
						buttonNextGroup.setVisibility(View.GONE);
					}
				}
				if (v.getParent() == null) {
					circlesViewContenter.addView(v, i);
				}
			}
			circlesViewContenter.scrollTo(currentEditPosition * screenWidth, 0);
		}
	}

	static class TouchEvnetStatus {
		public static int STATIC = 0;
		public static int MOVING_X = 1;
		public static int MOVING_Y = 2;
		public static int END = 3;
	}

	public int touchEvnetStatus = TouchEvnetStatus.STATIC;

	boolean isCopy;
	boolean isSaved;

	void resetSaveStatus() {
		if (isSaved) {
			isSaved = false;
			ImageView saveImage = (ImageView) save.findViewById(R.id.saveImage);
			TextView saveText = (TextView) save.findViewById(R.id.saveText);
			saveImage.setImageResource(R.drawable.save_up);
			saveText.setText("保存");
		}
	}

	void notifyFriendChanged() {
		Set<String> moveOutKeys = modifyFriend.moveOut.keySet();
		Set<String> moveInKeys = modifyFriend.moveIn.keySet();
		for (final String key : moveOutKeys) {
			final List<String> phones = modifyFriend.moveOut.get(key);
			if (phones.size() == 0 || (app.data.circlesMap.get(key) == null)) {
				continue;
			}
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					Circle circle = data.circlesMap.get(key);
					if (circle != null) {
						circle.phones.removeAll(phones);
					}
				}
			});
			StringBuffer buffer = new StringBuffer("[");
			for (String phone : phones) {
				buffer.append("\"" + phone + "\",");
			}
			buffer.replace(buffer.length() - 1, buffer.length(), "]");
			final String phonesParam = buffer.toString();
			CommonNetConnection netConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.CIRCLE_MOVEOROUT;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("rid", key);
					params.put("phoneto", phonesParam);
					params.put("filter", "REMOVE");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					System.out.println(jData);
				}
			};
			app.networkHandler.connection(netConnection);
		}

		for (final String key : moveInKeys) {
			final List<String> phones = modifyFriend.moveIn.get(key);
			if (phones.size() == 0 || (app.data.circlesMap.get(key) == null)) {
				continue;
			}
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					Circle circle = data.circlesMap.get(key);
					if (circle != null) {
						circle.phones.addAll(0, phones);
					}
					data.circlesMap.get("-1").phones.removeAll(phones);
				}
			});
			StringBuffer buffer = new StringBuffer("[");
			for (String phone : phones) {
				buffer.append("\"" + phone + "\",");
			}
			buffer.replace(buffer.length() - 1, buffer.length(), "]");
			final String phonesParam = buffer.toString();
			CommonNetConnection netConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.CIRCLE_MOVEOROUT;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("rid", key);
					params.put("phoneto", phonesParam);
					params.put("filter", "SHIFTIN");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					System.out.println(jData);
				}
			};
			app.networkHandler.connection(netConnection);
		}

		modifyFriend.clear();
	}

	void initEvent() {

		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView saveImage = (ImageView) save
						.findViewById(R.id.saveImage);
				TextView saveText = (TextView) save.findViewById(R.id.saveText);
				if (!isSaved) {
					isSaved = true;
					saveImage.setImageResource(R.drawable.saved_down);
					saveText.setText("已保存");
					notifyFriendChanged();
				}
			}
		});
		copy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView copyStatus = (ImageView) copy
						.findViewById(R.id.copyImage);
				TextView copyStatusText = (TextView) copy
						.findViewById(R.id.copyText);
				if (!isCopy) {
					isCopy = true;
					copyStatus.setImageResource(R.drawable.choise_down);
					copyStatusText.setText("复制中");
				} else {
					isCopy = false;
					copyStatus.setImageResource(R.drawable.choise_up);
					copyStatusText.setText("复制");
				}
			}
		});

		newGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}
				final EditText circleName;
				new AlertDialog.Builder(getActivity())
						.setTitle("请输入分组名")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(circleName = new EditText(getActivity()))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (circleName.getText().toString()
												.equals("")) {
											Alert.showMessage("密友圈名称不能为空");
											return;
										}
										app.networkHandler
												.connection(new CommonNetConnection() {

													@Override
													protected void settings(
															Settings settings) {
														settings.url = API.DOMAIN
																+ API.CIRCLE_ADDCIRCLE;
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"phone",
																app.data.user.phone);
														params.put(
																"accessKey",
																app.data.user.accessKey);
														params.put(
																"name",
																circleName
																		.getText()
																		.toString());
														settings.params = params;
													}

													@Override
													public void success(
															final JSONObject jData) {
														app.dataHandler
																.exclude(new Modification() {

																	@Override
																	public void modifyData(
																			Data data) {
																		Circle circle = new Circle();
																		try {
																			JSONObject jCircle = jData
																					.getJSONObject("circle");
																			circle.rid = jCircle
																					.getInt("rid");
																			circle.name = jCircle
																					.getString("name");
																			data.circles
																					.add(data.circles
																							.size() - 1,
																							String.valueOf(circle.rid));
																			data.circlesMap
																					.put(String
																							.valueOf(circle.rid),
																							circle);
																			currentEditPosition = circles
																					.size() - 1;
																		} catch (JSONException e) {
																		}
																	}

																	@Override
																	public void modifyUI() {
																		notifyViews();
																	}
																});
													}
												});
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		deleteCircle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String rid = String.valueOf(
						circles.get(currentEditPosition)).substring(6);
				if (rid.equals("-1")) {
					Alert.showMessage("默认分组不能删除");
					return;
				}
				Alert.showDialog("确定要删除该组？删除该组后该组好友如果不在其它分组将被自动转移到默认分组中。",
						new DialogListener() {

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub

							}

							@Override
							public boolean confirm() {
								circles.remove("group#" + rid);
								View v = views.remove("group#" + rid);
								if (v != null) {
									circlesViewContenter.removeView(v);
								}
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.circles.remove(rid);
										Circle circle = data.circlesMap
												.remove(rid);
										if (circle != null) {
											Map<String, List<String>> phones = checkFriends(circle.phones);
											data.circlesMap.get("-1").phones
													.addAll(phones
															.get("default"));
										}
									}

									@Override
									public void modifyUI() {
										notifyViews();
									}
								});
								CommonNetConnection deleteCirccle = new CommonNetConnection() {

									@Override
									protected void settings(Settings settings) {
										settings.url = API.DOMAIN
												+ API.CIRCLE_DELETE;
										Map<String, String> params = new HashMap<String, String>();
										params.put("phone", app.data.user.phone);
										params.put("accessKey",
												app.data.user.accessKey);
										params.put("rid", rid);
										settings.params = params;
									}

									@Override
									public void success(JSONObject jData) {
										DataUtil.getCircles(new GetDataListener() {
											@Override
											public void getSuccess() {
												notifyViews();
											}
										});
									}
								};
								app.networkHandler.connection(deleteCirccle);
								return true;
							}

							@Override
							public void cancel() {
								// TODO Auto-generated method stub

							}
						});
			}
		});

		modifyCircleName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (String.valueOf(circles.get(currentEditPosition))
						.substring(6).equals("-1")) {
					Alert.showMessage("默认分组不能修改名称");
					return;
				}
				final EditText circleName;
				new AlertDialog.Builder(getActivity())
						.setTitle("请输入新的密友圈名称")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(circleName = new EditText(getActivity()))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (circleName.getText().toString()
												.equals("")) {
											Alert.showMessage("密友圈名称不能为空");
											return;
										}
										app.networkHandler
												.connection(new CommonNetConnection() {

													@Override
													protected void settings(
															Settings settings) {
														settings.url = API.DOMAIN
																+ API.CIRCLE_MODIFY;
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"phone",
																app.data.user.phone);
														params.put(
																"accessKey",
																app.data.user.accessKey);
														params.put(
																"rid",
																String.valueOf(
																		circles.get(currentEditPosition))
																		.substring(
																				6));
														params.put(
																"name",
																circleName
																		.getText()
																		.toString());
														settings.params = params;
													}

													@Override
													public void success(
															JSONObject jData) {
														System.out
																.println(jData);
													}
												});
										((TextView) views
												.get(circles
														.get(currentEditPosition))
												.findViewById(R.id.panel_name))
												.setText(circleName.getText());
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		circlesViewContenter.setOnTouchListener(new OnTouchListener() {
			public float x0 = 0;
			public float y0 = 0;

			public float x0_0 = 0;
			public float y0_0 = 0;

			public float vx = 0;
			public float vy = 0;

			public float dy = 0;
			public float dx = 0;

			public int preTouchTimes = 5;

			long pre_lastMillis = 0;
			long lastMillis = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// eventCount++;
				float x = event.getRawX();
				float y = event.getRawY();

				long currentMillis = System.currentTimeMillis();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (touchEvnetStatus == TouchEvnetStatus.END) {
						touchEvnetStatus = TouchEvnetStatus.STATIC;
					}
					x0 = x;
					y0 = y;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (lastMillis == 0) {
						lastMillis = currentMillis;
						return true;
					}
					dy = y - y0;
					dx = x - x0;
					if (touchEvnetStatus == TouchEvnetStatus.MOVING_Y) {
						if (mode.equals("edit")) {
							return false;
						}
						circlesViewContenter.scrollBy(0, -(int) (dy));
						y0 = y;
					} else if (touchEvnetStatus == TouchEvnetStatus.STATIC) {
						if (dy * dy + dx * dx > 400) {
							if (dy * dy > dx * dx) {
								touchEvnetStatus = TouchEvnetStatus.MOVING_Y;
							} else {
								touchEvnetStatus = TouchEvnetStatus.MOVING_X;
							}
						}
					}

					if (preTouchTimes < 0) {
						preTouchTimes = 2;
						x0_0 = x0;
						y0_0 = y0;
						pre_lastMillis = lastMillis;

						x0 = x;
						y0 = y;

						lastMillis = currentMillis;
					}
					preTouchTimes--;

				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					long delta = currentMillis - lastMillis;

					if (delta == 0 || x == x0 || y == y0) {
						delta = currentMillis - pre_lastMillis;
						x0 = x0_0;
						y0 = y0_0;
					}

					vx = (x - x0) / delta;
					vy = (y - y0) / delta;

					System.out
							.println("vx:    " + vx + "     ----vy:    " + vy);

					// FrictionAnimation decelerationAnimation = new
					// FrictionAnimation(vx, vy);
					// circlesViewContenter.startAnimation(decelerationAnimation);
					if (touchEvnetStatus == TouchEvnetStatus.MOVING_Y
							|| touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
						touchEvnetStatus = TouchEvnetStatus.END;
					}
				}

				return true;
			}
		});
	}

	Map<String, List<String>> checkFriends(Collection<String> checkPhones) {
		List<String> allInOtherCircleFriend = new ArrayList<String>();
		List<String> resultHasCircle = new ArrayList<String>();
		List<String> resultNoCircle = new ArrayList<String>();
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (String rid : app.data.circles) {
			Circle circle = app.data.circlesMap.get(rid);
			allInOtherCircleFriend.addAll(circle.phones);
		}
		for (String phone : checkPhones) {
			if (!allInOtherCircleFriend.contains(phone)) {
				resultNoCircle.add(phone);
			} else {
				resultHasCircle.add(phone);
			}
		}
		result.put("have", resultHasCircle);
		result.put("default", resultNoCircle);
		return result;
	}

	int currentEditPosition = -1;

	public void switchToEditMode(View view) {
		// Alert.showMessage("分组管理");

		if (mode.equals("normal")) {
			mode = "edit";
		} else {
			return;
		}
		resetSaveStatus();

		final int screenWidth = getScreenWidth();

		mMainModeManager.setKeyDownListener(new KeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					swichToNormalMode();
				}
				return false;
			}
		});

		circlesViewContenter.setGravity(Gravity.TOP | Gravity.LEFT);

		CircleMenu.showBack();

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int y = location[1];

		for (int i = 0; i < circles.size(); i++) {
			View v = views.get(circles.get(i));
			if (v.equals(view)) {
				currentEditPosition = i;
				break;
			}
		}

		circlesViewContenter.removeAllViews();
		circlesViewContenter.scrollTo(0, 0);

		notifyViews();

		TranslateAnimation editControlIn = new TranslateAnimation(0, 0,
				dp2px(160), 0);
		editControlIn.setDuration(500);
		editControl.setVisibility(View.VISIBLE);
		editControl.startAnimation(editControlIn);

		circlesViewContenter.scrollTo(currentEditPosition * screenWidth, 0);

		TranslateAnimation viewMove = new TranslateAnimation(0, 0, y - 50
				- dp2px(20), 0);
		viewMove.setDuration(350);
		view.startAnimation(viewMove);

	}

	public void swichToNormalMode() {
		notifyFriendChanged();
		if (mode.equals("edit")) {
			mode = "normal";
		} else {
			return;
		}
		if (tempFriendsList.getChildCount() != 0) {
			Alert.showDialog("未放入其它密友圈的好友将自动转移到默认分组中。", new DialogListener() {

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean confirm() {
					app.dataHandler.exclude(new Modification() {
						@Override
						public void modifyData(Data data) {
							Set<String> checkPhones = tempFriendHolders
									.keySet();
							Map<String, List<String>> result = checkFriends(checkPhones);
							data.circlesMap.get("-1").phones.addAll(result
									.get("default"));
						}

						@Override
						public void modifyUI() {
							normalMode();
						}
					});
					tempFriendsList.removeAllViews();
					tempViewMap.clear();
					tempFriendHolders.clear();
					return true;
				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			});
		} else {
			normalMode();
		}
	}

	void normalMode() {
		CircleMenu.show();
		mMainModeManager.setKeyDownListener(null);

		TranslateAnimation editControlIn = new TranslateAnimation(0, 0, 0,
				dp2px(160));
		editControlIn.setDuration(400);
		editControl.setVisibility(View.GONE);
		editControl.startAnimation(editControlIn);

		circlesViewContenter.removeAllViews();
		circlesViewContenter
				.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View v = views.get(group);
			TextView manager = (TextView) v
					.findViewById(R.id.panel_right_button);
			manager.setVisibility(View.GONE);
			v.findViewById(R.id.bottomBar).setVisibility(View.GONE);

		}

		notifyViews();

		currentEditPosition = -1;
	}

	float density = 1.0f;

	public int getScreenWidth() {
		return getActivity().getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return getActivity().getResources().getDisplayMetrics().heightPixels;
	}

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}

	Map<String, View> views = new HashMap<String, View>();
	List<String> normalShow = new ArrayList<String>();
	List<String> circles = new ArrayList<String>();

	int lastChatFriendsSize;

	void generateViews(boolean initShowMessageCount) {
		// generate message views;
		if (initShowMessageCount) {
			lastChatFriendsSize = app.data.lastChatFriends.size();
			lastChatFriendsSize = lastChatFriendsSize < 5 ? lastChatFriendsSize
					: 5;
		}
		normalShow.clear();

		View newFriendButtonView = views.get("button#newfriend");
		if (newFriendButtonView == null) {
			newFriendButtonView = generateNewFriendButtonView();
			newFriendButtonView.setTag(46);
			views.put("button#newfriend", newFriendButtonView);
		}

		int newFriendsCount = 0;
		for (Friend friend : app.data.newFriends) {
			if (app.data.friends.get(friend.phone) == null) {
				newFriendsCount++;
			}
		}
		if (newFriendsCount != 0) {
			notifyNewFriendButtonView(newFriendButtonView, newFriendsCount);
			normalShow.add("button#newfriend");
		}

		for (int i = 0; i < lastChatFriendsSize; i++) {
			String phone = app.data.lastChatFriends.get(i);
			Friend friend = app.data.friends.get(phone);
			if (friend == null) {
				continue;
			}
			View messageView = views.get("message#" + phone);
			if (messageView == null) {
				messageView = generateMessageView(phone);
				messageView.setTag(74);
				views.put("message#" + phone, messageView);
			}
			notifyMessageView(messageView, friend);
			normalShow.add("message#" + phone);
		}

		if (views.get("button#moremessage") == null) {
			View moreMessageButtonView = generateMoreMessageButtonView();
			moreMessageButtonView.setTag(46);
			views.put("button#moremessage", moreMessageButtonView);
		}
		if (lastChatFriendsSize != 0) {
			normalShow.add("button#moremessage");
		}

		circles.clear();
		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circlesMap.get(app.data.circles.get(i));

			View circleView = views.get("group#" + circle.rid);
			if (circleView == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				circleView = generateCircleView();
				views.put("group#" + circle.rid, circleView);
				circleView.setTag(272);
			}
			notifyCircleView(circleView, circle,
					circleHolders.get("group#" + circle.rid));

			normalShow.add("group#" + circle.rid);
			circles.add("group#" + circle.rid);
		}

		// if (views.get("button#creategroup") == null) {
		// View createGroupButtonView = generateCreateGroupButtonView();
		// createGroupButtonView.setTag(46);
		// views.put("button#creategroup", createGroupButtonView);
		// }
		// normalShow.add("button#creategroup");

		if (views.get("button#findmore") == null) {
			View findMoreFriendButtonView = generateFindMoreFriendButtonView();
			findMoreFriendButtonView.setTag(46);
			views.put("button#findmore", findMoreFriendButtonView);
		}
		normalShow.add("button#findmore");

	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
		return newFriendButtonView;
	}

	void notifyNewFriendButtonView(View newFriendButtonView, int newFriendsCount) {
		Button newFriendButton = (Button) newFriendButtonView
				.findViewById(R.id.button);
		if (newFriendsCount != 0) {
			newFriendButton.setText("新的好友(" + newFriendsCount + ")");
		} else {
			newFriendButton.setText("新的好友");
		}
		newFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}
				mMainModeManager.showNext(mMainModeManager.mNewFriendsFragment);
			}
		});
	}

	View generateMoreMessageButtonView() {
		View moreMessageButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
		Button moreMessageButton = (Button) moreMessageButtonView
				.findViewById(R.id.button);
		moreMessageButton.setText("点击查看更多");
		moreMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}

			}
		});

		return moreMessageButtonView;

	}

	View generateFindMoreFriendButtonView() {
		View findMoreFriendButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
		Button findMoreFriendButton = (Button) findMoreFriendButtonView
				.findViewById(R.id.button);
		findMoreFriendButton.setText("找到更多密友");
		findMoreFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}
				mMainModeManager
						.showNext(mMainModeManager.mSearchFriendFragment);
			}
		});

		return findMoreFriendButtonView;
	}

	View generateCreateGroupButtonView() {

		View createGroupButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
		Button createGroupButton = (Button) createGroupButtonView
				.findViewById(R.id.button);
		createGroupButton.setText("新建分组");
		createGroupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}
				CircleMenu.showBack();
				final EditText circleName;
				new AlertDialog.Builder(getActivity())
						.setTitle("请输入分组名")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(circleName = new EditText(getActivity()))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										CircleMenu.show();
										if (circleName.getText().toString()
												.equals("")) {
											Alert.showMessage("密友圈名称不能为空");
											return;
										}
										app.networkHandler
												.connection(new CommonNetConnection() {

													@Override
													protected void settings(
															Settings settings) {
														settings.url = API.DOMAIN
																+ API.CIRCLE_ADDCIRCLE;
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"phone",
																app.data.user.phone);
														params.put(
																"accessKey",
																app.data.user.accessKey);
														params.put(
																"name",
																circleName
																		.getText()
																		.toString());
														settings.params = params;
													}

													@Override
													public void success(
															JSONObject jData) {
														DataUtil.getCircles(new GetDataListener() {
															@Override
															public void getSuccess() {
																// mAdapter.notifyDataSetChanged();
															}
														});

													}
												});
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										CircleMenu.show();
									}
								}).setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								CircleMenu.show();
							}
						}).show();
			}
		});

		return createGroupButtonView;

	}

	View generateMessageView(String lastChatFriendPhone) {
		View messageView = mInflater.inflate(
				R.layout.fragment_circles_messages_item, null);
		return messageView;
	}

	void notifyMessageView(View messageView, final Friend friend) {
		final ImageView head = (ImageView) messageView
				.findViewById(R.id.iv_head);
		TextView nickName = (TextView) messageView
				.findViewById(R.id.tv_nickname);
		TextView lastChatMessage = (TextView) messageView
				.findViewById(R.id.tv_lastchat);
		TextView notReadCount = (TextView) messageView
				.findViewById(R.id.tv_notread);

		nickName.setText(friend.nickName);
		Message lastMessage = friend.messages.get(friend.messages.size() - 1);
		if (lastMessage.contentType.equals("text")) {
			lastChatMessage
					.setText(friend.messages.get(friend.messages.size() - 1).content);
		} else if (lastMessage.contentType.equals("image")) {
			lastChatMessage.setText(getString(R.string.text_picture));
		} else if (lastMessage.contentType.equals("voice")) {
			// lastChatMessage.setText(getString(R.string.text_voice));
			lastChatMessage.setText(getActivity().getResources().getString(
					R.string.text_voice));
		}
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});

		Integer notread = friend.notReadMessagesCount;

		if (notread != null) {
			if (notread > 0) {
				notReadCount.setVisibility(View.VISIBLE);
				notReadCount.setText(notread.toString());
			} else {
				notReadCount.setText("");
				notReadCount.setVisibility(View.GONE);
			}
		}
		messageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return;
				}
				mMainModeManager.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
				mMainModeManager.mChatFragment.mNowChatFriend = friend;
				mMainModeManager.showNext(mMainModeManager.mChatFragment);
			}
		});
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		if ((i + 1) % 6 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 4) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		}
		return position;
	}

	class FriendHolder {
		Position position;
		View view;
		String phone = "";
		int index;

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				if (o instanceof FriendHolder) {
					FriendHolder h = (FriendHolder) o;
					if (phone.equals(h.phone)) {
						flag = true;
					}
				} else if (o instanceof String) {
					String s = (String) o;
					if (phone.equals(s)) {
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	public Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();

	public void resolveFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);
			friendHolder.position = switchPosition(i);
			friendHolder.index = i;
		}
	}

	public void setFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) dp2px(55f),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;

			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	class ModifyFriend {
		public Map<String, List<String>> moveIn = new HashMap<String, List<String>>();
		public Map<String, List<String>> moveOut = new HashMap<String, List<String>>();

		public void clear() {
			moveIn.clear();
			moveOut.clear();
		}
	}

	ModifyFriend modifyFriend = new ModifyFriend();

	Map<String, View> tempViewMap = new HashMap<String, View>();
	public Map<String, FriendHolder> tempFriendHolders = new Hashtable<String, CirclesFragment.FriendHolder>();

	View generateCircleView() {
		final View circleView = mInflater
				.inflate(R.layout.fragment_panel, null);
		return circleView;
	}

	void notifyCircleView(final View circleView, Circle circle,
			CircleHolder circleHolder) {
		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final RelativeLayout container = (RelativeLayout) circleView
				.findViewById(R.id.viewContainer);

		List<String> phones = circle.phones;
		Map<String, Friend> friends = app.data.friends;

		for (int i = 0; i < phones.size(); i++) {
			final Friend friend = friends.get(phones.get(i));
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders
					.remove(index) : null);
			View convertView;
			if (friendHolder == null) {
				if (tempFriendHolders.get(friend.phone) != null) {
					return;
				}
				convertView = generateFriendView(friend);
				friendHolder = new FriendHolder();
				friendHolder.phone = friend.phone;
				friendHolder.view = convertView;
				final FriendHolder clickHolder = friendHolder;
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View holderView) {
						if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
							return;
						}
						if (mode.equals("normal")) {
							mMainModeManager.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
							mMainModeManager.mChatFragment.mNowChatFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mChatFragment);
						} else if (mode.equals("edit")) {
							final View friendView = generateFriendView(friend);
							final View animationView = generateFriendView(friend);
							tempFriendScroll.smoothScrollTo(0, 0);
							int[] location = new int[2];
							holderView.getLocationInWindow(location);

							CircleHolder circleHolder = circleHolders
									.get(circles.get(currentEditPosition));

							if (!isCopy) {
								// change
								circleHolder.friendHolders.remove(clickHolder);

								View circleView = views.get(circles
										.get(currentEditPosition));
								RelativeLayout friendContainer = (RelativeLayout) circleView
										.findViewById(R.id.viewContainer);

								// change
								friendContainer.removeView(holderView);

								int animationFromIndex = clickHolder.index;
								int animationCount = 6 - animationFromIndex % 6;

								for (int i = 0; i < animationCount; i++) {
									int index = animationFromIndex + i;
									if (index < circleHolder.friendHolders
											.size()) {
										View view = circleHolder.friendHolders
												.get(index).view;
										if (index % 6 == 2) {
											view.startAnimation(friendToPreLineAnimation);
										} else {
											view.startAnimation(friendToLeftAnimation);
										}
									}
								}
								// change
								resolveFriendsPositions(circleHolder);
								setFriendsPositions(circleHolder);
								tempFriendHolders
										.put(friend.phone, clickHolder);

								String fromRid = circles.get(
										currentEditPosition).substring(6);
								if (!fromRid.equals("-1")) {
									List<String> phones = modifyFriend.moveOut
											.get(fromRid);
									if (phones == null) {
										modifyFriend.moveOut
												.put(fromRid,
														(phones = new ArrayList<String>()));
									}
									phones.add(friend.phone);
								}

								resetSaveStatus();
							} else {
								FriendHolder newHolder = new FriendHolder();
								newHolder.view = generateFriendView(friend);
								newHolder.view.setOnClickListener(this);
								newHolder.phone = friend.phone;
								tempFriendHolders.put(friend.phone, newHolder);
							}

							View tempFriendView;
							if ((tempFriendView = tempViewMap.get(friend.phone)) != null) {
								tempFriendsList.removeView(tempFriendView);
							}

							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									(int) dp2px(55f),
									android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
							params.leftMargin = location[0];
							params.topMargin = location[1] - 50;
							animationView.setLayoutParams(params);
							animationLayout.addView(animationView);

							LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
									(int) dp2px(55f),
									LinearLayout.LayoutParams.WRAP_CONTENT);
							tempParams.leftMargin = (int) dp2px(20);
							friendView.setVisibility(View.INVISIBLE);
							if (tempFriendsList.getChildCount() == 0) {
								tempParams.rightMargin = (int) dp2px(75);
							}
							tempViewMap.put(friend.phone, friendView);
							tempFriendsList.addView(friendView, 0, tempParams);

							friendView
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View view) {
											resetSaveStatus();
											View circleView = views.get(circles
													.get(currentEditPosition));
											RelativeLayout friendContainer = (RelativeLayout) circleView
													.findViewById(R.id.viewContainer);
											friendContainer.scrollTo(0, 0);

											final FriendHolder friendHolder = tempFriendHolders
													.remove(friend.phone);

											CircleHolder circleHolder = circleHolders.get(circles
													.get(currentEditPosition));

											int friendIndex;
											if ((friendIndex = circleHolder.friendHolders
													.indexOf(friendHolder)) != -1) {
												FriendHolder removeHolder = circleHolder.friendHolders
														.remove(friendIndex);
												friendContainer
														.removeView(removeHolder.view);
											} else {
												String toRid = circles.get(
														currentEditPosition)
														.substring(6);
												if (!toRid.equals("-1")) {
													if (modifyFriend.moveOut
															.get(toRid) == null
															|| !modifyFriend.moveOut
																	.get(toRid)
																	.remove(friend.phone)) {
														List<String> phones = modifyFriend.moveIn
																.get(toRid);
														if (phones == null) {
															modifyFriend.moveIn
																	.put(toRid,
																			(phones = new ArrayList<String>()));
														}
														phones.add(0,
																friend.phone);
													}
												}
											}

											friendContainer
													.addView(friendHolder.view);

											circleHolder.friendHolders.add(0,
													friendHolder);

											resolveFriendsPositions(circleHolder);
											setFriendsPositions(circleHolder);

											friendHolder.view
													.setVisibility(View.INVISIBLE);

											LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
													(int) dp2px(55f),
													LinearLayout.LayoutParams.WRAP_CONTENT);
											tempParams.leftMargin = (int) dp2px(20);
											tempParams.rightMargin = (int) dp2px(75);
											if (tempFriendsList.getChildCount() != 0) {
												tempFriendsList
														.getChildAt(
																tempFriendsList
																		.getChildCount() - 1)
														.setLayoutParams(
																tempParams);
											}

											for (int i = 1; i < circleHolder.friendHolders
													.size(); i++) {
												if (i > 6) {
													break;
												}
												View friendView = circleHolder.friendHolders
														.get(i).view;
												if (i == 3) {
													friendView
															.startAnimation(friendToNextLineAnimation);
												} else if (i == 6) {
													friendView
															.startAnimation(lastFriendToRightAnimation);
												} else {
													friendView
															.startAnimation(friendToRightAnimation);
												}
											}

											int[] location = new int[2];
											view.getLocationOnScreen(location);

											RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
													(int) dp2px(55f),
													android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
											params.leftMargin = location[0];
											params.topMargin = location[1] - 50;
											animationView
													.setLayoutParams(params);

											animationLayout
													.addView(animationView);

											TranslateAnimation moveToCircleAnimation = new TranslateAnimation(
													0,
													dp2px(46) - location[0],
													0,
													dp2px(75)
															- (location[1] - 50));
											moveToCircleAnimation
													.setDuration(270);
											moveToCircleAnimation
													.setAnimationListener(new AnimationAdapter() {
														@Override
														public void onAnimationEnd(
																Animation animation) {
															animationLayout
																	.removeView(animationView);
															friendHolder.view
																	.setVisibility(View.VISIBLE);
														}
													});
											animationView
													.startAnimation(moveToCircleAnimation);

											int count = tempFriendsList
													.getChildCount();
											int index = 0;
											for (int i = 0; i < count; i++) {
												View v = tempFriendsList
														.getChildAt(i);
												if (v.equals(view)) {
													index = i;
													break;
												}
											}
											tempFriendsList.removeView(view);
											for (int i = index; i < count - 1; i++) {
												View v = tempFriendsList
														.getChildAt(i);
												v.startAnimation(allTempFriendMoveToLeft);
											}
										}
									});

							int count = tempFriendsList.getChildCount();
							for (int i = 1; i < count; i++) {
								tempFriendsList.getChildAt(i).startAnimation(
										allTempFriendMoveToRight);
							}

							int currnetX = (int) dp2px(20);
							int currentY = animationLayout.getHeight()
									- (int) dp2px(155);

							TranslateAnimation moveToTempListAnimation = new TranslateAnimation(
									0, currnetX - location[0], 0, currentY
											- (location[1] - 50));
							moveToTempListAnimation.setDuration(270);
							moveToTempListAnimation
									.setAnimationListener(new AnimationAdapter() {
										@Override
										public void onAnimationEnd(
												Animation animation) {
											animationLayout
													.removeView(animationView);
											friendView
													.setVisibility(View.VISIBLE);
										}
									});
							animationView
									.startAnimation(moveToTempListAnimation);

						}
					}
				});

				container.addView(convertView);

			}

			circleHolder.friendHolders.add(i, friendHolder);

		}

		final GestureDetector detector = new GestureDetector(getActivity(),
				new SimpleOnGestureListener() {
					float x0 = 0;
					float dx = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						x0 = e.getRawX();
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						if (touchEvnetStatus == TouchEvnetStatus.MOVING_X
								|| touchEvnetStatus == TouchEvnetStatus.MOVING_Y) {
							return;
						}
						switchToEditMode(circleView);
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						dx = e2.getRawX() - x0;
						if (touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
							container.scrollBy(-(int) (dx), 0);
							x0 = e2.getRawX();
						}
						return true;
					}
				});

		container.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		circleView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
					return false;
				}
				switchToEditMode(v);
				return true;
			}
		});
		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

	}

	TranslateAnimation friendToLeftAnimation;
	TranslateAnimation friendToRightAnimation;

	TranslateAnimation friendToNextLineAnimation;
	TranslateAnimation friendToPreLineAnimation;
	TranslateAnimation allTempFriendMoveToLeft;
	TranslateAnimation allTempFriendMoveToRight;

	TranslateAnimation lastFriendToLeftAnimation;
	TranslateAnimation lastFriendToRightAnimation;

	void circleViewCommonAnimation() {
		lastFriendToLeftAnimation = new TranslateAnimation(dp2px(103), 0, 0, 0);
		lastFriendToLeftAnimation.setStartOffset(150);
		lastFriendToLeftAnimation.setDuration(120);

		lastFriendToRightAnimation = new TranslateAnimation(dp2px(-120), 0,
				dp2px(100), dp2px(100));
		lastFriendToRightAnimation.setStartOffset(150);
		lastFriendToRightAnimation.setDuration(120);

		friendToLeftAnimation = new TranslateAnimation(dp2px(103), 0, 0, 0);
		friendToLeftAnimation.setStartOffset(150);
		friendToLeftAnimation.setDuration(120);
		// friendToLeftAnimation.setAnimationListener(new AnimationAdapter() {
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// }
		// });

		friendToRightAnimation = new TranslateAnimation(dp2px(-103), 0, 0, 0);

		friendToRightAnimation.setStartOffset(150);
		friendToRightAnimation.setDuration(120);
		// friendToRightAnimation.setAnimationListener(new AnimationAdapter() {
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// }
		// });

		friendToNextLineAnimation = new TranslateAnimation(dp2px(206), 0,
				dp2px(-100), 0);

		friendToNextLineAnimation.setStartOffset(150);
		friendToNextLineAnimation.setDuration(120);
		// friendToNextLineAnimation.setAnimationListener(new AnimationAdapter()
		// {
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// }
		// });

		friendToPreLineAnimation = new TranslateAnimation(dp2px(-206), 0,
				dp2px(100), 0);

		friendToPreLineAnimation.setStartOffset(150);
		friendToPreLineAnimation.setDuration(120);
		// friendToPreLineAnimation.setAnimationListener(new AnimationAdapter()
		// {
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// }
		// });

		allTempFriendMoveToLeft = new TranslateAnimation(dp2px(75), 0, 0, 0);
		allTempFriendMoveToLeft.setStartOffset(150);
		allTempFriendMoveToLeft.setDuration(120);

		allTempFriendMoveToRight = new TranslateAnimation(-dp2px(75), 0, 0, 0);
		allTempFriendMoveToRight.setStartOffset(150);
		allTempFriendMoveToRight.setDuration(120);
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(
				R.layout.fragment_circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView
				.findViewById(R.id.tv_nickname);
		nickname.setText(friend.nickName);
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});
		return convertView;
	}
}
