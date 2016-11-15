package info.futureme.abs.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import info.futureme.abs.biz.ContextManager;

public class WindowUtils {
    public static int getWindowWidth() {
        WindowManager windowManager = (WindowManager) ContextManager.context()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getNavigationBarHeight(Activity context){
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        int naviHeight = 0;
        if(!hasMenuKey && !hasBackKey) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                naviHeight = resources.getDimensionPixelSize(resourceId);
            }
        }

        return naviHeight;
    }


    public static int dp2px(float dp) {
        return (int) (dp * ContextManager.context().getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int getWindowHeight() {
        WindowManager windowManager = (WindowManager) ContextManager.context()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }



    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        int statusHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static int getSuitSize(int oldWindowWidth, int nowWindowWidth, int suitSize) {
        return (int) ((nowWindowWidth * 1.0 / oldWindowWidth) * suitSize);
    }
}
