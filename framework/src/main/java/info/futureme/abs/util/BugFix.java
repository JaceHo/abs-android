package info.futureme.abs.util;

import android.view.View;

import com.tencent.smtt.sdk.WebView;

/**
 * Created by Jeffrey on 2016/3/21.
 */
public class BugFix {
    private static String[] misbehavingClasses = new String[]{
            "com.google.android.gms.ads",
            "com.android.org.chromium.android_webview.AwContents$AwComponentCallbacks",
    };

    public static boolean isMisbehavingCallBacks(String name){
        for(String s : misbehavingClasses){
            if(name.startsWith(s)){
                return true;
            }
        }
        return false;
    }

    public static void destroyAttach(WebView webView){
//        android.webkit.WebView target =
//                ReflectHelper.getFieldValue(webView, "h");
//        DLog.i("destroy", target + "");
//        if(target != null){
//            ReflectHelper.setField(target, "mAttachInfo", null);
//        }
    }

    public static void destoryCallback(View v){


        // Fixes android memory  issue 8488 :
        // http://code.google.com/p/android/issues/detail?id=8488
        if(v != null)
            ViewHelper.nullViewDrawablesRecursive(v);

        System.gc();
        Runtime.getRuntime().gc();
    }
}
