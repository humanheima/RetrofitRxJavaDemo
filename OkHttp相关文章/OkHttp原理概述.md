### OkHttp的优势

* HTTP/2支持请求同一个host的多个请求共享一个socket连接。
* 连接缓存降低请求延迟。
* 透明的GZIP压缩下载体积。
* 响应缓存，避免完全重复的request发起网络请求，可以直接从缓存里面获取响应。

当OkHttp遇到网络问题的时候，它会静默的从常见的连接问题中恢复。如果你的服务有多个IP地址，当第一次连接失败的时候，OkHttp会尝试其他的地址。

使用OkHttp很简单。它的request/response API都是使用构建模式创建，并且是不可变的。OkHttp支持同步和异步请求。


### OkHttp的基本工作原理

OkHttp是一个高效的HTTP客户端，它的工作原理主要包括以下几个步骤：

1. **创建请求**：首先，你需要创建一个`Request`对象，这个对象包含了你要访问的URL，请求方法（GET，POST等），请求头和请求体（如果有的话）。

2. **创建Call对象**：然后，你需要使用`OkHttpClient`对象的`newCall`方法，传入你刚刚创建的`Request`对象，这个方法会返回一个`Call`对象。这个`Call`对象代表了一个即将执行的HTTP请求。

3. **执行请求**：你可以选择同步执行请求（使用`Call`对象的`execute`方法）或者异步执行请求（使用`Call`对象的`enqueue`方法）。同步执行会阻塞当前线程直到请求完成，而异步执行则不会。

4. **处理响应**：无论你选择同步执行还是异步执行，你都会得到一个`Response`对象。这个对象包含了服务器的响应，包括响应码，响应头和响应体。

在这个过程中，OkHttp使用了一种叫做拦截器的机制来处理请求和响应。拦截器可以在请求被发送到服务器之前，或者响应被返回到客户端之前，对请求或响应进行修改。例如，你可以添加一个拦截器来添加一个特定的请求头，或者对服务器的响应进行缓存。

在你的代码中，`RealCall.java`文件就是OkHttp的核心部分之一，它实现了`Call`接口，负责创建和执行HTTP请求。在`getResponseWithInterceptorChain`方法中，你可以看到OkHttp是如何创建一个拦截器链，并使用这个链来处理请求和响应的。


### OkHttp的拦截器 Interceptor


OkHttp的拦截器（Interceptor）是一种强大的机制，可以监视、重写和重试调用。有两种类型的拦截器：

1. **应用拦截器**：这些拦截器添加在OkHttpClient构建器中，通过`addInterceptor()`方法。它们在请求发出和响应返回的过程中都会被调用，即使请求在传输过程中出现问题。应用拦截器可以操作请求的最终内容（如自定义头部、查询参数等），也可以操作响应的最终内容（如解析、修改响应等）。

2. **网络拦截器**：这些拦截器添加在OkHttpClient构建器中，通过`addNetworkInterceptor()`方法。它们在请求和响应被传输的过程中被调用。网络拦截器可以操作和查看数据（如重定向、重试、查看未压缩的响应体等）。

作用范围：
应用拦截器 Interceptor：可以处理所有请求和响应，包括从缓存中读取的。
网络拦截器NetworkInterceptor：仅处理实际的网络请求和响应，不涉及缓存。

使用场景：
Interceptor：适合需要通用处理的场景，如日志、错误处理等。
NetworkInterceptor：适合需要网络层特定处理的场景，如在请求发送前修改请求或在收到响应后处理。
以下是一些常见的OkHttp拦截器：

- **LoggingInterceptor**：这是一个用于记录请求和响应信息的拦截器，可以帮助我们调试网络请求。

- **RetryAndFollowUpInterceptor**：这是一个用于处理网络连接问题和其他恢复性问题的拦截器。

- **BridgeInterceptor**：这是一个用于填充请求头或响应头的拦截器。

- **CacheInterceptor**：这是一个用于读取缓存响应或更新缓存的拦截器。

- **ConnectInterceptor**：这是一个用于建立网络连接的拦截器。

- **CallServerInterceptor**：这是一个用于向服务器发送请求和接收响应的拦截器。

以上就是OkHttp的拦截器及其主要功能。拦截器是OkHttp的核心组件之一，它可以让我们在请求和响应的过程中对数据进行处理，从而实现更加灵活和强大的功能。

* 拦截器的顺序

