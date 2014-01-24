package edu.hebtu.movingcampus.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;

import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.setting.About;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.adapter.InfoNewsAdapter;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.db.DBHelper;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.subjects.LocalNewsSubject;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;
import edu.hebtu.movingcampus.subjects.NewsSubject;
import edu.hebtu.movingcampus.utils.IntentUtil;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.utils.ImageUtil.ImageCallback;
import edu.hebtu.movingcampus.widget.XListView;

public class NewsListActivity extends BaseActivity implements OnClickListener,
		XListView.IXListViewListener,NetworkchangeListener {
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
	private InfoNewsAdapter adapter;

	private String id;

	// load responseData
	private ArrayList<NewsShort> newsResponseData;
	private NewsSubject subject=null;
	private LocalNewsSubject localSubject=null;
	private boolean loaded;

	// [start]生命周期
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);

		loaded=false;
		imgQuery = (ImageView) findViewById(R.id.imageview_above_query);
		imgMore = (ImageView) findViewById(R.id.imageview_above_more);
		loadfailed = findViewById(R.id.view_load_fail);
		loading = findViewById(R.id.view_loading);
		id = getIntent().getStringExtra("id");
		if(Integer.parseInt(id)==0)
			localSubject=(LocalNewsSubject) IPreference.getInstance(this).getListOfNewsSubjectByID(Integer.parseInt(id));
		else
			subject=(NewsSubject) IPreference.getInstance(this).getListOfNewsSubjectByID(Integer.parseInt(id));


		initClass();
		initControl();
		bindButton();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			DBHelper db = DBHelper.getInstance(this);
			db.closeDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initControl() {

		imgQuery.setOnClickListener(this);
		imgQuery.setVisibility(View.VISIBLE);
		imgMore.setOnClickListener(this);
		loading.setVisibility(View.VISIBLE);
		loadfailed.setVisibility(View.GONE);

		listview = (XListView) findViewById(R.id.xlistview_news);
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(false);
		adapter = new InfoNewsAdapter(this, R.layout.news_item_layout,listview);

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
		case R.id.cbFeedback:
			FeedbackAgent agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			break;
		case R.id.cbSetting:
			IntentUtil.start_activity(this, About.class);
			break;
		}

	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_refresh).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onRefresh();
					}
				});
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
			if (id.equals("0"))
				IPreference.getInstance(NewsListActivity.this)
						.getListOfNewsSubjectByID(Integer.parseInt(id)).clear();
			loading.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(BaseDao... params) {
			// 本地信息
			if (id.equals("0"))
				newsResponseData = (ArrayList<NewsShort>) IPreference
						.getInstance(NewsListActivity.this)
						.getListOfNewsSubjectByID(Integer.parseInt(id)).dump(NewsListActivity.this);
			else
				newsResponseData = newsDao.mapperJson(true, id,
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
				loaded=true;
			} else {
				loading.setVisibility(View.GONE);
				loadfailed.setVisibility(View.VISIBLE);
				loaded=false;
			}
			onLoaded();
		}
	}

	@Override
	public void onRefresh() {
		mRunningTask = new MyTask(true, true).execute(newsDao);
	}

	@Override
	public void onLoadMore() {
		//上一次获取不为空显示加载
		if(newsResponseData==null||newsResponseData.size()>0)
			listview.stopLoadMore();
		else 
			mRunningTask = new MyTask(true).execute(newsDao);
	}
	@Override
	public void onResume(){
		super.onResume();
		NetworkChangeReceiver.registNetWorkListener(this);
	}

	protected void onLoaded() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime("刚刚");
	}

	@Override
	public void onPause() {
		super.onPause();
		listview.stopRefresh();
		listview.stopLoadMore();
		NetworkChangeReceiver.unRegistNetworkListener(this);
	}

	@Override
	public void onDataEnabled() {
		if(!loaded) mRunningTask.execute(newsDao);
	}

	@Override
	public void onDataDisabled() {
		// TODO Auto-generated method stub
		
	}
}
