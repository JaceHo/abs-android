package edu.hebtu.movingcampus.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class CurrentVersion {
	private static final String TAG = "config";
	public static final String appPackName = "edu.hebtu";

	public static String getAppName(Context paramContext) {
		return paramContext.getResources().getText(2131034114).toString();
	}

	public static int getVerCode(Context paramContext)
			throws PackageManager.NameNotFoundException {
		try {
			int i = paramContext.getPackageManager().getPackageInfo(
					"edu.hebtu", 0).versionCode;
			return i;
		} catch (Exception localException) {
			Log.e("config", localException.getMessage());
		}
		return 1;
	}

	public static String getVerName(Context paramContext) {
		try {
			String str = paramContext.getPackageManager().getPackageInfo(
					"edu.hebtu", 0).versionName;
			return str;
		} catch (Exception localException) {
			Log.e("config", localException.getMessage());
		}
		return "";
	}
}