package com.icloudoor.cloudoor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WuyeWidgeFragment3 extends Fragment {
	private final String mPageName = "WuyeWidgeFragment3";
	private String TAG = this.getClass().getSimpleName();

	private ImageView bgImage;

	private Thread mThread;

	private String portraitUrl;

	private static final int MSG_SUCCESS = 0;// get the image success
	private static final int MSG_FAILURE = 1;// fail

	private String link;

	//
	private String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Cloudoor/CachePic";
	private String imageName = "myCachePic3.jpg";

	boolean isDebug = DEBUG.isDebug;
	
	public WuyeWidgeFragment3() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout bigLayout;
		RelativeLayout contentLayout;
		TextView TVtitle;
		TextView TVcontent;
		TextView TVnamedate;

		Log.e(TAG, "create");

		View view = inflater.inflate(R.layout.fragment_wuye_widge_fragment3,
				container, false);
		bgImage = (ImageView) view.findViewById(R.id.image_bg);
		bigLayout = (RelativeLayout) view.findViewById(R.id.big_layout);
		contentLayout = (RelativeLayout) view.findViewById(R.id.content_layout);
		TVtitle = (TextView) view.findViewById(R.id.title);
		TVcontent = (TextView) view.findViewById(R.id.content);
		TVnamedate = (TextView) view.findViewById(R.id.name_date);

		SharedPreferences banner = getActivity().getSharedPreferences("BANNER",
				0);
		if ("1".equals(banner.getString("3type", "0"))) {
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentLayout
					.getLayoutParams();
			params.width = screenWidth - 48 * 2;

			contentLayout.setLayoutParams(params);

			TVtitle.setText(banner.getString("3title", null));
			TVnamedate.setText(banner.getString("3date", null));

			if (banner.getString("3content", null) != null) {
				String formatContent = ToDBC(banner.getString("3content", null)).replace("\t", "         ");
				TVcontent.setText(formatContent);
			}
			
			String color = banner.getString("3bg", null);
			bigLayout.setBackgroundColor(Color.parseColor(color));
			
		} else if ("2".equals(banner.getString("3type", "0"))) {
			SharedPreferences tempurl = getActivity().getSharedPreferences("TEMPURL3", 0);
			Editor editor = tempurl.edit();
			String temp = tempurl.getString("URL", "");
			if (temp != null) {
				if (temp.length() > 0) {
					if (temp.equals(banner.getString("3url", null))) {
						File f = new File(PATH + "/" + imageName);
						Log.e(TAG, "use local");

						ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getActivity());
						ImageLoader.getInstance().init(configuration);
				        
						DisplayImageOptions options = new DisplayImageOptions.Builder()
				        .resetViewBeforeLoading(false)  // default
				        .delayBeforeLoading(10)
				        .cacheInMemory(false) // default
				        .cacheOnDisk(false) // default
				        .considerExifParams(false) // default
				        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
				        .displayer(new SimpleBitmapDisplayer()) // default
				        .handler(new Handler()) // default
				        .displayer(new RoundedBitmapDisplayer(0))
				        .build();
						
						String imageUrl = Scheme.FILE.wrap(PATH + "/" + imageName);
						
						ImageLoader.getInstance().displayImage(imageUrl, bgImage, options);
//						Bitmap bm = BitmapFactory.decodeFile(PATH + "/" + imageName);
//						bgImage.setImageBitmap(bm);
					} else {
						File f = new File(PATH + "/" + imageName);
						if (f.exists())
							f.delete();
						portraitUrl = banner.getString("3url", null);

						Log.e(TAG, "use net - update local");
						
						Log.e(TAG, portraitUrl);

						if (mThread == null) {
							mThread = new Thread(runnable);
							mThread.start();
						}

						editor.putString("URL", portraitUrl);
						editor.commit();
					}
				} else {
					portraitUrl = banner.getString("3url", null);

					Log.e(TAG, "use net -- creat local");
					
					Log.e(TAG, portraitUrl);

					if (mThread == null) {
						mThread = new Thread(runnable);
						mThread.start();
					}

					editor.putString("URL", portraitUrl);
					editor.commit();
				}
			} else {
				portraitUrl = banner.getString("3url", null);

				Log.e(TAG, "use net -- creat local");
				
				Log.e(TAG, portraitUrl);

				if (mThread == null) {
					mThread = new Thread(runnable);
					mThread.start();
				}

				editor.putString("URL", portraitUrl);
				editor.commit();
			}

			String tempString = banner.getString("3link", null);
			if (tempString != null){
				if (tempString.length() > 0){
					link = banner.getString("3link", null);
					bgImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Uri uri = Uri.parse(link);
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(it);
						}

					});
				}
			}
		}

		return view;
	}
	
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				bgImage.setImageBitmap((Bitmap) msg.obj);
				break;
			case MSG_FAILURE:
				break;
			}
		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			Log.e(TAG, "loading");

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(portraitUrl);
			final Bitmap bitmap;
			try {
				org.apache.http.HttpResponse httpResponse = httpClient
						.execute(httpGet);

				bitmap = BitmapFactory.decodeStream(httpResponse.getEntity()
						.getContent());
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
				return;
			}

			File f = new File(PATH);
			if (!f.exists()) {
				f.mkdirs();
			}

			try {
				FileOutputStream out = new FileOutputStream(PATH + "/"
						+ imageName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(getActivity() != null)
					Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(getActivity() != null)
					Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}

			mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
		}
	};
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void onDetach() {
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		super.onDetach();

	}

}
