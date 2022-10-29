package plus.maa.android.assistant.support

import java.io.InputStream

fun InputStream.readTotally(size: Int): ByteArray {
    val bytes = ByteArray(size)
    readTotally(bytes)
    return bytes
}

fun InputStream.readTotally(bytes: ByteArray) {
    val size = bytes.size
    var len = 0
    var readSize = 0

    while (readSize < size
        && read(bytes, readSize, size - readSize)
            .also { len = it } != -1
    ) {
        readSize += len
    }
}