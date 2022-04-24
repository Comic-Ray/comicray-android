package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.comicreader.comicray.GlideApp
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.databinding.ItemFragmentReadBinding

@EpoxyModelClass(layout = R.layout.item_fragment_read)
abstract class ReadImageModel : EpoxyModelWithHolder<ReadImageModel.ReadHolder>() {

    @field:EpoxyAttribute
    open lateinit var imageUrl: String

    @field:EpoxyAttribute
    open lateinit var bookType: BookType

    override fun bind(holder: ReadHolder): Unit = with(holder.binding) {
        val headers = LazyHeaders.Builder()
            .addHeader("referer", "https://mangakakalot.com")
            .build()
        GlideApp.with(imageView)
            .asBitmap()
            .apply(RequestOptions().override(1000))
            .run {
            if (bookType == BookType.Manga) {
                load(GlideUrl(imageUrl, headers))
            } else {
                load(imageUrl)
            }
        }.into(imageView)
    }

    inner class ReadHolder : EpoxyHolder() {
        lateinit var binding: ItemFragmentReadBinding
            private set

        override fun bindView(itemView: View) {
            binding = ItemFragmentReadBinding.bind(itemView)
        }
    }

}