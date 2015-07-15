package com.example.sound.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sound.R;

public class BehaviourAdapter extends BaseAdapter {

	private Context context = null;
	private List<Map<String, Object>> list ;

	public BehaviourAdapter(Context context,
			List<Map<String, Object>> list) {
		super();
		this.context = context;
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

	private static class Holder {
		TextView message = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.action_item, null);
			holder = new Holder();
			holder.message = (TextView) convertView.findViewById(R.id.action_item);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.message.setText((String)list.get(position).get("content"));
		return convertView;
	}
}
