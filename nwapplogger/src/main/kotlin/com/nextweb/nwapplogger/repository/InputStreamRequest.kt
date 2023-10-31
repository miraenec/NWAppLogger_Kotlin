package com.nextweb.nwapplogger.repository

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser.*
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

/**
 * @author nextweb
 *
 */
class InputStreamRequest(
    method: Int,
    url: String,
    headers: Map<String, String>,
    params: Map<String, Any?>,
    errorListener: Response.ErrorListener,
    listener: Response.Listener<JSONObject>
) :
    Request<JSONObject>(method, url, errorListener) {
    private val mHeaders: Map<String, String>?
    private val mParams: Map<String, Any?>
    private val mErrorListener: Response.ErrorListener
    private val mListener: Response.Listener<JSONObject>

    //======================================================================
    // Constructor
    //======================================================================
    init {
        // this request would never use cache since you are fetching the file content from server
        setShouldCache(false)
        mHeaders = headers
        mParams = params
        this.mErrorListener = errorListener
        mListener = listener
    }

    //======================================================================
    // Override Methods
    //======================================================================
    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return if (mHeaders != null && mHeaders.size > 0) {
            mHeaders
        } else super.getHeaders()
    }

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return mParams.ifEmpty { super.getParams() } as Map<String, String>
    }

    override fun deliverResponse(response: JSONObject) {
        mListener.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        mErrorListener.onErrorResponse(error)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        return try {
            val json = response.data.toString(Charsets.UTF_8)
            Response.success(
                JSONObject(json),
                parseCacheHeaders(response)
            ) as Response<JSONObject>
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        }
    }
}