package com.example.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.sound.adapter.MoodAdapter;

public class ChooseActivity extends Activity {
	private List<Map<String, Object>> list = null;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose);
		listView = (ListView) findViewById(R.id.choose);
		Intent intent = this.getIntent();
		String type = intent.getExtras().getString("type");
		String id = intent.getExtras().getString("id");
		list = new ArrayList<Map<String, Object>>();
		if (type.equals("1")) {
			for (int i = 0; i < 25; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", i);
				map.put("message", id + "-" + i);
				list.add(map);
			}
			MoodAdapter adapt = new MoodAdapter(this, list);
			listView.setAdapter(adapt);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent();
					intent.putExtra("id", (Integer) list.get(position)
							.get("id"));
					intent.putExtra("message",
							(String) list.get(position).get("message"));
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		} else if (type.equals("2")) {
			for (int i = 0; i < 25; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", i);
				map.put("message", id + "-" + i);
				list.add(map);
			}
			MoodAdapter adapt = new MoodAdapter(this, list);
			listView.setAdapter(adapt);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent();
					intent.putExtra("message",
							(String) list.get(position).get("message"));
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		}

	}

}
