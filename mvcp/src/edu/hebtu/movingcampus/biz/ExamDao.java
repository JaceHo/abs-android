package edu.hebtu.movingcampus.biz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.ClassRoom;
import edu.hebtu.movingcampus.entity.ExamScore;
import edu.hebtu.movingcampus.entity.ExamineInfor;
import edu.hebtu.movingcampus.utils.RequestCacheUtil;
import edu.hebtu.movingcampus.utils.Utility;

public class ExamDao extends BaseDao {
	public ExamDao(Activity activity) {
		super(activity);
	}

	public List<ExamineInfor> getExamPlanMsg(boolean useCache) {
		ArrayList<ExamineInfor> balanceBean;
		try {
			String result;
			result = RequestCacheUtil.getRequestContentByPost(
					mActivity,
					String.format(Urls.EXAM_SCHEDULE)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_list, useCache);
			balanceBean = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<ExamineInfor>>() {
					});
			if (balanceBean == null) {
				return null;
			}
			return balanceBean;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public List<ClassRoom> getFreeRoomMsg(boolean useCache,String school,String building,String week,String weekday,String classes){
		ArrayList<ClassRoom> balanceBean;
		try {
			String result;
			result = RequestCacheUtil.getRequestContentByPost(
					mActivity,
					String.format(Urls.FREEROOM_SEARCH, school, building,week,weekday,classes)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_list, useCache);
			balanceBean = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<ClassRoom>>() {
					});
			if (balanceBean == null) {
				return null;
			}
			return balanceBean;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<ExamScore> getExamResultMsg(boolean useCache, String xn, String xq,String coursetype) {
		ArrayList<ExamScore> balanceBean;
		try {
			String result;
			result = RequestCacheUtil.getRequestContentByPost(
					mActivity,
					String.format(Urls.EXAM_SCORE_CONSULT, xn, xq,coursetype)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_list, useCache);
			balanceBean = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<ExamScore>>() {
					});
			if (balanceBean == null) {
				return null;
			}
			return balanceBean;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}