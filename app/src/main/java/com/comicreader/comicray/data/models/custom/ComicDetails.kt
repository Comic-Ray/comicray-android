package com.comicreader.comicray.data.models.custom

import android.os.Parcelable
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.BookType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComicDetails(
    var title: String,
    val url: String,
    val imageUrl: String,
    val latestIssue: Parcelable? = null,
    val status: String? = null,
    val totalIssues: Int? = null,
    val yearOfRelease: Int? = null
) : Parcelable

fun ComicDetails.toDataItem(): DataItem = DataItem(
    title = title,
    imageUrl = imageUrl,
    url = url,
    type = BookType.Manga,
    additionalData = this
)