package info.futureme.abs;

import android.app.Activity;
import android.app.Application;
import android.app.ApplicationErrorReport;
import android.content.ComponentCallbacks;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.Map;
import java.util.WeakHashMap;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.util.AppHelper;
import info.futureme.abs.util.BugFix;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.MemoryLeakUtils;
import info.futureme.abs.util.rx.RxBus;

/**
 * Customized framework application instance with crossing app lifecycle
 * components like rxbus,refwathcher and so on.
 * @author Jeffrey
 * @version 1.0
 * @updated 25-一月-2016 16:21:20
 */
public class FApplication extends Application {
    //used for Dlog to determine whecher DEBUG switch should be turned on
    public static boolean DEBUG = false;

    private static boolean activityVisible;

    /**
	 * refWatcher used for memory leak detection, More information should reference: <a
	 * href="$inet://https://github.com/square/leakcanary"><font
	 * color="#0000ff"><u>https://github.com/square/leakcanary</u></font></a>
	 */
    private static RefWatcher _refWatcher;

    /**
     * rxjava used for async network query, io scheduling,  event dispatching(rxbus is
     * a simple implementation)
     */
    private static RxBus _rxBus;

    @Override
    public void onCreate() {
        ContextManager.init(this);
        super.onCreate();
        //init contextmanager for later usage
        _refWatcher = LeakCanary.install(this);
//        _refWatcher = RefWatcher.DISABLED;

        registerActivityLifecycleCallbacks(new MemoryLeakUtils.LifecycleAdapter() {
            @Override
            public void onActivityDestroyed(Activity activity) {
                DLog.d("activity", "Cleaning up after the Android framework");
//                MemoryLeakUtils.clearNextServedView(FApplication.this);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                activityVisible = true;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                activityVisible = false;
            }
        });

        DLog.w("app:", "application started, process:" + AppHelper.processName() + ":" + android.os.Process.myPid());
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static RefWatcher getRefWatcher() {
        return _refWatcher;
    }

    /*
        rxbus is like eventbus and otto, thread safe implementation
     */
	public static RxBus getRxBus(){
        if(_rxBus == null) {
            _rxBus = new RxBus();
        }
		return _rxBus;
	}

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
        ComponentCallbacksBehavioralAdjustmentToolIcs.INSTANCE.onComponentCallbacksRegistered(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        ComponentCallbacksBehavioralAdjustmentToolIcs.INSTANCE.onComponentCallbacksUnregistered(callback);
        super.unregisterComponentCallbacks(callback);
    }

    public void forceUnregisterComponentCallbacks() {
        ComponentCallbacksBehavioralAdjustmentToolIcs.INSTANCE.unregisterAll(this);
    }


    private static class ComponentCallbacksBehavioralAdjustmentToolIcs {
        private static final String TAG = "componentCallbacks";
        static ComponentCallbacksBehavioralAdjustmentToolIcs INSTANCE = new ComponentCallbacksBehavioralAdjustmentToolIcs();

        private WeakHashMap<ComponentCallbacks, ApplicationErrorReport.CrashInfo> mCallbacks = new WeakHashMap<>();
        private boolean mSuspended = false;

        public void onComponentCallbacksRegistered(ComponentCallbacks callback) {
            Throwable thr = new Throwable("Callback registered here.");
            ApplicationErrorReport.CrashInfo ci = new ApplicationErrorReport.CrashInfo(thr);

            if (FApplication.DEBUG) DLog.w(TAG, "registerComponentCallbacks: " + callback.getClass().getName(), thr);

            if (!mSuspended) {
                if (BugFix.isMisbehavingCallBacks(callback.getClass().getName())) {
                    mCallbacks.put(callback, ci);
                }
                // TODO: other classes may still prove to be problematic?  For now, only watch for .gms.ads, since we know those are misbehaving
            } else {
                if (FApplication.DEBUG) DLog.e(TAG, "ComponentCallbacks was registered while tracking is suspended!");
            }
        }

        public void onComponentCallbacksUnregistered(ComponentCallbacks callback) {
            if (!mSuspended) {
                if (FApplication.DEBUG) {
                    DLog.i(TAG, "unregisterComponentCallbacks: " + callback, new Throwable());
                }

                mCallbacks.remove(callback);
            }
        }

        public void unregisterAll(Context context) {
            mSuspended = true;
            for (Map.Entry<ComponentCallbacks, ApplicationErrorReport.CrashInfo> entry : mCallbacks.entrySet()) {
                ComponentCallbacks callback = entry.getKey();
                if (callback == null) continue;

                if (FApplication.DEBUG) {
                    DLog.w(TAG, "Forcibly unregistering a misbehaving ComponentCallbacks: " + entry.getKey());
                    DLog.w(TAG, entry.getValue().stackTrace);
                }

                try {
                    context.unregisterComponentCallbacks(entry.getKey());
                } catch (Exception exc) {
                    if (FApplication.DEBUG) DLog.e(TAG, "Unable to unregister ComponentCallbacks", exc);
                }
            }

            mCallbacks.clear();
            mSuspended = false;
        }
    }

}
