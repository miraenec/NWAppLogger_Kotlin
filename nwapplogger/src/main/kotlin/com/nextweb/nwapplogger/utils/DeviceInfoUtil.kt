package com.nextweb.nwapplogger.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.nextweb.nwapplogger.enum.DeviceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID

/**
 * @author nextweb
 *
 */
class DeviceInfoUtil private constructor() {

    val HEXES = "0123456789abcdef"

    companion object {
        val instance = DeviceInfoUtil()
    }

    fun getResolution(act: Activity): String {
        val width = act.resources.displayMetrics.widthPixels
        val height = act.resources.displayMetrics.heightPixels
        return width.toString() + "x" + height
    }

    suspend fun getUniqueADID(act: Activity?): String? {
        val value: String = GlobalScope.async(Dispatchers.Default) {
            try {
                var adidStr: String? = null
                val advertisingIdInfo: AdvertisingIdClient.Info? =
                    act?.let { AdvertisingIdClient.getAdvertisingIdInfo(it) }
                if (advertisingIdInfo != null) {
                    if ((advertisingIdInfo?.isLimitAdTrackingEnabled == false)) {
                        advertisingIdInfo.id.toString().also { adidStr = it }
                        adidStr
//                    NWLogger.setVid(advertisingIdInfo.getId())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            }
            ""
        }.await()

        return value
    }

    fun getUniqueDeviceId(act: Activity): String {
        return try {
            val  telephonyManager = act.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ContextCompat.checkSelfPermission(act, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
                // Permission is  granted
                val imei : String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei.toString()
                } else { // older OS  versions
                    telephonyManager.deviceId
                }

                imei?.let {
                    Log.i("Log", "DeviceId=$imei" )
                }

                try {
                    val tmSerial = "" + telephonyManager.simSerialNumber
                    val androidId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        Settings.Secure.getString(
                            act.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    } else {}

                    val deviceUuid = UUID(
                        androidId.hashCode().toLong(),
                        imei.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
                    )

                    // Log.d("getUniqueDeviceId", uuid);
                    return deviceUuid.toString()
                } catch (ex: NullPointerException) {
                    val digest =
                        MessageDigest.getInstance("SHA-256")
                    digest.reset()
                    return getHex(digest.digest(imei?.toByteArray(charset("UTF-8"))))
                }
            } else {  // Permission is not granted
                return DeviceType.UnknowDevice.description
            }
        } catch (ex: Exception) {
            DeviceType.UnknowDevice.description
        }
    }

    fun isNotUnknownDevice(act: Activity): Boolean {
        val deviceId = getUniqueDeviceId(act)
        return !(DeviceType.UnknowDevice.description == deviceId && "null".equals(
                deviceId,
                ignoreCase = true
            ) && deviceId == null)
    }

    fun getMacAddress(context: Context): String {
        var macAddress = ""
        var bIsWifiOff = false
        val wfManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wfManager.isWifiEnabled) {
            // wfManager.setWifiEnabled(true);
            bIsWifiOff = true
        }
        val wfInfo = wfManager.connectionInfo
        macAddress = wfInfo.macAddress
        if (bIsWifiOff) {
            // wfManager.setWifiEnabled(false);
            bIsWifiOff = false
        }
        return macAddress
    }

    private fun getHex(raw: ByteArray): String {
        val hex = StringBuilder(2 * raw.size)
        for (b in raw) {
            hex.append(HEXES[b.toInt() and 0xF0 shr 4]).append(
                HEXES[b.toInt() and 0x0F]
            )
        }
        return hex.toString()
    }

    fun getCountry(context: Context): String {
        val systemLocale = context.resources.configuration.locale
        return systemLocale.country
    }

    fun getLanguage(context: Context): String {
        val systemLocale = context.resources.configuration.locale
        return systemLocale.language
    }

    fun getCarrier(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName
    }
}
