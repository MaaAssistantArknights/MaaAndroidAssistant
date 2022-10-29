package plus.maa.android.assistant.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import plus.maa.android.assistant.ScreenCaptureService
import plus.maa.android.assistant.server.packet.parser.MaaPacketParser
import plus.maa.android.assistant.server.task.MaaServerCallback
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class MaaServer {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val threadPool = Executors.newFixedThreadPool(1)

    private val serverSocket = ServerSocket()

    var isRunning = false
        private set

    private val lock = Object()

    private var callback: MaaServerCallback? = null

    fun setCallback(callback: MaaServerCallback) {
        this.callback = callback
    }

    fun start(port: Int) {
        synchronized(lock) {
            if (isRunning) {
                mainHandler.post {
                    callback?.onServerStartFailed(
                        Exception("MaaServer is Running!")
                    )
                }
                return
            }
        }

        threadPool.execute {
            synchronized(lock) {
                try {
                    serverSocket.bind(InetSocketAddress(port))
                } catch (e: IOException) {
                    mainHandler.post {
                        callback?.onServerStartFailed(e)
                    }
                    return@execute
                }

                isRunning = true
                mainHandler.post {
                    callback?.onServerStartSuccess()
                }
            }

            while (!Thread.currentThread().isInterrupted) {
                onLoop()
            }
        }
    }

    fun stop() {
        synchronized(lock) {
            if (isRunning) {
                try {
                    serverSocket.close()
                } catch (_: IOException) {
                }
            }
        }
    }

    private fun onLoop() {
        var outerSocket: Socket? = null
        var outerIs: InputStream? = null
        var outerOs: OutputStream? = null
        try {
            val socket = serverSocket.accept().also { outerSocket = it }
            val inputStream = socket.getInputStream().also { outerIs = it }
            val outputStream = socket.getOutputStream().also { outerOs = it }
            val task = MaaPacketParser.readAndParse(inputStream)
            if (task != null) {
                val result = task.exec()
                result.write(outputStream)
            }
        } catch (_: Exception) {
        } finally {
            outerOs?.close()
            outerIs?.close()
            outerSocket?.close()
        }
    }

    companion object {

        private var controller: ScreenCaptureService.Controller? = null

        fun setScreenCaptureController(controller: ScreenCaptureService.Controller) {
            this.controller = controller
        }

        fun screenshot(callback: (width: Int, height: Int, byteArray: ByteArray) -> Unit) {
            if (controller == null) {
                callback(0, 0, ByteArray(0))
                return
            }

            controller!!.captureScreen(callback)
        }
    }

}