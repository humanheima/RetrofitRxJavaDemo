## 拦截器

OkHttp的拦截器主要有以下几种：

### 应用拦截器(Application Interceptors)

这些拦截器在请求开始之前和响应返回之后执行，可以用于添加、移除或转换请求或响应的头部信息。例如，你可以添加一个应用拦截器来添加一个特定的请求头，那么这个请求头在所有的请求中都会存在。

### 网络拦截器(Network Interceptors)

这些拦截器在网络请求和响应的过程中执行，可以用于查看或者修改实际的网络请求和响应。例如，你可以使用网络拦截器来查看网络请求和响应中的HTTP头。

### RetryAndFollowUpInterceptor

`RetryAndFollowUpInterceptor`是OkHttp中的一个重要拦截器，它的主要职责是处理请求失败和重定向的情况。

在`RetryAndFollowUpInterceptor`中，主要的方法是`intercept`，这个方法会被OkHttp的拦截器链调用。在这个方法中，首先会创建一个`StreamAllocation`对象，这个对象负责管理HTTP请求的流。

然后，`RetryAndFollowUpInterceptor`会进入一个循环，这个循环会一直执行，直到请求成功，或者无法恢复的错误发生。在这个循环中，首先会检查请求是否被取消，如果被取消，那么会抛出一个`IOException`。

接着，`RetryAndFollowUpInterceptor`会尝试执行请求，并捕获可能发生的`IOException`。如果发生了`IOException`，那么`RetryAndFollowUpInterceptor`会尝试恢复请求。恢复的方式取决于异常的类型和请求的状态。例如，如果异常是`SocketTimeoutException`，并且请求还没有开始发送，那么`RetryAndFollowUpInterceptor`会尝试使用其他的路由重新执行请求。

如果请求成功，那么`RetryAndFollowUpInterceptor`会检查响应的状态码，如果状态码表示需要重定向，那么`RetryAndFollowUpInterceptor`会创建一个新的请求，并在下一次循环中执行这个请求。

在整个过程中，`RetryAndFollowUpInterceptor`会管理和复用HTTP连接，处理重定向和重试，以及处理请求的取消。这使得OkHttp能够更高效地处理HTTP请求。



### BridgeInterceptor


`BridgeInterceptor`是OkHttp中的一个拦截器，它的主要职责是在应用程序代码和网络代码之间建立桥梁。具体来说，它的作用包括：

1. **构建网络请求**：`BridgeInterceptor`会从用户的请求中构建一个网络请求。这包括添加或修改请求头，例如"Content-Type"，"Content-Length"，"Host"，"Connection"，"Accept-Encoding"，"Cookie"和"User-Agent"等。

2. **处理响应**：`BridgeInterceptor`会从网络响应中构建用户的响应。如果响应头中包含"Content-Encoding: gzip"，并且响应体不为空，`BridgeInterceptor`会负责解压缩响应流。

3. **处理Cookie**：`BridgeInterceptor`会从`CookieJar`中加载对应的Cookie，并添加到请求头中。同时，它也会处理响应头中的Set-Cookie字段，将新的Cookie保存到`CookieJar`中。

在`BridgeInterceptor.java`文件中，你可以看到`BridgeInterceptor`是如何实现这些功能的。
例如，`intercept`方法是其主要的工作方法，它首先从用户的请求中构建一个网络请求，然后调用`chain.proceed(requestBuilder.build())`执行网络请求，最后从网络响应中构建用户的响应。


### CacheInterceptor

`CacheInterceptor`是OkHttp中的一个拦截器，它的主要职责是处理HTTP请求的缓存。具体来说，它的作用包括：

1. **获取缓存的响应**：如果存在缓存，`CacheInterceptor`会尝试从缓存中获取响应。

2. **确定缓存策略**：`CacheInterceptor`会根据请求、缓存的响应以及当前时间，确定是否需要从网络获取数据，以及是否需要使用缓存的响应。

3. **处理缓存的响应**：如果缓存的响应可以使用，`CacheInterceptor`会直接返回缓存的响应。如果缓存的响应不能使用，`CacheInterceptor`会关闭缓存的响应。

4. **处理网络的响应**：如果需要从网络获取数据，`CacheInterceptor`会执行网络请求，并处理网络的响应。如果网络的响应表示内容没有修改，`CacheInterceptor`会使用缓存的响应，并更新缓存。如果网络的响应表示内容已经修改，`CacheInterceptor`会使用网络的响应，并尝试将其写入缓存。

在`CacheInterceptor.java`文件中，你可以看到`CacheInterceptor`是如何实现这些功能的。例如，`intercept`方法是其主要的工作方法，它首先从缓存中获取响应，然后确定缓存策略，接着处理缓存的响应，最后处理网络的响应。

### ConnectInterceptor

这个拦截器负责建立一个到目标服务器的连接。

`ConnectInterceptor`是OkHttp中的一个拦截器，它的主要职责是建立到目标服务器的连接。

在`ConnectInterceptor`的`intercept`方法中，首先会获取到`StreamAllocation`对象，这个对象负责管理HTTP请求的流和连接。然后，`ConnectInterceptor`会调用`StreamAllocation`的`newStream`方法，这个方法会尝试从连接池中获取一个已存在的连接，或者创建一个新的连接。

接着，`ConnectInterceptor`会创建一个新的`RealInterceptorChain`对象，并调用其`proceed`方法。这个方法会将请求传递给拦截器链中的下一个拦截器。在这个过程中，`ConnectInterceptor`会将`StreamAllocation`对象和新建立的连接传递给下一个拦截器。

如果在建立连接的过程中发生了错误，`ConnectInterceptor`会捕获这个错误，并尝试从`StreamAllocation`中获取另一个连接，然后重新执行上述过程。

以上就是`ConnectInterceptor`的基本工作原理。在`ConnectInterceptor.java`文件中，你可以看到`ConnectInterceptor`是如何实现这些功能的。

### CallServerInterceptor

`CallServerInterceptor`是拦截器链中的最后一个拦截器，负责向服务器发送网络请求。以下是`CallServerInterceptor`的主要工作原理：

1. **写入请求头**：`CallServerInterceptor`会调用`httpCodec.writeRequestHeaders(request)`方法，将请求头写入到请求中。

2. **处理请求体**：如果请求方法允许包含请求体，并且请求体不为空，`CallServerInterceptor`会处理请求体。如果请求头中包含"Expect: 100-continue"，那么会先发送请求头，等待服务器返回"HTTP/1.1 100 Continue"响应，然后再发送请求体。如果服务器没有返回"HTTP/1.1 100 Continue"响应，那么会直接返回服务器的响应，不会发送请求体。

3. **读取响应头**：`CallServerInterceptor`会调用`httpCodec.readResponseHeaders(false)`方法，读取服务器的响应头。

4. **处理响应体**：`CallServerInterceptor`会调用`httpCodec.openResponseBody(response)`方法，获取响应体。如果响应码是101，表示连接正在升级，那么会创建一个空的响应体。否则，会使用服务器的响应体。

5. **处理连接**：如果响应头中包含"Connection: close"，那么会调用`streamAllocation.noNewStreams()`方法，禁止在当前连接上创建新的流。

6. **检查响应**：如果响应码是204或205，但响应体的长度大于0，那么会抛出一个`ProtocolException`异常。

以上就是`CallServerInterceptor`的基本工作原理。在`CallServerInterceptor.java`文件中，你可以看到`CallServerInterceptor`是如何实现这些功能的。

