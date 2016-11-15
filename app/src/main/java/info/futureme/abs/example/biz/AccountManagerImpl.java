package info.futureme.abs.example.biz;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.igexin.sdk.PushManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.biz.IAccountManager;
import info.futureme.abs.entity.AccessToken;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.BuildConfig;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.MessageType;
import info.futureme.abs.example.conf.SyncType;
import info.futureme.abs.example.entity.Engineer;
import info.futureme.abs.example.entity.ProjectRequest;
import info.futureme.abs.example.entity.RefreshTokenRequest;
import info.futureme.abs.example.entity.g.Client;
import info.futureme.abs.example.entity.g.ClientDao;
import info.futureme.abs.example.entity.g.Notification;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.entity.g.Project;
import info.futureme.abs.example.entity.g.ProjectDao;
import info.futureme.abs.example.entity.g.SyncCode;
import info.futureme.abs.example.entity.g.SyncCodeDao;
import info.futureme.abs.example.entity.g.SysDict;
import info.futureme.abs.example.entity.g.SysDictDao;
import info.futureme.abs.example.rest.AuthAPI;
import info.futureme.abs.example.rest.DataAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.rest.UserAPI;
import info.futureme.abs.example.service.TickNetworkReceiver;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.example.ui.IntroActivity;
import info.futureme.abs.example.ui.LoginActivity;
import info.futureme.abs.example.util.DES;
import info.futureme.abs.example.util.PrefExtraUtils;
import info.futureme.abs.example.util.PreferenceManager;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import retrofit2.Call;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * configuration need to upload to the  server for next login initail
 */
public class AccountManagerImpl implements IAccountManager {
    public static final String KEY_MSG_NOTIFY = "msg_notify";
    public static final String KEY_UPLOAD_MODE_WIFI_ONLY = "upload_mode_in_wifi_only";
    public static final String KEY_SCHEME_QUERY = "SCHEME_QUERY";
    public static final String KEY_PRODUCTDESCRIPTION = "PRODUCTDESCRIPTION";
    public static final String KEY_FASTBILLING = "FASTBILLING";
    public static final String KEY_MYMESSAGE = "MYMESSAGE";
    public static final String KEY_INITIATECHAT = "INITIATECHAT";
    public static final String KEY_HANDYHAVERSACK = "HANDYHAVERSACK";
    public static final AccessToken token = new AccessToken();
    public static final AccountManagerImpl instance = new AccountManagerImpl();
    public static final Runnable terminateRunable = new Runnable() {
        @Override
        public void run() {
            NotificationDao dao = ABSApplication.getDaoSession().getNotificationDao();
            List<Notification> notificationList = dao.queryBuilder().where(NotificationDao.Properties.Type.eq(MessageType.TICKET_DATA_CHANGE.getValue())).list();
            if (notificationList != null && notificationList.size() > 0) {
                for (Notification n : notificationList)
                    dao.delete(n);
            }

        }
    };
    public static AuthAPI accountsService;
    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    private Subscription _subscription;
    private Runnable logoutRunnable = new Runnable() {
        @Override
        public void run() {
            //头像地址清除，未加载头像
            //账号登录,更新clientId, 接受登出消息
            // 先调用sdk logout，在清理app中自己的数据
            UploadService.actionStop(ContextManager.context());
            AccountManagerImpl.instance.setToken(null);
            //empty use settings
            //empty sites
            final SyncCodeDao dao = ABSApplication.getDaoSession().getSyncCodeDao();
            List<SyncCode> codes = dao.queryBuilder().where(SyncCodeDao.Properties.Type.eq(SyncType.APP_SITE_LIST.getValue())).list();
            if (codes != null && codes.size() > 0) {
                SyncCode code = codes.get(0);
                code.setCode((long) -1);
                dao.update(code);
            }
            //empty user profile
            PrefExtraUtils.empty();
            codes = dao.queryBuilder().where(SyncCodeDao.Properties.Type.eq(SyncType.APP_USER.getValue())).list();
            if (codes != null && codes.size() > 0) {
                SyncCode code = codes.get(0);
                code.setCode((long) -1);
                dao.update(code);
            }
            NotificationDao notificationDao = ABSApplication.getDaoSession().getNotificationDao();
            List<Notification> notificationList = notificationDao.queryBuilder().where(NotificationDao.Properties.Type.eq(MessageType.TICKET_DATA_CHANGE.getValue())).list();
            if (notificationList != null && notificationList.size() > 0) {
                for (Notification n : notificationList) {
                    notificationDao.delete(n);
                }
            }
        }
    };

