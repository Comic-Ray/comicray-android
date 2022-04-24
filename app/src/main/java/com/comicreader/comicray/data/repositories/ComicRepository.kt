package com.comicreader.comicray.data.repositories

import androidx.room.withTransaction
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class ComicRepository @Inject constructor(
    private val comicApi: ComicApi,
    private val comicDb: ComicDatabase
) {

    fun getFeaturedComics(
        forceRefresh: Boolean,
        fetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<FeaturedComic>>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getFeaturedComics()
            query
        },
        fetch = {
            val data = comicApi.getFeaturedComics()
            data
        },
        saveFetchResult = {
            comicDb.withTransaction {
                comicDb.homeComicDao().deleteFeaturedComics()
                comicDb.homeComicDao().insertFeaturedComics(it)
            }
        },
        shouldFetch = { featuredComics -> //it : List<FeaturedComic>
            if (forceRefresh) {
                true
            } else {
                //todo refresh the data after sometimes
                val cachedComics = featuredComics.isEmpty()
                cachedComics
            }
        },
        onFetchSuccess = fetchSuccess,
        onFetchFailed = {
            if (it !is HttpException && it !is IOException) {
                throw it
            }
            onFetchFailed(it)
        }
    )


    fun getGenreComics(
        forceRefresh: Boolean,
        tag: String,
        type: String,
        fetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<GenreResponse>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getGenreComicsResponse(tag, type)
            query
        },
        fetch = {
            val data = comicApi.getGenreComics(tag, 1)
            data
        },
        saveFetchResult = {
//                val comicsWithTag = CustomData(tag, it.data)
            val c = GenreResponse(
                tag = tag,
                data = it.data,
                page = it.page,
                totalPages = it.totalPages,
                Comictype = type
            )
            comicDb.withTransaction {
                comicDb.homeComicDao().deleteGenreComicsResponse(tag, type)
//                    comicDb.homeComicDao().insertGenreComics(comicsWithTag)
                comicDb.homeComicDao().insertGenreComicsResponse(c)
            }
        },
        shouldFetch = { genreResponse ->
            if (forceRefresh) {
                true
            } else {
                if (genreResponse != null) {
                    val data = genreResponse.data.isEmpty()
                    data
                } else {
                    true
                }
            }
        },
        onFetchSuccess = fetchSuccess,
        onFetchFailed = {
            if (it !is HttpException && it !is IOException) {
                throw it
            }
            onFetchFailed(it)
        }
    )

    fun getComicDetails(url: String) = flow<Resource<ComicDetailsResponse>>{
        try {
            val data = comicApi.getcomicDetails(url)
            emit(Resource.Success(data))
        }catch (e: Exception){
            emit(Resource.Error(e))
        }
    }

}