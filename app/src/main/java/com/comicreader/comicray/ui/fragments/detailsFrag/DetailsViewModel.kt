package com.comicreader.comicray.ui.fragments.detailsFrag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.comicDetails.ComicDetailsResponse
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.data.repositories.MangaRepository
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val _detailsComic =
        MutableStateFlow<Resource<ComicDetailsResponse>>(Resource.Loading())
    val detailsComic get() = _detailsComic

    private fun getComicDetails(url: String) {
        viewModelScope.launch {
            comicRepository.getComicDetails(url)
                .collect {
                    if (it is Resource.Success) {
                        if (it.data!=null) {
                            _detailsComic.emit(Resource.Success(it.data))
                        }
                    } else {
                        _detailsComic.emit(Resource.Error(it.throwable!!))
                    }
                }
        }
    }


    fun onFetch(url: String, type: BookType) {
        viewModelScope.launch {
            if (type == BookType.Comic) {
                _detailsComic.emit(Resource.Loading())
                getComicDetails(url)
            }else{

            }
        }

    }
}