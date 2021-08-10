package com.wahkor.spotifyclone.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.MusicService
import com.wahkor.spotifyclone.exo.MusicServiceConnection
import com.wahkor.spotifyclone.exo.currentPlaybackPosition
import com.wahkor.spotifyclone.utils.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel(){

    private val playbackState=musicServiceConnection.playbackState

    private val _curSongDuration=MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition=MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }
    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch{
            while (true){
                val pos= playbackState.value?.currentPlaybackPosition
                pos?.let {time ->
                    if (pos != _curPlayerPosition.value){
                        _curPlayerPosition.postValue(time)
                        _curSongDuration.postValue(MusicService.curSongDuration)
                    }
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}