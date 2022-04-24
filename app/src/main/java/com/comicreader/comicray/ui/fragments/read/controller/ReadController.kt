package com.comicreader.comicray.ui.fragments.read.controller

import com.airbnb.epoxy.EpoxyController
import com.comicreader.comicray.epoxyModels.readImage
import java.util.concurrent.CopyOnWriteArrayList

class ReadController : EpoxyController() {

    private val imageList: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()

    fun submitImageList(imageList: List<String>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
        requestModelBuild()
    }

    override fun buildModels() {
        for(image in imageList) {
            readImage {
                id(image.hashCode())
                imageUrl(image)
            }
        }
    }
}