#RetrofitRxJavaDemo
### OkHttp

### Retrofit 
Retrofit 是一个 RESTful 的 HTTP 网络请求框架的封装。网络请求的工作本质上是 OkHttp 完成，而 Retrofit 仅负责 网络请求接口的封装

[这是一份很详细的 Retrofit 2.0 使用教程](http://blog.csdn.net/carson_ho/article/details/73732076)

[Android：手把手带你深入剖析 Retrofit 2.0 源码](https://blog.csdn.net/carson_ho/article/details/73732115)

OkHttpCall的enqueue方法
```java
  @Override public void enqueue(final Callback<T> callback) {
    checkNotNull(callback, "callback == null");

    okhttp3.Call call;
    Throwable failure;

    synchronized (this) {
      if (executed) throw new IllegalStateException("Already executed.");
      executed = true;

      call = rawCall;
      failure = creationFailure;
      if (call == null && failure == null) {
        try {
          //1.构建一个okhttp3.Call   
          call = rawCall = createRawCall();
        } catch (Throwable t) {
          failure = creationFailure = t;
        }
      }
    }

    if (failure != null) {
      callback.onFailure(this, failure);
      return;
    }

    if (canceled) {
      call.cancel();
    }
    //2. 使用okhttp3.Call的enqueue方法
    call.enqueue(new okhttp3.Callback() {
      @Override public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse)
          throws IOException {
        Response<T> response;
        try {
          //把okhttp3.Response转换成retrofit2的Response  
          response = parseResponse(rawResponse);
        } catch (Throwable e) {
          callFailure(e);
          return;
        }
        //3. 成功的回调
        callSuccess(response);
      }

      @Override public void onFailure(okhttp3.Call call, IOException e) {
        try {
          //4. 失败的回调  
          callback.onFailure(OkHttpCall.this, e);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }

      private void callFailure(Throwable e) {
        try {
          callback.onFailure(OkHttpCall.this, e);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }

      private void callSuccess(Response<T> response) {
        try {
          callback.onResponse(OkHttpCall.this, response);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    });
  }
```
1. OkHttpCall的enqueue方法和execute方法一样，先判断请求是否执行过了。如果没有执行，就构建一个okhttp3.Call，
和execute方法不同的是，这里使用了okhttp3.Call的enqueue()方法，然后在回调方法里，回调我们传入的callback。

先看一下 create 方法是怎么创建Service实例的

```java
public <T> T create(final Class<T> service) {
    //1. 首先验证service是否合法
    Utils.validateServiceInterface(service);
    //2. 是否提前创建service里面的所有方法
    if (validateEagerly) {
      eagerlyValidateMethods(service);
    }
    //3. 创建service的动态代理
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();

          @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
             // service的所有方法调用，最终会转发到这里
            // 如果是Object的方法，就正常调用
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            //如果是default方法（Java8中的默认方法）,Android中不用管，一定是false
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            //最关键的三行
            ServiceMethod<Object, Object> serviceMethod =(ServiceMethod<Object, Object>) loadServiceMethod(method);
            OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }
        });
  }
```
这里插一句关于`Platform`,我们在Android中使用Retrofit,平台就是Android
```java
static class Android extends Platform {
    //默认的回调执行器
    @Override public Executor defaultCallbackExecutor() {
      return new MainThreadExecutor();
    }

    @Override CallAdapter.Factory defaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
      if (callbackExecutor == null) throw new AssertionError();
      return new ExecutorCallAdapterFactory(callbackExecutor);
    }

    static class MainThreadExecutor implements Executor {
      //隐隐约约看到了熟悉的Handler。可以猜想，回调之所以是在主线程执行，还是使用handler来实现的  
      private final Handler handler = new Handler(Looper.getMainLooper());

      @Override public void execute(Runnable r) {
        handler.post(r);
      }
    }
  }
```
加载method对应的ServiceMethod。如果缓存中存在就直接返回，否则创建一个ServiceMethod加入缓存，然后返回。可见每个ServiceMethod只会创建一次。
ServiceMethod把对接口方法的调用转为一个HTTP调用
```java
ServiceMethod<Object, Object> serviceMethod =(ServiceMethod<Object, Object>) loadServiceMethod(method);
```
```java
private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();
```
```java
ServiceMethod<?, ?> loadServiceMethod(Method method) {
    ServiceMethod<?, ?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(method);
      if (result == null) {
        result = new ServiceMethod.Builder<>(this, method).build();
        serviceMethodCache.put(method, result);
      }
    }
    return result;
  }
```
看一下ServiceMethod的构造函数
```java
ServiceMethod(Builder<R, T> builder) {
    this.callFactory = builder.retrofit.callFactory();
    this.callAdapter = builder.callAdapter;
    this.baseUrl = builder.retrofit.baseUrl();
    this.responseConverter = builder.responseConverter;
    this.httpMethod = builder.httpMethod;
    this.relativeUrl = builder.relativeUrl;
    this.headers = builder.headers;
    this.contentType = builder.contentType;
    this.hasBody = builder.hasBody;
    this.isFormEncoded = builder.isFormEncoded;
    this.isMultipart = builder.isMultipart;
    this.parameterHandlers = builder.parameterHandlers;
  }
```
几个比较重要成员
1. callFactory：用来创建`okhttp3.Call`的工厂。通常是一个OkHttpClient实例
2. callAdapter：把一个响应类型为`R`的retrofit2.Call转化成`T`类型的对象。callAdapter的实例由构建
Retrofit时候通过{Retrofit.Builder#addCallAdapterFactory(Factory)}方法设置
3. responseConverter：负责把`okhttp3.ResponseBody`转化为对象,或者把对象转化为`okhttp3.RequestBody`。Converter实例
由构建Retrofit时候通过{Retrofit.Builder#addConverterFactory(Factory)}设置
4. parameterHandlers：负责解析 API 定义时每个方法的参数，并在构造 HTTP 请求时设置参数

首先我们看一下，默认情况下上面四个成员的创建。
```java
this.callFactory = builder.retrofit.callFactory();
```
当我们构建一个默认的Retrofit实例的时候,在Retrofit.Builder的build方法中，默认传入了一个OkHttpClient实例。
```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();
```
```java
 public Retrofit build() {
      if (baseUrl == null) {
        throw new IllegalStateException("Base URL required.");
      }
     
      okhttp3.Call.Factory callFactory = this.callFactory;
      if (callFactory == null) {
        callFactory = new OkHttpClient();
      }
}
```


### RxJava
[给 Android 开发者的 RxJava 详解](https://gank.io/post/560e15be2dca930e00da1083)

[retryWhen使用方法](https://www.jianshu.com/p/023a5f60e6d0)

RxJava 一句话概括就是一个实现异步操作的库

Retrofit+RxJava：RxJava把Retrofit的请求结果封装成Observable，形成链式调用，代码清晰简洁。

* 上传文件，接口定义
```
    //上传单个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadFile(@Part("images") RequestBody file);

    //上传参数和单个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadSingleFile(@Part("description") RequestBody description, @Part MultipartBody.Part file);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadMultiFile(@Part MultipartBody.Part... file);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadMultiFile(@Part List<MultipartBody.Part> partList);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadManyFile(@PartMap Map<String, RequestBody> map);
```

* RequestBody和MultipartBody.Part的区别
```
RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
// MultipartBody.Part is used to send also the actual file name
MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
```

* disposable

```java
/**
     * {@link Disposable}
     * 调用 Disposable的dispose方法以后，会导致下游收不到事件，但是上游会继续发送剩余的事件.
     * @param view
     */
    public void testDisposable(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
                Log.d(TAG, "emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {

            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
                i++;
                if (i == 2) {
                    Log.d(TAG, "dispose");
                    mDisposable.dispose();
                    Log.d(TAG, "isDisposed : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }
    
   ```
* from 

   ```java
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
   ```
* buffer(int count, int skip)
```java

        //buffer(int count, int skip),从原始Observable中每缓存skip个item，从中选择最多count个数据发
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
                });
```
* buffer(ObservableSource<B> boundary, final int initialCapacity)
```java

  Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(100)
                //第二个参数 initialCapacity 表示返回的List的初始容量
                .buffer(Observable.interval(250, TimeUnit.MILLISECONDS),6)
                .subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(List<Long> longs) throws Exception {
                        Log.e(TAG, "accept: ");
                        for (Long integer : longs) {
                            Log.e(TAG, "accept: integer=" + integer);
                        }
                    }
                });
                
```
* compose 
```java

 /**
     * 使用compose复用操作符的例子
     * {@link NetWork#applySchedulers()}
     */
    public void getNowWeather(View view) {
        map = new HashMap();
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", "10003");
        map.put("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4");
        map.put("format", "json");
        NetWork.getApi().testNowWeather(map)
                .compose(NetWork.applySchedulers())
                .subscribe(new Consumer<NowWeatherBean>() {
                    @Override
                    public void accept(NowWeatherBean bean) throws Exception {
                        Log.e(TAG, bean.getCitynm());
                        binding.textWeatherResult.setText(bean.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: error" + throwable.getMessage());
                    }
                });
    }

```
* API 接口，统一使用 下面的方法来获取数据
```
@GET
Observable<HttpResult<Object>> getData(@Url String url, @QueryMap Map<String, Object> map);

```
* 如果要下载Apk 使用 DownloadUtil(RetrofitRxJavaActivity有使用例子)，或者使用系统自带的DownloadManager(DownloadManagerActivity有使用例子)

* retry
```java
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
```
* RxJava统一异常处理
 