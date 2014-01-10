package edu.hebtu.movingcampus.enums;

import edu.hebtu.movingcampus.R;

public enum NewsType {
	// 网站新闻,分学院+|学校
	O_LOCAL, O_HOLIDAY, O_COLLEAGE, O_SCHOOLS, O_ACADEMIC, O_NOTICE_SPEECH, O_JOB, O_JWNOTICE, O_SECOND_HNAD;
	public String getDesc() {
		switch (ordinal()) {
		case 0:
			return "本地信息";
		case 1:
			return "放假通知";
		case 2:
			return "学院新闻";
		case 3:
			return "学校新闻";
		case 4:
			return "学术新闻";
		case 5:
			return "公告讲座";
		case 6:
			return "就业信息";
		case 7:
			return "教务信息";
		case 8:
			return "二手交易";
		default:
			break;
		}
		return null;
	}

	public int getIconResource() {
		switch (ordinal()) {
		case 0:
			return R.drawable.btn_course;
		case 1:
			return R.drawable.btn_course;
		case 2:
			return R.drawable.btn_course;
		case 3:
			return R.drawable.btn_course;
		case 4:
			return R.drawable.btn_course;
		case 5:
			return R.drawable.btn_course;
		case 6:
			return R.drawable.btn_course;
		case 7:
			return R.drawable.btn_course;
		case 8:
			return R.drawable.btn_course;
		default:
			break;
		}
		return 0;
	}
}
