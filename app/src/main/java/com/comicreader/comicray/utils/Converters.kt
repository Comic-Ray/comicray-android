package com.comicreader.comicray.utils

import androidx.room.TypeConverter
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.custom.ComicDetail
import com.comicreader.comicray.data.models.custom.ComicGenreResponse
import com.comicreader.comicray.data.models.custom.MangaGenreResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    @TypeConverter
    fun fromListToString(list: List<ComicDetail>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToList(data: String?) : List<ComicDetail>?{
        val type = object : TypeToken<List<ComicDetail>>(){}.type
        return Gson().fromJson(data,type)
    }

    @TypeConverter
    fun fromBookTypeToString(bookType: BookType): String = bookType.name

    @TypeConverter
    fun fromStringToBookType(name: String): BookType = BookType.valueOf(name)

    @TypeConverter
    fun fromComicGenreResponseToString(value: ComicGenreResponse): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromStringToComicGenreResponse(value: String): ComicGenreResponse {
        val type = object : TypeToken<ComicGenreResponse>(){}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromMangaGenreResponseToString(value: MangaGenreResponse): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromStringToMangaGenreResponse(value: String): MangaGenreResponse {
        val type = object : TypeToken<MangaGenreResponse>(){}.type
        return Gson().fromJson(value, type)
    }

}