package com.open.welinks.model;

import java.util.LinkedList;

import com.open.lib.MyLog;

public class MyLinkedListQueue<E> extends LinkedList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String tag = "MyLinkedListQueue";
	public MyLog log = new MyLog(tag, true);

	public boolean isRunning = false;

	public MyLinkedListQueue<E> queue;

	public Runnable currentRunnable;

	public MyLinkedListQueue() {
		queue = this;
	}

	public E offerE(E myFile) {
		this.offer(myFile);
		// this.push(myFile);
		log.e("isRunning1:" + isRunning);
		if (!isRunning) {
			isRunning = true;
			log.e("isRunning2:" + isRunning);
			log.e("currentRunnable1:" + currentRunnable);
			new Thread(currentRunnable).start();
			log.e("currentRunnable2:" + currentRunnable);
		}
		return myFile;
	}

	public E takeE() throws Exception {
		E myFile = null;
		if (size() > 0) {
			myFile = this.poll();
			// this.pop();
		} else {
			log.e("isRunning3:" + isRunning);
			if (isRunning) {
				isRunning = false;
				log.e("isRunning4:" + isRunning);
			}
		}
		return myFile;
	}
}
