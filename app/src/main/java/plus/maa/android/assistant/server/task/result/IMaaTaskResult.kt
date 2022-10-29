package plus.maa.android.assistant.server.task.result

import java.io.IOException
import java.io.OutputStream

interface IMaaTaskResult {
    @Throws(IOException::class)
    fun write(outputStream: OutputStream)
}