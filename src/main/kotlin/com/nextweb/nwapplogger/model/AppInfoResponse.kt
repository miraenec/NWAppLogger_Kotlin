package com.nextweb.nwapplogger.model

import com.google.gson.annotations.SerializedName

/**
 * @author nextweb
 *
 */
data class AppInfoResponse(
    @SerializedName("result") val result: String
)

fun AppInfoResponse.asAppInfo(): String {
    return result
}