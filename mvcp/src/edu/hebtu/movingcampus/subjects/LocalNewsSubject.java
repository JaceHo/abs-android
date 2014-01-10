package edu.hebtu.movingcampus.subjects;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.subject.base.Newsdump;
import edu.hebtu.movingcampus.subject.base.Subject;
import edu.hebtu.movingcampus.subject.base.TitleNews;

public final class LocalNewsSubject extends Subject implements TitleNews {
	private List<Newsdump> localSubjects = new ArrayList<Newsdump>();

	private List<NewsShort> news = new ArrayList<NewsShort>();

	public LocalNewsSubject(Activity ac) {
		super(ac);
	}

	@Override
	public Boolean mesureChange() {
		for (Newsdump s : localSubjects)
			if (((Subject) s).mesureChange())
				return true;
		return false;
	}

	public void addLocalSubject(Newsdump subject) {
		localSubjects.add(subject);
	}

	@Override
	public List<NewsShort> dump() {
		for (Newsdump s : localSubjects)
			if(s!=null){
				news.addAll(s.dump());
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
}
