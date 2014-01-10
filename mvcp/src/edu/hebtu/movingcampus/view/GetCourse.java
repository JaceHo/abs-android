package edu.hebtu.movingcampus.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.biz.CourseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.Course;

/**
 * @author
 */
@SuppressLint("ShowToast")
public class GetCourse {
	private TextView tv1_2, tv1_3, tv2_2, tv2_3, tv3_2, tv3_3, tv4_2, tv4_3,
			tv5_2, tv5_3;
	private String[] course = new String[5];
	private String[] add = new String[5];
	private Activity context;
	private ArrayList<ArrayList<Course>> courseList;

	public GetCourse(Activity context) {
		this.context = context;
		// TODO
		courseList = new CourseDao(context).mapperJson(AppInfo.getXn(),
				AppInfo.getXq(), Constants.COURSE_DOMAIN.STUDENT);
		if (courseList == null) {
			Toast.makeText(context, "错误!", Toast.LENGTH_SHORT);
		}
	}

	public View getScheduleView(int week) {

		View view = View.inflate(context, R.layout.showcourse_page, null);
		LayoutInflater mInflater = LayoutInflater.from(context);
		View myView = mInflater.inflate(R.layout.showcourse_page, null);
		ArrayList<Course> courses = courseList.get(week - 1);

		// tv0 = (TextView) myView.findViewById(R.id.show_tv0);

		tv1_2 = (TextView) myView.findViewById(R.id.show_tv1_2);
		tv1_3 = (TextView) myView.findViewById(R.id.show_tv1_3);

		tv2_2 = (TextView) myView.findViewById(R.id.show_tv2_2);
		tv2_3 = (TextView) myView.findViewById(R.id.show_tv2_3);

		tv3_2 = (TextView) myView.findViewById(R.id.show_tv3_2);
		tv3_3 = (TextView) myView.findViewById(R.id.show_tv3_3);

		tv4_2 = (TextView) myView.findViewById(R.id.show_tv4_2);
		tv4_3 = (TextView) myView.findViewById(R.id.show_tv4_3);

		tv5_2 = (TextView) myView.findViewById(R.id.show_tv5_2);
		tv5_3 = (TextView) myView.findViewById(R.id.show_tv5_3);

		Log.i("GetSchedule", week + "");

		for (int i = 0; i < courses.size(); i++) {
			Course c = courses.get(i);
			if (c.getStatus()) {
				course[i] = c.getKcm() + " " + c.getNum() + c.getNum();
				add[i] = c.getUnit() + " " + c.getRoomid();
			} else {
				course[i] = "";
				add[i] = "";
			}
		}

		tv1_2.setText(course[0]);
		tv1_3.setText(add[0]);

		tv2_2.setText(course[1]);
		tv2_3.setText(add[1]);

		tv3_2.setText(course[2]);
		tv3_3.setText(add[2]);

		tv4_2.setText(course[3]);
		tv4_3.setText(add[3]);

		tv5_2.setText(course[4]);
		tv5_3.setText(add[4]);

		return myView;
	}

}
