package com.comicreader.comicray.data.models.custom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "CustomComics"
)
data class CustomData(
    @PrimaryKey
    val title : String,
    @ColumnInfo(name = "ComicDetails") val comics : List<ComicDetails>
) {
}