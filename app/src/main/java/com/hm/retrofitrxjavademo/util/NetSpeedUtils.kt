package com.hm.retrofitrxjavademo.util

import android.content.Context
import android.net.TrafficStats
import java.util.*

/**
 * Created by p_dmweidu on 2023/9/25
 * Desc: 测试网速
 */
object NetSpeedUtils {

    var netSpeedCallback: NetSpeedCallback? = null

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private var lastTotalReceiveBytes: Long = 0
    private var lastTotalTransferBytes: Long = 0

    private var mDownloadSpeed: Long = 0
    private var mUploadSpeed: Long = 0

    /**
     * 根据应用uid获取设备启动以来，该应用接收到的总字节数
     *
     * @param uid 应用的uid
     */
    fun getTotalReceiveBytes(context: Context): Long {
        val receiveBytes = TrafficStats.getUidRxBytes(context.applicationInfo.uid)
        // 当获取不到时，会返回TrafficStats.UNSUPPORTED
        return if (receiveBytes == TrafficStats.UNSUPPORTED.toLong()) 0 else receiveBytes / 1024
    }

    /**
     * 根据应用uid获取设备启动以来，该应用传输的总字节数
     *
     * @param uid 应用的uid
     */
    fun getTotalTransferBytes(context: Context): Long {
        val transferBytes = TrafficStats.getUidTxBytes(context.applicationInfo.uid)
        // 当获取不到时，会返回TrafficStats.UNSUPPORTED
        return if (transferBytes == TrafficStats.UNSUPPORTED.toLong()) 0 else transferBytes / 1024
    }

    // 通过Timer每隔1秒计算网速
    private fun calculateNetSpeed(context: Context) {
        val nowTotalReceiveBytes = getTotalReceiveBytes(context)
        val nowTotalTransferBytes = getTotalTransferBytes(context)

        val downloadSpeed = nowTotalReceiveBytes - lastTotalReceiveBytes
        val uploadSpeed = nowTotalTransferBytes - lastTotalTransferBytes

        mDownloadSpeed = downloadSpeed
        mUploadSpeed = uploadSpeed

        lastTotalReceiveBytes = nowTotalReceiveBytes
        lastTotalTransferBytes = nowTotalTransferBytes

        netSpeedCallback?.onNetSpeedChange("$downloadSpeed kb/s", "$uploadSpeed kb/s")
    }

    fun startMeasuringNetSpeed(context: Context) {
        if (timer == null && timerTask == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    calculateNetSpeed(context)
                }
            }
            timer?.run { timerTask?.let { schedule(it, 0L, 1000L) } }
        }
    }

    fun getDownloadSpeed(): Long {
        return mDownloadSpeed
    }

    fun getUploadSpeed(): Long {
        return mUploadSpeed
    }

    fun stopMeasuringNetSpeed() {
        timerTask?.cancel()
        timerTask = null
        timer?.cancel()
        timer = null
    }


    interface NetSpeedCallback {
        fun onNetSpeedChange(downloadSpeed: String, uploadSpeed: String)
    }
}
