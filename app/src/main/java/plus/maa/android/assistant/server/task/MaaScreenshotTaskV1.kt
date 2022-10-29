package plus.maa.android.assistant.server.task

import android.util.Log
import plus.maa.android.assistant.server.MaaServer
import plus.maa.android.assistant.server.task.result.IMaaTaskResult
import plus.maa.android.assistant.server.task.result.MaaScreenshotTaskResultV1

class MaaScreenshotTaskV1 : IMaaTask {

    override fun exec(): IMaaTaskResult {
        val result = MaaScreenshotTaskResultV1()
        MaaServer.screenshot(result.callback)
        Log.i("MaaScreenshotTaskV1", "execute task.")
        return result
    }
}