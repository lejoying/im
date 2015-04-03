package com.open.welinks.oss;

import java.io.File;

import org.apache.http.Header;

import android.view.View;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.lib.MyLog;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customListener.ThumbleListener;
import com.open.welinks.model.Data.TempData.ImageBean;
import com.open.welinks.model.SubData.ImageListener;

public class DownloadFile {

	public String tag = "DownloadFile";
	public MyLog log = new MyLog(tag, true);

	public DownloadFile instance;

	HttpClient httpClient = HttpClient.getInstance();

	public String url = "";
	public String path = "";

	public HttpHandler<File> httpHandler;

	public OnDownloadListener downloadListener;

	public static int DOWNLOAD_DEFAULT = 0x00;
	public static int DOWNLOAD_PARAM_EMPTY = 0x01;
	public static int DOWNLOAD_START = 0x02;
	public static int DOWNLOAD_LOADINGING = 0x03;
	public static int DOWNLOAD_SUCCESS = 0x04;
	public static int DOWNLOAD_FAILED = 0x05;

	public int isDownloadStatus;

	public ImageBean imageBean;

	public View view;
	public DisplayImageOptions options;

	public ThumbleListener thumbleListener;
	public ImageListener imageListener;

	public int type;
	public static int TYPE_HEAD_IMAGE = 0x01;
	public static int TYPE_THUMBLE_IMAGE = 0x02;
	public static int TYPE_GIF_IMAGE = 0x03;
	public static int TYPE_IMAGE = 0x04;

	public TimeLine time = new TimeLine();

	public int uploadPrecent;

	public long bytesLength = 0;

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
		// log.e(tag, "-----startAAA-----");

		httpUtils.download(url, path, download);
	}

	public ResponseHandler<File> download = httpClient.new ResponseHandler<File>() {
		@Override
		public void onStart() {
			time.start = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_START;
			// log.e(tag, "-----start-----");
		}

		@Override
		public void onConneced(Header[] headers) {
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				if (header.getName().equals("Content-Length")) {
					bytesLength = Long.valueOf(header.getValue());
					if (downloadListener != null) {
						downloadListener.onLoadingStarted(instance, uploadPrecent, isDownloadStatus);
					}
					break;
				}
			}
			if (imageBean != null) {
				if (imageBean.size == 0) {
					for (int i = 0; i < headers.length; i++) {
						Header header = headers[i];
						if (header.getName().equals("Content-Length")) {
							imageBean.size = Long.valueOf(header.getValue());
							if (downloadListener != null) {
								downloadListener.onLoadingStarted(instance, uploadPrecent, isDownloadStatus);
							}
							break;
						}
					}
				}
			}
			// log.e(tag, "-----onConneced-----");
		};

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			time.received = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_LOADINGING;
			uploadPrecent = (int) ((((double) current / (double) total)) * 100);
			if (downloadListener != null) {
				downloadListener.onLoading(instance, uploadPrecent, isDownloadStatus);
			}
			// log.e(tag, "-----onLoading-----");
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			time.received = System.currentTimeMillis();
			isDownloadStatus = DOWNLOAD_SUCCESS;
			if (downloadListener != null) {
				downloadListener.onSuccess(instance, isDownloadStatus);
			}
			// log.e(tag, "-----success-----" + responseInfo.statusCode);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			isDownloadStatus = DOWNLOAD_FAILED;
			if (downloadListener != null) {
				downloadListener.onFailure(instance, isDownloadStatus);
			}
			log.d(tag, "onFailure: -----" + msg);
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

	public void setDownloadFileListener(OnDownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
}
