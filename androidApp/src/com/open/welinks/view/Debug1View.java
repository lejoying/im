package com.open.welinks.view;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.open.welinks.R;
import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.model.Data;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;

public class Debug1View {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public DisplayImageOptions options;

	public ListView listView;

	public ControlProgress titleControlProgress;
	public View controlProgressView;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	String 状态机;

	public Status status = Status.welcome;

	public Debug1View(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initView() {

		thisActivity.setContentView(R.layout.debug1_image_list);

		listView = (ListView) thisActivity.findViewById(R.id.view_element_debug1_list);

		controlProgressView = thisActivity.findViewById(R.id.title_control_progress_container);
		titleControlProgress = new ControlProgress();
		titleControlProgress.initialize(controlProgressView);
	}

	public class ControlProgress {

		public ImageView progress_line1;
		public View controlProgressView;
		public TranslateAnimation move_progress_line1;

		public int percentage = 0;
		public int width = 0;

		public void initialize(View container) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			move_progress_line1 = new TranslateAnimation(103, 0, 0, 0);

			progress_line1 = (ImageView) container.findViewById(R.id.progress_line1);
			controlProgressView = container;

			width = displayMetrics.widthPixels;

		}

		public void moveTo(int targetPercentage) {
			float position = targetPercentage / 100.0f * this.width;
			move_progress_line1 = new TranslateAnimation((percentage-targetPercentage) / 100.0f * width, 0, 0, 0);
			move_progress_line1.setStartOffset(0);
			move_progress_line1.setDuration(200);

			progress_line1.startAnimation(move_progress_line1);

			progress_line1.setX(position);
			percentage = targetPercentage;
		}
		
		public void setTo(int targetPercentage) {
			float position = targetPercentage / 100.0f * this.width;
			progress_line1.setX(position);
			percentage = targetPercentage;
		}
	}
}