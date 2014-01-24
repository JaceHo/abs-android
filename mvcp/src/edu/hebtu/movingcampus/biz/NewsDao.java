package edu.hebtu.movingcampus.biz;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.NewsMore;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.utils.RequestCacheUtil;
import edu.hebtu.movingcampus.utils.Utility;

public class NewsDao extends BaseDao {
	public NewsDao(Activity activity) {
		super(activity);
	}

	/*
	 * 
	 * news search, brief news json mapper
	 */
	public ArrayList<NewsShort> mapperJson(boolean useCache, String key) {
		ArrayList<NewsShort> newsJson;
		try {
			String result;
			result = RequestCacheUtil.getRequestContentByGet(
					mActivity,
					String.format(Urls.NEWS_SEARCH, key)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_list, useCache);
			newsJson = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<NewsShort>>() {
					});
			if (newsJson == null) {
				return null;
			}
			return newsJson;

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

	/*
	 * 
	 * new list json mapper
	 */
	public ArrayList<NewsShort> mapperJson(boolean useCache, String type,
			String from, String size) {
		// 默认加载10条新闻
		if (size == null)
			size = "10";
		ArrayList<NewsShort> newsJson = null;
		try {
			String result;
			result = RequestCacheUtil.getRequestContentByGet(
					mActivity,
					String.format(Urls.NEWS_LIST, type, from, size)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_list, useCache);
			newsJson = mObjectMapper.readValue(result,
					new TypeReference<ArrayList<NewsShort>>() {
					});
			if (newsJson == null) {
				return null;
			}
			return newsJson;

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

	/*
	 * more news json mapper
	 */
	public NewsMore mapperJson(String id) {
		NewsMore response;
		try {
			String result = RequestCacheUtil.getRequestContentByGet(
					mActivity,
					String.format(Urls.NEWS_MORE, id)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_content, true);
			response = mObjectMapper.readValue(result,
					new TypeReference<NewsMore>() {
					});
			return response;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
