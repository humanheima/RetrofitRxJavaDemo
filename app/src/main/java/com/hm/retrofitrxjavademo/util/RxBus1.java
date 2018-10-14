package com.hm.retrofitrxjavademo.util;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 支持背压
 */
public class RxBus1 {

    private final FlowableProcessor<Object> mBus;

    private RxBus1() {
        this.mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus1 get() {
        return Holder.BUS;
    }

    public void post(Object object) {
        mBus.onNext(object);
    }

    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Flowable<Object> toFlowable() {
        return mBus;
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    private static class Holder {
        private static final RxBus1 BUS = new RxBus1();
    }


}
