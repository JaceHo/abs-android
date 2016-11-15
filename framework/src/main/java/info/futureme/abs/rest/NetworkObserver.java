package info.futureme.abs.rest;


import java.net.ConnectException;
import java.net.SocketTimeoutException;

import info.futureme.abs.R;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.util.DLog;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;


/**
 * network observer used to retrieve network response
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 15:53:50
 */
public abstract class NetworkObserver<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        DLog.p(e);
        if(e == null){
            onFailure(ContextManager.context().getString(R.string.unknown_error));
        }else{
            if (e instanceof SocketTimeoutException) {
                onFailure(ContextManager.context().getString(R.string.network_connection_timeout));
            } else if (e instanceof ConnectException) {
                onFailure(ContextManager.context().getString(R.string.network_not_stable));
            } else if (e instanceof HttpException) {
                //401 already handled
                if (((HttpException) e).code() != 401)
                    onFailure(e.getLocalizedMessage());
            } else {
                onFailure(ContextManager.context().getString(R.string.network_not_stable));
            }
        }
    }

    @Override
    public void onNext(T t) {
        if(t == null){
            onFailure(ContextManager.context().getString(R.string.unknown_error));
        }else{
            onSuccess(t);
        }
    }

    public abstract void onSuccess(T t);
    public abstract void onFailure(String tip);
}
