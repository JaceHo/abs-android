package info.futureme.abs.example.rest;

import info.futureme.abs.entity.FGson;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.rest.CountingRequestBody;
import info.futureme.abs.rest.DownloadProgressInterceptor;
import info.futureme.abs.rest.client.OneWayAuthHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * convenient factory method used for rest api service generation and to produce api proxy implementation
 */

public final class ServiceGenerator {

    // No need to instantiate this class.
    private ServiceGenerator() {
    }

    // service class is needed wherever using this class
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null, null, null);
    }

    //use this if you want replace baseurl(the prefix of the rest uri)
    //eg:  createService(UserAPI.class, "http://restapi2.com/app/");
    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl,null,null, null);
    }

    //use this if you only want to change converter adapter for a rest response
    //eg : GsonConvertFactory
    public static <S> S createService(Class<S> serviceClass, Converter.Factory factory) {
        return createService(serviceClass, null, factory, null, null);
    }

    //used to retrieve uploading progress for file io
    public static <S> S createService(Class<S> serviceClass, CountingRequestBody.Listener uploadListener) {
        return createService(serviceClass, null, null, uploadListener, null);
    }

    //used to retrieve downloading progress for file io
    public static <S> S createService(Class<S> serviceClass, DownloadProgressInterceptor.DownloadProgressListener downloadProgressListener) {
        return createService(serviceClass, null, null, null, downloadProgressListener);
    }

    //base method to combine components to produce rest api service need in this method
    public static <S> S createService(Class<S> serviceClass, String baseUrl,final Converter.Factory factory, CountingRequestBody.Listener uploadListener, DownloadProgressInterceptor.DownloadProgressListener downloadProgressListener) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl == null ? MVSConstants.APIConstants.APIServer_Address : baseUrl)
                .addConverterFactory(factory == null ? GsonConverterFactory.create(FGson.gson()) : factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(OneWayAuthHttpClient.getClient(AccountManagerImpl.instance, uploadListener, downloadProgressListener));
        Retrofit adapter = builder.build();
        return adapter.create(serviceClass);
    }

}