```java
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


### OkHttp的连接池 ConnectionPool

OkHttp的`ConnectionPool`类是用于管理HTTP和HTTP/2连接的重用，以减少网络延迟。HTTP请求如果共享相同的`Address`，则可能共享一个`Connection`。这个类实现了决定哪些连接应该保持开启以供未来使用的策略。

以下是`ConnectionPool`的主要工作原理：

1. **创建连接池**：在创建`ConnectionPool`对象时，可以指定最大空闲连接数和保持连接活跃的时间。如果不指定，那么默认的最大空闲连接数为5，保持连接活跃的时间为5分钟。

2. **获取连接**：当需要一个连接时，会首先查看连接池中是否有可用的连接。如果有，并且这个连接是可用的（即没有被其他请求占用），那么就直接使用这个连接，否则创建一个新的连接。

3. **回收连接**：当一个连接不再被使用时，会被回收到连接池中，等待下次使用。在回收时，会检查当前空闲的连接数是否超过了最大空闲连接数，如果超过了，那么就关闭最长时间未被使用的连接。

4. **清理连接**：连接池中的连接如果在一定时间内没有被使用，那么就会被清理掉。这个时间就是在创建`ConnectionPool`时指定的保持连接活跃的时间。清理工作是由一个后台线程完成的，这个线程会定期唤醒，检查并清理过期的连接。

以上就是OkHttp的`ConnectionPool`的大致工作原理。

### okhttp3 ConnectionPool 的工作原理

默认最大空闲数是5，最大空闲时间是5分钟

`okhttp3.ConnectionPool` 是 OkHttp 的连接池实现，它管理 HTTP 和 HTTP/2 连接的复用，以减少网络延迟。共享相同 `Address` 的 HTTP 请求可能会共享一个 `Connection`。此类实现了决定保持哪些连接开放以供将来使用的策略。

以下是 `ConnectionPool` 的主要工作原理。连接复用条件： 相同主机和端口：连接必须指向相同的主机和端口。 协议匹配：连接的协议（如 HTTP/2 或 HTTP/1.1）必须与新请求的协议兼容。


1. **连接复用**：`ConnectionPool` 管理着一个 `Deque<RealConnection>` 集合，这个集合保存了所有的连接。当需要一个连接时，会先从这个集合中查找是否有可复用的连接。如果有，则直接复用这个连接，否则创建新的连接。

2. **连接清理**：`ConnectionPool` 有一个后台线程，用于清理过期的连接。当一个连接空闲时间超过指定的保活时间，或者空闲连接数超过最大空闲连接数时，这个连接就会被清理掉。

3. **连接分配**：每个 `RealConnection` 都有一个 `allocations` 列表，用于跟踪哪些 `StreamAllocation` 正在使用这个连接。当 `StreamAllocation` 完成时，会从 `allocations` 列表中移除。如果 `allocations` 列表为空，那么这个连接就变成了空闲状态，可以被清理或者复用。

4. **连接逐出**：`ConnectionPool` 提供了一个 `evictAll()` 方法，可以关闭并移除所有的空闲连接。

5. **连接计数**：`ConnectionPool` 提供了 `idleConnectionCount()` 和 `connectionCount()` 方法，可以获取空闲连接数和总连接数。

6. **连接去重**：如果有多个连接同时连接到同一个 HTTP/2 服务器，`ConnectionPool` 会通过 `deduplicate()` 方法去除重复的连接。

以上就是 `okhttp3.ConnectionPool` 的工作原理。


`okhttp3.ConnectionPool` 清理空闲连接的过程主要是通过一个后台线程来实现的。这个后台线程会定期检查连接池中的所有连接，如果一个连接的空闲时间超过了指定的保活时间，或者空闲连接数超过了最大空闲连接数，那么这个连接就会被清理掉。

### ConnectionPool 是怎么清理空闲的连接的

具体的清理过程如下：

1. 后台线程会遍历连接池中的所有连接，对于每一个连接，都会检查它的最后活跃时间。如果当前时间减去最后活跃时间大于保活时间，那么这个连接就被认为是过期的，需要被清理。

2. 同时，后台线程还会检查当前的空闲连接数是否超过了最大空闲连接数。如果超过了，那么就需要清理一部分空闲连接。清理哪些连接通常是根据一定的策略来决定的，例如，可以清理最早进入空闲状态的连接，或者清理最长时间没有被使用的连接。

3. 当一个连接被确定需要清理时，后台线程会调用连接的 `close()` 方法来关闭这个连接，然后从连接池中移除这个连接。

以上就是 `okhttp3.ConnectionPool` 清理空闲连接的基本过程。

