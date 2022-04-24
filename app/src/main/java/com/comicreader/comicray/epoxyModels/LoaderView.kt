package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.LoaderviewBinding

@EpoxyModelClass(layout = R.layout.loaderview)
abstract class LoaderView() : EpoxyModelWithHolder<LoaderView.LoaderViewHolder>() {


    @EpoxyAttribute
    open var titleText: String? = null

    @EpoxyAttribute
    open var titleText2: String? = null

    @EpoxyAttribute
    open var titleText3: String? = null

    override fun bind(holder: LoaderViewHolder) {
        holder.binding.apply {
            comicType.text = titleText
            comicType2.text = titleText2
            comicType3.text = titleText3
            shimmerLayout.startShimmer()
        }
    }

    inner class LoaderViewHolder() : EpoxyHolder() {
        lateinit var binding: LoaderviewBinding
            private set

        override fun bindView(itemView: View) {
            binding = LoaderviewBinding.bind(itemView)
        }
    }
}