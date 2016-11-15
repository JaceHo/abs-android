package info.futureme.abs.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import info.futureme.abs.biz.ContextManager;


public class SysInfoHelper {
    private static final String UNKNOWN = "unknown";

    /**
     * A 64-bit number (as a hex string) that is randomly generated on the
     * device's first boot and should remain constant for the lifetime of the
     * device. (The value may change if a factory reset is performed on the
     * device.)
     */
    public static String androidId() {
        return Secure.getString(ContextManager.contentResolver(), Secure.ANDROID_ID);
    }

    public static String imei() {
        String imei = null;
        try {
            imei = ((TelephonyManager) ContextManager.systemService(Context.TELEPHONY_SERVICE))//
                    .getDeviceId();
        } catch (Exception e) {
        }
        return TextHelper.ensureNotNull(imei);
    }

    public static String imsi() {
        String imsi = null;
        try {
            imsi = ((TelephonyManager) ContextManager.systemService(Context.TELEPHONY_SERVICE))//
                    .getSubscriberId();
        } catch (Exception e) {
        }
        return TextHelper.ensureNotNull(imsi);
    }

    public static String wifiMac() {
        String mac = null;
        try {
            mac = ((WifiManager) ContextManager.systemService(Context.WIFI_SERVICE))//
                    .getConnectionInfo().getMacAddress();
        } catch (Exception e) {
        }

        return TextHelper.ensureNotNull(mac);
    }

    private static String mCPUArch;

    public static String cpuArch() {
        if (mCPUArch == null) {
            /**
             * Email from yuming.li@intel.com in 2014/06/27 said their new x86
             * ROM modifies the android.os.abi to make the Build.CPU_ABI to
             * always return "armeabi-v7a" and recommended following method to
             * get real CPU arch.
             */
            BufferedReader ibr = null;
            try {
                Process process = Runtime.getRuntime().exec("getprop ro.product.cpu.abi");
                ibr = new BufferedReader(new InputStreamReader(process.getInputStream()));
                mCPUArch = ibr.readLine();
            } catch (IOException e) {
            } finally {
                if (ibr != null) {
                    try {
                        ibr.close();
                    } catch (IOException e) {
                    }
                }
            }

            if (TextUtils.isEmpty(mCPUArch)) {
                // if meet something wrong, get cpu arch from android sdk.
                mCPUArch = Build.CPU_ABI;
            }
        }

        return mCPUArch;
    }

    private static int mRamSize;

    public static int ramSize() {
        if (mRamSize == 0) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("/proc/meminfo"), 1024);
                String line = br.readLine();

                /*
                 * # cat /proc/meminfo MemTotal: 94096 kB MemFree: 1684 kB
                 */
                if (!TextUtils.isEmpty(line)) {
                    String[] splits = line.split("\\s+");
                    if (splits.length > 1) {
                        mRamSize = Integer.valueOf(splits[1]).intValue();
                    }
                }
            } catch (IOException e) {
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        return mRamSize;
    }

    /**
     * The android version which is displayed in system Settings/About
     * phone/Android version
     */
    public static String osVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * The serial number which is displayed in system Settings/About
     * phone/Status/Serial number
     */
    public static String serial() {
        if (CompatHelper.sdk(9)) {
            return Build.SERIAL;
        } else {
            return "";
        }
    }

    /**
     * The OS build number which is displayed in system Settings/About
     * phone/Build number
     */
    public static String buildNumber() {
        return Build.DISPLAY;
    }

    /**
     * The baseband version which is displayed in system Settings/About
     * phone/Baseband version
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static String baseband() {
        if (CompatHelper.sdk(14)) {
            try {
                return Build.getRadioVersion();
            } catch (Throwable e) {
                Assert.d(e);
            }
        }
        return UNKNOWN;
    }

    /**
     * The end-user-visible name for the end product.
     */
    public static String device() {
        return Build.MODEL;
    }

    /**
     * The manufacturer (e.g.,Samsung/Huawei...) of the product/hardware
     */
    public static String manufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * The brand (e.g., Google) the software is customized for, if any.
     */
    public static String brand() {
        return Build.BRAND;
    }
}
