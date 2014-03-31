package com.lejoying.wxgs.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyViewPagerAdapter extends PagerAdapter{

	private List<View> mListViews;

	public MyViewPagerAdapter(List<View> mListViews) {  
        this.mListViews = mListViews;
        //构造方法，参数是我们的页卡，这样比较方便。  
    }  
	
	public void destroyItem(ViewGroup container, int position, Object object)   {     
        container.removeView(mListViews.get(position));//删除页卡  
    }  


    @Override  
    public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
         container.addView(mListViews.get(position), 0);//添加页卡  
         return mListViews.get(position);  
    }  

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

}
