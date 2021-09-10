package com.comicreader.comicray.data.models.custom

data class ComicDetails(
    var title: String,
    val url: String,
    val imageUrl: String,
    val latestIssue: Any? = null,
    val status: String? = null,
    val totalIssues: Int? = null,
    val yearOfRelease: Int? = null
)