package edu.hebtu.movingcampus.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseFragmentActivity;
import edu.hebtu.movingcampus.adapter.NewsListAdapter;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;
import edu.hebtu.movingcampus.widget.XListView;

public class SearchActivity extends BaseFragmentActivity implements
		OnClickListener, XListView.IXListViewListener,NetworkchangeListener{

	private ImageView btnGohome;
	private String searchContent;
	private EditText edtSearch;
	private XListView listview;
	private LinearLayout loadLayout;
	private NewsListAdapter adapter;
	private String mTag;
	private InputMethodManager imm;
	private List<NewsShort> newsResponse;
	private NewsDao newsDao;
	private ImageView mWait;
	private boolean loaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		Intent i = getIntent();
		mTag = i.getStringExtra("tag");
		loaded=false;
		initData();
		initView();
		bindButton();
	}

	private void bindButton() {
		findViewById(R.id.btn_gohome).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SearchActivity.this.finish();
					}
				});
		findViewById(R.id.img_search).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						edtSearch.clearFocus();
						searchContent = edtSearch.getText().toString();
						new MyTask(searchContent, true, true).execute(newsDao);
					}
				});
	}

	public void initData() {
		listview = (XListView) findViewById(R.id.list_view);
		newsDao = new NewsDao(this);
		listview.setXListViewListener(this);
		adapter = new NewsListAdapter(this, R.layout.news_item,listview);
		listview.setAdapter(adapter);
		imm = (InputMethodManager) getApplicationContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
	}

	public void initView() {
		// txtEmpty = (TextView) findViewById(R.id.txt_empty);
		btnGohome = (ImageView) findViewById(R.id.btn_gohome);
		btnGohome.setOnClickListener(this);
		edtSearch = (EditText) findViewById(R.id.edt_search);
		loadLayout = (LinearLayout) findViewById(R.id.view_loading);

		loadLayout.setVisibility(View.GONE);
		edtSearch.setHint("即将为您搜索 " + mTag);
		edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imm.showSoftInput(v, 0);
				} else {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});

		edtSearch.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (v.getTag() == null) {
						v.setTag(1);
						edtSearch.clearFocus();
						searchContent = edtSearch.getText().toString();
						new MyTask(searchContent, true, true).execute(newsDao);

					} else {
						v.setTag(null);
					}
					return true;
				}
				return false;
			}
		});
		mWait = (ImageView) findViewById(R.id.search_imageview_wait);
	}

	public class MyTask extends
			AsyncTask<BaseDao, String, ArrayList<NewsShort>> {
		private String content;
		private boolean useCache;
		private boolean clear;

		public MyTask(String content) {
			this.content = content;
			this.clear = false;
			this.useCache = true;
		}

		public MyTask(String content, boolean usecache) {
			this.content = content;
			this.useCache = usecache;
			this.clear = false;
		}

		public MyTask(String content, boolean usecache, boolean clear) {
			this.content = content;
			this.useCache = usecache;
			this.clear = clear;
		}

		@Override
		protected void onPreExecute() {
			if (clear)
				adapter.clear();
			loaded=false;
			mWait.setVisibility(View.GONE);
			loadLayout.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<NewsShort> doInBackground(BaseDao... params) {
			BaseDao dao = params[0];
			if ((newsResponse = ((NewsDao) dao).mapperJson(useCache,
					MyTask.this.content)) != null) {
				return (ArrayList<NewsShort>) newsResponse;
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<NewsShort> result) {
			super.onPostExecute(result);
			listview.setRefreshTime(new SimpleDateFormat("MM/dd HH:mm:ss")
					.format(new Date()));
			if (result != null)
				adapter.appendToList(result);

			loadLayout.setVisibility(View.GONE);
			mWait.setVisibility(View.GONE);
			loaded=true;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_gohome:
			SearchActivity.this.finish();
			break;
		}
	}

	@Override
	public void onRefresh() {
		new MyTask(searchContent, true, true).execute(newsDao);
	}

	@Override
	public void onLoadMore() {
		if (newsResponse != null && newsResponse.size() > 0)
			new MyTask(searchContent, true).execute(newsDao);
		else
			listview.stopLoadMore();
	}

	@Override 
	public void onResume(){
		super.onResume();
		NetworkChangeReceiver.unRegistNetworkListener(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		listview.stopRefresh();
		listview.stopLoadMore();
		NetworkChangeReceiver.registNetWorkListener(this);
	}

	@Override
	public void onDataEnabled() {
		if(!loaded)
			new MyTask(searchContent, true).execute(newsDao);
	}

	@Override
	public void onDataDisabled() {
		// TODO Auto-generated method stub
		
	}
}
