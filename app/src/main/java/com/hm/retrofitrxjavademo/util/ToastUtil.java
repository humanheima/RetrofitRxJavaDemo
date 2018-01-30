package com.hm.retrofitrxjavademo.util;

import android.widget.Toast;

import com.hm.retrofitrxjavademo.App;

/**
 * Created by dumingwei on 2018/1/30 0030.
 */

public class ToastUtil {

    private static Toast toast;

    public static void toast(String text) {
        if (toast == null) {
            toast = Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }
}
