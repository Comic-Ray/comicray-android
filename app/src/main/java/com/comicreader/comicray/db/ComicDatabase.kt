package com.comicreader.comicray.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.GenreDetail
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.db.daos.HomeComicDao
import com.comicreader.comicray.utils.Converters

@Database(
    entities = [FeaturedComic::class, GenreDetail::class, Genre.Comic::class, Genre.Manga::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun homeComicDao(): HomeComicDao
}