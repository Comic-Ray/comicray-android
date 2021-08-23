package com.comicreader.comicray.ui.fragments.comics

import android.util.Log

import androidx.lifecycle.*
import com.comicreader.comicray.data.models.custom.CommonData
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.data.models.featuredcomic.FeaturedComic
import com.comicreader.comicray.data.repositories.ComicRepository
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

    private val eventChannel = MutableLiveData<Event>()
    val events: LiveData<Event> get() = eventChannel


    private val _featuredComics = MutableLiveData<List<FeaturedComic>>()
    val featuredComics: LiveData<List<FeaturedComic>> = _featuredComics

    private val _collectionOfComics = MediatorLiveData<HashMap<Data, CustomData>>()
    val collectionComics: LiveData<HashMap<Data, CustomData>> = _collectionOfComics

    private val _allComics = MediatorLiveData<Resource<List<FeaturedComic>>>()
    val allComics: LiveData<Resource<List<FeaturedComic>>> = _allComics

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger get() = refreshTriggerChannel.receiveAsFlow()

    private val list = HashMap<Data, CustomData>()

    enum class Data {
        Featured
    }


    fun getFComics() {
        viewModelScope.launch {
            comicRepository.getFeaturedComicsTest(
                forceRefresh = true,
                fetchSuccess = {},
                onFetchFailed = {
                    eventChannel.value = Event.showErrorMessage(it)
                }
            ).collect {
                if (it is Resource.Success) {
//                        _featuredComics.value = it.data!!
                    list[Data.Featured] = CustomData(
                        "Featured Comics",
                        convertToCommonData(it.data!!)
                    )
                    _collectionOfComics.postValue(list)
                }
            }
        }
    }

    init {
//        getFeaturedComics()

        getFComics()
//        _collectionOfComics.addSource(_featuredComics) {
//            if (!it.isNullOrEmpty()) {
//                list.add(
//                    CustomData(
//                        "Featured Comics",
//                        convertToCommonData(it)
//                    )
//                )
//                _collectionOfComics.postValue(list)
//            }
//        }


    }

//    private fun getFeaturedComics() {
//        viewModelScope.launch {
//            comicRepository.getFeaturedComics(false)
//                .onStart {
//
//                }.catch { e ->
//                    if (e.message != null) {
//                        // TODO: 7/30/2021 Show Error in Toast
//                    }
//                }
//                .collect {
//
//                    if (it.isNotEmpty()) {
////                        val d = CustomData("Featured Comics", convertToCommonData(it))
////
////                        list.add(d)
//                        _featuredComics.postValue(it)
////                        _collectionOfComics.postValue(list)
//
////                    addOneMore(convertToCommonData(it))
//                    }
//                }
//        }
//    }
//
//    fun addOneMore(d: List<CommonData>) {
//        val c = CustomData("FEATUre", d)
//        list.add(c)
//        _collectionOfComics.value = list
//    }

    fun onManuelRefresh() {
        viewModelScope.launch {
            refreshTriggerChannel.send(Refresh.Force)
        }
    }

    fun onStart() {
        viewModelScope.launch {
            refreshTriggerChannel.send(Refresh.Normal)
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

    enum class Refresh {
        Normal,
        Force
    }

    sealed class Event {
        data class showErrorMessage(val error: Throwable) : Event()
    }

    override fun onCleared() {
        super.onCleared()
        list.clear()
    }

}