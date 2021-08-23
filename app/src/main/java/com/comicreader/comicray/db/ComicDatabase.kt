package com.comicreader.comicray.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import com.comicreader.comicray.db.daos.HomeComicDao

@Database(
    entities = [FeaturedComic::class, CompletedComic::class, OngoingComic::class],
    version = 1
)
abstract class ComicDatabase : RoomDatabase() {
     abstract fun homeComicDao() : HomeComicDao
}