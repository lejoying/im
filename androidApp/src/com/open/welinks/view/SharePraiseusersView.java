package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.open.welinks.R;
import com.open.welinks.controller.SharePraiseusersController;
import com.open.welinks.model.Data;

public class SharePraiseusersView {

	public Data data = Data.getInstance();
	public String tag = "SharePraiseusersView";

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	public ImageView backView;
	public ListView listView;

	public SharePraiseusersView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		thisView = this;
	}

	public void initView() {
		thisActivity.setContentView(R.layout.share_message_praiseusers);

		backView = (ImageView) thisActivity.findViewById(R.id.backview);
		listView = (ListView) thisActivity.findViewById(R.id.praiseusersContent);
	}

	public class PraiseUsersAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return thisController.praiseusersList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
