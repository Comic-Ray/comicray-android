package com.comicreader.comicray.data.repositories

import android.util.Log
import androidx.room.withTransaction
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ComicRepository @Inject constructor(
    private val comicApi: ComicApi,
    private val comicDb: ComicDatabase
) {

    fun getFeaturedComics(dbOnly: Boolean): Flow<List<FeaturedComic>> {
        val dbFlow = comicDb
            .homeComicDao()
            .getFeaturedComics()
            .flowOn(Dispatchers.IO)

        val apiFLow = flow {
            val res = comicApi.getFeaturedComics()
            if (res.isSuccessful && res.body() != null) {
                comicDb.homeComicDao().deleteFeaturedComics()
                val featuredComics = res.body()
                if (featuredComics != null) {
                    comicDb.homeComicDao().insertFeaturedComics(featuredComics)
                }
                emit(featuredComics)
            } else {
                error(res.message())
            }
        }.flowOn(Dispatchers.IO)

        return if (dbOnly) {
            dbFlow
        } else {
            dbFlow.combine(apiFLow) { db, api ->
                db
            }.flowOn(Dispatchers.IO)
        }
    }

    fun getFeaturedComicsTest(
        forceRefresh : Boolean,
        fetchSuccess: () -> Unit,
        onFetchFailed : (Throwable) -> Unit
    ): Flow<Resource<List<FeaturedComic>>> = networkBoundResource(
        query = {
            val query = comicDb.homeComicDao().getFeaturedComics()
            query
        },
        fetch = {
            val data = comicApi.getFeaturedComicsTest()
            data
        },
        saveFetchResult = {
            comicDb.withTransaction {
                comicDb.homeComicDao().deleteFeaturedComics()
                comicDb.homeComicDao().insertFeaturedComics(it)
            }
        },
        shouldFetch = { featuredComics -> //it : List<FeaturedComic>
            true
        },
        onFetchSuccess = fetchSuccess,
        onFetchFailed = {
            if (it !is HttpException && it !is IOException) {
                throw it
            }
            onFetchFailed(it)
        }
    )


}