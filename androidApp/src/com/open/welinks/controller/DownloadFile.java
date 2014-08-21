package com.open.welinks.controller;

import java.io.File;

import org.apache.http.Header;

import android.view.View;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.model.Data.TempData.ImageBean;
import com.open.welinks.view.DownloadOssFileView.TransportingList.TransportingItem;

public class DownloadFile {

	public String tag = "DownloadFile";

	public DownloadFile instance;

	HttpClient httpClient = HttpClient.getInstance();

	public String url = "";
	public String path = "";

	public HttpHandler<File> httpHandler;

	public DownloadListener downloadListener;

	public static int DOWNLOAD_DEFAULT = 0x00;
	public static int DOWNLOAD_PARAM_EMPTY = 0x01;
	public static int DOWNLOAD_START = 0x02;
	public static int DOWNLOAD_LOADINGING = 0x03;
	public static int DOWNLOAD_SUCCESS = 0x04;
	public static int DOWNLOAD_FAILED = 0x05;

	public int isDownloadStatus;

	public TransportingItem transportingItem;

	public ImageBean imageBean;

	public View view;

	public TimeLine time = new TimeLine();

	public int uploadPrecent;

	public DownloadFile(String url, String path) {
		this.url = url;
		this.path = path;
		this.isDownloadStatus = DOWNLOAD_DEFAULT;
	}

	public void startDownload() {
		instance = this;
		if (this.url.trim().equals("") || this.path.trim().equals("")) {
			isDownloadStatus = DOWNLOAD_PARAM_EMPTY;
			return;
		}
		HttpUtils httpUtils = new HttpUtils();
		// Log.e(tag, "-----startAAA-----");

		httpUtils.download(url, path, download);
	}

	public ResponseHandler<File> download = httpClient.new ResponseHandler<File>() {
		@Override
		public void onStart() {
			time.start = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_START;
			// Log.e(tag, "-----start-----");
		}

		@Override
		public void onConneced(Header[] headers) {
			if (imageBean != null) {
				if (imageBean.size == 0) {
					for (int i = 0; i < headers.length; i++) {
						Header header = headers[i];
						if (header.getName().equals("Content-Length")) {
							imageBean.size = Long.valueOf(header.getValue());
							downloadListener.loading(instance, uploadPrecent, isDownloadStatus);
							break;
						}
					}
				}
			}
			// Log.e(tag, "-----onConneced-----");
		};

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			time.received = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_LOADINGING;
			uploadPrecent = (int) ((((double) current / (double) total)) * 100);
			downloadListener.loading(instance, uploadPrecent, isDownloadStatus);
			// Log.e(tag, "-----onLoading-----");
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			time.received = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_SUCCESS;
			downloadListener.success(instance, isDownloadStatus);
			// Log.e(tag, "-----success-----" + responseInfo.statusCode);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			isDownloadStatus = DOWNLOAD_FAILED;
			// Log.d(tag, "onFailure: -----" + msg);
		}
	};

	public class TimeLine {

		public long start = 0; // 0
		public long startConnect = 0; // 1

		public long startSend = 0; // 2
		public long sent = 0; // 3

		public long startReceive = 0; // 4
		public long received = 0; // 5
	}

	public interface DownloadListener {
		public void loading(DownloadFile instance, int precent, int status);

		public void success(DownloadFile instance, int status);
	}

	public void setDownloadFileListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
}
