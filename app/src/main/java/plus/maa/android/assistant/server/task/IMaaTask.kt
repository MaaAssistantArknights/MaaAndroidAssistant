package plus.maa.android.assistant.server.task

import plus.maa.android.assistant.server.task.result.IMaaTaskResult

interface IMaaTask {
    fun exec(): IMaaTaskResult
}