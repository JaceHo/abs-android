package info.futureme.abs.example.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.MD5;

public class CommonUtil {

    public static String getDoubleBase64Md5(String plain){
        String pass = org.jivesoftware.smack.util.Base64.encodeBytes(new MD5(plain).getMD5().getBytes());
        return org.jivesoftware.smack.util.Base64.encodeBytes(pass.getBytes());
    }

    public static LinkedHashMap<String, String> getGenerateQueryMap(String urlString) {
        Uri uri = Uri.parse(urlString);
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        LinkedHashMap<String, String> queryMap = new LinkedHashMap<>();
        Iterator<String> iterator = queryParameterNames.iterator();
        while (iterator.hasNext()) {
            String queryName = iterator.next();
            String queryParameter = uri.getQueryParameter(queryName);
            queryMap.put(queryName, queryParameter);
        }
        return queryMap;
    }

    public static boolean isGPSEnable(Context context) {
        LocationManager locationManager =
                ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查SD卡是否插入
     *
     * @return
     *
     */
    public static boolean checkSDCARD() {

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static String getOwnedDataDir(Context context){
        if(CommonUtil.checkSDCARD()){
            return Environment.getExternalStorageDirectory()+File.separator+ MVSConstants.DataConstants.DATA_DIR;
        }
        else{
            return context.getCacheDir().getAbsolutePath()+File.separator+ MVSConstants.DataConstants.DATA_DIR;
        }
    }

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean sdCardIsAvailable() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return true;
    }

    /**
     * Checks if there is enough Space on SDCard
     *
     * @param updateSize Size to Check
     * @return True if the Update will fit on SDCard, false if not enough space
     * on SDCard Will also return false, if the SDCard is not mounted as
     * read/write
     */
    public static boolean enoughSpaceOnSdCard(long updateSize) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return (updateSize < getRealSizeOnSdcard());
    }

    /**
     * get the space is left over on sdcard
     */
    public static long getRealSizeOnSdcard() {
        File path = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * Checks if there is enough Space on phone self
     */
    public static boolean enoughSpaceOnPhone(long updateSize) {
        return getRealSizeOnPhone() > updateSize;
    }

    /**
     * get the space is left over on phone self
     */
    public static long getRealSizeOnPhone() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long realSize = blockSize * availableBlocks;
        return realSize;
    }

    /**
     * 根据手机分辨率从dp转成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f) - 15;
    }

    public static String getAppName(Context paramContext) {
        return paramContext.getResources().getText(R.string.app_name)
                .toString();
    }

    public static int  getScreenWidth(Activity context){
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getVerCode(Context paramContext) {
        PackageInfo pInfo;
        try {
            pInfo = paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVerName(Context paramContext) {
        try {
            return paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0).versionName;
        } catch (Exception localException) {
            DLog.e("config", localException.getMessage());
        }
        return "";
    }
}
