/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android framework
 * Create Time: 16-2-16 下午6:51
 */

package info.futureme.abs.util.rx;

/**
 * Created by hippo on 1/31/16.
 */

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A {@linkplain Subscription subscription} which ensures its {@linkplain #onUnsubscribe()
 * unsubscribe action} is executed on the new thread. When unsubscription occurs on a different
 */
public abstract class NewThreadSubscription implements Subscription {

    private final AtomicBoolean unsubscribed = new AtomicBoolean();

    @Override public final boolean isUnsubscribed() {
        return unsubscribed.get();
    }

    @Override public final void unsubscribe() {
        if (unsubscribed.compareAndSet(false, true)) {
            Schedulers.newThread().createWorker().schedule(new Action0() {
                @Override public void call() {
                    onUnsubscribe();
                }
            });
        }
    }

    protected abstract void onUnsubscribe();
}