    private AccountManagerImpl() {
        accountsService = ServiceGenerator.createService(AuthAPI.class);
    }

    public static void init() {
        accountsService = ServiceGenerator.createService(AuthAPI.class);
    }

    public static void async() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final TicketAPI ticketAPI = ServiceGenerator.createService(TicketAPI.class);
                final DataAPI otherAPI = ServiceGenerator.createService(DataAPI.class);
                final UserAPI userAPI = ServiceGenerator.createService(UserAPI.class);
                PushManager.getInstance().initialize(ContextManager.context());
                asyncPersistence(otherAPI, ticketAPI, userAPI);
                IntentFilter intentFilter = new IntentFilter();
                //addAction
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                ContextManager.appContext().registerReceiver(new TickNetworkReceiver(), intentFilter);

                intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_TIME_TICK);
                ContextManager.appContext().registerReceiver(new TickNetworkReceiver.TickReceiver(), intentFilter);
                UploadService.actionStart(ContextManager.context());
            }
        });
    }

    public static void syncAppConfigJson(UserAPI userAPI, boolean upload) {
        try {
            Result<String> res = userAPI.appConfig(upload ? PreferenceManager.exportJson().toString() + "" : "").execute().body();
            if (res != null && res.getEcode() == 0) {
                if (!upload) {
                    PreferenceManager.importJson(res.getData());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean syncProjects(DataAPI dataAPI) {
        try {
            ProjectRequest projectRequest = new ProjectRequest();
            projectRequest.setLimit(Integer.MAX_VALUE);
            String id = FPreferenceManager.getString(MVSConstants.ENGINEER_ID, null);
            projectRequest.setEngineerid(id);
            Result<ArrayList<Project>> res = dataAPI.projectList(projectRequest).execute().body();

            if (res != null && res.getStatus() == 1) {
                ProjectDao projectDao = ABSApplication.getDaoSession().getProjectDao();
                List<Project> list = projectDao.queryBuilder().list();
                projectDao.deleteAll();
                for (Project p : res.getData()) {
                    projectDao.insert(p);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean syncSites(DataAPI dataAPI) {
        try {
            Result<ArrayList<Client>> sites = dataAPI.clientList().execute().body();
            if (sites != null && sites.getStatus() == 1) {
                ClientDao siteDao = ABSApplication.getDaoSession().getClientDao();
                List<Client> list = siteDao.queryBuilder().list();
                for (Client s : list)
                    siteDao.delete(s);
                for (Client s : sites.getData())
                    siteDao.insert(s);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean sycUserDetail(UserAPI userAPI) {
        try {
            String id = FPreferenceManager.getString(MVSConstants.ENGINEER_ID, "");
            Result<Engineer> result = userAPI.engineerInfo(id).execute().body();
            if (result != null && result.getEcode() == 0) {
                Engineer user = result.getResult();
                DLog.d("FACOUNTS", "userdetail=" + result.getMessage() + user.getAvatar());
                PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_ID, user.getEngineerid());
                if(!(user.getAvatar() == null || "".equals(user.getAvatar().trim())))
                    PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_ICON, user.getAvatar());
                PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_ACCOUNT, user.getLoginname());
                PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_ACCOUNT_MOBILE, user.getPhone());
                PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_ACCOUNT_EMAIL, user.getEmail());
                PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_FULLNAME, user.getName());
          //      PrefExtraUtils.put(MVSConstants.UserInfoConstance.USER_CONFIG, user.getAppconfigjson());
                return true;

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return false;

    }

    public static boolean sycSysDics(DataAPI otherAPI) {

        try {
            Result<ArrayList<SysDict>> result = otherAPI.dict(3).execute().body();
            if (result != null && result.getStatus() == 1) {
                SysDictDao dictDao = ABSApplication.getDaoSession().getSysDictDao();
                dictDao.deleteAll();
                for (SysDict dict : result.getData()) {
                    if (dictDao.getKey(dict) != null)
                        dictDao.update(dict);
                    else
                        dictDao.insert(dict);
                }
                return true;
            }
        } catch (Exception e) {
            DLog.p(e);
        }
        return false;
    }

    public static void asyncPersistence(final DataAPI otherAPI, final TicketAPI ticketAPI, final UserAPI userAPI) {
        Observable.just(true)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        sycUserDetail(userAPI);
                    }
                });
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<Result<Map<String, Integer>>> call = otherAPI.appSync();
                try {
                    Result<Map<String, Integer>> res = call.execute().body();
                    final SyncCodeDao dao = MVSApplication.getDaoSession().getSyncCodeDao();
                    if (res != null && res.getStatus() == 1) {
                        for (final Map.Entry<String, Integer> en : res.getData().entrySet()) {
                            final List<SyncCode> syncCodes = dao.queryBuilder().where(SyncCodeDao.Properties.Type.eq(en.getKey())).list();
                            if (syncCodes != null && syncCodes.size() > 0) {
                                final SyncCode persist = syncCodes.get(0);
                                if (persist.getCode() < en.getValue()) {
                                    //server version newer,upgrade
                                    if (SyncType.APP_ADDRESS.getValue().equals(en.getKey())) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (syncProjects(ticketAPI)) {
                                                    persist.setCode((long) en.getValue());
                                                    dao.update(persist);
                                                }
                                            }
                                        }).start();
                                    } else if (SyncType.APP_SITE_LIST.getValue().equals(en.getKey())) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (syncSites(ticketAPI)) {
                                                    persist.setCode((long) en.getValue());
                                                    dao.update(persist);
                                                }
                                            }
                                        }).start();
                                    } else if (SyncType.APP_USER.getValue().equals(en.getKey())) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (sycUserDetail(userAPI)) {
                                                    persist.setCode((long) en.getValue());
                                                    dao.update(persist);
                                                }
                                            }
                                        }).start();
                                    } else if (SyncType.APP_STATUS.getValue().equals(en.getKey())) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (sycSysDics(otherAPI)) {
                                                    persist.setCode((long) en.getValue());
                                                    dao.update(persist);
                                                }
                                            }
                                        }).start();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    DLog.p(e);
                }
            }
        }).start();
        */
    }

    public static boolean checkIn(Activity context) {
        if (!FPreferenceManager.getBoolean(MVSConstants.LOGIN_NOT_FIRST_TIME, false)) {
            Intent intent =  new Intent(context, IntroActivity.class);
            context.startActivity(intent);
            return false;
        }
        String str2 = AccountManagerImpl.instance.getRefreshToken();
        Intent intent = null;
        if (TextUtils.isEmpty(str2)) {
            intent = new Intent(context, LoginActivity.class);
        } else {
            long last = PreferenceManager.getLong(MVSConstants.REFRESH_TOKEN_TIME, 0);
            // 或者记住密码超时

            if (last != 0 && (System.currentTimeMillis() - last) > MVSConstants.AUTHTOKEN_INVALIDE_TIME) {
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra(LoginActivity.UPDATE_TOKEN_TIME_LONG_AGO, true);
            }
        }
        if (intent != null) {
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    public synchronized Result<AccessToken> refreshTokenSync() throws IOException {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshtoken(getRefreshToken());
        Call<Result<AccessToken>> call = accountsService.refreshToken(request);
        return call.execute().body();
    }

    public AccessToken getToken(){
        return token;
    }

    public void setToken(AccessToken token) {
        synchronized (AccountManagerImpl.token) {
            if (token == null) {
                AccountManagerImpl.token.setToken(null);
                AccountManagerImpl.token.setRefreshtoken(null);
                AccountManagerImpl.token.setEngineerid(null);
                AccountManagerImpl.token.setExpiretime(null);
            } else {
                AccountManagerImpl.token.setToken(token.getToken());
                AccountManagerImpl.token.setRefreshtoken(token.getRefreshtoken());
                if(token.getEngineerid() != null)
                    AccountManagerImpl.token.setEngineerid(token.getEngineerid());
                if (token.getExpiretime() > System.currentTimeMillis()) {
                    DLog.w("token:", "curr: " + System.currentTimeMillis() + " create:" + token.getExpiretime());
                    token.setExpiretime(System.currentTimeMillis());
                }
                AccountManagerImpl.token.setExpiretime(token.getExpiretime());
            }
            PreferenceManager.putString(MVSConstants.ACCESS_TOKEN, AccountManagerImpl.token.getToken());
            try {
                if (AccountManagerImpl.token.getRefreshtoken() == null)
                    PreferenceManager.putString(MVSConstants.REFRESH_TOKEN, null);
                else {
                    String desRefreshToken = DES.encryptDES(AccountManagerImpl.token.getRefreshtoken(), BuildConfig.DES_KEY);
                    PreferenceManager.putString(MVSConstants.REFRESH_TOKEN, desRefreshToken);
                    PreferenceManager.putLong(MVSConstants.REFRESH_TOKEN_TIME, System.currentTimeMillis());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRefreshToken() {

        synchronized (AccountManagerImpl.token) {
            if (AccountManagerImpl.token.getRefreshtoken() == null) {
                String des2Token = PreferenceManager.getString(MVSConstants.REFRESH_TOKEN, "");
                if (!TextUtils.isEmpty(des2Token)) {
                    try {
                        String refreshToken = DES.decryptDES(des2Token, BuildConfig.DES_KEY);
                        if (!TextUtils.isEmpty(refreshToken))
                            AccountManagerImpl.token.setRefreshtoken(refreshToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            DLog.w("getRefreshtoken", null == token.getRefreshtoken() ? "" : token.getRefreshtoken());
            return token.getRefreshtoken();
        }
    }

    public String getAccessToken() {

        synchronized (AccountManagerImpl.token) {
            if (token.getToken() == null) {
                String accessToken = PreferenceManager.getString(MVSConstants.ACCESS_TOKEN, null);
                if (!TextUtils.isEmpty(accessToken)) {
                    token.setToken(accessToken);
                }
            }
            return token.getToken();
        }
    }

    /**
     * 退出登录,清空数据
     */
    public void logout() {
        try {
            logout(null);
        }catch (Exception e){
            DLog.p(e);
        }
        logoutRunnable.run();
    }

    public void logout(final EMCallBack emCallBack) {
        if (isLogin()) {
            PushManager.getInstance().stopService(ContextManager.context());
            if(_subscription == null || _subscription.isUnsubscribed()) {
                _subscription = accountsService.logout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new NetworkObserver<Result>() {
                            @Override
                            public void onSuccess(Result res) {
                                DLog.w("logout:", res + "");
                            }

                            @Override
                            public void onFailure(String tip) {
                                //AccountManagerImpl.instance.reLogin();
                                //Toast.makeText(MainActivity.this, tip, Toast.LENGTH_SHORT).show();
                                DLog.w("logout:", "fail");
                            }
                        });
            }
        }
    }

    public boolean isLogin() {
        long last = PreferenceManager.getLong(MVSConstants.REFRESH_TOKEN_TIME, 0);
        // 未超时并且可获取refreshtoken
        String id = PreferenceManager.getString(MVSConstants.ENGINEER_ID, null);
        return (!(last != 0 && (System.currentTimeMillis() - last) > MVSConstants.AUTHTOKEN_INVALIDE_TIME)) && (AccountManagerImpl.instance.getRefreshToken() != null && id != null);
    }


    public synchronized void reLogin() {
        if (isLogin()) {
            FBaseActivity.finishAll();
            AccountManagerImpl.instance.logout();
            Intent intent = new Intent(ContextManager.context(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextManager.context().startActivity(intent);
        }
    }
}

