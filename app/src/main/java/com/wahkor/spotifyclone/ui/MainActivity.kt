package com.wahkor.spotifyclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.adapter.SwipeSongAdapter
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.isPlaying
import com.wahkor.spotifyclone.exo.toSong
import com.wahkor.spotifyclone.ui.viewmodels.MainViewModel
import com.wahkor.spotifyclone.utils.AlbumArt
import com.wahkor.spotifyclone.utils.Constants.MEDIA_ROOT_ID
import com.wahkor.spotifyclone.utils.Query
import com.wahkor.spotifyclone.utils.Query.Companion.storageMedia
import com.wahkor.spotifyclone.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    @Inject lateinit var swipeSongAdapter: SwipeSongAdapter
    @Inject lateinit var glide:RequestManager

    private var curPlayingSong: Song?=null
    private var playbackState:PlaybackStateCompat?=null

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if(granted && Query.requestInitialing){
            Query().getTracks(this)
                mainActivityScope.launch {
                    storageMedia.forEachIndexed{index,song ->
                        val pic=AlbumArt().getImage(song)
                        storageMedia[index].albumArt=pic
                    }
                }

            }
        }

    private val mainActivityJob= Job()
    private val mainActivityScope= CoroutineScope(Dispatchers.IO+mainActivityJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if ( ! Query.requestInitialing){
            mainActivityScope.launch {
                storageMedia.forEachIndexed{index, song ->
                    if (song.albumArt==null){
                        storageMedia[index].albumArt=AlbumArt().getImage(song)
                    }
                }
            }
        }
        subscribeToObservers()
        vpSong.adapter=swipeSongAdapter
        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying==true){
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                }else{
                    curPlayingSong=swipeSongAdapter.songs[position]
                }
            }
        })
        ivPlayPause.setOnClickListener {
            curPlayingSong?.let { song ->
                mainViewModel.playOrToggleSong(song,true)
            }
        }
        
        swipeSongAdapter.setItemClickListener { 
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.songFragment -> showBottomBar(false)
                R.id.homeFragment -> showBottomBar(true)
                else -> showBottomBar(true)
            }
        }
        btn_all.setOnClickListener {
            mainViewModel.setSubscribe(MEDIA_ROOT_ID)
        }
        btn_ariana.setOnClickListener {
            mainViewModel.setSubscribe("custom id")
        }
    }


    private fun showBottomBar(visibility:Boolean){
        ivCurSongImage.isVisible=visibility
        ivPlayPause.isVisible=visibility
        vpSong.isVisible=visibility
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
                                loadImageToTabImage()
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
                loadImageToTabImage()
                switchViewPagerToCurrentSong(curPlayingSong?:return@observe)
            }
        }
        mainViewModel.playbackState.observe(this){
            playbackState=it
            ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.exo_icon_pause
            else R.drawable.exo_icon_play
            )

        }
        mainViewModel.isConnected.observe(this){
            it?.getContentIfNotHandled()?.let { result ->
                run {
                    when (result.status) {
                        Status.Error -> {
                            Snackbar.make(
                                rootLayout,
                                result.message ?: "An unknown error occurred",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
        mainViewModel.networkError.observe(this){
            it?.getContentIfNotHandled()?.let { result ->
                run {
                    when (result.status) {
                        Status.Error -> {
                            Snackbar.make(
                                rootLayout,
                                result.message ?: "An unknown error occurred",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    private fun loadImageToTabImage() {
        val freshSong= storageMedia.find {item ->item.mediaId==curPlayingSong?.mediaId }
        (freshSong?.albumArt?.let { img ->
            glide.load(img)
        }?:run{
            glide.load(R.drawable.ic_music)
        }).into(ivCurSongImage)
    }
}