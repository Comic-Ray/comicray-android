package com.comicreader.comicray.utils

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit
) = flow<Resource<ResultType>> {
    val result = runCatching {
        query().firstOrNull()
    }

    if (result.isFailure || (result.isSuccess && shouldFetch(result.getOrNull()!!))) {
        emit(Resource.Loading())

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