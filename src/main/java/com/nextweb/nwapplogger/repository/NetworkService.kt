package com.nextweb.nwapplogger.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.nextweb.nwapplogger.helper.DbOpenHelper
import com.nextweb.nwapplogger.model.AppInfo
import com.nextweb.nwapplogger.utils.Utils
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author nextweb
 *
 */
class NetworkService {
    fun sendHttp(urlStr: String, mDbOpenHelper: DbOpenHelper?) {
        try {
            val url = URL(urlStr)
            url.openConnection()
            Thread {
                try {
                    url.openStream()
                    if (Utils.isDebug) Log.i(
                        "NetworkService",
                        "sendHttp info(" + urlStr.length + " bytes)" + urlStr
                    )
                } catch (ex: IOException) {
                    // ex.printStackTrace();
                    if (Utils.isDebug) Log.e(
                        "NetworkService",
                        "error openConnection : " + ex.toString() + " (" + urlStr.length + " bytes)" + urlStr
                    )
                }
            }.start()
        } catch (ex: Exception) {
            // ex.printStackTrace();
            if (Utils.isDebug) Log.e(
                "NetworkService",
                "error sendHttp : " + ex.toString() + " (" + urlStr.length + " bytes)" + urlStr
            )
        }
    }

    @SuppressLint("LongLogTag")
    fun sendHttps(context: Context?, urlStr: String, data: AppInfo): String {
        try {
//            val mDbOpenHelper: DbOpenHelper = DbOpenHelper()
            trustAllHosts()

//			for (String ddd : params.keySet()) {
//				Log.d("", ddd);
//			}
            val uri = Uri.parse(urlStr)
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
//            val query = uri.query
            val params = data.asMap()//getQueryMap(query)
            val keys = params.keys
            for (key in keys) {
                println("Name=$key")
                println("Value=" + params[key])
                if (Utils.isDebug) {
                    Log.d("NetworkService", key + " " + params[key])
                }
            }
            val id = uri.getQueryParameter("id")
            val listner: Response.Listener<JSONObject> = object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {
                    if (Utils.isDebug) {
                        Log.d("NetworkService", "Response : $response")
                    }
                }
            }
            val errorListner: Response.ErrorListener = object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    if (Utils.isDebug) {
                        Log.i("NetworkService", "error " + error.message)
                    }
                }
            }
            var url = ""
            val urls = urlStr.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (urls.size > 1) {
                url = urls[0]
            }
            val jsonRequest = InputStreamRequest(
                Request.Method.POST,
                url, headers, params, errorListner, listner
            )
            context?.let { id?.let { it1 -> AppRequest().addRequest(it, jsonRequest, it1) } }
        } catch (e: Exception) {
            if (Utils.isDebug) Log.e(
                "sendHttps IOException error",
                " error : " + e.toString() + "(" + urlStr.length + " bytes)" + urlStr
            )
        }

        return ""
    }

    fun getQueryMap(query: String?): Map<String, String> {
        if (Utils.isDebug) Log.e(
            "NetworkService",
            " getQueryMap : $query"
        )

        val params = query!!.split("&".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val map: MutableMap<String, String> = HashMap()
        for (param in params) {
            val name = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            map[name] = value
        }
        return map
    }

    @SuppressLint("LongLogTag")
    private fun trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        })

        // Install the all-trusting trust manager
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        } catch (e: Exception) {
            if (Utils.isDebug) {
                Log.e("trustAllHosts Exception error", e.message!!)
            }
        }
    }
}