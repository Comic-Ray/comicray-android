package com.comicreader.comicray.data.models.comicDetails

import com.comicreader.comicray.data.models.Genre

data class ComicDetailsResponse(
    val alternateName: String,
    val author: String,
    val genres: List<Genre.Comic>,
    val imageUrl: String,
    val issues: List<Issue>,
    val recommended: List<Recommended>,
    val status: String,
    val summary: String,
    val title: String,
    val url: String,
    val yearOfRelease: Int
)