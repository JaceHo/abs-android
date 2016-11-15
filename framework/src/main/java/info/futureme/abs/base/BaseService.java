package info.futureme.abs.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import info.futureme.abs.util.AppHelper;

/**
 * Base service for data updating purpose which depends on network connection, use
 * this if you need a longtime lasting service that continuously fetch data that
 * should be updated.
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 14:47:21
 */
public abstract class BaseService extends Service {

	public static final String TAG = "BaseService";

	/**
	 * action start if needed
	 */
	protected static final String ACTION_START = AppHelper.packageName() + ".START";
	/**
	 * action stop if needed
	 */
	protected static final String ACTION_STOP = AppHelper.packageName() + ".STOP";
	/**
	 * action error if service encountered an error
	 */
	protected static final String ACTION_ERROR = AppHelper.packageName() + ".ERROR";
	/**
	 * action restart when needed
	 */
	protected static final String ACTION_RESTART = AppHelper.packageName() + ".RESTART";

	/**
	 * extra preferences used to record if service was started
	 */
	protected SharedPreferences mPrefs;
	protected volatile boolean mStarted;
	protected String mPrefStartedValue;
	
	protected ConnectivityManager mConnMan;
	
	/**
	 * initial retry interval when service encountered en error
	 */
	protected static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	/**
	 * top interval limitation to terminal a service restart
	 */
	protected static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;

	public static void actionStart(Context ctx, Class<?> clazz)
	{
		Intent i = new Intent(ctx, clazz);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	public static void actionStop(Context ctx, Class<?> clazz)
	{
		Intent i = new Intent(ctx, clazz);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}
	public static void actionError(Context ctx, Class<?> clazz)
	{
		Intent i = new Intent(ctx, clazz);
		i.setAction(ACTION_ERROR);
		ctx.startService(i);
	}

	
	@Override
	public void onCreate()
	{
		super.onCreate();

		mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
		
		mConnMan = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
 
		/* If our process was reaped by the system for any reason we need
		 * to restore our state with merely a call to onCreate.  We record
		 * the last "started" value and restore it here if necessary. */
		handleCrashedService();
	}
	
	
	protected void handleCrashedService()
	{
		if (wasStarted())
		{
			/* We probably didn't get a chance to clean up gracefully, so do
			 * it now. */
			//hideNotification();
			/* Formally start and attempt connection. */
			start();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "Service started with intent=" + intent);

		super.onStartCommand(intent, flags, startId);
		if (intent == null || intent.getAction() == null) return START_STICKY;
		
		if (intent.getAction().equals(ACTION_STOP)) {
			stop();
			stopSelf();
		} else if (intent.getAction().equals(ACTION_START)) {
			start();
		} else if (intent.getAction().equals(ACTION_ERROR)) {
			error();
		} else if (intent.getAction().equals(ACTION_RESTART)) {
			restartIfNecessary();
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Log.i(TAG, "Service destroyed (started=" + mStarted + ")");

		if (mStarted)
			stop();
	}

	
	protected boolean wasStarted()
	{
		return mPrefs.getBoolean(mPrefStartedValue, false);
	}

	protected void setStarted(boolean started)
	{
		mPrefs.edit().putBoolean(mPrefStartedValue, started).apply();
		mStarted = started;
	}
	
	/**
	 * network connectivity change dection
	 */
	protected BroadcastReceiver mConnectivityChanged = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			NetworkInfo info = mConnMan.getActiveNetworkInfo();
			
			boolean hasConnectivity = (info != null && info.isConnected());

			Log.i(TAG, "Connecting changed: connected=" + hasConnectivity);

			onConnectivityChanged(hasConnectivity);
		}
	};
	
	protected void onConnectivityChanged(boolean hasConnectivity) {
		if (hasConnectivity)
			restartIfNecessary();
	}


	protected synchronized void start()
	{
		if (mStarted)
		{
			Log.w(TAG, "Attempt to start connection that is already active");
			return;
		}

		setStarted(true);

		registerReceiver(mConnectivityChanged,
		  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		onStart();

	}

	abstract protected void onStart();

	protected synchronized void stop()
	{
		if (!mStarted)
		{
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}

		setStarted(false);

		unregisterReceiver(mConnectivityChanged);

		mPrefs.edit().putLong("retryInterval", INITIAL_RETRY_INTERVAL).apply();
		mPrefs.edit().putLong("startTime", 0).apply();

		onStop();
	}

	abstract protected void onStop();
	
	protected synchronized void error() {
		onError();
	}
	abstract protected void onError();
	

	protected void scheduleRestart(Class<?> clazz)
	{
		long interval =
		  mPrefs.getLong("retryInterval", INITIAL_RETRY_INTERVAL);
		long startTime = mPrefs.getLong("startTime", 0);

		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		if (elapsed < interval)
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		else
			interval = INITIAL_RETRY_INTERVAL;

		if(startTime  == 0){
			mPrefs.edit().putLong("startTime", System.currentTimeMillis()).apply();
		}


		Log.i(TAG, "Rescheduling connection in " + interval + "ms.");

		mPrefs.edit().putLong("retryInterval", interval).apply();

		Intent i = new Intent();
		i.setClass(this, clazz);
		i.setAction(ACTION_RESTART);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}
	
 	protected void cancelRestart(Class<?> clazz)
	{
		Intent i = new Intent();
		i.setClass(this, clazz);
		i.setAction(ACTION_RESTART);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	/**
	 * implement yourself's restart strategy if needed
	 */
	abstract protected void restartIfNecessary();
	

	protected boolean isNetworkAvailable()
	{
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null)
			return false;

		return info.isConnected();
	}
}
