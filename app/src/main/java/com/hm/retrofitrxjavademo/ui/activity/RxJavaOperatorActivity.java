package com.hm.retrofitrxjavademo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityRxJavaOperatorBinding;
import com.hm.retrofitrxjavademo.model.Animal;
import com.hm.retrofitrxjavademo.model.Dog;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Observable.just;

/**
 * RxJava 操作符
 */

@SuppressWarnings("unchecked")
@SuppressLint("CheckResult")

public class RxJavaOperatorActivity extends BaseActivity<ActivityRxJavaOperatorBinding> {

    public final static String TAG = "RxJavaOperatorActivity";
    /**
     * defer 操作符，just操作符是在创建Observable就进行了赋值操作，
     * 而defer是在订阅者订阅时才创建Observable，此时才进行真正的赋值操作
     */

    //初始的时候的时候i=10
    int i = 10;
    private Observer<Integer> observer;
    private Observer<Long> longObserver;
    /**
     * concat 连接多个操作符，只有在前一个observable onCompleted以后，下一个observable才会开始发射数据
     * ,如果前一个observable onError，那么这个错误会立即传递给观察者。
     * 我们可以利用这个特性来实现网络请求的优化，memoryCache ,diskCache,network
     * the link
     * https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Mathematical.html
     */
    private String concatData[] = {"memoryCache", null, "network"};
    /**
     * defer直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable.
     * 而just操作符是在创建Observable就进行了赋值操作
     */
    private int deferValue = 1;
    /**
     * buffer操作符把一个Observable 变换成另外一个，原来的Observable正常发射数据，
     * 变换后的Observable发射这些数据的缓存集合，订阅者处理后，清空buffer列表，
     * 同时接收下一次收集的结果并提交给订阅者，周而复始。如果原来的Observable
     * 发射了一个onError通知， buffer 会立即传递这个通知，而不是首先发射缓存的数据，
     * 即使在这之前，缓存中有来自原来的Observable 的数据，也不会发射出去。
     */
    private int num = 1;
    private String[] mails = new String[]{"Here is an email!", "Another email!", "Yet another email!"};

    public static void launch(Context context) {
        Intent intent = new Intent(context, RxJavaOperatorActivity.class);
        context.startActivity(intent);
    }

    public static <String> ObservableTransformer<Integer, java.lang.String> transformer() {
        return new ObservableTransformer<Integer, java.lang.String>() {
            @Override
            public ObservableSource<java.lang.String> apply(Observable<Integer> upstream) {
                return upstream.map(new Function<Integer, java.lang.String>() {
                    @Override
                    public java.lang.String apply(Integer integer) throws Exception {
                        return java.lang.String.valueOf(integer);
                    }
                });
            }
        };
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_rx_java_operator;
    }

