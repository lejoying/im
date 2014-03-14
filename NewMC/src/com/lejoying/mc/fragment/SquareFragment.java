package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.LocationData;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Group;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.AjaxInterface;
import com.lejoying.mc.utils.MCNetUtils.Settings;
import com.lejoying.utils.HttpUtils;

public class SquareFragment extends BaseFragment {

	View mContent;
	App app = App.getInstance();

	private TextView tv_nowsquare;
	private TextView tv_square_online;
	private EditText et_broadcast;
	private Button btn_send;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_square, null);
		tv_nowsquare = (TextView) mContent.findViewById(R.id.tv_nowsquare);
		tv_square_online = (TextView) mContent
				.findViewById(R.id.tv_square_online);
		et_broadcast = (EditText) mContent.findViewById(R.id.et_broadcast);
		btn_send = (Button) mContent.findViewById(R.id.btn_send);

		BDLocation bdLocation = app.locationHandler.mLocationClient
				.getLastKnownLocation();
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("longitude", bdLocation.getLongitude() + "");
		params.putString("latitude", bdLocation.getLatitude() + "");
		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.COMMUNITY_FIND;
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				JSONObject groupObject = null;

				try {
					groupObject = new JSONObject(jData.getString("group"));
					tv_square_online.setText("广场("
							+ (CharSequence) jData.getString("onlinecount")
							+ "人在线)");
					tv_nowsquare.setText((CharSequence) groupObject.get("name"));
					Group group = new Group();
					group.gid = (Integer) groupObject.get("gid");
					group.name = (String) groupObject.get("name");
					app.data.nowCommunity = group;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String send_content = et_broadcast.getText().toString()
						.trim();
				if (send_content != "") {
					/*
					 * Toast.makeText(getActivity(), send_content,
					 * Toast.LENGTH_SHORT).show();
					 */
					MCNetUtils.ajax(new AjaxAdapter() {

						@Override
						public void setParams(Settings settings) {
							final Bundle params = new Bundle();
							params.putString("phone", app.data.user.phone);
							params.putString("accessKey", "lejoying");
							params.putString("gid", app.data.nowCommunity.gid
									+ "");
							JSONObject message = new JSONObject();
							try {
								message.put("contentType", "text");
								message.put("content", send_content);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							params.putString("message", message.toString());
							settings.params = params;
							settings.url = API.SQUARE_SENDSQUAREMESSAGE;
						}

						@Override
						public void onSuccess(JSONObject jData) {
							try {
								Toast.makeText(
										getActivity(),
										jData.getString(getString(R.string.app_notice)),
										Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				}
			}
		});
		return mContent;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.squareFragment;
	}
}
