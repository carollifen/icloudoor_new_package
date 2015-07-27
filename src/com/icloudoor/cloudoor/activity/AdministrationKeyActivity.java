package com.icloudoor.cloudoor.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.fragment.MyKeyFragment;

public class AdministrationKeyActivity extends FragmentActivity implements OnClickListener,OnPageChangeListener{

	ViewPager viewpager;
	List<Fragment>  viewList;
	TextView title_tx1;
	ImageView title_img1;
	TextView title_tx2;
	ImageView title_img2;
	TextView title_tx3;
	ImageView title_img3;
	LinearLayout mykey_layout;
	LinearLayout borrowkey_layout;
	LinearLayout authrecord_layout;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_admin_key);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewList = new ArrayList<Fragment>();
		viewList.add(new MyKeyFragment());
		viewList.add(new MyKeyFragment());
		viewList.add(new MyKeyFragment());
		
		title_tx1 = (TextView) findViewById(R.id.title_tx1);
		title_img1 = (ImageView) findViewById(R.id.title_img1);
		title_tx2 = (TextView) findViewById(R.id.title_tx2);
		title_img2 = (ImageView) findViewById(R.id.title_img2);
		title_tx3 = (TextView) findViewById(R.id.title_tx3);
		title_img3 = (ImageView) findViewById(R.id.title_img3);
		mykey_layout = (LinearLayout) findViewById(R.id.mykey_layout);
		borrowkey_layout = (LinearLayout) findViewById(R.id.borrowkey_layout);
		authrecord_layout = (LinearLayout) findViewById(R.id.authrecord_layout);
		mykey_layout.setOnClickListener(this);
		borrowkey_layout.setOnClickListener(this);
		authrecord_layout.setOnClickListener(this);
		
		viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), viewList));
		viewpager.setOnPageChangeListener(this);
		
	}
	
	public void initBottom(){
		
	}

	
	
	 class MyPagerAdapter extends FragmentPagerAdapter {  
		  
        private List<Fragment> fragmentList;  
        private List<String>   titleList;  
  
        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList){  
            super(fm);  
            this.fragmentList = fragmentList;  
            this.titleList = titleList;  
        }  
  
        /** 
         * 得到每个页面 
         */  
        @Override  
        public Fragment getItem(int arg0) {  
            return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);  
        }  
  
  
        /** 
         * 页面的总个数 
         */  
        @Override  
        public int getCount() {  
            return fragmentList == null ? 0 : fragmentList.size();  
        }  
    }



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mykey_layout:
			setBottom(0);
			break;
		case R.id.borrowkey_layout:
			setBottom(1);
			break;
		case R.id.authrecord_layout:
			setBottom(2);
			break;

		default:
			break;
		}
		
	}  
	
	public void setBottom(int position){
		switch (position) {
		case 0:
			title_tx1.setTextColor(Color.parseColor("#009bf8"));
			title_img1.setBackgroundResource(R.drawable.bottom_bg);
			title_tx2.setTextColor(Color.parseColor("#999999"));
			title_img2.setBackgroundResource(R.color.transparent);
			title_tx3.setTextColor(Color.parseColor("#999999"));
			title_img3.setBackgroundResource(R.color.transparent);
			break;
		case 1:
			title_tx2.setTextColor(Color.parseColor("#009bf8"));
			title_img2.setBackgroundResource(R.drawable.bottom_bg);
			title_tx1.setTextColor(Color.parseColor("#999999"));
			title_img1.setBackgroundResource(R.color.transparent);
			title_tx3.setTextColor(Color.parseColor("#999999"));
			title_img3.setBackgroundResource(R.color.transparent);
			break;
		case 2:
			title_tx3.setTextColor(Color.parseColor("#009bf8"));
			title_img3.setBackgroundResource(R.drawable.bottom_bg);
			title_tx2.setTextColor(Color.parseColor("#999999"));
			title_img2.setBackgroundResource(R.color.transparent);
			title_tx1.setTextColor(Color.parseColor("#999999"));
			title_img1.setBackgroundResource(R.color.transparent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setBottom(arg0);
	}
	
}
