package info.futureme.abs.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * cancelablecallback used to cancel enquene requests whenever possible
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 15:50:38
 */
public abstract class CancelableCallback<T> implements Callback<T> {

	/**
	 * currently pending async retrofit calls
	 */
    private static final List<CancelableCallback> mList = new CopyOnWriteArrayList<>();

    private boolean isCanceled = false;
	/**
	 * tag attached to call object used for canceling retrofit okhttp calls
	 */
    private Object mTag = null;
	/**
	 * call reference to call it's cancle() method
	 */
    private Call<T> mCall = null;

    public static void cancelAll() {
        for (CancelableCallback callback : mList) {
            callback.cancel();
        }
    }


    public static void cancel(Object tag) {
        if (tag != null)
            for (CancelableCallback callback : mList) {
                if (tag.equals(callback.mTag))
                    callback.cancel();
            }
    }

    public CancelableCallback() {
        mList.add(this);
    }

    public CancelableCallback(Call<T> call, Object tag) {
        mCall = call;
        mTag = tag;
        mList.add(this);
    }

    public synchronized void cancel() {
        isCanceled = true;
        if(mCall != null)
            mCall.cancel();
        mList.remove(this);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        mList.remove(this);
        if (!isCanceled) {
            if (response.body() == null) {
                if(response.errorBody() == null) {
                    onFailure(null, new Exception(response.code() + ":" + response.message()));
                }else {
                    String tip = "";
                    if(response.errorBody() != null){
                        try {
                            tip =  response.errorBody().string();
                        } catch (IOException e) {
                            tip = response.message();
                        }
                    }
                    onFailure(null, new Exception(response.code() + ":" + tip));
                }
            }else {
                onSuccess(response.body(), response);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!isCanceled)
            onFail(t.getMessage());
        mList.remove(this);
    }

    public abstract void onSuccess(T t, Response response);
    public abstract void onFail(String tip);
}
