package edu.hebtu.movingcampus.config;

public class Constants {

	// 偏好设置文件
	public static final String PREFER_FILE = "mvcp.info";

	// 分类处理请求
	public static final class TAGS {
		public static final String NEWS = "news";
		public static final String IIBRARY = "lib";
		public static final String CARD = "card";
		public static final String ROOM = "room";
		public static final String COURSE = "course";
		public static final String EXAM = "exam";
	}

	public static final class COURSE_DOMAIN {
		public static final String TEACHER = "teacher";
		public static final String CLASS = "class";
		public static final String STUDENT = "student";
	}

	public static final class DBContentType {
		public static final String Content_list = "list";
		public static final String Content_content = "content";
	}

	// 结果类型
	public static final class WebSourceType {
		public static final String Json = "json";
		public static final String Xml = "xml";
	}

	public static final String BALANCE_LOWEAST="card.loweast";
	public static final String LIB_DAYS="books.days";

	public static final String NEWS_MORE = "more";
	public static final String NEWS_HEADPIC = "more";
	public static final String ACTION_LOCK = "lock";
	public static final String ACTION_DEFAULT_LOOKUP = "lookup";
	public static final String ACTION_UNLOCK = "unlock";
}
