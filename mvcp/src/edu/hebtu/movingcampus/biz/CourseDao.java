package edu.hebtu.movingcampus.biz;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.Course;
import edu.hebtu.movingcampus.utils.CustomHttpClient;

public class CourseDao extends BaseDao {
	public CourseDao(Activity activity) {
		super(activity);
	}

	public ArrayList<ArrayList<Course>> mapperJson(String xn, String xq,
			String domain) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<Course>> res;
		try {
			String result;
			result = CustomHttpClient.postByHttpClient(mActivity,
					String.format(Urls.COURSE_TABLE, xn, xq, domain));
			res = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<ArrayList<Course>>>() {
					});
			if (res == null) {
				return null;
			}
			return res;

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
