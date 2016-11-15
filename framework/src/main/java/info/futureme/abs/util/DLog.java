package info.futureme.abs.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import info.futureme.abs.FApplication;
import info.futureme.abs.biz.ContextManager;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * **use this for debug, log, assertion purpose
 * logging info, assertion and etc shown in debug mode(FApplication.DEBUG = true).
 * assertion use custom assert which can throw exception in runtime
 */

public class DLog {
    public static void i(String tag, String msg, Object... args) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" +format("" + msg, args));
            Log.i(tag, format("" + msg, args));
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" +format("" + msg, args));
            Log.d(tag, format("" + msg, args));
        }
    }

    public static void w(String tag, String msg, Object... args) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" +format("" + msg, args));
            Log.w(tag, format("" + msg, args));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" +msg);
            Log.e(tag, "" + msg, tr);
        }
    }

    public static void e(String tag, String msg, Object... args) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" +format("" + msg, args));
            Log.e(tag, format("" + msg, args));
        }
    }

    public static void v(String tag, String msg, Object... args) {
        if (FApplication.DEBUG) {
            writeToFile(tag + ":" + format("" + msg, args));
            Log.v(tag, format("" + msg, args));
        }
    }

    public static void p(String e) {
        if (FApplication.DEBUG) {
            writeToFile(e);
            if(e != null) {
                System.out.println(e);
            }
        }
    }

    public static void p(Throwable e) {
        if (FApplication.DEBUG) {
            writeToFile(e);
            if(e != null) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToFile(Throwable throwable){
        if(FApplication.DEBUG && throwable != null)
            writeToFile(throwable.getMessage());
    }

    public static void writeToFile(final String message){
        if(FApplication.DEBUG && ContextManager.getDataDir() != null)
            Observable.just(true).subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            FileHelper.append(ContextManager.getDataDir() + "/" + AppHelper.packageName() + ".txt", "\n\n" + new Date().toLocaleString() + "----\n" + (message == null ? "null\n" : message + "\n"));
                        }
                    });
    }

    /**
     * debug assert condition, throw exception if false
     */
    public static void a(boolean condition){
        Assert.d(condition);
    }

    public static void a(Throwable throwable){
        Assert.d(throwable);
    }

    /**
     * debug assert condition, throw exception if fase
     */
    public static void a(boolean condition, String tip){
        Assert.d(condition, tip);
    }

    /**
     * debug assert condition, throw exception if fase
     */
    public static void a(boolean condition, String tip, Object ... objects){
        Assert.d(condition, format(tip, objects));
    }

    private static String format(String msg, Object[] args) {
        if (args.length > 0) {
            msg = String.format("" + msg, args);
        }
        return msg;
    }


    public static void toast(final String msg) {
        writeToFile(msg);
        if (FApplication.DEBUG) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ContextManager.appContext(), "" + msg, Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}

