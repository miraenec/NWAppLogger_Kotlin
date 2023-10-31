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
                    shared().volleyImageCache
                )
            }
            return mImageLoader!!
        }
    private val volleyImageCache: LruBitmapCache
        private get() {
            if (mLruBitmapCache == null) {
                mLruBitmapCache = LruBitmapCache(shared())
            }
            return mLruBitmapCache!!
        }

    companion object Factory {
        private var mContext: Context? = null

        private fun shared(): AppRequest = AppRequest()

        private fun addRequest(request: Request<*>) {
            shared().volleyRequestQueue.add(request)
        }

        fun addRequest(context: Context, request: Request<*>, tag: String) {
            mContext = context
            request.tag = tag
            addRequest(request)
        }

        fun cancelAllRequests(tag: String) {
            shared().volleyRequestQueue.cancelAll(tag)
        }
    }
}