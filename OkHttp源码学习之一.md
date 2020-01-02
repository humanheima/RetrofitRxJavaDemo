本篇主要分析OkHttp整个执行的流程，分析之前，先来一张流程图

![okhttp_full_process.png](https://upload-images.jianshu.io/upload_images/3611193-01e71d74e1f39561.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 发送一个同步GET请求
```
//实例化OkHttpClient
OkHttpClient client = new OkHttpClient();

public String run(String url) throws IOException {
  Request request = new Request.Builder()
      .url(url)
      .build();

  Response response = client.newCall(request).execute();
  return response.body().string();
}
```
`OkHttpClient`：`Call` 的工厂，用来发送HTTP请求并获取响应，`OkHttpClient`应该被共享，最好使用单例模式。
`Request`：一个HTTP请求，一个Request实例的请求体是null或者这个实例本身就是不可修改的，那么这个Request实例就是不可修改的。
`Call`：一个Call是一个已经准备好被执行的`Request`。一个Call可以被取消。因为Call代表一个单独的request/response对，所以只能被执行一次。

OkHttpClient的newCall方法最终返回的是一个RealCall，由RealCall调用execute方法

`OkHttpClient#newCall`
```
@Override 
public Call newCall(Request request) {
    return RealCall.newRealCall(this, request, false /* for web socket */);
}
```
`RealCall#newRealCall`
```
static RealCall newRealCall(OkHttpClient client, Request originalRequest,
 boolean forWebSocket) {
    RealCall call = new RealCall(client, originalRequest, forWebSocket);
    call.eventListener = client.eventListenerFactory().create(call);
    return call;
}
```
RealCall的execute方法
```
@Override
public Response execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");//1
      executed = true;
    }
    captureCallStackTrace();
    eventListener.callStart(this);
    try {
      client.dispatcher().executed(this);//2
      Response result = getResponseWithInterceptorChain();//3
      if (result == null) throw new IOException("Canceled");
      return result;
    } catch (IOException e) {
      eventListener.callFailed(this, e);
      throw e;
    } finally {
      client.dispatcher().finished(this);//4
    }
}
```
1. 检查`call`是否已经被执行过了，每个`call`只能执行一次。
2. 把这个`call`加入`OkHttpClient`的`dispatcher`的队列中，标记这个call正在执行。

当我们构建一个`OkHttpClient`实例的时候，会传入一个`Dispatcher`，我们需要认识一下`Dispatcher`类里面的几个变量
```
 /** 准备好被执行的异步请求 */
  private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

  /** 正在被执行的异步请求，包括被取消但是还没有结束的请求*/
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

  /** 正在被执行的同步请求，包括被取消但是还没有结束的请求 */
  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
```

`Dispatcher#executed()`
```
/** 把请求加入runningSyncCalls 队列，标记这个请求正在执行 */
synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
}
```

3. 执行HTTP请求，获取响应，这个步骤是整个OkHttp的全部精华所在，暂且不做分析。
4. 执行完毕后 把这个请求从队列中移除，标记这个`Call`执行完毕。

### 发送一个异步GET请求
```
private final OkHttpClient client = new OkHttpClient();

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://publicobject.com/helloworld.txt")
        .build();

    client.newCall(request).enqueue(new Callback() {
        @Override
         public void onFailure(Call call, IOException e) {
            e.printStackTrace();
      }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            try (ResponseBody responseBody = response.body()) {
              if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

              Headers responseHeaders = response.headers();
              for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
              }

          System.out.println(responseBody.string());
        }
      }
    });
  }
```
`RealCall#enqueue()`
```
@Override
public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");//1
      executed = true;
    }
    captureCallStackTrace();
    eventListener.callStart(this);
    client.dispatcher().enqueue(new AsyncCall(responseCallback));//2
}
```
1. 如果请求已经被执行过了，抛出异常。
2. 首先利用传入的`responseCallback`构建一个`AsyncCall`实例，然后调用`dispatcher`的`enqueue`方法。先来看一下`AsyncCall`这个类的部分代码`AsyncCall`是`RealCall`的内部类，继承了`NamedRunnable`这个类。
```
final class AsyncCall extends NamedRunnable {
    private final Callback responseCallback;

    AsyncCall(Callback responseCallback) {
      super("OkHttp %s", redactedUrl());
      this.responseCallback = responseCallback;
    }
     //...

     @Override
     protected void execute() {
      boolean signalledCallback = false;
      try {
        //获取响应
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          eventListener.callFailed(RealCall.this, e);
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
  }
```
可以看到当`AsyncCall`的`execute`方法执行的时候，就可以获取响应

再看一下`NamedRunnable`这个类,就是一个有名字的`Runnable`的实现
```
public abstract class NamedRunnable implements Runnable {
  protected final String name;

  public NamedRunnable(String format, Object... args) {
    this.name = Util.format(format, args);
  }

  @Override
  public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract void execute();
}
```
可以看到当`NamedRunnable`的`run`方法执行的时候，会调用`execute`方法

再看一下`Dispatcher`的`enqueue`方法,注意一下这是一个同步方法
```
synchronized void enqueue(AsyncCall call) {
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
  }
```
有两个变量需要了解一下
```
//当前正在执行的异步请求数量的最大值
private int maxRequests = 64;
//当前正在请求同一个host的异步请求最大数量
private int maxRequestsPerHost = 5;
```
`enqueue`方法内部判断，如果当前正在执行的异步请求数量的最大值小于64而且正在请求新传入的AsyncCall将要请求的host的异步请求数量小于5，那么就把AsyncCall加入到runningAsyncCalls并开始执行AsyncCall，否则就把AsyncCall加入到readyAsyncCalls等待执行。执行AsyncCall，最终会调用到AsyncCall的execute()方法，也就是在这个方法里真正获取响应并回调传入responseCallback。再把代码贴一下
```
@Override
protected void execute() {
      boolean signalledCallback = false;
      try {
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          eventListener.callFailed(RealCall.this, e);
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
```
### Interceptors
OkHttp最关键的代码就是RealCall的getResponseWithInterceptorChain方法：我们发出的网络请求，会经过一系列拦截器处理，然后向服务器发送处理过的请求，并把服务器返回的响应经过一系列的处理，返回用户期望的响应。

```
Response response = getResponseWithInterceptorChain();
```
```
Response getResponseWithInterceptorChain() throws IOException {
    // 构建一整套拦截器
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());//1
    interceptors.add(retryAndFollowUpInterceptor);//2
    interceptors.add(new BridgeInterceptor(client.cookieJar()));//3
    interceptors.add(new CacheInterceptor(client.internalCache()));//4
    interceptors.add(new ConnectInterceptor(client));//5
    //构建一个RealCall的时候我们传入的forWebSocket是false
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());//6
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));//7
    //构建拦截器链
    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
        originalRequest, this, eventListener, client.connectTimeoutMillis(),
        client.readTimeoutMillis(), client.writeTimeoutMillis());
    //拦截器链处理请求
    return chain.proceed(originalRequest);
  }
```
1.  构建OkHttpClient的时候传入的应用程序拦截器。
2.  RetryAndFollowUpInterceptor 重试重定向拦截器 负责从请求失败中恢复，在必要的时候进行重定向。如果call被取消的话，可能会抛出IOException。
3. BridgeInterceptor 负责把应用程序代码转化成网络代码，首先它会根据用户的请求构建一个网络请求，然后发起网络请求。最后它会把网络请求响应转化成用户希望得到的响应。
4. CacheInterceptor 缓存拦截器 负责从缓存中返回响应和把网络请求响应写入缓存。
5. ConnectInterceptor 连接拦截器 负责建立一个到目标服务器的连接。
6. 构建OkHttpClient的时候传入的网络拦截器。
7. CallServerInterceptor 请求服务拦截器 整个责任链中最后一个拦截器，负责向服务器发送网络请求。
应用程序拦截器和网络拦截器是用户可以自行配置的拦截器，它们之间的区别可以参看[Interceptors](https://github.com/square/okhttp/wiki/Interceptors)，它们两者的作用位置如下图所示
![interceptors.png](https://upload-images.jianshu.io/upload_images/3611193-b25a850c1e43434e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

上图中的OkHttp core就是上面2-5这几个拦截器


当我们构建OkHttpClient的时候，如果没有传入自定义的应用程序拦截器，那么整个interceptors列表中第一个就是RetryAndFollowUpInterceptor，然后构建一个RealInterceptorChain，然后由RealInterceptorChain来处理请求。看一下RealInterceptorChain这个类。
```
public final class RealInterceptorChain implements Interceptor.Chain {

  //所有的拦截器
  private final List<Interceptor> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  //用来标记当前的拦截器在拦截器列表中的下标
  private final int index;
  private final Request request;
  private final Call call;
  private final EventListener eventListener;
  //连接超时
  private final int connectTimeout;
  //读取超时
  private final int readTimeout;
  //写入超时
  private final int writeTimeout;
  private int calls;

  public RealInterceptorChain(List<Interceptor> interceptors, 
        StreamAllocation streamAllocation,HttpCodec httpCodec, RealConnection
        connection, int index, Request request, Call call,EventListener eventListener, 
        int connectTimeout, int readTimeout, int writeTimeout) {
    this.interceptors = interceptors;
    this.connection = connection;//传入的是null
    this.streamAllocation = streamAllocation;//传入的是null
    this.httpCodec = httpCodec;//传入的是null
    this.index = index;//传入的是0
    this.request = request;
    this.call = call;
    this.eventListener = eventListener;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
  }
...
}
```
```
public interface Interceptor {
  Response intercept(Chain chain) throws IOException;
  ...
  interface Chain {
    Request request();

    Response proceed(Request request) throws IOException;
    }
...
}
```
RealInterceptorChain实现了Interceptor.Chain接口，是一个具体拦截器链，包括了所有的拦截器：所有的应用程序拦截器，OkHttp核心拦截器，所有的网络拦截器，最后是请求服务拦截器

RealInterceptorChain的proceed方法
```
public Response proceed(Request request, StreamAllocation streamAllocation,
 HttpCodec httpCodec,RealConnection connection) throws IOException {
  //如果index越界，抛出异常
    if (index >= interceptors.size()) throw new AssertionError();
    ...
    // 使用当前拦截器的变量构建一个新的RealInterceptorChain，并把index加1
    RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, 
        httpCodec,connection, index + 1, request, call, eventListener, connectTimeout,
       readTimeout,writeTimeout);
    //取得当前的拦截器，我们没有给OkHttpClient传入的默认的应用程序拦截器，
    //所以第一个拦截器就是RetryAndFollowUpInterceptor
    Interceptor interceptor = interceptors.get(index);
    //调用当前拦截器RetryAndFollowUpInterceptor的intercept方法
    Response response = interceptor.intercept(next);
    ...
    return response;
}
```
现在第一个拦截器RetryAndFollowUpInterceptor调用intercept方法，下一篇在进行分析，今天就到这。
参考链接
1. [拆轮子系列：拆 OkHttp](https://blog.piasy.com/2016/07/11/Understand-OkHttp/)
2. [okhttp源码分析（一）——基本流程（超详细）](https://www.jianshu.com/p/37e26f4ea57b)
3. [interceptor](https://square.github.io/okhttp/interceptors/)
