package com.lejoying.wxgs.activity.view;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lejoying.wxgs.app.handler.FileHandler.GifMovie;

@SuppressLint("ViewConstructor")
public class SampleView extends View {

	private Movie mMovie;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private long mMovieStart;
	private int height=0, width=0;
	@SuppressWarnings("unused")
	private byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
	}
	public SampleView(Context context,
			GifMovie gifMovie,int height,int  width) {
		super(context);
		setFocusable(true);
		this.height=height;
		this.width=width;
		initData(gifMovie);
		}
	public SampleView(Context context,
			GifMovie gifMovie) {
		super(context);
		setFocusable(true);
		initData(gifMovie);
		// File file = new File(sdFile, fileName);
		// try {
		// is = new FileInputStream(file);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		// is = context.getResources().openRawResource(R.drawable.tusiji_1);
//		if (true) {
//			// mMovie = Movie.decodeStream(is);
//			mMovie = gifMovie.movie;
//		} else {
//			// byte[] bytes;
//			// try {
//			// bytes = new byte[is.available()];
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//			// try {
//			// is.read(bytes);
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//			byte[] array = gifMovie.bytes;
//			mMovie = Movie.decodeByteArray(array, 0, array.length);
//		}
//		int w = mMovie.width();
//		int h = mMovie.height();
//		// int[] pixels = new int[w*h];
//		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//		mCanvas = new Canvas(mBitmap);
	}
	void initData(GifMovie gifMovie){
		if (true) {
			mMovie = gifMovie.movie;
		} else {
//			byte[] array = gifMovie.bytes;
//			mMovie = Movie.decodeByteArray(array, 0, array.length);
		}
		int w = mMovie.width();
		int h = mMovie.height();
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) { // first time
			mMovieStart = now;
		}
		if (mMovie != null) {
			int dur = mMovie.duration();
			if (dur == 0) {
				dur = 1000;
			}
			int relTime = (int) ((now - mMovieStart) % dur);
			mMovie.setTime(relTime);
			mMovie.draw(mCanvas, 0, 0);
			Bitmap bitmap = Bitmap.createScaledBitmap(mBitmap, 180, 180, true);
			Bitmap.createScaledBitmap(mBitmap, 100, 100, false);
			int w = mMovie.width();
			int h = mMovie.height();
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			if(height==0&&width==0){
				canvas.drawBitmap(bitmap, 230, 0, null);
			}else{
				canvas.drawBitmap(bitmap, (height-h)/2, (width-w)/2, null);
			}
			invalidate();
		}
	}
}
