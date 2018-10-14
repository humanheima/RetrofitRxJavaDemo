package com.hm.retrofitrxjavademo.util;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 支持背压
 */
public class RxBus3 {

    private final Map<Class<?>, Object> mStickyEventMap;
    private Relay<Object> bus = null;

    private RxBus3() {
        this.bus = PublishRelay.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RxBus3 get() {
        return Holder.BUS;
    }

    public void post(Object object) {
        bus.accept(object);
    }

    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        bus.accept(event);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    public <T> Observable<T> toObservableSticky(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = bus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);
            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                        emitter.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
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

    /***********************************/

    public <T> Disposable registerSticky(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext) {
        return toObservableSticky(eventType).observeOn(scheduler).subscribe(onNext);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                         Consumer onError) {
        return toObservableSticky(eventType).observeOn(scheduler).subscribe(onNext, onError);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                         Consumer onError, Action onComplete) {
        return toObservableSticky(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext,
                                         Consumer onError, Action onComplete, Consumer onSubscribe) {
        return toObservableSticky(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext) {
        return toObservableSticky(eventType).subscribe(onNext);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext,
                                         Consumer onError) {
        return toObservableSticky(eventType).subscribe(onNext, onError);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext,
                                         Consumer onError, Action onComplete) {
        return toObservableSticky(eventType).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext,
                                         Consumer onError, Action onComplete, Consumer onSubscribe) {
        return toObservableSticky(eventType).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> T removeStickyEvent(Class<T> evenType) {
        synchronized (mStickyEventMap) {
            return evenType.cast(mStickyEventMap.remove(evenType));
        }
    }

    public <T> void removeAllStickyEvent(Class<T> evenType) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

    public void unregister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static class Holder {
        private static final RxBus3 BUS = new RxBus3();
    }

}
