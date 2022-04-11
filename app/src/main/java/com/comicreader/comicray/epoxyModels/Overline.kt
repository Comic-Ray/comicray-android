package com.comicreader.comicray.epoxyModels

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.comicreader.comicray.R
import com.comicreader.comicray.databinding.OverlineBinding

@EpoxyModelClass(layout = R.layout.overline)
abstract class Overline : EpoxyModelWithHolder<Overline.viewHolder>() {

    @EpoxyAttribute
    open var value: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.IgnoreRequireHashCode)
    open var listener: View.OnClickListener? = null

    override fun bind(holder: viewHolder) {
        holder.binding.apply {
            titleText.text = value

            showMore.setOnClickListener(listener)
        }
    }


    inner class viewHolder() : EpoxyHolder() {

        lateinit var binding: OverlineBinding
            private set

        override fun bindView(itemView: View) {
            binding = OverlineBinding.bind(itemView)
        }
    }
}