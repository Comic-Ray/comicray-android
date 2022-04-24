package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.comicreader.comicray.R
import com.comicreader.comicray.data.models.BookType
import com.comicreader.comicray.data.models.Genre
import com.comicreader.comicray.databinding.ItemDetailBinding
import com.comicreader.comicray.ui.fragments.detailsFrag.controller.ChipController

@EpoxyModelClass(layout = R.layout.item_detail)
abstract class DetailCard : EpoxyModelWithHolder<DetailCard.ViewHolder>() {

    @EpoxyAttribute
    open var title: String = ""

    @EpoxyAttribute
    open var imageUrl: String = ""

    @EpoxyAttribute
    open var genres: List<Genre> = emptyList()

    @EpoxyAttribute
    open var type: BookType = BookType.Comic

    @EpoxyAttribute
    open var status: String = ""

    @EpoxyAttribute
    open var author: String = ""

    @EpoxyAttribute
    open var description: String = ""

    @EpoxyAttribute(value = [EpoxyAttribute.Option.IgnoreRequireHashCode])
    open var goToGenre: (Genre) -> Unit = {}

    override fun bind(holder: ViewHolder) {
        val controller = ChipController(goToGenre)
        holder.binding.apply {
            Glide.with(imgView)
                .load(imageUrl)
                .into(imgView)
            txtTitle.text = title
            statusTxt.text = this@DetailCard.status
            authortxt.text = this@DetailCard.author
            descriptiontxt.text = description

            chipRec.setController(controller)
            controller.submitComicGenres(genres)
        }
    }

    inner class ViewHolder : EpoxyHolder() {
        lateinit var binding: ItemDetailBinding
            private set
        override fun bindView(itemView: View) {
            binding = ItemDetailBinding.bind(itemView)
        }
    }
}