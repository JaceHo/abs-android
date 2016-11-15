/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android framework
 * Create Time: 16-2-16 下午6:52
 */

package info.futureme.abs.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import info.futureme.abs.biz.ContextManager;

public class AppHelper {
    // Info

    private static String mPackageName;

    public static String packageName() {
        if (mPackageName == null) {
            mPackageName = ContextManager.appContext().getPackageName();
        }
        return mPackageName;
    }

    private static String mVersionName;

    public static String versionName() {
        if (mVersionName == null) {
            try {
                mVersionName = ContextManager.packageManager().getPackageInfo(packageName(), 0).versionName;
            } catch (NameNotFoundException e) {
            }
            mVersionName = TextHelper.ensureNotNull(mVersionName);
        }
        return mVersionName;
    }

    public static final int INVALID_VERSION_CODE = -1;
    private static int mVersionCode = INVALID_VERSION_CODE;

    public static int versionCode() {
        if (mVersionCode == INVALID_VERSION_CODE) {
            try {
                mVersionCode = ContextManager.packageManager().getPackageInfo(packageName(), 0).versionCode;
            } catch (Exception e) {
            }
        }
        return mVersionCode;
    }

    public static String processName() {
        return ContextManager.appInfo().processName;
    }

    public static int pid() {
        return android.os.Process.myPid();
    }

    private static String byteToHexStr(byte[] input) {
        if (input == null) {
            return "";
        }
        String output = "";
        String tmp = "";
        for (int n = 0; n < input.length; n++) {
            tmp = Integer.toHexString(input[n] & 0xFF);
            if (tmp.length() == 1) {
                output = output + "0" + tmp;
            } else {
                output = output + tmp;
            }
        }
        return output.toUpperCase(Locale.ENGLISH);
    }

    public static String signature() {
        String ret = "";
        try {
            Signature[] sigs = ContextManager.packageManager().getPackageInfo(packageName(),//
                    PackageManager.GET_SIGNATURES).signatures;

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            X509Certificate X509Cert = (X509Certificate) certificateFactory.generateCertificate(//
                    new ByteArrayInputStream(sigs[0].toByteArray()));

            ret = byteToHexStr(md5.digest(X509Cert.getSignature()));
        } catch (NameNotFoundException e) {
            Assert.d(e);
        } catch (NoSuchAlgorithmException e) {
            Assert.d(e);
        } catch (CertificateException e) {
            Assert.d(e);
        }

        return ret;
    }

    public static String uid(){
        final TelephonyManager tm = (TelephonyManager) ContextManager.context().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" ;//+ tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(ContextManager.context().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return Base64.encodeToString(result, Base64.DEFAULT);
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
        if (android.os.Build.VERSION.SDK_INT >=  17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        kgen.init(128, sr); //256 bits or 128 bits,192bits
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

}
