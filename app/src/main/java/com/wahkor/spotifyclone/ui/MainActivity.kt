package com.wahkor.spotifyclone.ui

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.adapter.SwipeSongAdapter
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.toSong
import com.wahkor.spotifyclone.ui.viewmodels.MainViewModel
import com.wahkor.spotifyclone.utils.Query
import com.wahkor.spotifyclone.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    @Inject lateinit var swipeSongAdapter: SwipeSongAdapter
    @Inject lateinit var glide:RequestManager

    private var curPlayingSong: Song?=null
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        subscribeToObservers()
        vpSong.adapter=swipeSongAdapter

    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val itemIndex=swipeSongAdapter.songs.indexOf(song)
        if (itemIndex != -1){
            curPlayingSong=song
            vpSong.currentItem=itemIndex
        }
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(this){
            it?.let { result->
                when(result.status){
                    Status.Success->{
                        result.data?.let { songs ->
                            swipeSongAdapter.songs=songs
                            if(songs.isNotEmpty()){
                                glide.load((curPlayingSong?:songs[0]).imageUrl)
                                    .into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong?:return@observe)
                        }
                    }
                    Status.Error->Unit
                    Status.Loading->Unit
                }

            }
        }
        mainViewModel.curPlayingSong.observe(this){
            it?.let {
                curPlayingSong=it.toSong()
                glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
                switchViewPagerToCurrentSong(curPlayingSong?:return@observe)
            }
        }

    }
}