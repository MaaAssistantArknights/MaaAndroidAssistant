package plus.maa.android.assistant.server.task

interface MaaServerCallback {
    fun onServerStartSuccess()
    fun onServerStartFailed(e: Exception)
}