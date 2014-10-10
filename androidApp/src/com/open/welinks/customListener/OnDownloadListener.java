package com.open.welinks.customListener;

import com.open.welinks.controller.DownloadFile;

public class OnDownloadListener {
	
	public void onLoadingStarted(DownloadFile instance, int precent, int status) {
	};

	public void onLoading(DownloadFile instance, int precent, int status) {
	};

	public void onSuccess(DownloadFile instance, int status) {
	};

	public void onFailure(DownloadFile instance, int status) {
	};
}
