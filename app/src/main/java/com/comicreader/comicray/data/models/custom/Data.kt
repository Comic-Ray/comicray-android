package com.comicreader.comicray.data.models.custom

data class Data(
    val imageUrl: String,
    val latestIssue: Any?=null,
    val status: String,
    val title: String,
    val totalIssues: Int,
    val url: String,
    val yearOfRelease: Int
)