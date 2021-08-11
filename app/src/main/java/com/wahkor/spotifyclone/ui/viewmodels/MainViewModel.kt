package com.wahkor.spotifyclone.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.exo.*
import com.wahkor.spotifyclone.utils.Constants.MEDIA_ROOT_ID
import com.wahkor.spotifyclone.utils.Query.Companion.storageMedia
import com.wahkor.spotifyclone.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
):ViewModel(){
    private val _mediaItems=MutableLiveData<Resource<List<Song>>>()
    val mediaItems:LiveData<Resource<List<Song>>> = _mediaItems
    val isConnected=musicServiceConnection.isConnected
    val networkError=musicServiceConnection.networkError
    val curPlayingSong=musicServiceConnection.curPlayingSong
    val playbackState=musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))
       setSubscribe(MEDIA_ROOT_ID)
    }

    fun skipToNextSong()=musicServiceConnection.transportControls.skipToNext()
    fun skipToPreviousSong()=musicServiceConnection.transportControls.skipToPrevious()
    fun seekTo(position:Long)= musicServiceConnection.transportControls.seekTo(position)
    fun playOrToggleSong(mediaItem:Song,toggle:Boolean=false) {

        val isPrepared=playbackState.value?.isPrepared?:false
        if (isPrepared && mediaItem.mediaId==curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
            playbackState.value?.let { playbackStateCompat ->
                when{
                    playbackStateCompat.isPlaying && toggle ->musicServiceConnection.transportControls.pause()
                    playbackStateCompat.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        }else{
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId,null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubscript(MEDIA_ROOT_ID,object :MediaBrowserCompat.SubscriptionCallback(){ })
    }

    fun setSubscribe(id:String){
        musicServiceConnection.subscript(id,object:MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items=children.map{
                    Song(it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })
    }
}