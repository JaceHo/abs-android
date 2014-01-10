package edu.hebtu.movingcampus.biz;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.BorrowedBook;
import edu.hebtu.movingcampus.utils.CustomHttpClient;

public class LibraryDao extends BaseDao {
	public LibraryDao(Activity activity) {
		super(activity);
	}

	/*
	 * lock|unlock card json mapper return true success false otherwise
	 */
	public ArrayList<BorrowedBook> mapperJson(String days) {
		// TODO Auto-generated method stub
		// 用cache
		ArrayList<BorrowedBook> res;
		try {
			String result;
			result = CustomHttpClient.postByHttpClient(mActivity,
					String.format(Urls.BOOKS_SEARCH, days));
			res = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<BorrowedBook>>() {
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
