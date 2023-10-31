package com.nextweb.nwapplogger

import android.annotation.SuppressLint
import android.os.Build

/**
 * @author nextweb
 *
 */
@SuppressLint("NewApi")
class AndroidDevice {
    companion object {
        val BUILD_BOARD = Build.BOARD
        val BUILD_BRAND = Build.BRAND
        val BUILD_DEVICE = Build.DEVICE
        val BUILD_DISPLAY = Build.DISPLAY
        val BUILD_FINGERPRINT = Build.FINGERPRINT
        val BUILD_HOST = Build.HOST
        val BUILD_ID = Build.ID
        val BUILD_MANUFACTURER = Build.MANUFACTURER
        val BUILD_MODEL = Build.MODEL
        val BUILD_PRODUCT = Build.PRODUCT
        val BUILD_SERIAL = Build.SERIAL
        val BUILD_TAGS = Build.TAGS
        val BUILD_TIME = Build.TIME.toString()
        val BUILD_TYPE = Build.TYPE
        val BUILD_USER = Build.USER
        val BUILD_VERSION_RELEASE = Build.VERSION.RELEASE
        val BUILD_VERSION_SDK_INT = Build.VERSION.SDK_INT.toString()
        val BUILD_VERSION_CODENAME = Build.VERSION.CODENAME
        val BUILD_HARDWARE = Build.HARDWARE
    }
}