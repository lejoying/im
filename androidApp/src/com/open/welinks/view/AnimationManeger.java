package com.open.welinks.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.gson.Gson;
import com.open.welinks.LoginActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.UserInformation;

public class AnimationManeger {
	String tag = "AnimationManeger";

	public static AnimationManeger animationManeger;

	public static AnimationManeger getInstance() {
		if (animationManeger == null) {
			animationManeger = new AnimationManeger();
		}
		return animationManeger;

	}

	public Context context;

	public void initialize(Context context) {
		this.context = context;
		int i = 1;
		i = i + 66;
	}

	private Animation animationNextOut;
	private Animation animationNextIn;
	private Animation animationBackOut;
	private Animation animationBackIn;

	public void loadAnimations() {
		animationNextOut = AnimationUtils.loadAnimation(context, R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(context, R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(context, R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(context, R.anim.animation_back_in);
	}
}
