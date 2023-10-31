package com.nextweb.nwapplogger.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.util.Log

/**
 * @author nextweb
 *
 */
class AppInfoUtil private constructor() {
    @SuppressLint("NewApi")
    fun getApplicationName(ctx: Context): String {
        val stringId = ctx.applicationInfo.labelRes
        val appName = ctx.getString(stringId)
        return appName ?: ""
    }

    fun getActivityName(ctx: Context): String {
        return try {
            var label: String? = null
            val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = am.getRunningTasks(1)
            if (Utils.isDebug) {
                Log.d(
                    "topActivity", "CURRENT Activity ::"
                            + taskInfo[0].topActivity!!.className
                )
            }
            val componentInfo = taskInfo[0].topActivity
            label = componentInfo!!.className
            label ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getAppRunCount(ctx: Context): String {
        val pref = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val nVal = pref.getInt("appRunCount", 0)
        return nVal.toString()
    }

    fun addAppRunCount(ctx: Context) {
        val pref = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var nVal = pref.getInt("appRunCount", 0)
        nVal += 1
        val editor = pref.edit()
        editor.putInt("appRunCount", nVal)
        editor.commit()
    }

    fun getReferrer(ctx: Context): String? {
        val pref =
            ctx.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getString("referrer", "")
    }

    companion object {
        val instance = AppInfoUtil()
    }
}