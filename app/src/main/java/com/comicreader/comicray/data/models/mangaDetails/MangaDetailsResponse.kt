package com.comicreader.comicray.data.models.mangaDetails

data class MangaDetailsResponse(
    val authors: List<Author>,
    val chapters: List<Chapter>,
    val genres: List<Genre>,
    val imageUrl: String,
    val lastUpdated: String,
    val rating: Double,
    val status: String,
    val summary: String,
    val title: String,
    val totalViews: Int,
    val url: String
)