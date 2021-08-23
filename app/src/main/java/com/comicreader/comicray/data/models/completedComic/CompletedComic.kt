package com.comicreader.comicray.data.models.completedComic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "CompletedComics"
)
data class CompletedComic(
    @PrimaryKey(autoGenerate = true)
    val id :Int?=null,
    val detail: String,
    val instance: String,
    val title: String,
    val type: String
)