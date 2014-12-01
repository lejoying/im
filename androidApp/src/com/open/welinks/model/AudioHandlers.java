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

	private static final int SAMPLE_RATE = 11025;// 8000 11025 22050 32000 44100

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
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		mSpeexEncodeFrameSize = speex.getEncodeFrameSize();
		mSpeexDecodeFrameSize = speex.getDecodeFrameSize();
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
		if (mAudioRecord != null) {
			this.closeRecord();
		}
		initRecorder();
		getFilePath();
		new RecordAudioThread().start();
		isRecording = true;
	}

	public String stopRecording() {
		recorderEndTime = System.currentTimeMillis();
		if (mAudioRecord != null) {
			mAudioRecord.stop();
			this.closeRecord();
		}
		isRecording = false;
		int time = (int) ((recorderEndTime - recorderStartTime) / 1000);
		return raw.getAbsolutePath() + "@" + time + "@" + mRecordReadSize;
	}

	public void releaseRecording() {
		if (mAudioRecord != null) {
			mAudioRecord.stop();
		}
		isRecording = false;
		this.closeRecord();
		raw.delete();
	}

	private void closeRecord() {
		if (mAudioRecord != null)
			mAudioRecord.release();
		mAudioRecord = null;
	}

	public void prepareVoice(String fileName, int recordReadSize, boolean play) {
		File file = new File(fileName);
		if (!file.exists()) {
			fileHandlers.downloadVoiceFile(file, fileName, recordReadSize, play);
		}
	}

	public void prepareVoice(String fileName, int recordReadSize) {
		prepareVoice(fileName, recordReadSize, false);
	}

	public void startPlay(String fileName, int recordReadSize) {
		File file = null;
		if (isPlaying) {
			stopPlay();
		} else {
			file = new File(audioFolder, fileName);
			if (!file.exists()) {
				prepareVoice(fileName, recordReadSize, true);
			} else {
				initAudioTrack();
				isPlaying = true;
				mRecordReadSize = recordReadSize;
				startPlayThread(file);
			}
		}
	}

	public void startPlay(String fileName, String recordReadSize) {
		startPlay(fileName, Integer.valueOf(recordReadSize));
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
		// for (int rate : new int[] { 8000, 11025, 22050, 32000, 44100 }) {
		// for (short format : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
		// for (short config : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
		// if (mAudioRecord == null || mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
		// mRecorderMinBufferSize = AudioRecord.getMinBufferSize(rate, config, format);
		// Log.e(tag, "rate:" + rate + "             format:" + format + "             config:" + config + "             minBufferSize:" + mRecorderMinBufferSize);
		// if (mRecorderMinBufferSize != AudioRecord.ERROR_BAD_VALUE) {
		// mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, config, format, mRecorderMinBufferSize);
		// }
		// }
		// }
		// }
		// }
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
					// Log.i(tag, "readSize:" + readSize + "    decodeSize:" + decodeSize + "   size:" + size + "    total:" + total);
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
				// Log.v(tag, "pushEncodeData: " + readSize);
				speex.pushEncodeData(mRecordData, readSize);

				// Log.i(tag, "readSize:" + readSize + "    mRecorderMinBufferSize:" + mRecorderMinBufferSize);
				if (mRecordReadSize != readSize) {
					mRecordReadSize = readSize;
					// Log.i(tag, "readSize:" + readSize + "    mRecordReadSize:" + mRecordReadSize);
				}
				int volume = 0;
				if (mAudioListener != null) {
					mAudioListener.onRecording((int) Math.abs(volume / (float) readSize) / 10000 >> 1);
				}
			}
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
					// Log.v(tag, "encodeFrame");
					encodeSize = speex.encodeFrame(mRecordProcessedData);
					if (encodeSize > 0) {
						output.write(mRecordProcessedData, 0, encodeSize);
						// Log.d(tag, "encodeSize:" + encodeSize + "    mSpeexFrameSize:" + mSpeexEncodeFrameSize);
					} else {
						sleep(50);
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
