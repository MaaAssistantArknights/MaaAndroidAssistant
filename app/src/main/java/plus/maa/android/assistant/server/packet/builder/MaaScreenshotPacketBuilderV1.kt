package plus.maa.android.assistant.server.packet.builder

import plus.maa.android.assistant.server.packet.MAA_FUNCTION_SCREENSHOT
import plus.maa.android.assistant.server.packet.MAA_VERSION_1

object MaaScreenshotPacketBuilderV1 {

    fun build(width: Int, height: Int, bytes: ByteArray): ByteArray {
        // width + height + image data
        val payloadSize = 2 + 2 + bytes.size

        // head + payload size + payload
        val packet = ByteArray(1 + 4 + payloadSize)

        packet[0] = ((MAA_FUNCTION_SCREENSHOT shl 4) or MAA_VERSION_1).toByte()

        packet[1] = ((payloadSize shr 0) and 0xff).toByte()
        packet[2] = ((payloadSize shr 8) and 0xff).toByte()
        packet[3] = ((payloadSize shr 16) and 0xff).toByte()
        packet[4] = ((payloadSize shr 24) and 0xff).toByte()

        packet[5] = (width shr 0).toByte()
        packet[6] = (width shr 8).toByte()

        packet[7] = (height shr 0).toByte()
        packet[8] = (height shr 8).toByte()

        System.arraycopy(bytes, 0, packet, 9, bytes.size)

        return packet
    }
}