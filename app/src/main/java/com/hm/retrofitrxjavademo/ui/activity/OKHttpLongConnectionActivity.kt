package com.hm.retrofitrxjavademo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hm.retrofitrxjavademo.databinding.ActivityOkhttpLongConnectionBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Created by p_dmweidu on 2024/4/11
 * Desc: 测试 OkHttp 长连接的简单使用
 */
class OKHttpLongConnectionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOkhttpLongConnectionBinding

    companion object {

        private const val TAG = "OKHttpLongConnectionAct"

        @JvmStatic
        fun launch(context: Context) {
            val starter = Intent(context, OKHttpLongConnectionActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOkhttpLongConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            // 开始长连接
            startLongConnection()
        }

        binding.btnSend.setOnClickListener {
            // 发送消息
            sendMessage()
        }

        binding.btnClose.setOnClickListener {
            // 停止长连接
            stopLongConnection()
        }

    }

    private var webSocket: WebSocket? = null

    private val okHttpClient = OkHttpClient()

    private fun startLongConnection() {
        val request = Request.Builder()
            .url("https://github.com")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // 连接已打开
                Log.i(TAG, "onOpen: 连接已打开")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // 收到消息
                Log.i(TAG, "onMessage: 收到消息 $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // 连接正在关闭
                Log.i(TAG, "onClosing: 连接正在关闭")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // 连接失败或发生错误
                Log.i(TAG, "onFailure: 连接失败或发生错误")
            }
        })
    }

    private fun sendMessage() {
        webSocket?.send("Your message")
    }

    private fun stopLongConnection() {
        val success = webSocket?.close(1000, "Goodbye!")
        Log.i(TAG, "stopLongConnection: $success")
    }


}