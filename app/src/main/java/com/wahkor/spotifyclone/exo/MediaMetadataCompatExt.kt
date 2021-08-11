package com.wahkor.spotifyclone.exo

import android.support.v4.media.MediaMetadataCompat
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.utils.Query.Companion.storageMedia

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        storageMedia.find { song -> song.mediaId==it.mediaId }
    }
}