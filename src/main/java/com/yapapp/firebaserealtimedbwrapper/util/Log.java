package com.yapapp.firebaserealtimedbwrapper.util;

/**
 * Created by anuj on 26/5/17.
 */
public class Log {

    private static boolean print = true;                                             // true for printing and false for not

    public Log() {
    }

    public static void setLogs(boolean print) {
        Log.print = print;
    }

    public static void i(String TAG, String message) {
        if (print) {
            android.util.Log.i(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        if (print) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void e(String TAG, String message) {
        if (print) {
            android.util.Log.e(TAG, message);
        }
    }


    public static void v(String TAG, String message) {
        if (print) {
            android.util.Log.v(TAG, message);
        }
    }

    public static void w(String TAG, String message) {
        if (print) {
            android.util.Log.w(TAG, message);
        }
    }
}