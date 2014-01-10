package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.apiproxy.PushSettings;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.activity.login.LoginActivity;
import edu.hebtu.movingcampus.activity.setting.AccountSettingActivity;
import edu.hebtu.movingcampus.activity.setting.SettingActivity;
import edu.hebtu.movingcampus.utils.Utils;

public class MainActivity extends BaseActivity {

	public static MainActivity instance = null;

	private ViewPager mTabPager;
	private ImageView mTabImg;
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;
	private int currIndex = 0;
	private int one;
	private int two;
	private int three;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;

	/**
	 * 连续按两次返回键就退出
	 */
	private static volatile int keyBackClickCount = 0;
	private final ArrayList<PageWraper> wrapers = new ArrayList<PageWraper>();

	// private Button mRightBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bindButton();
		// PushSettings.enableDebugMode(this, true);

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(MainActivity.this, "api_key"));
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		findViewById(R.id.tab_infocenter).setOnClickListener(
				new MyOnClickListener(0));
		findViewById(R.id.tab_studyresource).setOnClickListener(
				new MyOnClickListener(1));
		findViewById(R.id.tab_lib).setOnClickListener(new MyOnClickListener(2));
		findViewById(R.id.tab_card)
				.setOnClickListener(new MyOnClickListener(3));
		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4;
		two = one * 2;
		three = one * 3;
		// Log.i("info", "" + one + two + three + "X" + displayHeight);
		final ArrayList<View> views = new ArrayList<View>();

		// InitImageView();//
		LayoutInflater mLi = LayoutInflater.from(this);
		View infoCenter = mLi.inflate(R.layout.main_tab_infocenter, null);
		View studyResource = mLi.inflate(R.layout.main_tab_studyresource, null);
		View library = mLi.inflate(R.layout.main_tab_library, null);
		View card = mLi.inflate(R.layout.main_tab_card, null);

		views.add(infoCenter);
		wrapers.add(new InfoCenterActivity(infoCenter));
		views.add(studyResource);
		wrapers.add(new StudyResourceActivity(studyResource));
		views.add(library);
		wrapers.add(new LibraryActivity(library));
		views.add(card);
		wrapers.add(new AllInOneCardActivity(card));

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

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				break;
			}
			wrapers.get(arg0).onResume();
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
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
			menuWindow.dismiss();
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
			menuWindow.showAtLocation(this.findViewById(R.id.mainweixin),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		wrapers.get(currIndex).onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void bindButton() {
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.main_menu, null);

		menuWindow = new PopupWindow(layout,
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setBackgroundDrawable(new BitmapDrawable());

		Button mSetting = (Button) layout.findViewById(R.id.btn_setting);
		Button mAccount = (Button) layout.findViewById(R.id.btn_account);

		mSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Toast.makeText(Main.this, "�˳�",
				// Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SettingActivity.class);
				menuWindow.dismiss();
				startActivity(intent);
			}
		});
		mAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Toast.makeText(Main.this, "�˳�",
				// Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AccountSettingActivity.class);
				menuWindow.dismiss();
				startActivity(intent);
			}
		});

	}

}
