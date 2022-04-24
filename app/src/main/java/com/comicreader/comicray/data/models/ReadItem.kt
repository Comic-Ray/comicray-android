package com.comicreader.comicray.data.models

import com.comicreader.comicray.data.models.custom.ComicReadResponse
import com.comicreader.comicray.data.models.custom.MangaReadResponse

data class ReadItem(
    val title: String,
    val issueName: String,
    val type: BookType,
    val imageUrls: List<String>
)

fun MangaReadResponse.toReadItem() = ReadItem(
    title = title,
    issueName = issueName,
    type = BookType.Manga,
    imageUrls = images
)

fun ComicReadResponse.toReadItem() = ReadItem(
    title = title,
    issueName = issueTitle,
    type = BookType.Manga,
    imageUrls = images
)