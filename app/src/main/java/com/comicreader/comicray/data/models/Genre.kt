package com.comicreader.comicray.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Genre(val name: String, val tag: String, val type: BookType) {
    @Entity(tableName = "manga_genre")
    data class Manga(
        val name: String,
        @PrimaryKey(autoGenerate = false) val category: String,
        val expiry: Long = 0
    ) {
        fun createNewExpiry() = System.currentTimeMillis() + EXPIRY_OFFSET
        fun isExpired() : Boolean = expiry < System.currentTimeMillis()
    }

    @Entity(tableName = "comic_genre")
    data class Comic(
        val name: String,
        @PrimaryKey(autoGenerate = false) val tag: String,
        val expiry: Long = 0
    ) {
        fun createNewExpiry() = System.currentTimeMillis() + EXPIRY_OFFSET
        fun isExpired() : Boolean = expiry < System.currentTimeMillis()
    }

    companion object {
        private const val EXPIRY_OFFSET: Long = 1000 * 60 * 60 * 7L

        fun Manga.toGenre(): Genre = Genre(name = name, tag = category, type = BookType.Manga)
        fun Comic.toGenre(): Genre = Genre(name = name, tag = tag, type = BookType.Comic)
    }
}