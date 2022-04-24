package com.comicreader.comicray.data.models.custom

import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.Genre.Companion.toGenre
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.mangaDetails.MangaDetailsResponse

data class CommonDetailResponse(
    val title: String,
    val status: String,
    val author: String,
    val imageUrl: String,
    val description: String,
    val genres: List<Genre>,
)

fun MangaDetailsResponse.toCommonDetail() = CommonDetailResponse(
    title = title,
    status = status,
    author = authors.getOrNull(0)?.name ?: "Unknown",
    imageUrl = imageUrl,
    description = summary,
    genres = genres.map { it.toGenre() }
)

fun ComicDetailsResponse.toCommonDetail() = CommonDetailResponse(
    title = title,
    status = status,
    author = author,
    imageUrl = imageUrl,
    description = summary,
    genres = genres.map { it.toGenre() }
)