package com.comicreader.comicray.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.comicreader.comicray.api.ComicApi
import com.comicreader.comicray.api.MangaApi
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.repositories.ComicRepository
import com.comicreader.comicray.data.repositories.MangaRepository
import com.comicreader.comicray.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val mangaRepository: MangaRepository,
    private val mangaApi: MangaApi,
    private val comicApi: ComicApi,
) : ViewModel() {

    fun getComicGenreList(): LiveData<Resource<List<Genre.Comic>>> = comicRepository.getGenreList().asLiveData()
    fun getMangaGenreList(): LiveData<Resource<List<Genre.Manga>>> = mangaRepository.getGenreList().asLiveData()


}