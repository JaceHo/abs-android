/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:46
 */
package info.futureme.abs.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Helper class to do operations on regular files/directories.
 */
public class FileSizeHelper {

  public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
  public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
  public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
  public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

  public FileSizeHelper() {}

  /**
   * Writes a file to Disk.
   * This is an I/O operation and this method executes in the getui_main thread, so it is recommended to
   * perform this operation using another thread.
   *
   * @param file The file to write to Disk.
   */
  public void writeToFile(File file, String fileContent) {
    if (!file.exists()) {
      try {
        FileWriter writer = new FileWriter(file);
        writer.write(fileContent);
        writer.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {

      }
    }
  }

  /**
   * Reads a content from a file.
   * This is an I/O operation and this method executes in the getui_main thread, so it is recommended to
   * perform the operation using another thread.
   *
   * @param file The file to read from.
   * @return A string with the content of the file.
   */
  public String readFileContent(File file) {
    StringBuilder fileContentBuilder = new StringBuilder();
    if (file.exists()) {
      String stringLine;
      try {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((stringLine = bufferedReader.readLine()) != null) {
          fileContentBuilder.append(stringLine + "\n");
        }
        bufferedReader.close();
        fileReader.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return fileContentBuilder.toString();
  }

  /**
   * Returns a boolean indicating whether this file can be found on the underlying file system.
   *
   * @param file The file to check existence.
   * @return true if this file exists, false otherwise.
   */
  public boolean exists(File file) {
    return file.exists();
  }

  /**
   * Warning: Deletes the content of a directory.
   * This is an I/O operation and this method executes in the getui_main thread, so it is recommended to
   * perform the operation using another thread.
   *
   * @param directory The directory which its content will be deleted.
   */
  public void clearDirectory(File directory) {
    if (directory.exists()) {
      for (File file : directory.listFiles()) {
        file.delete();
      }
    }
  }

  /**
   * Write a value to a user preferences file.
   *
   * @param context {@link Context} to retrieve android user preferences.
   * @param preferenceFileName A file name reprensenting where data will be written to.
   * @param key A string for the key that will be used to retrieve the value in the future.
   * @param value A long representing the value to be inserted.
   */
  public void writeToPreferences(Context context, String preferenceFileName, String key,
      long value) {

    SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName,
        Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(key, value);
    editor.apply();
  }

  /**
   * Get a value from a user preferences file.
   *
   * @param context {@link Context} to retrieve android user preferences.
   * @param preferenceFileName A file name representing where data will be get from.
   * @param key A key that will be used to retrieve the value from the preference file.
   * @return A long representing the value retrieved from the preferences file.
   */
  public long getFromPreferences(Context context, String preferenceFileName, String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName,
        Context.MODE_PRIVATE);
    return sharedPreferences.getLong(key, 0);
  }

    /*
  public void qiNiuUpload(String file, String newFile){
      // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
      UploadManager uploadManager = new UploadManager();
      //data() = <File对象、或 文件路径、或 字节数组>
      //七牛云有两个key:
      //AK:a6s7Kuyi69zKVkmjQJoB1GuQs3cdUJJfd50mXvn5
      //SK:iws441ChOQtXd5x_6KbJURhxmuUo2aAbgO2NxyTv
      uploadManager.put(
              file, newFile, MSConstants.QINIU_KEY,
          new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
              //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
              Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
            }
          },
          new UploadOptions(null, null, false,
                  new UpProgressHandler(){
                  public void progress(String key, double percent){
                  Log.i("qiniu", key + ": " + percent);
              }
          }, null)
      );
  }
  */

  /**
   * 获取文件指定文件的指定单位的大小
   * @param filePath 文件路径
   * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
   * @return double值的大小
   */
  public static double getFileOrFilesSize(String filePath,int sizeType){
    File file=new File(filePath);
    long blockSize=0;
    try {
      if(file.isDirectory()){
        blockSize = getFileSizes(file);
      }else{
        blockSize = getFileSize(file);
      }
    } catch (Exception e) {
      DLog.p(e);
      DLog.p(filePath);
      Log.e("获取文件大小", "获取失败!");
    }
    return formatFileSize(blockSize, sizeType);
  }

  /**
   * 调用此方法自动计算指定文件或指定文件夹的大小
   * @param filePath 文件路径
   * @return 计算好的带B、KB、MB、GB的字符串
   */
  public static String getAutoFileOrFilesSize(String filePath){
    File file=new File(filePath);
    long blockSize=0;
    try {
      if(file.isDirectory()){
        blockSize = getFileSizes(file);
      }else{
        blockSize = getFileSize(file);
      }
    } catch (Exception e) {
      e.printStackTrace();
      DLog.e("获取文件大小", "获取失败!");
    }
    return formatFileSize(blockSize);
  }
  /**
   * 获取指定文件大小
   * @param file
   * @throws Exception
   */
  private static long getFileSize(File file) throws Exception
  {
    long size = 0;
    if (file.exists()){
      FileInputStream fis = null;
      fis = new FileInputStream(file);
      size = fis.available();
      fis.close();
    }
    else{
      file.createNewFile();
      DLog.e("获取文件大小", "文件不存在!");
    }
    return size;
  }

  /**
   * 获取指定文件夹
   * @param f
   * @return
   * @throws Exception
   */
  private static long getFileSizes(File f) throws Exception
  {
    long size = 0;
    File flist[] = f.listFiles();
    for (int i = 0; i < flist.length; i++){
      if (flist[i].isDirectory()){
        size = size + getFileSizes(flist[i]);
      }
      else{
        size =size + getFileSize(flist[i]);
      }
    }
    return size;
  }
  /**
   * 转换文件大小
   * @param fileS
   * @return
   */
  private static String formatFileSize(long fileS)
  {
    DecimalFormat df = new DecimalFormat("#.00");
    String fileSizeString = "";
    String wrongSize="0B";
    if(fileS==0){
      return wrongSize;
    }
    if (fileS < 1024){
      fileSizeString = df.format((double) fileS) + "B";
    }
    else if (fileS < 1048576){
      fileSizeString = df.format((double) fileS / 1024) + "KB";
    }
    else if (fileS < 1073741824){
      fileSizeString = df.format((double) fileS / 1048576) + "MB";
    }
    else{
      fileSizeString = df.format((double) fileS / 1073741824) + "GB";
    }
    return fileSizeString;
  }
  /**
   * 转换文件大小,指定转换的类型
   * @param fileS
   * @param sizeType
   * @return
   */
  private static double formatFileSize(long fileS, int sizeType)
  {
    DecimalFormat df = new DecimalFormat("#.00");
    double fileSizeLong = 0;
    switch (sizeType) {
      case SIZETYPE_B:
        fileSizeLong=Double.valueOf(df.format((double) fileS));
        break;
      case SIZETYPE_KB:
        fileSizeLong=Double.valueOf(df.format((double) fileS / 1024));
        break;
      case SIZETYPE_MB:
        fileSizeLong=Double.valueOf(df.format((double) fileS / 1048576));
        break;
      case SIZETYPE_GB:
        fileSizeLong=Double.valueOf(df.format((double) fileS / 1073741824));
        break;
      default:
        break;
    }
    return fileSizeLong;
  }

}
