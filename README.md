#RetrofitRxJavaDemo
####上传文件，接口定义。


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


RequestBody和MultipartBody.Part的区别

    	RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        
#####使用系统自带的DownloadManager 下载apk

