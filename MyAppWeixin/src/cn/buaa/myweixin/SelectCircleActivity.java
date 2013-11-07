package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.adapter.MCResponseAdapter;
import cn.buaa.myweixin.api.RelationManager;
import cn.buaa.myweixin.apiimpl.RelationManagerImpl;
import cn.buaa.myweixin.apiutils.Circle;
import cn.buaa.myweixin.apiutils.MCTools;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SelectCircleActivity extends Activity {

	private RelationManager relationManager;
	private Spinner sp_selectcircle;

	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcircle);

		initView();
	}

	public void initView() {

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		sp_selectcircle = (Spinner) findViewById(R.id.sp_selectcircle);
		relationManager = new RelationManagerImpl(this);

		sp_selectcircle.setAdapter(new MCSpinnerAdapter(MCTools
				.getCircles(this)));

	}

	public void addFriendToCircle(View v) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("phone", MCTools.getLoginedAccount(this).getPhone());
		param.put("phoneto", getIntent().getExtras().getString("phone"));
		param.put("rid", String.valueOf(sp_selectcircle.getSelectedItemId()));
		param.put("accessKey", MCTools.getLoginedAccount(this).getAccessKey());

		relationManager.addfriend(param, new MCResponseAdapter(this) {

			@Override
			public void success(JSONObject data) {
				System.out.println(data);
			}

		});
	}

	public void back(View v) {
		finish();
	}

	private class MCSpinnerAdapter implements SpinnerAdapter {

		private List<Circle> circles = new ArrayList<Circle>();

		public MCSpinnerAdapter(List<Circle> circles) {
			super();
			this.circles = circles;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getCount() {
			return circles.size();
		}

		@Override
		public Object getItem(int position) {
			return circles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return circles.get(position).getRid();
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout rl = (RelativeLayout) inflater.inflate(
					R.layout.selectcircleitem, null);
			TextView tv = (TextView) rl.findViewById(R.id.tv_circle);

			tv.setText(circles.get(position).getName());

			return rl;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			RelativeLayout rl = (RelativeLayout) inflater.inflate(
					R.layout.selectcircleitem, null);
			TextView tv = (TextView) rl.findViewById(R.id.tv_circle);
			tv.setText(circles.get(position).getName());
			return rl;
		}

	}
}