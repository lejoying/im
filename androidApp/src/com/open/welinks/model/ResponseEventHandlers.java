package com.open.welinks.model;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.HttpClient;
import com.open.welinks.view.ViewManage;

public class ResponseEventHandlers {

	public Data data = Data.getInstance();

	public String tag = "ResponseEventHandlers";

	public ViewManage viewManage = ViewManage.getInstance();

	public static ResponseEventHandlers responseEventHandlers;

	public HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	public static ResponseEventHandlers getInstance() {
		if (responseEventHandlers == null) {
			responseEventHandlers = new ResponseEventHandlers();
		}
		return responseEventHandlers;
	}

	public void handleEvent(Event event) {
		if (event.event.equals("message")) {
			// TODO
		}
	}

	public static abstract class Response<T> {
		ResponseInfo<T> responseInfo;

		public Response(ResponseInfo<T> responseInfo) {
			this.responseInfo = responseInfo;
		}

		public abstract void handleResponse(InputStream is);
	}

	public static final class ResponseInfoHandler {

		public static final int WORKTHREADCOUNT_MIN = 1;
		public static final int WORKTHREADCOUNT_MAX = 50;
		public int mWorkThreadCount;

		Queue<ResponseInfo<String>> mResponseQueue;

		public ResponseInfoHandler(int concurrenceCount) {
			mResponseQueue = new LinkedList<ResponseInfo<String>>();
			if (concurrenceCount > WORKTHREADCOUNT_MAX) {
				mWorkThreadCount = WORKTHREADCOUNT_MAX;
			} else if (concurrenceCount < WORKTHREADCOUNT_MIN) {
				mWorkThreadCount = WORKTHREADCOUNT_MIN;
			} else {
				mWorkThreadCount = concurrenceCount;
			}
			for (int i = 0; i < mWorkThreadCount; i++) {
				new ResponseHandlerWorkThread(i).start();
			}
		}

		public synchronized void exclude(ResponseInfo<String> responseInfo) {
			mResponseQueue.offer(responseInfo);
			notify();
		}

		synchronized ResponseInfo<String> getExcude() throws InterruptedException {
			if (mResponseQueue.size() == 0) {
				wait();
			}
			return mResponseQueue.poll();
		}

		class ResponseHandlerWorkThread extends Thread {

			public int id;

			boolean interrupt;

			public ResponseHandlerWorkThread(int id) {
				this.id = id;
			}

			@Override
			public void run() {
				while (!interrupt) {
					ResponseInfo<String> responseInfo = null;
					try {
						while ((responseInfo = getExcude()) == null)
							;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					responseEventHandlers.parseEvent(responseInfo);
				}
			}
		}
	}

	public class Event {
		public String 提示信息;
		public String event;
		public String evnet_content;
	}

	public class EventContent {
		public String phone;
		public String gid;
		public boolean operation;
	}

	public void parseEvent(ResponseInfo<String> responseInfo) {
		Event event = gson.fromJson(responseInfo.result, Event.class);
		responseEventHandlers.handleEvent(event);
	}
}
