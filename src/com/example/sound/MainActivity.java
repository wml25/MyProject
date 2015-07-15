package com.example.sound;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sound.adapter.BehaviourAdapter;
import com.example.sound.adapter.FeelingsAdapter;
import com.example.sound.adapter.MoodAdapter;
import com.example.sound.until.ImageDownLoader;
import com.example.sound.until.Weather;

public class MainActivity extends Activity {
	private long mExitTime = 0;
	private GridView gridView;
	private TextView moodId, mood, action;
	private ImageView feeling;
	private FeelingsAdapter adapter = null;
	private List<Map<String, Object>> feelingsList = null;
	private List<Map<String, Object>> moodList = null;
	private List<Map<String, Object>> actionList = null;
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Weather weather = new Weather(this);
		weather.Location();
		try {
			ApplicationInfo appInfo = this.getPackageManager()
					.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA);
			url = appInfo.metaData.getString("url");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gridView = (GridView) findViewById(R.id.feelings);
		feeling = (ImageView) findViewById(R.id.feeling);
		moodId = (TextView) findViewById(R.id.moodId);
		mood = (TextView) findViewById(R.id.mood);
		action = (TextView) findViewById(R.id.action);
		feelingsList = new ArrayList<Map<String, Object>>();
		MyTask feelingTask = new MyTask(this, "1", null, "");
		feelingTask.execute();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(MainActivity.this,
				// ChooseActivity.class);
				// intent.putExtra("type", "1");
				// intent.putExtra("id", (String)
				// list.get(position).get("name"));
				// startActivityForResult(intent, 1);
				ImageDownLoader mImageDownLoader = new ImageDownLoader(
						MainActivity.this);
				Bitmap bitmap = mImageDownLoader
						.showCacheBitmap((url + feelingsList.get(position)
								.get("url").toString())
								.replaceAll("[^\\w]", ""));
				feeling.setImageBitmap(bitmap);
				final AlertDialog al = new AlertDialog.Builder(
						MainActivity.this).create();
				al.show();
				Window window = al.getWindow();
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				View v = factory.inflate(R.layout.choose, null);
				ListView listView = (ListView) v.findViewById(R.id.choose);
				moodList = new ArrayList<Map<String, Object>>();
				MyTask moodTask = new MyTask(MainActivity.this, "2", listView,
						feelingsList.get(position).get("id") + "");
				moodTask.execute();
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						moodId.setText(""
								+ (Integer) moodList.get(position).get("id"));
						mood.setText("心情"
								+ (String) moodList.get(position)
										.get("content"));
						mood.setVisibility(View.VISIBLE);
						action.setText("");
						action.setVisibility(View.GONE);
						al.dismiss();
					}
				});
				window.setContentView(v);
			}
		});
		mood.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(MainActivity.this,
				// ChooseActivity.class);
				// intent.putExtra("type", "2");
				// intent.putExtra("id", mood.getText().toString());
				// startActivityForResult(intent, 2);
				final AlertDialog al = new AlertDialog.Builder(
						MainActivity.this).create();
				al.show();
				Window window = al.getWindow();
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				View view = factory.inflate(R.layout.choose, null);
				ListView listView = (ListView) view.findViewById(R.id.choose);
				actionList = new ArrayList<Map<String, Object>>();
				MyTask actionTask = new MyTask(MainActivity.this, "3",
						listView, moodId.getText().toString());
				actionTask.execute();
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						action.setText((String) actionList.get(position).get(
								"content"));
						action.setVisibility(View.VISIBLE);
						al.dismiss();
					}
				});
				window.setContentView(view);
			}
		});

		Button test = (Button) findViewById(R.id.test);
		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						httpPost();
					}
				}).start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if (requestCode == 1 && resultCode == RESULT_OK) {
	// String message = data.getStringExtra("message");
	// int id = data.getIntExtra("id", 0);
	// moodId.setText(id + "");
	// mood.setText(message);
	// } else if (requestCode == 2 && resultCode == RESULT_OK) {
	// String message = data.getStringExtra("message");
	// action.setText(message);
	// }
	// }

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Object mHelperUtils;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void httpPost() {
		String httpUri = "http://193.100.100.123:8080/SoundServer/registered";
		String result = "";
		URL url = null;
		try {
			url = new URL(httpUri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			try {
				// 使用HttpURLConnection打开连接
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setConnectTimeout(10000);// 10s内连不上就断开
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setRequestMethod("POST");
				// Post 请求不能使用缓存
				urlConn.setUseCaches(false);
				urlConn.setInstanceFollowRedirects(true);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// 上传文件:"multipart/form-data"
				// 纯文本传输:"text/plain"
				urlConn.connect();
				OutputStream outputStream = urlConn.getOutputStream();// 向服务器写入
				DataOutputStream out = new DataOutputStream(outputStream);
				out.write(new String("name=6&password=6&sex=男").getBytes("UTF-8"));// 要是用objectOutputStream就是out.writeObject(content);//写入服务器的参数，但是放到内存中了
				// 中文处理URLEncoder.encode("吴", "UTF-8")
				// 刷新、关闭
				out.flush();// 真正的写过去了
				out.close();
				// 获服务器取数据
				InputStream inputStream = urlConn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader);// 读字符串用的。
				String inputLine = null;
				// 使用循环来读取获得的数据，把数据都村到result中了
				while (((inputLine = reader.readLine()) != null)) {
					// 我们在每一行后面加上一个"\n"来换行
					result += inputLine + "\n";
				}
				reader.close();// 关闭输入流
				// 关闭http连接
				urlConn.disconnect();
				// 设置显示取得的内容
				if (result != null) {
					System.out.println(result);
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
			}
		}
	}

	private String httpPost(String action, String content) {
		String result = null;
		URL url = null;
		try {
			url = new URL(this.url + action);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			try {
				// 使用HttpURLConnection打开连接
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setConnectTimeout(10000);// 10s内连不上就断开
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setRequestMethod("POST");
				// Post 请求不能使用缓存
				urlConn.setUseCaches(false);
				urlConn.setInstanceFollowRedirects(true);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// 上传文件:"multipart/form-data"
				// 纯文本传输:"text/plain"
				urlConn.connect();
				OutputStream outputStream = urlConn.getOutputStream();// 向服务器写入
				DataOutputStream out = new DataOutputStream(outputStream);
				out.writeBytes(new String(content));// 要是用objectOutputStream就是out.writeObject(content);//写入服务器的参数，但是放到内存中了
				// 中文处理URLEncoder.encode("吴", "UTF-8")
				// 刷新、关闭
				out.flush();// 真正的写过去了
				out.close();
				// 获服务器取数据
				InputStream inputStream = urlConn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader);// 读字符串用的。
				String inputLine = null;
				// 使用循环来读取获得的数据，把数据都村到result中了
				result = "";
				while (((inputLine = reader.readLine()) != null)) {
					// 我们在每一行后面加上一个"\n"来换行
					result += inputLine + "\n";
				}
				reader.close();// 关闭输入流
				// 关闭http连接
				urlConn.disconnect();
				// 设置显示取得的内容
				if (result != null) {
					System.out.println(result);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return result;
	}

	class MyTask extends AsyncTask<Void, Integer, String> {
		private Context context;
		private String type;
		private ListView listView;
		private String id;

		MyTask(Context context, String type, ListView listView, String id) {
			this.context = context;
			this.type = type;
			this.listView = listView;
			this.id = id;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			if (this.type.equals("1")) {
				result = httpPost("feelings", "type=1");
			} else if (this.type.equals("2")) {
				result = httpPost("mood", "type=" + id);
			} else if (this.type.equals("3")) {
				result = httpPost("behaviour", "type=" + id);
			}
			return result;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(String result) {
			if (this.type.equals("1")) {
				feelingsList = getList(result);
				adapter = new FeelingsAdapter(MainActivity.this, feelingsList,
						gridView);
				gridView.setAdapter(adapter);
			} else if (this.type.equals("2")) {
				moodList = getList(result);
				MoodAdapter adapt = new MoodAdapter(MainActivity.this, moodList);
				listView.setAdapter(adapt);
			} else if (this.type.equals("3")) {
				actionList = getList(result);
				BehaviourAdapter adapt = new BehaviourAdapter(
						MainActivity.this, actionList);
				listView.setAdapter(adapt);
			}
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	/**
	 * 把json 转换为ArrayList 形式
	 */
	private List<Map<String, Object>> getList(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 将json 数组转换为Map 对象
	 */
	private Map<String, Object> getMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
