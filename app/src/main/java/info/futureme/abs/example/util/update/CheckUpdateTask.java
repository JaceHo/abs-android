package info.futureme.abs.example.util.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.entity.Result;
import info.futureme.abs.entity.UpdateResponse;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.UpdateRequest;
import info.futureme.abs.example.rest.MediaAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.util.AppHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.MD5;
import info.futureme.abs.util.update.ApkUtils;
import info.futureme.abs.util.update.AppUtils;
import info.futureme.abs.util.update.DownloadService;
import info.futureme.abs.util.update.UpdateListener;
import retrofit2.Call;
import retrofit2.Response;


/**
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-07-05 19:21
 */
public class CheckUpdateTask extends AsyncTask<Void, Void, UpdateResponse> {

    private ProgressDialog dialog;
    private Context mContext;
    private int mType;
    private boolean mShowProgressDialog;
    //only one thread could run at one time
    private static volatile boolean running = false;
    private static final String url = MVSConstants.APIConstants.UPDATE_URL;

    public UpdateListener getUpdateListener() {
        return updateListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private UpdateListener updateListener;

    public CheckUpdateTask(Context context, int type, boolean showProgressDialog ) {
        this.mContext = context;
        this.mType = type;
        this.mShowProgressDialog = showProgressDialog;
    }

    public CheckUpdateTask(Context context, int type, boolean showProgressDialog, UpdateListener updateListener) {
        this.updateListener = updateListener;
        this.mContext = context;
        this.mType = type;
        this.mShowProgressDialog = showProgressDialog;
    }

    protected void onPreExecute() {
        if(running) return;
        running = true;
        if (mShowProgressDialog) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(mContext.getString(R.string.android_auto_update_dialog_checking));
            dialog.show();
        }
    }


    @Override
    protected void onPostExecute(UpdateResponse result) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        //TODO
        /*
        result = new UpdateResponse();
        result.setUpdate(true);
        result.setAppSize(60*1024*1024);
        result.setAppVersionName("1.3.5");
        result.setAppVersionCode(13500);
        result.setDelta(false);
        result.setMd5("md5");
        result.setUpdateUrl("http://59.47.129.49/apk.r1.market.hiapk.com/data/upload/apkres/2016/8_19/12/com.hiapk.live_122502.apk?wsiphost=ipdb");
        result.setUpdateLog("update log...");
        */
        if(result != null && result.getUpdate()){
            parseResult(result);
        }

        if(updateListener != null)
            updateListener.onUpdateReturned(result);
        running = false;
    }

    // json {"url":"http://example.com/a1.apk", "versionName":"0.1.1","versionCode":2,"md5Old":"dfajslk","md5New":"1303jfklljf", "updateMessage":"版本更新信息"}
    private void parseResult(UpdateResponse updateResponse) {

        String updateMessage = updateResponse.getUpdateLog();
        String apkUrl = updateResponse.getUpdateUrl();
        String md5Old = MD5.getFileMD5(new File(ApkUtils.getSourceApkPath(ContextManager.context())));
        String md5New = updateResponse.getMd5();
        long apkCode = updateResponse.getAppVersionCode();

        int versionCode = AppUtils.getVersionCode(mContext);

        //if (apkCode > versionCode) {
            if (mType == MVSConstants.APIConstants.TYPE_NOTIFICATION) {
                showNotification(mContext, updateResponse);
            } else if (mType == MVSConstants.APIConstants.TYPE_DIALOG) {
                showDialog(mContext, updateResponse);
            }
        //} else if (mShowProgressDialog) {
        //   Toast.makeText(mContext, mContext.getString(R.string.android_auto_update_toast_no_new_update), Toast.LENGTH_SHORT).show();
        //}

    }


    /**
     * Show dialog
     */
    private void showDialog(Context context, UpdateResponse updateResponse) {
        UpdateDialog.show(context, updateResponse);
    }

    public static void goToDownload(Context context, UpdateResponse updateResponse) {
        String apkUrl = updateResponse.getUpdateUrl();
        String md5Old = MD5.getFileMD5(new File(ApkUtils.getSourceApkPath(ContextManager.context())));
        String md5New = updateResponse.getMd5();

        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra(FConstants.APK_DOWNLOAD_URL, apkUrl);
        intent.putExtra(FConstants.APK_CURRENT_MD5, md5Old);
        intent.putExtra(FConstants.APK_NEW_MD5, md5New);
        intent.putExtra(FConstants.IS_DELTA, updateResponse.isDelta());
        context.startService(intent);
    }

    /**
     * Show Notification
     */
    public static void showNotification(Context context, UpdateResponse updateResponse) {

        String apkUrl = updateResponse.getUpdateUrl();
        String md5Old = MD5.getFileMD5(new File(ApkUtils.getSourceApkPath(ContextManager.context())));
        String md5New = updateResponse.getMd5();

        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra(FConstants.APK_DOWNLOAD_URL, apkUrl);
        intent.putExtra(FConstants.APK_CURRENT_MD5, md5Old);
        intent.putExtra(FConstants.APK_NEW_MD5, md5New);
        intent.putExtra(FConstants.IS_DELTA, updateResponse.isDelta());

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int smallIcon = context.getApplicationInfo().icon;
        Notification notify = new NotificationCompat.Builder(context)
                .setTicker(context.getString(R.string.android_auto_update_notify_ticker))
                .setContentTitle(context.getString(R.string.android_auto_update_notify_content))
                .setContentText(updateResponse.getUpdateLog())
                .setSmallIcon(smallIcon)
                .setContentIntent(pendingIntent).build();

        notify.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notify);
    }

    @Override
    protected UpdateResponse doInBackground(Void... args) {
        //MediaAPI mediaAPI = ServiceGenerator.createService(MediaAPI.class, "http://10.103.115.206:8080/");
        MediaAPI mediaAPI = ServiceGenerator.createService(MediaAPI.class, MVSConstants.APIConstants.API_ITSM_ADDRESS);
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setAppIdentifier(AppHelper.packageName());
        updateRequest.setAppVersionCode(AppHelper.versionCode());
        Call<Result<UpdateResponse>> call = mediaAPI.getUpdateInfo(updateRequest);
        try {
            Response<Result<UpdateResponse>> responseResponse = call.execute();
            if(responseResponse.body() != null){
                if(responseResponse.body().getEcode() == 0){
                    DLog.i("res:",responseResponse.body().getResult().toString());
                    return responseResponse.body().getResult();
                }else{
                    throw  new Exception(responseResponse.body().getReason());
                }
            }
        } catch (final Exception e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ContextManager.context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }
}
