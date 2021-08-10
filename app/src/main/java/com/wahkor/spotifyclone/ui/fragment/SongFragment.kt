package com.wahkor.spotifyclone.ui.fragment

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.wahkor.spotifyclone.R
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.isPlaying
import com.wahkor.spotifyclone.exo.toSong
import com.wahkor.spotifyclone.ui.MainActivity
import com.wahkor.spotifyclone.ui.viewmodels.MainViewModel
import com.wahkor.spotifyclone.ui.viewmodels.SongViewModel
import com.wahkor.spotifyclone.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment:Fragment(R.layout.fragment_song) {
    @Inject lateinit var glide:RequestManager
    private val songViewModel:SongViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel
    private var curPlayingSong: Song?=null
    private var playbackState:PlaybackStateCompat?=null
    private var shouldUpdateSeekbar=true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()

        ivPlayPauseDetail.setOnClickListener { curPlayingSong?.let { song -> mainViewModel.playOrToggleSong(song,true) } }
        ivSkipPrevious.setOnClickListener { mainViewModel.skipToPreviousSong() }
        ivSkip.setOnClickListener { mainViewModel.skipToNextSong() }
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUsers: Boolean) {
                if (fromUsers){
                    tvCurTime.text=millSecToString(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekbar=false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                }
                shouldUpdateSeekbar=true
            }
        })

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
        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState=it
            ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying==true) R.drawable.ic_pause else R.drawable.ic_play)
            seekBar.progress=it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
            if (shouldUpdateSeekbar){
                seekBar.progress=it.toInt()
                tvCurTime.text=millSecToString(it)
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            seekBar.max=it.toInt()
            tvSongDuration.text=millSecToString(it)
        }

    }


    private fun millSecToString(millSecs: Long): String {

        val shortFormat=SimpleDateFormat("mm:ss", Locale.getDefault())
        val longFormat=SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return if ((millSecs/(1000*60*60)) == 0L) shortFormat.format(millSecs) else longFormat.format(millSecs)
        /*
        var secs = millSecs / 1000
        var minute = secs / 60
        val hours = minute / 60
        minute -= hours * 60
        secs = secs - minute * 60 - hours * 60 * 60
        var text = if (hours == 0L) "" else "$hours:"
        text += if (minute < 10) "0$minute:" else "$minute:"
        text += if (secs < 10) "0$secs" else "$secs"
        return text*/
    }






































}