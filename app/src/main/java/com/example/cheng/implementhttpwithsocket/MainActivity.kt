package com.example.cheng.implementhttpwithsocket

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.Socket

/**
 *  用socket发http请求，并收到回复。
 * @author chengxiaobo
 * @time 2019/5/24 21:50
 */
class MainActivity : AppCompatActivity() {

    var handler: Handler? = null
    val MESSAGE_RECEIVER = 2
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGetData.setOnClickListener {
            //请求数据
            getDataWithSockect()
        }

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                msg?.let {
                    if (it.what == MESSAGE_RECEIVER) {
                        tvData.append(msg.obj.toString())
                        tvData.append("\n")
                    }
                }
            }
        }
    }

    private fun getDataWithSockect() {
        object : Thread() {
            override fun run() {
                super.run()
                connectToTcpServer()
            }
        }.start()
    }

    private fun connectToTcpServer() {
        var socket: Socket? = null
        var printWriter: PrintWriter? = null
        while (socket == null) {
            try {
                socket = Socket("www.baidu.com", 80)
                printWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                printWriter.println("GET / HTTP/1.0\n" +
                        "host: www.baidu.com\n" +
                        "Accept: text/html, application/xhtml+xml, application/xml; q=0.9, */*; q=0.8\n" +
                        "Accept-Encoding: gzip, deflate, br\n" +
                        "Accept-Language: zh-CN\n" +
                        "Connection: Keep-Alive\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763\n\n")
            } catch (e: IOException) {
                SystemClock.sleep(1000)
                log(TAG, "connect tcp server failed, retry...")
            }
        }

        try {
            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (!MainActivity@ this.isFinishing) {
                val msg = bufferedReader.readLine()
                msg?.let {
                    handler?.obtainMessage(MESSAGE_RECEIVER, msg)?.sendToTarget()
                    log(TAG, msg)
                }
            }

            close(printWriter)
            close(bufferedReader)
            socket.close()

        } catch (e: IOException) {
            e.printStackTrace()
            e.message?.let {
                log(TAG, it)
            }
        }
    }
}
