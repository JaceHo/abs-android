package edu.hebtu.movingcampus.activity.wrapper;

import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.SearchActivity;
import edu.hebtu.movingcampus.activity.base.ActionDispatcher;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.adapter.base.NewsPageAdapter;
import edu.hebtu.movingcampus.indicator.PageIndicator;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;
import edu.hebtu.movingcampus.utils.IntentUtil;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.utils.PopupWindowUtil;
import edu.hebtu.movingcampus.view.NewsFragment;

public class MainTabActivity implements OnClickListener, AnimationListener,
		PageWraper, NetworkchangeListener, ActionDispatcher {

	// [start]变量
	/**
	 * 数字代表列表顺序
	 */

	private View title;
	private LinearLayout mlinear_listview;

	// title标题
	private ImageView imgQuery;
	private ImageView imgMore;
	private ImageView imgLeft;
	private ImageView imgRight;

	// views
	private ViewPager mViewPager;
	private NewsPageAdapter mBasePageAdapter;
	private PageIndicator mIndicator;
	private LinearLayout loadLayout;
	private LinearLayout loadFaillayout;

	// init daos
	private LinearLayout llGoHome;
	private Button bn_refresh;

	private TextView mAboveTitle;
	// private SlidingMenu sm;
	private boolean mIsTitleHide = false;
	private boolean mIsAnim = false;

	public static String current_page = "0";

	private boolean isShowPopupWindows = false;
	private View content;
	private MainActivity mainActivity = MainActivity.instance;

	// [end]

	// [start]生命周期
	public MainTabActivity(View content) {
		this.content = content;
		initControl();
		initViewPager();
		initgoHome();
	}

	private void initControl() {

		loadLayout = (LinearLayout) content.findViewById(R.id.view_loading);
		loadFaillayout = (LinearLayout) content
				.findViewById(R.id.view_load_fail);
		mAboveTitle = (TextView) content.findViewById(R.id.tv_above_title);
		mAboveTitle.setText("信息中心");
		imgQuery = (ImageView) content.findViewById(R.id.imageview_above_query);
		imgQuery.setOnClickListener(this);
		imgQuery.setVisibility(View.GONE);
		imgMore = (ImageView) content.findViewById(R.id.imageview_above_more);
		imgMore.setOnClickListener(this);
		imgLeft = (ImageView) content.findViewById(R.id.imageview_above_left);
		imgRight = (ImageView) content.findViewById(R.id.imageview_above_right);

		mViewPager = (ViewPager) content.findViewById(R.id.above_pager);
		mIndicator = (PageIndicator) content.findViewById(R.id.above_indicator);

		llGoHome = (LinearLayout) content
				.findViewById(R.id.Linear_above_toHome);

		title = content.findViewById(R.id.main_title);
		mlinear_listview = (LinearLayout) content
				.findViewById(R.id.main_linear_listview);

		bn_refresh = (Button) content.findViewById(R.id.btn_refresh);
		bn_refresh.setOnClickListener(this);
	}

	private void initViewPager() {
		mBasePageAdapter = new NewsPageAdapter(mainActivity);
		mViewPager.setAdapter(mBasePageAdapter);
		mViewPager.setOffscreenPageLimit(0);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(new MyPageChangeListener());
		new MyTask().execute();
	}

	private void initgoHome() {
		llGoHome.setOnClickListener(this);
	}

	// [start]继承方法
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Linear_above_toHome:
			break;
		case R.id.imageview_above_more:
			if (isShowPopupWindows) {
				new PopupWindowUtil<Fragment>(mViewPager).showActionWindow(v,
						mainActivity, mBasePageAdapter.getFragments());
			}
			break;
		case R.id.imageview_above_query:

			if (NetWorkHelper.isNetworkAvailable(mainActivity)) {
				IntentUtil.start_activity(mainActivity, SearchActivity.class,
						new BasicNameValuePair("type", current_page));
			} else {
				Toast.makeText(mainActivity.getApplicationContext(),
						"网络连接失败,请检查网络", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btn_refresh:
			imgQuery.setVisibility(View.GONE);
			new MyTask().execute();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (mIsAnim || mViewPager.getChildCount() <= 1) {
			return false;
		}
		final int action = event.getAction();

		float x = event.getX();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			lastX = x;
			return false;
		case MotionEvent.ACTION_MOVE:
			float dY = Math.abs(y - lastY);
			float dX = Math.abs(x - lastX);
			boolean down = y > lastY ? true : false;
			lastY = y;
			lastX = x;
			if (dX < 8 && dY > 8 && !mIsTitleHide && !down) {
				Animation anim = AnimationUtils.loadAnimation(mainActivity,
						R.anim.push_top_in);
				// anim.setFillAfter(true);
				anim.setAnimationListener(MainTabActivity.this);
				title.startAnimation(anim);
			} else if (dX < 8 && dY > 8 && mIsTitleHide && down) {
				Animation anim = AnimationUtils.loadAnimation(mainActivity,
						R.anim.push_top_out);
				// anim.setFillAfter(true);
				anim.setAnimationListener(MainTabActivity.this);
				title.startAnimation(anim);
			} else {
				return false;
			}
			mIsTitleHide = !mIsTitleHide;
			mIsAnim = true;
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * 加载分类list的task
	 * 
	 * @author wangxin
	 */
	public class MyTask extends AsyncTask<Void, String, Void> {

		private boolean mUseCache;

		public MyTask() {
			mUseCache = true;
		}

		public MyTask(boolean useCache) {
			mUseCache = useCache;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imgLeft.setVisibility(View.GONE);
			imgRight.setVisibility(View.GONE);
			loadLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager.removeAllViews();
			isShowPopupWindows = false;
		}

		@Override
		protected Void  doInBackground(Void... params) {
			((NewsFragment)mBasePageAdapter.getItem(Integer.parseInt(current_page))).onLoad();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			isShowPopupWindows = true;
			mViewPager.removeAllViews();
			if (result != null) {
				imgRight.setVisibility(View.VISIBLE);
				loadLayout.setVisibility(View.GONE);
				loadFaillayout.setVisibility(View.GONE);
			} else {
				//mBasePageAdapter.addNullFragment();
				loadLayout.setVisibility(View.GONE);
				loadFaillayout.setVisibility(View.VISIBLE);
			}
			mViewPager.setVisibility(View.VISIBLE);
			mBasePageAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(0);
			mIndicator.notifyDataSetChanged();
		}
	}

	/**
	 * viewPager切换页面
	 * 
	 * @author mingxv
	 */
	class MyPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				imgLeft.setVisibility(View.GONE);
			} else if (arg0 == mBasePageAdapter.getFragments().size() - 1) {
				imgRight.setVisibility(View.GONE);
			} else {
				imgRight.setVisibility(View.VISIBLE);
			}
			current_page = "" + arg0;
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (mIsTitleHide) {
			title.setVisibility(View.GONE);
		} else {

		}
		mIsAnim = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		title.setVisibility(View.VISIBLE);
		if (mIsTitleHide) {
			FrameLayout.LayoutParams lp = (LayoutParams) mlinear_listview
					.getLayoutParams();
			lp.setMargins(0, 0, 0, 0);
			mlinear_listview.setLayoutParams(lp);
		} else {
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) title
					.getLayoutParams();
			lp.setMargins(0, 0, 0, 0);
			title.setLayoutParams(lp);
			FrameLayout.LayoutParams lp1 = (LayoutParams) mlinear_listview
					.getLayoutParams();
			lp1.setMargins(0, mainActivity.getResources()
					.getDimensionPixelSize(R.dimen.title_height), 0, 0);
			mlinear_listview.setLayoutParams(lp1);
		}
	}

	private float lastX = 0;
	private float lastY = 0;

	@Override
	public void onDataEnabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
	}
}
