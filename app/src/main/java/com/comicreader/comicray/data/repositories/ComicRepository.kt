package com.comicreader.comicray.data.repositories

import androidx.room.withTransaction
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.custom.ComicGenreResponse
import com.comicreader.comicray.data.models.custom.GenreDetail
import com.comicreader.comicray.data.models.custom.toGenreDetail
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.flow.*
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

    fun getGenre(
        forceRefresh: Boolean,
        tag: String,
        fetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<ComicGenreResponse>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getGenreComicsResponse(tag, BookType.Comic).map { it.getComicGenreResponse() }
            query
        },
        fetch = {
            val data = comicApi.getGenreComics(tag, 1)
            data
        },
        saveFetchResult = { comicDb.homeComicDao().insertGenreDetailResponse(it.toGenreDetail(tag)) },
        shouldFetch = { genreResponse ->
            if (forceRefresh) {
                true
            } else {
                genreResponse.data.isEmpty()
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
            val data = comicApi.getComicDetails(url)
            emit(Resource.Success(data))
        }catch (e: Exception){
            emit(Resource.Error(e))
        }
    }

    fun getGenreList() : Flow<Resource<List<Genre.Comic>>> = networkBoundResource(
        query = { comicDb.homeComicDao().getComicGenreList() },
        fetch = { comicApi.getGenreList() },
        saveFetchResult = { comicDb.homeComicDao().saveComicGenreList(it) },
        shouldFetch = { it.firstOrNull()?.isExpired() ?: true },
        onFetchFailed = {},
        onFetchSuccess = {}
    )

}