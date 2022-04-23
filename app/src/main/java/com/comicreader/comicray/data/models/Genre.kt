package com.comicreader.comicray.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GenreType : Parcelable { Comic, Manga }

data class Genre(val name: String, val tag: String, val type: GenreType) {
    data class Manga(val name: String, val category: String)
    data class Comic(val name: String, val tag: String)

    companion object {
        fun Manga.toGenre(): Genre = Genre(name = name, tag = category, type = GenreType.Manga)
        fun Comic.toGenre(): Genre = Genre(name = name, tag = tag, type = GenreType.Comic)
    }
}