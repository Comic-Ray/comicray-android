package com.comicreader.comicray.utils

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit
) = flow<Resource<ResultType>> {
    val data = query().first()

    if (shouldFetch(data)) {
            emit(Resource.Loading(null))

        try {
            saveFetchResult(fetch())
            onFetchSuccess()
            query().collect {
                emit(Resource.Success(it))
            }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().collect { emit(Resource.Error(t, it)) }
        }
    } else {
        query().collect {
            emit(Resource.Success(it))
        }
    }
}