package info.futureme.abs.example;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

import com.baidu.mapapi.SDKInitializer;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.igexin.sdk.PushManager;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import info.futureme.abs.FApplication;
import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.biz.FDevOpenHelper;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.g.DaoMaster;
import info.futureme.abs.example.entity.g.DaoSession;
import info.futureme.abs.example.service.TickNetworkReceiver;
import info.futureme.abs.example.ui.MainActivity;
import info.futureme.abs.example.util.CommonUtil;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.NetworkUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Customized application instance with crossing app lifecycle components like
 * rxbus,refwatcher, greendaosession and so on.
 *
 * @author Jeffrey
 * @version 1.0
 * @updated 17-一月-2016 14:05:24
 */
public class ABSApplication extends FApplication {

    /**
     * greendao master to create session used for this app
     */
    private static DaoMaster mDaoMaster;
    //greendao session used by this app
    private static DaoSession mDaoSession;
    /**
     * datadir for file saving
     */
    private static String dataDir;

    /**
     * cachedir in the datadir
     */
    private static File cacheDir;

    //do not crash here!!!
    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
            //TODO upload crash log
            DLog.p(paramThrowable);
            FBaseActivity.finishAll();
            Intent intent = new Intent(ContextManager.context(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextManager.appContext().startActivity(intent);
            System.exit(0);
        }
    };

    @Override
    public void onCreate() {
        FApplication.DEBUG = BuildConfig.DEBUG;
        super.onCreate();
        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }else {
            //打点
            MobclickAgent.setDebugMode(false);
            MobclickAgent.openActivityDurationTrack(true);
            MobclickAgent.updateOnlineConfig(this);
            MobclickAgent.setCatchUncaughtExceptions(true);
        }

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (handler == null || handler != ABSApplication.this.handler)
            Thread.setDefaultUncaughtExceptionHandler(handler);


        //初始化SDK
        Observable.create(
                new Observable.OnSubscribe<Class>() {
                    @Override
                    public void call(Subscriber<? super Class> subscriber) {
                        Class[] classes = new Class[]{
                                PushManager.class,
                                SDKInitializer.class
                        };
                        for(Class c : classes)
                            subscriber.onNext(c);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        dataDir = CommonUtil.getOwnedDataDir(ABSApplication.this);
                        ContextManager.initDataDir(dataDir);
                        cacheDir = new File(dataDir + "/cache/");
                        BridgeWebView.CACHE_DIR = cacheDir.getAbsolutePath();
                        FileHelper.ensureDir(cacheDir.getPath());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Func1<Class, Object>() {
                    @Override
                    public Object call(Class aClass) {
                        DLog.w(ABSApplication.class
                                .getName(), aClass.getName() + " initializing");
                        if (PushManager.class.getName().equals(aClass.getName())) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    String cid = FPreferenceManager.getString(MVSConstants.KEY_GETUI_CID, "");
                                    if("".equals(cid)) {
                                        if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                                            PushManager.getInstance().initialize(ABSApplication.this);
                                        } else {
                                            IntentFilter intentFilter = new IntentFilter();
                                            //addAction
                                            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                                            ContextManager.appContext().registerReceiver(new TickNetworkReceiver(), intentFilter);
                                        }
                                    }
                                }
                            });
                        } else if (SDKInitializer.class.getName().equals(aClass.getName())) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    SDKInitializer.initialize(getApplicationContext());
                                }
                            });
                        }
                        return aClass;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        DLog.w(ABSApplication.class
                                .getName(), o.toString() + " init finished");
                    }
                });

        //qa or debug have no https cert, use system webview
        if(!BuildConfig.CONFIG_ENDPOINT.contains("youfu365")){
            Observable.just(true).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                            QbSdk.forceSysWebView();
                    }
                });
        }
        trustEveryone();
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }


    public synchronized static DaoMaster getDaoMaster() {
        if (mDaoMaster == null) {
            //create database
            DaoMaster.OpenHelper helper = new FDevOpenHelper(ContextManager.context(), MVSConstants.DataConstants.DB_NAME, null);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    public synchronized static DaoSession getDaoSession() {
        if (mDaoSession == null) {
            mDaoSession = getDaoMaster().newSession();
        }
        return mDaoSession;
    }

    public static String getAppDataDir() {
        return dataDir;
    }

    public static File getCachedDir() {
        return cacheDir;
    }

    private static void enableStrictMode() {
        StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog();
        StrictMode.VmPolicy.Builder vmPolicyBuilder =
                new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog();

        threadPolicyBuilder.penaltyFlashScreen();
        vmPolicyBuilder.setClassInstanceLimit(MainActivity.class, 1);
        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

