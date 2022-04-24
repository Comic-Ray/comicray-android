package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.EpisodeCardBinding

@EpoxyModelClass(layout = R.layout.episode_card)
abstract class EpisodeCard : EpoxyModelWithHolder<EpisodeCard.EpisodeViewHolder>() {

    @EpoxyAttribute
    open var title: String? = null

    override fun bind(holder: EpisodeViewHolder) {
        holder.binding.apply {
            episodeNo.text = title
        }
    }


    inner class EpisodeViewHolder() : EpoxyHolder() {

        lateinit var binding: EpisodeCardBinding
            private set

        override fun bindView(itemView: View) {
            binding = EpisodeCardBinding.bind(itemView)
        }
    }
}