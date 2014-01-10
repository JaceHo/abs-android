package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.view.GetCourse;

public class ShowCourse extends BaseActivity {

	private ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;

	private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
	private String WEEK = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showcourse_main);

		InitTextView();
		InitImageView();
		InitViewPager();
		bindButton();
	}

	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ShowCourse.this.finish();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}

	private void InitTextView() {
		tv1 = (TextView) findViewById(R.id.text1);
		tv2 = (TextView) findViewById(R.id.text2);
		tv3 = (TextView) findViewById(R.id.text3);
		tv4 = (TextView) findViewById(R.id.text4);
		tv5 = (TextView) findViewById(R.id.text5);
		tv6 = (TextView) findViewById(R.id.text6);
		tv7 = (TextView) findViewById(R.id.text7);

		tv1.setOnClickListener(new MyOnClickListener(0));
		tv2.setOnClickListener(new MyOnClickListener(1));
		tv3.setOnClickListener(new MyOnClickListener(2));
		tv4.setOnClickListener(new MyOnClickListener(3));
		tv5.setOnClickListener(new MyOnClickListener(4));
		tv6.setOnClickListener(new MyOnClickListener(5));
		tv7.setOnClickListener(new MyOnClickListener(6));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		GetCourse getSchedule = new GetCourse(this);
		// TODO

		View monView = getSchedule.getScheduleView(1);
		View tueView = getSchedule.getScheduleView(2);
		View wedView = getSchedule.getScheduleView(3);
		View thuView = getSchedule.getScheduleView(4);
		View friView = getSchedule.getScheduleView(5);
		View stuView = getSchedule.getScheduleView(6);
		View sunView = getSchedule.getScheduleView(7);

		listViews.add(monView);
		listViews.add(tueView);
		listViews.add(wedView);
		listViews.add(thuView);
		listViews.add(friView);
		listViews.add(stuView);
		listViews.add(sunView);

		Intent intent = getIntent();
		WEEK = intent.getIntExtra("POSITION", 1) + "";
		// Log.i("intent.getIntExtra", WEEK);
		mPager.setAdapter(new MyPagerAdapter(listViews));
		// currIndex=Integer.parseInt(WEEK)-1;
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(Integer.parseInt(WEEK) - 1);

		// Log.i("WEEK_int", Integer.parseInt(WEEK)-1+"");

	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a_small)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 5 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			Log.i("arg0", arg0 + "");
			if (currIndex != arg0)
				animation = new TranslateAnimation(currIndex == 0 ? offset
						: one * currIndex, arg0 * one, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

}
