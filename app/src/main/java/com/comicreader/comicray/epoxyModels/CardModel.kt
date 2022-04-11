package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.ItemComicBinding

@EpoxyModelClass(layout = R.layout.item_comic)
abstract class CardModel() : EpoxyModelWithHolder<CardModel.CardViewHolder>() {

    @EpoxyAttribute
    open var urlToImage: String? = null

    @EpoxyAttribute
    open var title: String? = null

    @EpoxyAttribute
    open var urlToDetails: String? = null

    @EpoxyAttribute(value = [EpoxyAttribute.Option.IgnoreRequireHashCode])
    open var listener: View.OnClickListener?=null

    override fun bind(holder: CardViewHolder) {
        holder.binding.apply {
            txtView.text = title
            Glide.with(root)
                .load(urlToImage)
                .into(imgView)

            root.setOnClickListener(listener)

            shimmerLayout.hideShimmer()
        }
    }

    inner class CardViewHolder() : EpoxyHolder() {
        lateinit var binding: ItemComicBinding
            private set

        override fun bindView(itemView: View) {
            binding = ItemComicBinding.bind(itemView)
        }
    }
}