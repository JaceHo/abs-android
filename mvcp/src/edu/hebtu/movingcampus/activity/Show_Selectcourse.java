package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.entity.MajorCourse;

public class Show_Selectcourse extends BaseActivity {
	private final int ITEM_BACK = 0;
	private final int ITEM_EDIT = 2;
	private final int ITEM_EXIT = 3;
	private final int ITEM_HOME = 1;
	private ArrayList<String> SelectCourse;
	private String[] courseArray;
	private ArrayList<MajorCourse> courselist;
	private ImageView img_show;
	private ListView lv_show;
	private AlertDialog menuDialog;
	private GridView menuGridView;
	private View menuView;
	private TextView tv_XnXq;
	private TextView tv_id;
	private TextView tv_name;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(LayoutInflater.from(this).inflate(R.layout.showcourse_main, null));
		//TODO
	//	setScoreView();
	}

	private SimpleAdapter getMenuAdapter(String[] paramArrayOfString,
			int[] paramArrayOfInt) {
		ArrayList localArrayList = new ArrayList();
		for (int i = 0;; i++) {
			if (i >= paramArrayOfString.length)
				return new SimpleAdapter(this, localArrayList, 2130903052,
						new String[] { "itemImage", "itemText" }, new int[] {
								2131165254, 2131165255 });
			HashMap localHashMap = new HashMap();
			localHashMap.put("itemImage", Integer.valueOf(paramArrayOfInt[i]));
			localHashMap.put("itemText", paramArrayOfString[i]);
			localArrayList.add(localHashMap);
		}
	}

	private void setScoreView() {
		// this.courselist = new RemoteDBHelper().getCourseOfMajor();
		this.courseArray = new String[this.courselist.size()];
		for (int i = 0;; i++) {
			if (i >= this.courselist.size()) {
				this.img_show = ((ImageView) findViewById(2131165283));
				this.img_show.setBackgroundResource(2130837534);
				this.tv_XnXq = ((TextView) findViewById(2131165288));
				String str2 = AppInfo.getXnXq().substring(0, 9)
						+ "学年 第"
						+ AppInfo.getXnXq().substring(
								-1 + AppInfo.getXnXq().length()) + "学期";
				this.tv_XnXq.setText(str2);
				this.tv_id = ((TextView) findViewById(2131165284));
				this.tv_name = ((TextView) findViewById(2131165286));
				this.tv_id.setText(AppInfo.getUser().getUserName()
						.toCharArray(), 0, AppInfo.getUser().getUserName()
						.length());
				this.tv_name.setText(AppInfo.getUser().getUserName()
						.toCharArray(), 0, AppInfo.getUser().getUserName()
						.length());
				this.lv_show = ((ListView) findViewById(2131165280));
				this.lv_show.setCacheColorHint(Color.rgb(231, 230, 216));
				this.lv_show.setAdapter(new MyAdapter(null));
				return;
			}
			MajorCourse localMajorCourse = this.courselist.get(i);
			String str1 = " 课程名：" + localMajorCourse.getKcm() + "\n" + " 课程类别："
					+ localMajorCourse.getKclb() + "\n 任课教师："
					+ localMajorCourse.getJsm() + " ";
			this.courseArray[i] = str1;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		paramMenu.add("menu");
		return super.onCreateOptionsMenu(paramMenu);
	}

	@Override
	public boolean onMenuOpened(int paramInt, Menu paramMenu) {
		if (this.menuDialog == null) {
			this.menuDialog = new AlertDialog.Builder(this).setView(
					this.menuView).create();
			this.menuDialog.getWindow().setGravity(80);
			this.menuDialog.show();
		}
		while (true) {
			return false;
		}
	}

	private class MyAdapter extends BaseAdapter {
		private MyAdapter(Object object) {
		}

		@Override
		public int getCount() {
			return Show_Selectcourse.this.courseArray.length;
		}

		@Override
		public Object getItem(int paramInt) {
			return Integer.valueOf(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView != null)
				;
			for (TextView localTextView = (TextView) paramView;; localTextView = new TextView(
					Show_Selectcourse.this.getApplicationContext())) {
				localTextView
						.setText(Show_Selectcourse.this.courseArray[paramInt]);
				localTextView.setPadding(10, 0, 0, 0);
				localTextView.setTextSize(15.0F);
				localTextView.setTypeface(Typeface.DEFAULT, 0);
				localTextView.setTextColor(-16777216);
				return localTextView;
			}
		}
	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Show_Selectcourse.this.finish();
					}
				});
	}
}