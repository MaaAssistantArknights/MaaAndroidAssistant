package plus.maa.android.assistant.support

import android.graphics.Color

fun createBMP(width: Int, height: Int, bytes: ByteArray): ByteArray {
    val bmp = ByteArray(14 + 40 + bytes.size)

    /**
     * BMP格式文件头
     */
    //固定文件头
    bmp[0] = 0x42
    bmp[1] = 0x4d

    //文件大小
    bmp[2] = (bmp.size shr 0).toByte()
    bmp[3] = (bmp.size shr 8).toByte()
    bmp[4] = (bmp.size shr 16).toByte()
    bmp[5] = (bmp.size shr 24).toByte()

    //保留位
    bmp[6] = 0
    bmp[7] = 0
    bmp[8] = 0
    bmp[9] = 0

    //从头到位图数据的偏移量
    bmp[10] = 0x36
    bmp[11] = 0
    bmp[12] = 0
    bmp[13] = 0

    /**
     * BMP格式信息头
     */
    //信息头大小
    bmp[14] = 0x28
    bmp[15] = 0
    bmp[16] = 0
    bmp[17] = 0

    //图片宽度
    bmp[18] = (width shr 0).toByte()
    bmp[19] = (width shr 8).toByte()
    bmp[20] = (width shr 16).toByte()
    bmp[21] = (width shr 24).toByte()

    //图片高度
    bmp[22] = (height shr 0).toByte()
    bmp[23] = (height shr 8).toByte()
    bmp[24] = (height shr 16).toByte()
    bmp[25] = (height shr 24).toByte()

    //颜色平面数
    bmp[26] = 0x01
    bmp[27] = 0

    //图片位数
    bmp[28] = 0x20    //32位
    bmp[29] = 0

    //压缩类型
    bmp[30] = 0    //不压缩
    bmp[31] = 0
    bmp[32] = 0
    bmp[33] = 0

    //位图数据大小
    bmp[34] = 0
    bmp[35] = 0
    bmp[36] = 0
    bmp[37] = 0

    //水平分辨率
    bmp[38] = 0
    bmp[39] = 0
    bmp[40] = 0
    bmp[41] = 0

    //垂直分辨率
    bmp[42] = 0
    bmp[43] = 0
    bmp[44] = 0
    bmp[45] = 0

    //使用的颜色索引数
    bmp[46] = 0
    bmp[47] = 0
    bmp[48] = 0
    bmp[49] = 0

    //重要的颜色索引数
    bmp[50] = 0
    bmp[51] = 0
    bmp[52] = 0
    bmp[53] = 0

    val fixWidth = bytes.size / height / 4

    var offset = 54

    for (i in (bytes.size - 1) downTo fixWidth step fixWidth) {
        for (j in (i - fixWidth + 1) .. i) {
            bmp[offset++] = Color.blue(bytes[j].toInt()).toByte()
            bmp[offset++] = Color.green(bytes[j].toInt()).toByte()
            bmp[offset++] = Color.red(bytes[j].toInt()).toByte()
            bmp[offset++] = Color.alpha(bytes[j].toInt()).toByte()
        }
    }

//    System.arraycopy(bytes, 0, bmp, 54, bytes.size)

    return bmp
}