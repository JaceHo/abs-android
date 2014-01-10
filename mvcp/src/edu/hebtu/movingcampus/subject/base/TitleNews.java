package edu.hebtu.movingcampus.subject.base;

/*
 * 一个种类新闻标题,如信息中心..
 */
public interface TitleNews extends Newsdump {
	public void clear();

	public long getId();

	public String getDesc();

	public int getIcon();
}
