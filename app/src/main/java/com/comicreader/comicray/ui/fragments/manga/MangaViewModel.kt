package com.comicreader.comicray.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun getTrendingComics() = refreshTriggerChannel.flatMapLatest {
        repository.getGenreComics(
            forceRefresh = it == Refresh.Force,
            categoryNo = "all",
            tag = "trending",
            type = "Manga",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { it.data }

    fun getDramaComics() = refreshTriggerChannel.flatMapLatest {
        repository.getGenreComics(
            forceRefresh = it == Refresh.Force,
            categoryNo = "10",
            tag = "Drama",
            type = "Manga",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { it.data }

    fun getAdventureComics() = refreshTriggerChannel.flatMapLatest {
        repository.getGenreComics(
            forceRefresh = it == Refresh.Force,
            categoryNo = "4",
            tag = "Adventure",
            type = "Manga",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { it.data }

    fun getComedyComics() = refreshTriggerChannel.flatMapLatest {
        repository.getGenreComics(
            forceRefresh = it == Refresh.Force,
            categoryNo = "6",
            tag = "Comedy",
            type = "Manga",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    _eventChannel.send(Event.ShowErrorMessage(throwable))
                }
            }
        )
    }.filter { it is Resource.Success }.mapNotNull { it.data }


    fun onManuelRefresh() {
        viewModelScope.launch {
            _refreshTriggerChannel.emit(Refresh.Force)
        }
    }

    fun onStart() {
        viewModelScope.launch {
            _refreshTriggerChannel.emit(Refresh.Normal)
        }
    }

}