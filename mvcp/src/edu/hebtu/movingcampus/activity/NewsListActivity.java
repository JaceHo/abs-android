package edu.hebtu.movingcampus.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.adapter.NewsListAdapter;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.db.DBHelper;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.utils.IntentUtil;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.widget.XListView;

public class NewsListActivity extends BaseActivity implements OnClickListener,
		XListView.IXListViewListener {
	private final String LIST_TEXT = "text";
	private final String LIST_IMAGEVIEW = "img";

	// [start]变量
	/**
	 * 数字代表列表顺序
	 */
	private int mTag = 0;
	private XListView listview;

	// title标题
	private ImageView imgQuery;
	private ImageView imgMore;
	private View loading;
	private View loadfailed;

	// views

	// init dao
	private NewsDao newsDao;
	private NewsListAdapter adapter;

	private String typeID;

	// load responseData
	private ArrayList<NewsShort> newsResponseData;

	// [end]

	// [start]生命周期
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.news_list);

		imgQuery = (ImageView) findViewById(R.id.imageview_above_query);
		imgMore = (ImageView) findViewById(R.id.imageview_above_more);
		loadfailed = findViewById(R.id.view_load_fail);
		loading = findViewById(R.id.view_loading);
		typeID = getIntent().getStringExtra("id");

		initClass();
		initControl();
		bindButton();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			DBHelper db = DBHelper.getInstance(this);
			db.closeDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// [end]

	private void initControl() {

		imgQuery.setOnClickListener(this);
		imgQuery.setVisibility(View.VISIBLE);
		imgMore.setOnClickListener(this);
		loading.setVisibility(View.VISIBLE);
		loadfailed.setVisibility(View.GONE);

		//listview = (XListView) findViewById(R.id.xlistview_news);
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(false);
		//adapter = new NewsListAdapter(this, R.layout.news_item_layout,listview); 
		listview.setAdapter(adapter);

		mRunningTask = new MyTask(true).execute(newsDao);
	}

	private void initClass() {
		newsDao = new NewsDao(this);
	}

	// [start]继承方法
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.imageview_above_query:

					if (NetWorkHelper.isNetworkAvailable(NewsListActivity.this)) {
						IntentUtil.start_activity(this, SearchActivity.class,
								new BasicNameValuePair("tag", "0"));
					} else {
						Toast.makeText(getApplicationContext(), "网络连接失败,请检查网络",
								Toast.LENGTH_LONG).show();
					}
					break;
				}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						NewsListActivity.this.finish();
					}
				});
	}

	public class MyTask extends AsyncTask<BaseDao, String, Object> {

		private boolean mUseCache;
		private boolean clear;
		private boolean loaded;

		public MyTask() {
			mUseCache = true;
		}

		public MyTask(boolean useCache) {
			mUseCache = useCache;
		}

		public MyTask(boolean useCache, boolean clear) {
			mUseCache = useCache;
			this.clear = clear;
		}

		@Override
		protected void onPreExecute() {
			loaded=false;
			if (clear)
				adapter.clear();
			if (typeID.equals("0"))
				IPreference.getInstance(NewsListActivity.this)
						.getListOfNewsSubjectByID(Integer.parseInt(typeID)).clear();
			loading.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(BaseDao... params) {
			// 本地信息
			if (typeID.equals("0"))
				newsResponseData = (ArrayList<NewsShort>) IPreference
						.getInstance(NewsListActivity.this)
						.getListOfNewsSubjectByID(Integer.parseInt(typeID)).dump(NewsListActivity.this);
			else
				newsResponseData = newsDao.mapperJson(true, typeID,
						(adapter.getCount() + 1) + "", null);
			return newsResponseData;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			listview.setRefreshTime(new SimpleDateFormat("MM/dd HH:mm:ss")
					.format(new Date()));
			if (result != null) {
				if (clear) {
					listview.stopRefresh();
					listview.stopLoadMore();
				}
				adapter.appendToList((List<NewsShort>) result);
				loading.setVisibility(View.GONE);
				loadfailed.setVisibility(View.GONE);
			} else {
				loading.setVisibility(View.GONE);
				loadfailed.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onRefresh() {
		mRunningTask = new MyTask(true, true).execute(newsDao);
	}

	@Override
	public void onLoadMore() {
		//上一次获取不为空显示加载
		if(newsResponseData!=null&&newsResponseData.size()>0)
		mRunningTask = new MyTask(true).execute(newsDao);
		else listview.stopLoadMore();
	}
//    @Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (mUpdateView != null && data != null) {
//			int result = data.getIntExtra(NewsDetailsActivity.ACTION_RESULT, 0);
//			if (result > 0) {
//				mNewsItem.readStatus = 1;
//				((TextView) mUpdateView.getTag(R.id.new_item_title_view)).setTextColor(0xff9C9C9C);
//				mUpdateView = null;
//				mNewsItem = null;
//			}
//		}
//	}
    
	@Override
	public void onPause() {
		listview.stopRefresh();
		listview.stopLoadMore();
		super.onPause();
	}
}
