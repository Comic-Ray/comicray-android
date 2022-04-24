package com.comicreader.comicray.data.models.mangaDetails

import com.comicreader.comicray.data.models.Genre

data class MangaDetailsResponse(
    val authors: List<Author>,
    val chapters: List<Chapter>,
    val genres: List<Genre.Manga>,
    val imageUrl: String,
    val lastUpdated: String,
    val rating: Double,
    val status: String,
    val summary: String,
    val title: String,
    val totalViews: Int,
    val url: String
)