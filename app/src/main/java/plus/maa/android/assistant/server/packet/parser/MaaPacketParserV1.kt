package plus.maa.android.assistant.server.packet.parser

import android.util.Log
import plus.maa.android.assistant.server.packet.MAA_FUNCTION_SCREENSHOT
import plus.maa.android.assistant.server.task.IMaaTask
import plus.maa.android.assistant.server.task.MaaScreenshotTaskV1
import plus.maa.android.assistant.support.readTotally
import java.io.InputStream

object MaaPacketParserV1 : IMaaPacketParser {

    override fun readAndParse(function: Int, inputStream: InputStream): IMaaTask? {
        return when (function) {
            MAA_FUNCTION_SCREENSHOT -> parseScreenshot(inputStream)
            else -> null
        }
    }

    private fun parseScreenshot(inputStream: InputStream): MaaScreenshotTaskV1 {
        inputStream.readTotally(4)
        Log.i("MaaPacketParser", "Packet parse success, function: Screenshot.")
        return MaaScreenshotTaskV1()
    }
}