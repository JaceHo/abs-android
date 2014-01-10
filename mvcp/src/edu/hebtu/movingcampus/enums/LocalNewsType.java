/**
 * Project: MovingCampus
 */
package edu.hebtu.movingcampus.enums;

import edu.hebtu.movingcampus.R;

/**
 * @author hippo
 * 
 */
// 新闻来源，学校，学院网站新闻
public enum LocalNewsType {
	// 模块信息更新
	I_LIB_NOTIFY, I_CARD_NOTIFY,  I_EXAM_SCORE_NOTIFY,I_EXAM_INFOR_NOTIFY,I_COURSE_NOTIFY, I_CLASSROOM_APPLY_NOTIFY;
	/**
	 * TODO 信息分类Icon
	 * 
	 * @return
	 */
	public int getIconResource() {
		switch (ordinal()) {
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
		default:
			break;
		}
		return 0;
	}
}
