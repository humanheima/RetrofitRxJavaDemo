```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(new OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build();
```
上面这段代码就可以看出涉及到的设计模式
* 构建者模式
* 策略模式：可以选择传入不同的转换器和适配器
* 适配器模式：各种CallAdapter

```
API api = retrofit.create(API.class);
```
* 动态代理模式

参考链接：
* [Retrofit分析-经典设计模式案例](https://www.jianshu.com/p/fb8d21978e38)
