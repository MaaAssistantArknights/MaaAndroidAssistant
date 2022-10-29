package plus.maa.android.assistant.server.packet.parser

import android.util.Log
import plus.maa.android.assistant.server.task.IMaaTask
import java.io.IOException
import java.io.InputStream

object MaaPacketParser {

    @Throws(IOException::class)
    fun readAndParse(inputStream: InputStream): IMaaTask? {
        val head = inputStream.read()

        val version = head and 0b1111
        val function = head shr 4

        Log.i("MaaPacketParser", "version: $version, function: $function")

        return getParserImpl(version)?.readAndParse(function, inputStream)
    }

    private fun getParserImpl(version: Int): IMaaPacketParser? {
        return when (version) {
            1 -> MaaPacketParserV1
            else -> null
        }
    }
}