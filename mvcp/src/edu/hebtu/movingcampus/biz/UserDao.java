package edu.hebtu.movingcampus.biz;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.User;
import edu.hebtu.movingcampus.utils.CustomHttpClient;

public class UserDao extends BaseDao {
	public UserDao(Activity activity) {
		super(activity);
	}

	/*
	 * user json mapper return user from web,or null if error
	 */
	public User mapperJson(String name, String pswd) {
		// TODO Auto-generated method stub
		User userJson;
		try {
			String result;
			result = CustomHttpClient.postByHttpClient(mActivity,
					String.format(Urls.USER_LOGIN, name, pswd));
			userJson = mObjectMapper.readValue(result,
					new TypeReference<User>() {
					});
			if (userJson == null) {
				return null;
			}
			return userJson;

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
