package com.comicreader.comicray.data.models.custom

import android.os.Parcelable
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.DataItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class MangaDetail(
    val authors: List<String>,
    val chapters: List<Chapter>,
    val imageUrl: String,
    val lastUpdated: String,
    val rating: Double,
    val title: String,
    val totalViews: Int,
    val url: String
) : Parcelable {
    @Parcelize
    data class Chapter(
        val name: String,
        val url: String
    ) : Parcelable
}

@Parcelize
data class MangaGenre(
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
    val data: List<MangaGenre>,
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

fun MangaGenre.toDataItem(): DataItem = DataItem(
    title = title,
    imageUrl = imageUrl,
    url = url,
    type = BookType.Manga,
    additionalData = this
)