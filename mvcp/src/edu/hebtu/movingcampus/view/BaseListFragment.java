package edu.hebtu.movingcampus.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.NewsDetailsActivity;
import edu.hebtu.movingcampus.utils.ImageUtil;
import edu.hebtu.movingcampus.utils.ImageUtil.ImageCallback;
import edu.hebtu.movingcampus.utils.IntentUtil;
import edu.hebtu.movingcampus.widget.XListView;
import edu.hebtu.movingcampus.widget.XListView.IXListViewListener;

public abstract class BaseListFragment extends Fragment implements
		IXListViewListener {

	protected XListView listview;
	protected View view;
	LayoutInflater mInflater;
	protected boolean mIsScroll = false;
	ObjectMapper mMapper = new ObjectMapper();
	protected BaseAdapter mAdapter;

	public ExecutorService executorService = Executors.newFixedThreadPool(5);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		view = inflater.inflate(R.layout.newsxlist, null);
		listview = (XListView) view.findViewById(R.id.list_view);
		initListView();
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void initListView() {
	}

	public void startDetailActivity(Activity mContext, String id) {
		IntentUtil.start_activity(mContext, NewsDetailsActivity.class,
				new BasicNameValuePair("id", id));
	}

	protected void onStopLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime("刚刚");
	}

	ImageUtil.ImageCallback callback1 = new ImageCallback() {

		@Override
		public void loadImage(Bitmap bitmap, String imagePath) {
			try {
				ImageView img = (ImageView) listview.findViewWithTag(imagePath);
				img.setImageBitmap(bitmap);
			} catch (NullPointerException ex) {
				Log.e("error", "ImageView = null");
			}
		}
	};

}
