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

* Disposable

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