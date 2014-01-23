package edu.hebtu.movingcampus.subjects;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.subject.base.OneofNews;
import edu.hebtu.movingcampus.subject.base.Subject;
import edu.hebtu.movingcampus.subject.base.ListOfNews;

public final class LocalNewsSubject extends Subject implements ListOfNews {
	private List<OneofNews> localSubjects = new ArrayList<OneofNews>();

	private List<NewsShort> news = new ArrayList<NewsShort>();

	public LocalNewsSubject() {
	}

	@Override
	public Boolean mesureChange(Activity ac) {
		for (OneofNews s : localSubjects)
			if (((Subject) s).mesureChange(ac))
				return true;
		return false;
	}

	public LocalNewsSubject addLocalSubject(OneofNews subject) {
		localSubjects.add(subject);
		return this;
	}

	@Override
	public List<NewsShort> dump(Activity context) {
		for (OneofNews s : localSubjects)
			if(s!=null){
				news.add(s.dump(context));
			}
		return news;
	}

	@Override
	public long getId() {
		return NewsType.O_LOCAL.ordinal();
	}

	@Override
	public int getIcon() {
		return NewsType.O_LOCAL.getIconResource();
	}

	@Override
	public String getDesc() {
		return NewsType.O_LOCAL.getDesc();
	}

	@Override
	public void clear() {
		news.clear();
	}

	@Override
	public String getTag() {
		return "subject"+getDesc();
	}
}
