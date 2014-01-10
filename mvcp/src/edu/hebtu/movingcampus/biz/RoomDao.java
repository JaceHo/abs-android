package edu.hebtu.movingcampus.biz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.ClassRoom;
import edu.hebtu.movingcampus.utils.CustomHttpClient;

public class RoomDao extends BaseDao {
	public RoomDao(Activity activity) {
		super(activity);
	}

	/*
	 * return true success false otherwise
	 */
	public List<ClassRoom> mapperJson(String xiqu, String bd, String zc,
			String xq, String jc) {
		ArrayList<ClassRoom> res;
		try {
			String result;
			// get?TODO
			result = CustomHttpClient.postByHttpClient(mActivity,
					String.format(Urls.FREEROOM_SEARCH, xiqu, bd, zc, xq, jc));
			res = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<ClassRoom>>() {
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
