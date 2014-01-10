package edu.hebtu.movingcampus;

import java.io.File;

import com.baidu.android.pushservice.apiproxy.PushSettings;
import com.baidu.frontia.FrontiaApplication;

import android.app.Application;
import edu.hebtu.movingcampus.entity.User;
import edu.hebtu.movingcampus.utils.RequestCacheUtil;

/**
 * preferenced data holder extends Application
 * 
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:32 AM
 */
public class AppInfo extends FrontiaApplication{
	// ?TODO
	private static String XnXq = "2013-2014学年第2学期";
	// 会话用户
	private static User user;

	public static File cacheDir;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		cacheDir=new File(getApplicationContext().getCacheDir().getAbsolutePath());

		// if (!CommonUtil.sdCardIsAvailable()) { // sdcard not available
		// cacheDir = new File(Environment.getDataDirectory().getAbsolutePath()
		// + "/data/" + getPackageName()
		// + "/eoecn/cache/imgs");
		// } else {
		// cacheDir = new File(Constants.CachePath.IMAGE_CACHE_PATH);
		// }
		//

		// ImageLoaderConfiguration config = new
		// ImageLoaderConfiguration.Builder(this)
		// .memoryCacheExtraOptions(480, 800) // default = device screen
		// dimensions
		// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
		// .threadPoolSize(3) // default
		// .threadPriority(Thread.NORM_PRIORITY - 1) // default
		// .denyCacheImageMultipleSizesInMemory()
		// .offOutOfMemoryHandling()
		// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //
		// default
		// .discCache(new UnlimitedDiscCache(cacheDir)) // default
		// .discCacheSize(50 * 1024 * 1024)
		// .discCacheFileCount(100)
		// .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) //
		// default
		// .imageDownloader(new URLConnectionImageDownloader()) // default
		// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		// .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) //
		// default
		// .enableLogging()
		// .build();
		// ImageLoader.getInstance().init(config);

	}

	public static User getUser() {
		return user;
	}

	public static String getXnXq() {
		return XnXq.trim();
	}

	public static void setUser(User paramUser) {
		user = paramUser;
	}

	// TODO;
	public static String getXn() {
		return "2013";
	}

	public static String getXq() {
		return "2";
	}

	public static void setXnXq(String paramString) {
		XnXq = paramString;
	}

	public String getUsername() {
		return user.getUserName();
	}

	public String getPassword() {
		return user.getPassword();
	}

}