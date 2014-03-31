package com.lejoying.wxgs.adapter;

import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.MyAlert;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MsgInfoActivity;
import com.lejoying.wxgs.activity.MyMsgActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

public class MyMsgAdapter extends BaseAdapter implements View.OnClickListener {

	private List<Map<String, Object>> list;
	private Context context;
	private int mScreenWidth;
	private LayoutInflater layoutInflater;

	public MyMsgAdapter(Context context, int screenWidth,
			List<Map<String, Object>> list) {
		this.context=context;
		this.mScreenWidth = screenWidth;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;  
        //���û�����ù�,��ʼ��convertView  
        if (convertView == null)  
        {  
            //������õ�view  
            convertView = layoutInflater.inflate(R.layout.adapter_mymsg, null);  
  
            //��ʼ��holder  
            holder = new ViewHolder();  
            
            holder.action=convertView.findViewById(R.id.relativelayout2);
            holder.hSView=(HorizontalScrollView) convertView.findViewById(R.id.hsv); 
            holder.message_person=(ImageView) convertView.findViewById(R.id.message_person);
            holder.message_speak=(TextView) convertView.findViewById(R.id.message_speak);
            holder.message_name=(TextView) convertView.findViewById(R.id.message_name);
            holder.message_time=(TextView) convertView.findViewById(R.id.message_time);
            holder.message_del=(Button) convertView.findViewById(R.id.message_del);
            //��λ�÷ŵ�view��,�������¼��Ϳ���֪�����������һ��item  
            //holder.message_person.setTag(position);  
           // holder.message_speak.setTag(position);  
            //holder.message_name.setTag(position);  
            //holder.message_time.setTag(position);  
            holder.message_del.setTag(position);  
            holder.message_del.setOnClickListener(this);
  
            //��������view�Ĵ�СΪ��Ļ���,����ť����ñ�������Ļ��  
            holder.content = convertView.findViewById(R.id.relativelayout1);  
            LayoutParams lp = holder.content.getLayoutParams();  
            lp.width = mScreenWidth;  
  
            convertView.setTag(holder);  
        }  
        else//��ֱ�ӻ��ViewHolder  
        {  
            holder = (ViewHolder) convertView.getTag();  
        }  
  
        holder.message_speak.setText((String) list.get(position).get("speak"));
		holder.message_name.setText((String) list.get(position).get("name"));
		holder.message_time.setText((String) list.get(position).get("time"));
		holder.message_person.setImageResource((Integer) list.get(position).get(
				"picture"));
        
        //���ü����¼�  
        convertView.setOnTouchListener(new View.OnTouchListener(){  
            @Override  
            public boolean onTouch(View v, MotionEvent event)  
            {  
                switch (event.getAction())  
                {  
                    case MotionEvent.ACTION_UP:  
                          
                        //���ViewHolder  
                        ViewHolder viewHolder = (ViewHolder) v.getTag();  
                          
                        //���HorizontalScrollView������ˮƽ����ֵ.  
                        int scrollX = viewHolder.hSView.getScrollX();  
                          
                        //��ò�������ĳ���  
                        int actionW = viewHolder.message_del.getWidth();  
                          
                        //ע��ʹ��smoothScrollTo,����Ч�������Ƚ�Բ��,����Ӳ  
                        //���ˮƽ������ƶ�ֵ<��������ĳ��ȵ�һ��,�͸�ԭ  
                        if (scrollX < actionW / 2)  
                        {  
                            viewHolder.hSView.smoothScrollTo(0, 0);  
                        }  
                        else//����Ļ���ʾ��������  
                        {  
                            viewHolder.hSView.smoothScrollTo(actionW, 0);  
                        }  
                        return true;  
                }  
                return false;  
            }
        });
  
        //�����ֹɾ��һ��item��,ListView���ڲ���״̬,ֱ�ӻ�ԭ  
        if (holder.hSView.getScrollX() != 0)  
        {  
            holder.hSView.scrollTo(0, 0);  
        }  
          
        //���ñ�����ɫ,�����������.  
        //holder.content.setBackgroundResource(colors.get(position));  
       // holder.tvContent.setText("" + position);  
  
        //���ü����¼�  
        holder.message_del.setOnClickListener(this);  
        holder.message_speak.setOnClickListener(this);
		holder.message_name.setOnClickListener(this);
		holder.message_time.setOnClickListener(this);
		holder.message_person.setOnClickListener(this);
       
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//int position = (Integer) v.getTag(); 
		Intent intent;
        switch (v.getId())  
        {  
            case R.id.message_del:  
            	MyAlert.dialog(context, "提示ʾ", "确定要删除吗？");
                break; 
            case R.id.message_speak:  
            	intent = new Intent(context,
						MsgInfoActivity.class);
				intent.putExtra("Type", "personal");
				context.startActivity(intent);
                break;  
            case R.id.message_name:  
            	intent = new Intent(context,
						MsgInfoActivity.class);
				intent.putExtra("Type", "personal");
				context.startActivity(intent);
                break;  
            case R.id.message_person:  
            	intent = new Intent(context,
						MsgInfoActivity.class);
				intent.putExtra("Type", "personal");
				context.startActivity(intent);
                break;  
            case R.id.message_time:  
            	intent = new Intent(context,
						MsgInfoActivity.class);
				intent.putExtra("Type", "personal");
				context.startActivity(intent);
                break;  
        }
        notifyDataSetChanged();
	}

	class ViewHolder {
		public HorizontalScrollView hSView;  
		
		public View content,action;
		public ImageView message_person;
		public TextView message_speak, message_name, message_time;
		public Button message_del;
	}
	
}
