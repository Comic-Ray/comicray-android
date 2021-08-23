package com.comicreader.comicray.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeComicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeaturedComics(featuredComics: List<FeaturedComic>)

    @Query("SELECT * FROM FeaturedComics")
    fun getFeaturedComics() : Flow<List<FeaturedComic>>

    @Query("DELETE FROM FeaturedComics")
    suspend fun deleteFeaturedComics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOngoingComics(ongoingComics: OngoingComic)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedComics(completedComics: CompletedComic)

}