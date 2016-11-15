package info.futureme.abs.util.update;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import info.futureme.abs.R;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.ToastHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DownloadService extends IntentService {
    // 10-10 19:14:32.618: D/DownloadService(1926): 测试缓存：41234 32kb
    // 10-10 19:16:10.892: D/DownloadService(2069): 测试缓存：41170 1kb
    // 10-10 19:18:21.352: D/DownloadService(2253): 测试缓存：39899 10kb
    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    private File apkFile;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Builder(this);

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;
        final String md5Old = intent.getStringExtra(FConstants.APK_CURRENT_MD5);
        final String md5New = intent.getStringExtra(FConstants.APK_NEW_MD5);

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = intent.getStringExtra(FConstants.APK_DOWNLOAD_URL);
        boolean delta = intent.getBooleanExtra(FConstants.IS_DELTA, false);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            apkFile = new File(dir, apkName);
            apkFile.delete();
            apkFile.createNewFile();
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress > oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }

        } catch (Exception e) {
            DLog.p(e);
            Log.e(TAG, "download apk file error");
            ToastHelper.makeText(getApplicationContext(), "下载失败,请重试", 400).show();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }

        // 下载完成
        if(apkFile != null) {
            if (delta)
                patchApk(md5Old, md5New);
            else
                installAPk(apkFile);
        }
        mNotifyManager.cancel(NOTIFICATION_ID);

    }

    private void updateProgress(int progress) {
        //"正在下载:" + progress + "%"
        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
        //setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void patchApk(final String mCurentRealMD5, final String md5New){
        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Boolean, Object>() {
                    @Override
                    public Object call(Boolean aBoolean) {
                        long mBeginTime = System.currentTimeMillis();
                        String oldApkSource = ApkUtils.getSourceApkPath(getApplicationContext());

                        String text = null;
                        // 校验一下本地安装APK的MD5是不是和真实的MD5一致
                        if (SignUtils.checkMd5(oldApkSource, mCurentRealMD5)) {
                            int patchResult = PatchUtils.patch(oldApkSource, FConstants.NEW_APK_PATH, FConstants.PATCH_PATH);

                            if (patchResult == 0) {
                                if (SignUtils.checkMd5(FConstants.NEW_APK_PATH, md5New)) {
                                    installAPk(new File(FConstants.NEW_APK_PATH));
                                } else {
                                    text = "合成完毕，但是合成得到的apk MD5不对！";
                                }
                            } else {
                                text = "新apk已合成失败！";
                            }
                        } else {
                            text = "现在安装的apk的MD5不对, 程序非法！";
                        }
                        long mEndTime = System.currentTimeMillis();
                        DLog.i("耗时: " , (mEndTime - mBeginTime) + "ms");
                        return text;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (!TextUtils.isEmpty((CharSequence) o)) {
                            ToastHelper.makeText(getApplicationContext(), (CharSequence) o, 600).show();
                        }
                    }
                });
    }

    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
