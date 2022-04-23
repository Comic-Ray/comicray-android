package com.comicreader.comicray.data.models

import android.os.Parcelable

data class DataItem(
    val title: String,
    val imageUrl: String,
    val type: GenreType,
    val additionalData: Parcelable? = null
)
