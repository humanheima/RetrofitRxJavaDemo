package com.hm.retrofitrxjavademo.intercepter

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Created by dumingwei on 2020-03-10.
 * Desc:
 *
 */
class LoggingInterceptor : Interceptor {

    private val TAG = "LoggingInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val t1 = System.nanoTime()

        Log.d(TAG, "intercept: ${String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers())}")

        var response = chain.proceed(request)

        var t2 = System.nanoTime()

        Log.d(TAG, "intercept: ${String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers())}")


        /**
         * 如果是作为应用拦截器，chain.proceed(request)可以被调用多次
         * 如果是作为网络拦截器，chain.proceed(request)只能被调用一次
         */
        response.close()
        response = chain.proceed(request)

        t2 = System.nanoTime()

        Log.d(TAG, "intercept: ${String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers())}")
        return response

    }
}