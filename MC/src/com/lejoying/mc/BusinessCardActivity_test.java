package com.lejoying.mc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.lejoying.adapter.DragListAdapter;

public class BusinessCardActivity_test extends Activity {

	private List<String> list = null; // 存放联系人数据的list
	private List<String> listtag = null; // 存放字母的数据的list
	private DragListAdapter adapter = null; // 自定义的Adapter对象
	private ListView listView = null; // 主layout中用到的listview

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscard);
		setData(); // 初始化联系人和首字母的数据
		adapter = new DragListAdapter(this, list, listtag); // [重要],将每一个item重写排版和编辑得到信息view之后放到adapter里面
		// 将我们自定义的adapter放到listview里面
		listView = (ListView) findViewById(R.id.lv_test);
		listView.setAdapter(adapter);
	}

	public void setData() {
		list = new ArrayList<String>();
		listtag = new ArrayList<String>();
		list.add("A");
		listtag.add("A");
		for (int i = 0; i < 4; i++) {
			list.add("阿波次的" + i);
		}
		list.add("B");
		listtag.add("B");
		for (int i = 0; i < 4; i++) {
			list.add("波士顿" + i);
		}
		list.add("C");
		listtag.add("C");
		for (int i = 0; i < 4; i++) {
			list.add("车辙" + i);
		}
	}
}
