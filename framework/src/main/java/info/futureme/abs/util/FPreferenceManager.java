package info.futureme.abs.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.conf.Theme;


public class FPreferenceManager {


    public static final String PREF_COOKIES = "pref_cookies_set";

    private static SharedPreferences getSharedPreferences(final Context context) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static boolean isFirstTime(String key) {
        if (getBoolean(key, false)) {
            return false;
        } else {
            putBoolean(key, true);
            return true;
        }
    }


    public static boolean contains(String key) {
        return FPreferenceManager.getSharedPreferences(ContextManager.context()).contains(key);
    }

    public static int getInt(final String key, final int defaultValue) {
        SharedPreferences share = FPreferenceManager.getSharedPreferences(ContextManager.context());
        int value = share.getInt(key, defaultValue);
        return value;
    }

    public static boolean putInt(final String key, final int pValue) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putInt(key, pValue);

        return editor.commit();
    }


    public static boolean putIntArray(final String key, final int[] pValues) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        StringBuilder res = new StringBuilder();
        for(int i : pValues){
            res.append(i);
            res.append("$");
        }
        editor.putString(key, res.substring(0, res.length() - 2));

        return editor.commit();
    }

    public static int[] getIntArray(final String key) {
        String res = FPreferenceManager.getSharedPreferences(ContextManager.context()).getString(key, "");

        String[] ss = res.split("$");
        int[] r = new int[ss.length];
        int i=0;
        for(String s : ss){
            r[i++]=Integer.parseInt(s);
        }
        return r;
    }

    public static long getLong(final String key, final long defaultValue) {
        return FPreferenceManager.getSharedPreferences(ContextManager.context()).getLong(key, defaultValue);
    }

    public static Long getLong(final String key, final Long defaultValue) {
        if (FPreferenceManager.getSharedPreferences(ContextManager.context()).contains(key)) {
            return FPreferenceManager.getSharedPreferences(ContextManager.context()).getLong(key, 0);
        } else {
            return null;
        }
    }


    public static boolean putLong(final String key, final long pValue) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putLong(key, pValue);
        return editor.commit();
    }

    public static boolean getBoolean(final String key, final boolean defaultValue) {
        return FPreferenceManager.getSharedPreferences(ContextManager.context()).getBoolean(key, defaultValue);
    }

    public static boolean putBoolean(final String key, final boolean pValue) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putBoolean(key, pValue);

        return editor.commit();
    }

    public static String getString(final String key, final String defaultValue) {
        return FPreferenceManager.getSharedPreferences(ContextManager.context()).getString(key, defaultValue);
    }

    public static boolean putString(final String key, final String pValue) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putString(key, pValue);

        return editor.commit();
    }


    public static boolean remove(final String key) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.remove(key);

        return editor.commit();
    }

    public static Theme getCurrentTheme() {
        return Theme.valueOf(FPreferenceManager.getString("app_theme", Theme.Blue.name()));
    }

    public static void setCurrentTheme(Theme currentTheme) {
        FPreferenceManager.putString("app_theme", currentTheme.name());
    }

    public static Set<String> getStringSet(String prefCookies, HashSet<String> prefs) {
        final SharedPreferences preferences = FPreferenceManager.getSharedPreferences(ContextManager.context());
        return preferences.getStringSet(prefCookies, new HashSet<String>());
    }

    public static void putStringSet(String prefCookies, HashSet<String> cookies) {
        final SharedPreferences.Editor editor = FPreferenceManager.getSharedPreferences(ContextManager.context()).edit();
        editor.putStringSet(prefCookies, cookies);
        editor.commit();
    }
}
