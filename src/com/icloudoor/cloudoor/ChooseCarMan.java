package com.icloudoor.cloudoor;

import com.icloudoor.cloudoor.SettingDetailActivity.MyBtnOnClickListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ChooseCarMan extends BaseActivity {

	private LinearLayout back;
	private LinearLayout save;

	private RelativeLayout ChooseCar;
	private RelativeLayout ChooseMan;

	private ImageView ChooseCarImage;
	private ImageView ChooseManImage;

	private int chooseCar;

	private MySaveOnClickListener mySaveOnClickListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swich_car_man);

		back = (LinearLayout) findViewById(R.id.btn_back_from_swich);
		save = (LinearLayout) findViewById(R.id.save_swich_car_man);
		ChooseCar = (RelativeLayout) findViewById(R.id.choose_car);
		ChooseMan = (RelativeLayout) findViewById(R.id.choose_man);
		ChooseCarImage = (ImageView) findViewById(R.id.choose_car_image);
		ChooseManImage = (ImageView) findViewById(R.id.choose_man_image);

		SharedPreferences choosemancar = getSharedPreferences("SETTING", MODE_PRIVATE);
		chooseCar = choosemancar.getInt("chooseCar", 0);
		
		if(chooseCar == 1) {
			ChooseCarImage.setImageResource(R.drawable.confirm);
		} else if(chooseCar == 0) {
			ChooseManImage.setImageResource(R.drawable.confirm);
		}
		
		mySaveOnClickListener = new MySaveOnClickListener()	;

		back.setOnClickListener(mySaveOnClickListener);
		ChooseCar.setOnClickListener(mySaveOnClickListener);
		ChooseMan.setOnClickListener(mySaveOnClickListener);
		save.setOnClickListener(mySaveOnClickListener);
	}
	public class MySaveOnClickListener implements OnClickListener{

		SharedPreferences choosemancar = getSharedPreferences("SETTING", MODE_PRIVATE);
		Editor editor = choosemancar.edit();

		@Override
		public void onClick(View v) {
			switch (v.getId()){

			case R.id.choose_car:

				ChooseCarImage.setImageResource(R.drawable.confirm);
				ChooseManImage.setImageDrawable(null);
				chooseCar = 1;
				break;

			case R.id.choose_man:

				ChooseManImage.setImageResource(R.drawable.confirm);
				ChooseCarImage.setImageDrawable(null);
				chooseCar = 0;
				break;

			case R.id.save_swich_car_man:
				
				editor.putInt("chooseCar", chooseCar);
				editor.commit();
				finish();
				break;

			case R.id.btn_back_from_swich:

				finish();
				break;
			}

		}

	}
}
