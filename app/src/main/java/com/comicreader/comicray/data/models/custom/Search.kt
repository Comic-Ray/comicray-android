package com.comicreader.comicray.data.models.custom

import com.comicreader.comicray.data.models.BookType
import java.io.Serializable

data class ComicSearchResponse(val page: Int, val totalPages: Int, val data: List<ComicDetail>) : Serializable
data class MangaSearchResponse(val page: Int, val totalPages: Int, val data: List<MangaDetail>) : Serializable

data class SearchCommon(
    val query: String,
    val type: BookType,
    val additionalSearchData: Serializable
)

fun ComicSearchResponse.toSearchCommon(query: String, type: BookType) = SearchCommon(
    query = query,
    type = type,
    additionalSearchData = this
)

fun MangaSearchResponse.toSearchCommon(query: String, type: BookType) = SearchCommon(
    query = query,
    type = type,
    additionalSearchData = this
)