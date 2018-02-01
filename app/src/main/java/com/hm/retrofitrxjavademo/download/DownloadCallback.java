package com.hm.retrofitrxjavademo.download;

import java.io.File;

public interface DownloadCallback {

    void onSuccess(File file);

    void onFailed();

}