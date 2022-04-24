package com.comicreader.comicray.data.models.custom

import android.os.Parcelable
import com.comicreader.comicray.data.models.BookType

data class ComicSearchResponse(val page: Int, val totalPages: Int, val data: List<ComicDetails>)
data class MangaSearchResponse(val page: Int, val totalPages: Int, val data: List<MangaDetail>)

data class SearchCommon(
    val query: String,
    val type: BookType,
    val additionalSearchData: Parcelable
)