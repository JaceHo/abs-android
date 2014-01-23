package edu.hebtu.movingcampus.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.login.LoginActivity;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.entity.NewsMore;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;
import edu.hebtu.movingcampus.utils.CommonUtil;

@SuppressLint("SetJavaScriptEnabled")
public class NewsDetailsActivity extends BaseActivity implements
		OnClickListener,NetworkchangeListener {

	private NewsDao detailDao;
	static final String mimeType = "text/html";
	static final String encoding = "utf-8";

	private RelativeLayout share;

	private TextView detailTitle;
	private ImageView imgShare;

	private LinearLayout loadLayout;
	private LinearLayout failLayout;
	private Button bn_refresh;
	private ImageView imgGoHome;
	private WebView mWebView;
	private String id;
	private String mTitle;
	private String shareUrl;
	private String shareTitle;

	private int screen_width;
	private NewsMore responseEntity;

	private SharedPreferences sharePre;
	private String mKey;
	public boolean loaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity);
		loaded=false;
		Intent i = getIntent();
		id = i.getStringExtra("id");
		mTitle = i.getStringExtra("title");
		shareTitle = i.getStringExtra("sharetitle");
		sharePre = getSharedPreferences(LoginActivity.SharedName,
				Context.MODE_PRIVATE);
		mKey = sharePre.getString(LoginActivity.KEY, "");
		initData();
		initControl();
		bindButton();
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
	}

	private void initControl() {
		detailDao = new NewsDao(this);
		detailTitle = (TextView) findViewById(R.id.details_textview_title);
		detailTitle.setText(mTitle);
		loadLayout = (LinearLayout) findViewById(R.id.view_loading);
		failLayout = (LinearLayout) findViewById(R.id.view_load_fail);
		bn_refresh = (Button) findViewById(R.id.btn_refresh);

		share = (RelativeLayout) findViewById(R.id.rlShare);

	    // Initialize the WebView
		mWebView = (WebView) findViewById(R.id.detail_webView);
		this.mWebView.setBackgroundColor(0);
		this.mWebView.setBackgroundResource(R.color.detail_bgColor);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
	    mWebView.getSettings().setSupportZoom(true);
	    mWebView.getSettings().setBuiltInZoomControls(true);
	    mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    mWebView.setScrollbarFadingEnabled(true);
	    mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
	    mWebView.getSettings().setLoadsImagesAutomatically(true);

		share.setOnClickListener(this);

		bn_refresh.setOnClickListener(this);

		imgShare = (ImageView) findViewById(R.id.imageview_details_share);

		imgGoHome = (ImageView) findViewById(R.id.details_imageview_gohome);
		imgGoHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		MyTask mTask = new MyTask();
		mTask.execute();
	}

	class MyTask extends AsyncTask<String, Integer, String> {

		private boolean mUseCache;

		public MyTask() {
			mUseCache = false;
		}

		public MyTask(boolean useCache) {
			mUseCache = useCache;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loaded=false;
			loadLayout.setVisibility(View.VISIBLE);
			failLayout.setVisibility(View.GONE);
			mWebView.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if ((responseEntity = detailDao.mapperJson(id)) != null) {
				shareUrl = responseEntity.getShareURL();
				return responseEntity.getDetail();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				String linkCss = "<link rel=\"stylesheet\" href=\"file:///android_asset/pygments.css\" type=\"text/css\"/>";
				String content = linkCss + result;
				try {
					content = content.replace(
							"img{}",
							"img{width:"
									+ CommonUtil.px2dip(
											NewsDetailsActivity.this,
											screen_width) + "}");
					content = content.replaceAll("<br />", "");
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				loadLayout.setVisibility(View.GONE);
				failLayout.setVisibility(View.GONE);
				mWebView.setVisibility(View.VISIBLE);
				mWebView.setBackgroundResource(R.color.detail_bgColor);
				mWebView.loadDataWithBaseURL(null, content, "text/html",
						"utf-8", null);
				loaded=true;
			} else {
				loadLayout.setVisibility(View.GONE);
				mWebView.setVisibility(View.GONE);
				failLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_refresh) {
			new MyTask().execute();
			return;
		}
		if (responseEntity == null)
			return;
//		if (mKey.equals(null) || mKey.equals("")) {
//			showLongToast(getResources().getString(R.string.user_login_prompt));
//			return;
//		}
		switch (v.getId()) {
		case R.id.rlShare:
			recommandToYourFriend(shareUrl, shareTitle);
			break;
		}

	}

	@Override
	protected void bindButton() {
		findViewById(R.id.details_imageview_gohome).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NewsDetailsActivity.this.finish();
			}
		});
	}

	@Override
	public void onDataEnabled() {
		if(!loaded){
			MyTask mTask = new MyTask();
			mTask.execute();
		}
	}

	@Override
	public void onDataDisabled() {
		mWebView.stopLoading();
	}

	@Override
	public void onResume(){
		super.onResume();
		NetworkChangeReceiver.registNetWorkListener(this);
	}
	@Override
	public void onPause(){
		super.onPause();
		NetworkChangeReceiver.unRegistNetworkListener(this);
	}
}
