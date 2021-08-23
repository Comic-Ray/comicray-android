package com.comicreader.comicray.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.comicreader.comicray.data.models.custom.CommonData
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.databinding.ItemComicBinding
import com.comicreader.comicray.utils.load

class CustomAdapter(
    val listOfComics : List<CommonData>
) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

//    val differCallback = object : DiffUtil.ItemCallback<CommonData>() {
//        override fun areItemsTheSame(oldItem: CommonData, newItem: CommonData): Boolean {
//            return oldItem.title == newItem.title
//        }
//
//        override fun areContentsTheSame(oldItem: CommonData, newItem: CommonData): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            ItemComicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentComic = listOfComics[position]
        holder.bind(currentComic)
    }

    override fun getItemCount(): Int {
        return listOfComics.size
    }


    inner class CustomViewHolder(private val binding: ItemComicBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(currentComic : CommonData){
                binding.setImage(currentComic.imageUrl)
                binding.txtView.text = currentComic.title

            }
    }

    private fun ItemComicBinding.setImage(imageUrl : String){
        shimmerLayout.startShimmer()
        imgView.load(
            uri = imageUrl,
            onSuccess = { bmp ->
                imgView.setImageBitmap(bmp)
                shimmerLayout.hideShimmer()
            }
        )
    }
}