package com.wahkor.spotifyclone.exo

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.data.remote.MusicDatabase
import com.wahkor.spotifyclone.utils.Constants.MEDIA_ROOT_ID
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()
    var selectedSong=emptyList<MediaMetadataCompat>()
    private val onReadyListeners= mutableListOf< (Boolean) -> Unit >()
    private var state=State.STATE_CREATED
    set(value){
        when(value){
            State.STATE_INITIALIZED,State.STATE_ERROR ->{
                synchronized(onReadyListeners){
                    field=value
                    onReadyListeners.forEach {listener ->
                        listener(state==State.STATE_INITIALIZED)
                    }
                }
            }
            else ->{
                field=value
            }
        }
    }

    suspend fun fetchMediaData(filter:List<String>){
        state=State.STATE_INITIALIZING
        val allSongs=musicDatabase.getAllSongs()
        songs=allSongs.map { song: Song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_TITLE,song.title)
                .putString(METADATA_KEY_ARTIST,song.title)
                .putString(METADATA_KEY_ARTIST,song.subtitle)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE,song.subtitle)
                .putString(METADATA_KEY_MEDIA_ID,song.mediaId)
                .putString(METADATA_KEY_MEDIA_URI,song.songUrl)
               // .putString(METADATA_KEY_DISPLAY_ICON_URI,song.imageUrl)
               // .putString(METADATA_KEY_ALBUM_ART_URI,song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION,song.subtitle)
                .build()
        }.filter {filter.contains(it.description.mediaId ) }
        selectedSong=songs
        state=State.STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory):ConcatenatingMediaSource{
        val concatenatingMediaSource=ConcatenatingMediaSource()
        selectedSong.forEach { song ->
            val mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return selectedSong.map { song ->
            val disc = MediaDescriptionCompat.Builder()
                .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
                .setTitle(song.description.title)
                .setSubtitle(song.description.subtitle)
                .setMediaId(song.description.mediaId)
                .setIconUri(song.description.iconUri)
                .build()

            MediaBrowserCompat.MediaItem(disc, FLAG_PLAYABLE)

        }.toMutableList()
    }

    fun whenReady(action : (Boolean) -> Unit):Boolean{
        return when(state){
            State.STATE_CREATED,State.STATE_INITIALIZING ->{
                onReadyListeners += action
                false
            }
            else ->{
                action(state==State.STATE_INITIALIZED)
                true
            }
        }
    }
fun createSelected(id:String){
    selectedSong=if (id==MEDIA_ROOT_ID) songs else songs.filter { it.description.subtitle.toString().lowercase()
        .contains("ariana")}
}
}

enum class State{
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR,
}