package com.hm.retrofitrxjavademo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityRxJava2Binding;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 参考链接： https://www.jianshu.com/p/464fa025229e
 */
public class RxJava2Activity extends BaseActivity<ActivityRxJava2Binding> {

    private static final String TAG = "RxJava2Activity";
    private Subscription mSubscription;
    private int retryTimes = 5;
    private int rangeCount = 5;

    public static void launch(Context context) {
        Intent intent = new Intent(context, RxJava2Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_rx_java2;
    }

    @Override
    protected void initData() {

    }

    @SuppressLint("CheckResult")
    public void testRetry(View view) {
        testRetry();
    }

    private void testRetry() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                /*emitter.onNext(number);
                emitter.onComplete();*/
                retryTimes--;
                if (retryTimes > 0) {
                    emitter.onError(new RuntimeException("always fails"));
                } else {
                    //emitter.onError(new Throwable("last fails"));
                    emitter.onNext(100);
                    emitter.onComplete();
                }
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.zipWith(Observable.range(1, rangeCount), new BiFunction<Throwable, Integer, Boolean>() {
                    @Override
                    public Boolean apply(Throwable throwable, Integer integer) throws Exception {
                        boolean b = throwable instanceof RuntimeException;
                        Log.d(TAG, "apply: throwable:" + throwable.getMessage() + ",:" + b);
                        return b;
                    }
                }).flatMap(new Function<Boolean, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Boolean aBoolean) throws Exception {
                        //如果这个Observable发射了一项数据，它就重新订阅，如果这个Observable发射的
                        // 是onError通知，它就将这个通知传递给观察者然后终止。
                        if (aBoolean) {
                            //重新订阅
                            return Observable.timer(1, TimeUnit.SECONDS);
                        } else {
                            //给观察者传递一个throwable，然后终止
                            return Observable.error(new Throwable("test retryWhen"));
                        }
                    }
                });
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "accept: integer:" + integer);

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "accept: throwable:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    public void fun1(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext() called with: value = [" + value + "]");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError() called with: e = [" + e + "]");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() called");
            }
        });
    }

    /**
     * {@link Disposable}
     * 调用 Disposable的dispose方法以后，会导致下游收不到事件，但是上游会继续发送剩余的事件.
     *
     * @param view
     */
    public void testDisposable(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
                Log.e(TAG, "emit 2");
                emitter.onNext(2);
                Log.e(TAG, "emit 3");
                emitter.onNext(3);
                Log.e(TAG, "emit complete");
                emitter.onComplete();
                Log.e(TAG, "emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {

            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "subscribe");
                compositeDisposable.add(d);
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: " + integer);
                i++;
                if (i == 2) {
                    Log.e(TAG, "dispose");
                    mDisposable.dispose();
                    Log.e(TAG, "isDisposed : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }

    public void testSubscribe(View view) {
      /*  Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onError(new IllegalArgumentException("testSubscribe"));
            }
        }).subscribe();*/
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onError(new IllegalArgumentException("testSubscribe"));
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept integer=" + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        });
    }

    public void testMap(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "string:" + integer;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, s);
                    }
                });
    }

    public void testFlatMap(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }

    public void testZip(View view) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
                Log.e(TAG, "emit 2");
                emitter.onNext(2);
                Log.e(TAG, "emit 3");
                emitter.onNext(3);
                Log.e(TAG, "emit 4");
                emitter.onNext(4);
                Log.e(TAG, "emit complete1");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "emit A");
                emitter.onNext("A");
                Log.e(TAG, "emit B");
                emitter.onNext("B");
                Log.e(TAG, "emit C");
                emitter.onNext("C");
                Log.e(TAG, "emit complete2");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
                Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.e(TAG, "onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });

    }

    public void testFlowable(View view) {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        Log.d(TAG, "First requested = " + emitter.requested());
                        boolean flag;
                        for (int i = 0; ; i++) {
                            flag = false;
                            while (emitter.requested() == 0) {
                                if (!flag) {
                                    Log.d(TAG, "Oh no! I can't emit value!");
                                    flag = true;
                                }
                            }
                            emitter.onNext(i);
                            Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
                        }
                    }
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    public void request(View view) {
        mSubscription.request(95);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
