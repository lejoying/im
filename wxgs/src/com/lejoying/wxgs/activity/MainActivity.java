package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.adapter.MyExpandableListAdapter;
import com.lejoying.wxgs.adapter.MyViewPagerAdapter;
import com.lejoying.wxgs.adapter.SquareViewAdapter;

public class MainActivity extends Activity implements OnClickListener {
	private View squareView, messageView, neighbourView, myView;
	private ViewPager viewPager;// viewpager
	private List<View> viewList;// ����Ҫ������ҳ����ӵ����list��
	private ImageView cursor;// ����ͼƬ
	private TextView t1, t2, t3, t4;// ҳ��ͷ��
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���

	private TextView squareOnline;
	private ImageView squareImage;
	private Spinner squareSpinner;
	private ArrayAdapter squareAdapter;
	private ListView squareList;

	private ListView messageList;
	private String TYPE_SQUAREMSG="square";
	
	private Button neighbour_near, neighbour_group;
	private ExpandableListView neighbour_expandableListView;

	private TextView my_name, my_alter, my_sign, my_id;
	private ImageView my_set, my_imageview;
	private Button my_mymsg;
	private GridView my_gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		SysApplication.getInstance().addActivity(this);
		InitTextView();
		InitImageView();
		InitViewPager();

		squareView();
		messageView();
		neighbourView();
		myView();
	}

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.title_square);
		t2 = (TextView) findViewById(R.id.title_message);
		t3 = (TextView) findViewById(R.id.title_neibour);
		t4 = (TextView) findViewById(R.id.title_me);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	};

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewList = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();

		squareView = mInflater.inflate(R.layout.view_square, null);
		messageView = mInflater.inflate(R.layout.view_message, null);
		neighbourView = mInflater.inflate(R.layout.view_neighbours, null);
		myView = mInflater.inflate(R.layout.view_my, null);

		viewList.add(squareView);
		viewList.add(messageView);
		viewList.add(neighbourView);
		viewList.add(myView);

		viewPager.setAdapter(new MyViewPagerAdapter(viewList));
		viewPager.setCurrentItem(0);

		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 4 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��
	};

	private void squareView() {
		String[] squareName = { "广场1", "广场2", "广场3", "广场4", "广场5" };
		squareOnline = (TextView) squareView.findViewById(R.id.online);
		squareList = (ListView) squareView.findViewById(R.id.personList);
		squareImage = (ImageView) squareView.findViewById(R.id.send);
		squareSpinner = (Spinner) squareView.findViewById(R.id.squareSpinner);

		squareAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, squareName);
		squareAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		squareSpinner.setAdapter(squareAdapter);

		squareSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// SimpleAdapter adapter = new SimpleAdapter(this, getSquareData(),
		// R.layout.square_listview, new String[] { "picture", "name",
		// "speak", "time", "distance" }, new int[] {
		// R.id.person_pic, R.id.person_name, R.id.person_speak,
		// R.id.person_time, R.id.person_distance });
		SquareViewAdapter adapter = new SquareViewAdapter(this, getSquareData());
		squareList.setAdapter(adapter);
		// squareList.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long id) {
		// //TextView name=(TextView) findViewById(R.id.person_name);
		// // String name = (String)
		// // findViewById(R.id.person_name).toString();
		// HashMap<String,String>
		// map=(HashMap<String,String>)squareList.getItemAtPosition(arg2);
		// Intent intent = new
		// Intent(MainActivity.this,InformationActivity.class);
		// intent.putExtra("name", map.get("name"));
		//
		//
		// System.out.println(map.get("name"));
		// startActivity(intent);
		//
		// }
		// });

		squareImage.setOnClickListener(this);
		squareOnline.setOnClickListener(this);
	}

	private void messageView() {

		messageList = (ListView) messageView.findViewById(R.id.message_list);

		SimpleAdapter adapter = new SimpleAdapter(this, getMessageData(),
				R.layout.message_listview, new String[] { "picture", "name",
						"speak", "time" }, new int[] { R.id.message_person,
						R.id.message_name, R.id.message_speak,
						R.id.message_time });
		messageList.setAdapter(adapter);
		messageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						MsgInfoActivity.class);
				intent.putExtra("Type", TYPE_SQUAREMSG);
				startActivity(intent);
			}
		});
	}

	private void neighbourView() {

		// Ⱥ�����
		String[] group = new String[] { "我的好友", "我的同事", "我的同学" };
		// �������
		String[][] buddy = new String[][] { { "阿斯蒂芬", "反光镜", "伍尔特" },
				{ "科技感", "巨化股份", "宣传部", "面试地点" }, { "铅封号", "完成vis", "是翻个跟头", "约翰塞纳我" } };

		neighbour_near = (Button) neighbourView
				.findViewById(R.id.neighbour_near);
		neighbour_group = (Button) neighbourView
				.findViewById(R.id.neighbour_group);
		neighbour_expandableListView = (ExpandableListView) neighbourView
				.findViewById(R.id.neighbour_expandablelistview);

		ExpandableListAdapter adapter = new MyExpandableListAdapter(this,
				group, buddy);
		neighbour_expandableListView.setAdapter(adapter);
		neighbour_expandableListView.setGroupIndicator(null);

		// ����չ��
		neighbour_expandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
					}
				});
		// ����ر�
		neighbour_expandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
					}
				});
		neighbour_expandableListView
				.setOnChildClickListener(new OnChildClickListener() {
					public boolean onChildClick(ExpandableListView arg0,
							View arg1, int groupPosition, int childPosition,
							long arg4) {

						Intent intent = new Intent(MainActivity.this,
								ChatActivity.class);
						// intent.putExtra("name", name);
						startActivity(intent);

						return true;
					}
				});

		neighbour_near.setOnClickListener(this);
		neighbour_group.setOnClickListener(this);
	}

	private void myView() {
		// Ⱥ�����
		//String[] group = new String[] { "�ҵĹ㲥" };
		// �������
//		String[][] buddy = new String[][] { { "ABCDEFG", "HIJKLMN", "OPQRST",
//				"UVWXYZ" } };

		my_alter = (TextView) myView.findViewById(R.id.my_alter);
		my_set = (ImageView) myView.findViewById(R.id.my_set);
		my_mymsg=(Button) myView.findViewById(R.id.my_mymsg);
		my_gridview = (GridView) myView.findViewById(R.id.my_gridview);
		my_alter.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		

		SimpleAdapter my_adapter = new SimpleAdapter(this, getMyGroupData(),
				R.layout.my_group, new String[] { "picture", "name" },
				new int[] { R.id.my_group, R.id.my_group_name });
		my_gridview.setAdapter(my_adapter);
		my_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,
						GroupInfoActivity.class));
			}
		});
		my_mymsg.setOnClickListener(this);
		my_set.setOnClickListener(this);
		my_alter.setOnClickListener(this);
	}

	private List<Map<String, Object>> getSquareData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("picture", R.drawable.person);
		map.put("name", "小麦田");
		map.put("speak", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		map.put("time", "2:30");
		map.put("distance", "1.5km");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("picture", R.drawable.person);
		map.put("name", "撒地方");
		map.put("speak", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		map.put("time", "15:28");
		map.put("distance", "3.1km");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("picture", R.drawable.person);
		map.put("name", "桂丰大厦");
		map.put("speak", "ccccccccccccccccccccccccccccccccccccccccccccccc");
		map.put("time", "19:00");
		map.put("distance", "1.0km");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("picture", R.drawable.person);
		map.put("name", "Hilbert");
		map.put("speak", "ddddddddddddddddddddddddddddddddddddddddd");
		map.put("time", "20:35");
		map.put("distance", "45.1km");
		list.add(map);

		for (int i = 0; i < 50; i++) {
			map = new HashMap<String, Object>();
			map.put("picture", R.drawable.person);
			map.put("name", "SQUARE" + i);
			map.put("speak", "abcdefghijklmnopqrstuvwxyz");
			map.put("time", "00:00");
			map.put("distance", "00.00km");
			list.add(map);
		}

		return list;
	}

	private List<Map<String, Object>> getMessageData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < 50; i++) {
			map = new HashMap<String, Object>();
			map.put("picture", R.drawable.person);
			map.put("name", "MESSAGE" + i);
			map.put("speak", "MESSAGE......");
			map.put("time", "00:00");

			list.add(map);
		}

		return list;
	}

	private List<Map<String, Object>> getMyGroupData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (int i = 0; i < 20; i++) {
			map = new HashMap<String, Object>();
			map.put("picture", R.drawable.ic_launcher);
			map.put("name", i);
			list.add(map);
		}
		return list;
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {

			viewPager.setCurrentItem(index);

		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
		int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����
		int three = one * 3;

		@Override
		public void onPageSelected(int arg0) {

			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			cursor.startAnimation(animation);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.send:
			startActivity(new Intent(MainActivity.this, SendActivity.class));
			break;

		case R.id.online:
			startActivity(new Intent(MainActivity.this, OnlineActivity.class));
			break;

		case R.id.neighbour_near:
			startActivity(new Intent(MainActivity.this, NearActivity.class));
			break;
		case R.id.neighbour_group:
			startActivity(new Intent(MainActivity.this,
					NeighboursActivity.class));
			break;
		case R.id.my_set:
			startActivity(new Intent(MainActivity.this, SetActivity.class));
			break;
		case R.id.my_alter:
			startActivity(new Intent(MainActivity.this, MyActivity.class));
			break;
		case R.id.my_mymsg:
			startActivity(new Intent(MainActivity.this, MyMsgActivity.class));
			break;
		}
	}
}
