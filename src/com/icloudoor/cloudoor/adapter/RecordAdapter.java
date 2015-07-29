package com.icloudoor.cloudoor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.icloudoor.cloudoor.R;
import com.icloudoor.cloudoor.chat.entity.Key;
import com.icloudoor.cloudoor.chat.entity.KeyInfo;

public class RecordAdapter extends BaseExpandableListAdapter {

	List<KeyInfo> data;
	Context context;

	public RecordAdapter(List<KeyInfo> data, Context context) {
		// TODO Auto-generated constructor stub
		this.data = data;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).getKeys().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).getKeys().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		KeyInfo info = data.get(groupPosition);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.record_groupview, null);
		TextView zone_name = (TextView) convertView
				.findViewById(R.id.zone_name);
		zone_name.setText(info.getAddress());
		convertView.setEnabled(false);
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.record_childview, null);
		TextView key_name = (TextView) convertView.findViewById(R.id.key_name);
		TextView state_tx = (TextView) convertView.findViewById(R.id.state_tx);
		Key keys = data.get(groupPosition).getKeys().get(childPosition);
		if (keys.getUseStatus().equals("2")) {
			state_tx.setText(R.string.key_type1);
		} else {
			state_tx.setText(R.string.key_type);
		}
		key_name.setText(keys.getName());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
