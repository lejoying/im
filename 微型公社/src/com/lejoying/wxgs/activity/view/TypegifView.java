package com.lejoying.wxgs.activity.view;

import java.io.InputStream;

import com.lejoying.wxgs.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TypegifView extends View implements Runnable {
	gifOpenHelper gHelper;
	private boolean isStop = true;
	int delta;
	String title;

	Bitmap bmp;

	// construct - refer for java
	public TypegifView(Context context) {
		this(context, null);

	}

	// construct - refer for xml
	public TypegifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// �������
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.gifView);
		int n = ta.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = ta.getIndex(i);

			switch (attr) {
			case R.styleable.gifView_src:
				int id = ta.getResourceId(R.styleable.gifView_src, 0);
				setSrc(id);
				break;

			case R.styleable.gifView_delay:
				int idelta = ta.getInteger(R.styleable.gifView_delay, 1);
				setDelta(idelta);
				break;

			case R.styleable.gifView_stop:
				boolean sp = ta.getBoolean(R.styleable.gifView_stop, false);
				if (!sp) {
					setStop();
				}
				break;
			}

		}

		ta.recycle();
	}

	/**
	 * ����ֹͣ
	 * 
	 * @param stop
	 */
	public void setStop() {
		isStop = false;
	}

	/**
	 * ��������
	 */
	public void setStart() {
		isStop = true;

		Thread updateTimer = new Thread(this);
		updateTimer.start();
	}

	/**
	 * ͨ����Ʊ���õڼ���ͼƬ��ʾ
	 * 
	 * @param id
	 */
	public void setSrc(int id) {

		gHelper = new gifOpenHelper();
		gHelper.read(TypegifView.this.getResources().openRawResource(id));
		bmp = gHelper.getImage();// �õ���һ��ͼƬ
	}

	public void setInputStream(InputStream is) {
		gHelper = new gifOpenHelper();
		gHelper.read(is);
		bmp = gHelper.getImage();// �õ���һ��ͼƬ
	}

	public void setDelta(int is) {
		delta = is;
	}

	// to meaure its Width & Height
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		return gHelper.getWidth();
	}

	private int measureHeight(int measureSpec) {
		return gHelper.getHeigh();
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bmp, 0, 0, new Paint());
		bmp = gHelper.nextBitmap();

	}

	public void run() {
		while (isStop) {
			try {
				this.postInvalidate();
				Thread.sleep(gHelper.nextDelay() / delta);
			} catch (Exception ex) {

			}
		}
	}

}
