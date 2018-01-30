#RetrofitRxJavaDemo
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

```
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

   ```
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
```
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
```
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
```
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
