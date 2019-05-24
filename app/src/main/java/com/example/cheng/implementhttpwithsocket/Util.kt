package com.example.cheng.implementhttpwithsocket

import android.util.Log
import java.io.Closeable
import java.io.IOException


/**
 *  工具类
 * @author chengxiaobo
 * @time 2019/5/24 21:56
 */
fun close(closeable: Closeable?) {
    try {
        closeable?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun log(tag: String, s: String) {
    Log.e(tag, s)
}