package com.comicreader.comicray.db.daos

import androidx.room.*
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeComicDao {

    //Featured Comics
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeaturedComics(featuredComics: List<FeaturedComic>)

    @Query("SELECT * FROM FeaturedComics")
    fun getFeaturedComics(): Flow<List<FeaturedComic>>

    @Query("DELETE FROM FeaturedComics")
    suspend fun deleteFeaturedComics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOngoingComics(ongoingComics: OngoingComic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedComics(completedComics: CompletedComic)

    //GenreComics
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreComics(data: CustomData)

    @Query("SELECT * FROM CustomComics WHERE title =:tag")
    fun getGenreComics(tag: String): Flow<CustomData>

    @Query("DELETE FROM CustomComics WHERE title =:tag")
    suspend fun deleteGenreComics(tag: String)

    //GenreResponse Class
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreComicsResponse(data: GenreResponse)

    @Query("SELECT * FROM GenreComics WHERE tag =:tag AND Comictype=:type")
    fun getGenreComicsResponse(tag: String, type: String): Flow<GenreResponse>

    @Query("DELETE FROM GenreComics WHERE tag =:tag AND Comictype=:type")
    suspend fun deleteGenreComicsResponse(tag: String, type: String)

    @Query("SELECT * FROM comic_genre")
    fun getComicGenreList() : Flow<List<Genre.Comic>>

    @Query("SELECT * FROM manga_genre")
    fun getMangaGenreList() : Flow<List<Genre.Manga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreComics(list: List<Genre.Comic>)

    @Transaction
    suspend fun saveComicGenreList(list: List<Genre.Comic>) {
        val data = list.map { it.copy(expiry = it.createNewExpiry()) }
        insertGenreComics(data)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreManga(list: List<Genre.Manga>)

    @Transaction
    suspend fun saveMangaGenreList(list: List<Genre.Manga>) {
        val data = list.map { it.copy(expiry = it.createNewExpiry()) }
        insertGenreManga(data)
    }
}