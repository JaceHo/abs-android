package edu.hebtu.movingcampus.subject.base;

import java.util.List;

import android.app.Activity;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;

public abstract class ListBaseSubject<T> extends Subject{

	private List<T> datas=null;

	public ListBaseSubject(AdapterBase<T> data) {
		this.datas=data.getList();
	}
}
