package com.comicreader.comicray.data.models.custom

import android.os.Parcelable
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.GenreType
import kotlinx.parcelize.Parcelize

@Parcelize
data class MangaDetail(
    val imageUrl: String,
    val latestChapter: LatestChapter,
    val summary: String,
    val title: String,
    val totalViews: Int,
    val url: String
) : Parcelable {
    @Parcelize
    data class LatestChapter(
        val title: String,
        val url: String
    ) : Parcelable
}

data class MangaGenreResponse(
    val category: Category,
    val data: List<MangaDetail>,
    val page: Int,
    val state: String,
    val totalPages: Int,
    val type: String
) {
    data class Category(
        val category: String,
        val name: String
    )
}

fun MangaDetail.toDataItem(): DataItem = DataItem(
    title = title,
    imageUrl = imageUrl,
    type = GenreType.Manga,
    additionalData = this
)