package edu.hebtu.movingcampus.subjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import edu.hebtu.movingcampus.biz.LibraryDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.BorrowedBook;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.enums.LocalNewsType;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.subject.base.OneofNews;
import edu.hebtu.movingcampus.subject.base.Subject;

/**
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:30 AM
 */
public class LibrarySubject extends Subject implements OneofNews {

	private ArrayList<BorrowedBook> books;
	//设置剩余days天提醒 
	public static int days;

	public LibrarySubject(Activity ac) {
		SharedPreferences pre = ac.getSharedPreferences(
				Constants.PREFER_FILE, 0);
		LibrarySubject.days = pre.getInt("card.days",10);
	}

	@Override
	public Boolean mesureChange(Activity ac) {
		books = new LibraryDao(ac).mapperJson(days + "");
		if (books != null && books.size() > 0)
			return true;
		return false;
	}

	@Override
	public NewsShort dump(Activity ac) {
		NewsShort news = new NewsShort();
		if (mesureChange(ac)) {
			news.setType(NewsType.O_LOCAL);
			news.setTitle("借书到期提醒:");
			news.setDate(new Date());
			String content = "以下是"+days+"天内到期图书\n,详细信息：";
			for (BorrowedBook b : books) {
				content += "图书名称:" + b.getName() + "/n";
				if (b.getFine() > 0)
					content += "图书超期罚款:" + b.getFine() + " 元/n";
				content += "图书离应还日期剩余天数:" + b.getRemainTime() + " 天/n";
				content += "图书续借次数:" + b.getReBorrow() + "次/n";
			}

			news.setDate(new Date());
			news.setContent(content);
			// TODO,id?
			news.setID(LocalNewsType.I_LIB_NOTIFY.ordinal() + 10000);
		} 
		return news;
	}

	@Override
	public String getTag() {
		return "subject.library";
	}
}
