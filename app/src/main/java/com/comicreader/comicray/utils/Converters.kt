package com.comicreader.comicray.utils

import androidx.room.TypeConverter
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromListToString(list: List<ComicDetails>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToList(data: String?) : List<ComicDetails>?{
        val type = object : TypeToken<List<ComicDetails>>(){}.type
        return Gson().fromJson(data,type)
    }
}