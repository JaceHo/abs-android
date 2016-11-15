package info.futureme.abs.example.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.util.AppHelper;


/*
Prefutils use another preference beside main preference,
and it's content not synced to the server
 */
public class PrefExtraUtils {
    public static SharedPreferences pref() {
        String name = AppHelper.packageName() + "_extra_preferences";
        return pref(name);
    }

    public static SharedPreferences pref(String name) {
        int mode = Context.MODE_PRIVATE;
        if (MVSConstants.SDK >= 11) {
            // After sdk 11, this flag must be set to make share preference consistent in multi-process use.
            // One example is using share preference in BroadcastReceiver. BroadcastReceiver.onEvent mostly called
            // in other process.
            mode |= Context.MODE_MULTI_PROCESS;
        }

        return ContextManager.appContext().getSharedPreferences(name, mode);
    }

    public static void put(String prefKey, int val) {
        pref().edit().putInt(prefKey, val).commit();
    }

    public static void put(String name, String prefKey, int val) {
        pref(name).edit().putInt(prefKey, val).commit();
    }

    public static void put(String prefKey, long val) {
        pref().edit().putLong(prefKey, val).commit();
    }

    public static void put(String name, String prefKey, long val) {
        pref(name).edit().putLong(prefKey, val).commit();
    }

    public static void put(String prefKey, boolean val) {
        pref().edit().putBoolean(prefKey, val).commit();
    }

    public static void put(String name, String prefKey, boolean val) {
        pref(name).edit().putBoolean(prefKey, val).commit();
    }

    public static void put(String prefKey, String val) {
        pref().edit().putString(prefKey, val).commit();
    }

    public static void put(String name, String prefKey, String val) {
        pref(name).edit().putString(prefKey, val).commit();
    }

    public static int getInt(String prefKey, int defVal) {
        return pref().getInt(prefKey, defVal);
    }

    public static int getInt(String name, String prefKey, int defVal) {
        return pref(name).getInt(prefKey, defVal);
    }

    public static long getLong(String prefKey, long defVal) {
        return pref().getLong(prefKey, defVal);
    }

    public static long getLong(String name, String prefKey, long defVal) {
        return pref(name).getLong(prefKey, defVal);
    }

    public static boolean getBoolean(String prefKey, boolean defVal) {
        return pref().getBoolean(prefKey, defVal);
    }

    public static boolean getBoolean(String name, String prefKey, boolean defVal) {
        return pref(name).getBoolean(prefKey, defVal);
    }

    public static String getString(String prefKey, String defVal) {
        return pref().getString(prefKey, defVal);
    }

    public static String getString(String name, String prefKey, String defVal) {
        return pref(name).getString(prefKey, defVal);
    }

    public static void increaseIfExist(String prefKey) {
        int val = getInt(prefKey, -1);
        if (val != -1) {
            put(prefKey, val + 1);
        }
    }

    public static void increaseIfExist(String name, String prefKey) {
        int val = getInt(name, prefKey, -1);
        if (val != -1) {
            put(name, prefKey, val + 1);
        }
    }

    public static void increase(String prefKey) {
        put(prefKey, getInt(prefKey, 0) + 1);
    }

    public static void increase(String name, String prefKey) {
        put(name, prefKey, getInt(name, prefKey, 0) + 1);
    }

    public static boolean contains(String name, String prefKey) {
        return pref(name).contains(prefKey);
    }


    public static void storeList(String key, List countries) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = pref();
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(countries);
        editor.putString(key, jsonFavorites);
        editor.apply();
    }

    public static ArrayList<String> loadList(String key) {

        SharedPreferences settings = pref();
        List favorites;
        if (settings.contains(key)) {
            String jsonFavorites = settings.getString(key, null);
            Gson gson = new Gson();
            String[] favoriteItems = gson.fromJson(jsonFavorites, String[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList(favorites);
        } else
            return null;
        return (ArrayList) favorites;
    }

    public static void addList(String key,String value) {
        List favorites = loadList(key);
        deleteList(key);
        if (favorites == null)
            favorites = new ArrayList();

        if(favorites.contains(value)){
            favorites.remove(value);
        }

        favorites.add(0, value);

//        if(favorites.size() > 3) {
//            favorites.remove(favorites.size() - 1);
//        }

        storeList(key, favorites);
    }

    public static void deleteList(String key){

        SharedPreferences myPrefs = pref();
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void empty() {
        SharedPreferences myPrefs = pref();
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.apply();
    }
}
