package info.futureme.abs.example.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.baidu.location.BDLocation;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.futureme.abs.base.ActionBarFragmentActivity;
import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.entity.FGson;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.AttachInfos;
import info.futureme.abs.example.entity.Attachs;
import info.futureme.abs.example.entity.FAttachment;
import info.futureme.abs.example.entity.PositionRequest;
import info.futureme.abs.example.rest.DataAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.UserAPI;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.example.ui.WebActivity;
import info.futureme.abs.example.ui.fragment.ImageBrowseFragment;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.service.LocationService;
import info.futureme.abs.util.AppHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.ToastHelper;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by hippo on 11/2/15.
 */
public class ActionCallbacks {

    static final Subscription updateGrabSubscribtion[] = new Subscription[1];
    private static Subscription _subscription;

    //get single file from camera/gallery
    public static Result chooseFile(Activity context) {

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        context.startActivityForResult(i, WebActivity.FILECHOOSER_RESULTCODE);
        return null;
    }

    public static Result takePhoto(final FBaseActivity context, final RxDialogFragment rxDialogFragment, final CallBackFunction function, boolean isDir) {
        if(!isDir) {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), AppHelper.packageName());
            // Create the storage directory if it does not exist
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            final File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            final Uri imageUri = Uri.fromFile(file);
            final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            PackageManager pm = context.getPackageManager();
            if (pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
                context.requestPermission(new Runnable() {
                    @Override
                    public void run() {
                        if (rxDialogFragment != null) {
                            rxDialogFragment.startActivityForResult(intent, WebActivity.TAKEPICTURE_REQUESTCODE);
                        } else {
//                                context.startActivityForResult(intent, WebActivity.TAKEPICTURE_REQUESTCODE);
                            takePhoto(context, WebActivity.TAKEPICTURE_REQUESTCODE, imageUri);
                        }
                        WebActivity.res.setData(file.getAbsolutePath());
                        WebActivity.res.setStatus(-1);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        context.showShortToast(R.string.allow_camra_permission_request);
                    }
                }, Manifest.permission.CAMERA);
                return null;
            } else {
                DLog.toast(context.getResources().getString(R.string.no_camera));
                if (WebActivity.res != null) {
                    WebActivity.res.setStatus(0);
                    WebActivity.res.setMessage(context.getResources().getString(R.string.no_camera));
                }
                if(function != null)
                    function.onCallBack(FGson.gson().toJson(WebActivity.res));
            }

