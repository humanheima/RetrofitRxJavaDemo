package com.hm.retrofitrxjavademo.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private final Subject<Object> mBus;

    private RxBus() {
        this.mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    public void post(Object object) {
        mBus.onNext(object);
    }

    public <T> Observable<T> tObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> tObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }


}
