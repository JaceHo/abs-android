package info.futureme.abs.example.ui;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import info.futureme.abs.FApplication;
import info.futureme.abs.base.InjectableActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.FGson;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.TicketAction;
import info.futureme.abs.example.entity.AttachInfos;
import info.futureme.abs.example.entity.Attachs;
import info.futureme.abs.example.entity.FAttachment;
import info.futureme.abs.example.entity.TicketUpdateRequest;
import info.futureme.abs.example.rest.MediaAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.example.ui.fragment.TicketListFragment;
import info.futureme.abs.example.util.ActionCallbacks;
import info.futureme.abs.example.util.FileCacheUtil;
import info.futureme.abs.example.widget.camera.CameraSurfaceView;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.service.LocationService;
import info.futureme.abs.util.BitmapHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.ToastHelper;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SignInActivity extends InjectableActivity{

    public static final java.lang.String STEP_CONFIG_ID = "step_config_id";
    private String ticketid = "";
    private String stepId = "";
    private String clientName = "";
    int result = 90;
    private CallBackFunction callBackFunction;
    @Bind(R.id.camera_capture)
    Button btnCameraCapture;
    @Bind(R.id.camera_cancel)
    Button btnCameraCancel;
    @Bind(R.id.camera_ok)
    Button btnCameraOk;
    Result res = new Result();
    private Camera camera = null;
    private CameraSurfaceView mySurfaceView;
    private int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;//0代表后置摄像头，1代表前置摄像头

    private String plainNull = "";
    private String photoPathInSd = "";

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            if (!TextUtils.isEmpty(photoPathInSd)) {
                new File(photoPathInSd).delete();
            }
            FileHelper.ensureDir(ABSApplication.getAppDataDir() + "/image/");
            photoPathInSd = ABSApplication.getAppDataDir() + "/image/" + "IMG_" + System.currentTimeMillis() + ".jpg";
            DLog.w("pic", "length:" + (data == null ? 0 : data.length));
            savePicture(data, new File(photoPathInSd));
        }
    };
    private FrameLayout preview;
    private int rotate = 90;

    @OnClick(R.id.bt_front_back_camera)
    void onClickFrontCamera(){
        if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {//当前为前置
            cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            camera.stopPreview();//停掉原来摄像头的预览
            camera.release();//释放资源
            camera = null;//取消原来摄像头
            try {
                camera = Camera.open(cameraType);//打开当前选中的摄像头
            }catch (Exception e){
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开当前选中的摄像头
                cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            try {
                camera.setPreviewDisplay(mySurfaceView.getHolder());//通过surfaceview显示取景画面
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setCameraDisplayOrientation(cameraType, camera);
            camera.startPreview();//开始预览
            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        } else {
            cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
            camera.stopPreview();//停掉原来摄像头的预览
            camera.release();//释放资源
            camera = null;//取消原来摄像头
            try {
                camera = Camera.open(cameraType);//打开当前选中的摄像头
            }catch (Exception e){
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开当前选中的摄像头
                cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            try {
                camera.setPreviewDisplay(mySurfaceView.getHolder());//通过surfaceview显示取景画面
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setCameraDisplayOrientation(cameraType, camera);
            camera.startPreview();//开始预览
            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        }
    }

    private void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        Camera.Parameters params = camera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);

        if(params.getSupportedSceneModes() != null && params.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_AUTO)) {
            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        }
        if (params.getSupportedFlashModes() != null && params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)){
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        if(params.getSupportedFocusModes() != null && params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        }

        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        Camera.Size maxPictureSize = null;
        //选取图片的高度800
        for(int i = 0; i< pictureSizes.size(); i++){
            DLog.i("wh", "w:" + pictureSizes.get(i).width +  "h:" + pictureSizes.get(i).height);
            if(pictureSizes.get(i).height >= 800
                    && pictureSizes.get(i).width >= 600
                    || pictureSizes.get(i).height >= 600
                        && pictureSizes.get(i).width >= 800
                    ){
                if(maxPictureSize == null
                        || pictureSizes.get(i).height < maxPictureSize.height)
                    maxPictureSize = pictureSizes.get(i);
            }
        }
        if(maxPictureSize == null)
            maxPictureSize = pictureSizes.get(pictureSizes.get(0).width > pictureSizes.get(pictureSizes.size() - 1).width ? 0 : pictureSizes.size() - 1);

        int pictureHeight = maxPictureSize.height;
        int pictureWidth = maxPictureSize.width;

        float screenRatio = (float)maxPictureSize.height / (float)maxPictureSize.width;
        if (params.getSupportedPreviewSizes() != null){
            List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
            Camera.Size maxPreviewSize = previewSizes.get(0).height < previewSizes.get(previewSizes.size() - 1).height ? previewSizes.get(0) : previewSizes.get(previewSizes.size() - 1);
            float difference = Math.abs(((float)maxPreviewSize.height / (float)maxPreviewSize.width) - screenRatio);
            boolean found = false;
            float adjust = 0;
            do {
                for (Camera.Size previewSize : previewSizes) {
                    float previewRatio = (float)previewSize.height / (float)previewSize.width;
                    float diff = Math.abs(previewRatio - screenRatio);
                    if (difference + adjust > diff && maxPreviewSize.height < previewSize.height) {
                        maxPreviewSize = previewSize;
                        difference = diff;
                        found = true;
                    }
                }
                adjust += 0.01;
            } while (!found);
            params.setPreviewSize(maxPreviewSize.width, maxPreviewSize.height);

            float ratio = (float)maxPreviewSize.height / (float)maxPreviewSize.width;

            pictureHeight = (int)(maxPictureSize.width * ratio);
            pictureWidth = maxPictureSize.width;
            if (pictureHeight > maxPictureSize.height) {
                pictureHeight = maxPictureSize.height;
                pictureWidth = (int)(maxPictureSize.height / ratio);
            }
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        DLog.i("rotate degrees: ", degrees + "");

        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        DLog.i("rotate result: ", result + "");

        camera.setDisplayOrientation(result);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotate = (360 + info.orientation + degrees) % 360;
        } else {
            rotate = (360 + info.orientation - degrees) % 360;
        }
//        params.setRotation(0);
        DLog.i("rotate: ", rotate+ "");

        params.setJpegQuality(90);
        try {
            try {
//                params.setPictureSize(pictureWidth, pictureHeight);
                //TODO optimize
                DLog.i("wh", "w:" + maxPictureSize.width +  "h:" + maxPictureSize.height);
                params.setPictureSize(maxPictureSize.width, maxPictureSize.height);
                camera.setParameters(params);
            }catch (Exception e){
                params.setPictureSize(maxPictureSize.width, maxPictureSize.height);
                camera.setParameters(params);
            }
        } catch (RuntimeException e) {
            // strange stuff happnens on a unknown model.
            // fixing the first reported error by @googleplay
            DLog.p(e);
        }
    }


    @OnClick(R.id.camera_capture)
    void onClickCameraCapture(){
        camera.takePicture(mShutterCallback, null, pictureCallback);
        btnCameraCapture.setVisibility(View.INVISIBLE);
        btnCameraOk.setText(getString(R.string.use_photo_plain));
        btnCameraCancel.setText(getString(R.string.capture_again_plain));
        btnCameraOk.setVisibility(View.VISIBLE);
        btnCameraCancel.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.camera_ok)
    void onClickOk(){
        String ok_str = btnCameraOk.getText().toString();
        if (getString(R.string.use_photo_plain).equals(ok_str)) {
            btnCameraOk.setClickable(false);
            //手动签到并上传照片
            //手动签到
            if(callBackFunction == null || !new File(photoPathInSd).exists()){
                res.setStatus(0);
            }else{
                res.setData(photoPathInSd);
                res.setStatus(1);
            }
            if(callBackFunction != null)
                callBackFunction.onCallBack(FGson.gson().toJson(res));
            finish();
        }
    }

    @OnClick(R.id.camera_cancel)
    void onClickCancel(){
        btnCameraOk.setClickable(true);
        String cel_str = btnCameraCancel.getText().toString();
        if (getString(R.string.cancel).equals(cel_str)) {
            finish();
        } else if (getString(R.string.capture_again_plain).equals(cel_str)) {
            btnCameraCapture.setVisibility(View.VISIBLE);
            btnCameraOk.setVisibility(View.VISIBLE);
            btnCameraOk.setText(plainNull);
            btnCameraCancel.setVisibility(View.VISIBLE);
            btnCameraCancel.setText(getString(R.string.cancel));
            if(camera == null) {
                camera = Camera.open(cameraType);//打开当前选中的摄像头
                try {
                    camera.setPreviewDisplay(mySurfaceView.getHolder());//通过surfaceview显示取景画面
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };
            if(camera == null){
                finish();
            }else {
                try{
                    setCameraDisplayOrientation(cameraType, camera);
                    camera.startPreview();//开始预览
                    camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                }catch (Exception e){
                    try {
                        camera.stopPreview();//停掉原来摄像头的预览
                        camera.release();//释放资源
                        camera = null;//取消原来摄像头
                    }catch (Exception e2){}
                    camera = Camera.open(cameraType);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(mySurfaceView.getHolder());//通过surfaceview显示取景画面
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    setCameraDisplayOrientation(cameraType, camera);
                    camera.startPreview();//开始预览
                    camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        clientName = extra.getString(WebActivity.CLIENT_NAME);
        initData();
        btnCameraOk.setText(plainNull);
        btnCameraCancel.setText(getString(R.string.cancel));
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.mycamera_layout;
    }

    private void initData() {
        callBackFunction = new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                processPreview(getString(R.string.signing), null);
                Result result = FGson.gson().fromJson(data, Result.class);
                final String path = (String) result.getData();

                /**
                 * {"fields":{"ticketid":"87","stepconfigid":"13"},"attachments":{"pagefile_27_soRepairParts":["mobile_file:\/\/\/storage\/emulated\/0\/mso2o\/image\/1458733213979.jpg?0"],"pagefile_28_soRepairParts":["mobile_file:\/\/\/storage\/emulated\/0\/sina\/weibo\/weibo\/img-7cd930edd442d44b76f735d2ac395603.jpg?0"],"pagefile_29":["mobile_file:\/\/\/storage\/emulated\/0\/Tencent\/QQ_Images\/-31243a198ac4f91b.jpg?0"],"pagefile_30":["mobile_file:\/\/\/storage\/emulated\/0\/mvso2o\/signature_1464770270064.jpg?0"]}}
                 */
                String id = FPreferenceManager.getString(MVSConstants.ENGINEER_ID, "");
                final JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject fields = new JSONObject();
                    fields.put("ticketid", ticketid);
                    fields.put("stepconfigid", stepId);
                    fields.put("engineerid", id);
                    JSONObject attach = new JSONObject();
                    JSONArray att = new JSONArray();
                    att.put(BridgeWebView.LOCAL_FILE_SCHEMA + path + "?0");
                    attach.put("pagefile_0_stepInfo", att);
                    jsonObject.put("fields", fields);
                    jsonObject.put("attachments", attach);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ActionCallbacks.uploadInfoAsync(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        BDLocation location = LocationService.getRealTimeLatLngTimeless();
                        TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
                        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
                        linkedHashMap.put("longitude", "" + location.getLongitude());
                        linkedHashMap.put("latitude", "" + location.getLatitude());
                        linkedHashMap.put("address", "" + location.getAddrStr());
                        linkedHashMap.put("addressname", location.getBuildingName() == null ? location.getAddrStr() : location.getBuildingName());
                        ticketUpdateRequest.setFields(linkedHashMap);
                        final TicketAPI ticketAPI = ServiceGenerator.createService(TicketAPI.class);
                        ticketAPI.updateTicket(ticketid, TicketAction.SIGININ.getValue(), ticketUpdateRequest)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .unsubscribeOn(Schedulers.io())
                                .subscribe(new NetworkObserver<Result<LinkedHashMap<String, String>>>() {
                                    @Override
                                    public void onSuccess(Result<LinkedHashMap<String, String>> linkedHashMapResult) {
                                        if (linkedHashMapResult.getEcode() == 0) {
                                            rx.Observable.just(true).subscribeOn(Schedulers.io())
                                                    .subscribe(new Action1<Boolean>() {
                                                        @Override
                                                        public void call(Boolean aBoolean) {
                                                            FileCacheUtil.convertThumbnail(photoPathInSd);
                                                        }
                                                    });
                                            processDismiss();
                                            ToastHelper.makeText(ContextManager.context(), ContextManager.context().getString(R.string.sign_success), 600).show();
                                            ActionCallbacks.uploadGPSAddress(TicketAction.SIGININ.getValue());
                                            MediaAPI mediaAPI = ServiceGenerator.createService(MediaAPI.class, MVSConstants.APIConstants.API_ITSM_ADDRESS);
                                            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                                            mediaAPI.createAttachmentRecord(ticketid, body)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .unsubscribeOn(Schedulers.io())
                                                    .subscribe(new NetworkObserver<Result<Attachs>>() {
                                                        @Override
                                                        public void onSuccess(Result<Attachs> attachInfoses) {
                                                            if (attachInfoses.getEcode() == 0 && attachInfoses.getResult() != null && attachInfoses.getResult() != null && attachInfoses.getResult().getFiles().size() == 1) {
                                                                AttachInfos attachInfos1 = attachInfoses.getResult().getFiles().get(0);
                                                                ArrayList<FAttachment> list = new ArrayList<FAttachment>();
                                                                FAttachment fAttachment = new FAttachment();
                                                                fAttachment.setTicketid(ticketid);
                                                                fAttachment.setClientName(clientName);
                                                                fAttachment.setType(attachInfos1.getType());
                                                                String rp = attachInfos1.getFilepath();
                                                                if (rp.contains(BridgeWebView.LOCAL_FILE_SCHEMA)) {
                                                                    rp = attachInfos1.getFilepath().replaceAll(BridgeWebView.LOCAL_FILE_SCHEMA, "");
                                                                }
                                                                rp = rp.substring(0, rp.lastIndexOf("?") > 0 ? rp.lastIndexOf("?") : rp.length());
                                                                fAttachment.setPath(rp);
                                                                fAttachment.setLazyId(attachInfos1.getId());
                                                                list.add(fAttachment);
                                                                UploadService.actionLocalAttachInsert(ContextManager.context(), list);
                                                            } else {
                                                                onFailure(attachInfoses.getReason());
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(String tip) {
                                                            DLog.i("sign image", tip);
                                                        }
                                                    });
                                            FApplication.getRxBus().send(TicketListFragment.SIGNIN_SUCCESS);
                                        } else {
                                            onFailure(linkedHashMapResult.getReason());
                                        }
                                    }

                                    @Override
                                    public void onFailure(String tip) {
                                        processDismiss();
                                        new File(photoPathInSd).delete();
                                        Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();

        btnCameraCapture.setEnabled(false);
        btnCameraOk.setEnabled(false);
        btnCameraCapture.setClickable(false);
        btnCameraOk.setClickable(false);
        requestPermission(new Runnable() {
            @Override
            public void run() {
                if (camera == null) {
                    camera = getCameraInstance();
                    if (camera != null) {
                        setCameraDisplayOrientation(cameraType, camera);
                    } else {
                        Toast.makeText(SignInActivity.this, "请检查应用的拍照权限,调用摄像头失败!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    btnCameraCapture.setEnabled(true);
                    btnCameraOk.setEnabled(true);
                    btnCameraCapture.setClickable(true);
                    btnCameraOk.setClickable(true);
                    mySurfaceView = new CameraSurfaceView(getApplicationContext(), camera);
                    preview = (FrameLayout) findViewById(R.id.camera_preview);
                    preview.addView(mySurfaceView);
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignInActivity.this, R.string.allow_camra_permission_request, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, Manifest.permission.CAMERA);
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {//1 前置摄像头，0后置摄像头
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } catch (Exception e) {
            try {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            }catch (Exception ee){
            }
        }
        return camera;
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void onDestroy() {
        camera = null;
        super.onDestroy();
    }

    /*
    * created savePicture(byte [] data) for testing
    */
    public void savePicture(final byte[] data, final File pictureFile) {
        DLog.d("ScanVinFromBarcodeActivity ", "savePicture(byte [] data)");
        try {
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
            fileOutputStream.write(data);
            fileOutputStream.close();

            BitmapHelper.setExifRotation(rotate, pictureFile);

            DLog.i("saved", "New Image saved:" + pictureFile.getAbsolutePath());

        } catch (Exception error) {
            DLog.p(error);
            DLog.d("File not saved: ", error.getMessage());
        }
    }

}

