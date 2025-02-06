
### 介绍

Retrofit 将你的 HTTP API 转化成 Java接口。

```java
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```

Retrofit 类 生成 GitHubService 接口的实现。

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build();

GitHubService service = retrofit.create(GitHubService.class);
```

GitHubService中的每个请求，都可以同步或者异步发起。

```java
Call<List<Repo>> repos = service.listRepos("octocat");
```

使用注解来描述HTTP请求：
* 指出URL参数替换，支持请求参数。
* 将Java对象转化为请求体。(e.g., JSON, protocol buffers)
* Multipart request body 和 文件上传。

### API 声明

接口方法上的注解和注解参数指出一个请求是如何被处理的。

* 请求方法

每个接口必须有一个HTTP注解来提供请求方法和URL（要请求的资源的相对路径）。有8个内置的注解。`HTTP, GET, POST, PUT, PATCH, DELETE, OPTIONS 和 HEAD`。

```java
@GET("users/list")
// 在url里面指定查询参数 
@GET("users/list?sort=desc")
```

### URL 操作

一个请求URL可以使用替换括号和方法里的参数来动态更新。参数需要使用`@Path`来标明。

```java
@GET("group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId);
```

也可以添加请求参数。

```java
@GET("group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
```

复杂的请求参数可以使用 Map。

```java
@GET("group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);
```

### 请求体

一个对象可以使用 @Body 来指定作为 HTTP请求体来使用。

```java
@POST("users/new")
Call<User> createUser(@Body User user);
```

该对象会被 Retrofit 实例中指定的 converter 转换。如果没有添加converter，那么只能使用`RequestBody`。


### FORM ENCODED AND MULTIPART （表单编码和多部分）

接口方法也可以声明发送 form-encoded and multipart 数据。

使用@FormUrlEncoded注解的时候，会发送 form-encoded 数据。每个键值对使用`@Field`注解。

```java
@FormUrlEncoded
@POST("user/edit")
Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);
```

使用 @Multipart 的时候会发送Multipart请求。要发送的数据需要使用`@Part`注解。

```java
@Multipart
@PUT("user/photo")
Call<User> updateUser(@Part("photo") RequestBody photo, @Part("description") RequestBody description);
```

Multipart 部分使用 Retrofit的某个 converter，或者他们（是指谁呢？）可以实现RequestBody来处理他们自己的序列化。

### HEADER MANIPULATION  请求头操作

你可以使用@Headers注解，添加静态的请求头。

```java
@Headers("Cache-Control: max-age=640000")
@GET("widget/list")
Call<List<Widget>> widgetList();
```

```java
@Headers({
    "Accept: application/vnd.github.v3.full+json",
    "User-Agent: Retrofit-Sample-App"
})
@GET("users/{username}")
Call<User> getUser(@Path("username") String username);
```


注意，请求头不会覆盖，相同名称的所有请求头都会被包含在请求中。


使用 @Header 注解 可以动态更新请求头。

```java
@GET("user")
Call<User> getUser(@Header("Authorization") String authorization)
```

复杂的请求头可以使用map。

```java
@GET("user")
Call<User> getUser(@HeaderMap Map<String, String> headers)
```

如果每个请求都要添加请求头，可以自定义一个拦截器。

### 同步和异步

Call 实例可以同步或者异步执行。每个Call实例只能被使用一次，但是调用 clone() 会创建一个新的实例并被使用。

在Android平台上，回调在主线程执行。在JVM上，回调会在发出请求的同一个线程执行。

### Retrofit 配置

通过Retrofit类会将接口方法转化成可以请求对象。默认情况下，Retrofit会在响应的平台上给你一个默认的配置，但是也可以自定义。

### CONVERTERS

默认情况下，Retrofit只能将HTTP响应体反序列化成 OkHttp的ResponseBody 类型并且 `@Body` 只能接收RequestBody类型。

可以添加Converters来支持不同的类型。

下面的例子，使用GsonConverterFactory，使用Gson进行反序列化。

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

GitHubService service = retrofit.create(GitHubService.class);

```

### 自定义 CONVERTERS

可以继承 Converter.Factory 来创建自定义的转换器。