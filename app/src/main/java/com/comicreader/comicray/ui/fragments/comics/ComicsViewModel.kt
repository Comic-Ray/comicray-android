package com.comicreader.comicray.ui.fragments.comics

import androidx.lifecycle.*
import com.comicreader.comicray.data.models.custom.CommonData
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
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

//    private val _collectionOfComics = MediatorLiveData<HashMap<Data, CustomData>>()
//    val collectionComics: LiveData<HashMap<Data, CustomData>> = _collectionOfComics


    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger get() = refreshTriggerChannel.receiveAsFlow()

    private val list = HashMap<Data, CustomData>()

    enum class Data {
        Featured
    }

    val collectionComicsFlow: Flow<HashMap<Data, CustomData>> = flow {
        featuredComics.collect {
            if (it is Resource.Success) {
                list[Data.Featured] = CustomData(
                    "Featured Comics",
                    convertToCommonData(it.data)
                )
                emit(list)
            }
        }
    }

    val featuredComics = refreshTrigger.flatMapLatest {
        comicRepository.getFeaturedComicsTest(
            forceRefresh = it == Refresh.Force,
            fetchSuccess = {},
            onFetchFailed = { throwable ->
                viewModelScope.launch {
                    eventChannel.send(Event.showErrorMessage(throwable))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    init {
//        getFComics()
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

    private fun convertToCommonData(featured: List<FeaturedComic>?): List<CommonData> {

        return featured?.map {
            CommonData(
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