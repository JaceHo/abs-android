package info.futureme.abs.conf;

/**
 * Created by hippo on 1/19/16.
 */
public class FConstants {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final Integer HTTP_CACHE_SECONDS = 40;
    //Oauth2.0 client config
    public static final String OAUTH2_CLIENT_ID = "mvs";
    public static final String OAUTH2_CLIENT_SECRET = "2YotnFZFEjr1zCsicMWpAA";

    public static final String HTTP_CACHE_DIR = "http";

    public static final long HTTP_READ_TIMEOUT = 10 * 1000;
    public static final long CONNECTION_TIMEOUT = 10 * 1000;
    public static final int SDK = 23;
    public static final String MAIN_FINISH = "main_finish";
    public static final String X5WEBVIEW_INITIAL_URL = "x5webview_initial_url";

    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String HEADER_VERSION_KEY = "Version";

    public static final String NAME_SEARCH_PLACE = "name_search_place";
    public static final java.lang.String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String ENCODING = "Accept-Encoding";
    public static final String USER_AGENT = "User-Agent";
    public static final String API_VERSION = "1";
    public static final String HTTPS = "https://";

    public static final String APK_DOWNLOAD_URL = "download_url";
    public static final String APK_CURRENT_MD5 = "md5_old";
    public static final String APK_NEW_MD5 = "md5_new";
    public static final String NEW_APK_PATH = "new.apk";
    public static final String PATCH_PATH = "old2new.patch";
    public static final String IS_DELTA = "is_delta";
}
