package info.futureme.abs.example.util;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.Theme;


public class PreferenceManager {


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
        return PreferenceManager.getSharedPreferences(ContextManager.context()).contains(key);
    }

    public static int getInt(final String key, final int defaultValue) {
        SharedPreferences share = PreferenceManager.getSharedPreferences(ContextManager.context());
        int value = share.getInt(key, defaultValue);
        return value;
    }

    public static boolean putInt(final String key, final int pValue) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putInt(key, pValue);

        return editor.commit();
    }


    public static boolean putIntArray(final String key, final int[] pValues) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        StringBuilder res = new StringBuilder();
        for(int i : pValues){
            res.append(i);
            res.append("$");
        }
        editor.putString(key, res.substring(0, res.length() - 2));

        return editor.commit();
    }

    public static int[] getIntArray(final String key) {
        String res = PreferenceManager.getSharedPreferences(ContextManager.context()).getString(key, "");

        String[] ss = res.split("$");
        int[] r = new int[ss.length];
        int i=0;
        for(String s : ss){
            r[i++]=Integer.parseInt(s);
        }
        return r;
    }

    public static long getLong(final String key, final long defaultValue) {
        return PreferenceManager.getSharedPreferences(ContextManager.context()).getLong(key, defaultValue);
    }

    public static Long getLong(final String key, final Long defaultValue) {
        if (PreferenceManager.getSharedPreferences(ContextManager.context()).contains(key)) {
            return PreferenceManager.getSharedPreferences(ContextManager.context()).getLong(key, 0);
        } else {
            return null;
        }
    }


    public static boolean putLong(final String key, final long pValue) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putLong(key, pValue);
        return editor.commit();
    }

    public static boolean getBoolean(final String key, final boolean defaultValue) {
        return PreferenceManager.getSharedPreferences(ContextManager.context()).getBoolean(key, defaultValue);
    }

    public static boolean putBoolean(final String key, final boolean pValue) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putBoolean(key, pValue);

        return editor.commit();
    }

    public static String getString(final String key, final String defaultValue) {
        return PreferenceManager.getSharedPreferences(ContextManager.context()).getString(key, defaultValue);
    }

    public static boolean putString(final String key, final String pValue) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.putString(key, pValue);

        return editor.commit();
    }


    public static boolean remove(final String key) {
        final SharedPreferences.Editor editor = PreferenceManager.getSharedPreferences(ContextManager.context()).edit();

        editor.remove(key);

        return editor.commit();
    }

    public static Theme getCurrentTheme() {
        return Theme.valueOf(PreferenceManager.getString("app_theme", Theme.Blue.name()));
    }

    public static void setCurrentTheme(Theme currentTheme) {
        PreferenceManager.putString("app_theme", currentTheme.name());
    }


    public static JSONObject exportJson(){
        try {
            JSONObject obj = new JSONObject();
            obj.put(AccountManagerImpl.KEY_FASTBILLING, PreferenceManager.getBoolean(AccountManagerImpl.KEY_FASTBILLING, false));
            obj.put(AccountManagerImpl.KEY_HANDYHAVERSACK, PreferenceManager.getBoolean(AccountManagerImpl.KEY_HANDYHAVERSACK, false));
            obj.put(AccountManagerImpl.KEY_INITIATECHAT, PreferenceManager.getBoolean(AccountManagerImpl.KEY_INITIATECHAT, false));
            obj.put(AccountManagerImpl.KEY_MYMESSAGE, PreferenceManager.getBoolean(AccountManagerImpl.KEY_MYMESSAGE, false));
            obj.put(AccountManagerImpl.KEY_PRODUCTDESCRIPTION, PreferenceManager.getBoolean(AccountManagerImpl.KEY_PRODUCTDESCRIPTION, false));
            obj.put(AccountManagerImpl.KEY_SCHEME_QUERY, PreferenceManager.getBoolean(AccountManagerImpl.KEY_SCHEME_QUERY, false));

            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean importJson(String configJson) {
        try {
            JSONObject obj = new JSONObject(configJson);
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_FASTBILLING, obj.getBoolean(AccountManagerImpl.KEY_FASTBILLING));
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_HANDYHAVERSACK, obj.getBoolean(AccountManagerImpl.KEY_HANDYHAVERSACK));
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_INITIATECHAT, obj.getBoolean(AccountManagerImpl.KEY_INITIATECHAT));
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_MYMESSAGE, obj.getBoolean(AccountManagerImpl.KEY_MYMESSAGE));
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_PRODUCTDESCRIPTION, obj.getBoolean(AccountManagerImpl.KEY_PRODUCTDESCRIPTION));
            PreferenceManager.putBoolean(AccountManagerImpl.KEY_SCHEME_QUERY, obj.getBoolean(AccountManagerImpl.KEY_SCHEME_QUERY));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
