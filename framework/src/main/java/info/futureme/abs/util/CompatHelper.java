package info.futureme.abs.util;

import android.os.Build;
import android.util.SparseBooleanArray;

import java.util.regex.Pattern;

import info.futureme.abs.conf.FConstants;

public class CompatHelper {
    // SDK

    public static boolean sdk(int min) {
        return FConstants.SDK >= min;
    }

    public static boolean sdk(int min, int max) {
        return MathHelper.inRange(FConstants.SDK, min, max);
    }

    public static boolean sdks(int... targets) {
        int sdk = FConstants.SDK;
        for (int target : targets) {
            if (sdk == target) {
                return true;
            }
        }
        return false;
    }

    // Device Name

    private static SparseBooleanArray sDeviceResultCache;

    public static boolean device(String regex) {
        if (sDeviceResultCache == null) {
            sDeviceResultCache = new SparseBooleanArray();
        }

        int hashCode = regex.hashCode();
        int index = sDeviceResultCache.indexOfKey(hashCode);
        boolean result;
        if (index < 0) {
            result = Pattern.matches(regex, SysInfoHelper.device());
            sDeviceResultCache.put(hashCode, result);
        } else {
            result = sDeviceResultCache.valueAt(index);
        }
        return result;
    }

    // CPU Arch

    private static SparseBooleanArray sCPUResultCache;

    public static boolean cpu(String regex) {
        if (sCPUResultCache == null) {
            sCPUResultCache = new SparseBooleanArray();
        }

        int hashCode = regex.hashCode();
        int index = sCPUResultCache.indexOfKey(hashCode);
        boolean result;
        if (index < 0) {
            result = Pattern.matches(regex, SysInfoHelper.cpuArch());
            sCPUResultCache.put(hashCode, result);
        } else {
            result = sCPUResultCache.valueAt(index);
        }
        return result;
    }

    // Ram Size

    public static boolean ram(int min, int max) {
        return MathHelper.inRange(SysInfoHelper.ramSize(), min, max);
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;

    }

    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasKitKatWatch() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
    }

    public static boolean hasL(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH;
    }

}
