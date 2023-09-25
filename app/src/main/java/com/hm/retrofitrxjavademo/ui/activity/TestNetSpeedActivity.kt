package com.hm.retrofitrxjavademo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hm.retrofitrxjavademo.R
import com.hm.retrofitrxjavademo.util.NetSpeedUtils


/**
 * Created by p_dmweidu on 2023/9/25
 * Desc: 测试网速
 */
class TestNetSpeedActivity : AppCompatActivity() {

    private var tvNetSpeed: TextView? = null

    private var btnStart: Button? = null
    private var btnStop: Button? = null

    private var webView: WebView? = null

    companion object {

        @JvmStatic
        fun launch(context: Context) {
            val starter = Intent(context, TestNetSpeedActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_net_speed)

        webView = findViewById(R.id.web_view)
        tvNetSpeed = findViewById(R.id.tv_speed)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        initWebViewSetting(webView)
        webView?.loadUrl("https://go.minigame.vip/")

        NetSpeedUtils.netSpeedCallback = object : NetSpeedUtils.NetSpeedCallback {
            override fun onNetSpeedChange(downloadSpeed: String, uploadSpeed: String) {
                tvNetSpeed?.run {
                    post {
                        text = "downloadSpeed:$downloadSpeed , uploadSpeed:$uploadSpeed"
                    }
                }
            }
        }
        btnStart?.setOnClickListener {
            NetSpeedUtils.startMeasuringNetSpeed(this)
        }
        btnStop?.setOnClickListener {
            NetSpeedUtils.stopMeasuringNetSpeed()
        }

    }

    private fun initWebViewSetting(webView: WebView?) {
        webView?.run {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView?.clearHistory()
        //webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        webView?.destroy()
    }


}