package com.nextweb.nwapplogger.repository

import android.content.Context
import com.nextweb.nwapplogger.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author nextweb
 *
 */
class SendLogRepository(
    private val networkService: NetworkService
) {
    companion object {
        var BASE_URL = ""
    }

    suspend fun sendAppInfo(context: Context, data: AppInfo): String
    = withContext(Dispatchers.Default) {
        return@withContext networkService.sendHttps(context, BASE_URL, data)
    }
}