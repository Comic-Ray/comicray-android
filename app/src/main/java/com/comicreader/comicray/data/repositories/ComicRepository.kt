package com.comicreader.comicray.data.repositories

import android.util.Log
import androidx.room.withTransaction
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.custom.GenreResponse
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.db.ComicDatabase
import com.comicreader.comicray.utils.Resource
import com.comicreader.comicray.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.log

class ComicRepository @Inject constructor(
    private val comicApi: ComicApi,
    private val comicDb: ComicDatabase
) {

//    fun getFeaturedComics(dbOnly: Boolean): Flow<List<FeaturedComic>> {
//        val dbFlow = comicDb
//            .homeComicDao()
//            .getFeaturedComics()
//            .flowOn(Dispatchers.IO)
//
//        val apiFLow = flow {
//            val res = comicApi.getFeaturedComics()
//            if (res.isSuccessful && res.body() != null) {
//                comicDb.homeComicDao().deleteFeaturedComics()
//                val featuredComics = res.body()
//                if (featuredComics != null) {
//                    comicDb.homeComicDao().insertFeaturedComics(featuredComics)
//                }
//                emit(featuredComics)
//            } else {
//                error(res.message())
//            }
//        }.flowOn(Dispatchers.IO)
//
//        return if (dbOnly) {
//            dbFlow
//        } else {
//            dbFlow.combine(apiFLow) { db, api ->
//                db
//            }.flowOn(Dispatchers.IO)
//        }
//    }

//    suspend fun callMe(){
//        val d = comicApi.getGenreComics("action-comic",1)
//        val c = GenreResponse(tag = "abc",data = d.data,page = d.page,totalPages = d.totalPages)
//        comicDb.homeComicDao().deleteGenreComicsResponse("abc")
////        comicDb.homeComicDao().getGenreComicsResponse("abc").collect {
////            Log.d("TAGG", "callMe After Deleted: ${it} ")
////        }
//        comicDb.homeComicDao().insertGenreComicsResponse(c)
////        Log.d("TAGG", "callMe:  ${c}")
//        comicDb.homeComicDao().getGenreComicsResponse("abc").collect {
//            Log.d("TAGG", "callMe After Inserted: ${it} ")
//        }
//
//    }

    fun getFeaturedComics(
        forceRefresh : Boolean,
        fetchSuccess: () -> Unit,
        onFetchFailed : (Throwable) -> Unit
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
            if(forceRefresh){
                true
            }else{
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
        forceRefresh : Boolean,
        tag : String,
        fetchSuccess: () -> Unit,
        onFetchFailed : (Throwable) -> Unit
    ) : Flow<Resource<GenreResponse>> = networkBoundResource(
        query = {
           val query = comicDb.homeComicDao().getGenreComicsResponse(tag)
            query
        },
        fetch = {
           val data = comicApi.getGenreComics(tag,1)
            data
        },
        saveFetchResult = {
//                val comicsWithTag = CustomData(tag, it.data)
            val c = GenreResponse(tag = tag,data = it.data,page = it.page,totalPages = it.totalPages)
                comicDb.withTransaction {
                    comicDb.homeComicDao().deleteGenreComicsResponse(tag)
//                    comicDb.homeComicDao().insertGenreComics(comicsWithTag)
                    comicDb.homeComicDao().insertGenreComicsResponse(c)
                }
        },
        shouldFetch = {genreResponse ->
            if (forceRefresh){
                true
            }else {
                val data = genreResponse.data.isEmpty()
                data
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




















}