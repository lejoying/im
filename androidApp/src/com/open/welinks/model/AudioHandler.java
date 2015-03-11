package com.open.welinks.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.open.welinks.customListener.AudioListener;

public class AudioHandler {

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	private static final int SAMPLE_RATE = 11025;// 8000 11025 22050 32000 44100

	private short[] mRecordData;
	private short[] mPlayData;
	private byte[] mRecordProcessedData;
	private byte[] mPlayProcessedData;
	private boolean isSend = false;
	private File raw;
	private AudioRecord mAudioRecord;
	private AudioTrack mAudioTrack;
	private PlayAudioThread mPlayAudioThread;
	private RecordAudioThread mRecordAudioThread;
	private AudioListener mAudioListener;
	private int mPrimePlaySize = 0, mRecordReadSize = 38;
	private long recorderStartTime, recorderEndTime;
	private int mRecorderMinBufferSize = 0;
	public int mSpeexEncodeFrameSize = 0, mSpeexDecodeFrameSize = 0;

	private static Speex speex;

	private enum Status {
		recording, inited, releaseing, released, playing, played
	}

	private Status recorderStatus = Status.released, playStatus = Status.played;

	public static AudioHandler handlers;
	String tag = "audio";

	public AudioHandler() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		mSpeexEncodeFrameSize = speex.getEncodeFrameSize();
		mSpeexDecodeFrameSize = speex.getDecodeFrameSize();
	}

	public static AudioHandler getInstance() {
		speex = new Speex();
		if (handlers == null) {
			handlers = new AudioHandler();
		}
		return handlers;
	}

	public void setAudioListener(AudioListener mAudioListener) {
		this.mAudioListener = mAudioListener;
	}

	public void startRecording() {
		if (recorderStatus != Status.released) {
			mAudioListener.onRecordFail();
			return;
		}
		getFilePath();
		initRecorder();
		if (recorderStatus == Status.inited) {
			// while (mRecordAudioThread.isAlive()) {
			//
			// }
			mRecordAudioThread = new RecordAudioThread();
			mRecordAudioThread.start();
		}
	}

	public void stopRecording() {
		recorderEndTime = System.currentTimeMillis();
		isSend = true;
		if (recorderStatus == Status.recording) {
			recorderStatus = Status.releaseing;
		} else {
			this.closeRecord();
		}
	}

	public void releaseRecording() {
		isSend = false;
		recorderStatus = Status.releaseing;
		this.closeRecord();
		raw.delete();
	}

	private void closeRecord() {
		if (mAudioRecord != null) {
			mAudioRecord.release();
		}
		mAudioRecord = null;
		recorderStatus = Status.released;
	}

	public void prepareVoice(String fileName, int recordReadSize, boolean play) {
		File file = new File(taskManageHolder.fileHandler.sdcardVoiceFolder, fileName);
		if (!file.exists()) {
			taskManageHolder.fileHandler.downloadVoiceFile(file, fileName, recordReadSize, play);
		} else {
			if (play) {
				startPlay(fileName, recordReadSize);
			}
		}
	}

	public void prepareVoice(String fileName, int recordReadSize) {
		prepareVoice(fileName, recordReadSize, false);
	}

	public void startPlay(String fileName, int recordReadSize) {
		File file = null;
		if (playStatus == Status.playing) {
			stopPlay();
		} else {
			file = new File(taskManageHolder.fileHandler.sdcardVoiceFolder, fileName);
			if (!file.exists()) {
				prepareVoice(fileName, recordReadSize, true);
			} else {
				initAudioTrack();
				playStatus = Status.playing;
				mRecordReadSize = recordReadSize;
				startPlayThread(file);
			}
		}
	}

	public void startPlay(String fileName, String recordReadSize) {
		startPlay(fileName, Integer.valueOf(recordReadSize));
	}

	public void stopPlay() {
		playStatus = Status.played;
	}

	public void releasePlyer() {
		speex.close();
		this.stopPlay();
		releaseAudioTrack();
	}

	private void initRecorder() {
		mRecorderMinBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mRecorderMinBufferSize);
		mRecordData = new short[mRecorderMinBufferSize];
		mRecordProcessedData = new byte[mRecorderMinBufferSize];
		recorderStatus = Status.inited;
	}

	private void initAudioTrack() {
		mPrimePlaySize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mPrimePlaySize * 2, AudioTrack.MODE_STREAM);
		mPlayData = new short[mPrimePlaySize];
		mPlayProcessedData = new byte[mPrimePlaySize];
	}

	private void getFilePath() {
		try {
			String fileName = String.valueOf(System.currentTimeMillis()) + ".osa";
			raw = new File(taskManageHolder.fileHandler.sdcardVoiceFolder, fileName);
			raw.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRecording() {
		if (recorderStatus == Status.recording) {
			return true;
		}
		return false;
	}

	public boolean isPlaying() {
		if (playStatus == Status.playing) {
			return true;
		}
		return false;
	}

	private void releaseAudioTrack() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}

	}

	private void startPlayThread(File file) {
		DataInputStream inStream = null;
		try {
			inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if (inStream != null) {
			mPlayAudioThread = new PlayAudioThread(inStream);
			mPlayAudioThread.start();
		}
	}

	class PlayAudioThread extends Thread {
		private DataInputStream inStream;

		public PlayAudioThread(DataInputStream inStream) {
			this.inStream = inStream;
		}

		@Override
		public void run() {
			mAudioTrack.play();
			while (playStatus == Status.playing) {
				try {
					int readSize = inStream.read(mPlayProcessedData, 0, mRecordReadSize);
					int decodeSize = speex.decode(mPlayProcessedData, mPlayData, readSize);
					int size = mAudioTrack.write(mPlayData, 0, decodeSize);
					if (inStream.available() <= 0) {
						playStatus = Status.played;
						if (mAudioListener != null) {
							mAudioListener.onPlayComplete();
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (mAudioListener != null) {
						mAudioListener.onPlayFail();
					}
					break;
				}
			}
			mAudioTrack.stop();
			mAudioListener.onPlayComplete();
		}

	}

	class RecordAudioThread extends Thread {

		@Override
		public void run() {
			try {
				if (mAudioRecord != null && recorderStatus == Status.inited) {
					mAudioListener.onRecordStarted();
					recorderStatus = Status.recording;
					mAudioRecord.startRecording();
					new EncodeAudioThread().start();
					recorderStartTime = System.currentTimeMillis();
					while (recorderStatus == Status.recording) {
						int readSize = mAudioRecord.read(mRecordData, 0, mRecorderMinBufferSize);
						if (mRecordData.length > 0 && mRecordData.length >= readSize) {
							speex.pushEncodeData(mRecordData, readSize);
						}
						// Log.i(tag, "readSize:" + readSize + "    mRecorderMinBufferSize:" + mRecorderMinBufferSize);
						int volume = 0;
						for (int i = 0; i < mRecordData.length; i++) {
							volume += mRecordData[i] * mRecordData[i];
						}
						if (mAudioListener != null) {
							mAudioListener.onRecording((int) Math.abs(volume / (float) readSize) / 10000 >> 1);
						}
						long duration = System.currentTimeMillis();
						if ((duration - recorderStartTime) / 1000 >= 60) {
							recorderEndTime = duration;
							isSend = true;
							break;
						}
					}
				} else {
					mAudioListener.onRecordFail();
				}
			} catch (Exception e) {
				Log.e(tag, "Exception" + e.toString());
			}
			recorderStatus = Status.releaseing;
			closeRecord();
		}
	}

	class EncodeAudioThread extends Thread {
		@Override
		public void run() {
			DataOutputStream output = null;
			try {
				output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(raw)));
				int encodeSize = 0;
				while (true) {
					encodeSize = speex.encodeFrame(mRecordProcessedData);
					if (mRecordReadSize != encodeSize && encodeSize != -1) {
						mRecordReadSize = encodeSize;
						// Log.i(tag, "encodeSize:" + encodeSize + "    mRecordReadSize:" + mRecordReadSize);
					}
					if (encodeSize > 0) {
						output.write(mRecordProcessedData, 0, encodeSize);
					} else if (recorderStatus == Status.recording) {
						sleep(50);
					} else {
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							output.close();
							int time = (int) ((recorderEndTime - recorderStartTime) / 1000);
							if (mAudioListener != null && isSend) {
								isSend = false;
								if (time < 1) {
									mAudioListener.onRecordFail();
								} else {
									while (recorderStatus != Status.released || mRecordAudioThread.isAlive()) {
										sleep(10);
									}
									mAudioListener.onRecorded(raw.getAbsolutePath() + "@" + time + "@" + mRecordReadSize);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
