package info.futureme.abs.biz;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

/**
 * context manager hold app context to be used whenever in app lifecycle, and it
 * also can be used to provide service, assetmanager, configuration, windowmanager
 * and so on.
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 15:07:09
 */
public class ContextManager {

    private static String dataDir = null;

    public static void initDataDir(String dataDir){
        ContextManager.dataDir = dataDir;
    }

    public static String getDataDir(){
        return ContextManager.dataDir;
    }

    public static void init(Context context) {
        sContext = context;

        if (context != null) {
            sAppContext = context.getApplicationContext();
        }
    }

    // app scope

    private static Context sAppContext;

    public static Context appContext() {
        return sAppContext;
    }

    public static Resources resources() {
        return sAppContext.getResources();
    }

    public static AssetManager assetManager() {
        return sAppContext.getAssets();
    }

    public static ContentResolver contentResolver() {
        return sAppContext.getContentResolver();
    }

    public static Object systemService(String name) {
        if (!TextUtils.isEmpty(name)) {
            return sAppContext.getSystemService(name);
        }
        return null;
    }

	/**
	 * shared prefercence
	 * 
	 * @param name
	 * @param mode
	 */
    public static SharedPreferences sharedPreferences(String name, int mode) {
        return sAppContext.getSharedPreferences(name, mode);
    }

    public static PackageManager packageManager() {
        return sAppContext.getPackageManager();
    }

    public static ActivityManager activityManager() {
        return (ActivityManager) sAppContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

	/**
	 * app info
	 */
    public static ApplicationInfo appInfo() {
        return sAppContext.getApplicationInfo();
    }

	/**
	 * resource configuration
	 */
    public static Configuration config() {
        return resources().getConfiguration();
    }

    public static ViewConfiguration viewConfig() {
        return ViewConfiguration.get(sContext);
    }

    // activity/service scope

    private static Context sContext;

    public static Context context() {
        return sContext;
    }

    public static Activity activity() {
        return (Activity) sContext;
    }

    public static Window window() {
        return activity().getWindow();
    }

    public static WindowManager windowManager() {
        return (WindowManager) sAppContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public static Service service() {
        return (Service) sContext;
    }
}
