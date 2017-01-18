package info.futureme.abs.rest.client;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import info.futureme.abs.FApplication;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.biz.IAccountManager;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.rest.AddCookiesInterceptor;
import info.futureme.abs.rest.CountingRequestBody;
import info.futureme.abs.rest.DownloadProgressInterceptor;
import info.futureme.abs.rest.ReceivedCookiesInterceptor;
import info.futureme.abs.rest.RefreshableAuthInterceptor;
import info.futureme.abs.rest.TimberLoggingInterceptor;
import info.futureme.abs.rest.UpLoadProgressInterceptor;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * httpclient suitable for https&http connection but client is not certificated using specified ssl certificate
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 18:02:54
 */
public class OneWayAuthHttpClient {
    private static OkHttpClient okHttpClient;
    private static OkHttpClient glideClient;

    private static Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            return chain.proceed(builder.build());
        }
    };

    public static synchronized OkHttpClient getGlideClient() {
        if(glideClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(FConstants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(FConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(false)
                    .addInterceptor(headerInterceptor);
            if(FApplication.DEBUG) {
                builder.addInterceptor(new TimberLoggingInterceptor());
            }
            ignoreHttps(builder);
            glideClient = builder.build();
        }
        return glideClient;
    }

    public static void ignoreHttps(OkHttpClient.Builder clientBuilder){

        final X509TrustManager[] trustManager = new X509TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                            CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                            CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};    // 返回空
                    }
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            clientBuilder.sslSocketFactory(sslSocketFactory, trustManager[0]);
            clientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static synchronized OkHttpClient getClient(IAccountManager accountService, CountingRequestBody.Listener uploadListener, DownloadProgressInterceptor.DownloadProgressListener downloadProgressListener) {
        if(accountService != null && okHttpClient != null && uploadListener == null && downloadProgressListener == null)
            return okHttpClient;
        Cache cache = new Cache(new File(ContextManager.context().getCacheDir(), FConstants.HTTP_CACHE_DIR), 1024 * 1024 * 50);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(FConstants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(FConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .cache(cache)
                .addInterceptor(new AddCookiesInterceptor())
                .addInterceptor(new ReceivedCookiesInterceptor())
                .addInterceptor(new RefreshableAuthInterceptor(accountService));
        if(uploadListener != null)
            builder.addNetworkInterceptor(new UpLoadProgressInterceptor(uploadListener));
        if(downloadProgressListener != null)
            builder.addNetworkInterceptor(new DownloadProgressInterceptor(downloadProgressListener));
        if(FApplication.DEBUG) {
            builder.addInterceptor(new TimberLoggingInterceptor());
        }

        ignoreHttps(builder);
        //common okhttpclient
        if(accountService != null && uploadListener == null && downloadProgressListener == null)
            return okHttpClient = builder.build();
            //return new okhttpclient
        else
            return  builder.build();
    }

    public static synchronized OkHttpClient getClient(IAccountManager accountService) {
        return getClient(accountService, null, null);
    }
}
