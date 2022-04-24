package com.comicreader.comicray.db.daos

import androidx.room.*
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.GenreDetail
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
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

    //GenreResponse Class
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreDetailResponse(data: GenreDetail)

    @Query("SELECT * FROM GenreComics WHERE tag =:tag AND type=:type")
    fun getGenreComicsResponse(tag: String, type: BookType): Flow<GenreDetail>

    @Query("DELETE FROM GenreComics WHERE tag =:tag AND type=:type")
    suspend fun deleteGenreComicsResponse(tag: String, type: BookType)

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