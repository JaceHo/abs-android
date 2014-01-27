package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseSlidingFragmentActivity;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.activity.setting.AccountSettingActivity;
import edu.hebtu.movingcampus.activity.setting.SettingActivity;
import edu.hebtu.movingcampus.activity.wrapper.AllInOneCardActivity;
import edu.hebtu.movingcampus.activity.wrapper.InfoCenterActivity;
import edu.hebtu.movingcampus.activity.wrapper.LibraryActivity;
import edu.hebtu.movingcampus.activity.wrapper.StudyResourceActivity;
import edu.hebtu.movingcampus.activity.wrapper.UlitiesActivity;
import edu.hebtu.movingcampus.slidingmenu.SlidingMenu;
import edu.hebtu.movingcampus.utils.Utils;

public class MainActivity extends BaseSlidingFragmentActivity {

	private final String LIST_TEXT = "text";
	private final String LIST_IMAGEVIEW = "img";
	public static MainActivity instance = null;

	private ViewPager mTabPager;
	private SimpleAdapter lvAdapter;
	private ListView lvTitle;
	private int zero = 0;
	private int currIndex = 0;
	private int one;
	//private PopupWindow menuWindow;
	//private LayoutInflater inflater;

	/**
	 * 连续按两次返回键就退出
	 */
	private SlidingMenu sm;
	private static volatile int keyBackClickCount = 0;
	private final ArrayList<PageWraper> wrapers = new ArrayList<PageWraper>();

	// private Button mRightBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		setBehindContentView(R.layout.behind_slidingmenu);

		lvTitle = (ListView) findViewById(R.id.behind_list_show);
		// PushSettings.enableDebugMode(this, true);

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(MainActivity.this, "api_key"));
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
		initViewPager();
		initSlidingMenu();
		bindButton();
	}

	private void initViewPager() {
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4;
		// Log.i("info", "" + one + two + three + "X" + displayHeight);
		final ArrayList<View> views = new ArrayList<View>();

		// InitImageView();//
		LayoutInflater mLi = LayoutInflater.from(this);
		View infoCenter = mLi.inflate(R.layout.main_tab_infocenter, null);
		View studyResource = mLi.inflate(R.layout.main_tab_studyresource, null);
		View library = mLi.inflate(R.layout.main_tab_library, null);
		View card = mLi.inflate(R.layout.main_tab_card, null);
		View ulities = mLi.inflate(R.layout.main_tab_ulities, null);

		views.add(infoCenter);
		wrapers.add(new InfoCenterActivity(infoCenter));
		views.add(studyResource);
		wrapers.add(new StudyResourceActivity(studyResource));
		views.add(library);
		wrapers.add(new LibraryActivity(library));
		views.add(card);
		wrapers.add(new AllInOneCardActivity(card));
		views.add(ulities);
		wrapers.add(new UlitiesActivity(ulities));

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mTabPager.setAdapter(mPagerAdapter);

		
	}

	@Override
	public void onResume() {
		super.onResume();
		keyBackClickCount = 0;
		wrapers.get(currIndex).onResume();
	}

	// [start]初始化函数
	private void initSlidingMenu() {
		// customize the SlidingMenu
		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// sm.setFadeDegree(0.35f);

		//屏幕左右滑动不能显示menu,显示上一页，下一页
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// sm.setShadowWidth(20);
		sm.setBehindScrollScale(0);
		initListView();
	}

	/**
	 * 左侧栏菜单初始化，可以加入删除模块
	 * @return
	 */
	private List<Map<String, Object>> getData() {
		// TODO image button
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.info_center_title));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_handpick);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.study_resource));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_news);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.library_title));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_studio);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.card_title));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_blog);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.ulities_title));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_blog);
		list.add(map);
		return list;
	}

	//初始化左侧菜单列表按Mainactivity 中currentInx指定
	private void initListView() {
		lvAdapter = new SimpleAdapter(this, getData(),
				R.layout.behind_list_show, new String[] { LIST_TEXT,
						LIST_IMAGEVIEW },
				new int[] { R.id.textview_behind_title,
						R.id.imageview_behind_icon }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub.
				View view = super.getView(position, convertView, parent);
				if (position == currIndex) {
					view.setBackgroundResource(R.drawable.back_behind_list);
					lvTitle.setTag(view);
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
				}
				return view;
			}
		};
		lvTitle.setAdapter(lvAdapter);
		lvTitle.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==currIndex)return ;
				mTabPager.setCurrentItem(position);
				if (lvTitle.getTag() != null) {
					if (lvTitle.getTag() == view) {
						MainActivity.this.showContent();
						return;
					}
					((View) lvTitle.getTag())
							.setBackgroundColor(Color.TRANSPARENT);
				}
				lvTitle.setTag(view);
				view.setBackgroundResource(R.drawable.back_behind_list);
				sm.toggle();
			}
		});
	}

	/**
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};

	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			if(currIndex!=arg0)
				animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
			wrapers.get(currIndex).onPause();
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(Math.abs(currIndex-arg0)*500);
			//mTabImg.startAnimation(animation);
			wrapers.get(arg0).onResume();
			
			//最左侧pager让右滑出现左侧栏,其他pager左右滑切换pager
			if(currIndex!=0) sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			else sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//menuWindow.dismiss();
			if(sm.isMenuShowing()){
				sm.toggle();
				return super.onKeyDown(keyCode, event);
			}
			switch (keyBackClickCount++) {
			case 0:
				Toast.makeText(this,
						getResources().getString(R.string.press_again_exit),
						Toast.LENGTH_SHORT).show();
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							keyBackClickCount = 0;
						}
					}, 3000);
				} catch (Exception e) {
					keyBackClickCount = 0;
				}
				break;
			case 1:
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			default:
				break;
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// menuWindow.showAtLocation(this.findViewById(R.id.mainweixin),
			// Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			if (sm.isMenuShowing()) {
				toggle();
			} else {
				showMenu();
			}

			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		wrapers.get(currIndex).onActivityResult(requestCode, resultCode, data);
	}

	protected void bindButton() {
		findViewById(R.id.cbFeedback).setOnClickListener(
				new View.OnClickListener() {
					@Override
					//feedback activity from umeng sdk.
					public void onClick(View arg0) {
		                new FeedbackAgent(MainActivity.this).startFeedbackActivity();
					}
				});
		findViewById(R.id.cbSetting).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								SettingActivity.class);
						// menuWindow.dismiss();
						startActivity(intent);
					}
				});
		findViewById(R.id.cbAccount).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								AccountSettingActivity.class);
						// menuWindow.dismiss();
						startActivity(intent);
					}
				});

	}

}
