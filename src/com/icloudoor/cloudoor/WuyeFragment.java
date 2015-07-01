package com.icloudoor.cloudoor;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;

public class WuyeFragment extends Fragment {
	private final String mPageName = "WuyeFragment";
	private String TAG = this.getClass().getSimpleName();

	private ImageView WuyeWidgePush1;
	private View blank1;
	private ImageView WuyeWidgePush2;
	private View blank2;
	private ImageView WuyeWidgePush3;

	private ImageView BtnLianxiwuye;
	private ImageView BtnNotice;
	private ImageView BtnFix;
	private ImageView BtnBad;
	private ImageView BtnGood;
	private ImageView BtnQuery;
	private ImageView BtnBill;
	private ImageView BtnPay;

	private RelativeLayout unreadNoticeLayout;
	private RelativeLayout unreadQueryLayout;
	private ImageView unreadNoticeDot;
	private ImageView unreadQueryDot;
	private TextView unreadNoticeCount;
	private TextView unreadQueryCount;
	private int unreadNotice, unreadQuery;

	public MyClickListener myClick;

	private AutoScrollViewPager viewPager;
	public FragmentManager mFragmentManager;
	public MyPageChangeListener myPageChangeListener;

	private URL unReadURL;
	private RequestQueue mQueue;
	private String HOST = UrlUtils.HOST;
	private String sid;

	// for test
//	private URL bannerURL;

	boolean isDebug = DEBUG.isDebug;
	
	int bannerCount = 2;
		
	public WuyeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView BtnLianxiwuye;
		ImageView BtnNotice;
		ImageView BtnFix;
		ImageView BtnBad;
		ImageView BtnGood;
		ImageView BtnQuery;
		ImageView BtnBill;
		ImageView BtnPay;

		View view = inflater.inflate(R.layout.wuye_page, container, false);

		if(getActivity() != null){
			SharedPreferences banner = getActivity().getSharedPreferences("BANNER", 0);
			bannerCount = banner.getInt("COUNT", 2);
		}
		
		
		mQueue = Volley.newRequestQueue(getActivity());
		sid = loadSid();

		WuyeWidgePush1 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push1);
		blank1 = (View) view.findViewById(R.id.blankview1);
		WuyeWidgePush2 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push2);		
		blank2 = (View) view.findViewById(R.id.blankview2);
		WuyeWidgePush3 = (ImageView) view.findViewById(R.id.Iv_wuye_widge_push3);

		if(bannerCount == 2){
			blank2.setVisibility(View.GONE);
			WuyeWidgePush3.setVisibility(View.GONE);
		}
		
		BtnLianxiwuye = (ImageView) view.findViewById(R.id.btn_lianxiwuye);
		BtnNotice = (ImageView) view.findViewById(R.id.btn_notice);
		BtnFix = (ImageView) view.findViewById(R.id.btn_fix);
		BtnBad = (ImageView) view.findViewById(R.id.btn_bad);
		BtnGood = (ImageView) view.findViewById(R.id.btn_good);
		BtnQuery = (ImageView) view.findViewById(R.id.btn_query);
		BtnBill = (ImageView) view.findViewById(R.id.btn_bill);
		BtnPay = (ImageView) view.findViewById(R.id.btn_pay);

		unreadNoticeLayout = (RelativeLayout) view.findViewById(R.id.unread_notice_layout);
		unreadQueryLayout = (RelativeLayout) view.findViewById(R.id.unread_query_layout);
		unreadNoticeCount = (TextView) view.findViewById(R.id.unread_notice);
		unreadQueryCount = (TextView) view.findViewById(R.id.unread_query);
		unreadNoticeDot = (ImageView) view.findViewById(R.id.red_dot_notice);
		unreadQueryDot = (ImageView) view.findViewById(R.id.red_dot_query);

		unreadNoticeLayout.setVisibility(View.INVISIBLE);
		unreadQueryLayout.setVisibility(View.INVISIBLE);
		unreadNoticeCount.setText("");
		unreadQueryCount.setText("");

		myClick = new MyClickListener();

		BtnLianxiwuye.setOnClickListener(myClick);
		BtnNotice.setOnClickListener(myClick);
		BtnFix.setOnClickListener(myClick);
		BtnBad.setOnClickListener(myClick);
		BtnGood.setOnClickListener(myClick);
		BtnQuery.setOnClickListener(myClick);
		BtnBill.setOnClickListener(myClick);
		BtnPay.setOnClickListener(myClick);

		mFragmentManager = getChildFragmentManager();
		viewPager = (AutoScrollViewPager) view
				.findViewById(R.id.wuye_widge_pager);
		myPageChangeListener = new MyPageChangeListener();

		InitFragmentViews();
//		InitViewPager();

		return view;
	}

	public void InitFragmentViews() {
		ArrayList<Fragment> mWuyePageFragmentList;
		WuyePageAdapter mWuyePageAdapter;
		WuyeWidgeFragment mWuyeWidgeFragment;
		WuyeWidgeFragment2 mWuyeWidgeFragment2;
		WuyeWidgeFragment3 mWuyeWidgeFragment3;
		
		mWuyePageFragmentList = new ArrayList<Fragment>();
		
		if(bannerCount == 2){
			WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
			WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
			
			mWuyeWidgeFragment = new WuyeWidgeFragment();
			mWuyeWidgeFragment2 = new WuyeWidgeFragment2();
			
			mWuyePageFragmentList.add(mWuyeWidgeFragment);
			mWuyePageFragmentList.add(mWuyeWidgeFragment2);
		}else if(bannerCount == 3){
			WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
			WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
			WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);

			mWuyeWidgeFragment = new WuyeWidgeFragment();
			mWuyeWidgeFragment2 = new WuyeWidgeFragment2();
			mWuyeWidgeFragment3 = new WuyeWidgeFragment3();

			mWuyePageFragmentList.add(mWuyeWidgeFragment);
			mWuyePageFragmentList.add(mWuyeWidgeFragment2);
			mWuyePageFragmentList.add(mWuyeWidgeFragment3);
		}

		mWuyePageAdapter = new WuyePageAdapter(mFragmentManager, mWuyePageFragmentList);
		viewPager.setAdapter(mWuyePageAdapter);
		viewPager.setOnPageChangeListener(myPageChangeListener);
		viewPager.setInterval(4000);
		viewPager.startAutoScroll();
		viewPager.setCurrentItem(0);
	}

