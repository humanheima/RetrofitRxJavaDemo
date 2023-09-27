package com.hm.retrofitrxjavademo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hm.retrofitrxjavademo.R
import com.hm.retrofitrxjavademo.upload.ProgressRequestBody
import com.hm.retrofitrxjavademo.upload.UpLoadProgressListener
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by p_dmweidu on 2023/9/20
 * Desc: OkHttp测试上传文件
 */
class UploadFileActivity : AppCompatActivity() {


    private var progressBar: ProgressBar? = null
    private var btnUploadFile: Button? = null
    private var btnCancelUpload: Button? = null

    private var call: Call? = null

    private var url =
        "aHR0cDovL3h4dmlydHVhbGNoYXJhY3Rlci0xMjUyMzE3ODIyLmNvcy5hcC1zaGFuZ2hhaS5teXFjbG91ZC5jb20vMTQwMDc5ODQ2N18zODM1MzQzMDM0MzYzMDM0MzEzMTMxMzcuemlwP3NpZ249cS1zaWduLWFsZ29yaXRobSUzRHNoYTElMjZxLWFrJTNEQUtJRGtNa3k3OHlnR2dzR1NTSTV2ZElwQlZob3BqUnhJUU5HJTI2cS1zaWduLXRpbWUlM0QxNjk1NjIyNjY4JTNCMTY5NTYyMjcyOCUyNnEta2V5LXRpbWUlM0QxNjk1NjIyNjY4JTNCMTY5NTYyMjcyOCUyNnEtaGVhZGVyLWxpc3QlM0Rob3N0JTI2cS11cmwtcGFyYW0tbGlzdCUzRCUyNnEtc2lnbmF0dXJlJTNEOWJjYzQyNjEzZjE0MDUyNzQwOTliMzQwMjg2NjkwOTc2NWQwOGRlMw=="

    companion object {

        private const val TAG = "UploadFileActivity"


        @JvmStatic
        fun launch(context: Context) {
            val starter = Intent(context, UploadFileActivity::class.java)
            context.startActivity(starter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)
        progressBar = findViewById(R.id.main_upload_progress)
        btnUploadFile = findViewById(R.id.btn_upload_file)
        btnCancelUpload = findViewById(R.id.btn_cancel_upload)

        url = base64ToString(url)

        btnUploadFile?.setOnClickListener {

            val filePath =
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + File.separator + "temp.apk"
            val file = File(filePath)
            if (!file.exists()) {
                Toast.makeText(this, "上传文件不存在，请拷贝一个文件到指定目录下面", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "onCreate: 文件不存在")
                return@setOnClickListener
            }
            uploadFile(url, filePath, object : UpLoadProgressListener {

                override fun onStart() {
                    Log.i(TAG, "onStart: ")
                }

                override fun onProgress(progress: Long, total: Long, done: Boolean) {
                    Log.i(TAG, "onProgress:  progress = $progress, total = $total, done = $done")
                    progressBar?.max = total.toInt()
                    progressBar?.progress = progress.toInt()
                }

                override fun onFailed(code: Int, msg: String?) {
                    Log.i(TAG, "onFailed: ")
                }

                override fun onSucceed() {
                    Log.i(TAG, "onSucceed: ")
                }
            })

        }

        btnCancelUpload?.setOnClickListener {
            call?.cancel()
        }
    }

    /**
     * 上传文件
     *
     * @param uploadUrl 上传地址
     * @param zipFilePath 压缩文件路径
     */
    private fun uploadFile(
        uploadUrl: String, zipFilePath: String,
        upLoadProgressListener: UpLoadProgressListener
    ) {
        val client = OkHttpClient.Builder().build()
        val requestBody =
            RequestBody.create(MediaType.parse("application/zip"), File(zipFilePath))
        val progressRequestBody = ProgressRequestBody(requestBody, upLoadProgressListener)
        val request = Request.Builder()
            .url(uploadUrl)
            .put(progressRequestBody)
            .build()
        call = client.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i(TAG, "onFailure: $e")
                upLoadProgressListener.onFailed(-1, e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "onResponse: response = $response")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToBase64(str: String): String {

        // 编码为Base64
        val encodedBytes = Base64.getEncoder().encode(str.toByteArray())
        val encodedStr = String(encodedBytes)
        Log.i(TAG, "stringToBase64: $encodedStr")
        return encodedStr
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun base64ToString(str: String): String {
        // 解码Base64
        val decodedBytes = Base64.getDecoder().decode(str)
        val decodedStr = String(decodedBytes)
        Log.i(TAG, "base64ToString: $decodedStr")
        return decodedStr
    }


}