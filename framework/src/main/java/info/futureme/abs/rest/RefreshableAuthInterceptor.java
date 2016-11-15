package info.futureme.abs.rest;

import java.io.IOException;

import info.futureme.abs.biz.IAccountManager;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.entity.AccessToken;
import info.futureme.abs.entity.Result;
import info.futureme.abs.util.DLog;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * interceptor used to check whether network response's token is valid or should be refreshed or logged out
 */
public class RefreshableAuthInterceptor implements Interceptor {
    public static IAccountManager accountService;

    public RefreshableAuthInterceptor(IAccountManager accountService) {
        if(RefreshableAuthInterceptor.accountService == null && accountService != null) {
            RefreshableAuthInterceptor.accountService = accountService;
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        Response originalResponse;
        //not login, refresh token
        if (!request.url().uri().toASCIIString().contains("token")) {
            if (accountService.getRefreshToken() == null) {
                if(accountService.isLogin()) {
                    accountService.reLogin();
                }
                return chain.proceed(builder.build());
            }

            if (accountService.getAccessToken() == null) {
                //TODO login
                if (!attemptRefreshToken()) {
                    Response response = chain.proceed(builder.build());
                    //still unauthorized after refresh token, force logout
                    if(response.code() == 401) {
                        accountService.reLogin();
                        return response;
                    }
                }
            }
            builder.header(FConstants.HEADER_AUTHORIZATION_KEY, "Basic " + accountService.getAccessToken());
        }
        originalResponse = chain.proceed(builder.build());

        //TODO
        DLog.d("refresh", originalResponse.code() + "");
        if(originalResponse.code() == 401) {
            if (!attemptRefreshToken()) {
                //still unauthorized after refresh token, force logout
                DLog.e("again refresh", "fail!");
                return  originalResponse;
            } else {
                builder.header(FConstants.HEADER_AUTHORIZATION_KEY, "Basic " + accountService.getAccessToken());
                originalResponse = chain.proceed(builder.build());
                DLog.e("again refresh", "success:" + originalResponse.code());
            }
        }

        //recheck in case of expired time wrong!!!
        if(originalResponse.code() == 401) {
            accountService.reLogin();
            return originalResponse;
        }
        //** 20 is seconds.
        return originalResponse.newBuilder()
                .header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", FConstants.HTTP_CACHE_SECONDS, 0))
                .build();
    }

    public static boolean attemptRefreshToken() {
        synchronized (accountService.getToken()) {
            if (!accountService.isLogin()) return false;
            /*
            if(accountService.getToken().getExpiretime() != null
                && System.currentTimeMillis() - accountService.getToken().getExpiretime().getTime() < 0){
                return true;
            }
            */
            Result<AccessToken> refreshedAccessToken =
                    null;
            try {
                //TODO new token request
                refreshedAccessToken = accountService.refreshTokenSync();
            } catch (Exception e) {
                DLog.p(e);
            }
            if (refreshedAccessToken != null && refreshedAccessToken.getEcode() == 0) {
                accountService.setToken(refreshedAccessToken.getResult());
                return true;
            } else {
                accountService.reLogin();
                return false;
            }
        }
    }
}
