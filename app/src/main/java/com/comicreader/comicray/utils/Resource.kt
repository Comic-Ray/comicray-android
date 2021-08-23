package com.comicreader.comicray.utils

sealed class Resource<T>(
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T?=null) : Resource<T>(data)
    class Error<T>(t : Throwable, data: T?=null) : Resource<T>(data,t)
}
