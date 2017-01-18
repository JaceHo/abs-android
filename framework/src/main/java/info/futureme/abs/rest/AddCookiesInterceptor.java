package info.futureme.abs.rest;

/**
 * Created by Jeffrey on 18/01/2017.
 */


import java.io.IOException;
import java.util.HashSet;

import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences MAY VARY.
 * <p>
 * Created by tsuharesu on 4/1/15.
 */
public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) FPreferenceManager.getStringSet(FPreferenceManager.PREF_COOKIES, new HashSet<String>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            DLog.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }

        return chain.proceed(builder.build());
    }
}