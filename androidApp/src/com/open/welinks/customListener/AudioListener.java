package com.open.welinks.customListener;

public interface AudioListener {
	public void onRecording(int volume);

	public void onRecorded(String filePath);

	public void onPrepared();

	public void onRelease();

	public void onPlayComplete();

	public void onPlayFail();
}
