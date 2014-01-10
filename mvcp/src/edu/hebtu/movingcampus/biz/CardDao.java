package edu.hebtu.movingcampus.biz;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.entity.CardEntity;
import edu.hebtu.movingcampus.utils.CustomHttpClient;
import edu.hebtu.movingcampus.utils.HttpUtils;
import edu.hebtu.movingcampus.utils.RequestCacheUtil;
import edu.hebtu.movingcampus.utils.Utility;

public class CardDao extends BaseDao {
	public CardDao(Activity activity) {
		super(activity);
	}

	/*
	 * lock|unlock card json mapper return true success false otherwise
	 */
	public Boolean mapperJson(String action) {
		Boolean res;
		try {
			String result;
			result = CustomHttpClient.postByHttpClient(mActivity,
					String.format(Urls.CARD_STATUS, action));
			res = mObjectMapper.readValue(result, new TypeReference<Boolean>() {
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

	/*
	 * card status json mapper return user from web,or null if error
	 */
	public CardEntity mapperJson(boolean useCache) {
		CardEntity balanceBean;
		try {
			String result;
			result = HttpUtils.postByHttpClient(mActivity, String.format(
					Urls.CARD_STATUS, Constants.ACTION_DEFAULT_LOOKUP));
			RequestCacheUtil.getRequestContentByPost(
					mActivity,
					String.format(Urls.CARD_STATUS,
							Constants.ACTION_DEFAULT_LOOKUP)
							+ Utility.getScreenParams(mActivity),
					Constants.WebSourceType.Json,
					Constants.DBContentType.Content_content, useCache);
			balanceBean = mObjectMapper.readValue(result,
					new TypeReference<CardEntity>() {
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