            return WebActivity.res;
        }else {
            //弹出选择获取图像方式列表。拍照和图库获取
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setSingleChoiceItems(new String[]{"拍照", "图库"}, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0://拍照
                            final File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), AppHelper.packageName());
                            // Create the storage directory if it does not exist
                            if (!imageStorageDir.exists()) {
                                imageStorageDir.mkdirs();
                            }
                            final File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            final Uri imageUri = Uri.fromFile(file);
                            final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            PackageManager pm = context.getPackageManager();
                            if (pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
                                context.requestPermission(new Runnable() {
                                    @Override
                                    public void run() {
                                        takePhoto(context, WebActivity.TAKEPICTURE_REQUESTCODE, imageUri);
                                        WebActivity.res.setData(file.getAbsolutePath());
                                        //WebActivity.photoPath = file.getAbsolutePath();
                                        WebActivity.res.setStatus(-1);
                                    }
                                }, new Runnable() {
                                    @Override
                                    public void run() {
                                        context.showShortToast(R.string.allow_camra_permission_request);
                                    }
                                }, Manifest.permission.CAMERA);
                            } else {
                                DLog.toast(context.getResources().getString(R.string.no_camera));
                                WebActivity.res.setStatus(0);
                                WebActivity.res.setMessage(context.getResources().getString(R.string.no_camera));
                                if(function != null)
                                    function.onCallBack(FGson.gson().toJson(WebActivity.res));
                            }
                            break;
                        case 1://图库
                            Intent intent_PIC = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent_PIC.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            PackageManager pm_PIC = context.getPackageManager();
                            if (pm_PIC.queryIntentActivities(intent_PIC, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
                                context.startActivityForResult(intent_PIC, WebActivity.GET_PICTURE_FROMTHUMB_REQUESTCODE);
                            } else {
                                DLog.toast(context.getResources().getString(R.string.no_camera));
                                WebActivity.res.setStatus(0);
                                WebActivity.res.setMessage(context.getResources().getString(R.string.no_camera));
                                if(function != null)
                                function.onCallBack(FGson.gson().toJson(WebActivity.res));
                            }
                            break;
                    }
                    dialog.dismiss();
                }
            }).show();
            return null;
        }

    }

    public static void takePhoto(FBaseActivity context, int token, Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (uri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        }
        try {
            context.startActivityForResult(intent, token);
        }catch (Exception e){
            context.showShortToast(R.string.allow_camra_permission_request);
        }
    }


    //file use path
    public static Result restRequest(JSONObject gson, final FBaseActivity activity, final CallBackFunction callBackFunction) {
        try {
            final JSONObject data = gson.getJSONObject("data");

            JSONObject fields = new JSONObject();
            try {
                fields = data.getJSONObject("fields");
            }catch (Exception e){}
            String url = "" + data.getString("url");
            if(!url.contains("http")){
                if(url.startsWith("/")){
                    url = url.substring(1);
                }
                url = MVSConstants.APIConstants.APIServer_Address + url;
            }
            final String full = url;
            if (url.contains("?")) {
                String tmp = url.substring(0, url.indexOf("?") + 1);
                url = url.substring(0, tmp.lastIndexOf("/") + 1);
            }else{
                url = url.substring(0, url.lastIndexOf("/"));
                if(url.lastIndexOf("/") != url.length() - 1){
                    url += "/";
                }
            }
            final String end = full.replace(url, "");
            DLog.i("paths", Arrays.deepToString(HttpUrl.parse(url).pathSegments().toArray()));
            final DataAPI api = ServiceGenerator.createService(DataAPI.class, url);
            //params传值
            DLog.toast(url + end);

            String method = "post";
            try {
                method = data.getString("method");
            }catch (Exception e){}

            boolean coord = false;
            boolean loading = false;
            try {
                coord = data.getBoolean("coord");
            }catch (Exception e){}
            try {
                loading = data.getBoolean("loadingIcon");
            }catch (Exception e){}
            data.remove("method");
            data.remove("url");
            data.remove("coord");
            data.remove("loadingIcon");
            final String finalMethod = method;
            final JSONObject finalFields = fields;
            final String finalUrl = url;
            final boolean finalLoading = loading;
            if(coord) {
                if(loading)
                activity.processPreview(activity.getString(R.string.send_the_request), null);
                final JSONObject finalJsonObject = data;
                ActionCallbacks.uploadInfoAsync(activity, new Runnable() {
                    @Override
                    public void run() {
                        BDLocation location = LocationService.getRealTimeLatLngTimeless();
                        if (location != null) {
                            try {
                                finalFields.put("longitude", location.getLongitude());
                                finalFields.put("latitude", location.getLatitude());
                                finalFields.put("address", location.getAddrStr());
                                finalFields.put("addressname", null == location.getBuildingName() ? location.getAddrStr() : location.getBuildingName());
                            } catch (JSONException e) {
                                DLog.p(e);
                            }
                        }

                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), finalJsonObject.toString());
                        Observable<ResponseBody> observable = api.dynamicPost(end, body);
                        if ("put".equalsIgnoreCase(finalMethod)) {
                            observable = api.dynamicPut(end, body);
                        } else if ("get".equalsIgnoreCase(finalMethod)) {
                            observable = api.dynamicGet(end.substring(0, end.indexOf("?")), CommonUtil.getGenerateQueryMap(full));
                        }
                        final Observable<ResponseBody> finalObservable = observable;
                        finalObservable
                                .compose(activity.<ResponseBody>bindToLifecycle())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .unsubscribeOn(Schedulers.io())
                                .subscribe(new NetworkObserver<ResponseBody>() {
                                    @Override
                                    public void onSuccess(ResponseBody body) {
                                        if(finalLoading)
                                            activity.processDismiss();
                                        try {
                                            String res = body.string();
                                            try{
                                                Result<Attachs> result = FGson.gson().fromJson(res, new TypeToken<Result<Attachs>>(){}.getType());
                                                if (result.getEcode() == 0) {
                                                    uploadGPSAddress(finalUrl);
                                                    result.setStatus(1);
                                                    if(finalLoading)
                                                        ToastHelper.makeText(activity, R.string.operate_success, 600).show();
                                                    result.setReason(null);
                                                } else {
                                                    result.setStatus(0);
                                                    onFailure(result.getReason());
                                                }
                                                if (result.getEcode() == 0 && result.getResult() != null && result.getResult().getFiles() != null) {
                                                    final ArrayList<FAttachment> uploads = new ArrayList<>();
                                                    List<AttachInfos> keys = result.getResult().getFiles();
                                                    for (AttachInfos key : keys) {
                                                        FAttachment fAttachment = new FAttachment();
                                                        if (key.getFilepath().contains(BridgeWebView.LOCAL_FILE_SCHEMA)) {
                                                            String rp = key.getFilepath().replaceAll(BridgeWebView.LOCAL_FILE_SCHEMA, "");
                                                            rp = rp.substring(0, rp.lastIndexOf("?") > 0 ? rp.lastIndexOf("?") : rp.length());
                                                            key.setFilepath(rp);
                                                        }
                                                        fAttachment.setPath(key.getFilepath());
                                                        fAttachment.setLazyId(key.getId());
                                                        fAttachment.setTicketid(activity.getIntent().getStringExtra(WebActivity.TICKET_ID));
                                                        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                                                        fAttachment.setAccount(account);
                                                        fAttachment.setType(key.getType());
                                                        String clientName = activity.getIntent().getStringExtra(WebActivity.CLIENT_NAME);
                                                        fAttachment.setClientName(clientName);
                                                        uploads.add(fAttachment);
                                                    }
                                                    if (uploads.size() > 0)
                                                        UploadService.actionLocalAttachInsert(activity, uploads);
                                                    DLog.w("files:", Arrays.deepToString(uploads.toArray()));
                                                }
                                                if(result.getStatus() == 0 && !finalLoading)
                                                    return;
                                                callBackFunction.onCallBack(FGson.gson().toJson(result));

                                        }catch (JsonSyntaxException e){
                                            Result<Object> result = FGson.gson().fromJson(res, new TypeToken<Result<Object>>() {
                                            }.getType());
                                            if (result.getEcode() == 0) {
                                                result.setStatus(1);
                                                if(finalLoading)
                                                    ToastHelper.makeText(activity, R.string.operate_success, 600).show();
                                            } else {
                                                result.setStatus(0);
                                            }
                                            if(result.getStatus() == 0 && !finalLoading)
                                                return;
                                            callBackFunction.onCallBack(FGson.gson().toJson(result));
                                        }
                                        } catch (IOException e) {
                                            onFailure(e.getMessage());
                                            DLog.p(e);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String tip) {
                                        if(finalLoading)
                                            activity.processDismiss();
                                        Result res = new Result();
                                        res.setStatus(0);
                                        res.setMessage(tip);
                                        String r = FGson.gson().toJson(res);
                                        if(res.getStatus() == 0 && !finalLoading)
                                            return;
                                        callBackFunction.onCallBack(r);
                                    }
                                });
                    }
                });
            }else {
                if(loading)
                activity.processPreview(activity.getString(R.string.send_the_request), null);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), data.toString());
                Observable<ResponseBody> observable = api.dynamicPost(end, body);
                if ("put".equalsIgnoreCase(finalMethod)) {
                    observable = api.dynamicPut(end, body);
                } else if ("get".equalsIgnoreCase(finalMethod)) {
                    observable = api.dynamicGet(end.substring(0, end.indexOf("?")), CommonUtil.getGenerateQueryMap(full));
                }
                final Observable<ResponseBody> finalObservable = observable;
                finalObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new NetworkObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody body) {
                                if(finalLoading)
                                    activity.processDismiss();
                                try {
                                    String res = body.string();
                                    try{
                                        Result<Attachs> result = FGson.gson().fromJson(res, new TypeToken<Result<Attachs>>() {
                                        }.getType());
                                        if (result.getEcode() == 0) {
                                            result.setStatus(1);
                                            if(finalLoading)
                                                ToastHelper.makeText(activity, R.string.operate_success, 600).show();
                                        } else {
                                            result.setStatus(0);
                                        }
                                        if (result.getEcode() == 0 && result.getResult() != null && result.getResult().getFiles() != null) {
                                            final ArrayList<FAttachment> uploads = new ArrayList<>();
                                            List<AttachInfos> keys = result.getResult().getFiles();
                                            for (AttachInfos key : keys) {
                                                FAttachment fAttachment = new FAttachment();
                                                String rp = key.getFilepath();
                                                if (key.getFilepath().contains(BridgeWebView.LOCAL_FILE_SCHEMA)) {
                                                    rp = key.getFilepath().replaceAll(BridgeWebView.LOCAL_FILE_SCHEMA, "");
                                                }
                                                rp = rp.substring(0, rp.lastIndexOf("?") > 0 ? rp.lastIndexOf("?") : rp.length());
                                                key.setFilepath(rp);
                                                fAttachment.setPath(key.getFilepath());
                                                fAttachment.setLazyId(key.getId());
                                                fAttachment.setTicketid(activity.getIntent().getStringExtra(WebActivity.TICKET_ID));
                                                String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                                                fAttachment.setAccount(account);
                                                fAttachment.setType(key.getType());
                                                String clientName = activity.getIntent().getStringExtra(WebActivity.CLIENT_NAME);
                                                fAttachment.setClientName(clientName);
                                                uploads.add(fAttachment);
                                            }
                                            if (uploads.size() > 0)
                                                UploadService.actionLocalAttachInsert(activity, uploads);
                                            DLog.w("files:", Arrays.deepToString(uploads.toArray()));
                                        }
                                        if(result.getStatus() == 0 && !finalLoading)
                                            return;
                                        callBackFunction.onCallBack(FGson.gson().toJson(result));
                                    }catch (JsonSyntaxException e){
                                        Result<Object> result = FGson.gson().fromJson(res, new TypeToken<Result<Object>>() {
                                        }.getType());
                                        if (result.getEcode() == 0) {
                                            result.setStatus(1);
                                            if(finalLoading)
                                                ToastHelper.makeText(activity, R.string.operate_success, 600).show();
                                        } else {
                                            result.setStatus(0);
                                        }
                                        if(result.getStatus() == 0 && !finalLoading)
                                            return;
                                        callBackFunction.onCallBack(FGson.gson().toJson(result));
                                    }
                                }catch (IOException e){
                                    onFailure(e.getMessage());
                                    DLog.p(e);
                                }
                            }

                            @Override
                            public void onFailure(String tip) {
                                if(finalLoading)
                                    activity.processDismiss();
                                Result res = new Result();
                                res.setStatus(0);
                                res.setMessage(tip);
                                String r = FGson.gson().toJson(res);
                                if(res.getStatus() == 0 && !finalLoading)
                                    return;
                                callBackFunction.onCallBack(r);
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result openFile(final Activity activity, JSONObject object) {
        Result res = new Result();
        try {
            final Intent intent = new Intent(activity, ActionBarFragmentActivity.class);
            object = object.getJSONObject("data");
            String path = object.getString("path");
            if (path != null && path.contains(BridgeWebView.LOCAL_FILE_SCHEMA)) {
                path = path.replace(BridgeWebView.LOCAL_FILE_SCHEMA, "");
                intent.putExtra(ImageBrowseFragment.IS_REMOTE, false);
            }
            //将已经下载的cache作为本地文件进行展示
            if(path != null && path.startsWith("http")){
                String local = FileCacheUtil.existLocalCache(path);
                if(local != null && new File(local).exists()){
                    path = local;
                    intent.putExtra(ImageBrowseFragment.IS_REMOTE, false);
                }else{
                    intent.putExtra(ImageBrowseFragment.IS_REMOTE, true);
                }
            }
            boolean del = false;
            try {
                del = object.getBoolean("del");
            }catch (Exception e){}
            intent.putExtra(ActionBarFragmentActivity.RIGHT_ACTIONBAR_ENABLE, del);
            if(path != null && !(path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg"))){
                //TODO
                if(path.contains("http")) {
                    Uri uri = Uri.parse(path);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(it);
                }else{
                    FileHelper.openFiles(path);
                }

                res.setStatus(1);
                return res;
            }
            intent.putExtra(ActionBarFragmentActivity.FRAGMENT_CLASS_NAME, ImageBrowseFragment.class.getName());
            intent.putExtra(ActionBarFragmentActivity.FRAGMENT_TITLE, R.string.browse_photo);
            intent.putExtra(ImageBrowseFragment.PATH, path);
            activity.startActivityForResult(intent, WebActivity.REQUEST_OPENFILE);
        } catch (JSONException e) {
            res.setStatus(0);
            res.setMessage(e.getMessage());
            return res;
        }
        return null;
    }

    public static Result callPhone(final JSONObject object, final Activity context) {
        final Result res = new Result();
            ((FBaseActivity)context).requestPermission( new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject phone = object.getJSONObject("data");
                        String number = phone.getString("phone");
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                        try {
                            res.setStatus(1);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            res.setMessage(e.getMessage());
                            res.setStatus(0);
                        }
                    } catch (JSONException e) {
                        res.setMessage(e.getMessage());
                        res.setStatus(0);
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    ((FBaseActivity) context).showShortToast(R.string.allow_permission_request_to_call);
                }
            }, Manifest.permission.CALL_PHONE);
        return null;
    }

    public static Result gotoPage(JSONObject object, WebActivity webActivity) {
        Result res = new Result();
        try {
            JSONObject data = object.getJSONObject("data");
            String url = data.getString("url");
            if (url.contains("mobile:im")) {
                //todo groupId
                //go back
            } else if (url.contains("mobile:-2")) {
                List<FBaseActivity> activityList = FBaseActivity.getActivites();
                for(int i =0 ;i < 2 ;i++){
                    activityList.get(activityList.size() - i - 1).defaultFinish();
                }
            } else if (url.contains("mobile:-1")) {
                res.setStatus(1);
                webActivity.setResult(Activity.RESULT_OK);
                webActivity.finish();
            } else if (url.contains("mobile:0")) {
                res.setStatus(1);
                webActivity.setResult(Activity.RESULT_OK);
                webActivity.refresh();
            } else {
                Intent intent = new Intent(webActivity, WebActivity.class);
                try {
                    boolean retrieve = data.getBoolean("retrieve");
                    res.setStatus(1);
                    res.setMessage("" + retrieve);
                    if(!url.contains("http")){
                        url =  MVSConstants.APIConstants.APIServer_Address + url.substring(1);
                    }
                    intent.putExtra(FConstants.X5WEBVIEW_INITIAL_URL, url);
                    intent.putExtra(WebActivity.TICKET_ID, webActivity.getIntent().getSerializableExtra(WebActivity.TICKET_ID));
                    if (retrieve)
                        webActivity.startActivityForResult(intent, WebActivity.REQUEST_SUBPAGE_DATA);
                    else
                        webActivity.startActivityForResult(intent, WebActivity.REQUEST_STEP);
                } catch (Exception e) {
                    res.setStatus(0);
                    res.setMessage(e.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void uploadInfoAsync(final Context mContext, final Runnable asyncRunnable) {
        //订单编号，登录账号，操作类型（接单/抢单/预约/到场/完成/评价），经度，维度，APP操作时间，APP版本
        Intent intent = new Intent(ContextManager.context(), LocationService.class);
        final ServiceConnection[] connection = new ServiceConnection[1];
        //raw locationsuccess listener, hold strong reference
        LocationService.LocationSuccessListener listener =
                new LocationService.LocationSuccessListener() {
                    @Override
                    public void onReceiveLocation(BDLocation location) {
                        if(connection[0] != null){
                            try {
                                mContext.unbindService(connection[0]);
                                connection[0] = null;
                            }catch (Exception e){}
                        }
                        if(location != null) {
                            LocationService.updateLocation(location);
                        }
                        asyncRunnable.run();
                     }
                };
        connection[0] = LocationService.connection(listener, intent);
        mContext.bindService(intent, connection[0], Context.BIND_AUTO_CREATE);
    }

    public static void uploadGPSAddress(String operationType){
        BDLocation location = LocationService.getRealTimeLatLngTimeless();
        //组织好数据就开始上传
        //订单编号：orderId
        //登陆账号：
        String username = PreferenceManager.getString(MVSConstants.ENGINEER_ID, "engineerid error");
        //操作类型：operationType
        //经纬度：location
        PositionRequest positionRequest = new PositionRequest();
        if (location != null) {
            positionRequest.setLongitude(location.getLongitude());
            positionRequest.setLatitude(location.getLatitude());
            positionRequest.setAction(operationType);
            positionRequest.setAddress(location.getAddrStr());
            positionRequest.setName(null == location.getBuildingName()? location.getAddrStr() : location.getBuildingName());
            Observable<Result> gpsObservable = ServiceGenerator.createService(UserAPI.class).updateEngineerPostion(username, positionRequest);
            gpsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new NetworkObserver<Result>() {
                        @Override
                        public void onSuccess(Result res) {
                            //成功不做处理
                            DLog.i("gps:", "success.");
                        }

                        @Override
                        public void onFailure(String tip) {
                                                                DLog.i("gps:", "failed.");
                                                                                          }
                    });
        }
    }

    public static Result retrieveData(JSONObject object, WebActivity webActivity) {
        Intent intent = new Intent();
        Result res = new Result();
        try {
            intent.putExtra(WebActivity.PARENT_FUNCTION_NAME, object.getString("message"));
            intent.putExtra(WebActivity.PARENT_FUNCTION_DATA, object.getJSONObject("data").toString());
            DLog.w("retrieve", "message:" + object.getString("message") + " data:" + object.getJSONObject("data").toString());
            webActivity.setResult(Activity.RESULT_OK, intent);
            res.setStatus(1);
            webActivity.finish();
        } catch (JSONException e) {
            res.setStatus(0);
            res.setMessage(e.getMessage());
            DLog.p(e);
        }
        return res;
    }
}
