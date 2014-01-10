package edu.hebtu.movingcampus.subject.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public abstract class BaseListdataSubject<T> extends Subject{

	private List<T> datas=new ArrayList<T>();

	public BaseListdataSubject(Activity ac) {
		super(ac);
	}

}
