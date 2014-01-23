package edu.hebtu.movingcampus.utils;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetWorkHelper {

	private static String LOG_TAG = "NetWorkHelper";

	public static Uri uri = Uri.parse("content://telephony/carriers");

	/**
	 * 判断是否有网络连接
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {
			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable()) {
						Log.d(LOG_TAG, "network is available");
						return true;
					}
				}
			}
		}
		Log.d(LOG_TAG, "network is not available");
		return false;
	}

	   public static int TYPE_WIFI = 1;
       public static int TYPE_MOBILE = 2;
       public static int TYPE_NOT_CONNECTED = 0;
       
       
       public static int getConnectivityStatus(Context context) {
               ConnectivityManager cm = (ConnectivityManager) context
                               .getSystemService(Context.CONNECTIVITY_SERVICE);

               NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
               if (null != activeNetwork) {
                       if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                               return TYPE_WIFI;
                       
                       if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                               return TYPE_MOBILE;
               }
               return TYPE_NOT_CONNECTED;
       }
       
       public static String getConnectivityStatusString(Context context) {
               int conn = NetWorkHelper.getConnectivityStatus(context);
               String status = null;
               if (conn == NetWorkHelper.TYPE_WIFI) {
                       status = "Wifi enabled";
               } else if (conn == NetWorkHelper.TYPE_MOBILE) {
                       status = "Mobile data enabled";
               } else if (conn == NetWorkHelper.TYPE_NOT_CONNECTED) {
                       status = "Not connected to Internet";
               }
               return status;
       }
	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	/**
	 * 判断网络是否为漫游
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.isNetworkRoaming()) {
					Log.d(LOG_TAG, "network is roaming");
					return true;
				} else {
					Log.d(LOG_TAG, "network is not roaming");
				}
			} else {
				Log.d(LOG_TAG, "not using mobile network");
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isMobileDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isMobileDataEnable = false;

		isMobileDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		return isMobileDataEnable;
	}

	/**
	 * 判断wifi 是否可用
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isWifiDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiDataEnable = false;
		isWifiDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		return isWifiDataEnable;
	}

	/**
	 * 设置Mobile网络开关
	 * 
	 * @param context
	 * @param enabled
	 * @throws Exception
	 */
	public static void setMobileDataEnabled(Context context, boolean enabled)
			throws Exception {
		APNManager apnManager = APNManager.getInstance(context);
		List<APN> list = apnManager.getAPNList();
		if (enabled) {
			for (APN apn : list) {
				ContentValues cv = new ContentValues();
				cv.put("apn", apnManager.matchAPN(apn.apn));
				cv.put("type", apnManager.matchAPN(apn.type));
				context.getContentResolver().update(uri, cv, "_id=?",
						new String[] { apn.apnId });
			}
		} else {
			for (APN apn : list) {
				ContentValues cv = new ContentValues();
				cv.put("apn", apnManager.matchAPN(apn.apn) + "mdev");
				cv.put("type", apnManager.matchAPN(apn.type) + "mdev");
				context.getContentResolver().update(uri, cv, "_id=?",
						new String[] { apn.apnId });
			}
		}
	}

}
