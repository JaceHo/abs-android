package info.futureme.abs.example.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.ActivityEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.futureme.abs.FApplication;
import info.futureme.abs.base.BaseDialogFragment;
import info.futureme.abs.base.BaseSecondaryActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.FGson;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.JSAction;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.OrderMenu;
import info.futureme.abs.example.entity.SearchItem;
import info.futureme.abs.example.entity.g.Notification;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.service.GetuiPushReceiver;
import info.futureme.abs.example.ui.fragment.ImageBrowseFragment;
import info.futureme.abs.example.ui.fragment.SettingDialogFragment;
import info.futureme.abs.example.ui.fragment.TicketContentMoreDialogFragment;
import info.futureme.abs.example.ui.fragment.TicketListFragment;
import info.futureme.abs.example.util.ActionCallbacks;
import info.futureme.abs.example.util.FileCacheUtil;
import info.futureme.abs.example.util.MediaPathUtils;
import info.futureme.abs.ui.BridgedX5WebViewFragment;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.NetworkUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class WebActivity extends BaseSecondaryActivity implements BridgeHandler, BaseDialogFragment.DialogFragmentDismissListener, BridgedX5WebViewFragment.TitleUpdater, CallBackFunction, SettingDialogFragment.DialogClickListener {

    public static final int REQUEST_LOCATION = 0x31;
    public static final String EXTRA_PATH = "extra_path";
    private static final String CONFIRM = "dialog_confirm";
    public static final String TICKET_ID = "ticket_item_id";
    public static final int REQUEST_SUBPAGE_DATA = 0x09;
    public static final String CLIENT_NAME = "client_name";
    public static final String TITLE_EXTRA = "title_extra";
    public static final String GRAB_BACK = "grab_success";
    private String title = "";
    public static final int FILECHOOSER_RESULTCODE = 0x01;
    public static final int TAKEPICTURE_REQUESTCODE = 0x02;
    public static final int GET_PICTURE_FROMTHUMB_REQUESTCODE = 0x03;
    public static final int CHOOSE_DATE = 0x04;//选择时间标志位
    public static final int RECORD_VOICE = 0x05;//录音标志位
    public static final int CHOOSE_LIST = 0x06;//打开文件浏览器标志位
    public static final int REQUEST_STEP = 0x32;
    public static final int REQUEST_OPENFILE = 0x34;//打开图片Action的key
    public static final String PARENT_FUNCTION_NAME = "parent_function_name";
    public static final String PARENT_FUNCTION_DATA = "parent_function_data";
    public boolean sign = false;


    public static Result res = new Result();
    public CallBackFunction delayCallback;
    private FragmentManager manager;
    BridgedX5WebViewFragment fragment;
    private JSONObject object;
    private String url;

    String choiceTag = "choice";
    String talkTag = "talk";
    String moreTag = "more";
    String timeTag = "time";
    private NotificationDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra(MVSConstants.X5WEBVIEW_INITIAL_URL);
        manager = getSupportFragmentManager();
        DLog.toast(url);
        pushWebFragment(url);
        FApplication.getRxBus().toObserverable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if(o.equals(GetuiPushReceiver.NEW_NOTIFICATION_UNREAD)){
                            updateMessage();
                        }else if(o instanceof Notification){
                            Notification notification = (Notification) o;
                            String ticketId = getIntent().getStringExtra(TICKET_ID);
                            if(ticketId != null && ticketId.equals(notification.getTicketid())){
                                refresh();
                            }
                        } else if(o.equals(TicketListFragment.SIGNIN_SUCCESS)){
                            refresh();
                        } else if(o instanceof SearchItem){
                            if(delayCallback != null){
                                res.setStatus(1);
                                if(((SearchItem) o).getId() == -1){
                                    if(((SearchItem) o).getProjectId() != -1 && ((SearchItem) o).getProjectName() != null) {
                                        ((SearchItem) o).setName(((SearchItem) o).getProjectName());
                                        ((SearchItem) o).setId(((SearchItem) o).getProjectId());
                                    }else if(((SearchItem) o).getDeviceId() != -1 && ((SearchItem) o).getSerialNum() != null){
                                        ((SearchItem) o).setName(((SearchItem) o).getSerialNum());
                                        ((SearchItem) o).setId(((SearchItem) o).getDeviceId());
                                    }else if(((SearchItem) o).getCustAddressId() != -1 && ((SearchItem) o).getName() != null){
                                        ((SearchItem) o).setName(((SearchItem) o).getName());
                                        ((SearchItem) o).setId(((SearchItem) o).getCustAddressId());
                                    }else if(((SearchItem) o).getFaultId() != -1 && ((SearchItem) o).getFaultName() != null){
                                        ((SearchItem) o).setName(((SearchItem) o).getFaultName());
                                        ((SearchItem) o).setId(((SearchItem) o).getFaultId());
                                    }
                                }
                                res.setData(o);
                                delayCallback.onCallBack(FGson.gson().toJson(res));
                            }
                        }
                    }
                });
    }

    private void updateMessage() {
        boolean isDetail = getIntent().getBooleanExtra(BridgedX5WebViewFragment.IS_DETAIL, false);
        if(fragment == null || !isDetail) return;
        String ticketId = getIntent().getStringExtra(TICKET_ID);
        if(dao == null) {
            dao = ABSApplication.getDaoSession().getNotificationDao();
        }
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        List<Notification> list = dao.queryBuilder().where(
                NotificationDao.Properties.Account.eq(account),
                NotificationDao.Properties.Readed.eq(false)
                ,NotificationDao.Properties.Ticketid.eq(ticketId)
        ).list();
        fragment.loadJS("message", list.size());
    }


    @Override
    protected void onResume() {
        super.onResume();
        fragment.send("", this);
    }

    private void pushWebFragment(String url) {
        fragment = BridgedX5WebViewFragment.newInstance(url);
        fragment.setDefaultBridgeHandler(this);
        fragment.setOnTitleReceiveHandler(this);
        boolean isDetail = getIntent().getBooleanExtra(BridgedX5WebViewFragment.IS_DETAIL, false);
        Bundle b = fragment.getArguments();
        if(b == null)
            b = new Bundle();
        b.putBoolean(BridgedX5WebViewFragment.IS_DETAIL, isDetail);
        fragment.setArguments(b);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the backstack
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
//提交修改
        transaction.commit();
    }


    protected String getActionBarTitleString() {
        return title;
    }

    @Override
    public void updateTitle(String title) {
        //http://stackoverflow.com/questions/6552160/prevent-webview-from-displaying-web-page-not-available
        if(title == null
                || "".equals(title)
                || "about:blank".equals(title)
                || "找不到网页".equals(title)
                || "网络错误".equals(title)){
            this.title = getIntent().getStringExtra(TITLE_EXTRA);
        }else {
            this.title = title;
        }
        super.updateTitle();
    }

    @Override
    public void onCallBack(String data) {
        updateMessage();
        DLog.d(getClass().getName(), data);
    }

    public void refresh() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            if (fragment != null)
                fragment.clearCacheAndRefresh();
        } else {
            showShortToast(R.string.network_isnot_available);
        }
    }

    protected void onStop() {
        super.onStop();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_web;
    }

    @Override
    public void handler(String data, CallBackFunction function) {
        if(fragment != null && fragment.getView() != null)
            hideKeyboard(fragment.getView());
        res.setData(new Object());
        try {
            object = new JSONObject(data);
            final int code = object.getInt("actionCode");
            JSONObject datas = new JSONObject();
            try {
                datas = object.getJSONObject("data");
            }catch (Exception e){}
            if(datas == null) datas = new JSONObject();
            if (code == JSAction.GET_PICTURE.getValue()) {
                boolean isDir = false;
                try {
                    isDir = datas.getBoolean("isDir");
                } catch (Exception e) {
                }
                boolean isSignature = false;
                try {
                    isSignature = datas.getBoolean("isSignature");
                } catch (Exception e) {
                }
                if (!isSignature) {
                    res = ActionCallbacks.takePhoto(this, null, function, isDir);
                }
            } else if (code == JSAction.RETRIEVE_H5DATA_TO_PARENT.getValue()) {
                res = ActionCallbacks.retrieveData(object, this);
            } else if (code == JSAction.OPEN_PICTURE.getValue()) {
                res = ActionCallbacks.openFile(this, object);
            } else if (code == JSAction.CHOOSE_FILE.getValue()) {
                res = ActionCallbacks.chooseFile(this);
            } else if (code == JSAction.CHOOSE_LIST.getValue()) {
//                ChoiceConditionDialogFragment dialogFragment = new ChoiceConditionDialogFragment();
                Bundle bundle = new Bundle();
//                bundle.putInt(ChoiceConditionDialogFragment.POSITION, datas.getInt("selected"));
                bundle.putBoolean(BaseDialogFragment.DISMISSABLE, true);
//                bundle.putBoolean(ChoiceConditionDialogFragment.IS_MENU_OR_NOT, "menu".equals(datas.getString("type")));
                JSONArray array = datas.getJSONArray("items");
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
                res = null;
            } else if (code == JSAction.POST_DATA.getValue()) {
                res = ActionCallbacks.restRequest(object, this, function);
            } else if (code == JSAction.CALL.getValue()) {
                res = ActionCallbacks.callPhone(object, this);
            } else if (code == JSAction.MORE_MENU.getValue()) {
                TicketContentMoreDialogFragment orderMoreFragment = new TicketContentMoreDialogFragment();
                Bundle b = new Bundle();
                JSONArray array = new JSONObject(data).getJSONArray("data");
                ArrayList<OrderMenu> list = FGson.gson().fromJson(array.toString(), new TypeToken<ArrayList<OrderMenu>>() {
                }.getType());
                b.putParcelableArrayList(TicketContentMoreDialogFragment.MENU, list);
                b.putBoolean(BaseDialogFragment.DISMISSABLE, true);
                orderMoreFragment.setArguments(b);
                orderMoreFragment.show(manager, moreTag);
                res = null;
            } else if (code == JSAction.GOTO.getValue()) {
                res = ActionCallbacks.gotoPage(object, this);
            } else if (code == JSAction.TOAST.getValue()) {
                try {
                    String tip = object.getJSONObject("data").getString("tip");
                    if(tip != null && !"".equals(tip.trim())) {
                        showShortToast(tip);
                    }
                } catch (Exception e) {
                }
                res.setStatus(1);
            } else if (code == JSAction.CONFIRM.getValue()) {
                String title = datas.getString("title");
                String message = datas.getString("message");
                SettingDialogFragment fragment = SettingDialogFragment.newInstance(title, message);
                fragment.setListener(this);
                fragment.show(getSupportFragmentManager(), CONFIRM);
                res = null;
            } else {
                res.setMessage("no actionCode found!");
                res.setStatus(0);
            }
            if (res == null) {
                res = new Result();
                res.setData(new Object());
                delayCallback = function;
            } else if (res.getStatus()!=-1){
                function.onCallBack(FGson.gson().toJson(res));
                function = null;
            }else{
                delayCallback = function;
                function = null;
            }
        } catch (JSONException e) {
            res.setStatus(0);
            res.setMessage("json data format wrong!");
            function.onCallBack(FGson.gson().toJson(res));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE && resultCode == RESULT_OK) {
            Uri result = intent == null ? null : intent.getData();
            if (result != null) {
                String path = MediaPathUtils.getImageAbsolutePath(WebActivity.this, result);
                DLog.e(TAG, path);
                try {
                    JSONObject object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = object.getJSONObject("data");

                    File newFile = new File(ABSApplication.getAppDataDir(), "file");
                    FileHelper.ensureDir(newFile.getAbsolutePath());
                    String newPath = newFile.getPath() + "/" + FileHelper.getHashAbleFileName(path, null);
                    try {
                        FileHelper.copyFile(path, newPath);
                        data.put("path", BridgeWebView.LOCAL_FILE_SCHEMA + newPath);
                        if (delayCallback != null) {
                            delayCallback.onCallBack(object.toString());
                            delayCallback = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    DLog.p(e);
                }
            }
        } else if (requestCode == REQUEST_SUBPAGE_DATA) {
            String name = null;
            String data = null;
            try {
                name = intent.getStringExtra(WebActivity.PARENT_FUNCTION_NAME);
                data = intent.getStringExtra(WebActivity.PARENT_FUNCTION_DATA);
            } catch (Exception e) {
                DLog.p(e);
            }
            fragment.loadJS(name, data);
        } else if (requestCode == TAKEPICTURE_REQUESTCODE ) {//照相
            try {
                if (resultCode == RESULT_OK)
                    res.setStatus(1);
                else
                    res.setStatus(0);
                final JSONObject object = new JSONObject(FGson.gson().toJson(res));
                final JSONObject data = new JSONObject();
                //data.put("path", BridgeWebView.LOCAL_FILE_SCHEMA + WebActivity.photoPath);
                object.put("data", data);

                if(res.getStatus() == 1){
                    final String path = "" + res.getData();
                    DLog.i("path", "" + res.getData());
                    File newFile = new File(ABSApplication.getAppDataDir(), "file");
                    FileHelper.ensureDir(newFile.getAbsolutePath());
                    final String newPath = newFile.getPath() + "/" + FileHelper.getHashAbleFileName(path, null);
                    try {
                        FileHelper.copyFile(path, newPath);
                    }catch (Exception e){
                        DLog.p(e);
                    }
                    data.put("path", BridgeWebView.LOCAL_FILE_SCHEMA + newPath);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FileCacheUtil.convertThumbnail(newPath);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    if (delayCallback != null) {
                                        delayCallback.onCallBack(object.toString());
                                        delayCallback = null;
                                    }
                                }
                            });

                        }
                    }).start();
                }
            } catch (JSONException e) {
                res.setStatus(0);
                res.setMessage(e.getMessage());
            }

        } else if (requestCode == GET_PICTURE_FROMTHUMB_REQUESTCODE && resultCode == RESULT_OK) {//图库获取图片
            Uri uri = intent.getData();
            if (uri != null) {
                try {
                    res.setStatus(1);
                    JSONObject object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = object.getJSONObject("data");
                    String path =  MediaPathUtils.getImageAbsolutePath(this, uri);
                    File newFile = new File(ABSApplication.getAppDataDir(), "file");
                    FileHelper.ensureDir(newFile.getAbsolutePath());
                    final String newPath = newFile.getPath() + "/" + FileHelper.getHashAbleFileName(path, null);
                    try {
                        FileHelper.copyFile(path, newPath);
                    }catch (Exception e){
                        DLog.p(e);
                    }
                    FileCacheUtil.convertThumbnail(newPath);
                    data.put("path", BridgeWebView.LOCAL_FILE_SCHEMA + newPath);
                    if (delayCallback != null) {
                        delayCallback.onCallBack(object.toString());
                        delayCallback = null;
                    }
                } catch (JSONException e) {
                    res.setStatus(0);
                    res.setMessage(e.getMessage());
                }
            }
        } else if (requestCode == WebActivity.REQUEST_OPENFILE) {
            try {
                if (resultCode == Activity.RESULT_OK)
                    res.setStatus(1);
                else res.setStatus(0);
                JSONObject object = new JSONObject(FGson.gson().toJson(res));
                JSONObject data = object.getJSONObject("data");
                if (intent == null) {
                    data.put("deleted", false);
                } else {
                    data.put("deleted", intent.getBooleanExtra(ImageBrowseFragment.DELTED, false));
                }
                if (delayCallback != null) {
                    delayCallback.onCallBack(object.toString());
                    delayCallback = null;
                }
            } catch (JSONException e) {
                res.setStatus(0);
                res.setMessage(e.getMessage());
            }
        } else if (requestCode == REQUEST_STEP && resultCode == RESULT_OK) {
            res.setStatus(1);
            if (delayCallback != null) {
                delayCallback.onCallBack(FGson.gson().toJson(res));
                delayCallback = null;
            }
            if (fragment != null) {
                fragment.clearCacheAndRefresh();
                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_ACTIVE);
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(fragment != null) {
            fragment.releaseWebViews();
            fragment = null;
        }
    }

    /**
     * native调用弹窗操作
     *
     * @param b   dialogFragment回传数据
     * @param Tag fragment标志
     */
    @Override
    public void onRetrieveDialogFragmentData(Bundle b, int Tag) {
        if (b == null) {
            Toast.makeText(this, getResources().getString(R.string.error_send_invalid_content), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (Tag) {
            case MVSConstants.FragmentType.DIALOG_CHOICE_FRAGMENT:
//                int position = b.getInt(ChoiceConditionDialogFragment.POSITION);
                try {
                    res.setStatus(1);
                    object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = object.getJSONObject("data");
//                    data.put("index", position);
                    data.put("text", b.getString("text"));
                    data.put("value", b.getString("value"));
                    if(delayCallback != null) {
                        DLog.w("choice:", object.toString());
                        delayCallback.onCallBack(object.toString());
                        delayCallback = null;
                    }
                } catch (JSONException e) {
                    DLog.p(e);
                }
                break;
            case MVSConstants.FragmentType.DIALOG_TALK_FRAGMENT:
                String path = b.getString("audio_path").isEmpty() ? "" : b.getString("audio_path");
                DLog.d("audio path:", path);
                try {
                    res.setStatus(1);
                    object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = object.getJSONObject("data");
                    data.put("path", BridgeWebView.LOCAL_FILE_SCHEMA + path);
                    data.put("duration", FileHelper.getAmrDuration(new File(path)));
                    delayCallback.onCallBack(object.toString());
                    delayCallback = null;
                } catch (IOException | JSONException e) {
                    DLog.p(e);
                }

                break;
            case MVSConstants.FragmentType.DIALOG_MORE_FRAGMENT:
                int actionId = b.getInt(TicketContentMoreDialogFragment.ACTION_ID);
                try {
                    res.setStatus(1);
                    object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = new JSONObject();
                    object.put("data", data);
                    object.getJSONObject("data").put("actionId", actionId);
                    delayCallback.onCallBack(object.toString());
                    delayCallback = null;
                } catch (JSONException e) {
                    DLog.p(e);
                }
                break;
            case MVSConstants.FragmentType.DIALOG_SEARCH_FRAGMENT_MVS:
                String address = b.getString("address");
                double latitude = b.getDouble("latitude");
                double longitude = b.getDouble("longitude");
                DLog.w("web:", address + "-" + latitude + "-" + longitude);
                try {
                    res.setStatus(1);
                    object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = new JSONObject();
                    object.put("data", data);
                    object.getJSONObject("data").put("location", address);
                    object.getJSONObject("data").put("latitude", latitude);
                    object.getJSONObject("data").put("longitude", longitude);
                    if(delayCallback != null) {
                        delayCallback.onCallBack(object.toString());
                        delayCallback = null;
                    }
                    break;
                } catch (JSONException e) {
                    DLog.p(e);
                }
                break;
            case MVSConstants.FragmentType.DIALOG_LOCATION_CHOICE_FRAGMENT:
                String addBack = b.getString("location");
                try {
                    if ("ADD_NEW_ADDRESS".equals(addBack)) {
                        res.setStatus(-1);
                    } else {
                        res.setStatus(1);
                    }
                    object = new JSONObject(FGson.gson().toJson(res));
                    JSONObject data = new JSONObject();
                    object.put("data", data);
                    if (!"ADD_NEW_ADDRESS".equals(addBack)) {
                        double latBack = b.getDouble("latitude");
                        double lngBack = b.getDouble("longtitude");
                        object.getJSONObject("data").put("location", addBack);
                        object.getJSONObject("data").put("latitude", latBack);
                        object.getJSONObject("data").put("longitude", lngBack);
                    }
                    delayCallback.onCallBack(object.toString());
                    delayCallback = null;
                } catch (JSONException e) {
                    DLog.p(e);
                }
                break;
            case MVSConstants.FragmentType.DIALOG_SEARCH_FRAGMENT:
//                String selected = b.getString(SearchToolbarDialogFragment.RESULT_KEY);
//                ClientDao dao = MVSApplication.getDaoSession().getClientDao();
//                List<Client> sites = dao.queryBuilder().where(ClientDao.Properties.Name.eq(selected)).list();
//                if (sites != null && sites.size() > 0) {
//                    Integer id = sites.get(0).getClientid();
//                    String name = sites.get(0).getName();
//                    try {
//                        res.setStatus(1);
//                        object = new JSONObject(FGson.gson().toJson(res));
//                        JSONObject data = new JSONObject();
//                        object.put("data", data);
//                        object.getJSONObject("data").put(SearchToolbarDialogFragment.RESULT_KEY, name);
//                        object.getJSONObject("data").put(SearchToolbarDialogFragment.RESULT_VALUE, id);
//                        DLog.i("data:", object.toString() + " id:" + id + sites.get(0).toString());
//                        delayCallback.onCallBack(object.toString());
//                        delayCallback = null;
//                        break;
//                    } catch (JSONException e) {
//                        DLog.p(e);
//                    }
//                }
                break;
        }
    }

    @Override
    public void finish() {
        //hold the fragment after finish
        if (fragment != null && fragment.getView() != null) {
            fragment.getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.hold));
        }
        //TODO result ok
        super.finish();
    }

    @Override
    protected void onDestroy(){
        ((FApplication) ContextManager.appContext()).forceUnregisterComponentCallbacks();
        super.onDestroy();
    }

    @Override
    public void doPositiveClick(String tag) {
        res.setStatus(1);
        delayCallback.onCallBack(FGson.gson().toJson(res));
    }

    @Override
    public void doNegativeClick(String tag) {
        res.setStatus(0);
        delayCallback.onCallBack(FGson.gson().toJson(res));
    }
}


