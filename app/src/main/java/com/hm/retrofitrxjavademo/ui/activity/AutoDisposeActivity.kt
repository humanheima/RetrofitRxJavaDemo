package com.hm.retrofitrxjavademo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hm.retrofitrxjavademo.R
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Crete by dumingwei on 2020-03-17
 * Desc:
 *
 */
class AutoDisposeActivity : AppCompatActivity() {


    private val TAG = "AutoDisposeActivity"

    companion object {

        @JvmStatic
        fun launch(context: Context) {
            val intent = Intent(context, AutoDisposeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_dispose)
    }


    fun onClick(view: View) {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete: ")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe: ")
                    }

                    override fun onNext(t: Long) {
                        Log.d(TAG, "onNext: $t")
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "onError: ${e.message}")
                    }
                })

    }


}
