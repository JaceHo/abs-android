package edu.hebtu.movingcampus.subjects;

import android.app.Activity;
import edu.hebtu.movingcampus.entity.Course;
import edu.hebtu.movingcampus.subject.base.BaseListdataSubject;

public class CourseListSubject extends BaseListdataSubject<Course> {
	private String domain;

	public CourseListSubject(Activity ac) {
		super(ac);
	}

	@Override
	public Boolean mesureChange() {
		// TODO Auto-generated method stub
		return null;
	}
}
