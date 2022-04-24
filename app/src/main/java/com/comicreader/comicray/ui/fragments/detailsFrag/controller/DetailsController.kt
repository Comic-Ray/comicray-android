package com.comicreader.comicray.ui.fragments.detailsFrag.controller

import com.airbnb.epoxy.EpoxyController
import com.comicreader.comicray.data.models.comicDetails.Issue
import com.comicreader.comicray.epoxyModels.episodeCard
import java.util.concurrent.CopyOnWriteArrayList

class DetailsController : EpoxyController() {

    private var comicIssues: CopyOnWriteArrayList<Issue> = CopyOnWriteArrayList()

    fun submitComicChapters(issues: List<Issue>) {
        comicIssues.clear()
        comicIssues.addAll(issues)
        requestModelBuild()
    }


    override fun buildModels() {
        if (comicIssues.isNotEmpty()) {
            for (comicsIssue in comicIssues) {
                episodeCard {
                    id("episodeNo" + comicsIssue.rawName)
                    title(comicsIssue.rawName)
                }
            }
        }

    }
}