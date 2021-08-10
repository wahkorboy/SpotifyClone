package com.wahkor.spotifyclone.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.toSong
import com.wahkor.spotifyclone.ui.MainActivity
import com.wahkor.spotifyclone.ui.viewmodels.MainViewModel
import com.wahkor.spotifyclone.ui.viewmodels.SongViewModel
import com.wahkor.spotifyclone.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment:Fragment(R.layout.fragment_song) {
    @Inject lateinit var glide:RequestManager
    private val songViewModel:SongViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel
    private var curPlayingSong: Song?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()

    }

    private fun updateTitleAndSongImage(song: Song){
        (song.title + " - " + song.subtitle).also { tvSongName.text = it }
        glide.load(song.imageUrl).into(ivSongImage)

    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){it?.let { resource ->
            when(resource.status){
                Status.Success ->{
                    resource.data?.let { songs ->
                        if (curPlayingSong == null && songs.isNotEmpty()){
                            curPlayingSong=songs[0]
                            updateTitleAndSongImage(curPlayingSong!!)
                        }
                    }
                }

                else -> Unit
            }
        }}
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it==null) return@observe
            curPlayingSong=it.toSong()
            updateTitleAndSongImage(it.toSong()!!)
        }
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
            seekBar.progress=it.toInt()
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            seekBar.max=it.toInt()
        }
    }








































}