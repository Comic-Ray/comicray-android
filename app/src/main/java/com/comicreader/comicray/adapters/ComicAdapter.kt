package com.comicreader.comicray.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.comicreader.comicray.data.models.custom.CommonData
import com.comicreader.comicray.data.models.custom.CustomData
import com.comicreader.comicray.databinding.ItemRecViewBinding
import com.comicreader.comicray.ui.fragments.comics.ComicsViewModel

class ComicAdapter : RecyclerView.Adapter<ComicAdapter.ComicViewHolder>() {

//    private lateinit var resource1 : Resource<List<FeaturedComic>>
//    private lateinit var finalList : List<FeaturedComic>

    private val differCallBack = object : DiffUtil.ItemCallback<CustomData>() {
        override fun areItemsTheSame(oldItem: CustomData, newItem: CustomData): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CustomData, newItem: CustomData): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder {
        return ComicViewHolder(
            ItemRecViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        val data = differ.currentList[position]

        if (data != null) {
            holder.bind(data)
        }
    }

    fun submitList(listOfComics: Map<ComicsViewModel.Data,CustomData>){
            differ.submitList(listOfComics.map {
                it.value
            })
    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ComicViewHolder(private val binding : ItemRecViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(data :CustomData){
                binding.titleText.text = data.title

                binding.setUpItemComicRec(data.comics)
            }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun ItemRecViewBinding.setUpItemComicRec(comics : List<CommonData>){
        val adapter = CustomAdapter(comics)

//        binding.itemComicRec.layoutManager = LinearLayoutManager(binding.itemComicRec.context,LinearLayoutManager.HORIZONTAL,false)
        itemComicRec.setHasFixedSize(true)
        itemComicRec.recycledViewPool.setMaxRecycledViews(0, 0)
        itemComicRec.adapter = adapter
    }


}

















