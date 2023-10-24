package com.nextweb.nwapplogger

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.RemoteException
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.nextweb.nwapplogger.helper.DbOpenHelper
import com.nextweb.nwapplogger.`interface`.INWAppLogger
import com.nextweb.nwapplogger.model.AppInfo
import com.nextweb.nwapplogger.repository.NetworkService
import com.nextweb.nwapplogger.repository.SendLogRepository
import com.nextweb.nwapplogger.utils.AppInfoUtil
import com.nextweb.nwapplogger.utils.DeviceInfoUtil
import com.nextweb.nwapplogger.utils.Utils
import com.nextweb.nwapplogger.utils.Utils.Companion.isDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author nextweb
 *
 */
class NWAppLogger() : INWAppLogger() {

    private var mContext: Context? = null
    private var appInfo: AppInfo = AppInfo()
    private var referrerClient: InstallReferrerClient? = null

    interface OnGoogleReferrerDataCallback {
        fun onGoogleReferrerData(result: Boolean)
    }

    private var mCallback: OnGoogleReferrerDataCallback? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    companion object {
        val instance = NWAppLogger()
    }

    private fun initDefaultInfo(act: Activity) {
        mContext = act
    }

    private fun tryToConnectReferrer(act: Activity) {
        // 리퍼러 연결
        referrerClient = InstallReferrerClient.newBuilder(act).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                if (mHandler != null) {
                    mHandler!!.removeCallbacks(mRunnable!!)
                }
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK ->
                        /**
                         *
                         * 구글플레이 앱과 연결이 성공했을 때에 구글 인스톨 리퍼러 데이터를 얻어 오기 위한 작업을 수행합니다.
                         *
                         */
                        try {
                            val response = referrerClient?.getInstallReferrer()
                            val referrerUrl = response?.installReferrer
                            val referrerClickTime = response?.referrerClickTimestampSeconds
                            val appInstallTime = response?.installBeginTimestampSeconds
                            val pref = mContext!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putString("referrer", referrerUrl)
                            editor.commit()
                            referrerClient?.endConnection()
                            mCallback?.onGoogleReferrerData(true)
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED, InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE ->                         // Connection could'nt be established
                        mCallback?.onGoogleReferrerData(false)
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Google Play by calling the startConnection() method.
            }
        })
        mRunnable = Runnable { mCallback?.onGoogleReferrerData(false) }
        mHandler = Handler()
        mHandler?.postDelayed(mRunnable!!, 3000)
    }

    override fun init(act: Activity, isDebug: Boolean, url: String) {
        if (act != null) {
            initDefaultInfo(act)

            Utils.isDebug = isDebug
            SendLogRepository.BASE_URL = url

            try {
                if (act.javaClass.name.contains("OnGoogleReferrerDataCallback")) {
                    mCallback = act as OnGoogleReferrerDataCallback
                }
            } catch (e: Error) {
            }

            // 앱 실행 횟수
            AppInfoUtil.instance.addAppRunCount(act)

            // 리퍼러 정보 가져오기
            tryToConnectReferrer(act)
        }
    }

    override fun setParameters(type: String, userId: String, sessionId: String) {
        isDebug = type == "0"

        appInfo.xtUid = userId
        appInfo.ssId = sessionId
    }

    override fun <T : Activity> start(act: T) {
        initDefaultInfo(act)
        CoroutineScope(Dispatchers.IO).launch {
            sendLog(act, "APP_START")
        }
    }

    override fun <T : Activity> clear(act: T) {
        initDefaultInfo(act)
        CoroutineScope(Dispatchers.IO).launch {
            sendLog(act, "APP_FINISH")
        }
        DbOpenHelper.instance.close()
    }

    override fun <T : Activity> resume(act: T) {
        initDefaultInfo(act)
        CoroutineScope(Dispatchers.IO).launch {
            sendLog(act, "APP_RESUME")
        }
    }

    override fun <T : Activity> pause(act: T) {
        initDefaultInfo(act)
        CoroutineScope(Dispatchers.IO).launch {
            sendLog(act, "APP_PAUSE")
        }
    }

    override fun <T : Activity> sendLog(act: T, buildStr: String) {
        if (isDebug) {
            Log.d("NWAppLogger", "sendLog : $buildStr")
        }
        CoroutineScope(Dispatchers.IO).launch {
            sendLog(act, mapOf(), buildStr)
        }
    }

    override suspend fun <T : Activity> sendLog(act: T, params: Map<String, String>, buildStr: String) {
        initDefaultInfo(act)

        val context = mContext!!

        appInfo.osType = "android"
        appInfo.appName = AppInfoUtil.instance.getApplicationName(context)
        appInfo.activityName = AppInfoUtil.instance.getActivityName(context)
        appInfo.packageName = mContext!!.packageName

        if (act != null) {
            appInfo.resolution = DeviceInfoUtil.instance.getResolution(act)
            appInfo.className = act.localClassName
        }
        appInfo.macAddress = DeviceInfoUtil.instance.getMacAddress(context)
        appInfo.appRunCount = AppInfoUtil.instance.getAppRunCount(context)
        appInfo.referrer = AppInfoUtil.instance.getReferrer(context).toString()
        appInfo.buildBoard = AndroidDevice.BUILD_BOARD
        appInfo.buildBrand = AndroidDevice.BUILD_BRAND
        appInfo.buildDevice = AndroidDevice.BUILD_DEVICE
        appInfo.buildDisplay = AndroidDevice.BUILD_DISPLAY
        appInfo.buildFingerprint = AndroidDevice.BUILD_FINGERPRINT
        appInfo.buildHost = AndroidDevice.BUILD_HOST
        appInfo.buildId = AndroidDevice.BUILD_ID
        appInfo.buildManufacturer = AndroidDevice.BUILD_MANUFACTURER
        appInfo.buildModel = AndroidDevice.BUILD_MODEL
        appInfo.buildProduct = AndroidDevice.BUILD_PRODUCT
        appInfo.buildSerial = AndroidDevice.BUILD_SERIAL
        appInfo.buildTags = AndroidDevice.BUILD_TAGS
        appInfo.buildTime = AndroidDevice.BUILD_TIME
        appInfo.buildType = AndroidDevice.BUILD_TYPE
        appInfo.buildUser = AndroidDevice.BUILD_USER
        appInfo.buildVersionRelease = AndroidDevice.BUILD_VERSION_RELEASE
        appInfo.buildVersionSdkInt = AndroidDevice.BUILD_VERSION_SDK_INT
        appInfo.buildVersionCodename = AndroidDevice.BUILD_VERSION_CODENAME
        appInfo.buildHardware = AndroidDevice.BUILD_HARDWARE
        appInfo.countryCd = DeviceInfoUtil.instance.getCountry(context)
        appInfo.language = DeviceInfoUtil.instance.getLanguage(context)
        appInfo.carrier = DeviceInfoUtil.instance.getCarrier(context)

        if (isDebug) {
            Log.d("NWAppLogger", "sendLog : $appInfo")
        }
        SendLogRepository(NetworkService()).sendAppInfo(act, appInfo)
    }

    override fun setUserId(uid: String) {
        appInfo.xtUid = uid
    }

    override fun setSessionId(ssid: String) {
        appInfo.ssId = ssid
    }

    override fun setVid(vid: String) {
        appInfo?.xtVid = vid
    }

    override fun setPrintLog(isPrint: Boolean) {
        isDebug = isPrint
    }

}