package com.comicreader.comicray.data.models.ongoingComic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "OngoingComics"
)
data class OngoingComic(
    @PrimaryKey(autoGenerate = true)
    val id : Int?=null,
    val date: String,
    val rawName: String,
    val title: String,
    val url: String
)