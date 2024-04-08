### Retrofit中的 Convert 是如何起作用的？

Retrofit的Converter是用于将HTTP响应体转换为特定类型的对象，或者将对象转换为请求体的组件。Retrofit本身并不直接支持任何一种特定的数据格式，而是通过添加不同的Converter来支持不同的数据格式，例如JSON，XML，Protocol Buffers等。

以下是Converter的工作原理：

1. **响应体的转换**：当Retrofit接收到HTTP响应后，它会调用Converter的`ResponseBodyConverter`方法，将响应体转换为特定类型的对象。这个方法接收一个`ResponseBody`对象，返回一个特定类型的对象。例如，如果你使用的是GsonConverter，那么这个方法会将响应体中的JSON字符串转换为Java对象。

2. **请求体的转换**：当你使用Retrofit发送HTTP请求时，你可以传入一个Java对象作为请求体。Retrofit会调用Converter的`RequestBodyConverter`方法，将这个Java对象转换为`RequestBody`对象。这个方法接收一个Java对象，返回一个`RequestBody`对象。例如，如果你使用的是GsonConverter，那么这个方法会将Java对象转换为JSON字符串，然后创建一个`RequestBody`对象。

在使用Retrofit时，你可以通过`Retrofit.Builder`的`addConverterFactory`方法添加Converter。例如，如果你想使用Gson作为数据格式，你可以添加GsonConverterFactory：

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

以上就是Retrofit的Converter的基本工作原理。

### Retrofit的 CallAdapter 是如何起作用的？

Retrofit的`CallAdapter`是一个接口，它定义了将Retrofit的`retrofit2.Call`对象转换为其他类型的对象的方法。这个接口有两个主要的方法：

1. `responseType()`：这个方法返回这个`CallAdapter`支持的Java类型。例如，如果你使用的是RxJava2CallAdapterFactory，那么这个方法可能会返回`Observable.class`，`Single.class`等。

2. `adapt(Call<T> call)`：这个方法将Retrofit的`Call`对象转换为其他类型的对象。例如，如果你使用的是RxJava2CallAdapterFactory，那么这个方法会将`Call`对象转换为RxJava的`Observable`或`Single`对象。

在使用Retrofit时，你可以通过`Retrofit.Builder`的`addCallAdapterFactory`方法添加`CallAdapter.Factory`。这个工厂类负责创建`CallAdapter`对象。例如，如果你想使用RxJava作为数据格式，你可以添加`RxJava2CallAdapterFactory`：

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com")
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build();
```

当你调用`Retrofit.create`方法创建API接口的实现时，Retrofit会检查每个方法的返回类型，然后使用`CallAdapter.Factory`创建对应的`CallAdapter`，并使用这个`CallAdapter`将`Call`对象转换为方法的返回类型。

以上就是Retrofit的`CallAdapter`的基本工作原理。