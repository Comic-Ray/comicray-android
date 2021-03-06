package com.comicreader.comicray.ui.fragments.more.controller

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.comicreader.comicray.data.models.DataItem
import com.comicreader.comicray.epoxyModels.CardModel_

class MoreController(
    private val goToDetail: (DataItem) -> Unit
) : PagedListEpoxyController<DataItem>() {
    override fun buildItemModel(currentPosition: Int, item: DataItem?): EpoxyModel<*> {
        if (item == null) return CardModel_().id("empty")
        return CardModel_()
            .id(item.title.hashCode())
            .title(item.title)
            .urlToImage(item.imageUrl)
            .listener { _ -> this@MoreController.goToDetail(item) }
    }
}