package com.comicreader.comicray.data.models.custom

import android.os.Parcelable
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.DataItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComicDetails(
    var title: String,
    val url: String,
    val imageUrl: String,
    val latestIssue: LatestIssue? = null,
    val status: String? = null,
    val totalIssues: Int? = null,
    val yearOfRelease: Int? = null
) : Parcelable {
    @Parcelize
    data class LatestIssue(
        val title: String,
        val rawName: String,
        val url: String,
        val date: String
    ) : Parcelable
}

fun ComicDetails.toDataItem(): DataItem = DataItem(
    title = title,
    imageUrl = imageUrl,
    url = url,
    type = BookType.Manga,
    additionalData = this
)