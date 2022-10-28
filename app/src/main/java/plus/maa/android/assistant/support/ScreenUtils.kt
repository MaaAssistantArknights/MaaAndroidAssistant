package plus.maa.android.assistant.support

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager

fun Context.getScreenSize(): Rect {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        wm.currentWindowMetrics.bounds
    } else {
        val display = wm.defaultDisplay
        val rect = Rect()
        display.getRectSize(rect)
        rect
    }
}