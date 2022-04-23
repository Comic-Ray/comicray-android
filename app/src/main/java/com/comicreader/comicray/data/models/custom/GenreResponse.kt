package com.comicreader.comicray.data.models.custom

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "GenreComics"
)
data class GenreResponse(
    @PrimaryKey(autoGenerate = true)
    val id : Int?=null,
    val tag : String?,
    val data: List<ComicDetails>,
    val page: Int,
    val totalPages: Int,
    val type: String
){
}