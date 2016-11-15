package info.futureme.abs.example.conf;


import android.os.Build;

import info.futureme.abs.example.BuildConfig;


public final class MVSConstants {
    public static final int SDK = Build.VERSION.SDK_INT;
    //Oauth2.0 client config
    public static final String OAUTH2_CLIENT_ID = "mvs";
    public static final String OAUTH2_CLIENT_SECRET = "2YotnFZFEjr1zCsicMWpAA";
    //默认显示50千米的范围
    public static final Integer MIN_RADIS_AREA = 50*1000;

    public static final String HTTP_CACHE_DIR = "http";
    public static final Integer HTTP_CACHE_SECONDS = 40;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_NORMAL = "yyyy/MM/dd HH:mm";
    public static final long HTTP_READ_TIMEOUT = 10 * 1000;
    public static final long CONNECTION_TIMEOUT = 10 * 1000;
    public static final String REFRESH_TOKEN_TIME = "update_token_time_millis";
    public static final String ACCESS_TOKEN = "oauth2_access_token";
    public static final String REFRESH_TOKEN = "oauth2_refresh_token_mvs";
    // 一个星期未登录提示重新输入密码
    public static final long AUTHTOKEN_INVALIDE_TIME = 1000 * 60 * 60 * 24 * 7;
    //七牛云有两个key:
    //AK:a6s7Kuyi69zKVkmjQJoB1GuQs3cdUJJfd50mXvn5
    //SK:iws441ChOQtXd5x_6KbJURhxmuUo2aAbgO2NxyTv
    public static final String QINIU_KEY = "key";
    public static final int PAGE_SIZE = 10;
    public static final String GETUI_CID = "getui";
    public static final double RADIS_MIN_SIGN = 1000;
    public static final String SEARCH_HISTORY = "search_history";
    public static final String X5WEBVIEW_INITIAL_URL = "x5webview_initial_url";
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String HEADER_VERSION_KEY = "Version";
    public static final String ACCOUNT_SIGNED = "account_last_logined_name";
    public static final String KEY_GETUI_CID = "getui_cid";
    public static final String KEY_GETUI_INITED = "getui_inited";
    public static final String LOGIN_NOT_FIRST_TIME = "login_first_time";
    public static final String STEP_RESULT_DATA = "step_result_return";
    public static final String AUDIO_PATH = "audio_path";
    public static final long FILE_LIVE_TIME = 24 * 3600 * 1000;
    public static final long UPLOAD_FAIL_DURATION = 60 * 1000;
    public static final Integer UPLOAD_FAIL_MAXTIME = 10;
    public static final java.lang.String NAME_SEARCH_PLACE = "name_search_place";

    public static final String DATE_FORMAT_DAY = "MM月dd日";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_HOUR_MIN = "HH:mm";
    public static final java.lang.String TICKET_HEADER_TIME = "yyyy/MM/dd";
    public static final java.lang.String TICKET_HEADER_HISTORY_TIME = "yyyy年MM月";
    public static final String ACCOUNT_PASSWORD ="account_password" ;
    public static final String ACCOUNT_USERNAME = "account_username";
    public static final long REFRESH_DURATION = 2;
    public static final String KEY_UPLOAD_MODE_WIFI_ONLY = "upload_wifi_only";
    public static final String ENGINEER_ID = "engineerid";
    public static final java.lang.String DATE_FORMAT_LONG = "yyyyMMdd'T'HHmmss";

    public static final class LIMITS {
        public static final int IMAGE_UPLOAD_MAX_KB = 400;
        public static final int IMAGE_UPLOAD_MAX_HEIGHT = 2000;
        public static final int IMAGE_UPLOAD_MAX_WIDTH = 2000;
    }

    /**
     * 保存Fragment类型的常量
     */
    public static final class FragmentType {

        public static final String FRAGMENT_ORDERTYPE = "oder_type";
        public static final String FRAGMENT_GRABORDERS = "0";
        public static final String FRAGMENT_RECEIVEORDERS = "1";
        public static final String FRAGMENT_ACTIVORDERS = "2";
        public static final String FRAGMENT_FINISH_ORDERS = "3";
        public static final String FRAGMENT_DONE_HISTORYORDERS = "4";
        public static final String FRAGMENT_CLOSE_HISTORYORDERS = "7";

        public static final int DIALOG_CHOICE_FRAGMENT = 0x010;
        public static final int DIALOG_TIME_FRAGMENT = 0x011;
        public static final int DIALOG_TALK_FRAGMENT = 0x012;
        public static final int DIALOG_MORE_FRAGMENT = 0x013;
        public static final int DIALOG_SEARCH_FRAGMENT = 0x014;
        public static final int DIALOG_LOCATION_CHOICE_FRAGMENT = 0x015;
        public static final int DIALOG_SEARCH_FRAGMENT_MVS = 0x016;
    }

