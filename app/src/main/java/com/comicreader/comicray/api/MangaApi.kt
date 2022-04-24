package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.MangaGenreResponse
import com.comicreader.comicray.data.models.custom.MangaSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MangaApi {
    @GET("v1/manga/Genre/list")
    suspend fun getMangaGenreList(): List<Genre.Manga>

    @GET("v1/manga/Genre")
    suspend fun getMangaGenre(@Query("category") category: String, @Query("page") page: Int) : MangaGenreResponse

    @GET("v1/manga/search")
    suspend fun getSearch(@Query("query") query: String, @Query("page") page: Int) : MangaSearchResponse
}