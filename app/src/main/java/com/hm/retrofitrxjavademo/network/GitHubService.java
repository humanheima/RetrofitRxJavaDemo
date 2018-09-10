package com.hm.retrofitrxjavademo.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GitHubService {

//    @GET("users/{user}/repos")
//    Call<List<Repo>> listRepos(@Path("user") String user);

    //Headers()
    @GET("users/{user}/repos")
    Call<ResponseBody> listRepos(@Header("") @Path("user") String user);
}
