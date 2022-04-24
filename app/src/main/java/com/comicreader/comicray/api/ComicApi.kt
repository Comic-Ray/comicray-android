package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.completedComic.CompletedComic
import com.comicreader.comicray.data.models.custom.ComicSearchResponse
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.models.ongoingComic.OngoingComic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ComicApi {

    @GET("comic/Home/featured")
    suspend fun getFeaturedComics(): List<FeaturedComic>

    @GET("comic/Home/ongoing")
    fun getOngoingComics(): Response<List<OngoingComic>>

    @GET("comic/Home/completed")
    fun getCompletedComics(): Response<List<CompletedComic>>

    @GET("comic/Genre")
    suspend fun getGenreComics(
        @Query("tag") tag: String,
        @Query("page") page: Int
    ): GenreResponse

    @Deprecated(message = "Use MangaAPI")
    @Headers("Accept: application/json")
    @GET("manga/Genre")
    suspend fun getGenreManga(
        @Query("type") tag: String = "Latest",
        @Query("state") state: String = "All",
        @Query("category") category: String,
        @Query("page") page: Int = 1
    ): GenreResponse

    @GET("v1/comic/Genre/list")
    suspend fun getGenreList(): List<Genre.Comic>

    @GET("v1/comic/search")
    suspend fun getSearch(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ComicSearchResponse
}