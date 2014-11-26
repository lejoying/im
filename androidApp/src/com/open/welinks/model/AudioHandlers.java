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

import com.open.welinks.customListener.AudioListener;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioHandlers {
	private FileHandlers fileHandlers = FileHandlers.getInstance();
	// private Data data = Data.getInstance();

	private static final int SAMPLE_RATE = 11025;// 8000 11025 32000 44100

	private short[] mRecordData;
	private short[] mPlayData;
	private byte[] mRecordProcessedData;
	private byte[] mPlayProcessedData;
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private File audioFolder;
	private File raw;
	private AudioRecord mAudioRecord;
	private AudioTrack mAudioTrack;
	private PlayAudioThread mPlayAudioThread;
	private AudioListener mAudioListener;
	private int mPrimePlaySize = 0, mRecordReadSize = 38;
	private long recorderStartTime, recorderEndTime;
	private int mRecorderMinBufferSize = 0;

	private int mSpeexEncodeFrameSize = 0, mSpeexDecodeFrameSize = 0;

	private static Speex speex;

	public static AudioHandlers handlers;
	String tag = "audio";

	public AudioHandlers() {
		mSpeexEncodeFrameSize = speex.getEncodeFrameSize();
		mSpeexDecodeFrameSize = speex.getDecodeFrameSize();
		Log.e(tag, "mSpeexEncodeFrameSize:" + mSpeexEncodeFrameSize + "          mSpeexDecodeFrameSize:" + mSpeexDecodeFrameSize);
		audioFolder = new File(fileHandlers.sdcardVoiceFolder.getAbsolutePath());
		if (!audioFolder.exists()) {
			audioFolder.mkdir();
		}
	}

	public static AudioHandlers getInstance() {
		speex = new Speex();
		if (handlers == null) {
			handlers = new AudioHandlers();
		}
		return handlers;
	}

	public void setAudioListener(AudioListener mAudioListener) {
		this.mAudioListener = mAudioListener;
	}

	public void startRecording() {
		if (isRecording) {
			return;
		}
		if (mAudioRecord == null) {
			initRecorder();
		}
		getFilePath();
		new RecordAudioThread().start();
		isRecording = true;
	}

	public String stopRecording() {
		if (mAudioRecord != null) {
			mAudioRecord.stop();
			isRecording = false;
			this.closeRecord();
		}
		int time = (int) ((recorderEndTime - recorderStartTime) / 1000);
		return raw.getAbsolutePath() + "@" + time;
	}

	public void releaseRecording() {
		mAudioRecord.stop();
		isRecording = false;
		this.closeRecord();
		raw.delete();
	}

	private void closeRecord() {
		if (mAudioRecord != null)
			mAudioRecord.release();
		mAudioRecord = null;
	}

	public void prepareVoice(String fileName, boolean play) {
		File file = new File(fileName);
		if (!file.exists()) {
			fileHandlers.downloadVoiceFile(file, fileName, play);
		}
	}

	public void prepareVoice(String fileName) {
		prepareVoice(fileName, false);
	}

	public void startPlay(String fileName) {
		File file = null;
		if (isPlaying) {
			stopPlay();
		} else {
			file = new File(audioFolder, fileName);
			if (!file.exists()) {
				prepareVoice(fileName, true);
			} else {
				initAudioTrack();
				isPlaying = true;
				startPlayThread(file);
			}
		}
	}

	public void stopPlay() {
		isPlaying = false;
		stopPlayThread();
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
		mRecordProcessedData = new byte[mSpeexEncodeFrameSize];

	}

	private void initAudioTrack() {
		mPrimePlaySize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mPrimePlaySize * 2, AudioTrack.MODE_STREAM);
		mPlayData = new short[mPrimePlaySize];
		mPlayProcessedData = new byte[mSpeexDecodeFrameSize];
	}

	private void getFilePath() {
		try {
			String fileName = String.valueOf(System.currentTimeMillis());
			raw = new File(audioFolder, fileName);
			raw.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean isPlaying() {
		return isPlaying;
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

	private void stopPlayThread() {
		if (mPlayAudioThread != null) {
			mPlayAudioThread.interrupt();
			mPlayAudioThread = null;
		}
	}

	class PlayAudioThread extends Thread {
		private DataInputStream inStream;

		public PlayAudioThread(DataInputStream inStream) {
			this.inStream = inStream;

			try {
				Log.i(tag, "available:" + inStream.available());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			mAudioTrack.play();
			int total = 0;
			while (isPlaying) {
				try {
					int readSize = inStream.read(mPlayProcessedData, 0, mRecordReadSize);
					int decodeSize = speex.decode(mPlayProcessedData, mPlayData, readSize);
					int size = mAudioTrack.write(mPlayData, 0, decodeSize);
					Log.i(tag, "readSize:" + readSize + "    decodeSize:" + decodeSize + "   size:" + size + "    total:" + total);
					if (inStream.available() <= 0) {
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
			isPlaying = false;
			mAudioTrack.stop();
		}

		@Override
		public void interrupt() {
			isPlaying = false;
			mAudioTrack.stop();
			super.interrupt();
		}
	}

	class RecordAudioThread extends Thread {

		@Override
		public void run() {
			mAudioRecord.startRecording();
			new EncodeAudioThread().start();
			recorderStartTime = System.currentTimeMillis();
			while (isRecording) {
				int readSize = mAudioRecord.read(mRecordData, 0, mRecorderMinBufferSize);
				Log.v(tag, "pushEncodeData: " + readSize);
				speex.pushEncodeData(mRecordData, readSize);

				Log.i(tag, "readSize:" + readSize + "    mRecorderMinBufferSize:" + mRecorderMinBufferSize);
				if (mRecordReadSize != readSize) {
					mRecordReadSize = readSize;
					Log.i(tag, "readSize:" + readSize + "    mRecordReadSize:" + mRecordReadSize);
				}
				int volume = 0;
				if (mAudioListener != null) {
					mAudioListener.onRecording((int) Math.abs(volume / (float) readSize) / 10000 >> 1);
				}
			}
			recorderEndTime = System.currentTimeMillis();
		}
	}

	class EncodeAudioThread extends Thread {
		@Override
		public void run() {
			DataOutputStream output = null;
			try {
				output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(raw)));
				int encodeSize = 0;
				while (isRecording) {
					Log.v(tag, "encodeFrame");
					encodeSize = speex.encodeFrame(mRecordProcessedData);
					if (encodeSize > 0) {
						output.write(mRecordProcessedData, 0, encodeSize);
						Log.d(tag, "encodeSize:" + encodeSize + "    mSpeexFrameSize:" + mSpeexEncodeFrameSize);
					} else {
						if (!isRecording) {
							break;
						} else {
							sleep(50);
						}
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
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
