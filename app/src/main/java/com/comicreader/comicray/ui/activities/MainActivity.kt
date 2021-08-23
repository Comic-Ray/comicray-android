package com.comicreader.comicray.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.comicreader.comicray.databinding.ActivityMainBinding
import com.comicreader.comicray.ui.fragments.main.MainFragment
import com.kpstv.navigation.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentNavigator.Transmitter{

    private lateinit var navigator : FragmentNavigator
//    @Inject
//    lateinit var repository : ComicRepository

    override fun getNavigator(): FragmentNavigator = navigator

    private lateinit var binding : ActivityMainBinding
//    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navigator = FragmentNavigator.with(this,savedInstanceState)
            .initialize(binding.fragmentContainer, Destination.Companion.of(MainFragment::class))


//          CoroutineScope(Dispatchers.IO).launch {
//            repository.getFeaturedComics(false).collect{
//                it
//            }
//        }
    }

    override fun onBackPressed() {
        if (navigator.canFinish()){
            super.onBackPressed()
        }
    }

}