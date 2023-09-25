package com.hm.retrofitrxjavademo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.hm.retrofitrxjavademo.R

/**
 * Created by p_dmweidu on 2023/9/20
 * Desc: 测试上传文件
 */
class UploadFileActivity : AppCompatActivity() {


    private var progressBar: ProgressBar? = null
    private var btn_upload_file: Button? = null
    private var btn_cancel_upload: Button? = null

    private var url= "http://xxvirtualcharacter-1252317822.cos.ap-shanghai.myqcloud.com/1400798467_383534303436303431313137.zip?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDkMky78ygGgsGSSI5vdIpBVhopjRxIQNG%26q-sign-time%3D1695622668%3B1695622728%26q-key-time%3D1695622668%3B1695622728%26q-header-list%3Dhost%26q-url-param-list%3D%26q-signature%3D9bcc42613f1405274099b3402866909765d08de3"

    companion object {


        fun launch(context: Context) {
            val starter = Intent(context, UploadFileActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)
        progressBar = findViewById(R.id.main_upload_progress)
        btn_upload_file = findViewById(R.id.btn_upload_file)
        btn_cancel_upload = findViewById(R.id.btn_cancel_upload)

        btn_upload_file?.setOnClickListener {


        }

        btn_cancel_upload?.setOnClickListener {

        }
    }


}