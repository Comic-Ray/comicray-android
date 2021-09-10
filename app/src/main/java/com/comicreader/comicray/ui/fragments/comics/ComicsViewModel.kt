package com.comicreader.comicray.ui.fragments.comics

import android.util.Log
import androidx.lifecycle.*
import com.comicreader.comicray.data.models.custom.ComicDetails
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.utils.ComicGenres
import com.comicreader.comicray.utils.Refresh
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComicsViewModel @Inject constructor(
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val comicListChannel = Channel<HashMap<ComicGenres,CustomData>>()
    val comicList = comicListChannel.receiveAsFlow()

     val comicListMutable = MutableStateFlow(HashMap<ComicGenres,CustomData>(0))


//    private val _collectionOfComics = MediatorLiveData<HashMap<Data, CustomData>>()
//    val collectionComics: LiveData<HashMap<Data, CustomData>> = _collectionOfComics

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger get() = refreshTriggerChannel.receiveAsFlow()

    private val list = HashMap<ComicGenres, CustomData>()

    val collectionComicsFlow: Flow<HashMap<ComicGenres, CustomData>> = flow {
        featuredComics.collect {
            if (it is Resource.Success) {
                list[ComicGenres.Featured] = CustomData(
                    "Featured Comics",
                    convertToCommonData(it.data)
                )
                emit(list)
            }
        }
    }

    fun getComics() = channelFlow<HashMap<ComicGenres,CustomData>> {
        refreshTrigger.flatMapLatest {
            comicRepository.getFeaturedComics(
                forceRefresh = it == Refresh.Force,
                fetchSuccess = {},
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.showErrorMessage(t))
                    }
                }
            )
        }.collect {
            if (it is Resource.Success) {
                list[ComicGenres.Featured] = CustomData(
                    "Featured Comics",
                    convertToCommonData(it.data)
                )
                comicListMutable.value = list
                comicListChannel.send(list)
            }
        }
    }


    fun getActionComics() = channelFlow<HashMap<ComicGenres,CustomData>> {
        refreshTrigger.flatMapLatest {
            comicRepository.getGenreComics(
                forceRefresh = it == Refresh.Force,
                tag = "action-comic",
                fetchSuccess = {},
                onFetchFailed = { throwable ->
                    viewModelScope.launch {
                        eventChannel.send(Event.showErrorMessage(throwable))
                    }
                }
            )
        }.collect {
            if (it is Resource.Success) {
                if (it.data?.data?.isNotEmpty() == true) {
                    list[ComicGenres.Action] = CustomData(
                        "Action Comics",
                        it.data.data
                    )
                    comicListMutable.value = list
                    comicListChannel.send(list)
                }
            }
        }
    }

    val collectionF: Flow<HashMap<ComicGenres, CustomData>> = flow {
        actionComics.collect {
            if (it is Resource.Success) {
                if (it.data?.data?.isNotEmpty() == true) {
                    list[ComicGenres.Action] = CustomData(
                        "Action Comics",
                        it.data.data
                    )
                    emit(list)
                }
//                Log.d("TAGG", "${it.data?.data}")
            }
        }
    }

    private val actionComics = refreshTrigger.flatMapLatest {
        comicRepository.getGenreComics(
            forceRefresh = true,
            tag = "action-comic",
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.showErrorMessage(throwable))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val featuredComics = refreshTrigger.flatMapLatest {
        comicRepository.getFeaturedComics(
            forceRefresh = it == Refresh.Force,
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.showErrorMessage(throwable))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
//        getFComics()
        viewModelScope.launch {
//            collectionF.collect {
//                comicListMutable.value = it
//            }
            getComics().collect { }
//            getActionComics().collect {  }
        }
    }

    fun onManuelRefresh() {
        viewModelScope.launch {
            if (featuredComics.value !is Resource.Loading) {
                refreshTriggerChannel.send(Refresh.Force)
            }
        }
    }

    fun onStart() {
        viewModelScope.launch {
            if (featuredComics.value !is Resource.Loading) {
                refreshTriggerChannel.send(Refresh.Normal)
            }
        }
    }

    private fun convertToCommonData(featured: List<FeaturedComic>?): List<ComicDetails> {

        return featured?.map {
            ComicDetails(
                title = it.title,
                url = it.url,
                imageUrl = it.imageUrl
            )
        } ?: emptyList()
    }

    sealed class Event {
        data class showErrorMessage(val error: Throwable) : Event()
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }

}


//    fun getFComics() {
//        viewModelScope.launch {
//            featuredComics.collect {
//                if (it is Resource.Success) {
//                    if (!it.data.isNullOrEmpty()) {
//                        list[Data.Featured] = CustomData(
//                            "Featured Comics",
//                            convertToCommonData(it.data)
//                        )
//                        _collectionOfComics.postValue(list)
//                    }
//                }
//            }
//        }
//
//    }