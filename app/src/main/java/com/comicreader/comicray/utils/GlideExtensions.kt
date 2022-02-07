package com.comicreader.comicray.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.comicreader.comicray.GlideApp
import com.comicreader.comicray.GlideRequests

fun ImageView.load(
    uri: String?,
    requestBuilder: GlideRequests? = null,
    onSuccess: ((Bitmap?) -> Unit)? = null,
    onError: ((GlideException?) -> Unit)? = null
) {
    val glideRequests = requestBuilder ?: GlideApp.with(this)
    glideRequests.asBitmap().load(uri)
        .listener(object :
            RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                onError?.invoke(e)
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onSuccess?.invoke(resource)
                return true
            }

        }).into(this)
}

fun ImageView.load(uri: String?) {
    Glide.with(this)
        .load(uri)
        .into(this)
}