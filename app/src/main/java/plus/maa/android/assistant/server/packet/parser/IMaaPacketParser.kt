package plus.maa.android.assistant.server.packet.parser

import plus.maa.android.assistant.server.task.IMaaTask
import java.io.InputStream

interface IMaaPacketParser {
    fun readAndParse(function: Int, inputStream: InputStream): IMaaTask?
}