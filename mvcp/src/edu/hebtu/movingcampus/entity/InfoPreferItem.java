package edu.hebtu.movingcampus.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.TextView;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.enums.NewsType;

public class InfoPreferItem {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InfoPreferItem(int id) {
		this.id = id;
	}

	public String getTitle() {
		return NewsType.values()[id].getDesc();
	}

	public void setTitle(TextView textView) {
		textView.setText(getTitle());
	}

	public boolean isChecked(Context context) {
		SharedPreferences pre = context.getSharedPreferences(
				Constants.PREFER_FILE, 0);
		return pre.getBoolean("news_" + getId(), true);
	}

	/**
	 * set checked to sharedpreferences by checkbox
	 * 
	 * @param check
	 * @param context
	 */
	public void setChecked(CheckBox check, boolean checked, Context context) {
		SharedPreferences pre = context.getSharedPreferences(
				Constants.PREFER_FILE, 0);
		SharedPreferences.Editor localEditor = pre.edit();
		localEditor.putBoolean("news_" + getId(), checked);
		localEditor.commit();
	}
}
