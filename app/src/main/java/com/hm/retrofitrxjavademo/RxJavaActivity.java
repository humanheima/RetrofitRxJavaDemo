package com.hm.retrofitrxjavademo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hm.retrofitrxjavademo.model.Animal;
import com.hm.retrofitrxjavademo.model.Dog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action4;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import static rx.Observable.just;

public class RxJavaActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.activity_rx_java)
    RelativeLayout activityRxJava;
    public final static String tag = "RxJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);
        ButterKnife.bind(this);
        //useDoOnSubscribe();
        //compareJustAndDefer();
        //useRepeat();
        // zip();
        // retry();
        //retryWhen();
        //repeatWhen();
        //useBuffer();
        //contactMap();
        //flatMap();
        //flatMapIterable();
        //groupBy();
        //useCase();
        //useScan();
        //useWindow();
        //debounce();
        //ofType();
        //single();
        //combineLatest();
        // useStart();
        //  useError();
        //  never();
    }

    private void useStart() {

        Observable
                .just(1, 2, 4)
                .startWith(Observable.just(9, 8, 7))
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "useStart onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "useStart onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(tag, "useStart:" + integer);
                    }
                });
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
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "never onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "never onError");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(tag, "never onNext");
                    }
                });
        //什么也不输出
    }


    public void useError() {
        Observable
                .error(new Throwable("使用error"))
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "useError onError");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(tag, "useError onNext");
                    }
                });

        //输出 useError onError
    }

    private void flatMap() {

        /**
         *  flatMap() 的原理是这样的：
         *  1. 使用传入的事件对象创建一个 Observable 对象；
         *  2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
         *  3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，
         *  而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。
         *  这三个步骤，把事件拆成了两级，
         *  通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去
         *
         */
        List<Integer> integerList1 = new ArrayList<>();
        integerList1.add(1);
        integerList1.add(2);
        integerList1.add(3);
        integerList1.add(4);
        integerList1.add(5);

        List<Integer> integerList2 = new ArrayList<>();
        integerList2.add(6);
        integerList2.add(7);
        integerList2.add(8);
        integerList2.add(9);
        integerList2.add(10);

        List<List> listList = new ArrayList<>();
        listList.add(integerList1);
        listList.add(integerList2);
        Observable.from(listList)
                .map(new Func1<List, List<Integer>>() {
                    @Override
                    public List<Integer> call(List list) {
                        return list;
                    }
                }).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> strings) {
                for (Integer integer : strings) {
                    Log.e(tag, "map" + integer);
                }
            }
        });

        Observable.from(listList)
                .flatMap(new Func1<List, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(List list) {
                        return Observable.from(list);
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer s) {
                Log.e(tag, "flatMap" + s);
            }
        });


    }

    private void flatMapIterable() {
        just(1, 2, 3, 5)
                .flatMapIterable(new Func1<Integer, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> call(Integer integer) {
                        List<Integer> integerList = new ArrayList<Integer>();
                        integer = integer + 100;
                        integerList.add(integer);
                        return integerList;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(tag, "flatMapIterable" + integer);
            }
        });
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

        fileObservable.subscribe(new Subscriber<File>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "onError:" + e.getMessage());
            }

            @Override
            public void onNext(File file) {
                Log.e(tag, file.getPath() + file.getName());
            }
        });
    }

    private Observable<File> listFiles(File f) {
        if (!f.exists()) {
            return Observable.error(new Throwable("指定目录不存在！"));
        } else {
            if (f.isDirectory()) {
                return Observable.from(f.listFiles()).concatMap(new Func1<File, Observable<? extends File>>() {
                    @Override
                    public Observable<? extends File> call(File file) {
                        return listFiles(file);
                    }
                });
            } else {
                return just(f);
            }
        }
    }

    private void repeatWhen() {
        Observable.range(3, 3).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                return observable.zipWith(Observable.range(1, 3), new Func2<Void, Integer, Integer>() {
                    @Override
                    public Integer call(Void aVoid, Integer integer) {
                        return integer;
                    }
                }).flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        Log.e(tag, "delay repeat the " + integer + " count");
                        //1秒钟重复一次
                        return Observable.timer(1, TimeUnit.SECONDS);
                    }
                });
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "onError:" + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(tag, "onNext" + integer);
            }
        });
    }

    private void retryWhen() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onError(new RuntimeException("always fails"));
            }
        })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> errors) {
                        return errors.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
                            @Override
                            public Integer call(Throwable throwable, Integer integer) {
                                return integer;
                            }
                        }).flatMap(new Func1<Integer, Observable<?>>() {
                            @Override
                            public Observable<?> call(Integer integer) {
                                Log.e(tag, "delay retryWhen the " + integer + " count");
                                //i秒钟重复一次
                                return Observable.timer(integer, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(tag, "onNext:" + s);
                    }
                });


    }

    private void retry() {
        /**
         * ②. retry(count)
         *     最多2次尝试重新订阅
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 3; i++) {
                    if (i == 1) {
                        Log.e(tag, "②retry(count)->onError");
                        subscriber.onError(new RuntimeException("always fails"));
                    } else {
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry(2)    //最多尝试2次重新订阅
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "②retry(count)->onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "②retry(count)->onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer i) {
                        Log.e(tag, "②retry(count)->onNext" + i);
                    }
                });

        /**
         * ③. retry(Func2)
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 3; i++) {
                    if (i == 1) {
                        Log.e(tag, "③retry(Func2)->onError");
                        subscriber.onError(new RuntimeException("always fails"));
                    } else {
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry(new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer integer, Throwable throwable) {
                Log.e(tag, "③发生错误了：" + throwable.getMessage() + ",第" + integer + "次重新订阅");
                if (integer > 2) {
                    return false;//不再重新订阅
                }
                //此处也可以通过判断throwable来控制不同的错误不同处理
                return true;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "③retry(Func2)->onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "③retry(Func2)->onError" + e.getMessage());
            }

            @Override
            public void onNext(Integer i) {
                Log.e(tag, "③retry(Func2)->onNext" + i);
            }
        });

    }

    public void repeat() {

        just(1, 2, 3, 4).repeat(2).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(tag, "repeat" + integer);
            }
        });

       /* Observable.just(1, 2, 3, 4).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                return null;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

            }
        });*/


      /*  Observable.range(3, 3).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                return observable.zipWith(Observable.range(1, 3), new Func2<Void, Integer, Integer>() {
                    @Override
                    public Integer call(Void aVoid, Integer integer) {
                        return integer;
                    }
                }).flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        Log.e(tag, "delay repeat the " + integer + " count");
                        //1秒钟重复一次
                        return Observable.timer(1, TimeUnit.SECONDS);
                    }
                });
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(tag, "onNext" + integer);
            }
        });*/
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

        //1 创建观察者的两种方式
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.e("tag", s);
            }
        };

        //2 创建被观察者的几种方式
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
            }
        });
        //just(T...): 将传入的参数依次发送出来。
        Observable observable1 = just("hello", "hi", "world");
        /**
         将会依次调用：
         onNext("Hello");
         onNext("Hi");
         onNext("world");
         onCompleted();
         */
        //from(T[]) / from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
        String[] words = {"Hello", "Hi", "Aloha"};
        Observable observable2 = Observable.from(words);
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("Hi");
        list.add("Aloha");
        Observable observable3 = Observable.from(list);
        /**
         * 将会依次调用：
         onNext("Hello");
         onNext("Hi");
         onNext("Aloha");
         onCompleted();
         */

        //3 Subscribe (订阅)
        observable.subscribe(subscriber);
        observable1.subscribe(subscriber);
        observable2.subscribe(subscriber);
        observable3.subscribe(subscriber);

        //subscribe() 还支持不完整定义的回调
        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(tag, s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(tag, throwable.getMessage());
            }
        };
        Action0 onCompletedAction = new Action0() {
            @Override
            public void call() {
                Log.e(tag, "onCompletedAction");
            }
        };
        Action4<String, String, Integer, List<String>> action4 = new Action4<String, String, Integer, List<String>>() {
            @Override
            public void call(String s, String s2, Integer integer, List<String> list) {

            }
        };
        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

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
        just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(tag, integer.toString());
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
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {

                Drawable drawable = getResources().getDrawable(resId);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, e.getMessage());
                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列
     */
    public void example4() {
        //一个map的例子
        // map() 方法将参数中的 String 对象转换成一个 Bitmap 对象后返回，
        // 而在经过 map() 方法后，事件的参数类型也由 String 转为了 Bitmap。
        final ImageView imageView = new ImageView(this);
        just("images/logo.png")// 输入类型 String
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String filePath) {
                        return getBitMapFromPath(filePath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
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
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.e(tag, "主线程" + s);
            }
        };
        just("hello", "world", "hi")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return "新线程1" + s;
                    }
                }).observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
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
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressBar.setVisibility(View.VISIBLE);//需要在主线程执行
                        Log.e(tag, "doOnSubscribe ,thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                    }
                })
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        Log.e(tag, "doAfterTerminate thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(tag, "onNext" + integer + ",thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
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
                                Log.e(tag, "doOnSubscribe ,thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(tag, e.getMessage());
                            }

                            @Override
                            public void onNext(Integer integer) {
                                Log.e(tag, "onNext" + integer + ",thread id" + Thread.currentThread().getId() + ",thread name" + Thread.currentThread().getName());
                            }
                        });
            }
        }).start();*/
    }

    /**
     * defer 操作符，just操作符是在创建Observable就进行了赋值操作，
     * 而defer是在订阅者订阅时才创建Observable，此时才进行真正的赋值操作
     */

    //初始的时候的时候i=10
    int i = 10;

    public void compareJustAndDefer() {
        Observable<Integer> justObservable = just(i);
        i = 12;
        Observable<Integer> deferObservable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return just(i);
            }
        });
        i = 15;
        justObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                //输出结果 10
                Log.e(tag, "justObservable i=" + integer);
            }
        });

        deferObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                //输出结果 15
                Log.e(tag, "deferObservable i=" + integer);
            }
        });
    }

    /**
     * timer 操作符 延迟产生一个数字就结束，
     * app 从欢迎页，2秒后自动跳转到主页面
     */
    public void delayStartAct() {
        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        startActivity(new Intent(RxJavaActivity.this, MainActivity.class));
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
     * @param view
     */
    public void interval(View view) {
        /**
         * interval操作符是每隔一段时间就产生一个数字，这些数字从0开始，递增直至无穷大
         */
        Observable.interval(2, 2, TimeUnit.SECONDS, Schedulers.io())
                .take(5)//最多输出5个
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(tag, "timer" + aLong);
                    }
                });
    }

    /**
     * range操作符
     * range操作符是创建一组在从n开始，个数为m的连续数字，比如range(3,10)，就是创建3、4、5…12的一组数字，
     *
     * @param view
     */
    public void useRange(View view) {
        Observable.range(3, 10, Schedulers.io())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(tag, "timer" + integer);
                    }
                });
    }


    /**
     * buffer操作符把一个Observable 变换成另外一个，原来的Observable正常发射数据，
     * 变换后的Observable发射这些数据的缓存集合，订阅者处理后，清空buffer列表，
     * 同时接收下一次收集的结果并提交给订阅者，周而复始。如果原来的Observable
     * 发射了一个onError通知， buffer 会立即传递这个通知，而不是首先发射缓存的数据，
     * 即使在这之前，缓存中有来自原来的Observable 的数据，也不会发射出去。
     */
    int num = 1;

    public void useBuffer() {

        //定义邮件内容
        final String[] mails = new String[]{"Here is an email!", "Another email!", "Yet another email!"};
        //每隔1秒就随机发布一封邮件
        Observable<String> endlessMail = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                Random ran = new Random();
                while (true) {
                    String mail = mails[ran.nextInt(mails.length)];
                    subscriber.onNext(mail);
                    if (num == 8) {
                        subscriber.onError(new Throwable("故意出错"));
                    }
                    num++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }

            }
        }).subscribeOn(Schedulers.io());
        //把上面产生的邮件内容缓存到列表中，并每隔3秒通知订阅者
        endlessMail.buffer(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<String> list) {
                        Log.e(tag, String.format("You've got %d new messages!  Here they are!", list.size()));
                        for (int i = 0; i < list.size(); i++)
                            Log.e(tag, list.get(i).toString());
                    }
                });

        /**
         * 输出结果
         You've got 3 new messages!  Here they are!
         Here is an email!
         Yet another email!
         Yet another email!
         You've got 3 new messages!  Here they are!
         Yet another email!
         Another email!
         Here is an email!
         onError故意出错
         */
    }

    /**
     * GroupBy操作符将原始Observable分拆为一些Observables集合，它们中的每一个发射原始Observable
     * 数据序列的一个子序列。哪个数据项由哪一个Observable发射是由函数getKey 判定的，
     * 这个函数给每一项指定一个Key，Key相同的数据会被同一个Observable发射。
     */
    public void groupBy() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(10)
                .groupBy(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong % 2;
                    }
                }, new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return 100 + aLong;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Long, Long>>() {
                    @Override
                    public void call(final GroupedObservable<Long, Long> result) {
                        result.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long value) {
                                Log.e(tag, "key:" + result.getKey() + ", value:" + value);
                            }
                        });
                    }
                });
    }

    /**
     * cast操作符
     * cast操作符类似于map操作符，不同的地方在于map操作符可以通过自定义规则，
     * 把一个值A1变成另一个值A2，A1和A2的类型可以一样也可以不一样；
     * 而cast操作符主要是做类型转换的，传入参数为类型class，
     * 如果源Observable产生的结果不能转成指定的class，则会抛出ClassCastException运行时异常。
     */
    public void useCase() {

        Animal animalTong = new Dog(1, "通狗");
        Animal animalHui = new Dog(1, "jingba");
        List<Animal> animals = new ArrayList<>();
        animals.add(animalTong);
        animals.add(animalHui);
        Observable
                .from(animals)
                .cast(Dog.class)
                .subscribe(new Action1<Dog>() {
                    @Override
                    public void call(Dog dog) {
                        Log.e(tag, dog.getHead() + "," + dog.getName());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(tag, "强制类型转换出错");
                    }
                });
    }

    /**
     * scan操作符
     * scan操作符通过遍历源Observable产生的结果，
     * 依次对每一个结果项按照指定规则进行运算，
     * 计算后的结果作为下一个迭代项参数，每一次迭代项都会把计算结果输出给订阅者。
     */
    public void useScan() {
        just(1, 2, 3, 4, 5)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer item) {
                        //参数sum就是上一次的计算结果
                        return sum + item;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(tag, "Next: " + integer);
            }
        });
        //输出结果
        /**
         Next: 1
         Next: 3
         Next: 6
         Next: 10
         Next: 15
         */
    }

    public void useWindow() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(12)
                .window(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> observable) {
                        Log.e(tag, "subdivide begin......");
                        observable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Log.e(tag, "Next:" + aLong);
                            }
                        });
                    }
                });
    }

    /**
     * debounce操作符对源Observable每产生一个结果后，如果在规定的间隔时间内没有别的结果产生，
     * 则把这个结果提交给订阅者处理，否则忽略该结果。
     * 值得注意的是，如果源Observable产生的最后一个结果后在规定的时间间隔内调用了onCompleted
     * ，那么通过debounce操作符也会把这个结果提交给订阅者。
     */
    public void debounce() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (subscriber.isUnsubscribed())
                    return;
                try {
                    for (int i = 1; i < 10; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(i * 100);
                    }
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "completed!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(tag, "Next:" + integer);
                    }
                });
        //输出结果
        /* Next:4
         Next:5
         Next:6
         Next:7
         Next:8
         Next:9
        completed!*/

    }

    /**
     * distinct操作符对源Observable产生的结果进行过滤，把重复的结果过滤掉，只输出不重复的结果给订阅者
     */
    public void distinct() {
        just(1, 1, 22, 3, 3, 4)
                .distinct()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "completed!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        //输出1,22,3,4
                        Log.e(tag, "Next:" + integer);
                    }
                });
    }

    /**
     * elementAt操作符在源Observable产生的结果中，仅仅把指定索引的结果提交给订阅者
     */
    public void elementAt() {
        just(1, 2, 3, 4, 5)
                .elementAt(2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "completed!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        //输出3
                        Log.e(tag, "elementAt Next:" + integer);
                    }
                });
    }

    /**
     * filter操作符是对源Observable产生的结果按照指定条件进行过滤，
     * 只有满足条件的结果才会提交给订阅者
     */
    public void filter() {
        just(1, 2, 3, 4, 5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 4;
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "completed!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        //只输出1,2,3,
                        Log.e(tag, "filter Next:" + integer);
                    }
                });
    }

    /**
     * ofType操作符类似于filter操作符，区别在于ofType操作符是按照类型对结果进行过滤
     */
    public void ofType() {
        just("hello", 2F, 3L, true, 'c', 4F, 5.2F)
                .ofType(Float.class)
                .subscribe(new Subscriber<Float>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "ofType onCompleted:");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Float aFloat) {
                        Log.e(tag, "ofType Next:" + aFloat);
                    }
                });
        //输出结果
         /*
         ofType Next:2.0
         ofType Next:4.0
         ofType Next:5.2
         ofType onCompleted:*/

    }

    /**
     * single操作符是对源Observable的结果进行判断，如果产生的结果满足指定条件的数量不为1(有且只有一个)
     * ，则抛出异常，否则把满足条件的结果提交给订阅者，
     */
    public void single() {
        //不满足条件
        just(1, 2, 3, 4, 5)
                .single(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 3;
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "single onCompleted:");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "single onError:" + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(tag, "single Next:" + integer);
            }
        });
        // single onError:Sequence contains too many elements

        //满足条件
        just(1, 2, 3, 4, 5)
                .single(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 4;
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "single onCompleted:");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "single onError:" + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(tag, "single Next:" + integer);
            }
        });
      /*
        single Next:5
        single onCompleted:
        */
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
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(tag, "Sequence complete.");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(tag, "ignoreElements Next:" + integer);
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

    public void skip(View view) {
        just(1, 2, 3, 4, 5, 6, 7).skip(3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Sequence complete.");
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
        //产生0,5,10,15,20数列
        Observable<Long> observable1 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong * 5;
                    }
                }).take(5);
        //产生0,5,10,15,20数列
        Observable<Long> observable3 = Observable.interval(300, 1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong * 5;
                    }
                }).take(5);

        //产生0,10,20,30,40数列
        Observable<Long> observable2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong * 10;
                    }
                }).take(5);

        Observable.combineLatest(observable1, observable2, new Func2<Long, Long, Long>() {
            @Override
            public Long call(Long aLong, Long aLong2) {
                return aLong + aLong2;
            }
        }).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(tag, "Sequence complete.");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "Error: " + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(tag, "Next: " + aLong);
            }
        });

        //输出结果
        /**
         * 01-10 12:22:32.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 0
         01-10 12:22:33.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 5
         01-10 12:22:33.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 15
         01-10 12:22:34.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 20
         01-10 12:22:34.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 30
         01-10 12:22:35.131 22138-22280/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 35
         01-10 12:22:35.634 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 45
         01-10 12:22:36.130 22138-22280/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 50
         01-10 12:22:36.634 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Next: 60
         01-10 12:22:36.635 22138-22281/com.hm.retrofitrxjavademo E/RxJavaActivity: Sequence complete.

         */
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
        Observable.zip(observable1, observable2, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(tag, "zip Next:" + integer);
            }
        });
        //zipWith
        observable1.zipWith(observable2, new Func2<Integer, Integer, String>() {
            @Override
            public String call(Integer integer, Integer integer2) {
                return integer + "zipWith" + integer2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(tag, "zipWith:" + s);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


