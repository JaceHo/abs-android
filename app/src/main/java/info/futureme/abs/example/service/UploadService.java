package info.futureme.abs.example.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import info.futureme.abs.FApplication;
import info.futureme.abs.base.BaseService;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.FAttachment;
import info.futureme.abs.example.entity.g.Attachment;
import info.futureme.abs.example.entity.g.AttachmentDao;
import info.futureme.abs.example.rest.MediaAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.rest.CountingRequestBody;
import info.futureme.abs.util.BitmapHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.FileSizeHelper;
import info.futureme.abs.util.NetworkUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class UploadService extends BaseService {

    public static final String ACTION_LOCAL_ATTACHMENT_INSERT = "info.futureme.abs.example.LOCAL.ATTACHEMENT.INSERT";
    private static final String KEY_ATTACHMENT_LIST = "attachment";
    private MediaAPI mPushClient = null;
    private static Scheduler.Worker worker = Schedulers.io().createWorker();
    private static AttachmentDao dao = ABSApplication.getDaoSession().getAttachmentDao();

    public enum UPLOAD_MODE {
        NOW_ONCE(-1),
        NORMAL(0),
        FAILED(1),
        SUCCESS(2);
        private int value;

        UPLOAD_MODE(int i) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }

    ;


    public static class AttachmentChangeInfo {
        public int percent;//0-100
        public String ticketId;
        public String attachmentId;
        public String path;
        public String name;
        public ATTACHMENT_STATUS status = ATTACHMENT_STATUS.IDLE;

        public enum ATTACHMENT_STATUS {
            IDLE,
            SUCCESS,
            FAILURE,
            UPLOADING,
            DEL,
            NEW
        }
    }


    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, UploadService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    public static void actionLocalAttachInsert(Context ctx, ArrayList<FAttachment> list) {
        Log.i(UploadService.class.getName(), "attach insert");
        if (!AccountManagerImpl.instance.isLogin()) return;

        Bundle data = new Bundle();
        data.putParcelableArrayList(KEY_ATTACHMENT_LIST, list);
        Intent i = new Intent(ctx, UploadService.class);
        i.setAction(ACTION_LOCAL_ATTACHMENT_INSERT);
        i.putExtras(data);
        ctx.startService(i);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(getClass().getName(), "onBind");
        return super.onBind(intent);
    }


    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.w("upload", "startcommand");
        super.onStartCommand(intent, flags, startId);
        if (intent == null || intent.getAction() == null || intent.getExtras() == null) {
            return START_STICKY;
        }
        if (intent.getAction().equals(ACTION_LOCAL_ATTACHMENT_INSERT)) {
            worker.schedule(new Action0() {
                @Override
                public void call() {
                    synchronized (UploadService.class) {
                        Bundle data = intent.getExtras();
                        final ArrayList<FAttachment> args = data.getParcelableArrayList(KEY_ATTACHMENT_LIST);
                        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                        for (FAttachment newAttachment : args) {
                            if (newAttachment.getLazyId() == null || newAttachment.getType() == null)
                                continue;
                            List<Attachment> same = dao.queryBuilder().where(
                                    AttachmentDao.Properties.LazyId.eq(newAttachment.getLazyId()),
                                    AttachmentDao.Properties.Type.eq(newAttachment.getType()),
                                    AttachmentDao.Properties.Account.eq(account)
                            ).list();
                            if (same == null || same.size() == 0) {
                                newAttachment.setAccount(account);
                                newAttachment.setFailedTime(0);
                                dao.insert(newAttachment);
                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.NEW, newAttachment);
                            }
                        }
                        restartIfNecessary();
                    }
                }
            });
        } else {
            restartIfNecessary();
        }
        return START_STICKY;
    }

    /* get upload attaments include:
     * a. failed uploaded attachment(retry after 2^failedCount　time
     * b. new attachment
     */
    private static List<Attachment> getAttachTodos() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");

        List<Attachment> todoNow = dao.queryBuilder().where(AttachmentDao.Properties.Status.eq(UPLOAD_MODE.NOW_ONCE.getValue()),
                AttachmentDao.Properties.Account.eq(account)
        ).list();
        if (todoNow != null && todoNow.size() != 0)
            return todoNow;

        List<Attachment> todos = dao.queryBuilder().where(AttachmentDao.Properties.Status.eq(1),
                AttachmentDao.Properties.Account.eq(account)
        ).list();
        if (todos != null && todos.size() > 0) {
            for (Attachment a : todos) {
                if ((a.getFailedTime() == null ? 0 : a.getFailedTime()) > 0) {
                    // if (a.getFailedTime() > 3 || System.currentTimeMillis() - a.getTime() > MVSConstants.UPLOAD_FAIL_DURATION * Math.pow(2, a.getFailedTime() - 1)) {
                    if (System.currentTimeMillis() - a.getTime() > MVSConstants.UPLOAD_FAIL_DURATION * Math.pow(2, (a.getFailedTime() == null ? 0 : a.getFailedTime()) - 1)) {
                        a.setStatus(UPLOAD_MODE.NORMAL.getValue());
                        dao.update(a);
                    }
                }
            }
        }
        return dao.queryBuilder().where(AttachmentDao.Properties.Status.eq(UPLOAD_MODE.NORMAL.getValue()),
                AttachmentDao.Properties.Account.eq(account)
        ).list();
    }

    //根据连接模式判断连接状态可以上传图片, 附件
    public static boolean isConnectedNecessary() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        List<Attachment> todoNow = dao.queryBuilder().where(AttachmentDao.Properties.Status.eq(UPLOAD_MODE.NOW_ONCE.getValue()),
                AttachmentDao.Properties.Account.eq(account)
        ).list();
        if (todoNow != null && todoNow.size() != 0) {
            return NetworkUtil.isNetworkAvailable(ContextManager.context());
        }
        //默认一直上传
        if (FPreferenceManager.getBoolean(MVSConstants.KEY_UPLOAD_MODE_WIFI_ONLY + account, false)) {
            return NetworkUtil.isWifiConnected(ContextManager.context());
        } else {
            return NetworkUtil.isNetworkAvailable(ContextManager.context());
        }
    }

    public static boolean shouldRestart() {
        Log.i("upload", "connected?" + isConnectedNecessary());
        if (isConnectedNecessary()) {
            List<Attachment> todos = getAttachTodos();
            if (todos != null
                    && todos.size() > 0) {
                Log.i("upload", "todos?" + todos.size());
                return true;
            }
        }
        return false;
    }

    public static void actionStop(Context ctx) {
        BaseService.actionStop(ctx, UploadService.class);
    }

    synchronized protected void restartIfNecessary() {
        List<Attachment> todos = getAttachTodos();
        if (isConnectedNecessary()
                && todos != null
                && todos.size() > 0) {
            Log.w(getClass().getName(), "Reconnecting...");
            connect();
        }
    }

    public static int getUploadingNumber() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        List<Attachment> uploads = dao.queryBuilder().where(
                AttachmentDao.Properties.Account.eq(account)).list();
        return uploads.size();
    }

    public static String getUploadingSize() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        List<Attachment> uploads = dao.queryBuilder().where(
                AttachmentDao.Properties.Account.eq(account)).list();
        double total = 0.0;
        String bytes = "KB";
        for (Attachment a : uploads) {
            double res = FileSizeHelper.getFileOrFilesSize(a.getPath(), FileSizeHelper.SIZETYPE_KB);
            total += (res > 400 ? res % 100 + 300 : res);
        }
        if (total >= 1000) {
            total /= 1000;
            bytes = "MB";
        }
        if (total >= 1000) {
            total /= 1000;
            bytes = "GB";
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        if (total == 0.0)
            return null;
        return df.format(total) + bytes;
    }

    private void sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS status, Attachment a) {
        AttachmentChangeInfo changeInfo = new AttachmentChangeInfo();
        changeInfo.status = status;
        changeInfo.ticketId = a.getTicketid();
        changeInfo.attachmentId = a.getLazyId();
        changeInfo.path = a.getPath();
        changeInfo.name = a.getClientName();
        FApplication.getRxBus().send(changeInfo);
    }

    private void connect() {
        worker.schedule(new Action0() {
            @Override
            public void call() {
                synchronized (UploadService.class) {
                    List<Attachment> todos = getAttachTodos();
                    while (todos != null && todos.size() != 0) {
                        Log.w(getClass().getName(), todos.size() + "");
                        if (!AccountManagerImpl.instance.isLogin()) return;
                        todos = getAttachTodos();
                        if (todos == null
                                || todos.size() == 0
                                || !isConnectedNecessary()) {
                            stop();
                            return;
                        }
                        final Attachment a = todos.get(0);
                        try {
                            if ((a.getFailedTime() == null ? 0 : a.getFailedTime()) > MVSConstants.UPLOAD_FAIL_MAXTIME) {
                                dao.delete(a);
                                String cache = ABSApplication.getCachedDir() + FileHelper.getHashAbleFileName(a.getPath(), null);
                                FileHelper.copyFile(a.getPath(), cache);
                                new File(a.getPath()).delete();
                                a.setPath(cache);
                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.DEL, a);
                                continue;
                            }
                            String extension = FileHelper.splitExtension(a.getPath());
                            String scaleDir = ABSApplication.getAppDataDir() + "/scaled/";
                            FileHelper.ensureDir(scaleDir);
                            String newPath = scaleDir + FileHelper.getHashAbleFileName(a.getPath(), null);
                            if ("png".equals(extension)
                                    || "jpg".equals(extension)
                                    || "jpeg".equals(extension)) {
                                double sizeKB = FileSizeHelper.getFileOrFilesSize(a.getPath(), FileSizeHelper.SIZETYPE_KB);
                                if (sizeKB > MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_KB)
                                    if (!a.getPath().contains("scaled")) {
                                        Bitmap scaledBitmap =
                                                BitmapHelper.safeDecodeScaledBitmapFromFileSystem(a.getPath(), MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_WIDTH,
                                                        MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_HEIGHT);
                                        boolean success = BitmapHelper.shrinkBitmap(scaledBitmap, MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_KB, new File(newPath));
                                        Log.d("scale:", "success" + success + " scalePath" + newPath);
                                        if (success) {
//                                            new File(a.getPath()).delete();
                                            a.setPath(newPath);
                                            dao.update(a);
                                        } else {
                                            a.setFailedTime((a.getFailedTime() == null ? 0 : a.getFailedTime()) + 1);
                                            if ((a.getFailedTime() == null ? 0 : a.getFailedTime()) > MVSConstants.UPLOAD_FAIL_MAXTIME) {
                                                Log.d("failed:", a.getPath());
                                                dao.delete(a);
                                                String cache = ABSApplication.getCachedDir() + FileHelper.getHashAbleFileName(a.getPath(), null);
                                                FileHelper.copyFile(a.getPath(), cache);
                                                new File(a.getPath()).delete();
                                                a.setPath(cache);
                                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.DEL, a);
                                            } else {
                                                a.setStatus(UPLOAD_MODE.FAILED.getValue());
                                                dao.update(a);
                                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.FAILURE, a);
                                            }
                                            continue;
                                        }
                                    }
                            }

                            if (!a.getPath().equals(newPath)) {
                                FileHelper.copyFile(a.getPath(), newPath);
                                a.setPath(newPath);
                                dao.update(a);
                            }
                            Log.d("path:", a.getPath() + " exist:" + FileHelper.exists(a.getPath()));
                            if (!FileHelper.exists(a.getPath())) {
                                dao.delete(a);
                                String cache = ABSApplication.getCachedDir() + FileHelper.getHashAbleFileName(a.getPath(), null);
                                FileHelper.copyFile(a.getPath(), cache);
                                new File(a.getPath()).delete();
                                a.setPath(cache);
                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.DEL, a);
                                continue;
                            } else {
                                //rotate if necessary
                                try {
                                    // Decode image size
                                    int degree = BitmapHelper.readPictureDegree(a.getPath());
                                    if (degree % 360 != 0) {
                                        DLog.i("rotate:", degree + "");
                                        Bitmap bitmap = BitmapHelper.rotateBy(BitmapFactory.decodeFile(a.getPath()), degree);
                                        BitmapHelper.toFile(bitmap, new File(a.getPath()));

                                        ExifInterface exifInterface = new ExifInterface(a.getPath());
                                        exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
//                                        exifInterface.saveAttributes();
                                        DLog.i("rotate:", "success!");
                                    }
                                } catch (IOException e) {
                                    DLog.p(e);
                                }
                            }
                            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(a.getPath()));
                            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), a.getLazyId());
                            RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), a.getType());
                            mPushClient = ServiceGenerator.createService(MediaAPI.class, MVSConstants.APIConstants.API_ITSM_ADDRESS,
                                    null, new CountingRequestBody.Listener() {
                                        @Override
                                        public void onRequestProgress(long bytesWritten, long contentLength) {
                                            AttachmentChangeInfo changeInfo = new AttachmentChangeInfo();
                                            changeInfo.status = AttachmentChangeInfo.ATTACHMENT_STATUS.UPLOADING;
                                            changeInfo.ticketId = a.getTicketid();
                                            changeInfo.percent = (int) ((bytesWritten / contentLength) * 100);
                                            changeInfo.attachmentId = a.getLazyId();
                                            changeInfo.path = a.getPath();
                                            FApplication.getRxBus().sendDebouncing(changeInfo);
                                        }
                                    }, null);

                            Response<Result> res =
                                    mPushClient.uploadAttachmentAsync(
                                            fileBody, idBody, typeBody
                                    ).execute();
                            if (res.body() != null && res.body().getEcode() == 0) {
                                Log.d("success", a.getPath());
                                dao.delete(a);
                                String cache = ABSApplication.getCachedDir() + FileHelper.getHashAbleFileName(a.getPath(), null);
                                FileHelper.copyFile(a.getPath(), cache);
                                new File(a.getPath()).delete();
                                a.setPath(cache);
//                                worker.schedule(new Action0() {
//                                    @Override
//                                    public void call() {
//                                        a.setStatus(UPLOAD_MODE.NORMAL.getValue());
//                                        dao.insert(a);
//                                    }
//                                }, 10*1000, TimeUnit.MICROSECONDS);
                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.SUCCESS, a);
                            } else {
                                if (res.body() != null && res.body().getEcode() == -1) {
                                    a.setFailedTime(MVSConstants.UPLOAD_FAIL_MAXTIME);
                                    dao.update(a);
                                }
                                Log.d("fail:", res.body() + "");
                                if (AccountManagerImpl.instance.isLogin()) {
                                    a.setFailedTime((a.getFailedTime() == null ? 0 : a.getFailedTime()) + 1);
                                    a.setTime(System.currentTimeMillis());
                                    a.setStatus(UPLOAD_MODE.FAILED.getValue());
                                    dao.update(a);
                                    sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.FAILURE, a);
                                } else {
                                    DLog.p("not login:" + todos.size());
                                    stop();
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            DLog.d("upload:", "exception:" + e.getMessage());
                            if (AccountManagerImpl.instance.isLogin()) {
                                a.setFailedTime((a.getFailedTime() == null ? 0 : a.getFailedTime()) + 1);
                                a.setTime(System.currentTimeMillis());
                                a.setStatus(UPLOAD_MODE.FAILED.getValue());
                                dao.update(a);
                                sendStatus(AttachmentChangeInfo.ATTACHMENT_STATUS.FAILURE, a);
                            } else {
                                DLog.p("not login:" + todos.size());
                                stop();
                                AccountManagerImpl.instance.reLogin();
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.w(this.getClass().getName(), "onDestroy");
        super.onDestroy();
    }

    protected void onStart() {
        Log.i("upload", "onstart");

        Log.w(TAG, "Connecting...");

        connect();
    }

    protected void onStop() {
        Log.i("upload", "onstop");

        cancelRestart(UploadService.class);
        if (mPushClient != null) {
            mPushClient = null;
        }
    }

    protected void onError() {
        if (!mStarted) {
            Log.i(TAG, "Push Client stoped, shutting down.");
        } else {

            mPushClient = null;

            if (isNetworkAvailable()) {
                scheduleRestart(UploadService.class);
            }
        }
    }

}
