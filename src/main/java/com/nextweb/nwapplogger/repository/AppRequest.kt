package com.nextweb.nwapplogger.repository

import android.app.Application
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.nextweb.nwapplogger.utils.LruBitmapCache

/**
 * @author nextweb
 *
 */
class AppRequest : Application() {
    private var mRequestQueue: RequestQueue? = null
    private var mLruBitmapCache: LruBitmapCache? = null
    private var mImageLoader: ImageLoader? = null

    val volleyRequestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext, OkHttp3Stack())
            }
            return mRequestQueue!!
        }
    val volleyImageLoader: ImageLoader
        get() {
            if (mImageLoader == null) {
                mImageLoader = ImageLoader(
                    volleyRequestQueue,
                    sInstance!!.volleyImageCache
                )
            }
            return mImageLoader!!
        }
    private val volleyImageCache: LruBitmapCache
        private get() {
            if (mLruBitmapCache == null) {
                mLruBitmapCache = LruBitmapCache(sInstance!!)
            }
            return mLruBitmapCache!!
        }

    companion object {
        private var mContext: Context? = null

        @Volatile
        private var sInstance: AppRequest? = null
            get() {
                synchronized(AppRequest::class.java) {
                    if (sInstance == null) {
                        sInstance = AppRequest()
                    }
                }
                return sInstance
            }

        private fun addRequest(request: Request<*>) {
            sInstance!!.volleyRequestQueue.add(request)
        }
    }

    fun addRequest(context: Context, request: Request<*>, tag: String) {
        mContext = context
        request.tag = tag
        addRequest(request)
    }

    fun cancelAllRequests(tag: String) {
        sInstance!!.volleyRequestQueue.cancelAll(tag)
    }
}