package com.open.welinks.customListener;

public interface AudioListener {
	public void onRecording(int volume);

	public void onPrepared();

	public void onPlayComplete();

	public void onPlayFail();
}
