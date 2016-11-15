package info.futureme.abs.example.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.g.FileCache;
import info.futureme.abs.example.entity.g.FileCacheDao;
import info.futureme.abs.util.BitmapHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.MD5;

/**
 * Created by hippo on 12/16/15.
 */
public class FileCacheUtil {
    private static final FileCacheDao fileCacheDao = ABSApplication.getDaoSession().getFileCacheDao();

    public static String existLocalCache(String url){
        //1 .从数据库中取未过期的文件
        synchronized (fileCacheDao) {
            final List<FileCache> list = fileCacheDao.queryBuilder().where(FileCacheDao.Properties.Timestamp.gt(System.currentTimeMillis() - MVSConstants.FILE_LIVE_TIME)).where(FileCacheDao.Properties.Url.eq(url)).where(FileCacheDao.Properties.Path.isNotNull()).list();
            if (list != null && list.size() == 1) {
                File file = new File(list.get(0).getPath());
                if (!file.exists()) {
                    fileCacheDao.delete(list.get(0));
                    return null;
                }
                return list.get(0).getPath();
            } else {
                //2. 删除过期
                List<FileCache> tmpList = fileCacheDao.queryBuilder().where(FileCacheDao.Properties.Timestamp.le(System.currentTimeMillis() - MVSConstants.FILE_LIVE_TIME)).where(FileCacheDao.Properties.Url.eq(url)).where(FileCacheDao.Properties.Path.isNotNull()).list();
                if (list != null) {
                    for (FileCache f : tmpList) {
                        new File(f.getPath()).delete();
                        fileCacheDao.delete(f);
                    }
                }
            }
        }
        return null;
    }

    public static void getFileFromWebOrLocal(final String url, final FileLoadListener listener, final Activity activity){
        //1 .从数据库中取未过期的文件
        synchronized (fileCacheDao) {
            final List<FileCache> list = fileCacheDao.queryBuilder().where(FileCacheDao.Properties.Timestamp.gt(System.currentTimeMillis() - MVSConstants.FILE_LIVE_TIME)).where(FileCacheDao.Properties.Url.eq(url)).where(FileCacheDao.Properties.Path.isNotNull()).list();
            if (list != null && list.size() == 1) {
                File file  = new File(list.get(0).getPath());
                if(!file.exists()){
                    fileCacheDao.delete(list.get(0));
                    getFileFromWebOrLocal(url,listener,activity);
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onGotFile(new File(list.get(0).getPath()));
                    }
                });
            } else {
                //2. 删除过期
                List<FileCache> tmpList = fileCacheDao.queryBuilder().where(FileCacheDao.Properties.Timestamp.le(System.currentTimeMillis() - MVSConstants.FILE_LIVE_TIME)).where(FileCacheDao.Properties.Url.eq(url)).where(FileCacheDao.Properties.Path.isNotNull()).list();
                if (list != null) {
                    for (FileCache f : tmpList) {
                        new File(f.getPath()).delete();
                        fileCacheDao.delete(f);
                    }
                }


                //3. 网络获取
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url_get = new URL(url);
                            HttpURLConnection conn = (HttpURLConnection) url_get.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            File listnames = new File(ABSApplication.getCachedDir().getAbsolutePath() + File.separator);
                            final String file =  listnames.getAbsolutePath() + new MD5(url).getMD5() + url.substring(url.lastIndexOf("."));
                            if( !listnames.exists())
                                listnames.mkdirs();
                            else if( !listnames.isDirectory() && listnames.canWrite() ){
                                listnames.delete();
                                listnames.mkdirs();
                            }
                            else{
                                //you can't access there with write permission.
                                //Try other way.
                                DLog.i("access", "fail!");
                            }
                            FileOutputStream fos = new FileOutputStream(file);
                            int len = 0;
                            long p = 0;
                            byte[] buf = new byte[1024];
                            int prePro = 0;
                            listener.onProgress(0);
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                p += len;
                                if((int) ((100.0f/is.available())* p) - prePro > 3){
                                    listener.onProgress((int) ((100.0f/is.available())* p));
                                }
                                prePro = (int) ((100.0f/is.available())* p);
                            }
                            fos.close();
                            is.close();
                            fos = null;
                            is = null;
//保存到数据库
                            FileCache fileCache = new FileCache();
                            fileCache.setPath(file);
                            fileCache.setUrl(url);
                            fileCache.setTimestamp(System.currentTimeMillis());
                            fileCacheDao.insert(fileCache);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onGotFile(new File(file));
                                }
                            });

                        } catch (final Exception e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onFail(e.getMessage());
                                }
                            });

                        }
                    }
                }).start();

            }
        }
    }

    public static String convertThumbnail(String path){
        String thumbnail = (ABSApplication.getCachedDir().getAbsolutePath() + "/" + Base64.encodeToString(path.getBytes(), 0)).replaceAll("\\s*", "").replaceAll("=*", "").trim();
        boolean thumb = true;
        if(!new File(thumbnail).exists()) {
            BitmapFactory.Options options = BitmapHelper.readBitmapInfo(FileHelper.TYPE_FILE_SYSTEM, path);
            if (options != null) {
                Bitmap bitmap = BitmapHelper.getThumbnail(path, 300, (300*options.outHeight) / options.outWidth);
                // Decode image size

                int degree = BitmapHelper.readPictureDegree(path);
                if(degree > 0) {
                    bitmap = BitmapHelper.rotateBy(bitmap, degree);
                }
                thumb = BitmapHelper.toFile(bitmap, new File(thumbnail));
                try {
                    if(degree>0) {
                        ExifInterface exifInterface = new ExifInterface(thumbnail);
                        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
//                        exifInterface.saveAttributes();
                    }
                } catch (Exception e) {
                }
            }
        }
        return  thumb ? thumbnail : null;
    }

    /**
     * Created by hippo on 12/16/15.
     */
    public interface FileLoadListener {
        void onGotFile(File file);
        void onFail(String error);
        void onProgress(int progress);
    }
}
