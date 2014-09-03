package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TestHttp;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.TempData.ImageBean;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.DownloadOssFileView;
import com.open.welinks.view.DownloadOssFileView.TransportingList.TransportingItem;

public class DownloadOssFileController {
	public Data data = Data.getInstance();
	public static ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public static String tag = "DownloadOssFileController";

	public Runnable animationRunnable;

	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public DownloadListener downloadListener;

	public OnClickListener onClickListener;

	String[] paths;

	public DownloadOssFileController(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();
	}

	public void onCreate() {
		String Oss_Host = "http://images5.we-links.com/mutilpart/";
		ArrayList<ImageBean> imagesSource = new ArrayList<ImageBean>();
		paths = new String[] { Oss_Host + "20F5B7FCE06E5CC24B0B875389A55AF11023DC4D.jpg", Oss_Host + "2AFF1954C7B313A9BCE6A56B6CA1DEC77E96A6DC.png", Oss_Host + "2F5791D890001B3D41EB2FFFC5EB1448E1597F91.png", Oss_Host + "3097B1772863E8124BFDCD20F771F6D1547D1A9E.jpg", Oss_Host + "450E249AA2335F7499F6EDEED840A9DEDF01CDDE.jpg", Oss_Host + "957BED54FB76F947B43D66BC324C7A6AA68AD1F6.jpg", Oss_Host + "983EBA49BD1564E3B117059283CE59A5115CED3A.jpg", Oss_Host + "A0DD7F909178445CCDB34394D47D859F17C6A05D.jpg", Oss_Host + "55CCAABEBA40364550841D0E64EE17D663713F87.jpg", Oss_Host + "7905B516EA97EDAC71E6A02DA04392C2B632D6C5.jpg", Oss_Host + "3748A2E00E1FEBCADE100BC8BCA62694D9614618.jpg", Oss_Host + "A0DD7F909178445CCDB34394D47D859F17C6A05D.jpg", Oss_Host + "B750457DD14BFE0D1B92168A5ECD0E8742DA23FE.jpg",
				Oss_Host + "FD2D8F3F76D7CF2E9B655E0584042CE0CC04532B.jpg", Oss_Host + "ED22A28C7FAA543C22BA428A560836BF80C310FA.jpg" };
		int start = data.localStatus.localData.prepareDownloadImagesList.size();
		if (start < paths.length) {
			int length = start + 3;
			if (length > paths.length) {
				length = paths.length - start;
			}
			for (int i = start; i < start + 3; i++) {
				ImageBean bean = data.tempData.new ImageBean();
				bean.path = paths[i];
				imagesSource.add(bean);
			}
		}
		if (imagesSource != null) {
			data.localStatus.localData.prepareDownloadImagesList.addAll(imagesSource);
		}
	}

	public void initializeListeners() {
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.equals(thisView.addMonyImageUploadView)) {
					if (data.localStatus.localData.prepareDownloadImagesList.size() < paths.length) {
						onCreate();
						initializeListeners();
						thisView.initView();
						bindEvent();
					}
				}
			}
		};

		downloadListener = new DownloadListener() {

			@Override
			public void success(DownloadFile instance, int status) {
				// TODO
				String fileName = urlToLocalFileName(instance.url);
				File file = new File(sdFile, "test0/" + fileName);
				imageLoader.displayImage("file://" + file.getAbsolutePath(), instance.transportingItem.imageView, options);
			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
				// TODO
				instance.transportingItem.controlProgress.moveTo(precent);
				instance.transportingItem.text_transport_time_view.setText((instance.time.received - instance.time.start) + "ms");
				instance.transportingItem.text_file_size_view.setText(instance.imageBean.size / 1000 + "k");
			}

			@Override
			public void failure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	public void bindEvent() {
		thisView.addMonyImageUploadView.setOnClickListener(onClickListener);
		ArrayList<TransportingItem> transportingList = thisView.transportingList.transportingItems;
		for (int i = 0; i < transportingList.size() - 1; i++) {
			transportingList.get(i).downloadFile.setDownloadFileListener(downloadListener);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			thisActivity.finish();
		}
		boolean flag = false;
		return flag;
	}

	int targetPercentage = 30;
	int index = 0;
	TestHttp testHttp = new TestHttp();

	public boolean onOptionsItemSelected(MenuItem item) {

		return true;
	}

	public File sdFile = Environment.getExternalStorageDirectory();

	public DownloadFile downloadFile(String url) {
		String fileName = urlToLocalFileName(url);
		File file = new File(sdFile, "test0/" + fileName);
		DownloadFile downloadFile = new DownloadFile(url, file.getAbsolutePath());
		downloadFileList.addDownloadFile(downloadFile);
		return downloadFile;
	}

	public String urlToLocalFileName(String url) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		int index = fileName.lastIndexOf(".");
		String suffixName = fileName.substring(index);
		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			suffixName = ".osp";
		}
		fileName = fileName.substring(0, index) + suffixName;
		return fileName;
	}
}
