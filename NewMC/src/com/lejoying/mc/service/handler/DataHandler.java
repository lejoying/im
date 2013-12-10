package com.lejoying.mc.service.handler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.service.MainService;
import com.lejoying.mc.service.handler.MainServiceHandler.ServiceEvent;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCStaticData;

public class DataHandler {

	private LayoutInflater mInflater;
	private Context mContext;
	private ServiceEvent mServiceEvent;

	private Bitmap head;

	DataHandler(Context context, ServiceEvent serviceEvent) {
		this.mContext = context;
		this.mServiceEvent = serviceEvent;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.xiaohei), true, 5,
				Color.WHITE);
	}

	protected void process(Intent intent) {
		Intent broadcast = new Intent();
		broadcast.setAction(MainService.ACTION_NOTIFY);
		int notify = intent.getIntExtra("NOTIFY", -1);
		switch (notify) {
		case MainService.NOTIFY_MESSAGELIST:
			// List<Message> messages = MCDataTools.getMessages(mContext, 0);
			break;
		case MainService.NOTIFY_CHATMESSAGE:

			break;
		case MainService.NOTIFY_FRIEND:
			List<Circle> circles = MCDataTools.getCircles(mContext);
			List<View> circlesView = new ArrayList<View>();
			for (Circle circle : circles) {
				final View group = (RelativeLayout) mInflater.inflate(
						R.layout.f_group_panel, null);
				TextView tv_groupname = (TextView) group
						.findViewById(R.id.tv_groupname);
				tv_groupname.setText(circle.getName());

				final List<Friend> friends = circle.getFriends();
				final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
						: friends.size() / 6 + 1;
				final List<View> pageviews = new ArrayList<View>();

				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(int position, View convertView,
								final ViewGroup parent) {
							RelativeLayout rl_gridpage_item = (RelativeLayout) mInflater
									.inflate(
											R.layout.f_group_panelitem_gridpageitem_user,
											null);
							ImageView iv_head = (ImageView) rl_gridpage_item
									.findViewById(R.id.iv_head);
							TextView tv_nickname = (TextView) rl_gridpage_item
									.findViewById(R.id.tv_nickname);

							iv_head.setImageBitmap(head);
							tv_nickname.setText(friends.get(a * 6 + position)
									.getNickName());
							return rl_gridpage_item;
						}

						@Override
						public long getItemId(int position) {
							return position;
						}

						@Override
						public Object getItem(int position) {
							return friends.get(a * 6 + position);
						}

						@Override
						public int getCount() {
							int nowcount = 0;
							if (a < pagecount - 1) {
								nowcount = 6;
							} else {
								nowcount = friends.size() - a * 6;
							}
							return nowcount;
						}

						@Override
						public void unregisterDataSetObserver(
								DataSetObserver observer) {
							if (observer != null) {
								super.unregisterDataSetObserver(observer);
							}
						}

					};
					GridView gridpage = (GridView) mInflater.inflate(
							R.layout.f_group_panelitem_gridpage, null);
					gridpage.setAdapter(gridpageAdapter);
					pageviews.add(gridpage);
				}

				ViewPager vp_content = (ViewPager) group
						.findViewById(R.id.vp_content);
				PagerAdapter vp_contentAdapter = new PagerAdapter() {
					@Override
					public boolean isViewFromObject(View arg0, Object arg1) {
						return arg0 == arg1;
					}

					@Override
					public int getCount() {
						return pageviews.size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						((ViewPager) container).removeView(pageviews
								.get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						((ViewPager) container)
								.addView(pageviews.get(position));
						return pageviews.get(position);
					}

					@Override
					public void unregisterDataSetObserver(
							DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}
				};
				vp_content.setAdapter(vp_contentAdapter);
				circlesView.add(group);
				System.out.println(group.getWidth());
			}
			MCStaticData.circlesViewList = circlesView;
			mServiceEvent.sendBroadcast(broadcast);
			break;
		default:
			break;
		}
	}

	public interface NotifyListener {
		public void notifyChanged();
	}

}
