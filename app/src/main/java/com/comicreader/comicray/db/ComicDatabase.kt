package com.comicreader.comicray.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import com.comicreader.comicray.db.daos.HomeComicDao
import com.comicreader.comicray.utils.Converters

@Database(
    entities = [FeaturedComic::class, CompletedComic::class, OngoingComic::class,CustomData::class,GenreResponse::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ComicDatabase : RoomDatabase() {
     abstract fun homeComicDao() : HomeComicDao
}