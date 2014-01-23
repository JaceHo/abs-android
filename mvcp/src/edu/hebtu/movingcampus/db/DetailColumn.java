package edu.hebtu.movingcampus.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class DetailColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "newsdetail";
	// public static final String KEY_WORD = "key_word";
	// public static final String CONTENT_ID = "contentID";
	public static final String URL = "url";
	public static final String JSON_PATH= "json_path";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(URL, "text");
		mColumnMap.put(JSON_PATH, "text");
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

	@Override
	public Uri getTableContent() {
		// TODO Auto-generated method stub
		return CONTENT_URI;
	}

	@Override
	protected Map<String, String> getTableMap() {
		// TODO Auto-generated method stub
		return mColumnMap;
	}

}
