package cn.buaa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CropImageView extends ImageView {

	private int stop_x;
	private int stop_y;
	private int start_x;
	private int start_y;

	public CropImageView(Context context) {
		super(context);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();
			start_x = (int) event.getX();
			start_y = stop_y - this.getTop();
			System.out.println("down");
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println(stop_x + "__" + stop_y + "___" + start_x + "___"
					+ start_y);
			this.layout(stop_x - start_x, stop_y - start_y,
					stop_x + this.getWidth() - start_x,
					stop_y - start_y + this.getHeight());
			stop_x = (int) event.getRawX();
			stop_y = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("up");
			break;
		}
		return true;
	}
	

}
