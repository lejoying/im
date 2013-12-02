package com.lejoying.mc.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.utils.MCNetTools;

public class RegisterCodeFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_code;
	private Button mView_next;
	private TextView mView_sendcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_registercode, null);
		mView_code = (EditText) mContent.findViewById(R.id.et_code);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);

		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		canSend = true;
		reSend();

		mView_sendcode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (canSend) {
					reSend();
				}
			}
		});

		return mContent;
	}

	private int resendTime;

	private Timer timer;

	private Handler handler = MCNetTools.handler;

	private boolean canSend;

	private void reSend() {
		resendTime = 60;
		timer = new Timer();

		if (canSend) {
			canSend = false;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (resendTime == 0) {
								mView_sendcode.setText("重新发送");
								timer.cancel();
								canSend = true;
							} else {
								mView_sendcode.setText("重新发送(" + resendTime
										+ ")");
							}
							resendTime--;
						}
					});
				}
			}, 200, 1000);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			timer.cancel();
			getActivity().getSupportFragmentManager().popBackStack();
			getActivity().getSupportFragmentManager().popBackStack();
			mMCFragmentManager.relpaceToContent(new RegisterPassFragment(),
					true);
			break;
		case R.id.tv_sendcode:

			break;
		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_code;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println(intent.getIntExtra("remain", 0));
	}
}
