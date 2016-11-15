/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android framework
 * Create Time: 16-2-16 下午6:51
 */

package info.futureme.abs.util.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * courtesy: https://gist.github.com/benjchristensen/04eef9ca0851f3a5d7bf
 * rxbus is a simple implementation like otto, eventbus
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 17:59:07
 */
public class RxBus {

    //private final PublishSubject<Object> _bus = PublishSubject.create();

    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    private final Subject<Object, Object> __bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        _bus.onNext(o);
    }

    public void sendDebouncing(Object o) {
        __bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return _bus;
    }

    public Observable<Object> toDebouncingObserverable() {
        return __bus.debounce(100, TimeUnit.MILLISECONDS);
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }
}
