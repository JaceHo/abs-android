package info.futureme.abs.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.baidu.location.BDLocation;

import java.util.concurrent.TimeUnit;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.rx.NewThreadSubscription;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class LocationEventsOnSubscribe implements Observable.OnSubscribe<BDLocation>{

    private ServiceConnection connection;
    private long scanSpan = 60*1000;
    private Scheduler.Worker worker;

    public LocationEventsOnSubscribe(){
        worker = Schedulers.newThread().createWorker();
    }

    public LocationEventsOnSubscribe(long span){
        setScanSpan(span);
        worker = Schedulers.newThread().createWorker();
    }

    @Override public void call(final Subscriber<? super BDLocation> subscriber) {
        try{
            Intent intent = new Intent(ContextManager.context(), LocationService.class);
            connection = LocationService.connection(new LocationService.LocationSuccessListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    subscriber.onNext(location);
                    if(connection != null) {
                        DLog.i("share location", "destory");
                        try {
                            ContextManager.context().unbindService(connection);
                        }catch (Exception e){}
                        worker.schedule(new Action0() {
                            @Override
                            public void call() {
                                if(connection != null) {
                                    Intent intent = new Intent(ContextManager.context(), LocationService.class);
                                    ContextManager.context().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                                }
                            }
                        }, getScanSpan(), TimeUnit.MILLISECONDS);
                    }
                }
            }, intent);
            ContextManager.context().bindService(intent, connection, Context.BIND_AUTO_CREATE);
            subscriber.add(new NewThreadSubscription() {
                protected void onUnsubscribe() {
                    if(connection != null) {
                        try {
                            ContextManager.context().unbindService(connection);
                            LocationEventsOnSubscribe.this.connection = null;
                        }catch (Exception e){}
                    }
                    worker.unsubscribe();
            }});
        }catch (Exception e){
            subscriber.onError(e);
            DLog.p(e);
        }
        // Emit initial value.
        //subscriber.onNext(new lat);
    }

    public void setScanSpan(long scanSpan) {
        this.scanSpan = scanSpan;
    }

    public long getScanSpan() {
        return scanSpan;
    }
}
