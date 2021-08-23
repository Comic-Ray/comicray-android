package com.comicreader.comicray.data.models.featuredcomic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "FeaturedComics"
)
data class FeaturedComic(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val imageUrl: String,
    val url : String,
    val title: String
)