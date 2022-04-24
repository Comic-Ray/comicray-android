package com.comicreader.comicray.ui.fragments.detailsFrag.controller

import com.airbnb.epoxy.EpoxyController
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.data.models.comicDetails.Issue
import com.comicreader.comicray.data.models.custom.CommonDetailResponse
import com.comicreader.comicray.data.models.mangaDetails.Chapter
import com.comicreader.comicray.epoxyModels.detailCard
import com.comicreader.comicray.epoxyModels.episodeCard
import java.util.concurrent.CopyOnWriteArrayList

class DetailsController(
    private val goToGenre: (Genre) -> Unit,
    private val goToRead: (url: String, type: BookType) -> Unit,
) : EpoxyController() {

    private var comicIssues: CopyOnWriteArrayList<Issue> = CopyOnWriteArrayList()
    private var mangaChapters: CopyOnWriteArrayList<Chapter> = CopyOnWriteArrayList()
    private var type: BookType = BookType.Comic

    var detail: CommonDetailResponse? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    fun submitComicChapters(issues: List<Issue>) {
        comicIssues.clear()
        comicIssues.addAll(issues)
        requestModelBuild()
    }

    fun submitMangaChapters(chapters: List<Chapter>){
        mangaChapters.clear()
        mangaChapters.addAll(chapters)
        requestModelBuild()
    }

    fun submitType(type: BookType){
        this.type = type
    }

    override fun buildModels() {

        val detail = detail
        if (detail != null) {
            detailCard {
                id("detail-card")
                title(detail.title)
                author(detail.author)
                status(detail.status)
                description(detail.description)
                imageUrl(detail.imageUrl)
                type(this@DetailsController.type)
                genres(detail.genres)
                goToGenre(this@DetailsController.goToGenre)
            }
        }

        if (type == BookType.Comic) {
            if (comicIssues.isNotEmpty()) {
                for (comicsIssue in comicIssues) {
                    episodeCard {
                        id("episodeNo" + comicsIssue.rawName)
                        title(comicsIssue.rawName)
                        listener { this@DetailsController.goToRead(comicsIssue.url, BookType.Comic) }
                    }
                }
            }
        }

        if (type == BookType.Manga){
            if (mangaChapters.isNotEmpty()) {
                for (mangaChapter in mangaChapters) {
                    episodeCard {
                        id("episodeNo" + mangaChapter.name)
                        title(mangaChapter.name)
                        listener { this@DetailsController.goToRead(mangaChapter.url, BookType.Manga) }
                    }
                }
            }
        }

    }
}