    @Override
    protected void initData() {
        observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer s) {
                Log.e(TAG, "onNext: s=" + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };
        longObserver = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_parallel:
                parallel();
                break;
            case R.id.btn_transform:
                transform();
                break;
            case R.id.btn_ref_count:
                refCount();
                break;
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_join:
                join();
                break;
            case R.id.btn_take_while:
                takeWhile();
                break;
            case R.id.btn_take_until:
                takeUntil();
                break;
            case R.id.btn_skip_until:
                skipUntil();
                break;
            case R.id.btn_skip_while:
                skipWhile();
                break;
            case R.id.btn_amb:
                amb();
                break;
            case R.id.btn_sequence_equal:
                sequenceEqual();
                break;
            case R.id.btn_default_if_empty:
                defaultIfEmpty();
                break;
            case R.id.btn_create:
                create();
                break;
            case R.id.btn_take:
                take();
                break;
            case R.id.btn_defer:
                defer();
                break;
            case R.id.btn_from:
                from();
                break;
            case R.id.btn_interval:
                interval();
                break;
            case R.id.btn_range:
                range();
                break;
            case R.id.btn_repeat:
                repeat();
                break;
            case R.id.btn_repeat_when:
                repeatWhen();
                break;
            case R.id.btn_retry_when:
                retryWhen();
                break;
            case R.id.btn_buffer:
                buffer();
                break;
            case R.id.btn_window:
                window();
                break;
            case R.id.btn_flatMap:
                flatMap();
                break;
            case R.id.btn_group_by:
                groupBy();
                break;
            case R.id.btn_scan:
                scan();
                break;
            case R.id.btn_debounce:
                debounce();
                break;
            case R.id.btn_sample:
                sample();
                break;
            case R.id.btn_skip:
                skip();
                break;
            case R.id.btn_skip_last:
                skipLast();
                break;
            case R.id.btn_combine_latest:
                combineLatest();
                break;
            case R.id.btn_zip:
                zip();
                break;
            case R.id.btn_use_catch:
                useCatch();
                break;
            case R.id.btn_retry:
                retry();
                break;
            case R.id.btn_delay:
                delay();
                break;
            case R.id.btn_do:
                userDo();
                break;
            case R.id.btn_to:
                userTo();
                break;
            case R.id.btn_reduce:
                reduce();
                break;
            case R.id.btn_concat:
                concat();
                break;
            case R.id.btn_throttle_with_timeout:
                throttleWithTimeOut();
                break;
            default:
                break;
        }
    }

    private void parallel() {
        ParallelFlowable<Integer> parallelFlowable = Flowable.range(1, 100).parallel();
        parallelFlowable.runOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer.toString();
                    }
                })
                .sequential()
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });
    }

    private void transform() {
        Observable.just(123, 456)
                .compose(transformer())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: s= " + s);
                    }
                });
    }


    private void refCount() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(6);

        ConnectableObservable<Long> connectableObservable = obs.publish();
        Observable obsRefCount = connectableObservable.refCount();

        connectableObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "subscriber1: onNext" + aLong + "->time:" + sdf.format(new Date()));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "subscriber1: onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "subscriber1: onComplete");
            }
        });
        connectableObservable.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "subscriber2: onNext" + aLong + "->time:" + sdf.format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "subscriber2: onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "subscriber1: onComplete");
                    }
                });
        obsRefCount.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "obsRefCount1:onNext: " + aLong + "->time:" + sdf.format(new Date()));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "obsRefCount1:onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "obsRefCount1:onComplete: ");
            }
        });
        obsRefCount.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "obsRefCount2:onNext: " + aLong + "->time:" + sdf.format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "obsRefCount2:onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "obsRefCount2:onComplete: ");
                    }
                });
        connectableObservable.connect();
    }

    private void connect() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(6);

        ConnectableObservable<Long> connectableObservable = obs.publish();
        connectableObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "subscriber1: onNext" + aLong + "->time:" + sdf.format(new Date()));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "subscriber1: onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "subscriber1: onComplete");
            }
        });
        connectableObservable.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "subscriber2: onNext" + aLong + "->time:" + sdf.format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "subscriber2: onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "subscriber1: onComplete");
                    }
                });
        connectableObservable.connect();
    }

    private void join() {
        Observable<Integer> o1 = Observable.just(1, 2, 3).delay(200, TimeUnit.MILLISECONDS);
        Observable<Integer> o2 = Observable.just(4, 5, 6);
        o1.join(o2, new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS);
            }
        }, new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS);
            }
        }, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return integer + ":" + integer2;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "accept: " + s);
            }
        });
    }

    private void skipWhile() {
        Observable.just(1, 2, 3, 4, 5)
                .skipWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer aLong) throws Exception {
                        return aLong <= 2;
                    }
                })
                .subscribe(observer);
    }

    private void takeWhile() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .takeWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer <= 4;
                    }
                })
                .subscribe(observer);

    }

    private void takeUntil() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer == 5;
                    }
                })
                .subscribe(observer);

    }

    private void skipUntil() {
        Observable.intervalRange(1, 9, 0, 1, TimeUnit.MILLISECONDS)
                .skipUntil(Observable.timer(4, TimeUnit.MILLISECONDS))
                .subscribe(longObserver);
    }

    private void sequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3, 4, 5), Observable.just(1, 2, 3, 4))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG, "accept: " + aBoolean);
                    }
                });
    }

    private void defaultIfEmpty() {
        /*Observable.empty().defaultIfEmpty(8)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.d(TAG, "accept: " + o);
                    }
                });*/
        Observable.empty().switchIfEmpty(Observable.just(1, 2, 3))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.d(TAG, "accept: " + o);
                    }
                });
    }

    private void amb() {
        Observable.ambArray(Observable.just(1, 2, 3).delay(1, TimeUnit.SECONDS), Observable.just(4, 5, 6))
                .subscribe(observer);
    }

    private void concat() {
       /* Observable<Integer> ob1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                //emitter.onError(new Throwable("First observable onError"));
                emitter.onComplete();
            }
        });
        Observable<Integer> ob2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onNext(6);
            }
        });
        Observable.concat(ob1, ob2)
                .subscribe(observer);*/
        //网络请求的例子
        Observable<String> memoryCache = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = concatData[0];
                Log.e(TAG, "subscribe: memoryCache");
                if (null == s) {
                    //没有内存缓存
                    emitter.onComplete();
                } else {
                    emitter.onNext(s);
                }
            }
        });
        Observable<String> diskCache = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "subscribe: diskCache");
                String s = concatData[1];
                if (null == s) {
                    //没有硬盘缓存
                    emitter.onComplete();
                } else {
                    emitter.onNext(s);
                }
            }
        });
        Observable<String> network = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "subscribe: network");
                String s = concatData[2];
                emitter.onNext(s);
                emitter.onComplete();
            }
        });
        Observable.concat(memoryCache, diskCache, network)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: s=" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    private void reduce() {
       /* Observable.just(1, 2, 3, 4)
                .reduce(1,new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "apply: integer ,integer2 are " + integer + "," + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: integer=" + integer);
                    }
                });*/
        Observable.just(1, 2, 3, 4)
                .reduceWith(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return 10;
                    }
                }, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "apply: integer ,integer2 are " + integer + "," + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: integer=" + integer);
                    }
                });
    }

    private void userTo() {
        try {
            int a = Observable.just(1)
                    .toFuture()
                    .get();
            Log.e(TAG, "userTo: a=" + a);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void userDo() {
        Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer > 2) {
                            throw new RuntimeException("test doOnNext");
                        }
                    }
                })
                .subscribe(observer);
    }

    /**
     * Sample操作符定时查看一个Observable，然后发射自上次采样以来它最近发射的数据。
     * 在这个例子中，每隔5秒采样一次
     */
    private void sample() {
       /* Observable.interval(2, TimeUnit.SECONDS)
                .take(10)
                .sample(5, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: aLong=" + aLong);
                    }
                });*/
        //sample的这个变体每当第二个Observable发射一个数据（或者当它终止）时就对原始Observable进行采样
        Observable.interval(2, TimeUnit.SECONDS)
                .take(10)
                .sample(Observable.interval(5, TimeUnit.SECONDS))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: aLong=" + aLong);
                    }
                });
    }

    private void retryWhen() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new RuntimeException("always fails"));
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.zipWith(Observable.range(1, 3), new BiFunction<Throwable, Integer, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable, Integer integer) throws Exception {
                        return integer;
                    }
                }).flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        Log.e(TAG, "apply: retryWhen integer=" + integer);
                        //如果这个Observable发射了一项数据，它就重新订阅，如果这个Observable发射的
                        // 是onError通知，它就将这个通知传递给观察者然后终止。
                        if (integer < 3) {
                            //重新订阅
                            return Observable.timer(integer, TimeUnit.SECONDS);
                        } else {
                            //给观察者传递一个throwable，然后终止
                            return Observable.error(new Throwable("test retryWhen"));
                        }
                    }
                });
            }
        }).subscribe(observer);


      /*  Observable.create((ObservableEmitter<String> e) ->
                e.onError(new RuntimeException("always fails")))
                .retryWhen(throwableObservable ->
                        throwableObservable.zipWith(Observable.range(1, 3), (t, integer) -> integer)
                                .flatMap(integer -> {
                                    Log.e(TAG, "delay retryWhen the " + integer + " count");
                                    //i秒钟重复一次
                                    return Observable.timer(integer, TimeUnit.SECONDS);
                                }))
                .subscribe(s -> Log.e(TAG, "onNext:" + s),
                        e -> Log.e(TAG, "onError:" + e));*/
    }

    /**
     * 有条件的重新订阅和发射原来的Observable
     */
    private void repeatWhen() {
        Observable.range(1, 3)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        return objectObservable.zipWith(Observable.range(0, 4), new BiFunction<Object, Integer, Object>() {
                            @Override
                            public Object apply(Object o, Integer integer) throws Exception {
                                Log.e(TAG, "apply: integer=" + integer);
                                return Observable.interval(integer, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "accept: integer=" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "accept: error" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
               /* .repeatWhen(objectObservable -> objectObservable.zipWith(Observable.range(1, 3), (o, integer) -> integer))
                .flatMap(integer -> {
                    Log.e(TAG, "delay repeat the " + integer + " count");
                    //1秒钟重复一次
                    return Observable.timer(1, TimeUnit.SECONDS);
                })
                .subscribe(integer -> Log.e(TAG, "onNext" + integer),
                        e -> Log.e(TAG, e.getMessage()));*/
    }

    public void repeat() {
        Observable.just(1, 2, 3, 4)
                .repeat(2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "repeat" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * interval操作符是每隔一段时间就产生一个数字，这些数字从0开始，递增直至无穷大
     */
    public void interval() {
        Observable.interval(2, 2, TimeUnit.SECONDS, Schedulers.io())
                .take(5)//最多输出5个
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "interval:" + aLong);
                    }
                });
    }

    /**
     * range操作符
     * range操作符是创建一组在从n开始，个数为m的连续数字，比如range(3,10)，就是创建3、4、5…12的一组数字，
     */
    public void range() {
        Observable.range(3, 10)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept" + integer);
                    }
                });
    }

    private void create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "integer=" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }

    private void take() {
        Observable.intervalRange(0, 10, 1, 1, TimeUnit.SECONDS)
                .take(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Long integer) {
                        Log.e(TAG, "integer=" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    //变换操作符

    private void takeLast() {
        Observable.just(1, 2, 3, 4, 50)
                .takeLast(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "integer=" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    private void defer() {
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(deferValue);
            }
        });
        deferValue = 2;
        observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: integer=" + integer);
            }
        });
        deferValue = 3;
        observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: integer=" + integer);
            }
        });
    }

    private void from() {
        String[] words = {"Hello", "Hi", "Aloha"};
        Observable.fromArray(words)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: s=" + s);
                    }
                });
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("Hi");
        list.add("Aloha");
        Observable.fromIterable(list)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: s=" + s);
                    }
                });
        FutureTask<Integer> future = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.e(TAG, "call: current Thread is" + Thread.currentThread().getName());
                Thread.sleep(1000);
                return 1;
            }
        });
        new Thread(future).start();

        Observable.fromFuture(future, 3000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: integer =" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: error:" + throwable);
                    }
                });
    }

    public void buffer() {
        //buffer(ObservableSource<B> boundary, final int initialCapacity)
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(100)
                //第二个参数 initialCapacity 表示返回的List的初始容量
                .buffer(Observable.interval(250, TimeUnit.MILLISECONDS), 6)
                .subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(List<Long> longs) throws Exception {
                        Log.e(TAG, "accept: ");
                        for (Long integer : longs) {
                            Log.e(TAG, "accept: integer=" + integer);
                        }
                    }
                });
       /* //buffer(int count),buffer(int count, Callable<U> bufferSupplier)
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .buffer(2, new Callable<LinkedList<Integer>>() {

                    @Override
                    public LinkedList<Integer> call() throws Exception {
                        return new LinkedList<Integer>();
                    }
                })
                .subscribe(new Consumer<LinkedList<Integer>>() {
                    @Override
                    public void accept(LinkedList<Integer> integers) throws Exception {
                        Log.e(TAG, "accept: ");
                        for (Integer integer : integers) {
                            Log.e(TAG, "accept: integer=" + integer);
                        }
                    }
                });*/

       /* //buffer(int count, int skip),从原始Observable中每缓存skip个item，从中选择最多count个数据发
        // 射。如果从原始Observable缓存的数据不到skip个，就遇到onCompleted(),则发射当前缓存的数据。
        // 如果从原始Observable缓存的数据不到skip个，就遇到onError(),如果当前缓存的数据够count个，
        // 则发射这组数据，并传递错误通知。如果当前缓存的数据不够count个，则不发射当前缓存的数据，
        // 直接传递错误通知
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int j = 1; j <= 11; j++) {
                    emitter.onNext(j);
                    if (j == 10) {
                        emitter.onError(new Throwable("emit a error"));
                    }
                }
                emitter.onComplete();
            }
        }).buffer(2, 4)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e(TAG, "accept: ");
                        for (Integer integer : integers) {
                            Log.e(TAG, "accept: integer=" + integer);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: " + throwable);
                    }
                });*/

      /*  Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (e.isDisposed()) {
                    return;
                }
                Random ran = new Random();
                while (true) {
                    String mail = mails[ran.nextInt(mails.length)];
                    e.onNext(mail);
                    if (num == 8) {
                        e.onError(new Throwable("故意出错"));
                    }
                    num++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        e.onError(ex);
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .buffer(3, TimeUnit.SECONDS)
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> list) {
                        Log.e(TAG, String.format("You've got %d new messages!  Here they are!", list.size()));
                        for (int i = 0; i < list.size(); i++)
                            Log.e(TAG, list.get(i));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
*/
    }

    private void window() {
        Observable.range(1, 10)
                .window(2)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Exception {
                        Log.d(TAG, "accept: onNext");
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d(TAG, "accept: onNext:" + integer);
                            }
                        });

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "run: onCompleted");
                    }
                });
    }

    private void useStart() {
        just(1, 2, 4)
                .startWith(just(9, 8, 7))
                .subscribe(integer -> Log.e(TAG, "useStart:" + integer),
                        e -> Log.e(TAG, "useStart onError"),
                        () -> Log.e(TAG, "useStart onCompleted"));
        //输出结果
        /**
         useStart:9
         useStart:8
         useStart:7
         useStart:1
         useStart:2
         useStart:4
         useStart onCompleted
         */
    }

    public void never() {
        Observable
                .never()
                .subscribe(object -> Log.e(TAG, "useStart:" + object.toString()),
                        e -> Log.e(TAG, "useStart onError"),
                        () -> Log.e(TAG, "useStart onCompleted"));
        //什么也不输出
    }

    public void useError() {
        Observable
                .error(new Throwable("使用error"))
                .subscribe(o -> Log.e(TAG, "useError onNext"),
                        e -> Log.e(TAG, "useError onError"),
                        () -> Log.e(TAG, "onCompleted"));
        //输出 useError onError
    }

    private void delay() {
        Observable.just(1, 2, 3, 4)
                //.delay(2, TimeUnit.SECONDS)
                //.delaySubscription(2, TimeUnit.SECONDS)
                .delaySubscription(new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        //如果这个observer不发射数据，或者onComplete,那么订阅就不会发生
                        observer.onComplete();
                    }
                })
                .subscribe(observer);
    }

    /**
     * flatMap() 的原理是这样的：
     * 1. 使用传入的事件对象创建一个 Observable 对象；
     * 2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
     * 3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，
     * 而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。
     * 这三个步骤，把事件拆成了两级，
     * 通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去
     */
    private void flatMap() {
        List<Integer> integerList1 = new ArrayList<>();
        integerList1.add(1);
        integerList1.add(2);

        List<Integer> integerList2 = new ArrayList<>();
        integerList2.add(3);
        integerList2.add(4);
        integerList2.add(5);

        List<Integer> integerList3 = new ArrayList<>();
        integerList3.add(6);
        integerList3.add(7);
        integerList3.add(8);
        List<Integer> integerList4 = new ArrayList<>();
        integerList4.add(9);
        integerList4.add(10);

        List<List> listList = new ArrayList<>();
        listList.add(integerList1);
        listList.add(integerList2);
        listList.add(integerList3);
        listList.add(integerList4);

        Observable.fromIterable(listList)
                //发射4个list
                .flatMap(new Function<List, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(List list) throws Exception {
                        //最终将4个list中的数据汇集到一个list中，先后发送数据
                        return Observable.fromIterable(list);
                    }
                })
                .subscribe(observer);
    }

    private void flatMapIterable() {
        Observable.just(1, 2, 3, 5)
                .flatMapIterable(new Function<Integer, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> apply(Integer integer) throws Exception {
                        List<Integer> integerList = new ArrayList<>();
                        integerList.add(integer);
                        integerList.add(integer + 100);
                        return integerList;
                    }
                })
                .subscribe(observer);
    }

    /**
     * concatMap操作符
     * cancatMap操作符与flatMap操作符类似，都是把Observable产生的结果转换成多个Observable，
     * 然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
     * 与flatMap操作符不同的是，concatMap操作符在处理产生的Observable时，
     * 采用的是“连接(concat)”的方式，而不是“合并(merge)”的方式，这就能保证产生结果的顺序性，
     * 也就是说提交给订阅者的结果是按照顺序提交的，不会存在交叉的情况。
     */
    private void contactMap() {
        // Observable<File> fileObservable = listFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        Observable<File> fileObservable = listFiles(new File("hehe"));

        fileObservable
                .subscribe(file -> Log.e(TAG, file.getPath() + file.getName()),
                        e -> Log.e(TAG, "onError:" + e.getMessage()));

    }

    /**
     * GroupBy操作符将原始Observable分拆为一些Observables集合，它们中的每一个发射原始Observable
     * 数据序列的一个子序列。哪个数据项由哪一个Observable发射是由函数getKey 判定的，
     * 这个函数给每一项指定一个Key，Key相同的数据会被同一个Observable发射。
     */
    public void groupBy() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(10)
                .groupBy(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong % 2;
                    }
                }, new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return 100 + aLong;
                    }
                })
                .subscribe(new Consumer<GroupedObservable<Long, Long>>() {
                    @Override
                    public void accept(GroupedObservable<Long, Long> result) throws Exception {
                        result.subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.e(TAG, "key:" + result.getKey() + ", value:" + aLong);
                            }
                        });
                    }
                });
    }

    private Observable<File> listFiles(File f) {
        if (!f.exists()) {
            return Observable.error(new Throwable("指定目录不存在！"));
        } else {
            if (f.isDirectory()) {
                return Observable.fromArray(f.listFiles())
                        .concatMap(this::listFiles);
            } else {
                return just(f);
            }
        }
    }

    private void retry() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 3; i++) {
                    if (i == 1) {
                        Log.e(TAG, "retry");
                        emitter.onError(new RuntimeException("always fails"));
                    } else {
                        emitter.onNext(i);
                    }
                }
                emitter.onComplete();
            }
        })
                .retry(2)//重试两次
                .subscribe(observer);

         /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 3; i++) {
                    if (i == 1) {
                        Log.e(TAG, "③retry(Func2)->onError");
                        emitter.onError(new RuntimeException("always fails"));
                    } else {
                        emitter.onNext(i);
                    }
                }
                emitter.onComplete();
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                //参数integer表示重试的次数
                Log.e(TAG, "③发生错误了：" + throwable.getMessage() + ",第" + integer + "次重新订阅");
                if (integer > 2) {
                    return false;//不再重新订阅
                }
                //此处也可以通过判断throwable来控制不同的错误不同处理
                return true;
            }
        })
                .subscribe(observer);*/
    }

    /**
     * RxJava 的基本实现主要有三点：
     * 11) 创建 Observer 即观察者，它决定事件触发的时候将有怎样的行为
     * 除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。
     * Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的：
     * 2) 创建 Observable
     * Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。
     * 3) Subscribe (订阅)
     * 创建了 Observable 和 Observer 之后，再用 subscribe() 方法将它们联结起来，整条链子就可以工作了
     */
    private void fun1() {
        //2 创建被观察者的几种方式
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onComplete();
            }
        });
        //just(T...): 将传入的参数依次发送出来。
        Observable observable1 = just("hello", "hi", "world");
        String[] words = {"Hello", "Hi", "Aloha"};
        Observable observable2 = Observable.fromArray(words);
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("Hi");
        list.add("Aloha");
        Observable observable3 = Observable.fromIterable(list);
    }

    /**
     * 线程控制 —— Scheduler
     * 在不指定线程的情况下， RxJava 遵循的是线程不变的原则，
     * 即：在哪个线程调用 subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。
     * 如果需要切换线程，就需要用到 Scheduler （调度器）。
     * 在RxJava 中，Scheduler ——调度器，相当于线程控制器，RxJava 通过它来指定每一段代码应该运行在什么样的线程。
     * RxJava 已经内置了几个 Scheduler ，它们已经适合大多数的使用场景：
     * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
     * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，
     * 区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，
     * 因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。
     * 这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
     * 有了这几个 Scheduler ，就可以使用 subscribeOn() 和 observeOn() 两个方法来对线程进行控制了。
     * subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
     * * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
     */

    public void example3() {
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, integer.toString());
                    }
                });

        /**
         * 上面这段代码中，由于 subscribeOn(Schedulers.io()) 的指定，被创建的事件的内容 1、2、3、4 将会在 IO 线程发出；
         * 而由于 observeOn(AndroidScheculers.mainThread()) 的指定，因此 subscriber 数字的打印将发生在主线程 。
         * 事实上，这种在 subscribe() 之前写上两句 subscribeOn(Scheduler.io()) 和 observeOn(AndroidSchedulers.mainThread()) 的使用方式非常常见，
         * 它适用于多数的 『后台线程取数据，主线程显示』的程序策略。
         */

        /**
         * 而前面提到的由图片 id 取得图片并显示的例子，如果也加上这两句：
         * 那么，加载图片将会发生在 IO 线程，而设置图片则被设定在了主线程。
         * 这就意味着，即使加载图片耗费了几十甚至几百毫秒的时间，也不会造成丝毫界面的卡顿。
         */
        final int resId = R.mipmap.ic_launcher;
        final ImageView imageView = new ImageView(this);
        Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(ObservableEmitter<Drawable> e) throws Exception {
                Drawable drawable = getResources().getDrawable(resId);
                e.onNext(drawable);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Drawable value) {
                        imageView.setImageDrawable(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列
     */
    public void map() {
        //一个map的例子
        // map() 方法将参数中的 String 对象转换成一个 Bitmap 对象后返回，
        // 而在经过 map() 方法后，事件的参数类型也由 String 转为了 Bitmap。
        final ImageView imageView = new ImageView(this);
        Observable.just("images/logo.png")// 输入类型 String
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return getBitMapFromPath(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    /**
     * 获取图片
     *
     * @param filePath
     * @return
     */
    private Bitmap getBitMapFromPath(String filePath) {
        return null;
    }

    /**
     * 5. 线程控制：Scheduler (二)
     * 除了灵活的变换，RxJava 另一个牛逼的地方，就是线程的自由控制。
     * observeOn() 指定的是它之后的操作所在的线程。
     * 因此如果有多次切换线程的需求，只要在每个想要切换线程的位置调用一次 observeOn()
     */

    public void changeScheduler(View view) {
        Observer<String> subscriber = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "主线程" + s);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        just("hello", "world", "hi")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return "新线程1" + s;
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return "新线程2" + s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * doOnSubscribe执行的线程不是subscriber执行所在的线程
     * 默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，
     * 它将执行在离它最近的 subscribeOn() 所指定的线程。
     * 下面的的代码如果运行注释的代码会发现progressBar不能显示，因为subscribe()所发生的线程是一个new Thread 不是主线程
     */
    public void useDoOnSubscribe() {
        just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //progressBar.setVisibility(View.VISIBLE);//需要在主线程执行
                        Log.e(TAG, "doOnSubscribe ,thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doAfterTerminate thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "onNext" + integer + ",thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                    }
                });
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                Observable.just(1, 2, 3, 4)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                progressBar.setVisibility(View.VISIBLE);//需要在主线程执行
                                Log.e(TAG, "doOnSubscribe ,thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.getMessage());
                            }

                            @Override
                            public void onNext(Integer integer) {
                                Log.e(TAG, "onNext" + integer + ",thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                            }
                        });
            }
        }).start();*/
    }

    public void compareJustAndDefer() {
        Observable<Integer> justObservable = just(i);
        i = 12;
        Observable<Integer> deferObservable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return just(i);
            }
        });
        i = 15;
        justObservable.subscribe(integer -> {
            //输出结果 10
            Log.e(TAG, "justObservable i=" + integer);
        });
        deferObservable.subscribe(integer -> {
            //输出结果 15
            Log.e(TAG, "deferObservable i=" + integer);
        });
    }

    /**
     * timer 操作符 延迟产生一个数字就结束，
     * app 从欢迎页，2秒后自动跳转到主页面
     */
    public void delayStartAct() {
        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        startActivity(new Intent(RxJavaOperatorActivity.this, MainActivity.class));
                        finish();
                        return null;
                    }
                }).subscribe();
    }


    private Observable<ImageView> loadImgFromLocal() {
        return null;
    }

    private Observable<ImageView> loadImgFromNet() {
        return null;
    }


    /**
     * cast操作符
     * cast操作符类似于map操作符，不同的地方在于map操作符可以通过自定义规则，
     * 把一个值A1变成另一个值A2，A1和A2的类型可以一样也可以不一样；
     * 而cast操作符主要是做类型转换的，传入参数为类型class，
     * 如果源Observable产生的结果不能转成指定的class，则会抛出ClassCastException运行时异常。
     */
    public void cast() {
        Animal animalTong = new Dog(1, "通狗");
        Animal animalHui = new Dog(1, "jingba");
        List<Animal> animals = new ArrayList<>();
        animals.add(animalTong);
        animals.add(animalHui);
        Observable
                .fromIterable(animals)
                .cast(Dog.class)
                .subscribe(new Consumer<Dog>() {
                    @Override
                    public void accept(Dog dog) throws Exception {
                        Log.e(TAG, dog.getHead() + "," + dog.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    /**
     * scan操作符
     * scan操作符通过遍历源Observable产生的结果，
     * 依次对每一个结果项按照指定规则进行运算，
     * 计算后的结果作为下一个迭代项参数，每一次迭代项都会把计算结果输出给订阅者。
     */
    public void scan() {
        Observable.just(1, 2, 3, 4, 5)
                .scan(10, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        //参数sum就是上一次的计算结果
                        Log.e(TAG, "apply: integer=" + integer + ",integer2=" + integer2);
                        return integer + integer2;
                    }
                }).subscribe(observer);
    }


    /**
     * debounce操作符对源Observable每产生一个结果后，如果在规定的间隔timeout时间内没有别的结果产生，
     * 则把这个结果提交给订阅者处理，否则忽略该结果。
     * 值得注意的是，如果源Observable产生的最后一个结果后在规定的时间间隔内调用了onCompleted
     * ，那么通过debounce操作符也会把这个结果提交给订阅者。
     */
    private void debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> subscriber) throws Exception {
                try {
                    for (int i = 1; i < 8; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(i * 100);
                    }
                    subscriber.onComplete();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(observer);
    }

    private void throttleWithTimeOut() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> subscriber) throws Exception {
                try {
                    for (int i = 1; i < 8; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(i * 100);
                    }
                    subscriber.onComplete();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .throttleWithTimeout(400, TimeUnit.MILLISECONDS)
                .subscribe(observer);
    }

    /**
     * distinct操作符对源Observable产生的结果进行过滤，把重复的结果过滤掉，只输出不重复的结果给订阅者
     */
    public void distinct() {
        just(1, 1, 22, 3, 3, 4)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //输出1,22,3,4
                        Log.e(TAG, "Next:" + integer);
                    }
                });
    }

    /**
     * elementAt操作符在源Observable产生的结果中，仅仅把指定索引的结果提交给订阅者
     */
    public void elementAt() {
        just(1, 2, 3, 4, 5)
                .elementAt(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //输出3
                        Log.e(TAG, "elementAt Next:" + integer);
                    }
                });
    }

    /**
     * filter操作符是对源Observable产生的结果按照指定条件进行过滤，
     * 只有满足条件的结果才会提交给订阅者
     */
    public void filter() {
        just(1, 2, 3, 4, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 4;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //只输出1,2,3,
                        Log.e(TAG, "filter Next:" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * ofType操作符类似于filter操作符，区别在于ofType操作符是按照类型对结果进行过滤
     */
    public void ofType() {
        Observable.just("hello", 2F, 3L, true, 'c', 4F, 5.2F)
                .ofType(Float.class)
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(Float aFloat) throws Exception {
                        Log.e(TAG, "ofType Next:" + aFloat);
                    }
                });
        //输出结果
         /*
         ofType Next:2.0
         ofType Next:4.0
         ofType Next:5.2
         ofType onCompleted:*/

    }

    public void single() {
        Single.just(1)
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    /**
     * ignoreElements操作符
     * ignoreElements操作符忽略所有源Observable产生的结果，只把Observable的onCompleted和onError事件通知给订阅者。
     * ignoreElements操作符适用于不太关心Observable产生的结果，
     * 只是在Observable结束时(onCompleted)或者出现错误时能够收到通知。
     */
    public void ignoreElements() {
        just(1, 2, 3, 4, 5)
                .ignoreElements()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "Sequence complete.");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * skip操作符
     * skip操作符针对源Observable产生的结果，跳过前面n个不进行处理，而把后面的结果提交给订阅者处理
     * 运行结果
     * Next: 4
     * Next: 5
     * Next: 6
     * Next: 7
     * Sequence complete.
     * skipLast操作符
     * skipLast操作符针对源Observable产生的结果，忽略Observable最后产生的n个结果，而把前面产生的结果提交给订阅者处理，
     * 值得注意的是，skipLast操作符提交满足条件的结果给订阅者是存在延迟效果的
     */

    private void skip() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .skip(440, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "Next: " + aLong);
                    }
                });
    }

    private void skipLast() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(10)
                .skipLast(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "Next: " + aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }

    /**
     * CombineLatest操作符能接受2~9个Observable或者一个Observable集合作为参数，当其中一个Observable
     * 要发射数据时，它会用传入的Func函数对每个Observable最近发射的数据进行组合后发射一个新的数据。
     * 这里有两个规则：
     * <p>
     * 所有的Observable必须都发射过数据，如果其中一个Observable从来没发射过数据，
     * 将不会组合发射新的数据；
     * 满足上面条件之后，当其中任何一个Observable要发射数据时，就会调用Func函数对所有Observable
     * 最近发射的数据进行组合（每个Observable贡献一个），然后发射出去。
     */
    public void combineLatest() {
       /* //产生0,5,10,15,20数列
        Observable<Long> observable1 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 5;
                    }
                }).take(5);
        //产生0,10,20,30,40数列
        Observable<Long> observable2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).take(5);
        Observable.combineLatest(observable1, observable2, new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long aLong, Long aLong2) throws Exception {
                return aLong + aLong2;
            }
        }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.e(TAG, "Next: " + aLong);
            }
        });*/
        //输出结果
        /**
         * 01-10 12:22:32.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 0
         01-10 12:22:33.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 5
         01-10 12:22:33.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 15
         01-10 12:22:34.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 20
         01-10 12:22:34.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 30
         01-10 12:22:35.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 35
         01-10 12:22:35.634 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 45
         01-10 12:22:36.130 22138-22280/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 50
         01-10 12:22:36.634 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Next: 60
         01-10 12:22:36.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaOperatorActivity: Sequence complete.
         */
        Observable<Integer> observable1 = Observable.just(1, 2, 3);
        Observable<Integer> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        });
        Observable.combineLatest(observable1, observable2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return integer + "," + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "combineLatest accept: ");
            }
        });
    }

    /**
     * zip and zipWith
     * 将多个Observables的发射物结合到一起，基于这个函数的结果为每个结合体发射单个数据项
     * Zip操作符按顺序把两个或多个Observables发射的数据项结合成一个单独的数据项 ，
     * 然后它发射这个数据项。它按照严格的顺序发射数据。它只发射与发射数据项最少的那个Observable一样多的数据。
     * RxJava将这个操作符实现为zip（static）和zipWith(非static)：
     */
    public void zip() {
        Observable<Integer> observable1 = just(10, 20, 30);
        Observable<Integer> observable2 = just(4, 8, 12, 16, 18);
        Observable.zip(observable1, observable2,
                (integer1, integer2) -> integer1 + integer2
        ).subscribe(integer -> Log.e(TAG, "zip Next:" + integer));
        //zipWith
        observable1.zipWith(observable2, (integer1, integer2) -> integer1 + "hahah" + integer2
        ).subscribe(s -> Log.e(TAG, s));
    }

    private void useCatch() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onError(new Throwable("use catch"));
            }
        })
                //原始Observable遇到error是一个Exception时发射备用的Observable，否则传递 error
                .onExceptionResumeNext(new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        observer.onNext(-1);
                        observer.onNext(-2);
                        observer.onComplete();
                    }
                }).subscribe(observer);

               /* //让Observable遇到错误时发射备用的Observable
               .onErrorResumeNext(new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        observer.onNext(-1);
                        observer.onNext(-2);
                        observer.onComplete();
                    }
                })
                .subscribe(observer);*/

                /*//让Observable遇到错误时发射一个特殊的项并且正常终止。
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        Log.e(TAG, "apply: " + throwable);
                        return -1;
                    }
                }).subscribe(observer);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


