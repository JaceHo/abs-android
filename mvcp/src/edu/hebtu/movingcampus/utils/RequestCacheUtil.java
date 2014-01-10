package edu.hebtu.movingcampus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.config.Configs;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.db.DBHelper;
import edu.hebtu.movingcampus.db.RequestCacheColumn;

//整个调用方法就是异步，这里不需要异步
public class RequestCacheUtil {
	private static final String TAG = "RequestCacheUtil";

	private static LinkedHashMap<String, SoftReference<String>> RequestCache = new LinkedHashMap<String, SoftReference<String>>(
			20);

	// [start] 公有方法

	/**
	 * it is a get way to visit a websit
	 * @param context
	 * @param RequestUrl
	 * @param source_type
	 * @param content_type
	 * @param UseCache
	 * @return
	 */
	public static String getRequestContentByGet(Context context, String RequestUrl,
			String source_type, String content_type, boolean UseCache) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		String md5 = MD5.encode(RequestUrl);
		// 缓存目录
		if (!CommonUtil.sdCardIsAvailable())/* true 为可用 */{
			String cachePath = AppInfo.cacheDir + "/"
					+ md5; // data里的缓存
			return getCacheRequestByGet(context, RequestUrl, cachePath, source_type,
					content_type, dbHelper, UseCache);
		} else {
			String imagePath = AppInfo.cacheDir + File.separator
					+ md5; // sd卡
			return getCacheRequestByGet(context, RequestUrl, imagePath, source_type,
					content_type, dbHelper, UseCache);
		}
	}
	/**
	 * it is a post way to visit a websit
	 * @param context
	 * @param RequestUrl
	 * @param source_type
	 * @param content_type
	 * @param UseCache
	 * @return
	 */
	public static String getRequestContentByPost(Context context, String RequestUrl,
			String source_type, String content_type, boolean UseCache) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		String md5 = MD5.encode(RequestUrl);
		// 缓存目录
		if (!CommonUtil.sdCardIsAvailable())/* true 为可用 */{
			String cachePath = context.getCacheDir().getAbsolutePath() + "/"
					+ md5; // data里的缓存
			return getCacheRequestByPost(context, RequestUrl, cachePath, source_type,
					content_type, dbHelper, UseCache);
		} else {
			String imagePath = getExternalCacheDir(context) + File.separator
					+ md5; // sd卡
			return getCacheRequestByPost(context, RequestUrl, imagePath, source_type,
					content_type, dbHelper, UseCache);
		}
	}
	// [end]

	// [start] 私有方法

	/**
	 * 获得程序在sd|root上的cahce目录
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	//TODO not used
	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context) {
		// android 2.2 以后才支持的特性
		if (hasExternalCacheDir()) {
			if(context.getExternalCacheDir()!=null)
					return new File(context.getExternalCacheDir().getPath() + File.separator
							+ "request");
		}else{
		// Before Froyo we need to construct the external cache dir ourselves
		// 2.2以前我们需要自己构造
			final String cacheDir = "/Android/data/" + context.getPackageName()
					+ "/cache/request/";
			if(Environment.getExternalStorageDirectory()!=null)
			return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
		}

		return new File(context.getCacheDir().getPath() + File.separator + "request");
	}

	private static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}
	/**
	 * it is a get method that used to connect to web
	 * @param context
	 * @param requestUrl
	 * @param requestPath
	 * @param source_type
	 * @param content_type
	 * @param dbHelper
	 * @param useCache
	 * @return
	 */
	private static String getCacheRequestByGet(Context context, String requestUrl,
			String requestPath, String source_type, String content_type,
			DBHelper dbHelper, boolean useCache) {
		String result = "";
		if (useCache) {
			result = getStringFromSoftReference(requestUrl);
			if (!result.equals(null) && !result.equals("")) {
				return result;
			}
			result = getStringFromLocal(requestPath, requestUrl, dbHelper);
			if (!result.equals(null) && !result.equals("")) {
				putStringForSoftReference(requestUrl, result);
				return result;
			}
		}
		// not using cache or cannot find any cache file in sqlite or localfile
		// , getStringFromWeb instead
		result = getStringFromWebByGet(context, requestPath, requestUrl,
				source_type, content_type, dbHelper);
		return result;
	}
	/**
	 * it is a post method that used to connect to web
	 * @param context
	 * @param requestUrl
	 * @param requestPath
	 * @param source_type
	 * @param content_type
	 * @param dbHelper
	 * @param useCache
	 * @return
	 */
	private static String getCacheRequestByPost(Context context, String requestUrl,
			String requestPath, String source_type, String content_type,
			DBHelper dbHelper, boolean useCache) {
		String result = "";
		if (useCache) {
			result = getStringFromSoftReference(requestUrl);
			if (!result.equals(null) && !result.equals("")) {
				return result;
			}
			result = getStringFromLocal(requestPath, requestUrl, dbHelper);
			if (!result.equals(null) && !result.equals("")) {
				putStringForSoftReference(requestUrl, result);
				return result;
			}
		}
		// not using cache or cannot find any cache file in sqlite or localfile
		// , getStringFromWeb instead
		result = getStringFromWebByPost(context, requestPath, requestUrl,
				source_type, content_type, dbHelper);
		return result;
	}
	private static void putStringForSoftReference(String requestUrl,
			String result) {
		SoftReference<String> referece = new SoftReference<String>(result);
		RequestCache.put(requestUrl, referece);
	}

	/**
	 * to visit a service by post method
	 * @param context
	 * @param requestPath
	 * @param requestUrl
	 * @param source_type
	 * @param content_type
	 * @param dbHelper
	 * @return
	 */
	private static String getStringFromWebByGet(Context context, String requestPath,
			String requestUrl, String source_type, String content_type,
			DBHelper dbHelper) {
		String result = "";
		try {
			result = HttpUtils.getByHttpClient(context, requestUrl);
			if (result.equals(null) || result.equals("")) {
				return result;
			}
			// 更新数据库
			Cursor cursor = getStringFromDB(requestUrl, dbHelper);
			updateDB(cursor, requestUrl, source_type, content_type, dbHelper);
			saveFileByRequestPath(requestPath, result);
			putStringForSoftReference(requestUrl, result);
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * to visit a service by post method
	 * @param context
	 * @param requestPath
	 * @param requestUrl
	 * @param source_type
	 * @param content_type
	 * @param dbHelper
	 * @return the result of request of web
	 */
	private static String getStringFromWebByPost(Context context, String requestPath,
			String requestUrl, String source_type, String content_type,
			DBHelper dbHelper) {
		String result = "";
		try {
			result = HttpUtils.postByHttpClient(context, requestUrl);
			if (result.equals(null) || result.equals("")) {
				return result;
			}
			// 更新数据库
			Cursor cursor = getStringFromDB(requestUrl, dbHelper);
			updateDB(cursor, requestUrl, source_type, content_type, dbHelper);
			saveFileByRequestPath(requestPath, result);
			putStringForSoftReference(requestUrl, result);
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	private static void saveFileByRequestPath(String requestPath, String result) {
		deleteFileFromLocal(requestPath);
		saveFileForLocal(requestPath, result);
	}

	private static void saveFileForLocal(String requestPath, String result) {
		File file = new File(requestPath);
		if (!file.exists()) {
			try {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				file.createNewFile();
				FileOutputStream fout = new FileOutputStream(file);
				byte[] buffer = result.getBytes();
				fout.write(buffer);
				fout.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void updateDB(Cursor cursor, String requestUrl,
			String source_type, String content_type, DBHelper dbHelper) {
		if (cursor != null && cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(RequestCacheColumn._ID));
			long timestamp = System.currentTimeMillis();
			String SQL = "update " + RequestCacheColumn.TABLE_NAME + " set "
					+ RequestCacheColumn.Timestamp + "=" + timestamp
					+ " where " + RequestCacheColumn._ID + "=" + id;
			dbHelper.ExecSQL(SQL);
		} else {
			// 添加
			String SQL = "insert into " + RequestCacheColumn.TABLE_NAME + "("
					+ RequestCacheColumn.URL + ","
					+ RequestCacheColumn.SOURCE_TYPE + ","
					+ RequestCacheColumn.Content_type + ","
					+ RequestCacheColumn.Timestamp + ") values('" + requestUrl
					+ "','" + source_type + "','" + content_type + "','"
					+ System.currentTimeMillis() + "')";
			dbHelper.ExecSQL(SQL);
		}
	}

	private static String getStringFromSoftReference(String requestUrl) {
		if (RequestCache.containsKey(requestUrl)) {
			SoftReference<String> reference = RequestCache.get(requestUrl);
			String result = (String) reference.get();
			if (result != null && !result.equals("")) {
				return result;
			}
		}
		return "";
	}

	private static String getStringFromLocal(String requestPath,
			String requestUrl, DBHelper dbHelper) {
		String result = "";
		Cursor cursor = getStringFromDB(requestUrl, dbHelper);
		if (cursor != null && cursor.moveToFirst()) {
			Long timestamp = cursor.getLong(cursor
					.getColumnIndex(RequestCacheColumn.Timestamp));
			String strContentType = cursor.getString(cursor
					.getColumnIndex(RequestCacheColumn.Content_type));
			long span = getSpanTimeFromConfigs(strContentType);
			long nowTime = System.currentTimeMillis();
			if ((nowTime - timestamp) > span * 60 * 1000) {
				// 过期
				deleteFileFromLocal(requestPath);
			} else {
				// 没过期
				result = getFileFromLocal(requestPath);
			}
			cursor.close();
		}
		return result;
	}

	/**
	 * 从db中查找数据
	 * 
	 * @param requestUrl
	 * @param dbHelper
	 * @return
	 */
	private static Cursor getStringFromDB(String requestUrl, DBHelper dbHelper) {
		String SQL = "select * from " + RequestCacheColumn.TABLE_NAME
				+ " where " + RequestCacheColumn.URL + "='" + requestUrl + "'";
		return dbHelper.rawQuery(SQL, new String[] {});
	}

	private static String getFileFromLocal(String requestPath) {
		// TODO Auto-generated method stub
		File file = new File(requestPath);
		String result = "";
		if (file.exists()) {
			FileInputStream fileIn;
			try {
				fileIn = new FileInputStream(file);

				int length = fileIn.available();
				byte[] buffer = new byte[length];
				fileIn.read(buffer);
				result = EncodingUtils.getString(buffer, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		return "";
	}

	private static void deleteFileFromLocal(String requestPath) {
		// TODO Auto-generated method stub
		File file = new File(requestPath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 根据类型获取缓存时间
	 * 
	 * @param str
	 * @return
	 */
	private static long getSpanTimeFromConfigs(String str) {
		long span = 0;
		if (str.equals(Constants.DBContentType.Content_list)) {
			span = Configs.Content_ListCacheTime;
		} else if (str.equals(Constants.DBContentType.Content_content)) {
			span = Configs.Content_ContentCacheTime;
		} else {
			span = Configs.Content_DefaultCacheTime;
		}
		return span;
	}
	// [end]

}
