package edu.hebtu.movingcampus.subject.base;

import java.util.List;

import android.app.Activity;

import edu.hebtu.movingcampus.entity.NewsShort;

/*
 * 一个种类新闻标题,如信息中心..
 */
public interface ListOfNews{

	public void clear();

	public long getId();

	public String getDesc();

	public int getIcon();
	
	public List<NewsShort> dump(Activity context);
}
