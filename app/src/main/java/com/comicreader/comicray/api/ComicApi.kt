package com.comicreader.comicray.api

import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.custom.*
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import retrofit2.http.GET
import retrofit2.http.Query

interface ComicApi {

    @GET("v1/comic/Home/featured")
    suspend fun getFeaturedComics(): List<FeaturedComic>

    @GET("v1/comic/Genre")
    suspend fun getGenreComics(
        @Query("tag") tag : String,
        @Query("page") page : Int
    ) : ComicGenreResponse

    @GET("v1/comic/Detail")
    suspend fun getComicDetails(
        @Query("url") url:String
    ): ComicDetailsResponse


    @GET("v1/comic/Genre/list")
    suspend fun getGenreList() : List<Genre.Comic>

    @GET("v1/comic/search")
    suspend fun getSearch(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ComicSearchResponse

    @GET("/v1/comic/Read/all")
    suspend fun getReadData(@Query("url") url: String) : ComicReadResponse
}