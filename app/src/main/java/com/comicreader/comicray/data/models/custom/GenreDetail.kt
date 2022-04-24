package com.comicreader.comicray.data.models.custom

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.utils.Converters

@Entity(tableName = "GenreComics")
data class GenreDetail(
    @PrimaryKey(autoGenerate = false)
    val tag : String,
    val page: Int,
    val totalPages: Int,
    val type: BookType,
    val additionalData: String, // should be identified by type converters.
) {
    @Ignore
    private var data: Any? = null
    fun getComicGenreResponse(): ComicGenreResponse {
        if (data == null) {
            data = Converters.fromStringToComicGenreResponse(additionalData)
        }
        return data as ComicGenreResponse
    }
    fun getMangaGenreResponse(): MangaGenreResponse {
        if (data == null) {
            data = Converters.fromStringToMangaGenreResponse(additionalData)
        }
        return data as MangaGenreResponse
    }
}

fun ComicGenreResponse.toGenreDetail(tag: String) = GenreDetail(
    tag = tag,
    page = page,
    totalPages = totalPages,
    type = BookType.Comic,
    additionalData = Converters.fromComicGenreResponseToString(this)
)

fun MangaGenreResponse.toGenreDetail(tag: String) = GenreDetail(
    tag = tag,
    page = page,
    totalPages = totalPages,
    type = BookType.Manga,
    additionalData = Converters.fromMangaGenreResponseToString(this)
)