    public static final class ListParamConstants {
        public static final int FIRST_PAGE = 1;
        public static final String ListType = "listType";
        public static final String billStatus = "billStatus";
        public static final String billLevel = "emergencyLevel";
        public static final String projectId = "projectId";
        public static final String orderBy = "orderBy";
        public static final String keyWord = "keyword";
        public static final String startTime = "startTime";
        public static final String endTime = "endTime";
        public static final String mark = "mark";
        public static final String page = "page";
        public static final String pageSize = "limit";

        //参数类型
        public static final String listType_grab = "grab";
        public static final String listType_receive = "receive";
        public static final String listType_live = "live";
        public static final String listType_history = "history";

        //排序条件类型
        public static final String orderBy_default = "0";
        public static final String orderBy_distance = "1";
        public static final String orderBy_respond = "2";
        public static final String orderBy_repair = "3";

        public static final int page_default = 1;
        public static final int pageSize_default = 10;
        public static final String[] LEVEL_ORDER = {"high", "normal", "low"};


    }

    /**
     * 保存sharedPreference及sqlite常量名
     *
     * @author ltxxx
     */
    public static final class DataConstants {

        public static final String DB_NAME = "mvso2o.db";
        public static final String DATA_DIR = "mvso2o";
    }


    /**
     * 所有接口常量
     */
    public static final class APIConstants {

        //update?versionCode=23&versionName=0.9.5
        // json {"url":"http://example.com/a1.apk", "versionName":"0.1.1","versionCode":2,"md5Old":"dfajslk","md5New":"1303jfklljf", "updateMessage":"版本更新信息"}
        public static final String TAG = "UpdateChecker";
        public static final String APK_CURRENT_MD5 = "md5Old";
        public static final String APK_NEW_MD5 = "md5New";
        public static final String APK_UPDATE_CONTENT = "updateMessage";
        public static final String APK_VERSION_CODE = "versionCode";
        public static final String APK_VERSION_NAME = "versionName";
        public static final String APK_DOWNLOAD_URL = "url";




        public static final int TYPE_NOTIFICATION = 2;

        public static final int TYPE_DIALOG = 1;
        public static final String UPDATE_URL = "https://raw.githubusercontent.com/feicien/android-auto-update/develop/extras/update.json";

        /**
         * IP地址列表
         */
        public static final String API_BASE_URL = BuildConfig.CONFIG_ENDPOINT;
        public static final String API_ITSM_ADDRESS = BuildConfig.CONFIG_ITSM_ENDPOINT;

        public static final String APIServer_Address  = API_BASE_URL + "app/" + BuildConfig.API_VERSION + "/" + BuildConfig.ITSMCODE + "/";

        public static final String TICKET_STATIC_PAGE = APIServer_Address + "tickets/%s/pages/%s";

        public static final String TICKET_BACK = API_ITSM_ADDRESS + "page/sendback/%s";
        public static final String TICKET_SHORTCUT_PAGE = APIServer_Address + "pages/redirect/jump?q=%s";

        //返回创建三种订单：正常上门服务单，巡检单，补单的HTML页面
        public static final String TICKET_OPEN_PAGE = APIServer_Address + "tickets/openpage?tickettype=%s";

        public static final String STEP_URL = APIConstants.APIServer_Address + "tickets/%s/view";
        public static final String BILLING_DETAIL_URL = APIConstants.APIServer_Address + "api/repairBill/repairBilDetail";
        public static final String CLIENT_ID = "mvs";
        public static final String CLIENT_SECRET = "2YotnFZFEjr1zCsicMWpAA";
        public static final String HISTORY_STEP_URL = APIConstants.APIServer_Address + "api/repairBill/personalHistoryData";
        public static final String FEED_BACK_URL = APIConstants.APIServer_Address + "page/feedback.html";
    }

    public class UserInfoConstance {
        public static final String USER_ID = "user_Id";
        public static final String USER_ICON = "user_Icon";
        public static final String USER_ICON_LOCAL = "user_localIcon";
        public static final String USER_CREATE_USER = "user_createUser";
        public static final String USER_CREATE_TIME = "user_createTime";
        public static final String USER_UPDATE_TIME = "user_updateTime";
        public static final String USER_UPDATE_USER = "user_updateUser";
        public static final String USER_ACCOUNT = "user_account";
        public static final String USER_ACCOUNT_MOBILE = "user_accountMobile";
        public static final String USER_ACCOUNT_EMAIL = "user_accountEmail";
        public static final String USER_PASSWORD = "user_password";
        public static final String USER_IP = "user_Ip";
        public static final String USER_LASTLOGIN_TIME = "user_lastLoginTime";
        public static final String USER_CONFIG = "user_appConfigJson";
        public static final String USER_DELETE = "user_delete";
        public static final String USER_FULLNAME = "user_full_name";
    }

}