//	public void InitViewPager() {
//		
//		mWuyePageFragmentList = new ArrayList<Fragment>();
//
//		mWuyeWidgeFragment = new WuyeWidgeFragment();
//		mWuyeWidgeFragment2 = new WuyeWidgeFragment2();
//		mWuyeWidgeFragment3 = new WuyeWidgeFragment3();
//
//		mWuyePageFragmentList.add(mWuyeWidgeFragment);
//		mWuyePageFragmentList.add(mWuyeWidgeFragment2);
//		mWuyePageFragmentList.add(mWuyeWidgeFragment3);
//
//		mWuyePageAdapter = new WuyePageAdapter(mFragmentManager,
//				mWuyePageFragmentList);
//		viewPager.setAdapter(mWuyePageAdapter);
//		viewPager.setOnPageChangeListener(myPageChangeListener);
//		viewPager.setInterval(4000);
//		viewPager.startAutoScroll();
//		viewPager.setCurrentItem(0);
//	}
	
	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			Log.e(TAG, String.valueOf(position));
		
			if(bannerCount == 2){
				if (position == 0) {
					WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
					WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
				} else if (position == 1) {
					WuyeWidgePush2.setImageResource(R.drawable.wuye_push_current);
					WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
				}
			} else if(bannerCount == 3) {
				if (position == 0) {
					WuyeWidgePush1.setImageResource(R.drawable.wuye_push_current);
					WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
					WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);
				} else if (position == 1) {
					WuyeWidgePush2.setImageResource(R.drawable.wuye_push_current);
					WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
					WuyeWidgePush3.setImageResource(R.drawable.wuye_push_next);
				} else if (position == 2) {
					WuyeWidgePush3.setImageResource(R.drawable.wuye_push_current);
					WuyeWidgePush1.setImageResource(R.drawable.wuye_push_next);
					WuyeWidgePush2.setImageResource(R.drawable.wuye_push_next);
				}
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// stop auto scroll when onPause
		viewPager.stopAutoScroll();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		Log.e(TAG, "test");
		
		// start auto scroll when onResume
		viewPager.startAutoScroll();

		try {
			unReadURL = new URL(HOST + "/user/prop/zone/getGridCount.do" + "?sid=" + sid);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MyJsonObjectRequest mJsonRequest = new MyJsonObjectRequest(Method.GET,
				unReadURL.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, response.toString());

						try {
							JSONArray data = response.getJSONArray("data");

							if (data.getInt(0) != 0) {
								unreadNotice = data.getInt(0);
								unreadNoticeLayout.setVisibility(View.VISIBLE);
								unreadNoticeCount.setText(String.valueOf(unreadNotice));
								if(unreadNotice > 9){
									unreadNoticeDot.setBackgroundResource(R.drawable.wuye_red_dot2);
								}else if(unreadNotice < 10){
									unreadNoticeDot.setBackgroundResource(R.drawable.wuye_red_dot1);
								}
							}

							if (data.getInt(1) != 0) {
								unreadQuery = data.getInt(1);
								unreadQueryLayout.setVisibility(View.VISIBLE);
								unreadQueryCount.setText(String.valueOf(unreadQuery));
								if(unreadQuery > 9){
									unreadQueryDot.setBackgroundResource(R.drawable.wuye_red_dot2);
								}else if(unreadQuery < 10){
									unreadQueryDot.setBackgroundResource(R.drawable.wuye_red_dot1);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.toString());
						if(getActivity() != null)
							Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(mJsonRequest);
	}

	public class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_lianxiwuye:
				intent.setClass(getActivity(), ContactWuyeActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_notice:

				unreadNoticeLayout.setVisibility(View.INVISIBLE);
				unreadNoticeCount.setText("");
				unreadNotice = 0;

				intent.setClass(getActivity(), NoticeActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_fix:
				intent.setClass(getActivity(), ReportToRepairActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_bad:
				intent.setClass(getActivity(), ComplainActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_good:
				intent.setClass(getActivity(), CommendActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_query:

				unreadQueryLayout.setVisibility(View.INVISIBLE);
				unreadQueryCount.setText("");
				unreadQuery = 0;

				intent.setClass(getActivity(), QueryActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_bill:
				intent.setClass(getActivity(), BillActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_pay:
				intent.setClass(getActivity(), PayActivity.class);
				startActivity(intent);
				break;
			}
		}

	}

	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void saveSid(String sid) {
		SharedPreferences savedSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		Editor editor = savedSid.edit();
		editor.putString("SID", sid);
		editor.commit();
	}

	public String loadSid() {
		SharedPreferences loadSid = getActivity().getSharedPreferences(
				"SAVEDSID", 0);
		return loadSid.getString("SID", null);
	}

}
