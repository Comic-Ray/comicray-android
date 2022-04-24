package com.comicreader.comicray.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.data.models.custom.ComicDetail
import com.comicreader.comicray.data.models.custom.MangaGenre
import com.comicreader.comicray.data.models.custom.toDataItem
import com.comicreader.comicray.data.repositories.MangaRepository
import com.comicreader.comicray.utils.Event
import com.comicreader.comicray.utils.Refresh
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val repository: MangaRepository
) : ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventChannel get() = _eventChannel.receiveAsFlow()

    private val _refreshTriggerChannel = MutableSharedFlow<Refresh>()
    val refreshTriggerChannel get() = _refreshTriggerChannel


    //we get all latest comics by passing categoryNo as "all" so I am naming it as Trending Comics
    fun getTrendingComics(): Flow<List<DataItem>> = refreshTriggerChannel.flatMapLatest {
        repository.getGenre(
            forceRefresh = it == Refresh.Force,
            categoryNo = "all",
            tag = "trending",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun getDramaComics(): Flow<List<DataItem>> = refreshTriggerChannel.flatMapLatest {
        repository.getGenre(
            forceRefresh = it == Refresh.Force,
            categoryNo = "10",
            tag = "Drama",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun getAdventureComics(): Flow<List<DataItem>> = refreshTriggerChannel.flatMapLatest {
        repository.getGenre(
            forceRefresh = it == Refresh.Force,
            categoryNo = "4",
            tag = "Adventure",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }

    fun getComedyComics(): Flow<List<DataItem>> = refreshTriggerChannel.flatMapLatest {
        repository.getGenre(
            forceRefresh = it == Refresh.Force,
            categoryNo = "6",
            tag = "Comedy",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { convertToCommonData(it.data?.data ?: emptyList()) }


    fun onManualRefresh() {
        viewModelScope.launch {
            _refreshTriggerChannel.emit(Refresh.Force)
        }
    }

    private fun convertToCommonData(data: List<MangaGenre>) : List<DataItem> {
        return data.map { it.toDataItem() }
    }

    fun onStart() {
        viewModelScope.launch {
            _refreshTriggerChannel.emit(Refresh.Normal)
        }
    }

}