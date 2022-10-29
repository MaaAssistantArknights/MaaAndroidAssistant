package plus.maa.android.assistant.server.task.result

import android.util.Log
import plus.maa.android.assistant.server.packet.builder.MaaScreenshotPacketBuilderV1
import java.io.OutputStream

class MaaScreenshotTaskResultV1 : IMaaTaskResult {

    private class ScreenshotResult(
        val width: Int,
        val height: Int,
        val bytes: ByteArray
    )

    private val lock = Object()

    private var result: ScreenshotResult? = null

    val callback: (Int, Int, ByteArray) -> Unit = { width, height, bytes ->
        synchronized(lock) {
            if (width != 0 && height != 0) {
                result = ScreenshotResult(width, height, bytes)
            }
            lock.notify()
        }
    }

    override fun write(outputStream: OutputStream) {
        synchronized(lock) {
            if (result == null) {
                lock.wait()
            }

            if (result == null) {
                return
            }

            val finalRes = result!!

            val packet = MaaScreenshotPacketBuilderV1.build(
                finalRes.width,
                finalRes.height,
                finalRes.bytes
            )

            outputStream.write(packet)
            outputStream.flush()

            Log.i(
                "MaaScreenshotTaskResult",
                "Write packet, width: ${finalRes.width}, height: ${finalRes.height}, byteSize: ${finalRes.bytes.size}"
            )
        }
    }
}