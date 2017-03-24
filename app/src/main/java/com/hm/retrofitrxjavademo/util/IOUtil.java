package com.hm.retrofitrxjavademo.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by dumingwei on 2017/3/24.
 */
public class IOUtil {

    public static void closeAll(Closeable... closeables) {

        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
