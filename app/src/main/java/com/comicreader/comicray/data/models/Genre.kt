package com.comicreader.comicray.data.models

data class Genre(val name: String, val tag: String, val type: BookType) {
    data class Manga(val name: String, val category: String)
    data class Comic(val name: String, val tag: String)

    companion object {
        fun Manga.toGenre(): Genre = Genre(name = name, tag = category, type = BookType.Manga)
        fun Comic.toGenre(): Genre = Genre(name = name, tag = tag, type = BookType.Comic)
    }
}