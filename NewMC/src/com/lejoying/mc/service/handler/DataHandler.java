package com.lejoying.mc.service.handler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

import com.lejoying.mc.entity.Message;
import com.lejoying.mc.service.MainService;
import com.lejoying.mc.service.handler.MainServiceHandler.ServiceEvent;
import com.lejoying.mc.utils.MCDataTools;

public class DataHandler {

	private LayoutInflater mInflater;
	private Context mContext;
	private ServiceEvent mServiceEvent;

	DataHandler(Context context, ServiceEvent serviceEvent) {
		this.mContext = context;
		this.mServiceEvent = serviceEvent;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	protected void process(Intent intent) {
		int notify = intent.getIntExtra("NOTIFY", -1);
		switch (notify) {
		case MainService.WHAT_MESSAGELIST:
			break;
		case MainService.WHAT_CHATMESSAGE:

			break;
		case MainService.WHAT_FRIEND:

			break;
		default:
			break;
		}
	}
}
