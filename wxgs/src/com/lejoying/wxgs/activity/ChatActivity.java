package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.R.menu;
import com.lejoying.wxgs.adapter.ChatMsgViewAdapter;
import com.lejoying.wxgs.entity.ChatMsgEntity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity implements OnClickListener{

	private ImageView chat_back,chat_information,chat_face,chat_pic,chat_send;
	private TextView chat_state,chat_name;
	private EditText chat_messsage;
	private ListView chat_listview;
    //�������ݵ�������  
    private ChatMsgViewAdapter mAdapter;  
      
    //���������  
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		SysApplication.getInstance().addActivity(this);
		initView();
		initData();
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {  
        case R.id.chat_back:  
            finish(); 
            break;  
        case R.id.chat_send:  
        	send() ;
        	
            break;  
        }  
		
	}

	private void initView() {  
		chat_listview = (ListView) findViewById(R.id.chat_listview);  
        chat_back = (ImageView) findViewById(R.id.chat_back);  
        chat_back.setOnClickListener(this);  
        chat_send = (ImageView) findViewById(R.id.chat_send);  
        chat_send.setOnClickListener(this);  
        chat_messsage = (EditText) findViewById(R.id.chat_messsage);  
    }  
	
	private void initData() {  
        for(int i = 0; i < COUNT; i++) {  
            ChatMsgEntity entity = new ChatMsgEntity();  
            entity.setDate(dataArray[i]);  
            if (i % 2 == 0)  
            {  
                entity.setName("WHO ARE YOU");  
                entity.setMsgType(true);  
            }else{  
                entity.setName("СARRY");  
                entity.setMsgType(false);  
            }  
              
            entity.setText(msgArray[i]);  
            mDataArrays.add(entity);  
        }  
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);  
        chat_listview.setAdapter(mAdapter);  
    }  
	private void send()  
    {  
        String contString = chat_messsage.getText().toString();  
        if (contString.length() > 0)  
        {  
            ChatMsgEntity entity = new ChatMsgEntity();  
            entity.setDate(getDate());  
            entity.setName("");  
            entity.setMsgType(false);  
            entity.setText(contString);  
            mDataArrays.add(entity);  
            mAdapter.notifyDataSetChanged();  
            chat_messsage.setText("");  
            chat_listview.setSelection(chat_listview.getCount() - 1);  
        }  
    }  
	
	 private String getDate() {  
	        Calendar c = Calendar.getInstance();  
	        String year = String.valueOf(c.get(Calendar.YEAR));  
	        String month = String.valueOf(c.get(Calendar.MONTH));  
	        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);  
	        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));  
	        String mins = String.valueOf(c.get(Calendar.MINUTE));  
	        StringBuffer sbBuffer = new StringBuffer();  
	        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);   
	        return sbBuffer.toString();  
	    }  
	
	private String[] msgArray = new String[]{"AAAAAAAAAA",   
            "BBBBBBBBBBBBBB",   
            "CCCCCCCCCCCCCC",   
            "DDDDDDDDDDDDDDD",   
            "EEEEEEEEEEEEEEEEE",   
            "FFFFFFFFFFFFFFFFFFF",  
            "GGGGGGGGGGGGGGGGG",   
            "HHHHHHHHHHHHHHHHHHH"};  
  
    private String[]dataArray = new String[]{"2012-09-01 18:00", "2012-09-01 18:10",   
            "2012-09-01 18:11", "2012-09-01 18:20",   
            "2012-09-01 18:30", "2012-09-01 18:35",   
            "2012-09-01 18:40", "2012-09-01 18:50"};  
    private final static int COUNT = 8;  
	
}
