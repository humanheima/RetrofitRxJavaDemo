package com.hm.retrofitrxjavademo.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hm.retrofitrxjavademo.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OnlyOkHttpActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName


    private var okHttpClient: OkHttpClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_only_ok_http)
        okHttpClient = OkHttpClient()
    }

    fun get(url: String): Unit {

        var request: Request = Request.Builder()
                .url(url)
                .build()
        val response = okHttpClient?.newCall(request)?.execute()
        Log.d("OnlyOkHttpActivity", response?.body().toString())
    }
}
