package com.example.sound.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sound.R;
import com.example.sound.until.ImageDownLoader;
import com.example.sound.until.ImageDownLoader.onImageLoaderListener;

public class FeelingsAdapter extends BaseAdapter implements OnScrollListener {
	private Context context = null;
	private List<Map<String, Object>> list;
	private GridView gridView;
	private ImageDownLoader mImageDownLoader;
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	private int mFirstVisibleItem;// 一屏中第一个item的位置
	private int mVisibleItemCount;// 一屏中所有item的个数
	private String url = "";

	public FeelingsAdapter(Context context,
			List<Map<String, Object>> feelingsList, GridView gridView) {
		super();
		this.context = context;
		this.list = feelingsList;
		this.gridView = gridView;
		mImageDownLoader = new ImageDownLoader(context);
		gridView.setOnScrollListener(this);
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			url = appInfo.metaData.getString("url");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}

	}

	/**
	 * GridView滚动的时候调用的方法，刚开始显示GridView也会调用此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
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
		ImageView img = null;
		TextView name = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.feelings_item, null);
			holder = new Holder();
			holder.img = (ImageView) convertView
					.findViewById(R.id.feelings_item);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		String url = this.url + "img/" + list.get(position).get("content");
		// 给ImageView设置Tag,这里已经是司空见惯了
		holder.img.setTag(url);
		/******************************* 去掉下面这几行试试是什么效果 ****************************/
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(url.replaceAll(
				"[^\\w]", ""));
		if (bitmap != null) {
			holder.img.setImageBitmap(bitmap);
		} else {
			holder.img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_launcher));
		}
		/**********************************************************************************/

		return convertView;
	}

	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount) {
		Bitmap bitmap = null;
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			String mImageUrl = url + list.get(i).get("url");
			final ImageView mImageView = (ImageView) gridView
					.findViewWithTag(mImageUrl);
			bitmap = mImageDownLoader.downloadImage(mImageUrl,
					new onImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (mImageView != null && bitmap != null) {
								mImageView.setImageBitmap(bitmap);
							}

						}
					});
			if (bitmap != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				mImageView.setImageDrawable(context.getResources().getDrawable(
						R.drawable.ic_launcher));
			}
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}
}
