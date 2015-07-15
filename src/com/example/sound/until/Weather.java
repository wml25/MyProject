package com.example.sound.until;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.sound.MainActivity;

public class Weather {
	private Context context;
	private LocationClient mLocationClient;
	public Weather(Context context){
		this.context = context;
		mLocationClient = new LocationClient(context.getApplicationContext()); // 声明LocationClient类
	}
	public void Location() {
		MyLocationListener myLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myLocationListener); // 注册监听函数
		setLocationOption();
		mLocationClient.start();// 开始网络定位
	}

	/**
	 * 设置相关参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度,默认值gcj02坐标系类型(gcj02,gps,bd09,bd09ll)
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		mLocationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			mLocationClient.stop();// 停止定位
			WeatherTask weatherTask = new WeatherTask(location.getLatitude(),
					location.getLongitude());
			weatherTask.execute();
		}
	}

	class WeatherTask extends AsyncTask<Void, Integer, String> {
		private Double latitude, longitude;

		WeatherTask(Double latitude, Double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
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
			URL url = null;
			try {
				url = new URL(
						"http://api.map.baidu.com/telematics/v3/weather?location="
								+ longitude + "," + latitude
								+ "&output=json&ak=XdtkRzTEdcG2tSHOb05jy9Qv");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (url != null) {
				try {
					HttpURLConnection urlConn = (HttpURLConnection) url
							.openConnection();
					urlConn.setConnectTimeout(10000);
					urlConn.setRequestMethod("GET");
					urlConn.connect();
					InputStream inputStream = urlConn.getInputStream();
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);
					BufferedReader reader = new BufferedReader(
							inputStreamReader);
					String inputLine = null;
					result = "";
					while (((inputLine = reader.readLine()) != null)) {
						result += inputLine;
					}
					reader.close();
					urlConn.disconnect();
				} catch (Exception e) {
				}
			}
			return result;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject resultJson = new JSONObject(result);
				JSONArray resultArray = new JSONArray(
						resultJson.getString("results"));
				JSONObject firstCityWeather = resultArray.getJSONObject(0);
				String city = firstCityWeather.getString("currentCity");
				JSONArray weatherData = new JSONArray(
						firstCityWeather.getString("weather_data"));
				JSONObject firstDataWeather = weatherData.getJSONObject(0);
				String weather = firstDataWeather.getString("weather");
				Toast.makeText(context, city + " : " + weather,
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
			}
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}
}
