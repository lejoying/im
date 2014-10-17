package com.open.welinks.customListener;

import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;

public class OnUploadLoadingListListener {

	public String tag = "OnUploadLoadingListListener";

	public OnUploadLoadingListener uploadLoadingListener;

	public int currentUploadCount = 0;
	public int totalUploadCount = 0;

	public ShareMessage shareMessage;

	public OnUploadLoadingListListener instance;
	public OnUploadLoadingListListener instance2;

	public OnUploadLoadingListListener() {
		instance = this;
		instance2 = this;
		initializeListeners();
	}

	public void initializeListeners() {

		uploadLoadingListener = new OnUploadLoadingListener() {
			@Override
			public void onSuccess(UploadMultipart instance, int time) {
				super.onSuccess(instance, time);
				currentUploadCount++;
				if (currentUploadCount == totalUploadCount) {
					instance2.onSuccess(instance2);
				}
			}
		};
	}

	public void onSuccess(OnUploadLoadingListListener instance) {
	}
}
