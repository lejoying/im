package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class SearchFriendFragment extends BaseListFragment {

	App app = App.getInstance();
	LayoutInflater mInflater;

	public static SearchFriendFragment instance;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		mInflater = inflater;
		mMCFragmentManager.showCircleMenuToTop(true, true);

		return inflater.inflate(R.layout.android_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new SearchFriendAdapter());
	}

	class SearchFriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (position) {
			case 1:
				convertView = mInflater.inflate(R.layout.f_group_panel, null);
				TextView tv_groupname = (TextView) convertView
						.findViewById(R.id.tv_groupname);
				tv_groupname.setText("附近好友");
				break;
			case 2:
				convertView = mInflater.inflate(R.layout.f_searchfriend, null);
				final EditText mView_phone = (EditText) convertView
						.findViewById(R.id.et_phone);
				View mView_search = convertView.findViewById(R.id.btn_search);
				mView_search.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String phone = mView_phone.getText().toString();
						if (phone.equals("")) {
							showMsg("请输入好友手机号");
							return;
						}
						Bundle params = new Bundle();
						params.putString("phone", app.data.user.phone);
						params.putString("accessKey", app.data.user.accessKey);
						params.putSerializable("target", phone);
						MCNetTools.ajax(getActivity(), API.ACCOUNT_GET, params,
								MCHttpTools.SEND_POST, 5000,
								new ResponseListener() {

									@Override
									public void success(JSONObject data) {
										if (phone.equals(app.data.user.phone)) {
											try {
												MCDataTools.updateUser(data
														.getJSONObject("account"));
												app.businessCardStatus = app.SHOW_SELF;
												mMCFragmentManager
														.replaceToContent(
																new BusinessCardFragment(),
																true);
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											return;
										}
										if (app.data.friends.get(phone) != null) {

											app.tempFriend = app.data.friends
													.get(phone);
											app.businessCardStatus = app.SHOW_FRIEND;
											mMCFragmentManager
													.replaceToContent(
															new BusinessCardFragment(),
															true);

											return;
										}
										try {
											data.getString("失败原因");
											mMCFragmentManager
													.replaceToContent(
															new FriendNotFoundFragment(),
															true);
											return;
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										try {
											app.tempFriend = MCDataTools
													.generateFriendFromJSON(data
															.getJSONObject("account"));
											app.tempFriend.temp = true;
											app.businessCardStatus = app.SHOW_TEMPFRIEND;
											mMCFragmentManager
													.replaceToContent(
															new BusinessCardFragment(),
															true);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
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
				});

				break;
			case 3:
				convertView = mInflater.inflate(R.layout.f_button, null);
				((Button) convertView).setText("扫描名片");
				break;

			default:
				convertView = mInflater.inflate(R.layout.f_margin, null);
				break;
			}
			return convertView;
		}
	}

}
