package info.futureme.abs.example.util;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import info.futureme.abs.util.DLog;


public class DES {

    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
    private static String CLIPHER_KEY = "r23oisdj";

    /**
     * DES加密
     *
     * @param encryptString为原文
     * @param encryptKey为密钥
     * @return 返回加密后的密文
     * @throws Exception
     */
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        encryptKey = (encryptKey + CLIPHER_KEY).substring(0, 8);
        //实例化lvParameterSpec对象，使用指定的初始化向量
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        //实例化SecretKeySpec类，根据字节数组来构造SecretKey
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        //创建密码器
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        //用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        //执行加密操作
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        //返回加密后的数据
        DLog.w("encode:", Base64.encodeToString(encryptedData, 0));
        return Base64.encodeToString(encryptedData, 0);
    }

    /**
     * DES解密
     *
     * @param decryptString为密文
     * @param decryptKey为密钥
     * @return 返回解密后的原文
     * @throws Exception
     */
    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        decryptKey = (decryptKey + CLIPHER_KEY).substring(0, 8);
        //先使用Base64解密
        byte[] byteMi = Base64.decode(decryptString, 0);
        //实例化lvParameterSpec对象，使用指定的初始化向量
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        //实例化SecretKeySpec类，根据字节数组来构造SecretKey
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        //创建密码器
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        //用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        //获取解密的数据
        byte decryptedData[] = cipher.doFinal(byteMi);
        //解密数据转换为字符串输出
        return new String(decryptedData);
    }
}
