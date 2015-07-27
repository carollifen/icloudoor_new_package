package com.icloudoor.cloudoor.fragment;

import com.icloudoor.cloudoor.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyKeyFragment extends Fragment{

	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mykey, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
