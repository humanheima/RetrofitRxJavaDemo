package com.hm.retrofitrxjavademo.upload;

/**
 * Created by p_dmweidu on 2023/9/25
 * Desc: 上传进度监听
 */
public interface UpLoadProgressListener {

    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
