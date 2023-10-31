package com.nextweb.nwapplogger.`interface`

import android.app.Activity

/**
 * @author nextweb
 *
 */
abstract class INWAppLogger {
    abstract fun init(act: Activity, isDebug: Boolean, url: String)
    abstract fun setParameters(type: String, userId: String, sessionId: String)
    abstract fun <T : Activity> start(act: T)
    abstract fun <T : Activity> clear(act: T)
    abstract fun <T : Activity> resume(act: T)
    abstract fun <T : Activity> pause(act: T)
    abstract fun <T : Activity> sendLog(act: T, buildStr: String)
    abstract suspend fun <T : Activity> sendLog(act: T, params: Map<String, String>, buildStr: String)

    abstract fun setUserId(uid: String)
    abstract fun setSessionId(ssid: String)
    abstract fun setVid(vid: String)
    abstract fun setPrintLog(isPrint: Boolean)
}