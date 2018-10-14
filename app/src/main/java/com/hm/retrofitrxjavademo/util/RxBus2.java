package com.hm.retrofitrxjavademo.util;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 支持背压
 */
public class RxBus2 {

    private Relay<Object> bus = null;

    private RxBus2() {
        this.bus = PublishRelay.create().toSerialized();
    }

    public static RxBus2 get() {
        return Holder.BUS;
    }

    public void post(Object object) {
        bus.accept(object);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    public boolean hasObservables() {
        return bus.hasObservers();
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError, Action onComplete) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                   Consumer onError, Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext) {
        return toObservable(eventType).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext,
                                   Consumer onError) {
        return toObservable(eventType).subscribe(onNext, onError);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext,
                                   Consumer onError, Action onComplete) {
        return toObservable(eventType).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext,
                                   Consumer onError, Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public void unregister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static class Holder {
        private static final RxBus2 BUS = new RxBus2();
    }

}
